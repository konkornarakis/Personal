import java.io.PrintStream;
import java.util.*;

public class StringStackImpl <E> implements StringStack <E>{

	Node head;
	int counter;
	
	public StringStackImpl() {
		head = null;
		counter = 0;
	}
	
	public boolean isEmpty() {
		
		return head == null;
	}
	
	public void push(E item) {
		
		head = new Node(item, head);
		counter++;
	}
	
	public E pop() throws NoSuchElementException {
		
		if (isEmpty()) {
			throw new NoSuchElementException("");
		}
		Node t = head;
		head = t.next;
		counter--;
		return (E) t.item;
	}
	
	public E peek() throws NoSuchElementException {
		
		if (isEmpty()) {
			throw new NoSuchElementException("");
		}
		return (E) head.item;
	}
	
	
	public void printStack(PrintStream stream){
		
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
		public Node(E item, Node next) {
			this.item = item;
			this.next = next;
		}
	}
}
