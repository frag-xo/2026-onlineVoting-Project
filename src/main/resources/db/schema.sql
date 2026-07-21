-- 温宿县农商平台数据库表结构

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
  `p_id` VARCHAR(64) NOT NULL COMMENT '商品 ID',
  `p_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
  `p_description` VARCHAR(500) DEFAULT NULL COMMENT '商品描述',
  `p_price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '商品原价',
  `p_image` VARCHAR(255) DEFAULT NULL COMMENT '商品图片 URL',
  `detail_images` TEXT DEFAULT NULL COMMENT '商品详情图片 (多张，逗号分隔)',
  `category` VARCHAR(50) NOT NULL COMMENT '商品分类 (apple/核桃)',
  `origin` VARCHAR(100) DEFAULT NULL COMMENT '商品产地',
  `specification` VARCHAR(50) DEFAULT NULL COMMENT '商品规格',
  `stock` INT DEFAULT 0 COMMENT '库存数量',
  `sales` INT DEFAULT 0 COMMENT '销量',
  `is_recommended` TINYINT DEFAULT 0 COMMENT '是否推荐 (1-推荐，0-不推荐)',
  `sort_order` INT DEFAULT 0 COMMENT '排序权重',
  `status` TINYINT DEFAULT 1 COMMENT '状态 (1-上架，0-下架)',
  PRIMARY KEY (`p_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 轮播广告表
CREATE TABLE IF NOT EXISTS `banner` (
  `banner_id` VARCHAR(64) NOT NULL COMMENT '广告 ID',
  `title` VARCHAR(100) DEFAULT NULL COMMENT '广告标题',
  `image_url` VARCHAR(255) NOT NULL COMMENT '广告图片 URL',
  `link_url` VARCHAR(255) DEFAULT NULL COMMENT '跳转链接',
  `banner_type` TINYINT DEFAULT 1 COMMENT '广告类型 (1-首页轮播，2-分类页广告等)',
  `sort_order` INT DEFAULT 0 COMMENT '排序权重',
  `status` TINYINT DEFAULT 1 COMMENT '状态 (1-启用，0-禁用)',
  PRIMARY KEY (`banner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播广告表';

-- 友情链接表
CREATE TABLE IF NOT EXISTS `friend_link` (
  `link_id` VARCHAR(64) NOT NULL COMMENT '链接 ID',
  `link_name` VARCHAR(100) NOT NULL COMMENT '链接名称',
  `link_url` VARCHAR(255) NOT NULL COMMENT '链接 URL',
  `logo` VARCHAR(255) DEFAULT NULL COMMENT '链接图标',
  `sort_order` INT DEFAULT 0 COMMENT '排序权重',
  `status` TINYINT DEFAULT 1 COMMENT '状态 (1-显示，0-隐藏)',
  PRIMARY KEY (`link_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='友情链接表';

-- 初始化数据 - 商品表
INSERT INTO `product` VALUES 
('1', '阿克苏冰糖心苹果', '新疆阿克苏特产，皮薄肉厚，甜脆可口', 68.00, 88.00, '/images/apple1.jpg', '/images/apple1_detail1.jpg,/images/apple1_detail2.jpg', 'apple', '新疆阿克苏', '5kg 装', 1000, 500, 1, 10, 1),
('2', '温宿富硒苹果', '富含硒元素，营养丰富，口感极佳', 78.00, 98.00, '/images/apple2.jpg', '/images/apple2_detail1.jpg,/images/apple2_detail2.jpg', 'apple', '新疆温宿', '5kg 装', 800, 300, 1, 9, 1),
('3', '有机红富士苹果', '绿色有机认证，无农药残留', 88.00, 108.00, '/images/apple3.jpg', '/images/apple3_detail1.jpg,/images/apple3_detail2.jpg', 'apple', '新疆温宿', '精选大果 5kg', 600, 200, 1, 8, 1),
('4', '新疆纸皮核桃', '薄如纸皮，手捏即开，仁香饱满', 58.00, 78.00, '/images/walnut1.jpg', '/images/walnut1_detail1.jpg,/images/walnut1_detail2.jpg', 'walnut', '新疆温宿', '500g 装', 1500, 800, 1, 10, 1),
('5', '温宿老树核桃', '百年老树核桃，营养价值更高', 68.00, 88.00, '/images/walnut2.jpg', '/images/walnut2_detail1.jpg,/images/walnut2_detail2.jpg', 'walnut', '新疆温宿', '精选 500g', 1200, 600, 1, 9, 1),
('6', '原味烘烤核桃', '传统工艺烘烤，香脆可口', 48.00, 68.00, '/images/walnut3.jpg', '/images/walnut3_detail1.jpg,/images/walnut3_detail2.jpg', 'walnut', '新疆温宿', '熟核桃 500g', 2000, 1000, 1, 8, 1);

-- 初始化数据 - 轮播广告表
INSERT INTO `banner` VALUES 
('1', '阿克苏苹果丰收季', '/images/banner1.jpg', '/product/1', 1, 10, 1),
('2', '温宿核桃促销活动', '/images/banner2.jpg', '/product/4', 1, 9, 1),
('3', '新鲜水果直供', '/images/banner3.jpg', null, 1, 8, 1);

-- 初始化数据 - 友情链接表
INSERT INTO `friend_link` VALUES 
('1', '温宿县政府网', 'http://www.wensu.gov.cn', '/images/logo_gov.png', 10, 1),
('2', '新疆农业厅', 'http://nynct.xinjiang.gov.cn', '/images/logo_agri.png', 9, 1),
('3', '中国农产品网', 'http://www.agri.cn', null, 8, 1),
('4', '电商平台合作', 'http://www.example.com', null, 7, 1);

-- 购物车表
CREATE TABLE IF NOT EXISTS `cart` (
  `cart_id` VARCHAR(64) NOT NULL COMMENT '购物车 ID',
  `user_id` VARCHAR(64) NOT NULL COMMENT '用户 ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`cart_id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 购物车项表
CREATE TABLE IF NOT EXISTS `cart_item` (
  `item_id` VARCHAR(64) NOT NULL COMMENT '购物车项 ID',
  `cart_id` VARCHAR(64) NOT NULL COMMENT '购物车 ID',
  `p_id` VARCHAR(64) NOT NULL COMMENT '商品 ID',
  `p_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
  `p_image` VARCHAR(255) DEFAULT NULL COMMENT '商品图片 URL',
  `p_price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '购买数量',
  `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`item_id`),
  KEY `idx_cart_id` (`cart_id`),
  KEY `idx_product_id` (`p_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车项表';
