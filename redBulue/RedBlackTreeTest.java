package redBulue;
public class RedBlackTreeTest {
    public static void main(String[] args) {
        RedBlackTree<Integer> rbt = new RedBlackTree<>();
        
        // 测试插入
        System.out.println("插入元素: 10, 5, 15, 3, 7, 12, 18");
        rbt.insert(10);
        rbt.insert(5);
        rbt.insert(15);
        rbt.insert(3);
        rbt.insert(7);
        rbt.insert(12);
        rbt.insert(18);
        
        // 测试查找
        System.out.println("树中是否包含元素 7: " + rbt.contains(7));
        System.out.println("树中是否包含元素 9: " + rbt.contains(9));
        
        // 测试中序遍历
        System.out.print("中序遍历结果: ");
        rbt.inorder();
        
        // 测试删除
        System.out.println("删除元素 5");
        rbt.delete(5);
        System.out.print("删除后的中序遍历: ");
        rbt.inorder();
        
        // 测试树的大小
        System.out.println("树的大小: " + rbt.size());
        
        // 测试查找最小值
        System.out.println("最小值: " + rbt.min());
    }
}