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
     * Removes the smallest element from the tree, maintaining red–black invariants.
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
     * Deletes the minimum element from the subtree rooted at {@code h} and returns the updated subtree root.
     *
     * The method removes the leftmost node in the subtree and restores red–black tree invariants before returning.
     *
     * @param h the root of the subtree to delete the minimum from
     * @return the new root of the subtree after deletion, or {@code null} if the subtree becomes empty
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
     * Deletes the maximum (largest) element from the tree.
     *
     * Maintains the red–black tree invariants and updates the root color as required.
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
     * Removes the maximum element from the subtree rooted at h and returns the updated subtree root while preserving red–black invariants.
     *
     * @param h the root of the subtree to delete the maximum from
     * @return the new root of the subtree after deletion, or `null` if the subtree is empty
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
     * Removes the specified element from the tree if it exists.
     *
     * If the element is not present this method does nothing. When deletion occurs
     * the tree is rebalanced to preserve red–black invariants and the root is
     * ensured to be black after the operation.
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
     * Retrieve the smallest element stored in the tree.
     *
     * @return the smallest element in the tree
     * @throws NoSuchElementException if the tree is empty
     */
    public T min() {
        if (isEmpty()) throw new NoSuchElementException("calls min() with empty symbol table");
        return min(root).data;
    }

    /**
     * Finds the node containing the smallest element in the subtree rooted at x.
     *
     * @param x the root of the subtree to search; must not be null
     * @return the node with the minimum key in the subtree rooted at x
     */
    private Node min(Node x) {
        if (x.left == null) return x;
        else return min(x.left);
    }

    /**
     * Retrieve the largest element in the tree.
     *
     * @return the largest element stored in the tree
     * @throws NoSuchElementException if the tree is empty
     */
    public T max() {
        if (isEmpty()) throw new NoSuchElementException("calls max() with empty symbol table");
        return max(root).data;
    }

    /**
     * Finds the node containing the maximum key in the subtree rooted at the given node.
     *
     * @param x the root of the subtree to search
     * @return the rightmost (maximum) node in the subtree rooted at `x`
     */
    private Node max(Node x) {
        if (x.right == null) return x;
        else return max(x.right);
    }

    /**
     * Prints the tree's elements in in-order (ascending) to standard output and then writes a newline.
     */
    public void inorder() {
        inorder(root);
        System.out.println();
    }

    /**
     * Performs an in-order traversal of the subtree rooted at h and prints each element to System.out separated by a space.
     *
     * @param h the root of the subtree to traverse; if null nothing is printed
     */
    private void inorder(Node h) {
        if (h == null) return;
        inorder(h.left);
        System.out.print(h.data + " ");
        inorder(h.right);
    }

    /**
     * Performs a pre-order traversal of the tree and prints each node's value in traversal order, then writes a newline.
     */
    public void preorder() {
        preorder(root);
        System.out.println();
    }

    /**
     * Performs a pre-order traversal of the subtree rooted at the given node and prints each element followed by a space.
     *
     * @param h the root of the subtree to traverse; may be null
     */
    private void preorder(Node h) {
        if (h == null) return;
        System.out.print(h.data + " ");
        preorder(h.left);
        preorder(h.right);
    }

    /**
     * Prints the tree's elements in post-order and terminates the line.
     *
     * Performs a post-order traversal of the tree (left, right, root), printing each element in traversal order and then writing a newline.
     */
    public void postorder() {
        postorder(root);
        System.out.println();
    }

    /**
     * Performs a post-order traversal of the subtree rooted at the given node and prints each element followed by a space.
     *
     * @param h the root of the subtree to traverse; if `null`, nothing is printed
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
     * @return true if the tree contains no elements, false otherwise.
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Compute the height of the tree.
     *
     * @return the height defined as the number of edges on the longest path from the root to a leaf;
     *         returns -1 if the tree is empty
     */
    public int height() {
        return height(root);
    }

    /**
     * Compute the height of the subtree rooted at the given node.
     *
     * @param x the root of the subtree (may be null)
     * @return `-1` if `x` is null; otherwise the maximum number of edges on a path from `x` to a leaf
     */
    private int height(Node x) {
        if (x == null) return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    /**
     * Finds the largest element in the tree that is less than or equal to the given value.
     *
     * @param data the target value to compare against
     * @return the largest element <= {@code data}, or {@code null} if no such element exists
     */
    public T floor(T data) {
        Node x = floor(root, data);
        if (x == null) return null;
        else return x.data;
    }

    /**
     * Finds the node with the largest key less than or equal to the given key in the subtree rooted at `x`.
     *
     * @param x    the root of the subtree to search (may be null)
     * @param data the key to find the floor for
     * @return the node whose key is the floor of `data` in the subtree, or `null` if no such node exists
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
     * @param data the target value to compare against
     * @return `null` if no element is greater than or equal to `data`, otherwise the smallest element >= `data`
     */
    public T ceiling(T data) {
        Node x = ceiling(root, data);
        if (x == null) return null;
        else return x.data;
    }

    /**
     * Finds the node with the smallest element greater than or equal to the given key in the subtree rooted at {@code x}.
     *
     * @param x    the root of the subtree to search
     * @param data the key to compare
     * @return the node whose value is the smallest element >= {@code data}, or {@code null} if no such node exists
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
     * Selects the element with rank k (0-based) in the tree.
     *
     * @param k the rank of the desired element, where 0 denotes the smallest element
     * @return the element whose rank is k
     * @throws IllegalArgumentException if k is negative or k is greater than or equal to the tree size
     */
    public T select(int k) {
        if (k < 0 || k >= size()) throw new IllegalArgumentException();
        Node x = select(root, k);
        return x.data;
    }

    /**
     * Locate the node containing the element of rank k (0-based) within the subtree rooted at x.
     *
     * @param x the root of the subtree to search
     * @param k the desired rank (0-based)
     * @return the node whose rank is k within the subtree, or `null` if k is out of range
     */
    private Node select(Node x, int k) {
        if (x == null) return null;
        int t = size(x.left);
        if (t > k) return select(x.left, k);
        else if (t < k) return select(x.right, k - t - 1);
        else return x;
    }

    /**
     * Compute the number of elements in the tree strictly less than the given key.
     *
     * @param data the key to compare against elements in the tree
     * @return the count of elements less than `data` (rank, zero-based)
     */
    public int rank(T data) {
        return rank(root, data);
    }

    /**
     * Compute the number of elements strictly less than the given key within the subtree rooted at x.
     *
     * @param x    the root of the subtree to search (may be null)
     * @param data the key to compare against nodes in the subtree
     * @return the count of elements in the subtree rooted at x that are less than {@code data}
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
     * @param lo the lower bound of the range (inclusive)
     * @param hi the upper bound of the range (inclusive)
     * @return the number of elements x in the tree such that lo <= x <= hi
     */
    public int size(T lo, T hi) {
        if (lo.compareTo(hi) > 0) return 0;
        if (contains(hi)) return rank(hi) - rank(lo) + 1;
        else return rank(hi) - rank(lo);
    }

    /**
     * Creates an in-order iterator over the tree's elements.
     *
     * @return an Iterator that traverses the tree's elements in in-order (ascending) order
     */
    public Iterator<T> iterator() {
        return new RedBlackTreeIterator();
    }

    private class RedBlackTreeIterator implements Iterator<T> {
        private Stack<Node> stack = new Stack<>();
        
        /**
         * Initializes the iterator by pushing the leftmost path from the tree root onto the internal stack.
         *
         * Prepares the iterator so the next call to {@code next()} returns the smallest element.
         */
        public RedBlackTreeIterator() {
            pushLeft(root);
        }
        
        /**
         * Pushes all left descendants of the given node onto the iterator stack, starting with the node itself.
         *
         * @param x the root of the subtree whose left spine should be pushed onto the stack; may be null
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
         * @return true if the iterator has more elements, false otherwise.
         */
        public boolean hasNext() {
            return !stack.isEmpty();
        }
        
        /**
         * Advance the iterator and return the next element in the tree's in-order sequence.
         *
         * @return the next element in in-order traversal
         */
        public T next() {
            Node current = stack.pop();
            pushLeft(current.right);
            return current.data;
        }
        
        /**
         * Indicates that removal via this iterator is not supported.
         *
         * @throws UnsupportedOperationException always thrown; this iterator does not support element removal
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}