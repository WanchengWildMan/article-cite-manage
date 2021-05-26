/*
 Navicat Premium Data Transfer

 Source Server         : xwc
 Source Server Type    : MySQL
 Source Server Version : 80021
 Source Host           : localhost:3306
 Source Schema         : article_manage

 Target Server Type    : MySQL
 Target Server Version : 80021
 File Encoding         : 65001

 Date: 25/05/2021 19:54:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE article_manage;

-- ----------------------------
-- Table structure for article
-- ----------------------------
CREATE TABLE IF NOT EXISTS `article` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `seq_id` bigint NOT NULL,
  `author` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `article_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `article_type` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `publish_house` varchar(50) DEFAULT NULL,
  `publish_year` char(4) NOT NULL,
  `num` smallint NOT NULL,
  `start_page` smallint NOT NULL DEFAULT '0',
  `end_page` smallint NOT NULL DEFAULT '0',
  `gmt_created` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  PRIMARY KEY (`id`,`article_name`) USING BTREE,
  UNIQUE KEY `unique_article_name` (`article_name`)
) ENGINE=InnoDB AUTO_INCREMENT=435678882 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
