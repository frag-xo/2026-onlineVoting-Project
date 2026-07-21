package org.mjc;

import org.mjc.mapper.ProductMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 数据库连接测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DatabaseTest {

    @Autowired
    private ProductMapper productMapper;

    /**
     * 测试数据库连接是否成功
     */
    @Test
    public void testDatabaseConnection() {
        System.out.println("========== 开始测试数据库连接 ==========");
        
        try {
            // 查询所有商品
            List<Product> products = productMapper.selectList(null);
            
            System.out.println("✅ 数据库连接成功!");
            System.out.println("查询到 " + products.size() + " 条商品数据");
            
            for (Product p : products) {
                System.out.println("  - 商品：" + p.getPName() + ", 价格：¥" + p.getPPrice());
            }
            
            // 测试推荐商品查询
            List<Product> recommended = productMapper.selectRecommendedProducts(6);
            System.out.println("\n推荐商品数量：" + recommended.size());
            
            // 测试苹果商品查询
            List<Product> apples = productMapper.selectProductsByCategory("apple", 1);
            System.out.println("苹果商品数量：" + apples.size());
            
            // 测试核桃商品查询
            List<Product> walnuts = productMapper.selectProductsByCategory("walnut", 1);
            System.out.println("核桃商品数量：" + walnuts.size());
            
            System.out.println("\n========== 数据库连接测试通过 ==========");
            
        } catch (Exception e) {
            System.err.println("❌ 数据库连接失败!");
            System.err.println("错误信息：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试查询商品详情
     */
    @Test
    public void testGetProductDetail() {
        System.out.println("========== 测试查询商品详情 ==========");
        
        Product product = productMapper.selectProductDetail("1");
        
        if (product != null) {
            System.out.println("✅ 查询成功!");
            System.out.println("商品 ID: " + product.getPId());
            System.out.println("商品名称：" + product.getPName());
            System.out.println("商品价格：" + product.getPPrice());
            System.out.println("商品分类：" + product.getCategory());
            System.out.println("商品产地：" + product.getOrigin());
        } else {
            System.out.println("❌ 未找到商品，可能数据库中没有初始化数据");
        }
    }
}
