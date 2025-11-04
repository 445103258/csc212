package com.ecommerce.datastructures;

/**
 * Custom Stack implementation using LinkedList
 * Time Complexity:
 * - push(): O(1)
 * - pop(): O(1)
 * - peek(): O(1)
 * - isEmpty(): O(1)
 * Space Complexity: O(n)
 */
public class Stack<T> {
    private LinkedList<T> list;

    public Stack() {
        this.list = new LinkedList<>();
    }

    public void push(T data) {
        list.addFirst(data);
    }

    public T pop() {
        if (isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        return list.removeFirst();
    }

    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("Stack is empty");
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
