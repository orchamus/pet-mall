/*
 Navicat Premium Dump SQL

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : petshop

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 30/06/2026 10:19:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `address_id` int NOT NULL AUTO_INCREMENT COMMENT '??????',
  `user_id` int NOT NULL COMMENT '????ID',
  `consignee` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '?????',
  `email` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '???????',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `detail` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '????',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区',
  `postal_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮政编码',
  `is_default` smallint NOT NULL DEFAULT 0 COMMENT '??????(1?,0?)',
  `tag` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '????(?????)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '??????',
  `region` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '?????',
  PRIMARY KEY (`address_id`) USING BTREE,
  INDEX `idx_address_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '???????' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES (4, 3, '??', 'user1@example.com', NULL, '??????', NULL, NULL, NULL, NULL, 1, '??', '2025-06-08 06:55:16', '2025-06-08 06:55:16', '?????????');
INSERT INTO `address` VALUES (7, 3, '???', 'user1@example.com', NULL, '????', NULL, NULL, NULL, NULL, 0, '?', '2025-06-18 16:22:24', '2025-06-18 16:22:24', '?????????');
INSERT INTO `address` VALUES (8, 3, '??', 'user2@example.com', NULL, '????????', NULL, NULL, NULL, NULL, 0, '??', '2025-06-23 23:05:21', '2025-06-23 23:05:21', '?????????');
INSERT INTO `address` VALUES (10, 19, '123', '13800000000', '13800000000', '11', '天津市', '市辖区', '和平区', '136212', 0, '', '2026-06-29 18:39:05', '2026-06-29 18:39:05', '天津市市辖区和平区');

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `cart_id` int NOT NULL AUTO_INCREMENT COMMENT '????????',
  `user_id` int NOT NULL COMMENT '????ID',
  `product_id` int NOT NULL COMMENT '????ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '??????',
  PRIMARY KEY (`cart_id`) USING BTREE,
  UNIQUE INDEX `uk_user_product`(`user_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '??????' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (33, 3, 10, '2026-06-29 16:07:00', '2026-06-29 16:07:00');
INSERT INTO `cart` VALUES (36, 3, 12, '2026-06-29 17:40:06', '2026-06-29 17:40:06');

-- ----------------------------
-- Table structure for media
-- ----------------------------
DROP TABLE IF EXISTS `media`;
CREATE TABLE `media`  (
  `media_id` int NOT NULL AUTO_INCREMENT COMMENT '????ID',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '??????/URL',
  `upload_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`media_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '???????' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of media
-- ----------------------------
INSERT INTO `media` VALUES (33, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.jpg', '2025-06-16 20:50:39');
INSERT INTO `media` VALUES (34, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.mp4', '2025-06-16 20:50:39');
INSERT INTO `media` VALUES (35, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.jpg', '2025-06-17 21:42:38');
INSERT INTO `media` VALUES (36, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.mp4', '2025-06-17 21:42:38');
INSERT INTO `media` VALUES (37, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.jpg', '2025-06-20 15:50:44');
INSERT INTO `media` VALUES (38, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.jpg', '2025-06-20 15:52:01');
INSERT INTO `media` VALUES (39, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 08:38:02');
INSERT INTO `media` VALUES (40, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 08:38:02');
INSERT INTO `media` VALUES (42, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 08:42:44');
INSERT INTO `media` VALUES (43, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 08:42:44');
INSERT INTO `media` VALUES (44, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.mp4', '2025-06-26 08:42:44');
INSERT INTO `media` VALUES (45, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.mp4', '2025-06-26 08:43:03');
INSERT INTO `media` VALUES (46, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 08:48:26');
INSERT INTO `media` VALUES (47, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 08:48:26');
INSERT INTO `media` VALUES (48, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.mp4', '2025-06-26 08:48:26');
INSERT INTO `media` VALUES (49, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 08:53:01');
INSERT INTO `media` VALUES (50, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 08:53:01');
INSERT INTO `media` VALUES (51, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.mp4', '2025-06-26 08:53:01');
INSERT INTO `media` VALUES (52, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 08:57:40');
INSERT INTO `media` VALUES (53, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 08:57:40');
INSERT INTO `media` VALUES (54, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.mp4', '2025-06-26 08:57:40');
INSERT INTO `media` VALUES (55, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 09:02:07');
INSERT INTO `media` VALUES (56, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 09:02:07');
INSERT INTO `media` VALUES (57, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.mp4', '2025-06-26 09:02:07');
INSERT INTO `media` VALUES (58, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 09:02:23');
INSERT INTO `media` VALUES (59, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 09:02:23');
INSERT INTO `media` VALUES (60, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.mp4', '2025-06-26 09:02:23');
INSERT INTO `media` VALUES (61, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 09:06:25');
INSERT INTO `media` VALUES (62, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 09:06:25');
INSERT INTO `media` VALUES (63, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.mp4', '2025-06-26 09:06:25');
INSERT INTO `media` VALUES (64, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 09:35:30');
INSERT INTO `media` VALUES (65, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 09:35:30');
INSERT INTO `media` VALUES (66, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.mp4', '2025-06-26 09:35:30');
INSERT INTO `media` VALUES (67, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 09:53:20');
INSERT INTO `media` VALUES (68, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.webp', '2025-06-26 09:53:20');
INSERT INTO `media` VALUES (69, '/media/productMedia/e10adc3949ba59abbe56e057f20f883e.mp4', '2025-06-26 09:53:20');

-- ----------------------------
-- Table structure for order_refunds
-- ----------------------------
DROP TABLE IF EXISTS `order_refunds`;
CREATE TABLE `order_refunds`  (
  `refund_id` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '??ID(UUID)',
  `order_id` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '?????ID',
  `user_id` int NOT NULL COMMENT '???????ID',
  `refund_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '????',
  `refund_status` tinyint NOT NULL COMMENT '????(1-???,2-????,3-???)',
  `apply_time` datetime NOT NULL COMMENT '????',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`refund_id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_apply_time`(`apply_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '?????' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_refunds
-- ----------------------------
INSERT INTO `order_refunds` VALUES ('e10adc3949ba59abbe56e057f20f883e', 'e10adc3949ba59abbe56e057f20f883e', 3, '????', 2, '2025-06-19 06:45:38', '2025-06-19 14:45:37', '2025-06-19 14:45:37');
INSERT INTO `order_refunds` VALUES ('e10adc3949ba59abbe56e057f20f883e', 'e10adc3949ba59abbe56e057f20f883e', 3, 'sd', 3, '2025-06-22 17:52:07', '2025-06-21 01:16:27', '2025-06-21 01:16:27');
INSERT INTO `order_refunds` VALUES ('e10adc3949ba59abbe56e057f20f883e', 'e10adc3949ba59abbe56e057f20f883e', 3, '???', 2, '2025-06-23 01:43:16', '2025-06-23 09:43:15', '2025-06-23 09:43:15');
INSERT INTO `order_refunds` VALUES ('e10adc3949ba59abbe56e057f20f883e', 'e10adc3949ba59abbe56e057f20f883e', 19, '1', 2, '2026-06-29 18:47:14', '2026-06-29 18:47:14', '2026-06-29 18:47:14');
INSERT INTO `order_refunds` VALUES ('e10adc3949ba59abbe56e057f20f883e', 'e10adc3949ba59abbe56e057f20f883e', 3, '????', 1, '2025-07-03 09:41:02', '2025-07-03 09:41:02', '2025-07-03 09:41:02');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `order_id` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '???(????:??+???)',
  `shop_id` int NOT NULL COMMENT '??ID',
  `user_id` int NOT NULL COMMENT '????ID',
  `product_id` int NOT NULL COMMENT '??id',
  `quantity` int NOT NULL COMMENT '????',
  `address_id` int NOT NULL COMMENT '????ID',
  `price` double NOT NULL COMMENT '????',
  `status` int NOT NULL DEFAULT 0 COMMENT '????(1-???,2-???,3-???,4-???,5-???,6-???)',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '????',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '????',
  `receive_time` datetime NULL DEFAULT NULL COMMENT '??????',
  `close_time` datetime NULL DEFAULT NULL COMMENT '??????',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '??????',
  `pre_refund_status` int NULL DEFAULT NULL COMMENT '????????',
  PRIMARY KEY (`order_id`) USING BTREE,
  INDEX `address_id`(`address_id` ASC) USING BTREE,
  INDEX `idx_orders_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`address_id`) REFERENCES `address` (`address_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '????' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES ('e10adc3949ba59abbe56e057f20f883e', 3, 3, 12, 1, 4, 2800, 7, '2026-06-29 17:39:51', '2026-06-30 10:01:02', '2026-06-30 10:01:30', NULL, '2026-06-29 17:39:51', '2026-06-29 17:39:51', NULL);
INSERT INTO `orders` VALUES ('e10adc3949ba59abbe56e057f20f883e', 3, 3, 12, 1, 4, 2800, 5, '2025-06-30 07:00:21', NULL, NULL, NULL, '2025-06-30 15:00:21', '2025-06-30 15:00:21', 1);
INSERT INTO `orders` VALUES ('e10adc3949ba59abbe56e057f20f883e', 3, 3, 13, 1, 4, 3500, 2, '2025-07-01 01:24:32', '2026-06-30 10:01:04', NULL, NULL, '2025-07-01 09:24:31', '2025-07-01 09:24:31', NULL);
INSERT INTO `orders` VALUES ('e10adc3949ba59abbe56e057f20f883e', 3, 3, 12, 1, 4, 2800, 2, '2025-07-01 01:13:30', '2026-06-30 10:01:07', NULL, NULL, '2025-07-01 09:13:30', '2025-07-01 09:13:30', NULL);
INSERT INTO `orders` VALUES ('e10adc3949ba59abbe56e057f20f883e', 2, 3, 11, 1, 8, 20, 2, '2025-06-24 07:08:09', '2026-06-30 09:59:58', NULL, NULL, '2025-06-24 15:08:08', '2025-06-24 15:08:08', NULL);
INSERT INTO `orders` VALUES ('e10adc3949ba59abbe56e057f20f883e', 1, 3, 10, 1, 4, 1600, 1, '2026-06-23 11:46:29', NULL, NULL, NULL, '2026-06-23 11:46:29', '2026-06-23 11:46:29', NULL);
INSERT INTO `orders` VALUES ('e10adc3949ba59abbe56e057f20f883e', 2, 3, 11, 2, 4, 20, 6, '2025-06-17 16:00:36', '2025-06-18 14:15:23', '2025-06-18 22:18:00', '2025-07-03 09:43:05', '2025-06-18 00:00:36', '2025-06-18 00:00:36', 3);
INSERT INTO `orders` VALUES ('e10adc3949ba59abbe56e057f20f883e', 3, 19, 14, 1, 10, 199, 4, '2026-06-29 18:39:21', NULL, NULL, '2026-06-29 18:39:47', '2026-06-29 18:39:21', '2026-06-29 18:39:21', NULL);
INSERT INTO `orders` VALUES ('e10adc3949ba59abbe56e057f20f883e', 1, 3, 10, 1, 4, 1600, 3, '2026-06-29 16:07:19', '2026-06-29 16:12:11', '2026-06-29 16:12:33', NULL, '2026-06-29 16:07:18', '2026-06-29 16:07:18', NULL);
INSERT INTO `orders` VALUES ('e10adc3949ba59abbe56e057f20f883e', 2, 3, 11, 3, 4, 20, 6, '2025-06-17 16:00:34', NULL, NULL, '2025-06-19 15:37:09', '2025-06-18 00:00:33', '2025-06-18 00:00:33', 1);
INSERT INTO `orders` VALUES ('e10adc3949ba59abbe56e057f20f883e', 3, 3, 12, 1, 4, 2800, 2, '2025-07-03 09:40:42', '2026-06-30 10:01:11', NULL, NULL, '2025-07-03 09:40:42', '2025-07-03 09:40:42', NULL);
INSERT INTO `orders` VALUES ('e10adc3949ba59abbe56e057f20f883e', 2, 3, 11, 1, 4, 20, 2, '2025-06-15 00:00:39', '2026-06-30 10:00:01', '2025-06-20 01:16:12', '2025-06-22 09:50:16', '2025-06-15 08:00:38', '2025-06-15 08:00:38', 1);
INSERT INTO `orders` VALUES ('e10adc3949ba59abbe56e057f20f883e', 1, 3, 10, 1, 4, 1600, 2, '2025-06-24 06:51:33', '2026-06-29 16:12:15', NULL, NULL, '2025-06-24 14:51:33', '2025-06-24 14:51:33', NULL);
INSERT INTO `orders` VALUES ('e10adc3949ba59abbe56e057f20f883e', 2, 3, 11, 4, 4, 20, 4, '2025-06-18 16:00:30', NULL, NULL, '2025-06-20 02:40:58', '2025-06-19 00:00:29', '2025-06-19 00:00:29', NULL);
INSERT INTO `orders` VALUES ('e10adc3949ba59abbe56e057f20f883e', 1, 19, 10, 1, 10, 1600, 6, '2026-06-29 18:40:00', NULL, NULL, '2026-06-29 18:47:29', '2026-06-29 18:39:59', '2026-06-29 18:39:59', 1);
INSERT INTO `orders` VALUES ('test-order-review-001', 1, 3, 10, 1, 4, 999, 3, '2026-06-30 09:25:51', NULL, '2026-06-30 09:25:51', NULL, '2026-06-30 09:25:51', '2026-06-30 09:25:51', NULL);
INSERT INTO `orders` VALUES ('test-order-review-002', 1, 3, 10, 1, 4, 999, 3, '2026-06-30 09:33:05', NULL, '2026-06-30 09:33:05', NULL, '2026-06-30 09:33:05', '2026-06-30 09:33:05', NULL);
INSERT INTO `orders` VALUES ('test-order-review-003', 1, 3, 10, 1, 4, 999, 3, '2026-06-30 09:35:52', NULL, '2026-06-30 09:35:52', NULL, '2026-06-30 09:35:52', '2026-06-30 09:35:52', NULL);
INSERT INTO `orders` VALUES ('test-order-status-7-001', 1, 3, 10, 1, 4, 999, 3, NULL, NULL, '2026-06-30 09:52:16', NULL, '2026-06-30 09:52:16', '2026-06-30 09:52:16', NULL);
INSERT INTO `orders` VALUES ('test-order-status-flow-001', 1, 3, 10, 1, 4, 999, 3, NULL, NULL, '2026-06-30 09:50:43', NULL, '2026-06-30 09:50:24', '2026-06-30 09:50:24', NULL);

-- ----------------------------
-- Table structure for pet_video
-- ----------------------------
DROP TABLE IF EXISTS `pet_video`;
CREATE TABLE `pet_video`  (
  `video_id` int NOT NULL AUTO_INCREMENT COMMENT '视频ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频标题',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '视频描述',
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '封面图URL',
  `video_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频URL',
  `product_id` int NULL DEFAULT NULL COMMENT '关联商品ID',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`video_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '萌宠视频表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pet_video
-- ----------------------------
INSERT INTO `pet_video` VALUES (1, '金毛幼犬日常', '看看这只活泼可爱的金毛幼犬，温顺亲人，适合家庭饲养。', 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=400', 'https://videos.pexels.com/video-files/855029/855029-hd_1920_1080_30fps.mp4', 10, 1, 1, '2026-06-29 15:46:02', '2026-06-29 15:46:02');
INSERT INTO `pet_video` VALUES (2, '布偶猫咪养成记', '温顺可爱的布偶猫，毛色漂亮，性格黏人。', 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=400', 'https://videos.pexels.com/video-files/2575756/2575756-hd_1920_1080_24fps.mp4', 12, 2, 1, '2026-06-29 15:46:02', '2026-06-29 15:46:02');
INSERT INTO `pet_video` VALUES (3, '萌宠小仓鼠', '圆滚滚的小仓鼠，治愈系萌宠，适合学生党。', 'https://images.unsplash.com/photo-1425082661705-1834bfd09dca?w=400', 'https://videos.pexels.com/video-files/2795746/2795746-hd_1080_25fps.mp4', 14, 3, 1, '2026-06-29 15:46:02', '2026-06-29 15:46:02');

-- ----------------------------
-- Table structure for pets
-- ----------------------------
DROP TABLE IF EXISTS `pets`;
CREATE TABLE `pets`  (
  `pet_id` int NOT NULL AUTO_INCREMENT COMMENT '????ID',
  `product_id` int NOT NULL COMMENT '????ID',
  `species` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '??(??/?/?)',
  `breed` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '??(???/???)',
  `birth_date` date NULL DEFAULT NULL COMMENT '????',
  `gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '??',
  `health_status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '??????',
  PRIMARY KEY (`pet_id`) USING BTREE,
  UNIQUE INDEX `product_id`(`product_id` ASC) USING BTREE,
  CONSTRAINT `pets_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '??????????' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pets
-- ----------------------------
INSERT INTO `pets` VALUES (5, 10, '?', '????', '2025-04-11', '2', '????????');
INSERT INTO `pets` VALUES (6, 12, '?', '??', '2025-06-01', '2', '??');
INSERT INTO `pets` VALUES (7, 13, '?', '???', '2025-05-11', '2', '??');
INSERT INTO `pets` VALUES (8, 15, '?', '???', '2025-06-04', '2', '??');
INSERT INTO `pets` VALUES (9, 18, '?', '???', '2025-05-30', '2', '??');
INSERT INTO `pets` VALUES (10, 19, '?', '??', '2025-06-02', '2', '??');

-- ----------------------------
-- Table structure for product_media
-- ----------------------------
DROP TABLE IF EXISTS `product_media`;
CREATE TABLE `product_media`  (
  `product_id` int NOT NULL COMMENT '????ID',
  `media_id` int NOT NULL COMMENT '????ID',
  `sort_order` int NULL DEFAULT 0 COMMENT '????(???????)',
  `media_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '0??????1??????????',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '????',
  PRIMARY KEY (`product_id`, `media_id`) USING BTREE,
  INDEX `media_id`(`media_id` ASC) USING BTREE,
  CONSTRAINT `product_media_ibfk_1` FOREIGN KEY (`media_id`) REFERENCES `media` (`media_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `product_media_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '????????????' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_media
-- ----------------------------
INSERT INTO `product_media` VALUES (10, 33, 1, '0', '??.jpg');
INSERT INTO `product_media` VALUES (10, 34, 2, '1', '????.mp4');
INSERT INTO `product_media` VALUES (10, 38, 3, '1', 'R-C.jpg');
INSERT INTO `product_media` VALUES (11, 35, 1, '0', '??.jpg');
INSERT INTO `product_media` VALUES (11, 36, 2, '1', '????.mp4');
INSERT INTO `product_media` VALUES (11, 37, 3, '1', '??.jpg');
INSERT INTO `product_media` VALUES (12, 39, 1, '1', '??.webp');
INSERT INTO `product_media` VALUES (12, 40, 2, '0', 'OIP-C.webp');
INSERT INTO `product_media` VALUES (12, 45, 3, '1', '????????.mp4');
INSERT INTO `product_media` VALUES (13, 42, 1, '0', 'OIP-C (1).webp');
INSERT INTO `product_media` VALUES (13, 43, 2, '1', 'OIP-C (2).webp');
INSERT INTO `product_media` VALUES (13, 44, 3, '1', '???????? (1).mp4');
INSERT INTO `product_media` VALUES (14, 46, 1, '0', 'OIP-C (4).webp');
INSERT INTO `product_media` VALUES (14, 47, 2, '1', 'OIP-C (3).webp');
INSERT INTO `product_media` VALUES (14, 48, 3, '1', '???????? (2).mp4');
INSERT INTO `product_media` VALUES (15, 49, 1, '0', 'OIP-C (5).webp');
INSERT INTO `product_media` VALUES (15, 50, 2, '1', '?? (1).webp');
INSERT INTO `product_media` VALUES (15, 51, 3, '1', '???????? (3).mp4');
INSERT INTO `product_media` VALUES (16, 52, 1, '0', 'OIP-C (6).webp');
INSERT INTO `product_media` VALUES (16, 53, 2, '1', '?? (2).webp');
INSERT INTO `product_media` VALUES (16, 54, 3, '1', '???????? (4).mp4');
INSERT INTO `product_media` VALUES (17, 55, 1, '0', '?? (4).webp');
INSERT INTO `product_media` VALUES (17, 56, 2, '1', '?? (3).webp');
INSERT INTO `product_media` VALUES (17, 57, 3, '1', '???????? (5).mp4');
INSERT INTO `product_media` VALUES (18, 58, 1, '0', 'OIP-C (8).webp');
INSERT INTO `product_media` VALUES (18, 59, 2, '1', 'OIP-C (7).webp');
INSERT INTO `product_media` VALUES (18, 60, 3, '1', '???????? (6).mp4');
INSERT INTO `product_media` VALUES (19, 61, 1, '0', 'OIP-C (9).webp');
INSERT INTO `product_media` VALUES (19, 62, 2, '1', 'OIP-C (10).webp');
INSERT INTO `product_media` VALUES (19, 63, 3, '1', '???????? (7).mp4');
INSERT INTO `product_media` VALUES (20, 64, 1, '0', 'OIP-C (11).webp');
INSERT INTO `product_media` VALUES (20, 65, 2, '1', 'OIP-C (12).webp');
INSERT INTO `product_media` VALUES (20, 66, 3, '1', '???????? (8).mp4');
INSERT INTO `product_media` VALUES (21, 67, 1, '0', 'OIP-C (15).webp');
INSERT INTO `product_media` VALUES (21, 68, 2, '1', 'OIP-C (16).webp');
INSERT INTO `product_media` VALUES (21, 69, 3, '1', 'e10adc3949ba59abbe56e057f20f883e.mp4');

-- ----------------------------
-- Table structure for product_reviews
-- ----------------------------
DROP TABLE IF EXISTS `product_reviews`;
CREATE TABLE `product_reviews`  (
  `review_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '??ID(UUID)',
  `product_id` int NOT NULL COMMENT '????ID',
  `order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联订单ID',
  `user_id` int NOT NULL COMMENT '????ID',
  `rating` int NULL DEFAULT NULL COMMENT '评分(1-5星)，暂不使用',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '????',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '??(0-???,1-???,2-???)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`review_id`) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  CONSTRAINT `fk_review_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chk_rating` CHECK (`rating` between 1 and 5)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '?????' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_reviews
-- ----------------------------
INSERT INTO `product_reviews` VALUES ('e10adc3949ba59abbe56e057f20f883e', 10, 'test-order-status-flow-001', 3, NULL, 'Status flow test review works perfectly and order becomes completed', 1, '2026-06-30 09:51:11', '2026-06-30 09:51:11');
INSERT INTO `product_reviews` VALUES ('e10adc3949ba59abbe56e057f20f883e', 10, 'test-order-review-001', 3, NULL, 'This is a great product, I love it very much and recommend to everyone!', 1, '2026-06-30 09:29:40', '2026-06-30 09:29:40');
INSERT INTO `product_reviews` VALUES ('e10adc3949ba59abbe56e057f20f883e', 10, 'e10adc3949ba59abbe56e057f20f883e', 3, NULL, 'shortThis product is excellent, my pet loves it very much and the quality is great. Highly recommend!', 1, '2026-06-30 09:34:27', '2026-06-30 09:34:27');
INSERT INTO `product_reviews` VALUES ('e10adc3949ba59abbe56e057f20f883e', 10, 'test-order-status-7-001', 3, NULL, 'Great product from web, the status flow works perfectly and smoothly!', 1, '2026-06-30 09:52:58', '2026-06-30 09:52:58');
INSERT INTO `product_reviews` VALUES ('e10adc3949ba59abbe56e057f20f883e', 11, NULL, 3, 5, '???', 1, '2025-06-20 11:24:57', '2025-06-20 11:24:57');
INSERT INTO `product_reviews` VALUES ('e10adc3949ba59abbe56e057f20f883e', 11, NULL, 3, 4, '??', 1, '2025-06-20 11:40:40', '2025-06-20 11:40:40');
INSERT INTO `product_reviews` VALUES ('e10adc3949ba59abbe56e057f20f883e', 10, 'test-order-review-003', 3, NULL, 'Mobile review test from UniApp H5 works great', 1, '2026-06-30 09:36:01', '2026-06-30 09:36:01');
INSERT INTO `product_reviews` VALUES ('e10adc3949ba59abbe56e057f20f883e', 10, 'test-order-review-002', 3, NULL, '1231231313213123213123123123', 1, '2026-06-30 09:44:18', '2026-06-30 09:44:18');

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `product_id` int NOT NULL AUTO_INCREMENT COMMENT '???????',
  `shop_id` int NULL DEFAULT NULL COMMENT '????ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '????',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '????',
  `price` double(10, 2) NOT NULL COMMENT '????',
  `stock_quantity` int NOT NULL DEFAULT 0 COMMENT '????',
  `category` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '??????????0?/?????1?',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '??????',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '??????0????1???',
  PRIMARY KEY (`product_id`) USING BTREE,
  INDEX `idx_products_shop`(`shop_id` ASC) USING BTREE,
  CONSTRAINT `fk_products_shop` FOREIGN KEY (`shop_id`) REFERENCES `shop` (`shop_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `chk_price` CHECK (`price` >= 0),
  CONSTRAINT `chk_stock` CHECK (`stock_quantity` >= 0)
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '???????' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (10, 1, '金毛幼犬,温顺可爱', '健康活泼的金毛幼犬,适合家庭饲养', 1600.00, 3, '0', '2025-06-14 20:50:39', '2025-06-14 20:50:39', '0');
INSERT INTO `products` VALUES (11, 2, '进口猫粮,营养均衡', '精选进口猫粮,富含蛋白质和多种维生素', 20.00, 1961, '1', '2025-06-09 13:42:38', '2025-06-09 13:42:38', '1');
INSERT INTO `products` VALUES (12, 3, '布偶猫咪', '温顺可爱的布偶猫,毛色漂亮', 2800.00, 16, '0', '2025-06-25 08:38:02', '2025-06-25 08:38:02', '0');
INSERT INTO `products` VALUES (13, 3, '柯基犬', '短腿柯基,活泼好动', 3500.00, 9, '0', '2025-06-26 00:42:44', '2025-06-26 00:42:44', '0');
INSERT INTO `products` VALUES (14, 3, '智能喂食器', 'APP远程控制,定时定量喂食', 199.00, 31, '1', '2025-06-26 08:48:26', '2025-06-26 08:48:26', '0');
INSERT INTO `products` VALUES (15, 2, '泰迪幼犬,活泼聪明', '血统纯正泰迪幼犬,性格温顺亲人', 120.00, 30, '0', '2025-06-26 08:53:01', '2025-06-26 08:53:01', '0');
INSERT INTO `products` VALUES (16, 3, '宠物自动饮水机', '循环过滤活水,让爱宠随时喝到干净水', 190.00, 30, '1', '2025-06-26 08:57:40', '2025-06-26 08:57:40', '0');
INSERT INTO `products` VALUES (17, 3, '猫爬架豪华版', '多层设计,剑麻磨爪,猫咪乐园', 298.00, 30, '1', '2025-06-26 09:02:07', '2025-06-26 09:02:07', '0');
INSERT INTO `products` VALUES (18, 2, '英短蓝猫,圆润可爱', '包子脸英短蓝猫,疫苗齐全健康保证', 3000.00, 20, '0', '2025-06-26 09:02:23', '2025-06-26 09:02:23', '0');
INSERT INTO `products` VALUES (19, 2, '柴犬幼犬,忠诚陪伴', '日本柴犬幼犬,性格独立又亲人', 900.00, 10, '0', '2025-06-26 09:06:25', '2025-06-26 09:06:25', '0');
INSERT INTO `products` VALUES (20, 2, '宠物背包外出包', '透气舒适,便携出行,适合猫狗', 210.00, 20, '1', '2025-06-26 09:35:30', '2025-06-26 09:35:30', '0');
INSERT INTO `products` VALUES (21, 3, '狗狗磨牙棒零食', '天然牛皮磨牙棒,清洁牙齿', 100.00, 20, '1', '2025-06-26 09:53:20', '2025-06-26 09:53:20', '0');

-- ----------------------------
-- Table structure for shop
-- ----------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop`  (
  `shop_id` int NOT NULL AUTO_INCREMENT COMMENT '??????',
  `user_id` int NOT NULL COMMENT '??????ID',
  `shop_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '????',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '????',
  `logo` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '??logo URL',
  `contact_email` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '????',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '?????',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '??(0-??,1-???,2-????)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`shop_id`) USING BTREE,
  INDEX `idx_shop_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_shop_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '?????' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of shop
-- ----------------------------
INSERT INTO `shop` VALUES (1, 8, '萌宠乐园总店', '???????????', '/media/shopLogo/Business_shopLogo.jpg', 'user2@example.com', '福建省厦门市思明区中山路1号', 1, '2025-06-11 23:45:43', '2026-06-29 15:01:20');
INSERT INTO `shop` VALUES (2, 15, '萌宠乐园集美店', '???????????', '/media/shopLogo/Bussiness1_shopLogo.jpg', 'user1@example.com', '福建省厦门市集美区杏林湾路2号', 1, '2025-06-17 21:36:49', '2026-06-29 15:01:42');
INSERT INTO `shop` VALUES (3, 17, '萌宠乐园海沧店', '??????????', '/media/shopLogo/Shops_shopLogo.png', 'user3@example.com', '福建省厦门市海沧区海沧大道3号', 1, '2025-06-25 09:18:58', '2026-06-29 15:01:42');

-- ----------------------------
-- Table structure for supplies
-- ----------------------------
DROP TABLE IF EXISTS `supplies`;
CREATE TABLE `supplies`  (
  `supply_id` int NOT NULL AUTO_INCREMENT COMMENT '??????ID',
  `product_id` int NOT NULL COMMENT '????ID',
  `supply_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '????(??/??/??)',
  `material` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '????',
  `size` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '??(S/M/L?????)',
  `weight_kg` decimal(5, 2) NULL DEFAULT NULL COMMENT '??(??)',
  `target_species` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '??????(?/?/???)',
  PRIMARY KEY (`supply_id`) USING BTREE,
  UNIQUE INDEX `product_id`(`product_id` ASC) USING BTREE,
  CONSTRAINT `supplies_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '?????????' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of supplies
-- ----------------------------
INSERT INTO `supplies` VALUES (1, 11, '??', '??', 'S', 0.50, '?');
INSERT INTO `supplies` VALUES (2, 14, '???', '??', 'M', 5.00, '??');
INSERT INTO `supplies` VALUES (3, 16, '??', '??', 'S', 7.00, '?');
INSERT INTO `supplies` VALUES (4, 17, '???', '??', 'S', 6.00, '??');
INSERT INTO `supplies` VALUES (5, 20, '??', '?', 'M', 2.00, '??');
INSERT INTO `supplies` VALUES (6, 21, '??', '?', 'M', 3.00, '?');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '??ID',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '???',
  `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '??',
  `user_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '?????0??????1??????2?????',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '???0???1???',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '??',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '????',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '????',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '?????0?? 1???',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '?????0???? 2?????',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `id`(`id` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE COMMENT '????????????',
  UNIQUE INDEX `user_name`(`user_name` ASC) USING BTREE COMMENT '?????????'
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '???' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (3, 'customer', 'customer', '0', '1', 'e10adc3949ba59abbe56e057f20f883e', 'user4@example.com', '/media/userAvatar/customer.jpg?1751333719220', '0', '0');
INSERT INTO `user` VALUES (8, 'Business', 'Business', '1', '1', 'e10adc3949ba59abbe56e057f20f883e', 'user2@example.com', '/media/userAvatar/Business.jpg', '0', '0');
INSERT INTO `user` VALUES (15, 'Bussiness1', 'Business1', '1', '0', 'e10adc3949ba59abbe56e057f20f883e', 'user1@example.com', '/media/userAvatar/Bussiness1.jpg', '0', '0');
INSERT INTO `user` VALUES (16, 'admin', 'admin', '2', '0', 'e10adc3949ba59abbe56e057f20f883e', 'user5@example.com', '/media/userAvatar/default.jpg', '0', '0');
INSERT INTO `user` VALUES (17, 'Shops', 'Shops', '1', '0', 'e10adc3949ba59abbe56e057f20f883e', 'user3@example.com', '/media/userAvatar/default.jpg?1750735758477?1750735767207', '0', '0');
INSERT INTO `user` VALUES (18, 'testuser101', 'test', '0', '0', 'e10adc3949ba59abbe56e057f20f883e', 'user7@example.com', '/media/userAvatar/default.jpg', '0', '0');
INSERT INTO `user` VALUES (19, 'test', '123', '0', '0', 'e10adc3949ba59abbe56e057f20f883e', 'user1@example.com', '/media/userAvatar/default.jpg', '0', '0');

SET FOREIGN_KEY_CHECKS = 1;
