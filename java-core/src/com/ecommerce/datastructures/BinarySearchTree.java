package com.ecommerce.datastructures;

/**
 * Enhanced Binary Search Tree implementation for Phase II
 * Supports key-value pairs and range queries
 * Time Complexity:
 * - insert(): O(log n) average, O(n) worst case
 * - search(): O(log n) average, O(n) worst case
 * - delete(): O(log n) average, O(n) worst case
 * - rangeQuery(): O(log n + k) where k is number of results
 * - inorderTraversal(): O(n)
 * Space Complexity: O(n)
 */
public class BinarySearchTree<K extends Comparable<K>, V> {
    private Node<K, V> root;
    private int size;

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    public BinarySearchTree() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Insert a key-value pair into the BST
     * Time Complexity: O(log n) average
     */
    public void insert(K key, V value) {
        root = insertRec(root, key, value);
        size++;
    }

    private Node<K, V> insertRec(Node<K, V> node, K key, V value) {
        if (node == null) {
            return new Node<>(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insertRec(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, key, value);
        } else {
            // Update value if key already exists
            node.value = value;
            size--; // Don't count as new insertion
        }

        return node;
    }

    /**
     * Search for a value by key
     * Time Complexity: O(log n) average
     */
    public V search(K key) {
        Node<K, V> node = searchNode(root, key);
        return node != null ? node.value : null;
    }

    private Node<K, V> searchNode(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return searchNode(node.left, key);
        } else {
            return searchNode(node.right, key);
        }
    }

    /**
     * Check if a key exists in the BST
     * Time Complexity: O(log n) average
     */
    public boolean contains(K key) {
        return search(key) != null;
    }

    /**
     * Delete a key-value pair from the BST
     * Time Complexity: O(log n) average
     */
    public boolean delete(K key) {
        if (!contains(key)) {
            return false;
        }
        root = deleteRec(root, key);
        size--;
        return true;
    }

    private Node<K, V> deleteRec(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = deleteRec(node.left, key);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, key);
        } else {
            // Node to be deleted found
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }

            // Node has two children: get inorder successor
            Node<K, V> minNode = findMinNode(node.right);
            node.key = minNode.key;
            node.value = minNode.value;
            node.right = deleteRec(node.right, node.key);
        }

        return node;
    }

    private Node<K, V> findMinNode(Node<K, V> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /**
     * Range query: find all values with keys in [minKey, maxKey]
     * Time Complexity: O(log n + k) where k is number of results
     */
    public ArrayList<V> rangeQuery(K minKey, K maxKey) {
        ArrayList<V> result = new ArrayList<>();
        rangeQueryRec(root, minKey, maxKey, result);
        return result;
    }

    private void rangeQueryRec(Node<K, V> node, K minKey, K maxKey, ArrayList<V> result) {
        if (node == null) {
            return;
        }

        // If current key is greater than minKey, explore left subtree
        if (node.key.compareTo(minKey) > 0) {
            rangeQueryRec(node.left, minKey, maxKey, result);
        }

        // If current key is in range, add to result
        if (node.key.compareTo(minKey) >= 0 && node.key.compareTo(maxKey) <= 0) {
            result.add(node.value);
        }

        // If current key is less than maxKey, explore right subtree
        if (node.key.compareTo(maxKey) < 0) {
            rangeQueryRec(node.right, minKey, maxKey, result);
        }
    }

    /**
     * Get all values in sorted order (inorder traversal)
     * Time Complexity: O(n)
     */
    public ArrayList<V> inorderTraversal() {
        ArrayList<V> result = new ArrayList<>();
        inorderRec(root, result);
        return result;
    }

    private void inorderRec(Node<K, V> node, ArrayList<V> result) {
        if (node != null) {
            inorderRec(node.left, result);
            result.add(node.value);
            inorderRec(node.right, result);
        }
    }

    /**
     * Get all keys in sorted order
     * Time Complexity: O(n)
     */
    public ArrayList<K> getKeysSorted() {
        ArrayList<K> result = new ArrayList<>();
        getKeysRec(root, result);
        return result;
    }

    private void getKeysRec(Node<K, V> node, ArrayList<K> result) {
        if (node != null) {
            getKeysRec(node.left, result);
            result.add(node.key);
            getKeysRec(node.right, result);
        }
    }

    /**
     * Update value for existing key
     * Time Complexity: O(log n) average
     */
    public boolean update(K key, V newValue) {
        Node<K, V> node = searchNode(root, key);
        if (node != null) {
            node.value = newValue;
            return true;
        }
        return false;
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

    /**
     * Get minimum key in the tree
     * Time Complexity: O(log n) average
     */
    public K getMinKey() {
        if (root == null) return null;
        Node<K, V> minNode = findMinNode(root);
        return minNode.key;
    }

    /**
     * Get maximum key in the tree
     * Time Complexity: O(log n) average
     */
    public K getMaxKey() {
        if (root == null) return null;
        Node<K, V> node = root;
        while (node.right != null) {
            node = node.right;
        }
        return node.key;
    }
}