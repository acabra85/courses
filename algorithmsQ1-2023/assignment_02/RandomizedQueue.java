import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

  private static final int BASE_CAPACITY = 25;
  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
  private static final int SHRINK_THRESHOLD = 48;
  private static final int SHRINK_FACTOR = 75;
  private static final int PERCENTAGE = 100;
  private int iActualSize;
  private Item[] iObjects;
  private long opId = Long.MIN_VALUE;
  private int capacity;

  // construct an empty randomized queue
  public RandomizedQueue() {
    this.iActualSize = 0;
    this.capacity = BASE_CAPACITY;
    this.iObjects = buildArrayWithCapacity(BASE_CAPACITY);
  }

  private Item[] buildArrayWithCapacity(int baseCapacity) {
    Item[] arr = (Item[]) new Object[baseCapacity];
    for (int i = 0; i < baseCapacity; i++) {
      arr[i] = null;
    }
    return arr;
  }

  // is the randomized queue empty?
  public boolean isEmpty() {
    return this.iActualSize == 0;
  }

  // return the number of items on the randomized queue
  public int size() {
    return this.iActualSize;
  }

  // add the item
  public void enqueue(Item item) {
    if (item == null) {
      throw new IllegalArgumentException("item must not be null");
    }
    reviewGrow();
    this.iObjects[this.iActualSize] = item;
    ++this.iActualSize;
    ++this.opId;
  }

  private void reviewGrow() {
    if (this.iActualSize == this.getCapacity()) {
      this.iObjects = grow();
    }
  }

  private Item[] grow() {
    int current = this.getCapacity();
    int newLen = min(current << 1);
    if (newLen == current || newLen < 0) {
      throw new OutOfMemoryError("Max memory reached");
    }
    Item[] destination = buildArrayWithCapacity(newLen);
    for (int i = 0; i < this.iActualSize; ++i) {
      destination[i] = this.iObjects[i];
    }
    this.capacity = newLen;
    return destination;
  }

  private static int min(int newSize) {
    if (newSize < MAX_ARRAY_SIZE) {
      return newSize;
    }
    return MAX_ARRAY_SIZE;
  }

  private Item[] shrink(int newSize) {
    Item[] destination = buildArrayWithCapacity(newSize);
    for (int i = 0; i < this.iActualSize; ++i) {
      destination[i] = this.iObjects[i];
    }
    this.capacity = newSize;
    return destination;
  }

  // remove and return a random item
  public Item dequeue() {
    if (isEmpty()) {
      throw new NoSuchElementException("queue is empty");
    }
    int pos = StdRandom.uniformInt(this.iActualSize);
    Item removed = this.remove(pos);
    --this.iActualSize;
    ++this.opId;
    reviewShrink();
    return removed;
  }

  private void reviewShrink() {
    int fortyEightPercent = this.getCapacity() * SHRINK_THRESHOLD / PERCENTAGE;
    if (this.iActualSize <= fortyEightPercent && this.getCapacity() > BASE_CAPACITY) {
      this.iObjects = shrink(max(this.getCapacity() * SHRINK_FACTOR / PERCENTAGE));
    }
  }

  private int max(int newSize) {
    if (newSize > BASE_CAPACITY) {
      return newSize;
    }
    return BASE_CAPACITY;
  }

  private Item remove(int pos) {
    Item obj = iObjects[pos];
    for (int i = pos; i < this.iActualSize - 1; ++i) {
      iObjects[i] = iObjects[i + 1];
    }
    iObjects[this.iActualSize-1] = null;
    return obj;
  }

  // return a random item (but do not remove it)
  public Item sample() {
    if (isEmpty()) {
      throw new NoSuchElementException("Queue is empty");
    }
    return this.iObjects[StdRandom.uniformInt(this.iActualSize)];
  }

  // return an independent iterator over items in random order
  public Iterator<Item> iterator() {
    return new Iterator<Item>() {
      private final long acceptedId = RandomizedQueue.this.opId;
      private final Item[] indexes = RandomizedQueue.this.buildIndexesRandom();
      private int index = 0;

      @Override
      public boolean hasNext() {
        return index < indexes.length;
      }

      @Override
      public Item next() {
        if (acceptedId != RandomizedQueue.this.opId) {
          throw new IllegalStateException("Object list modified");
        } else if (!hasNext()) {
          throw new NoSuchElementException("Element does not exist");
        }
        return indexes[index++];
      }
    };
  }

  private Item[] buildIndexesRandom() {
    int actualSize = this.iActualSize;
    Item[] permutation = buildArrayWithCapacity(actualSize);
    boolean[] seen = new boolean[actualSize];
    int idx = -1;
    for (int i = 0; i < actualSize; ++i) {
      idx = StdRandom.uniformInt(actualSize);
      while (seen[idx]) {
        idx = StdRandom.uniformInt(actualSize);
      }
      seen[idx] = true;
      permutation[i] = this.iObjects[idx];
    }
    return permutation;
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
    } catch (IllegalStateException ise) {
      StdOut.println("Concurrent modification exception: " + ise.getMessage());
    }

    try {
      q.enqueue(null);
    } catch (IllegalArgumentException iae) {
      StdOut.println("null not accepted: " + iae.getMessage());
    }

    RandomizedQueue<Integer> q2 = new RandomizedQueue<>();
    StdOut.println("CAP: " + q2.getCapacity());
    int valCap = q2.getCapacity();
    for (int i = 0; i < PERCENTAGE; ++i) {
      q2.enqueue(i);
      if (valCap != q2.getCapacity()) {
        StdOut.println("it: " + i + " size: " + q2.size() + " CAP: " + q2.getCapacity());
        valCap = q2.getCapacity();
      }
    }

    for (int i = 0; i < 65; ++i) {
      q2.dequeue();
      if (valCap != q2.getCapacity()) {
        StdOut.println("it: " + i + " size: " + q2.size() + " CAP: " + q2.getCapacity());
        valCap = q2.getCapacity();
      }
    }

    RandomizedQueue<Integer> test = new RandomizedQueue<>();
    for (int i = 0; i < 100; i++) {
      test.enqueue(i);
    }
    test.dequeue();
  }

  private int getCapacity() {
    return this.capacity;
  }
}
