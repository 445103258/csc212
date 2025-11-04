package com.ecommerce.datastructures;

/**
 * Custom Binary Search Tree implementation for efficient searching
 * Time Complexity:
 * - insert(): O(log n) average, O(n) worst case
 * - search(): O(log n) average, O(n) worst case
 * - delete(): O(log n) average, O(n) worst case
 * - inorderTraversal(): O(n)
 * Space Complexity: O(n)
 */
public class BinarySearchTree<T extends Comparable<T>> {
    private Node<T> root;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> left;
        Node<T> right;

        Node(T data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }
    }

    public BinarySearchTree() {
        this.root = null;
        this.size = 0;
    }

    public void insert(T data) {
        root = insertRec(root, data);
        size++;
    }

    private Node<T> insertRec(Node<T> node, T data) {
        if (node == null) {
            return new Node<>(data);
        }

        int cmp = data.compareTo(node.data);
        if (cmp < 0) {
            node.left = insertRec(node.left, data);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, data);
        }

        return node;
    }

    public T search(T data) {
        return searchRec(root, data);
    }

    private T searchRec(Node<T> node, T data) {
        if (node == null) {
            return null;
        }

        int cmp = data.compareTo(node.data);
        if (cmp == 0) {
            return node.data;
        } else if (cmp < 0) {
            return searchRec(node.left, data);
        } else {
            return searchRec(node.right, data);
        }
    }

    public boolean contains(T data) {
        return search(data) != null;
    }

    public void delete(T data) {
        root = deleteRec(root, data);
        size--;
    }

    private Node<T> deleteRec(Node<T> node, T data) {
        if (node == null) {
            return null;
        }

        int cmp = data.compareTo(node.data);
        if (cmp < 0) {
            node.left = deleteRec(node.left, data);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, data);
        } else {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }

            node.data = findMin(node.right);
            node.right = deleteRec(node.right, node.data);
        }

        return node;
    }

    private T findMin(Node<T> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node.data;
    }

    public void inorderTraversal(ArrayList<T> result) {
        inorderRec(root, result);
    }

    private void inorderRec(Node<T> node, ArrayList<T> result) {
        if (node != null) {
            inorderRec(node.left, result);
            result.add(node.data);
            inorderRec(node.right, result);
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        root = null;
        size = 0;
    }
}
