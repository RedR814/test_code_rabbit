package redBulue;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Stack;

public class RedBlackTree<T extends Comparable<T>> implements Iterable<T> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private class Node {
        T data;
        Node left;
        Node right;
        boolean color;
        int size;

        Node(T data, boolean color, int size) {
            this.data = data;
            this.color = color;
            this.size = size;
        }
    }

    private Node root;

    // 判断节点是否为红色
    private boolean isRed(Node x) {
        if (x == null) return false;
        return x.color == RED;
    }

    // 获取节点数量
    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null) return 0;
        return x.size;
    }

    // 查找操作
    public boolean contains(T data) {
        return contains(root, data);
    }

    private boolean contains(Node x, T data) {
        while (x != null) {
            int cmp = data.compareTo(x.data);
            if (cmp < 0) x = x.left;
            else if (cmp > 0) x = x.right;
            else return true;
        }
        return false;
    }

    // 插入操作
    public void insert(T data) {
        root = insert(root, data);
        root.color = BLACK;
    }

    private Node insert(Node h, T data) {
        if (h == null) return new Node(data, RED, 1);

        int cmp = data.compareTo(h.data);
        if (cmp < 0) h.left = insert(h.left, data);
        else if (cmp > 0) h.right = insert(h.right, data);
        else h.data = data;

        // 修复红黑树性质
        if (isRed(h.right) && !isRed(h.left)) h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left) && isRed(h.right)) flipColors(h);

        h.size = size(h.left) + size(h.right) + 1;
        return h;
    }

    // 左旋转
    private Node rotateLeft(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = h.color;
        h.color = RED;
        x.size = h.size;
        h.size = size(h.left) + size(h.right) + 1;
        return x;
    }

    // 右旋转
    private Node rotateRight(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = h.color;
        h.color = RED;
        x.size = h.size;
        h.size = size(h.left) + size(h.right) + 1;
        return x;
    }

    // 颜色翻转
    private void flipColors(Node h) {
        h.color = RED;
        h.left.color = BLACK;
        h.right.color = BLACK;
    }

    /**
     * Removes the smallest element from the tree.
     *
     * @throws NoSuchElementException if the tree is empty
     */
    public void deleteMin() {
        if (isEmpty()) throw new NoSuchElementException("RedBlackBST underflow");
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;
        root = deleteMin(root);
        if (root != null) root.color = BLACK;
    }

    /**
     * Deletes the minimum node from the subtree rooted at `h` and returns the new subtree root.
     *
     * Performs necessary color/rotation adjustments to preserve red-black invariants during deletion.
     *
     * @param h the root of the subtree to delete the minimum from; may be returned as `null` if the subtree becomes empty
     * @return the updated root of the subtree after removal (possibly `null`)
     */
    private Node deleteMin(Node h) {
        if (h.left == null)
            return null;

        if (!isRed(h.left) && !isRed(h.left.left))
            h = moveRedLeft(h);

        h.left = deleteMin(h.left);

        return balance(h);
    }

    /**
     * Removes the largest element from this red-black binary search tree.
     *
     * @throws NoSuchElementException if the tree is empty
     */
    public void deleteMax() {
        if (isEmpty()) throw new NoSuchElementException("RedBlackBST underflow");
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;
        root = deleteMax(root);
        if (root != null) root.color = BLACK;
    }

    /**
     * Deletes the maximum element from the subtree rooted at h and restores red-black properties.
     *
     * @param h the root of the subtree from which to remove the maximum element
     * @return the root of the updated subtree after removal, or `null` if the subtree becomes empty
     */
    private Node deleteMax(Node h) {
        if (isRed(h.left))
            h = rotateRight(h);

        if (h.right == null)
            return null;

        if (!isRed(h.right) && !isRed(h.right.left))
            h = moveRedRight(h);

        h.right = deleteMax(h.right);

        return balance(h);
    }

    /**
     * Removes the specified element from the tree if present and restores red–black balance.
     *
     * If the element is not found the tree remains unchanged. After removal the tree's
     * red–black invariants are maintained and the root is set to black.
     *
     * @param data the element to remove
     */
    public void delete(T data) {
        if (!contains(data)) return;
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;
        root = delete(root, data);
        if (root != null) root.color = BLACK;
    }

    private Node delete(Node h, T data) {
        if (data.compareTo(h.data) < 0) {
            if (!isRed(h.left) && !isRed(h.left.left))
                h = moveRedLeft(h);
            h.left = delete(h.left, data);
        } else {
            if (isRed(h.left))
                h = rotateRight(h);
            if (data.compareTo(h.data) == 0 && (h.right == null))
                return null;
            if (!isRed(h.right) && !isRed(h.right.left))
                h = moveRedRight(h);
            if (data.compareTo(h.data) == 0) {
                Node x = min(h.right);
                h.data = x.data;
                h.right = deleteMin(h.right);
            } else {
                h.right = delete(h.right, data);
            }
        }
        return balance(h);
    }

    // 移动红色节点到左子树
    private Node moveRedLeft(Node h) {
        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
            flipColors(h);
        }
        return h;
    }

    // 移动红色节点到右子树
    private Node moveRedRight(Node h) {
        flipColors(h);
        if (isRed(h.left.left)) {
            h = rotateRight(h);
            flipColors(h);
        }
        return h;
    }

    // 平衡节点
    private Node balance(Node h) {
        if (isRed(h.right)) h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left) && isRed(h.right)) flipColors(h);

        h.size = size(h.left) + size(h.right) + 1;
        return h;
    }

    /**
     * Finds the smallest element stored in the tree.
     *
     * @return the smallest element in the tree
     * @throws NoSuchElementException if the tree is empty
     */
    public T min() {
        if (isEmpty()) throw new NoSuchElementException("calls min() with empty symbol table");
        return min(root).data;
    }

    /**
     * Locate the node with the minimum key in the subtree rooted at x.
     *
     * @param x the root of the subtree to search
     * @return the node containing the smallest element in the subtree rooted at x
     */
    private Node min(Node x) {
        if (x.left == null) return x;
        else return min(x.left);
    }

    /**
     * Retrieve the largest element stored in the tree.
     *
     * @return the largest element in the tree
     * @throws NoSuchElementException if the tree is empty
     */
    public T max() {
        if (isEmpty()) throw new NoSuchElementException("calls max() with empty symbol table");
        return max(root).data;
    }

    /**
     * Finds the node with the largest element in the subtree rooted at x.
     *
     * @param x the root of the subtree to search (must not be null)
     * @return the rightmost node in the subtree containing the maximum element
     */
    private Node max(Node x) {
        if (x.right == null) return x;
        else return max(x.right);
    }

    /**
     * Prints the tree's elements in ascending order using an in-order traversal.
     *
     * Prints each element separated by a space, then outputs a trailing newline.
     */
    public void inorder() {
        inorder(root);
        System.out.println();
    }

    /**
     * Performs an in-order traversal of the subtree rooted at {@code h}, printing each element followed by a space to standard output.
     *
     * @param h the root of the subtree to traverse; may be {@code null}
     */
    private void inorder(Node h) {
        if (h == null) return;
        inorder(h.left);
        System.out.print(h.data + " ");
        inorder(h.right);
    }

    /**
     * Prints the tree's elements in pre-order (root, left, right).
     *
     * Prints node values separated by a single space and terminates the output with a newline.
     * If the tree is empty this writes only a newline.
     */
    public void preorder() {
        preorder(root);
        System.out.println();
    }

    /**
     * Prints the subtree rooted at {@code h} in pre-order to standard output, separating elements with a space.
     *
     * @param h the root of the subtree to print (may be {@code null})
     */
    private void preorder(Node h) {
        if (h == null) return;
        System.out.print(h.data + " ");
        preorder(h.left);
        preorder(h.right);
    }

    /**
     * Prints the tree's elements in post-order traversal.
     *
     * Elements are printed in ascending post-order sequence separated by single spaces and the output is terminated with a newline.
     */
    public void postorder() {
        postorder(root);
        System.out.println();
    }

    /**
     * Performs a post-order traversal of the subtree rooted at h and prints each node's data followed by a space.
     *
     * Does nothing if h is null.
     *
     * @param h the root of the subtree to traverse
     */
    private void postorder(Node h) {
        if (h == null) return;
        postorder(h.left);
        postorder(h.right);
        System.out.print(h.data + " ");
    }

    /**
     * Checks whether the tree contains no elements.
     *
     * @return {@code true} if the tree contains no elements, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Compute the height of the tree.
     *
     * @return the height of the tree; returns -1 for an empty tree (null root)
     */
    public int height() {
        return height(root);
    }

    /**
     * Compute the height of the subtree rooted at the given node.
     *
     * @param x the root of the subtree, or {@code null} to represent an empty tree
     * @return the height (number of edges on the longest path from {@code x} to a leaf); {@code -1} if {@code x} is {@code null}
     */
    private int height(Node x) {
        if (x == null) return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    /**
     * Finds the greatest element less than or equal to the given value.
     *
     * @param data the value to compare against
     * @return the greatest element <= {@code data}, or {@code null} if no such element exists
     */
    public T floor(T data) {
        Node x = floor(root, data);
        if (x == null) return null;
        else return x.data;
    }

    /**
     * Finds the node containing the greatest element less than or equal to `data` in the subtree rooted at `x`.
     *
     * @param x the root of the subtree to search (may be null)
     * @param data the value to compute the floor for
     * @return the node whose `data` is the largest value less than or equal to `data` in the subtree, or `null` if none exists
     */
    private Node floor(Node x, T data) {
        if (x == null) return null;
        int cmp = data.compareTo(x.data);
        if (cmp == 0) return x;
        if (cmp < 0) return floor(x.left, data);
        Node t = floor(x.right, data);
        if (t != null) return t;
        else return x;
    }

    /**
     * Finds the smallest element in the tree that is greater than or equal to the given value.
     *
     * @param data the value to compare against
     * @return the smallest element >= {@code data}, or {@code null} if no such element exists
     */
    public T ceiling(T data) {
        Node x = ceiling(root, data);
        if (x == null) return null;
        else return x.data;
    }

    /**
     * Finds the node with the smallest value greater than or equal to the given value in the subtree rooted at x.
     *
     * @param x    root of the subtree to search, may be null
     * @param data value to compare against node keys
     * @return the node containing the smallest element >= data, or `null` if no such element exists
     */
    private Node ceiling(Node x, T data) {
        if (x == null) return null;
        int cmp = data.compareTo(x.data);
        if (cmp == 0) return x;
        if (cmp > 0) return ceiling(x.right, data);
        Node t = ceiling(x.left, data);
        if (t != null) return t;
        else return x;
    }

    /**
     * Finds the element whose rank is k in the tree's ascending order (0-based).
     *
     * @param k the rank of the desired element, where 0 is the smallest element
     * @return the element with rank k
     * @throws IllegalArgumentException if k is less than 0 or greater than or equal to the tree size
     */
    public T select(int k) {
        if (k < 0 || k >= size()) throw new IllegalArgumentException();
        Node x = select(root, k);
        return x.data;
    }

    /**
     * Finds the node containing the element of rank k within the subtree rooted at x.
     *
     * @param x the root of the subtree to search (may be null)
     * @param k the rank (0-based) of the desired element within the subtree
     * @return the `Node` whose rank is k within the subtree, or `null` if k is out of range or x is null
     */
    private Node select(Node x, int k) {
        if (x == null) return null;
        int t = size(x.left);
        if (t > k) return select(x.left, k);
        else if (t < k) return select(x.right, k - t - 1);
        else return x;
    }

    /**
     * Compute the number of elements in the tree that are strictly less than the given value.
     *
     * @param data the value whose rank to compute
     * @return the count of elements strictly less than `data` (rank starting at 0)
     */
    public int rank(T data) {
        return rank(root, data);
    }

    /**
     * Computes the number of elements strictly less than {@code data} in the subtree rooted at {@code x}.
     *
     * @param x    the root of the subtree to examine; may be {@code null}
     * @param data the key to compare against
     * @return the count of elements in the subtree rooted at {@code x} that are less than {@code data} (returns 0 if {@code x} is {@code null})
     */
    private int rank(Node x, T data) {
        if (x == null) return 0;
        int cmp = data.compareTo(x.data);
        if (cmp < 0) return rank(x.left, data);
        else if (cmp > 0) return 1 + size(x.left) + rank(x.right, data);
        else return size(x.left);
    }

    /**
     * Count elements within the inclusive range [lo, hi].
     *
     * @param lo the lower bound (inclusive)
     * @param hi the upper bound (inclusive)
     * @return the number of elements between {@code lo} and {@code hi} (inclusive); returns {@code 0} if {@code lo.compareTo(hi) > 0}
     */
    public int size(T lo, T hi) {
        if (lo.compareTo(hi) > 0) return 0;
        if (contains(hi)) return rank(hi) - rank(lo) + 1;
        else return rank(hi) - rank(lo);
    }

    /**
     * Returns an iterator that traverses the tree's elements in ascending (in-order) order.
     *
     * The returned iterator performs an in-order traversal from smallest to largest element;
     * its remove() method is unsupported and will throw UnsupportedOperationException.
     *
     * @return an in-order {@link Iterator} over the tree's elements
     */
    public Iterator<T> iterator() {
        return new RedBlackTreeIterator();
    }

    private class RedBlackTreeIterator implements Iterator<T> {
        private Stack<Node> stack = new Stack<>();
        
        /**
         * Creates an in-order iterator positioned at the smallest element.
         *
         * Initializes the iterator's stack by pushing the leftmost path starting
         * from the tree root so that the first call to {@link #next()} returns
         * the smallest element in the tree.
         */
        public RedBlackTreeIterator() {
            pushLeft(root);
        }
        
        /**
         * Pushes the given node and all of its left descendants onto the iterator stack.
         *
         * @param x the root of the subtree whose left-path nodes will be pushed; may be null
         */
        private void pushLeft(Node x) {
            while (x != null) {
                stack.push(x);
                x = x.left;
            }
        }
        
        /**
         * Indicates whether the iterator has more elements.
         *
         * @return `true` if there are more elements to iterate over, `false` otherwise.
         */
        public boolean hasNext() {
            return !stack.isEmpty();
        }
        
        /**
         * Advance the iterator and return the next element in the tree's in-order sequence.
         *
         * @return the next element in the in-order traversal
         */
        public T next() {
            Node current = stack.pop();
            pushLeft(current.right);
            return current.data;
        }
        
        /**
         * Indicates that this iterator does not support element removal.
         *
         * @throws UnsupportedOperationException always thrown to signal that remove() is not supported by this iterator
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}