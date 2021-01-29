import java.io.PrintStream;
import java.util.NoSuchElementException;

public class IntQueueImpl <E> implements IntQueue <E>{

	Node head, tail;
	int counter;
	
	public IntQueueImpl() {
		head = tail = null;
		counter = 0;
	}
	
	public boolean isEmpty() {

		return head == null;
	}
	
	public void put(E item) {
		
		Node t = tail;
		tail = new Node(item);
		
		if (isEmpty()) {
			head = tail;
		} else {
			t.next = tail;
		}
		counter++;
	}
	
	public E get() throws NoSuchElementException {
		if (isEmpty()) {
			throw new NoSuchElementException("");
		}
		E v = (E) head.item;
		Node t = head.next;
		head = t;
		return v;
	}
	
	public E peek() throws NoSuchElementException {
		
		if (isEmpty()) {
			throw new NoSuchElementException("");
		}
		return (E)head.item;
	} 
	
	
	public void printQueue(PrintStream stream){
		
		if (isEmpty()) {
			stream.println("Empty list.");
		}
		
		for (Node t = head; t != null; t = t.next) {
			stream.print(t.item + " ");
		}
		
		stream.println();
	}
	
	
	public int size() {
		
		return counter;
	}
	
	class Node {
		
		E item;
		Node next;
		public Node(E item) {
			this.item = item;
			this.next = null;
		}
	}
}
