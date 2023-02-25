import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private static class DequeItem<Item> {
        DequeItem<Item> next;
        DequeItem<Item> prev;
        Item item;

        DequeItem(Item item) {
            this.item = item;
        }

        public static <Item> DequeItem<Item> startIterator(DequeItem<Item> first) {
            DequeItem<Item> start = new DequeItem<>(null);
            start.next = first;
            return start;
        }

        void setNext(DequeItem<Item> next) {
            this.next = next;
        }

        void setPrev(DequeItem<Item> prev) {
            this.prev = prev;
        }

        @Override
        public String toString() {
            String iPrev = this.prev == null ? "*" : this.prev.item.toString();
            String iNext = this.next == null ? "*" : this.next.item.toString();
            return "It:" + this.item + ", next:" + iNext + " prev:" + iPrev;
        }
    }

    private int actualSize;
    private DequeItem<Item> first = null;
    private DequeItem<Item> last = null;
    private long opId = Long.MIN_VALUE;

    // construct an empty deque
    public Deque() {
        this.actualSize = 0;
    }

    private void checkValidItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Element must not be null");
        }
    }

    private void checkNotEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque is empty");
        }
    }

    // is the deque empty?
    public boolean isEmpty() {
        return this.actualSize == 0;
    }

    // return the number of items on the deque
    public int size() {
        return actualSize;
    }

    // add the item to the front
    public void addFirst(Item item) {
        checkValidItem(item);
        DequeItem<Item> newElm = new DequeItem<>(item);
        if (isEmpty()) {
            this.first = newElm;
            this.last = newElm;
        } else {
            DequeItem<Item> tmp = this.first;
            this.first = newElm;
            this.first.setNext(tmp);
            tmp.setPrev(this.first);
        }
        ++this.opId;
        ++this.actualSize;
    }

    // add the item to the last
    public void addLast(Item item) {
        checkValidItem(item);
        DequeItem<Item> newElm = new DequeItem<>(item);
        if (isEmpty()) {
            this.first = newElm;
            this.last = newElm;
        } else {
            DequeItem<Item> tmp = this.last;
            this.last = newElm;
            this.last.setPrev(tmp);
            tmp.setNext(this.last);
        }
        ++this.opId;
        ++this.actualSize;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        checkNotEmpty();
        DequeItem<Item> removed = this.first;
        this.first = removed.next;
        if (this.first != null) {
            this.first.setPrev(null);
        }
        removed.setNext(null);
        ++this.opId;
        --this.actualSize;
        return removed.item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        checkNotEmpty();
        DequeItem<Item> removed = this.last;
        this.last = removed.prev;
        if (this.last != null) {
            this.last.setNext(null);
        }
        removed.setPrev(null);
        --this.actualSize;
        ++this.opId;
        return removed.item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
            private DequeItem<Item> curr = DequeItem.startIterator(Deque.this.first);
            private final long refOpId = Deque.this.opId;

            @Override
            public boolean hasNext() {
                return curr.next != null;
            }

            @Override
            public Item next() {
                if (this.refOpId != Deque.this.opId) {
                    throw new IllegalStateException("Object list modified");
                } else if (!hasNext()) {
                    throw new NoSuchElementException("No More Elements");
                }
                DequeItem<Item> ref = curr.next;
                this.curr = ref;
                return ref.item;
            }
        };
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> q = new Deque<>();
        StdOut.printf("Queue is empty: True[%s]%n", q.isEmpty());
        q.addFirst(1);
        StdOut.printf("Queue is empty: False[%s]%n", q.isEmpty());
        StdOut.printf("size: 1->[%d]%n", q.size());
        q.addFirst(2);
        StdOut.printf("size: 2->[%d]%n", q.size());
        q.addLast(3);
        StdOut.printf("size: 3->[%d]%n", q.size());

        Iterator<Integer> iterator = q.iterator();
        StdOut.printf("Read: 2[%d]%n", iterator.next());
        StdOut.printf("Read: 1[%d]%n", iterator.next());
        StdOut.printf("Read: 3[%d]%n", iterator.next());
        StdOut.printf("Iterator Next?: False[%s]%n", iterator.hasNext());

        try {
            iterator.next();
        } catch (NoSuchElementException nse) {
            StdOut.println("no more elements [" + nse.getMessage());
        }

        StdOut.printf("removed 2[%d]%n", q.removeFirst());
        StdOut.printf("size: 2->[%d]%n", q.size());
        StdOut.printf("removed 3[%d]%n", q.removeLast());
        StdOut.printf("size: 1->[%d]%n", q.size());
        StdOut.printf("removed 1[%d]%n", q.removeLast());
        StdOut.printf("size: 0->[%d]%n", q.size());

        try {
            q.addLast(null);
        } catch (IllegalArgumentException iae) {
            StdOut.println("must not be null [" + iae.getMessage());
        }

        try {
            q.addFirst(null);
        } catch (IllegalArgumentException iae) {
            StdOut.println("must not be null [" + iae.getMessage());
        }

        try {
            q.removeFirst();
        } catch (NoSuchElementException nse) {
            StdOut.println("Queue is empty [" + nse.getMessage());
        }

        try {
            q.removeLast();
        } catch (NoSuchElementException nse) {
            StdOut.println("Queue is Empty [" + nse.getMessage());
        }

        q = new Deque<>();
        q.addFirst(1);
        Iterator<Integer> iterator1 = q.iterator();
        q.removeFirst();
       // StdOut.println(iterator1.next());

        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addFirst(1);
        Iterator<Integer> iterator2 = a.iterator();
        a.removeFirst();
        iterator2.next();

    }
}