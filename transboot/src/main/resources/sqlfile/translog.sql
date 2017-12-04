/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50557
Source Host           : localhost:3306
Source Database       : yc_test

Target Server Type    : MYSQL
Target Server Version : 50557
File Encoding         : 65001

Date: 2017-12-04 13:48:03
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for translog
-- ----------------------------
DROP TABLE IF EXISTS `translog`;
CREATE TABLE `translog` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `trans_name` varchar(64) DEFAULT NULL COMMENT '执行转移的线程名称，job名称',
  `trans_table` varchar(32) DEFAULT NULL COMMENT '转移的目标表名，暂时不用',
  `all_between` varchar(64) DEFAULT NULL COMMENT '整体转移id区间，格式：1000-2000',
  `all_count` int(11) DEFAULT '0' COMMENT '总共需要转移的条数',
  `none_between` text COMMENT '空数据id区间',
  `suc_between` text COMMENT '已经转移成功的id区间，格式：2000-3000*6000-8000',
  `suc_count` int(11) DEFAULT '0' COMMENT '已经转移成功的条数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=424 DEFAULT CHARSET=utf8;
