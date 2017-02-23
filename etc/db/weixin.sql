/*
Navicat MySQL Data Transfer

Source Server         : root
Source Server Version : 50506
Source Host           : localhost:3306
Source Database       : weixin

Target Server Type    : MYSQL
Target Server Version : 50506
File Encoding         : 65001

Date: 2016-12-12 14:43:15
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `members`
-- ----------------------------
DROP TABLE IF EXISTS `members`;
CREATE TABLE `members` (
  `mid` int(11) NOT NULL AUTO_INCREMENT,
  `wxOpenId` varchar(5000) DEFAULT NULL,
  `nickname` varchar(200) DEFAULT NULL,
  `sex` int(11) DEFAULT NULL,
  `city` varchar(200) DEFAULT NULL,
  `country` varchar(200) DEFAULT NULL,
  `province` varchar(200) DEFAULT NULL,
  `headimgurl` varchar(5000) DEFAULT NULL,
  `subscribeTime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`mid`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;