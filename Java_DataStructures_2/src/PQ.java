package project_top_k;

import java.util.Comparator;

public class PQ<T> {
	/**
	 * Array based heap representation
	 */
	private T[] heap;
	/**
	 * The number of objects in the heap
	 */
	private int size;
	/**
	 * Comparator.
	 */
	protected Comparator<T> cmp;

	/**
	 * Creates heap with a given capacity and comparator. param capacity The
	 * capacity of the heap being created. param cmp The comparator that will be
	 * used.
	 */
	public PQ(int capacity, Comparator<T> cmp) {
		if (capacity < 1)
			throw new IllegalArgumentException();

		this.heap = (T[]) new Object[capacity + 1];
		this.size = 0;
		this.cmp = cmp;
	}

	/**
	 * Inserts an object in this heap. throws IllegalStateException if heap
	 * capacity is exceeded. param object The object to insert.
	 */
	public void insert(T object) {
		// Ensure object is not null
		if (object == null)
			throw new IllegalArgumentException();
		// Check available space
		if (size == heap.length - 1)
			resize();
		// Place object at the next available position
		heap[++size] = object;
		// Let the newly added object swim
		swim(size);
	}

	/**
	 * Removes the object at the root of this heap. throws IllegalStateException
	 * if heap is empty. return The object removed.
	 */
	public T getMax() {
		// Ensure not empty
		if (size == 0)
			throw new IllegalStateException();
		// Keep a reference to the root object
		T object = heap[1];
		// Replace root object with the one at rightmost leaf
		if (size > 1)
			heap[1] = heap[size];
		// Dispose the rightmost leaf
		heap[size--] = null;
		// Sink the new root element
		sink(1);
		// Return the object removed
		return object;
	}

	public T max() {

		if (size == 0)
			throw new IllegalStateException();

		return heap[1];
	}

	public T remove(int id) {

		if (size == 0)
			throw new IllegalStateException();

		Song song = new Song(0, " ", 0);
		boolean find = false;

		for (int i = 1; i < size; i++) {

			song = (Song) heap[i];
			if (song.getId() == id) {

				find = true;
				swap(1, size);
				swap(i, size);
				heap[size--] = null;
				sink(1);
				swim(i);
				break;
			}
		}

		if (!find) {
			song = null;
			System.out.println("Song not found");
		}

		return (T) song;
	}

	public T getMin() {

		if (size == 0)
			throw new IllegalStateException();

		T min = heap[1];
		boolean find = false;

		for (int i = 1; i < size; i++) {

			min = heap[i];
			if (cmp.compare((T) min, heap[i]) > 0) {

				min = heap[i];
			}
		}

		Song song = (Song) min;
		return remove(song.getId());
	}

	public void print() {

		for (int i = 1; i <= size; i++)
			System.out.println(heap[i]);
	}

	/**
	 * Shift up.
	 */
	private void swim(int i) {
		while (i > 1) { // if i root (i==1) return
			int p = i / 2; // find parent
			int result = cmp.compare(heap[i], heap[p]); // compare parent with
														// child
			if (result <= 0)
				return; // if child <= parent return
			swap(i, p); // else swap and i=p
			i = p;
		}
	}

	/**
	 * Shift down.
	 */
	private void sink(int i) {
		int left = 2 * i, right = left + 1, max = left;
		// If 2*i >= size, node i is a leaf
		while (left <= size) {
			// Determine the largest children of node i
			if (right <= size) {
				max = cmp.compare(heap[left], heap[right]) < 0 ? right : left;
			}
			// If the heap condition holds, stop. Else swap and go on.
			if (cmp.compare(heap[i], heap[max]) >= 0)
				return;
			swap(i, max);
			i = max;
			left = 2 * i;
			right = left + 1;
			max = left;
		}
	}

	/**
	 * Interchanges two array elements.
	 */
	private void swap(int i, int j) {
		T tmp = heap[i];
		heap[i] = heap[j];
		heap[j] = tmp;
	}

	boolean isEmpty() {
		return size == 0;
	}

	int size() {
		return size;
	}

	private void resize() {

		T[] newHeap = (T[]) new Object[2 * size()];
		for (int i = 1; i < heap.length; i++) {
			newHeap[i] = heap[i];
		}
		heap = newHeap;
	}
}