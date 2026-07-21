package org.mjc.service;

import org.mjc.entity.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 首页服务测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class HomeServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private BannerService bannerService;

    @Autowired
    private FriendLinkService friendLinkService;

    /**
     * 测试获取推荐商品
     */
    @Test
    public void testGetRecommendedProducts() {
        List<Product> products = productService.getRecommendedProducts(6);
        System.out.println("推荐商品数量：" + products.size());
        for (Product p : products) {
            System.out.println("商品名称：" + p.getPName() + ", 价格：" + p.getPPrice());
        }
    }

    /**
     * 测试获取轮播图
     */
    @Test
    public void testGetActiveBanners() {
        List<Banner> banners = bannerService.getActiveBanners(1);
        System.out.println("轮播图数量：" + banners.size());
        for (Banner b : banners) {
            System.out.println("广告标题：" + b.getTitle());
        }
    }

    /**
     * 测试获取友情链接
     */
    @Test
    public void testGetActiveLinks() {
        List<FriendLink> links = friendLinkService.getActiveLinks();
        System.out.println("友情链接数量：" + links.size());
        for (FriendLink l : links) {
            System.out.println("链接名称：" + l.getLinkName());
        }
    }

    /**
     * 测试获取商品详情
     */
    @Test
    public void testGetProductDetail() {
        Product product = productService.getProductDetail("1");
        if (product != null) {
            System.out.println("商品名称：" + product.getPName());
            System.out.println("商品描述：" + product.getPDescription());
            System.out.println("商品价格：" + product.getPPrice());
        } else {
            System.out.println("商品不存在");
        }
    }

    /**
     * 测试获取相关商品
     */
    @Test
    public void testGetRelatedProducts() {
        List<Product> products = productService.getRelatedProducts("apple", "1", 2);
        System.out.println("相关商品数量：" + products.size());
        for (Product p : products) {
            System.out.println("商品名称：" + p.getPName());
        }
    }
}
