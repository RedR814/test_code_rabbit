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

    // 删除最小值
    public void deleteMin() {
        if (isEmpty()) throw new NoSuchElementException("RedBlackBST underflow");
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;
        root = deleteMin(root);
        if (root != null) root.color = BLACK;
    }

    private Node deleteMin(Node h) {
        if (h.left == null)
            return null;

        if (!isRed(h.left) && !isRed(h.left.left))
            h = moveRedLeft(h);

        h.left = deleteMin(h.left);

        return balance(h);
    }

    // 删除最大值
    public void deleteMax() {
        if (isEmpty()) throw new NoSuchElementException("RedBlackBST underflow");
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;
        root = deleteMax(root);
        if (root != null) root.color = BLACK;
    }

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

    // 删除操作
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

    // 查找最小值
    public T min() {
        if (isEmpty()) throw new NoSuchElementException("calls min() with empty symbol table");
        return min(root).data;
    }

    private Node min(Node x) {
        if (x.left == null) return x;
        else return min(x.left);
    }

    // 查找最大值
    public T max() {
        if (isEmpty()) throw new NoSuchElementException("calls max() with empty symbol table");
        return max(root).data;
    }

    private Node max(Node x) {
        if (x.right == null) return x;
        else return max(x.right);
    }

    // 中序遍历打印
    public void inorder() {
        inorder(root);
        System.out.println();
    }

    private void inorder(Node h) {
        if (h == null) return;
        inorder(h.left);
        System.out.print(h.data + " ");
        inorder(h.right);
    }

    // 前序遍历打印
    public void preorder() {
        preorder(root);
        System.out.println();
    }

    private void preorder(Node h) {
        if (h == null) return;
        System.out.print(h.data + " ");
        preorder(h.left);
        preorder(h.right);
    }

    // 后序遍历打印
    public void postorder() {
        postorder(root);
        System.out.println();
    }

    private void postorder(Node h) {
        if (h == null) return;
        postorder(h.left);
        postorder(h.right);
        System.out.print(h.data + " ");
    }

    // 检查树是否为空
    public boolean isEmpty() {
        return root == null;
    }

    // 获取树的高度
    public int height() {
        return height(root);
    }

    private int height(Node x) {
        if (x == null) return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    // 查找小于等于给定值的最大元素
    public T floor(T data) {
        Node x = floor(root, data);
        if (x == null) return null;
        else return x.data;
    }

    private Node floor(Node x, T data) {
        if (x == null) return null;
        int cmp = data.compareTo(x.data);
        if (cmp == 0) return x;
        if (cmp < 0) return floor(x.left, data);
        Node t = floor(x.right, data);
        if (t != null) return t;
        else return x;
    }

    // 查找大于等于给定值的最小元素
    public T ceiling(T data) {
        Node x = ceiling(root, data);
        if (x == null) return null;
        else return x.data;
    }

    private Node ceiling(Node x, T data) {
        if (x == null) return null;
        int cmp = data.compareTo(x.data);
        if (cmp == 0) return x;
        if (cmp > 0) return ceiling(x.right, data);
        Node t = ceiling(x.left, data);
        if (t != null) return t;
        else return x;
    }

    // 返回排名为k的元素（从0开始计数）
    public T select(int k) {
        if (k < 0 || k >= size()) throw new IllegalArgumentException();
        Node x = select(root, k);
        return x.data;
    }

    private Node select(Node x, int k) {
        if (x == null) return null;
        int t = size(x.left);
        if (t > k) return select(x.left, k);
        else if (t < k) return select(x.right, k - t - 1);
        else return x;
    }

    // 返回指定元素的排名（从0开始计数）
    public int rank(T data) {
        return rank(root, data);
    }

    private int rank(Node x, T data) {
        if (x == null) return 0;
        int cmp = data.compareTo(x.data);
        if (cmp < 0) return rank(x.left, data);
        else if (cmp > 0) return 1 + size(x.left) + rank(x.right, data);
        else return size(x.left);
    }

    // 返回范围内的元素数量
    public int size(T lo, T hi) {
        if (lo.compareTo(hi) > 0) return 0;
        if (contains(hi)) return rank(hi) - rank(lo) + 1;
        else return rank(hi) - rank(lo);
    }

    // 迭代器实现
    public Iterator<T> iterator() {
        return new RedBlackTreeIterator();
    }

    private class RedBlackTreeIterator implements Iterator<T> {
        private Stack<Node> stack = new Stack<>();
        
        public RedBlackTreeIterator() {
            pushLeft(root);
        }
        
        private void pushLeft(Node x) {
            while (x != null) {
                stack.push(x);
                x = x.left;
            }
        }
        
        public boolean hasNext() {
            return !stack.isEmpty();
        }
        
        public T next() {
            Node current = stack.pop();
            pushLeft(current.right);
            return current.data;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}