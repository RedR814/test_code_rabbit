package redBulue;

public class RedBlackTreeCompleteTest {
    public static void main(String[] args) {
        RedBlackTree<Integer> rbt = new RedBlackTree<>();
        
        System.out.println("=== 红黑树完整功能测试 ===\n");
        
        // 测试插入
        System.out.println("1. 插入元素: 10, 5, 15, 3, 7, 12, 18, 1, 4, 6, 8, 11, 13, 17, 20");
        int[] values = {10, 5, 15, 3, 7, 12, 18, 1, 4, 6, 8, 11, 13, 17, 20};
        for (int val : values) {
            rbt.insert(val);
        }
        
        // 测试树的基本信息
        System.out.println("树的大小: " + rbt.size());
        System.out.println("树的高度: " + rbt.height());
        System.out.println("树是否为空: " + rbt.isEmpty());
        
        // 测试遍历
        System.out.print("中序遍历: ");
        rbt.inorder();
        
        System.out.print("前序遍历: ");
        rbt.preorder();
        
        System.out.print("后序遍历: ");
        rbt.postorder();
        
        // 测试查找操作
        System.out.println("\n2. 查找操作测试:");
        System.out.println("树中是否包含元素 7: " + rbt.contains(7));
        System.out.println("树中是否包含元素 9: " + rbt.contains(9));
        
        // 测试最值操作
        System.out.println("\n3. 最值操作测试:");
        System.out.println("最小值: " + rbt.min());
        System.out.println("最大值: " + rbt.max());
        
        // 测试floor和ceiling操作
        System.out.println("\n4. Floor和Ceiling操作测试:");
        System.out.println("小于等于6的最大元素: " + rbt.floor(6));
        System.out.println("小于等于9的最大元素: " + rbt.floor(9));
        System.out.println("大于等于6的最小元素: " + rbt.ceiling(6));
        System.out.println("大于等于9的最小元素: " + rbt.ceiling(9));
        
        // 测试选择和排名操作
        System.out.println("\n5. 选择和排名操作测试:");
        System.out.println("排名第0的元素: " + rbt.select(0));
        System.out.println("排名第5的元素: " + rbt.select(5));
        System.out.println("排名第14的元素: " + rbt.select(14));
        System.out.println("元素7的排名: " + rbt.rank(7));
        System.out.println("元素15的排名: " + rbt.rank(15));
        
        // 测试范围查询
        System.out.println("\n6. 范围查询测试:");
        System.out.println("[5, 15]范围内元素的数量: " + rbt.size(5, 15));
        System.out.println("[7, 13]范围内元素的数量: " + rbt.size(7, 13));
        
        // 测试迭代器
        System.out.println("\n7. 迭代器测试:");
        System.out.print("使用迭代器遍历: ");
        for (Integer val : rbt) {
            System.out.print(val + " ");
        }
        System.out.println();
        
        // 测试删除操作
        System.out.println("\n8. 删除操作测试:");
        System.out.println("删除最小值前的最小值: " + rbt.min());
        rbt.deleteMin();
        System.out.println("删除最小值后的最小值: " + rbt.min());
        System.out.println("删除最小值后的大小: " + rbt.size());
        
        System.out.println("删除最大值前的最大值: " + rbt.max());
        rbt.deleteMax();
        System.out.println("删除最大值后的最大值: " + rbt.max());
        System.out.println("删除最大值后的大小: " + rbt.size());
        
        System.out.println("删除元素 10");
        rbt.delete(10);
        System.out.println("删除元素 10 后的大小: " + rbt.size());
        System.out.print("删除后的中序遍历: ");
        rbt.inorder();
        
        // 再次测试树的信息
        System.out.println("\n9. 删除操作后的树信息:");
        System.out.println("树的大小: " + rbt.size());
        System.out.println("树的高度: " + rbt.height());
        System.out.println("最小值: " + rbt.min());
        System.out.println("最大值: " + rbt.max());
    }
}