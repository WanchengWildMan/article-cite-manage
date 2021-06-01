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

 Date: 25/05/2021 20:21:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
CREATE DATABASE IF NOT EXISTS `article_manage`;
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `seq_id` bigint(20) DEFAULT NULL,
  `author` varchar(80) CHARACTER SET utf8 NOT NULL,
  `article_name` varchar(100) CHARACTER SET utf8 NOT NULL,
  `article_type` varchar(5) CHARACTER SET utf8 NOT NULL,
  `publish_house` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `publish_year` char(4) CHARACTER SET utf8 NOT NULL,
  `num` smallint(6) NOT NULL,
  `start_page` smallint(6) NOT NULL DEFAULT '0',
  `end_page` smallint(6) NOT NULL DEFAULT '0',
  `gmt_created` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  PRIMARY KEY (`id`,`article_name`) USING BTREE,
  UNIQUE KEY `unique_article_name` (`article_name`)
) ENGINE=InnoDB AUTO_INCREMENT=435678882 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



-- ----------------------------
-- Records of article
-- ----------------------------
BEGIN;
INSERT INTO `article` VALUES (435678659, 2, '葛家澍，林志军', '现代西方财务会计理论', 'M', '厦门：厦门大学出版社', '2001', 0, 42, 0, '2021-05-07 08:19:06', '2021-05-07 08:19:06');
INSERT INTO `article` VALUES (435678661, 1, '夏鲁惠', '高等学校毕业论文教学情况调研报告', 'J', '高等理科教育', '2004', 1, 46, 52, '2021-05-21 19:00:13', '2021-05-21 19:00:13');
INSERT INTO `article` VALUES (435678682, 1, '刘国钧，陈绍业，王凤翥', '图书馆目录', 'M', '北京：高等教育出版社', '1957', 0, 15, 18, '2021-05-21 19:52:18', '2021-05-21 19:52:18');
INSERT INTO `article` VALUES (435678687, 2, '辛希孟', '信息技术和信息服务国际研讨会论文集：A集', 'C', '北京：中国社会科学出版社', '1994', 0, 0, 0, '2021-05-21 20:05:13', '2021-05-21 20:05:13');
INSERT INTO `article` VALUES (435678689, 3, '张筑生', '微分半动力系统的不变集', 'D', '北京：北京大学数学系数学研究所', '1983', 0, 0, 0, '2021-05-21 20:05:13', '2021-05-21 20:05:13');
INSERT INTO `article` VALUES (435678690, 4, '冯西桥', '核反应堆压力管道和压力容器的LBB分析', 'R', '北京：清华大学核能技术设计研究院', '1997', 0, 0, 0, '2021-05-21 20:05:13', '2021-05-21 20:05:13');
INSERT INTO `article` VALUES (435678692, 9, '钟文发', '非线性规划在可燃毒物配置中的应用', 'A', '赵玮.运筹学的理论和应用——中国运筹学会第五届大会论文集[C]. 西安：西安电子科技大学出版社', '1996', 0, 468, 471, '2021-05-21 20:05:13', '2021-05-21 20:05:13');
INSERT INTO `article` VALUES (435678693, 16, '汤晓光', '无砟轨道客运专线沉降变形观测评估系统建设管理', 'J', '铁道标准设计', '2010', 1, 3, 6, '2021-05-21 20:05:13', '2021-05-21 20:05:13');
INSERT INTO `article` VALUES (435678699, 7, '金显贺，王昌长，王忠东，等', '一种用于在线检测局部放电的数字滤波技术', 'J', '清华大学学报（自然科学版）', '1993', 0, 33, 0, '2021-05-21 20:08:25', '2021-05-21 20:08:25');
INSERT INTO `article` VALUES (435678701, 11, '谢希德', '创造学习的新思路', 'N', '人民日报', '1998', 0, 12, 25, '2021-05-21 20:08:25', '2021-05-23 07:56:49');
INSERT INTO `article` VALUES (435678702, 14, '姜锡洲', '一种温热外敷药制备方案', 'P', '中国专利：881056073', '1989', 0, 7, 26, '2021-05-21 20:08:25', '2021-05-21 20:08:25');
INSERT INTO `article` VALUES (435678704, 17, '王天亮，刘建坤，彭丽云', '冻融循环作用下水泥改良土的力学性质研究', 'J', '中国铁道科学', '2010', 0, 31, 0, '2021-05-21 20:08:25', '2021-05-21 20:08:25');
INSERT INTO `article` VALUES (435678705, 18, '周丹，田红旗', '强侧风下客车在不同路况运行的气动性能比较', 'J', '中南大学学报： 自然科学版', '2008', 0, 39, 0, '2021-05-21 20:08:25', '2021-05-21 20:08:25');
INSERT INTO `article` VALUES (435678706, 20, '李旭东，宗光华，毕树生，等', '生物工程微操作机器人视觉系统的研究', 'J', '北京航空航天大学学报', '2002', 0, 28, 0, '2021-05-21 20:08:25', '2021-05-21 20:08:25');
INSERT INTO `article` VALUES (435678707, 21, '张佐光，张晓宏，仲伟虹，等', '多相混杂纤维复合材料拉伸行为分析', 'C', '/张为民.第九届全国复合材料学术会议论文集(下册).北京：世界图书出版公司', '1996', 0, 410, 416, '2021-05-21 20:08:25', '2021-05-21 20:08:25');
INSERT INTO `article` VALUES (435678708, 22, '金宏', '导航系统的精度及容错性能的研究', 'D', '北京：北京航空航天大学自动控制系', '1998', 0, 0, 0, '2021-05-21 20:08:25', '2021-05-21 20:08:25');
INSERT INTO `article` VALUES (435678733, 15, '万锦坤', '中国大学学报论文文摘（1983-1993）.英文版', 'DB/CD', '北京：中国大百科全书出版社', '1996', 0, 0, 0, '2021-05-21 20:11:48', '2021-05-21 20:11:48');
INSERT INTO `article` VALUES (435678740, 23, '杨家军', '开放式研究性教学模式的研究与实践', 'EB/OL', '北京:中国教育和科研计算机网', '2009', 0, 2009, 12, '2021-05-21 20:11:48', '2021-05-21 20:11:48');
INSERT INTO `article` VALUES (435678840, 5, 'Gill,R', 'Mastering English Literature', 'M', 'London: Macmillan', '1985', 0, 0, 0, '2021-05-22 22:58:59', '2021-05-22 22:58:59');
INSERT INTO `article` VALUES (435678841, 10, 'In C.Nelson & L. Grossberg(eds.)', 'Victory in Limbo: Imigism ', 'C', 'Urbana: University of Illinois Press', '1988', 0, 271, 313, '2021-05-22 22:59:19', '2021-05-22 22:59:19');
INSERT INTO `article` VALUES (435678842, 12, 'French,W', 'Between Silences: A Voice from China', 'N', 'Atlantic Weekly', '1987', 0, 8, 15, '2021-05-22 22:59:22', '2021-05-22 22:59:22');
INSERT INTO `article` VALUES (435678849, 8, 'Heider,E.R.& D.C.Oliver', 'The structure of color space in naming and memory of two languages ', 'J', 'Foreign Language Teaching and Research', '1999', 0, 62, 67, '2021-05-22 23:09:20', '2021-05-22 23:09:20');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
