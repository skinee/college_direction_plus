/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 80042
Source Host           : localhost:3306
Source Database       : campus_navigation

Target Server Type    : MYSQL
Target Server Version : 80042
File Encoding         : 65001

Date: 2025-06-15 20:31:53
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for locations
-- ----------------------------
DROP TABLE IF EXISTS `locations`;
CREATE TABLE `locations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of locations
-- ----------------------------
INSERT INTO `locations` VALUES ('1', '北门', '校园北门入口');
INSERT INTO `locations` VALUES ('2', '西北门', '校园西北门入口');
INSERT INTO `locations` VALUES ('3', '河西食堂', '河西区域的食堂');
INSERT INTO `locations` VALUES ('4', '游泳池', '校园内的游泳池');
INSERT INTO `locations` VALUES ('5', '河东食堂', '河东区域的食堂');
INSERT INTO `locations` VALUES ('6', '理科大楼', '理科教学楼');
INSERT INTO `locations` VALUES ('7', '毛主席像', '校园内的毛主席雕像');
INSERT INTO `locations` VALUES ('8', '图书馆', '校园图书馆');
INSERT INTO `locations` VALUES ('9', '篮球场', '校园内的篮球场');
INSERT INTO `locations` VALUES ('10', '文史楼', '文科历史教学楼');
INSERT INTO `locations` VALUES ('11', '东门', '校园东门入口');
INSERT INTO `locations` VALUES ('12', '体育馆', '校园体育馆');
INSERT INTO `locations` VALUES ('13', '文科大楼', '文科教学楼');
INSERT INTO `locations` VALUES ('14', '文附楼', '文科附属楼');

-- ----------------------------
-- Table structure for paths
-- ----------------------------
DROP TABLE IF EXISTS `paths`;
CREATE TABLE `paths` (
  `id` int NOT NULL AUTO_INCREMENT,
  `start_location_id` int DEFAULT NULL,
  `end_location_id` int DEFAULT NULL,
  `distance` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `start_location_id` (`start_location_id`),
  KEY `end_location_id` (`end_location_id`),
  CONSTRAINT `paths_ibfk_1` FOREIGN KEY (`start_location_id`) REFERENCES `locations` (`id`),
  CONSTRAINT `paths_ibfk_2` FOREIGN KEY (`end_location_id`) REFERENCES `locations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of paths
-- ----------------------------
INSERT INTO `paths` VALUES ('1', '1', '4', '152');
INSERT INTO `paths` VALUES ('2', '2', '3', '96');
INSERT INTO `paths` VALUES ('3', '2', '6', '312');
INSERT INTO `paths` VALUES ('4', '3', '4', '532');
INSERT INTO `paths` VALUES ('5', '3', '6', '281');
INSERT INTO `paths` VALUES ('6', '3', '7', '318');
INSERT INTO `paths` VALUES ('7', '3', '8', '323');
INSERT INTO `paths` VALUES ('8', '4', '5', '251');
INSERT INTO `paths` VALUES ('9', '4', '8', '98');
INSERT INTO `paths` VALUES ('10', '4', '9', '143');
INSERT INTO `paths` VALUES ('11', '5', '10', '85');
INSERT INTO `paths` VALUES ('12', '6', '7', '145');
INSERT INTO `paths` VALUES ('13', '6', '8', '442');
INSERT INTO `paths` VALUES ('14', '6', '13', '541');
INSERT INTO `paths` VALUES ('15', '7', '8', '226');
INSERT INTO `paths` VALUES ('16', '8', '9', '112');
INSERT INTO `paths` VALUES ('17', '8', '13', '437');
INSERT INTO `paths` VALUES ('18', '9', '10', '96');
INSERT INTO `paths` VALUES ('19', '9', '12', '412');
INSERT INTO `paths` VALUES ('20', '9', '13', '346');
INSERT INTO `paths` VALUES ('21', '10', '11', '261');
INSERT INTO `paths` VALUES ('22', '10', '12', '348');
INSERT INTO `paths` VALUES ('23', '11', '12', '187');
INSERT INTO `paths` VALUES ('24', '12', '13', '162');
INSERT INTO `paths` VALUES ('25', '12', '14', '198');
INSERT INTO `paths` VALUES ('26', '13', '14', '47');
