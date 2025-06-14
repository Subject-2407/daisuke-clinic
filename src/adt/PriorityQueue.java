package adt;

import java.time.LocalDateTime;
import java.util.function.Predicate;

// Priority Queue based on date time (earlier dates have higher priority)

public class PriorityQueue<T> {
    private class Node {
        T value;
        LocalDateTime time;
        Node next;

        Node(T value, LocalDateTime time) {
            this.value = value;
            this.time = time;
            this.next = null;
        }
    }

    private Node head;
    private int size;

    public PriorityQueue() {
        this.head = null;
        this.size = 0;
    }

    public void enqueue(T value, LocalDateTime time) {
        Node newNode = new Node(value, time);

        if (head == null || time.isBefore(head.time)) {
            newNode.next = head;
            head = newNode;
        } else {
            Node temp = head;
            while (temp.next != null && time.isAfter(temp.next.time)) {
                temp = temp.next;
            }
            newNode.next = temp.next;
            temp.next = newNode;
        }
        size++;
    }

    public T dequeue() {
        if (head == null) return null;

        T value = head.value;
        head = head.next;
        size--;
        return value;
    }

    public T peek() {
        if (head == null) return null;
        return head.value;
    }

    public int size() { return size; }

    public T removeIf(Predicate<T> predicate) {
        if (head == null) return null;

        T value;

        // check if node matches the predicate
        if (predicate.test(head.value)) {
            value = head.value;
            head = head.next;
            size--;
            return value;
        }

        Node prev = head;
        Node current = head.next;

        while (current != null) {
            if (predicate.test(current.value)) {
                value = current.value;
                prev.next = current.next;
                size--;
                return value;
            }
            prev = current;
            current = current.next;
        }

        return null;
    }

    public T find(Predicate<T> predicate) {
        Node current = head;
        while (current != null) {
            if (predicate.test(current.value)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public T[] findAll(Predicate<T> predicate) {
        LinkedList<T> matches = new LinkedList<>();
        Node current = head;
        while (current != null) {
            if (predicate.test(current.value)) {
                matches.insert(current.value);
            }
            current = current.next;
        }
        return (T[]) matches.toArray();
    }

    public Object[] toArray() {
        Object[] array = new Object[size];
        Node temp = head;
        for (int i = 0; i < size; i++) {
            array[i] = temp.value;
            temp = temp.next;
        }
        return array;
    }

    public boolean isEmpty() { return size == 0; }
}
