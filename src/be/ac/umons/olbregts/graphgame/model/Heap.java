/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ac.umons.olbregts.graphgame.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Simon
 */
public class Heap<E extends HeapElement<E>> implements Iterable<E> {

    private ArrayList<E> heap;

    public Heap() {
        heap = new ArrayList<>();
    }

    public void initialize(E[] sorted) {
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i].compareTo(sorted[i - 1]) < 0) {
                throw new IllegalArgumentException("The array must be sorted");
            }
        }
        heap.clear();
        heap.ensureCapacity(sorted.length);
        for (int i = 0; i < sorted.length; i++) {
            heap.add(sorted[i]);
            sorted[i].setHeapIndex(i);
        }
    }

    public void insert(E element) {
        int i = heap.size();
        heap.add(element);
        element.setHeapIndex(i);
        heapUp(i);
    }

    public E extractMin() {
        E min = heap.get(0);
        min.setHeapIndex(-1);
        heap.set(0, heap.get(heap.size() - 1));
        heap.get(0).setHeapIndex(0);
        heap.remove(heap.size() - 1);
        heapify(0);
        return min;
    }

    public void decreaseKey(int i, E modified) {
        if (heap.get(i).compareTo(modified) < 0) {
            throw new IllegalArgumentException("The new object must be lower than older");
        }
        heap.set(i, modified);
        modified.setHeapIndex(i);
        while (i > 0 && heap.get(father(i)).compareTo(heap.get(i)) > 0) {
            exchange(i, father(i));
            i = father(i);
        }
    }

    public void increaseKey(int i, E modified) {
        if (heap.get(i).compareTo(modified) > 0) {
            throw new IllegalArgumentException("The new object must be greather than older");
        }
        heap.set(i, modified);
        modified.setHeapIndex(i);
        heapify(i);
    }

    public void heapUp(int i) {
        if (i > 0 && i < heap.size()) {
            if (heap.get(father(i)).compareTo(heap.get(i)) >= 0) {
                exchange(i, father(i));
                heapUp(father(i));
            }
        }
    }

    public void heapify(int i) {
        int smallest = i;
        int left = left(i);
        int right = right(i);
        if (left < heap.size() && heap.get(left).compareTo(heap.get(i)) < 0) {
            smallest = left;
        }
        if (right < heap.size() && heap.get(right).compareTo(heap.get(smallest)) < 0) {
            smallest = right;
        }
        if (i != smallest) {
            exchange(i, smallest);
            heapify(smallest);
        }
    }

    private void exchange(int i, int j) {
        E temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.get(i).setHeapIndex(i);
        heap.set(j, temp);
        temp.setHeapIndex(j);
    }

    private int left(int i) {
        return 2 * i;
    }

    private int right(int i) {
        return 2 * i + 1;
    }

    private int father(int i) {
        return i / 2;
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public E peek() {
        if (heap.isEmpty()) {
            return null;
        }
        return heap.get(0);
    }

    @Override
    public Iterator<E> iterator() {
        return heap.iterator();
    }

    public boolean contains(E element) {
        return heap.contains(element);
    }

    public void clear() {
        heap.clear();
    }

    @Override
    public String toString() {
        String str = " empty ";
        if (!heap.isEmpty()) {
            str = "" + heap.get(0) + "\n";
            int nextLn = 2;
            int increment = 4;
            for (int i = 1; i < heap.size(); i++) {
                str += "" + heap.get(i) + " ";
                if (i == nextLn) {
                    str += "\n";
                    nextLn += increment;
                    increment *= 2;
                }
            }
        }
        return "Heap{" + str + "}";
    }
}
