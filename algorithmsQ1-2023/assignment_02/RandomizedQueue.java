import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class RandomizedQueue<T> implements Iterable<T> {
    public static final int BASE_CAPACITY = 25;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private final Random selector;
    private int actualSize;
    private Object[] objects;
    private long opId = Long.MIN_VALUE;

    // construct an empty randomized queue
    public RandomizedQueue() {
        this.actualSize = 0;
        this.objects = new Object[BASE_CAPACITY];
        this.selector = new Random();
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return this.actualSize == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return this.actualSize;
    }

    // add the item
    public void enqueue(T item) {
        if (item == null) {
            throw new IllegalArgumentException("item must not be null");
        }
        reviewGrow();
        this.objects[this.actualSize] = item;
        ++this.actualSize;
        ++this.opId;
    }

    private void reviewGrow() {
        if (this.actualSize == this.objects.length) {
            this.objects = RandomizedQueue.grow(this.objects);
        }
    }

    private static Object[] grow(Object[] source) {
        int current = source.length;
        int newLen = min(source.length << 1);
        if (newLen == current) {
            throw new OutOfMemoryError("Max memory reached");
        }
        Object[] destination = new Object[newLen];
        for (int i = 0; i < source.length; ++i) {
            destination[i] = source[i];
        }
        return destination;
    }

    private static int min(int newSize) {
        if (newSize < MAX_ARRAY_SIZE) {
            return newSize;
        }
        return MAX_ARRAY_SIZE;
    }

    private static Object[] shrink(Object[] source, int actualSize, int newSize) {
        Object[] destination = new Object[newSize];
        for (int i = 0; i < actualSize; ++i) {
            destination[i] = source[i];
        }
        return destination;
    }

    // remove and return a random item
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        int pos = selector.nextInt(this.actualSize);
        T removed = this.remove(pos);
        --this.actualSize;
        ++this.opId;
        reviewShrink();
        return removed;
    }

    private void reviewShrink() {
        double fortyPercent = this.objects.length * 0.4d;
        if (this.actualSize <= fortyPercent && this.objects.length > BASE_CAPACITY) {
            this.objects = shrink(this.objects, this.actualSize, max(this.objects.length * 75 / 100));
        }
    }

    private int max(int newSize) {
        if (newSize > BASE_CAPACITY) {
            return newSize;
        }
        return BASE_CAPACITY;
    }

    private T remove(int pos) {
        T obj = (T) objects[pos];
        for (int i = pos; i < this.actualSize - 1; ++i) {
            objects[i] = objects[i + 1];
        }
        return obj;
    }

    // return a random item (but do not remove it)
    public T sample() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return (T) this.objects[selector.nextInt(this.actualSize)];
    }

    // return an independent iterator over items in random order
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private final RandomizedQueue<T> parent = RandomizedQueue.this;
            private final long acceptedId = parent.opId;
            private final Object[] indexes = buildIndexesRandom(new Random(), parent.actualSize, parent.objects);
            private int index = 0;

            Object[] buildIndexesRandom(Random random, int actualSize, Object[] objects) {
                Object[] permutation = new Object[actualSize];
                boolean[] seen = new boolean[actualSize];
                int idx = -1;
                for (int i = 0; i < actualSize; ++i) {
                    idx = random.nextInt(actualSize);
                    while (seen[idx]) {
                        idx = random.nextInt(actualSize);
                    }
                    seen[idx] = true;
                    permutation[i] = objects[idx];
                }
                return permutation;
            }

            @Override
            public boolean hasNext() {
                return index < indexes.length;
            }

            @Override
            public T next() {
                if (acceptedId != parent.opId) {
                    throw new RuntimeException("Object list modified");
                }
                if (!hasNext()) {
                    throw new NoSuchElementException("Element does not exist");
                }
                T object = (T) indexes[index];
                ++this.index;
                return object;
            }
        };
    }

    // unit testing (required) {
    public static void main(String[] args) {
        RandomizedQueue<Integer> q = new RandomizedQueue<>();
        q.enqueue(1);
        StdOut.println("Size{1}: " + q.size());

        q.enqueue(2);
        StdOut.println("Size{2}: " + q.size());
        q.enqueue(3);
        StdOut.println("Size{3}: " + q.size());
        q.enqueue(4);
        StdOut.println("Size{4}: " + q.size());

        StdOut.println("Sample: " + q.sample());
        StdOut.println("Sample: " + q.sample());
        StdOut.println("Sample: " + q.sample());

        StdOut.println("Remove: " + q.dequeue());
        StdOut.println("Size{3}: " + q.size());
        StdOut.println("Remove: " + q.dequeue());
        StdOut.println("Size{2}: " + q.size());
        StdOut.println("Remove: " + q.dequeue());
        StdOut.println("Size{1}: " + q.size());
        StdOut.println("Remove: " + q.dequeue());
        StdOut.println("Size{0}: " + q.size());

        try {
            q.dequeue();
        } catch (NoSuchElementException nse) {
            StdOut.println("NSE " + nse.getMessage());
        }
        try {
            q.sample();
        } catch (NoSuchElementException nse) {
            StdOut.println("NSE " + nse.getMessage());
        }

        q.enqueue(5);
        q.enqueue(6);
        q.enqueue(7);

        Iterator<Integer> iterator1 = q.iterator();
        Iterator<Integer> iterator2 = q.iterator();

        while (iterator1.hasNext() && iterator2.hasNext()) {
            StdOut.println(iterator1.next() + "!=" + iterator2.next());
        }

        try {
            iterator1.next();
        } catch (NoSuchElementException nse) {
            StdOut.println("NSE " + nse.getMessage());
        }

        q.enqueue(8);
        q.enqueue(9);
        Iterator<Integer> iterator3 = q.iterator();
        q.enqueue(10);

        try {
            iterator3.next();
        } catch (RuntimeException rte) {
            StdOut.println("Concurrent modification exception: " + rte.getMessage());
        }

        try {
            q.enqueue(null);
        } catch (IllegalArgumentException iae) {
            StdOut.println("null not accepted: " + iae.getMessage());
        }

        RandomizedQueue q2 = new RandomizedQueue();
        StdOut.println("CAP: " + q2.objects.length);
        int valCap = q2.objects.length;
        for (int i = 0; i < 100; ++i) {
            q2.enqueue(i);
            if (valCap != q2.objects.length) {
                StdOut.println(i + " CAP: " + q2.objects.length);
                valCap = q2.objects.length;
            }
        }

        for (int i = 0; i < 65; ++i) {
            q2.dequeue();
            if (valCap != q2.objects.length) {
                StdOut.println(i + " CAP: " + q2.objects.length);
                valCap = q2.objects.length;
            }

        }
    }
}
