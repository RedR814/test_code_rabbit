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
     * Removes the smallest (minimum) element from the tree.
     *
     * The tree is updated so that red-black invariants and subtree sizes remain valid after removal.
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
     * Removes the minimum element from the subtree rooted at {@code h} and restores red-black tree balance.
     *
     * @param h the root of the subtree from which to remove the minimum element
     * @return  the root of the updated subtree after deletion and rebalancing (or {@code null} if the subtree is empty)
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
     * Removes the maximum (largest) element from the tree.
     *
     * The tree is rebalanced to preserve red-black invariants after removal.
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
     * Deletes the maximum element from the subtree rooted at `h` and restores red-black invariants.
     *
     * @param h the root of the subtree to remove the maximum from
     * @return the root of the updated subtree, or `null` if the subtree becomes empty
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
     * Removes the specified element from the tree if present.
     *
     * If the element is not in the tree, the method does nothing. After removal the
     * tree's red–black invariants are restored and the root (if present) is black.
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
     * Retrieve the smallest element in the tree.
     *
     * @return the smallest (minimum) element stored in the tree
     * @throws NoSuchElementException if the tree is empty
     */
    public T min() {
        if (isEmpty()) throw new NoSuchElementException("calls min() with empty symbol table");
        return min(root).data;
    }

    /**
     * Finds the node containing the minimum key in the subtree rooted at x.
     *
     * @param x the root of the subtree to search (must not be null)
     * @return the node with the minimum key in the subtree rooted at x
     */
    private Node min(Node x) {
        if (x.left == null) return x;
        else return min(x.left);
    }

    /**
     * Retrieves the largest element stored in the tree.
     *
     * @return the largest element in the tree
     * @throws NoSuchElementException if the tree is empty
     */
    public T max() {
        if (isEmpty()) throw new NoSuchElementException("calls max() with empty symbol table");
        return max(root).data;
    }

    /**
     * Finds the node with the maximum key in the subtree rooted at the given node.
     *
     * @param x the root of the subtree (must not be null)
     * @return  the node containing the maximum key in that subtree
     */
    private Node max(Node x) {
        if (x.right == null) return x;
        else return max(x.right);
    }

    /**
     * Prints the tree's elements in ascending order to standard output, then writes a trailing newline.
     */
    public void inorder() {
        inorder(root);
        System.out.println();
    }

    /**
     * Performs an in-order traversal of the subtree rooted at {@code h}, printing each node's data followed by a space to standard output.
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
     * Prints the tree's keys in pre-order (root, left, right) and emits a trailing newline.
     */
    public void preorder() {
        preorder(root);
        System.out.println();
    }

    /**
     * Performs a preorder traversal of the subtree rooted at {@code h} and prints each node's data followed by a space to standard output.
     *
     * @param h the root of the subtree to traverse; may be {@code null}
     */
    private void preorder(Node h) {
        if (h == null) return;
        System.out.print(h.data + " ");
        preorder(h.left);
        preorder(h.right);
    }

    /**
     * Prints the tree's elements in post-order (left, right, node) to standard output, then prints a trailing newline.
     */
    public void postorder() {
        postorder(root);
        System.out.println();
    }

    /**
     * Prints the keys of the subtree rooted at the given node in post-order (left, right, node).
     *
     * Each element is written to System.out followed by a space.
     *
     * @param h the root of the subtree to print; if null nothing is printed
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
     * @return `true` if the tree contains no elements, `false` otherwise.
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Compute the height of the tree.
     *
     * The height is the number of edges on the longest root-to-leaf path; an empty tree has height -1.
     *
     * @return the tree height, or -1 if the tree is empty
     */
    public int height() {
        return height(root);
    }

    /**
     * Compute the height of the subtree rooted at the given node.
     *
     * @param x the root of the subtree, or {@code null} to denote an empty subtree
     * @return the height (number of edges on the longest path from {@code x} to a leaf), or {@code -1} if {@code x} is {@code null}
     */
    private int height(Node x) {
        if (x == null) return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    /**
     * Finds the greatest element less than or equal to the given value.
     *
     * @param data the value to compare against
     * @return the greatest element less than or equal to {@code data}, or {@code null} if none exists
     */
    public T floor(T data) {
        Node x = floor(root, data);
        if (x == null) return null;
        else return x.data;
    }

    /**
     * Finds the node with the greatest key less than or equal to {@code data} in the subtree rooted at {@code x}.
     *
     * @param x the subtree root to search (may be {@code null})
     * @param data the search key
     * @return the node whose key is the floor of {@code data} in this subtree, or {@code null} if none exists
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
     * Finds the smallest element greater than or equal to the specified value.
     *
     * @param data the value to compare against
     * @return the smallest element greater than or equal to `data`, or `null` if no such element exists
     */
    public T ceiling(T data) {
        Node x = ceiling(root, data);
        if (x == null) return null;
        else return x.data;
    }

    /**
     * Finds the node with the smallest key greater than or equal to the given search key within the subtree rooted at x.
     *
     * @param x    the root of the subtree to search (may be null)
     * @param data the search key
     * @return the node whose key is the smallest key >= {@code data} in the subtree, or {@code null} if no such node exists
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
     * Selects the element with rank k (0-based) in ascending order.
     *
     * @param k the rank to select; 0 returns the smallest element and size()-1 returns the largest
     * @return the element whose rank is k (the (k+1)th smallest element)
     * @throws IllegalArgumentException if k is negative or k >= size()
     */
    public T select(int k) {
        if (k < 0 || k >= size()) throw new IllegalArgumentException();
        Node x = select(root, k);
        return x.data;
    }

    /**
     * Finds the node containing the element with the specified in-order rank within the subtree rooted at `x`.
     *
     * @param x the root of the subtree to search
     * @param k the zero-based rank within the subtree (0 = smallest element)
     * @return the node whose in-order rank is `k`, or `null` if `x` is `null` or `k` is out of range
     */
    private Node select(Node x, int k) {
        if (x == null) return null;
        int t = size(x.left);
        if (t > k) return select(x.left, k);
        else if (t < k) return select(x.right, k - t - 1);
        else return x;
    }

    /**
     * Computes the number of elements strictly less than the given value.
     *
     * @param data the value to compare against
     * @return the number of elements less than {@code data}
     */
    public int rank(T data) {
        return rank(root, data);
    }

    /**
     * Compute the number of elements strictly less than the given key in the subtree rooted at {@code x}.
     *
     * @param x    the root of the subtree to search (may be {@code null})
     * @param data the key whose rank (count of smaller elements) is requested
     * @return the count of elements in the subtree rooted at {@code x} that are less than {@code data};
     *         returns 0 if {@code x} is {@code null}
     */
    private int rank(Node x, T data) {
        if (x == null) return 0;
        int cmp = data.compareTo(x.data);
        if (cmp < 0) return rank(x.left, data);
        else if (cmp > 0) return 1 + size(x.left) + rank(x.right, data);
        else return size(x.left);
    }

    /**
     * Count elements in the tree within the inclusive range [lo, hi].
     *
     * @param lo the lower bound (inclusive)
     * @param hi the upper bound (inclusive)
     * @return the number of elements >= {@code lo} and <= {@code hi}; returns 0 if {@code lo.compareTo(hi) > 0}
     */
    public int size(T lo, T hi) {
        if (lo.compareTo(hi) > 0) return 0;
        if (contains(hi)) return rank(hi) - rank(lo) + 1;
        else return rank(hi) - rank(lo);
    }

    /**
     * Provides an iterator that traverses the tree's elements in ascending (in-order) order.
     *
     * @return an {@link Iterator} over the tree's elements in ascending order
     */
    public Iterator<T> iterator() {
        return new RedBlackTreeIterator();
    }

    private class RedBlackTreeIterator implements Iterator<T> {
        private Stack<Node> stack = new Stack<>();
        
        /**
         * Creates a new iterator over the tree that will traverse elements in ascending order.
         *
         * <p>Initializes the iterator's internal stack by pushing the leftmost path starting from the tree root,
         * so the first call to {@code next()} returns the smallest element.</p>
         */
        public RedBlackTreeIterator() {
            pushLeft(root);
        }
        
        /**
         * Pushes the given node and all of its left descendants onto the traversal stack.
         *
         * @param x the starting node whose left spine will be pushed; may be {@code null}
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
         * @return true if the iteration has more elements, false otherwise.
         */
        public boolean hasNext() {
            return !stack.isEmpty();
        }
        
        /**
         * Advance the iterator and return the next element in ascending order.
         *
         * @return the next element in ascending order
         */
        public T next() {
            Node current = stack.pop();
            pushLeft(current.right);
            return current.data;
        }
        
        /**
         * Disables removal of elements via this iterator.
         *
         * @throws UnsupportedOperationException always thrown to indicate removal is not supported
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}