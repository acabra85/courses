import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import edu.princeton.cs.algs4.StdRandom;

public class Permutation {

  public static void main(String[] args) {
    int k = Integer.parseInt(args[0]);
    int readCount = 0;
    RandomizedQueue<String> q = new RandomizedQueue<>();
    while (!StdIn.isEmpty()) {
      String item = StdIn.readString();
      ++readCount;
      if (readCount <= k) {
        q.enqueue(item);
      } else if (StdRandom.uniformInt(readCount) < (double) k / readCount) {
        q.dequeue();
        q.enqueue(item);
      }
    }
    for (String it : q) {
      StdOut.println(it);
    }
  }
}