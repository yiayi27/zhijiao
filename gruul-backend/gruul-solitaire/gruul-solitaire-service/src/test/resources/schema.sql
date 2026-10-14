/*
 Navicat Premium Data Transfer

 Source Server         : 139.155.90.217
 Source Server Type    : MySQL
 Source Server Version : 80020
 Source Host           : 139.155.90.217:3305
 Source Schema         : gruul

 Target Server Type    : MySQL
 Target Server Version : 80020
 File Encoding         : 65001

 Date: 10/10/202415:02:29
*/

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_solitaire_activity
-- ----------------------------
DROP TABLE IF EXISTS t_solitaire_activity;
CREATE TABLE t_solitaire_activity (
  id bigint NOT NULL AUTO_INCREMENT,
  create_time datetime DEFAULT NULL ,
  update_time datetime DEFAULT NULL ,
  is_deleted tinyint unsigned NOT NULL DEFAULT '0' ,
  shop_id varchar(64) ,
  name varchar(16) ,
  start_time datetime DEFAULT NULL ,
  end_time datetime DEFAULT NULL ,
  preheat_hour int NOT NULL DEFAULT '0' ,
  product_type int NOT NULL DEFAULT '0' ,
  cover_poster varchar(512) ,
  content varchar(512) ,
  status int NOT NULL DEFAULT '0' ,
  views int DEFAULT '0' ,
  PRIMARY KEY (id)
);


-- ----------------------------
-- Table structure for t_solitaire_activity_product
-- ----------------------------
DROP TABLE IF EXISTS t_solitaire_activity_product;
CREATE TABLE t_solitaire_activity_product (
  id bigint NOT NULL AUTO_INCREMENT,
  create_time datetime DEFAULT NULL ,
  update_time datetime DEFAULT NULL ,
  is_deleted tinyint unsigned NOT NULL DEFAULT '0' ,
  shop_id varchar(64) ,
  activity_id bigint NOT NULL ,
  product_id bigint DEFAULT NULL ,
  PRIMARY KEY (id)
);


-- ----------------------------
-- Table structure for t_solitaire_info
-- ----------------------------
DROP TABLE IF EXISTS t_solitaire_info;
CREATE TABLE t_solitaire_info (
  id bigint NOT NULL AUTO_INCREMENT,
  create_time datetime DEFAULT NULL ,
  update_time datetime DEFAULT NULL ,
  is_deleted tinyint unsigned NOT NULL DEFAULT '0' ,
  shop_id varchar(64) ,
  activity_state tinyint(1) DEFAULT '0' ,
  welcome_info varchar(255) ,
  welcome_info_set tinyint(1) DEFAULT '0' ,
  pay_order_info varchar(255) ,
  pay_order_info_set tinyint(1) DEFAULT '0' ,
  arrive_info varchar(255) ,
  arrive_info_set tinyint(1) DEFAULT '0' ,
  PRIMARY KEY (id)
);


-- ----------------------------
-- Table structure for t_solitaire_record
-- ----------------------------
DROP TABLE IF EXISTS t_solitaire_record;
CREATE TABLE t_solitaire_record (
  id bigint NOT NULL AUTO_INCREMENT,
  create_time datetime DEFAULT NULL ,
  update_time datetime DEFAULT NULL ,
  is_deleted tinyint unsigned NOT NULL DEFAULT '0' ,
  shop_id varchar(64) ,
  order_id varchar(20) ,
  activity_id varchar(20) ,
  buyer_id varchar(20) ,
  buyer_name varchar(32) ,
  buyer_icon varchar(255) ,
  product_name varchar(64) ,
  product_quantity int DEFAULT NULL ,
  pay_amount decimal(10,2) NOT NULL DEFAULT '0.00' ,
  PRIMARY KEY (id)
);