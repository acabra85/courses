import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<T> implements Iterable<T> {
    private int actualSize;

    // construct an empty randomized queue
    public RandomizedQueue() {
        this.actualSize = 0;
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
        if(item == null) {
            throw new IllegalArgumentException("item must not be null");
        }
        ++this.actualSize;
    }

    // remove and return a random item
    public T dequeue() {
        if(isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        --this.actualSize;
        return null;
    }

    // return a random item (but do not remove it)
    public T sample() {
        return null;
    }

    // return an independent iterator over items in random order
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                return null;
            }
        };
    }

    // unit testing (required) {
    public static void main(String[] args) {

    }
}
