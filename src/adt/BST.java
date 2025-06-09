package adt;

import implementation.model.interfaces.Identifiable;

// Generic BST (with AVL balancing) for identifiable objects (using the 'id' field as the key)

public class BST<T extends Identifiable> {
    private static class Node<T> {
        T data;
        Node<T> left, right;
        int height;

        Node (T data) {
            this.data = data;
            this.height = 1;
        }
    }

    private Node<T> root;

    public BST() {
        this.root = null;
    }

    // inserts a new node
    public void insert(T data) {
        root = insertRec(root, data);
    }

    // recursive helper for insert method
    private Node<T> insertRec(Node<T> node, T data) {
        if (node == null) return new Node<>(data);

        if (data.getId() < node.data.getId()) {
            node.left = insertRec(node.left, data);
        } else if (data.getId() > node.data.getId()) {
            node.right = insertRec(node.right, data);
        } else {
            return node;
        }

        // update height and balance the tree
        updateHeight(node);
        return balance(node);
    }

    // Public method to search for a node by ID
    public T search(int id) {
        return searchRec(root, id);
    }

    // Recursive helper to search for an ID
    private T searchRec(Node<T> node, int id) {
        if (node == null) return null; // not found

        if (id == node.data.getId()) return node.data;
        if (id < node.data.getId()) return searchRec(node.left, id);
        return searchRec(node.right, id);
    }

    // Public method to remove a node by ID
    public void remove(int id) {
        root = removeRec(root, id);
    }

    // Recursive helper for deletion with AVL balancing
    private Node<T> removeRec(Node<T> node, int id) {
        if (node == null) return null;

        if (id < node.data.getId()) {
            node.left = removeRec(node.left, id);
        } else if (id > node.data.getId()) {
            node.right = removeRec(node.right, id);
        } else {
            // Node with one or no child
            if (node.left == null) return node.right;
            else if (node.right == null) return node.left;

            // Node with two children: get inorder successor
            Node<T> minNode = getMinNode(node.right);
            node.data = minNode.data; // replace current node data
            node.right = removeRec(node.right, minNode.data.getId()); // delete successor
        }

        // Update height and balance
        updateHeight(node);
        return balance(node);
    }

    // helper method to find smallest node in a subtree
    private Node<T> getMinNode(Node<T> node) {
        while (node.left != null) node = node.left;
        return node;
    }

    // balancing method for AVL
    private Node<T> balance(Node<T> node) {
        int bf = getBalanceFactor(node);

        // if left subtree is heavy
        if (bf > 1) {
            // left-right case
            if (getBalanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            // left-left case
            return rotateRight(node);
        }

        // if right subtree is heavy
        if (bf < -1) {
            // right-left case
            if (getBalanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            // right-right case
            return rotateLeft(node);
        }

        return node;
    }

    // returns the height of a node
    private int getHeight(Node<T> node) {
        return (node == null) ? 0 : node.height;
    }

    // updates the height of a node
    private void updateHeight(Node<T> node) {
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    // calculates balance factor
    private int getBalanceFactor(Node<T> node) {
        return getHeight(node.left) - getHeight(node.right);
    }

    // right rotation for left-left and right-left cases
    private Node<T> rotateRight(Node<T> y) {
        Node<T> x = y.left;
        Node<T> T2 = x.right;

        // rotate
        x.right = y;
        y.left = T2;

        // update height of nodes
        updateHeight(y);
        updateHeight(x);

        return x; // returns new root
    }

    // left rotation for right-right and left-right cases
    private Node<T> rotateLeft(Node<T> x) {
        Node<T> y = x.right;
        Node<T> T2 = y.left;

        // rotate
        y.left = x;
        x.right = T2;

        // update height of nodes
        updateHeight(x);
        updateHeight(y);

        return y; // returns new root
    }

    // traversals

    public void inOrder() {
        if (this.root == null) {
            System.out.println("No data available.\n");
            return;
        }
        inOrderRec(root);
        System.out.println();
    }

    private void inOrderRec(Node<T> node) {
        if (node != null) {
            inOrderRec(node.left);
            System.out.println(node.data);
            inOrderRec(node.right);
        }
    }

    public void preOrder() {
        preOrderRec(root);
        System.out.println();
    }

    private void preOrderRec(Node<T> node) {
        if (node != null) {
            System.out.println(node.data);
            preOrderRec(node.left);
            preOrderRec(node.right);
        }
    }

    public void postOrder() {
        postOrderRec(root);
        System.out.println();
    }

    private void postOrderRec(Node<T> node) {
        if (node != null) {
            postOrderRec(node.left);
            postOrderRec(node.right);
            System.out.println(node.data);
        }
    }
}