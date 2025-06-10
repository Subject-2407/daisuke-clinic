package adt;

import java.util.function.Predicate;

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
    private int size;

    public BST() {
        this.root = null;
        this.size = 0;
    }

    // returns tree size
    public int size() { return size; }

    // inserts a new node
    public void insert(T data) {
        root = insertRec(root, data);
    }

    // recursive helper for method above
    private Node<T> insertRec(Node<T> node, T data) {
        if (node == null) {
            size++;
            return new Node<>(data);
        }

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

    // search for a node by ID
    public T search(int id) {
        return searchRec(root, id);
    }

    // recursive helper for method above
    private T searchRec(Node<T> node, int id) {
        if (node == null) return null; // not found

        if (id == node.data.getId()) return node.data;
        if (id < node.data.getId()) return searchRec(node.left, id);
        return searchRec(node.right, id);
    }

    // search all matching nodes by predicate
    public Object[] searchAll(Predicate<T> predicate) {
        LinkedList<T> matches = new LinkedList<>();
        searchAllRec(root, predicate, matches);
        return matches.toArray();
    }   
    
    // recursive helper for method above (in order traversal)
    private void searchAllRec(Node<T> node, Predicate<T> predicate, LinkedList<T> matches) {
        if (node != null) {
            searchAllRec(node.left, predicate, matches);
            if (predicate.test(node.data)) {
                matches.insert(node.data);
            }
            searchAllRec(node.right, predicate, matches);
        }
    }

    // remove a node by ID
    public void remove(int id) {
        root = removeRec(root, id);
    }

    // recursive helper for method above
    private Node<T> removeRec(Node<T> node, int id) {
        if (node == null) return null;

        if (id < node.data.getId()) {
            node.left = removeRec(node.left, id);
        } else if (id > node.data.getId()) {
            node.right = removeRec(node.right, id);
        } else { // node to be removed found
            size--;
            if (node.left == null) return node.right;
            else if (node.right == null) return node.left;

            Node<T> minNode = getMinNode(node.right);
            node.data = minNode.data;
            node.right = removeRec(node.right, minNode.data.getId()); 
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

    // AVL balancing method
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

    public Object[] toArray() {
        LinkedList<T> list = new LinkedList<>();
        inOrderToArray(root, list);
        return list.toArray();
    }

    private void inOrderToArray(Node<T> node, LinkedList<T> list) {
        if (node != null) {
            inOrderToArray(node.left, list);
            list.insert(node.data);
            inOrderToArray(node.right, list);
        }
    }
}