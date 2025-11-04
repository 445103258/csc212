package com.ecommerce.datastructures;

/**
 * Custom Queue implementation using LinkedList
 * Time Complexity:
 * - enqueue(): O(1)
 * - dequeue(): O(1)
 * - peek(): O(1)
 * - isEmpty(): O(1)
 * Space Complexity: O(n)
 */
public class Queue<T> {
    private LinkedList<T> list;

    public Queue() {
        this.list = new LinkedList<>();
    }

    public void enqueue(T data) {
        list.addLast(data);
    }

    public T dequeue() {
        if (isEmpty()) {
            throw new RuntimeException("Queue is empty");
        }
        return list.removeFirst();
    }

    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("Queue is empty");
        }
        return list.getFirst();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }
}
