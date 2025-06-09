package adt;

import java.util.function.Predicate;

// Generic Singly Linked List

public class LinkedList<T> {
    private class Node {
        T value;
        Node next;

        Node(T value) {
            this.value = value;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public LinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // Returns Linked List size
    public int size() { return size; }

    // Returns a Node by index
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        if (index == size - 1) {
            return tail.value;
        }

        Node temp = head;
        for (int i = 0; i < index; i++) {
            temp = temp.next;
        }
        return temp.value;
    }

    // Returns head Node
    public T getFirst() {
        if (head == null) {
            return null;
        }
        return head.value;
    }

    // Returns tail Node
    public T getLast() {
        if (tail == null) {
            return null;
        }
        return tail.value;
    }

    // Returns Node's index by Node's value
    public int search(T value) {
        Node temp = head;
        for (int i = 0; i < size; i++) {
            if ((temp.value == null && value == null) || (temp.value != null && temp.value.equals(value))) {
                return i;
            }
            temp = temp.next;
        }

        return -1;
    }

    // Find first matching Node by a predicate
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

    // Find all matching Nodes by a predicate
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

    // Inserts a new Node (after tail)
    public void insert(T value) {
        insertLast(value);
    }

    // Inserts a new Node at specific index
    public void insertAt(int index, T value) {
        if (index < 0 || index > size) {
            return;
        }

        if (index == 0) {
            Node temp = head;
            head = new Node(value);
            head.next = temp;
            if (tail == null) tail = head;
        } else {
            Node temp = head;
            for (int i = 0; i < index - 1; i++) {
                temp = temp.next;
            }
            Node after = temp.next;
            temp.next = new Node(value);
            temp.next.next = after;
        }
        size++;
    }

    // Inserts a new Node at head
    public void insertFirst(T value) {
        insertAt(0, value);
    }

    // Inserts a new Node after tail
    public void insertLast(T value) {
        if (head == null && tail == null) {
            head = tail = new Node(value);
        } else {
            Node vertex = new Node(value);
            tail.next = vertex;
            tail = vertex;
        }
        size++;
    }

    // Updates a Node's value by index
    public void set(int index, T value) {
        if (index < 0 || index >= size) {
            return;
        }

        if (index == size - 1) {
            tail.value = value;
            return;
        }

        Node temp = head;
        for (int i = 0; i < index; i++) {
            temp = temp.next;
        }
        temp.value = value;
    }

    // Removes a Node by index
    public T remove(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        T value;

        if (index == 0) {
            value = head.value;
            head = head.next;
            if (head == null) tail = null;
        } else {
            Node temp = head;
            for (int i = 0; i < index - 1; i++) {
                temp = temp.next;
            }
            value = temp.next.value;
            temp.next = temp.next.next;
            if (temp.next == null) tail = temp;
        }
        size--;
        return value;
    }

    // Removes a Node by a predicate
    public T removeIf(Predicate<T> predicate) {
        if (head == null) return null;

        T value;

        if (predicate.test(head.value)) {
            value = head.value;
            head = head.next;
            if (head == null) tail = null;
            size--;
            return value;
        }

        Node prev = head;
        Node current = head.next;

        while (current != null) {
            if (predicate.test(current.value)) {
                value = current.value;
                prev.next = current.next;
                if (current == tail) {
                    tail = prev;
                }
                size--;
                return value;
            }
            prev = current;
            current = current.next;
        }

        return null;
    }

    // Removes head Node
    public T removeFirst() {
        if (head == null) {
            return null;
        }

        T value = head.value;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return value;
    }

    // Removes tail Node
    public T removeLast() {
        if (head == null) {
            return null;
        }

        T value;

        if (head.next == null) {
            value = head.value;
            head = tail = null;
        } else {
            Node temp = head;
            while (temp.next.next != null) {
                temp = temp.next;
            }
            value = temp.next.value;
            temp.next = null;
            tail = temp;
        }
        size--;
        return value;
    }

    // additional method to convert this linked list to an array
    public Object[] toArray() {
        Object[] array = new Object[size];
        Node temp = head;
        for (int i = 0; i < size; i++) {
            array[i] = temp.value;
            temp = temp.next;
        }
        return array;
    }
}


