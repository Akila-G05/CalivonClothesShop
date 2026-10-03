/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE IF NOT EXISTS `calivon` /*!40100 DEFAULT CHARACTER SET utf32 COLLATE utf32_bin */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `calivon`;

CREATE TABLE IF NOT EXISTS `address` (
  `id` int NOT NULL AUTO_INCREMENT,
  `line_one` varchar(45) COLLATE utf32_bin NOT NULL,
  `line_two` varchar(45) COLLATE utf32_bin DEFAULT NULL,
  `mobile` varchar(20) COLLATE utf32_bin NOT NULL,
  `postal_code` varchar(10) COLLATE utf32_bin DEFAULT NULL,
  `city_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `is_primary` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpwa35mv5w9mb3syngd4m8fprw` (`city_id`),
  KEY `FKfmbstuufapi4tvs7epqq1q0pq` (`user_id`),
  CONSTRAINT `FKfmbstuufapi4tvs7epqq1q0pq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKpwa35mv5w9mb3syngd4m8fprw` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `address` (`id`, `line_one`, `line_two`, `mobile`, `postal_code`, `city_id`, `user_id`, `is_primary`) VALUES
	(19, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', '70017', 1, 3, b'1'),
	(20, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', '70017', 7, 1, b'0'),
	(22, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', '70017', 2, 1, b'1'),
	(25, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', '70017', 2, 5, b'1'),
	(31, 'R.M.P.C Rathnayaka  Rural, Developmen', '', '0710000000', '12312', 2, 16, b'1'),
	(32, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', '70017', 5, 18, b'1'),
	(33, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', '12323', 3, 17, b'1'),
	(34, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', '12323', 3, 22, b'1'),
	(35, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', '70017', 3, 23, b'1');

CREATE TABLE IF NOT EXISTS `admin` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(150) COLLATE utf32_bin NOT NULL,
  `first_name` varchar(45) COLLATE utf32_bin NOT NULL,
  `last_name` varchar(45) COLLATE utf32_bin NOT NULL,
  `password` varchar(20) COLLATE utf32_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_jl20d0ecx48g7qwy1dxe2akre` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `admin` (`id`, `email`, `first_name`, `last_name`, `password`) VALUES
	(1, 'akila@gmail.com', 'Akila', 'Gimhana', 'Usera@0000');

CREATE TABLE IF NOT EXISTS `brand` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf32_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `brand` (`id`, `name`) VALUES
	(1, 'Nike'),
	(2, 'Carnage'),
	(3, 'Puma'),
	(4, 'Moose'),
	(5, 'Reebok'),
	(6, 'Levi\'s'),
	(7, 'Gucci'),
	(8, 'Zara'),
	(9, 'H&M'),
	(10, 'Uniqlo'),
	(11, 'Tommy Hilfiger'),
	(12, 'Calvin Klein'),
	(13, 'Champion'),
	(14, 'North Face'),
	(15, 'Lacoste');

CREATE TABLE IF NOT EXISTS `cart` (
  `id` int NOT NULL AUTO_INCREMENT,
  `qty` int NOT NULL,
  `stock_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnwvjbmn8yhbtxc9igxwdko5f3` (`stock_id`),
  KEY `FK7toxf0y2a3mewe83du44h29fi` (`user_id`),
  CONSTRAINT `FK7toxf0y2a3mewe83du44h29fi` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKnwvjbmn8yhbtxc9igxwdko5f3` FOREIGN KEY (`stock_id`) REFERENCES `stock` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=231 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `cart` (`id`, `qty`, `stock_id`, `user_id`) VALUES
	(159, 1, 64, 16),
	(160, 1, 45, 16),
	(200, 1, 28, 18),
	(201, 1, 21, 18),
	(229, 1, 65, 1),
	(230, 1, 21, 1);

CREATE TABLE IF NOT EXISTS `category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf32_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `category` (`id`, `name`) VALUES
	(1, 'Male'),
	(2, 'Female'),
	(3, 'Unisex'),
	(4, 'Kids'),
	(5, 'Accessories');

CREATE TABLE IF NOT EXISTS `city` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf32_bin NOT NULL,
  `district_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbagelmxpwkea6853e1oiasqtl` (`district_id`),
  CONSTRAINT `FKbagelmxpwkea6853e1oiasqtl` FOREIGN KEY (`district_id`) REFERENCES `district` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `city` (`id`, `name`, `district_id`) VALUES
	(1, 'Rathnapura', 24),
	(2, 'Colombo', 1),
	(3, 'Kandy', 4),
	(4, 'Galle', 7),
	(5, 'Gampaha', 2),
	(6, 'Mathara', 8),
	(7, 'Kegalla', 25),
	(8, 'Badulla', 22),
	(9, 'Nuwara Eliya', 6),
	(10, 'Awissawella', 1),
	(11, 'Aheliyagoda', 24);

CREATE TABLE IF NOT EXISTS `color` (
  `id` int NOT NULL AUTO_INCREMENT,
  `value` varchar(45) COLLATE utf32_bin NOT NULL,
  `code` varchar(10) COLLATE utf32_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `color` (`id`, `value`, `code`) VALUES
	(1, 'Black', '#000000'),
	(2, 'White', '#FFFFFF'),
	(3, 'Red', '#FF0000'),
	(4, 'Blue', '#0000FF'),
	(5, 'Green', '#008000'),
	(6, 'Yellow', '#FFFF00'),
	(7, 'Orange', '#FFA500'),
	(8, 'Purple', '#800080'),
	(9, 'Pink', '#FFC0CB'),
	(10, 'Brown', '#A52A2A'),
	(11, 'Grey', '#808080'),
	(12, 'Navy', '#000080'),
	(13, 'Maroon', '#800000'),
	(14, 'Olive', '#808000'),
	(15, 'Beige', '#F5F5DC'),
	(16, 'Cream', '#FFFDD0'),
	(17, 'Teal', '#008080'),
	(18, 'Cyan', '#00FFFF'),
	(19, 'Magenta', '#FF00FF'),
	(20, 'Gold', '#FFD700'),
	(21, 'Silver', '#C0C0C0');

CREATE TABLE IF NOT EXISTS `delivery_types` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf32_bin NOT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `delivery_types` (`id`, `name`, `price`) VALUES
	(1, 'Card Payment', 0),
	(2, 'Cash On Delivery', 50);

CREATE TABLE IF NOT EXISTS `discount` (
  `id` int NOT NULL AUTO_INCREMENT,
  `expired_at` datetime(6) NOT NULL,
  `started_at` datetime(6) NOT NULL,
  `value` double NOT NULL,
  `discount_name` varchar(45) COLLATE utf32_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `discount` (`id`, `expired_at`, `started_at`, `value`, `discount_name`) VALUES
	(1, '2025-11-28 19:39:12.000000', '2025-11-28 19:39:15.000000', 0, 'DEFAULT'),
	(2, '2026-03-01 14:30:21.000000', '2025-11-29 14:30:13.000000', 10, 'Year End Sale'),
	(3, '2025-12-29 14:30:53.000000', '2025-12-31 14:31:00.000000', 5, 'Sale'),
	(5, '2025-10-29 17:22:54.000000', '2025-09-26 17:23:01.000000', 7, 'Sale'),
	(6, '2026-01-02 15:00:36.000000', '2025-12-02 15:00:33.000000', 50, 'Black Friday Sale');

CREATE TABLE IF NOT EXISTS `district` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf32_bin NOT NULL,
  `shipping` double NOT NULL,
  `province_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjxyk2jiq1rpxyv76da9sn1ygc` (`province_id`),
  CONSTRAINT `FKjxyk2jiq1rpxyv76da9sn1ygc` FOREIGN KEY (`province_id`) REFERENCES `province` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `district` (`id`, `name`, `shipping`, `province_id`) VALUES
	(1, 'Colombo', 250, 1),
	(2, 'Gampaha', 250, 1),
	(3, 'Kalutara', 300, 1),
	(4, 'Kandy', 350, 2),
	(5, 'Matale', 350, 2),
	(6, 'Nuwara Eliya', 400, 2),
	(7, 'Galle', 350, 3),
	(8, 'Matara', 350, 3),
	(9, 'Hambantota', 400, 3),
	(10, 'Jaffna', 450, 4),
	(11, 'Kilinochchi', 450, 4),
	(12, 'Mannar', 450, 4),
	(13, 'Vavuniya', 400, 4),
	(14, 'Mullaitivu', 450, 4),
	(15, 'Batticaloa', 400, 5),
	(16, 'Ampara', 400, 5),
	(17, 'Trincomalee', 400, 5),
	(18, 'Kurunegala', 350, 6),
	(19, 'Puttalam', 350, 6),
	(20, 'Anuradhapura', 400, 7),
	(21, 'Polonnaruwa', 400, 7),
	(22, 'Badulla', 400, 8),
	(23, 'Monaragala', 400, 8),
	(24, 'Ratnapura', 350, 9),
	(25, 'Kegalle', 350, 9);

CREATE TABLE IF NOT EXISTS `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `price` double NOT NULL,
  `delivery_types_id` int DEFAULT NULL,
  `status_id` int DEFAULT NULL,
  `user` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi7a3ycmjwid6iixp7kpano11m` (`delivery_types_id`),
  KEY `FKg09a38ionvsxoej9hy9kyv36a` (`status_id`),
  KEY `FKoamucu044lhcgtc1pqmxhl22e` (`user`),
  CONSTRAINT `FKg09a38ionvsxoej9hy9kyv36a` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`),
  CONSTRAINT `FKi7a3ycmjwid6iixp7kpano11m` FOREIGN KEY (`delivery_types_id`) REFERENCES `delivery_types` (`id`),
  CONSTRAINT `FKoamucu044lhcgtc1pqmxhl22e` FOREIGN KEY (`user`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `orders` (`id`, `created_at`, `updated_at`, `price`, `delivery_types_id`, `status_id`, `user`) VALUES
	(41, '2025-12-17 13:44:33', '2025-12-24 17:44:49.195134', 0, 2, 8, 5),
	(43, '2025-12-17 15:35:40', '2025-12-24 17:44:32.268096', 0, 2, 8, 5),
	(44, '2025-12-17 18:25:18', '2026-01-03 16:10:12.592401', 2995, 2, 11, 5),
	(45, '2025-12-17 22:10:18', '2025-12-17 22:10:18.415126', 5672.5, 2, 8, 16),
	(46, '2025-12-21 20:38:08', '2025-12-23 08:06:35.995797', 550, 2, 9, 1),
	(47, '2025-12-22 14:36:45', '2025-12-24 17:44:41.128761', 5872.5, 2, 12, 16),
	(48, '2025-12-23 21:16:26', '2025-12-23 21:16:50.005543', 350, 2, 9, 3),
	(49, '2025-12-23 21:25:17', '2025-12-23 21:26:28.498881', -450, 2, 9, 3),
	(50, '2025-12-23 21:27:13', '2025-12-23 23:23:37.801625', 350, 2, 8, 3),
	(51, '2025-12-23 21:51:27', '2025-12-23 21:55:44.851472', 14265, 2, 9, 1),
	(52, '2025-12-23 22:23:17', '2025-12-23 22:24:33.577315', 5967.5, 2, 9, 1),
	(53, '2025-12-23 22:39:07', '2025-12-23 22:39:32.189576', 0, 2, 9, 1),
	(54, '2025-12-23 23:01:12', '2025-12-23 23:23:04.194037', 0, 2, 8, 1),
	(55, '2025-12-31 21:08:47', '2025-12-31 21:18:50.759195', 8975, 2, 11, 18),
	(56, '2025-12-31 21:09:05', '2025-12-31 21:18:44.904762', 1900, 2, 12, 18),
	(57, '2025-12-31 21:09:57', '2025-12-31 21:18:33.773589', 4660, 2, 11, 18),
	(58, '2025-12-31 21:10:18', '2025-12-31 21:18:28.705702', 2650, 2, 11, 18),
	(59, '2025-12-31 21:11:34', '2025-12-31 21:18:21.525805', 9700, 2, 12, 17),
	(60, '2025-12-31 21:13:36', '2025-12-31 21:18:08.576996', 1600, 2, 12, 17),
	(61, '2026-01-04 16:29:10', '2026-01-04 16:29:10.032436', 7210, 2, 2, 22),
	(62, '2026-01-11 21:38:31', '2026-01-11 21:38:30.593419', 22650, 1, 2, 22),
	(63, '2026-01-11 21:47:30', '2026-01-11 21:47:29.626747', 22650, 1, 2, 22),
	(64, '2026-01-11 21:49:15', '2026-01-11 21:49:14.944742', 8100, 1, 2, 22),
	(65, '2026-01-11 22:02:08', '2026-01-11 22:02:08.405290', 8100, 1, 2, 22),
	(66, '2026-01-11 22:07:08', '2026-01-11 22:07:08.249643', 8100, 1, 2, 22),
	(67, '2026-01-11 22:07:50', '2026-01-11 22:07:50.286251', 8100, 1, 2, 22),
	(68, '2026-01-11 22:13:35', '2026-01-11 22:13:35.268852', 8100, 1, 2, 22),
	(69, '2026-01-11 22:23:26', '2026-01-11 22:23:25.707247', 8100, 1, 2, 22),
	(70, '2026-01-11 23:16:42', '2026-01-11 23:16:42.382817', 5435, 2, 2, 23),
	(71, '2026-01-13 20:28:01', '2026-01-13 20:28:00.730775', 8100, 1, 2, 22),
	(72, '2026-01-13 20:30:45', '2026-01-13 20:30:44.665990', 5200, 1, 2, 22),
	(73, '2026-01-13 20:30:57', '2026-01-13 20:30:56.835044', 5200, 1, 2, 22),
	(74, '2026-01-13 20:33:11', '2026-01-13 20:33:10.676932', 5200, 1, 2, 22),
	(75, '2026-01-13 21:08:29', '2026-01-13 21:08:28.904278', 5200, 1, 2, 22),
	(76, '2026-01-13 21:19:11', '2026-01-13 21:19:11.040334', 5250, 2, 2, 22),
	(77, '2026-01-13 22:25:00', '2026-01-13 22:25:00.371225', 1480, 2, 2, 22),
	(78, '2026-01-13 22:35:28', '2026-01-13 22:35:27.870770', 3250, 1, 2, 22),
	(79, '2026-01-13 22:35:38', '2026-01-13 22:35:37.771992', 3010, 2, 2, 22),
	(80, '2026-01-13 23:41:53', '2026-01-13 23:41:52.929131', 2960, 1, 2, 22),
	(81, '2026-01-14 00:09:45', '2026-01-14 00:09:44.801567', 1150, 1, 2, 22),
	(82, '2026-01-15 14:46:52', '2026-01-15 15:27:19.901140', 3975, 2, 9, 22),
	(83, '2026-01-15 15:26:24', '2026-01-15 15:26:24.060201', 6080, 1, 2, 22),
	(84, '2026-01-15 16:49:39', '2026-01-15 16:49:38.759498', 1350, 1, 2, 22),
	(85, '2026-01-15 16:56:43', '2026-01-15 16:56:43.287885', 2960, 1, 2, 22),
	(86, '2026-01-15 21:39:28', '2026-01-15 21:40:07.379285', 0, 1, 9, 22),
	(87, '2026-01-15 22:32:42', '2026-01-15 22:38:49.727920', 0, 1, 9, 22),
	(88, '2026-01-15 22:35:55', '2026-01-15 22:38:46.472983', 0, 1, 9, 22),
	(89, '2026-01-15 22:37:03', '2026-01-15 22:38:43.577299', 0, 1, 9, 22),
	(90, '2026-01-15 22:52:35', '2026-01-15 22:52:35.120137', 2960, 1, 2, 22),
	(91, '2026-01-15 23:00:34', '2026-01-15 23:00:34.280328', 2960, 1, 2, 22),
	(92, '2026-01-15 23:01:57', '2026-01-15 23:01:57.169940', 5000, 1, 2, 22),
	(93, '2026-01-15 23:18:16', '2026-01-15 23:18:16.082880', 2960, 1, 2, 22),
	(94, '2026-01-15 23:22:34', '2026-01-15 23:22:33.984686', 1150, 1, 2, 22),
	(95, '2026-01-15 23:27:35', '2026-01-15 23:27:34.943841', 5200, 1, 2, 22),
	(96, '2026-01-15 23:32:05', '2026-01-15 23:32:05.300784', 2960, 1, 2, 22),
	(97, '2026-01-15 23:43:01', '2026-01-15 23:43:01.296746', 2960, 1, 2, 22),
	(98, '2026-01-15 23:46:49', '2026-01-15 23:46:48.536523', 2960, 1, 2, 22),
	(99, '2026-02-17 21:48:05', '2026-02-18 21:55:53.149592', 2860, 1, 12, 1);

CREATE TABLE IF NOT EXISTS `order_details` (
  `id` int NOT NULL AUTO_INCREMENT,
  `line_one` varchar(45) COLLATE utf32_bin NOT NULL,
  `line_two` varchar(45) COLLATE utf32_bin DEFAULT NULL,
  `mobile` varchar(20) COLLATE utf32_bin NOT NULL,
  `name` varchar(45) COLLATE utf32_bin NOT NULL,
  `postal_code` varchar(10) COLLATE utf32_bin DEFAULT NULL,
  `city_id` int DEFAULT NULL,
  `orders_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1gyrbolh2vhsmd1t44bg5v0oy` (`city_id`),
  KEY `FK6xxnvv8s2d2sxpps9ncv3b962` (`orders_id`),
  CONSTRAINT `FK1gyrbolh2vhsmd1t44bg5v0oy` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`),
  CONSTRAINT `FK6xxnvv8s2d2sxpps9ncv3b962` FOREIGN KEY (`orders_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `order_details` (`id`, `line_one`, `line_two`, `mobile`, `name`, `postal_code`, `city_id`, `orders_id`) VALUES
	(1, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Oshan  Saminda', '70017', 2, 41),
	(2, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Oshan Saminda', '70017', 2, 43),
	(3, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Oshan Saminda', '70017', 2, 44),
	(4, 'R.M.P.C Rathnayaka  Rural, Developmen', '', '0710000000', 'Chamal Rajapaksha', '12312', 8, 45),
	(5, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Sahan Ranawaka', '70017', 2, 46),
	(6, 'R.M.P.C Rathnayaka  Rural, Developmen', '', '0710000000', 'Chamal Rajapaksha', '12312', 2, 47),
	(7, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Meraj Lakvindu', '70017', 1, 48),
	(8, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Meraj Lakvindu', '70017', 1, 49),
	(9, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Meraj Lakvindu', '70017', 1, 50),
	(10, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Sahan Ranawaka', '70017', 2, 51),
	(11, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Sahan Ranawaka', '70017', 2, 52),
	(12, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Sahan Ranawaka', '70017', 2, 53),
	(13, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Sahan Ranawaka', '70017', 2, 54),
	(14, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Maleesha Dasanayaka', '70017', 5, 55),
	(15, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Maleesha Dasanayaka', '70017', 5, 56),
	(16, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Maleesha Dasanayaka', '70017', 5, 57),
	(17, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Maleesha Dasanayaka', '70017', 5, 58),
	(18, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Sheron Randewa', '12323', 3, 59),
	(19, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Sheron Randewa', '12323', 3, 60),
	(20, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 61),
	(21, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Tharun Rathnayaka', '70017', 3, 70),
	(22, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 76),
	(23, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 77),
	(24, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 79),
	(25, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 80),
	(26, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 81),
	(27, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 82),
	(28, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 83),
	(29, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 84),
	(30, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 85),
	(31, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 86),
	(32, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 87),
	(33, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 88),
	(34, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 89),
	(35, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 90),
	(36, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 91),
	(37, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 92),
	(38, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 93),
	(39, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 94),
	(40, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 95),
	(41, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 96),
	(42, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 97),
	(43, 'Karawita Road, Palawela, Udaniriella', '', '0712354661', 'Migara Lakshan', '12323', 3, 98),
	(44, 'Karawita Road, Palawela, Rathnapura', '', '0710000000', 'Sahan Ranawaka', '70017', 2, 99);

CREATE TABLE IF NOT EXISTS `order_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `qty` int NOT NULL,
  `rating` int DEFAULT NULL,
  `orders_id` bigint DEFAULT NULL,
  `stock_id` int DEFAULT NULL,
  `buying_price` double NOT NULL,
  `status_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkbaf1oyqru4qwvaojv3thajvs` (`orders_id`),
  KEY `FK6d19tbirujiojgx3wqrthaqwn` (`stock_id`),
  KEY `FKsr9e768rnpajit1nw81e3oek1` (`status_id`),
  CONSTRAINT `FK6d19tbirujiojgx3wqrthaqwn` FOREIGN KEY (`stock_id`) REFERENCES `stock` (`id`),
  CONSTRAINT `FKkbaf1oyqru4qwvaojv3thajvs` FOREIGN KEY (`orders_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKsr9e768rnpajit1nw81e3oek1` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `order_items` (`id`, `qty`, `rating`, `orders_id`, `stock_id`, `buying_price`, `status_id`) VALUES
	(19, 1, 0, 41, 64, 0, 8),
	(20, 1, 0, 41, 29, 0, 8),
	(21, 2, 0, 41, 1, 0, 8),
	(22, 2, 0, 43, 29, 0, 8),
	(23, 1, 0, 43, 21, 0, 8),
	(24, 2, 0, 44, 29, 0, 1),
	(25, 1, 0, 44, 21, 0, 1),
	(26, 1, 0, 45, 64, 0, 1),
	(27, 1, 0, 45, 45, 0, 1),
	(28, 1, 0, 46, 64, 2897.5, 9),
	(29, 3, 0, 46, 45, 2425, 9),
	(30, 1, 0, 47, 45, 2425, 12),
	(31, 1, 0, 47, 64, 2897.5, 12),
	(32, 2, 0, 48, 4, 800, 9),
	(33, 1, 0, 48, 1, 1000, 9),
	(34, 2, 0, 49, 4, 800, 9),
	(35, 1, 0, 49, 12, 1045, 9),
	(36, 2, 0, 50, 12, 1045, 9),
	(37, 1, 0, 50, 4, 800, 8),
	(38, 1, 0, 51, 64, 2897.5, 9),
	(39, 2, 0, 51, 45, 2425, 9),
	(40, 1, 0, 51, 21, 1080, 9),
	(41, 1, 0, 52, 64, 2897.5, 9),
	(42, 2, 0, 52, 29, 807.5, 9),
	(43, 1, 0, 52, 21, 1080, 9),
	(44, 1, 0, 52, 1, 1000, 9),
	(45, 1, 0, 53, 64, 2897.5, 9),
	(46, 2, 0, 53, 53, 2425, 9),
	(47, 2, 0, 53, 4, 800, 9),
	(48, 2, 0, 54, 64, 2897.5, 8),
	(49, 1, 0, 54, 61, 3000, 8),
	(50, 2, 0, 54, 66, 3000, 8),
	(51, 2, 0, 55, 37, 2325, 1),
	(52, 1, 0, 55, 45, 2425, 1),
	(53, 2, 0, 55, 28, 800, 1),
	(54, 2, 0, 56, 4, 800, 12),
	(55, 1, 0, 57, 14, 1200, 1),
	(56, 1, 0, 57, 1, 1000, 1),
	(57, 2, 0, 57, 21, 1080, 1),
	(58, 1, 0, 58, 44, 2350, 1),
	(59, 3, 0, 59, 62, 3100, 12),
	(60, 1, 0, 60, 16, 1200, 12),
	(61, 2, 0, 61, 21, 1080, 1),
	(62, 2, 0, 61, 37, 4650, 1),
	(63, 1, 0, 70, 65, 2610, 1),
	(64, 1, 0, 70, 45, 4850, 1),
	(65, 1, 0, 76, 53, 4850, 1),
	(66, 1, 0, 77, 21, 1080, 1),
	(67, 1, 0, 79, 65, 2610, 1),
	(68, 1, 0, 80, 65, 2610, 1),
	(69, 1, 0, 81, 4, 800, 1),
	(70, 1, 0, 82, 21, 1080, 9),
	(71, 1, 0, 82, 37, 4650, 9),
	(72, 1, 0, 83, 37, 4650, 1),
	(73, 1, 0, 83, 21, 1080, 1),
	(74, 1, 0, 84, 1, 1000, 1),
	(75, 1, 0, 85, 65, 2610, 1),
	(76, 1, 0, 86, 45, 4850, 9),
	(77, 1, 0, 86, 65, 2610, 9),
	(78, 1, 0, 86, 37, 4650, 9),
	(79, 1, 0, 87, 21, 1080, 9),
	(80, 1, 0, 88, 28, 800, 9),
	(81, 1, 0, 89, 65, 2610, 9),
	(82, 1, 0, 90, 65, 2610, 1),
	(83, 1, 0, 91, 65, 2610, 1),
	(84, 1, 0, 92, 37, 4650, 1),
	(85, 1, 0, 93, 65, 2610, 1),
	(86, 1, 0, 94, 4, 800, 1),
	(87, 1, 0, 95, 45, 4850, 1),
	(88, 1, 0, 96, 65, 2610, 1),
	(89, 1, 0, 97, 65, 2610, 1),
	(90, 1, 0, 98, 65, 2610, 1),
	(91, 1, 0, 99, 65, 2610, 12);

CREATE TABLE IF NOT EXISTS `product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `description` text COLLATE utf32_bin NOT NULL,
  `title` varchar(200) COLLATE utf32_bin NOT NULL,
  `admin_id` int DEFAULT NULL,
  `brand_id` int DEFAULT NULL,
  `sub_category_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKet5jgt88eahyltjfk4y7y5h47` (`admin_id`),
  KEY `FKcbnyvs2x321b8yw2vi398b26h` (`brand_id`),
  KEY `FKp8b3opf1ep5qbtjxosus9v7ab` (`sub_category_id`),
  CONSTRAINT `FKcbnyvs2x321b8yw2vi398b26h` FOREIGN KEY (`brand_id`) REFERENCES `brand` (`id`),
  CONSTRAINT `FKet5jgt88eahyltjfk4y7y5h47` FOREIGN KEY (`admin_id`) REFERENCES `admin` (`id`),
  CONSTRAINT `FKp8b3opf1ep5qbtjxosus9v7ab` FOREIGN KEY (`sub_category_id`) REFERENCES `sub_category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `product` (`id`, `created_at`, `updated_at`, `description`, `title`, `admin_id`, `brand_id`, `sub_category_id`) VALUES
	(3, '2025-11-28 17:14:12', '2025-11-28 17:14:12.023277', '<ul style=" list-style: none; font-family: Roboto; font-size: 14px; background-color: rgb(255, 255, 255)"><li style=" margin-bottom: 7px; position: relative; padding-left: 15px"><span>Product : Royal Blue &amp; Navy Blue Men’s Sports T-Shirt</span></li><li style=" margin-bottom: 7px; position: relative; padding-left: 15px"><span>Brand : OXYGEN SPORTS</span></li><li style=" margin-bottom: 7px; position: relative; padding-left: 15px"><span>Style : Crew Neck – Short Sleeve</span></li><li style=" margin-bottom: 7px; position: relative; padding-left: 15px"><span>Colour : Royal Blue &amp; Navy Blue</span></li><li style=" margin-bottom: 7px; position: relative; padding-left: 15px"><span>Material : Dri-Fit – 95% Polyester Microfiber, 5% Spandex</span></li><li style=" margin-bottom: 7px; position: relative; padding-left: 15px"><span>Thickness : 150 – 160 GSM</span></li><li style=" margin-bottom: 7px; position: relative; padding-left: 15px"><span>Size Range : XS – XXL</span></li><li style=" margin-bottom: 7px; position: relative; padding-left: 15px"><span>Quality Standards : 100% QC Passed. Export Ready.</span></li><li style=" margin-bottom: 7px; position: relative; padding-left: 15px"><span>Specialities : Lightweight, Moisture-Wicking, Wrinkle Free, Anti-Shrink, Quick-Dry Performance.&nbsp;</span></li><li style=" margin-bottom: 7px; position: relative; padding-left: 15px"><span>Warranty : 14 Day Easy Returns &amp; Size Exchanges.&nbsp;</span><strong style=" font-weight: 700"><a href="https://tshirtrepublic.lk/returns-exchanges/" style=" background: 0px 0px; color: rgb(0, 102, 204); text-decoration: none; transition: 0.5s">Return &amp; Exchange Policy</a></strong></li><li style=" margin-bottom: 7px; position: relative; padding-left: 15px"><span>Delivery : Estimated 1-3 Working Days within Colombo &amp; Suburbs. 3-5 Working Days Outstation.</span></li><li style=" margin-bottom: 7px; position: relative; padding-left: 15px"><span>Payment Options : Card or Cash on Delivery at Checkout.</span></li><li style=" margin-bottom: 7px; position: relative; padding-left: 15px"><span>A Genuine Product. Made in Sri Lanka.</span></li></ul>', 'Male Cotton Sports T-Shirt', 1, 14, 1),
	(4, '2025-11-28 21:46:21', '2025-11-28 21:46:21.178712', '<p>\n\n</p>\r\n\n\n\r\n<p data-pm-slice="0 0 []">Change Of Mind is NOT APPLICABLE\n</p>\r\n<p>\n</p>\r\n<p>\n</p>\r\n<p>About the fabric\n</p>\r\n<p>\n</p>\r\n<ul><li>Fabric composition: 100% Cotton\n</li><li>Fabric pattern: Solid\n</li><li>Fabric Care</li></ul>\r\n<ul><li>Machine wash\n</li><li>Add- on features</li></ul>\r\n<ul><li>Waist: Mid-Rise</li><li>Fit type: Slim\n</li><li>Length: Regular\n</li><li>Type of pleat: Flat front\n</li><li>Pocket styling: Slant pocket\n</li><li>Waistband closure: Centre front button with zip fly</li><li>Double welt with back pocket</li></ul>\r\n<p>\n</p>\r\n<p>Note\n</p>\r\n<p>\n</p>\r\n<p>Please bear in mind that the photo may be slightly different from the actual item in terms of color due to lighting conditions or the display used to view.</p>\r\n\n\r\n<br />', 'Moose Men’s Slim Fit Chino Shorts', 1, 4, 3),
	(5, '2025-11-29 01:35:25', '2025-11-29 01:35:24.543952', '<p>\n\n<p style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 12px; white-space: break-spaces; background-color: rgb(255, 255, 255)"><span style=" white-space: break-spaces">Change of Mind is NOT APPLICABLE</span></p><p style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 12px; white-space: break-spaces; background-color: rgb(255, 255, 255)"><span style=" white-space: break-spaces">Note</span></p><ul style="margin: 0px 0px 0px 16px; list-style: disc; overflow: hidden; column-count: 2; column-gap: 32px; font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 12px; white-space: break-spaces; background-color: rgb(255, 255, 255)"><li style=" padding: 0px 0px 0px 15px; position: relative; font-size: 14px; line-height: 18px; text-align: left; list-style: none; word-break: break-word; break-inside: avoid; column-span: all"><div data-spm-anchor-id="a2a0e.pdp_revamp.product_detail.i0.1470453d5yCu94"><span style=" white-space: break-spaces">Please bear in mind that the photo may be slightly different from the actual item in terms of color due to lighting conditions or the display used to view.</span></div></li></ul><p style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 12px; white-space: break-spaces; background-color: rgb(255, 255, 255)"><br class="Apple-interchange-newline" />\n</p><br /></p>', 'Moose Men’s Band Collar Polo T-Shirts', 1, 4, 1),
	(6, '2025-11-30 01:03:31', '2025-11-30 01:03:30.635984', '<p>\n\n<p style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 12px; white-space: break-spaces; background-color: rgb(255, 255, 255)"><span style=" white-space: break-spaces">Change Of Mind is NOT APPLICABLE</span></p><p style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 12px; white-space: break-spaces; background-color: rgb(255, 255, 255)"></p><p style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 12px; white-space: break-spaces; background-color: rgb(255, 255, 255)"><span style=" white-space: break-spaces">Note</span></p><ul style="margin: 0px 0px 0px 16px; list-style: disc; overflow: hidden; column-count: 2; column-gap: 32px; font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 12px; white-space: break-spaces; background-color: rgb(255, 255, 255)"><li style=" padding: 0px 0px 0px 15px; position: relative; font-size: 14px; line-height: 18px; text-align: left; list-style: none; word-break: break-word; break-inside: avoid; column-span: all"><div data-spm-anchor-id="a2a0e.pdp_revamp.product_detail.i0.53876b47gd89MQ"><span style=" white-space: break-spaces">Please bear in mind that the photo may be slightly different from the actual item in terms of color due to lighting conditions or the display used to view.</span></div></li></ul>\n\n\n<img src="https://img.drz.lazcdn.com/static/lk/p/1fb4657a3c8391949b0cd54b052a81e3.png_2200x2200q80.png_.webp" />\n<br /></p>', 'Moose Men’s Assorted Polo T-Shirts', 1, 4, 1),
	(7, '2025-11-30 01:11:42', '2025-11-30 01:11:41.765973', '<p>\n\n<p style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 12px; white-space: break-spaces; background-color: rgb(255, 255, 255)"><span style=" white-space: break-spaces">Change Of Mind is NOT APPLICABLE</span></p><p style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 12px; white-space: break-spaces; background-color: rgb(255, 255, 255)"></p><p style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 12px; white-space: break-spaces; background-color: rgb(255, 255, 255)"><span style=" white-space: break-spaces">Note</span></p><p data-spm-anchor-id="a2a0e.pdp_revamp.product_detail.i0.7b0d39423ofzXF" style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 12px; white-space: break-spaces; background-color: rgb(255, 255, 255)"><span style=" white-space: break-spaces">Please note: Item may slightly vary from the displayed image in terms of colour due to lighting conditions or the display used to view, and the fabric material from colour to colour and size to size.</span></p>\n<br /></p>', 'Moose Everyday Essential Tee with back logo', 1, 4, 26),
	(9, '2025-12-02 14:57:55', '2025-12-02 14:57:55.315001', '<p>dsd</p>', 'Fearless Sweatshirt', 1, 2, 14),
	(10, '2025-12-02 15:13:59', '2025-12-02 15:13:58.595955', '<p>\n\n<p data-pm-slice="0 0 []"><span style="font-weight: bold;">Fearless Relaxed Jogger - Unisex\n</span></p><p>\n</p><p>Comfort Meets Style in Every Step.\n</p><p>\n</p><p>Fit: Regular Fit for maximum comfort and freedom of movement.\n</p><p>Design Elements: These joggers feature a relaxed silhouette with a stretchy waistband and adjustable drawstrings for a perfect fit. The subtle \'Carnage\' branding on the leg adds a touch of exclusivity.\n</p><p>Fabric: Made from soft, warm fleece to keep you comfortable whether you\'re relaxing at home or out and about.\n</p><p>Functionality: Equipped with deep side pockets for practicality and ribbed ankle cuffs to keep the warmth in and the cold out</p>\n</p>', 'Fearless Relaxed Jogger - Unisex', 1, 2, 16),
	(11, '2025-12-02 15:18:53', '2025-12-02 15:18:53.269155', '<div>test</div>', 'Test Product', 1, 15, 7),
	(12, '2025-12-02 15:25:05', '2025-12-02 15:25:05.478617', '<p>no Stocks</p>', 'Test With no Stocks', 1, 15, 8),
	(13, '2025-12-03 10:18:12', '2025-12-03 10:18:11.855979', '<p>\n\n<span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Specifications:</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Collar:V-Neck</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Sleeve Length(cm):Full</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Clothing Length:Short</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Pattern Type:Solid</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Type:Regular</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Closure Type:Single Breasted</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Sleeve Style:Regular</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Hooded:No</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Style:Casual</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Model Number:regular</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Thickness:STANDARD</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Place Of Origin:China (Mainland)</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Origin:CN(Origin)</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Season:Spring/Autumn</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Gender:WOMEN</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Item Type:Outerwear &amp; Coats</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Outerwear Type:Jackets</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Size----------------Bust-------------Sleeves-------Length</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">One-size---80-90cm/31.4-35.4"---60cm/23.6"---42cm/16.5"</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">Features:</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">1.Unique Design: Button down, casual sytle, fitting, very soft and warm</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">2.This is the perfect layering piece for Fall! Its super soft, cozy, knit material is so comfy and flattering on all body types!</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">3.You\'ll love the cozy, relaxed fit and it\'s a great go to piece to keep you covered this fall and winter!</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">4.Occasion: Casual, daily life, office, outdoor, school; Suitable for Spring, Fall, Winter. Perfect for skinny jeans, leggings, dresses, boots or sneakers</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" /><span style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)">5.Loose-Fit: designed for comfort</span><br style=" font-family: Roboto, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, sans-serif; font-size: 14px; white-space: break-spaces; background-color: rgb(255, 255, 255)" />\n<br /></p>', 'Y2k Women Cardigan Sweater Knitted Cropped Korean ', 1, 15, 7);

CREATE TABLE IF NOT EXISTS `product_images` (
  `pr_id` int NOT NULL,
  `images` varchar(255) COLLATE utf32_bin DEFAULT NULL,
  KEY `FK23lp2kao2hmugpiry9xfh6il7` (`pr_id`),
  CONSTRAINT `FK23lp2kao2hmugpiry9xfh6il7` FOREIGN KEY (`pr_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `product_images` (`pr_id`, `images`) VALUES
	(3, '/calivon/uploads/product/3/1764331625533.jpg'),
	(3, '/calivon/uploads/product/3/1764331625539.jpg'),
	(3, '/calivon/uploads/product/3/1764331625540.jpg'),
	(4, '/calivon/uploads/product/4/1764346583707.webp'),
	(4, '/calivon/uploads/product/4/1764346583797.webp'),
	(4, '/calivon/uploads/product/4/1764346583798.webp'),
	(5, '/calivon/uploads/product/5/1764360325135.webp'),
	(5, '/calivon/uploads/product/5/1764360325181.webp'),
	(5, '/calivon/uploads/product/5/1764360325183.jpg'),
	(6, '/calivon/uploads/product/6/1764444811070.webp'),
	(6, '/calivon/uploads/product/6/1764444811076.jpg'),
	(6, '/calivon/uploads/product/6/1764444811077.webp'),
	(7, '/calivon/uploads/product/7/1764445301993.jpg'),
	(7, '/calivon/uploads/product/7/1764445301995.webp'),
	(7, '/calivon/uploads/product/7/1764445301997.webp'),
	(9, '/calivon/uploads/product/9/1764667676158.jpg'),
	(9, '/calivon/uploads/product/9/1764667676231.jpg'),
	(9, '/calivon/uploads/product/9/1764667676233.jpg'),
	(10, '/calivon/uploads/product/10/1764668638888.jpg'),
	(10, '/calivon/uploads/product/10/1764668638890.jpg'),
	(10, '/calivon/uploads/product/10/1764668638913.jpg'),
	(11, '/calivon/uploads/product/11/1764668933335.webp'),
	(12, '/calivon/uploads/product/12/1764669305540.webp'),
	(13, '/calivon/uploads/product/13/1764737292254.webp'),
	(13, '/calivon/uploads/product/13/1764737292261.webp'),
	(13, '/calivon/uploads/product/13/1764737292263.webp');

CREATE TABLE IF NOT EXISTS `province` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf32_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `province` (`id`, `name`) VALUES
	(1, 'Western'),
	(2, 'Central'),
	(3, 'Southern'),
	(4, 'Northern'),
	(5, 'Eastern'),
	(6, 'North Western'),
	(7, 'North Central'),
	(8, 'Uva'),
	(9, 'Sabaragamuwa');

CREATE TABLE IF NOT EXISTS `size` (
  `id` int NOT NULL AUTO_INCREMENT,
  `value` varchar(45) COLLATE utf32_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `size` (`id`, `value`) VALUES
	(1, 'XS'),
	(2, 'S'),
	(3, 'M'),
	(4, 'L'),
	(5, 'XL'),
	(8, 'XXL'),
	(9, '3XL');

CREATE TABLE IF NOT EXISTS `status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `value` varchar(45) COLLATE utf32_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_da9ode5mags8exr2uu1xcnp4d` (`value`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `status` (`id`, `value`) VALUES
	(1, 'ACTIVE'),
	(7, 'APPROVED'),
	(4, 'BLOCKED'),
	(9, 'CANCELED'),
	(12, 'COMPLETED'),
	(5, 'DELIVERED'),
	(13, 'GUEST'),
	(3, 'INACTIVE'),
	(6, 'PACKING'),
	(2, 'PENDING'),
	(11, 'RECEIVED'),
	(8, 'REJECTED'),
	(10, 'VERIFIED');

CREATE TABLE IF NOT EXISTS `stock` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `price` double NOT NULL,
  `qty` int NOT NULL,
  `product_id` int DEFAULT NULL,
  `color_id` int DEFAULT NULL,
  `size_id` int DEFAULT NULL,
  `status_id` int DEFAULT NULL,
  `discount_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgcqg8nn63ci2t8r5prcs3m9mw` (`product_id`),
  KEY `FKs7o087iv4whni24ukgqjy30pa` (`color_id`),
  KEY `FKsk5y0tktj48mnsclf6k78gsq8` (`size_id`),
  KEY `FKhx99omwced336v7wm70uln155` (`status_id`),
  KEY `FK7nrqu7r0duvtv7oivrdqeatfd` (`discount_id`),
  CONSTRAINT `FK7nrqu7r0duvtv7oivrdqeatfd` FOREIGN KEY (`discount_id`) REFERENCES `discount` (`id`),
  CONSTRAINT `FKgcqg8nn63ci2t8r5prcs3m9mw` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FKhx99omwced336v7wm70uln155` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`),
  CONSTRAINT `FKs7o087iv4whni24ukgqjy30pa` FOREIGN KEY (`color_id`) REFERENCES `color` (`id`),
  CONSTRAINT `FKsk5y0tktj48mnsclf6k78gsq8` FOREIGN KEY (`size_id`) REFERENCES `size` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `stock` (`id`, `created_at`, `updated_at`, `price`, `qty`, `product_id`, `color_id`, `size_id`, `status_id`, `discount_id`) VALUES
	(1, '2025-11-28 19:43:55', '2025-12-31 21:09:56.585099', 1000, 3, 3, 15, 3, 1, 1),
	(2, '2025-11-28 19:43:55', '2025-11-28 19:43:54.884681', 1100, 15, 3, 4, 3, 1, 1),
	(3, '2025-11-28 19:43:55', '2025-11-28 19:43:55.125765', 1100, 20, 3, 1, 3, 1, 1),
	(4, '2025-11-28 21:46:25', '2026-01-14 00:09:44.916141', 800, 14, 4, 1, 3, 1, 1),
	(5, '2025-11-28 21:46:26', '2025-11-28 21:46:25.673633', 900, 20, 4, 1, 4, 1, 1),
	(6, '2025-11-28 21:46:26', '2025-11-28 21:46:25.765472', 1000, 25, 4, 4, 3, 1, 1),
	(7, '2025-11-28 21:46:26', '2025-11-28 21:46:25.831084', 1000, 25, 4, 4, 4, 1, 1),
	(8, '2025-11-28 21:46:26', '2025-11-28 21:46:25.935805', 800, 20, 4, 1, 2, 1, 1),
	(9, '2025-11-28 21:46:26', '2025-11-28 21:46:26.035146', 1000, 25, 4, 4, 2, 1, 1),
	(10, '2025-11-28 21:46:26', '2025-11-28 21:46:26.162794', 900, 20, 4, 2, 3, 1, 1),
	(11, '2025-11-28 21:46:26', '2025-11-28 21:46:26.249212', 900, 20, 4, 2, 4, 1, 1),
	(12, '2025-11-29 01:35:26', '2025-12-23 21:28:41.765775', 1100, 20, 5, 2, 2, 1, 3),
	(13, '2025-11-29 01:35:26', '2025-11-29 21:12:41.832121', 1100, 20, 5, 2, 3, 1, 3),
	(14, '2025-11-29 01:35:26', '2025-12-31 21:09:56.584101', 1200, 19, 5, 3, 4, 1, 1),
	(15, '2025-11-29 01:35:26', '2025-11-29 21:46:23.344245', 1300, 20, 5, 3, 5, 1, 1),
	(16, '2025-11-29 01:35:26', '2025-12-31 21:13:36.284919', 1200, 19, 5, 2, 4, 1, 1),
	(17, '2025-11-29 01:35:26', '2025-11-29 01:35:26.494116', 1300, 20, 5, 2, 5, 1, 1),
	(18, '2025-11-29 01:35:27', '2025-11-29 01:35:26.527027', 1100, 20, 5, 3, 3, 1, 1),
	(19, '2025-11-29 01:35:27', '2025-11-29 01:35:26.560874', 1100, 20, 5, 3, 2, 1, 1),
	(20, '2025-11-30 01:03:31', '2025-11-30 01:03:31.223236', 1200, 20, 6, 9, 2, 1, 1),
	(21, '2025-11-30 01:03:31', '2026-01-15 22:38:49.723929', 1200, 7, 6, 9, 3, 1, 2),
	(22, '2025-11-30 01:03:31', '2025-11-30 01:03:31.330493', 1300, 20, 6, 9, 4, 1, 1),
	(23, '2025-11-30 01:03:31', '2025-12-02 13:23:22.880852', 1400, 15, 6, 9, 5, 1, 2),
	(24, '2025-11-30 01:03:31', '2025-11-30 01:03:31.432931', 1200, 20, 6, 8, 2, 1, 1),
	(25, '2025-11-30 01:03:31', '2025-11-30 01:03:31.482930', 1200, 20, 6, 8, 3, 1, 1),
	(26, '2025-11-30 01:03:32', '2025-11-30 01:03:31.529453', 1300, 15, 6, 8, 4, 1, 1),
	(27, '2025-11-30 01:03:32', '2025-11-30 01:03:31.583803', 1400, 15, 6, 8, 5, 1, 1),
	(28, '2025-11-30 01:11:42', '2026-01-15 22:38:46.466003', 800, 29, 7, 1, 3, 1, 1),
	(29, '2025-11-30 01:11:42', '2025-12-24 12:29:03.782178', 850, 26, 7, 1, 4, 1, 3),
	(30, '2025-11-30 01:11:42', '2025-11-30 01:11:42.233992', 900, 35, 7, 1, 5, 1, 1),
	(31, '2025-11-30 01:11:42', '2025-11-30 01:11:42.282022', 800, 20, 7, 1, 2, 1, 1),
	(32, '2025-11-30 01:11:42', '2025-12-02 03:08:33.476465', 850, 20, 7, 2, 4, 1, 3),
	(33, '2025-11-30 01:11:42', '2025-11-30 01:11:42.393024', 800, 30, 7, 2, 3, 1, 1),
	(34, '2025-11-30 01:11:42', '2025-11-30 01:11:42.451999', 900, 25, 7, 2, 5, 1, 1),
	(35, '2025-11-30 01:11:43', '2025-11-30 01:11:42.592966', 1000, 25, 7, 2, 5, 1, 1),
	(37, '2025-12-02 14:57:57', '2026-01-15 21:39:58.737657', 4650, 27, 9, 7, 3, 1, 6),
	(38, '2025-12-02 14:57:57', '2025-12-02 15:01:17.095014', 4650, 30, 9, 7, 4, 1, 6),
	(39, '2025-12-02 14:57:57', '2025-12-02 15:01:19.067956', 4700, 30, 9, 7, 5, 1, 6),
	(40, '2025-12-02 14:57:57', '2025-12-02 15:01:21.105041', 4700, 30, 9, 7, 8, 1, 6),
	(41, '2025-12-02 14:57:57', '2025-12-02 15:01:23.190077', 4650, 30, 9, 4, 3, 1, 6),
	(42, '2025-12-02 14:57:57', '2025-12-02 15:01:26.217157', 4650, 30, 9, 4, 4, 1, 6),
	(43, '2025-12-02 14:57:58', '2025-12-02 15:01:28.105593', 4700, 30, 9, 4, 5, 1, 6),
	(44, '2025-12-02 14:57:58', '2025-12-31 21:10:17.843127', 4700, 29, 9, 4, 8, 1, 6),
	(45, '2025-12-02 15:13:59', '2026-01-15 21:40:02.651698', 4850, 16, 10, 7, 3, 1, 6),
	(46, '2025-12-02 15:13:59', '2025-12-02 15:19:10.133264', 4850, 20, 10, 7, 4, 1, 6),
	(47, '2025-12-02 15:13:59', '2025-12-02 15:19:12.284318', 4850, 20, 10, 7, 5, 1, 6),
	(48, '2025-12-02 15:13:59', '2025-12-02 15:19:14.514027', 4850, 20, 10, 7, 8, 1, 6),
	(49, '2025-12-02 15:13:59', '2025-12-02 15:19:16.623876', 4850, 20, 10, 4, 3, 1, 6),
	(50, '2025-12-02 15:13:59', '2025-12-02 15:19:20.518130', 4850, 20, 10, 4, 4, 1, 6),
	(51, '2025-12-02 15:13:59', '2025-12-02 15:19:21.967262', 4850, 20, 10, 4, 5, 1, 6),
	(52, '2025-12-02 15:13:59', '2025-12-02 15:19:23.784616', 4850, 20, 10, 4, 8, 1, 6),
	(53, '2025-12-02 15:13:59', '2026-01-13 21:19:11.167301', 4850, 19, 10, 1, 3, 1, 1),
	(54, '2025-12-02 15:13:59', '2025-12-02 15:19:29.757068', 4850, 20, 10, 1, 4, 1, 6),
	(55, '2025-12-02 15:13:59', '2025-12-02 15:19:31.335206', 4850, 20, 10, 1, 5, 1, 6),
	(56, '2025-12-02 15:13:59', '2025-12-02 15:19:33.243897', 4850, 20, 10, 1, 5, 1, 6),
	(57, '2025-12-02 15:18:53', '2025-12-12 20:06:54.775530', 2000, 20, 11, 10, 1, 3, 1),
	(59, '2025-12-02 15:26:19', '2025-12-02 15:26:25.000000', 8000, 0, 12, 4, 4, 1, 1),
	(60, '2025-12-02 23:30:49', '2026-01-03 17:38:42.609172', 2000, 0, 11, 11, 2, 1, 1),
	(61, '2025-12-03 10:18:12', '2025-12-23 23:23:04.181072', 3000, 11, 13, 9, 3, 1, 1),
	(62, '2025-12-03 10:18:12', '2025-12-31 21:11:34.108755', 3100, 17, 13, 9, 5, 1, 1),
	(63, '2025-12-03 10:18:12', '2025-12-03 10:18:12.458219', 2800, 20, 13, 4, 2, 1, 1),
	(64, '2025-12-03 10:18:12', '2025-12-24 12:33:14.489651', 3050, 10, 13, 4, 4, 1, 3),
	(65, '2025-12-03 10:18:13', '2026-01-15 22:38:43.572311', 2900, 24, 13, 1, 3, 1, 2),
	(66, '2025-12-03 10:18:13', '2025-12-23 23:10:57.193931', 3000, 21, 13, 1, 4, 1, 1),
	(67, '2025-12-03 10:18:13', '2025-12-03 10:18:12.612760', 3150, 18, 13, 1, 5, 1, 1);

CREATE TABLE IF NOT EXISTS `sub_category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf32_bin NOT NULL,
  `category_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfrvso3uwxnvjkvbs38bql5vps` (`category_id`),
  CONSTRAINT `FKfrvso3uwxnvjkvbs38bql5vps` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `sub_category` (`id`, `name`, `category_id`) VALUES
	(1, 'T-Shirts', 1),
	(2, 'Shirts', 1),
	(3, 'Shorts', 1),
	(4, 'Jeans', 1),
	(5, 'Formal Wear', 1),
	(6, 'Sportswear', 1),
	(7, 'Blouses', 2),
	(8, 'Dresses', 2),
	(9, 'Skirts', 2),
	(10, 'Leggings', 2),
	(11, 'Crop Tops', 2),
	(12, 'Activewear', 2),
	(13, 'Hoodies', 3),
	(14, 'Sweatshirts', 3),
	(15, 'Jackets', 3),
	(16, 'Pants', 3),
	(17, 'Kid T-Shirts', 4),
	(18, 'Kid Shorts', 4),
	(19, 'Kid Dresses', 4),
	(20, 'School Wear', 4),
	(21, 'Caps', 5),
	(22, 'Bags', 5),
	(23, 'Socks', 5),
	(24, 'Belts', 5),
	(25, 'Wallets', 5),
	(26, 'T-Shirts', 3),
	(27, 'Shirts', 3);

CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `email` varchar(150) COLLATE utf32_bin NOT NULL,
  `first_name` varchar(45) COLLATE utf32_bin NOT NULL,
  `last_name` varchar(45) COLLATE utf32_bin NOT NULL,
  `password` varchar(20) COLLATE utf32_bin NOT NULL,
  `verification_code` varchar(15) COLLATE utf32_bin NOT NULL,
  `status_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  KEY `FKkhvq8wvtimtj2v6281bwi44eg` (`status_id`),
  CONSTRAINT `FKkhvq8wvtimtj2v6281bwi44eg` FOREIGN KEY (`status_id`) REFERENCES `status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `users` (`id`, `created_at`, `updated_at`, `email`, `first_name`, `last_name`, `password`, `verification_code`, `status_id`) VALUES
	(1, '2025-11-23 23:46:27', '2025-11-27 19:40:51.232994', 'sahan@gmail.com', 'Sahan', 'Ranawaka', 'User@0000', '123456', 10),
	(3, '2025-11-25 17:49:48', '2025-11-25 18:03:19.175674', 'meraj@gmail.com', 'Meraj', 'Lakvindu', 'User@0005', '', 10),
	(4, '2025-11-25 18:08:23', '2025-11-25 18:08:23.271920', 'kavishka@gmail.com', 'Kavishka', 'Devinda', 'User@0000', '920483', 2),
	(5, '2025-12-15 20:19:33', '2025-12-15 20:23:10.347406', 'oshan@gmail.com', 'Oshan', 'Saminda', 'User@0000', '', 10),
	(16, '2025-12-17 22:10:16', '2025-12-27 22:03:30.946835', 'chamal@gmail.com', 'Chamal', 'Rajapaksha', 'User@0000', '427348', 13),
	(17, '2025-12-23 18:27:20', '2025-12-31 21:07:05.662320', 'sheron@gmail.com', 'Sheron', 'Randewa', 'User@0000', '', 10),
	(18, '2025-12-31 21:06:16', '2025-12-31 21:06:53.539201', 'maleesha@gmail.com', 'Maleesha', 'Dasanayaka', 'User@0000', '580253', 10),
	(19, '2026-01-03 22:01:33', '2026-01-03 22:01:32.705286', 'gayan@gmail.com', 'Gayan', 'Gunaruwan', 'User@0000', '824465', 2),
	(20, '2026-01-03 22:01:58', '2026-01-04 15:48:55.682054', 'sanithu@gmail.com', 'Sanithu', 'Rathnayaka', 'User@0000', '854723', 10),
	(21, '2026-01-03 22:02:20', '2026-01-03 22:02:19.671452', 'supun@gmail.com', 'Supun', 'Chanaka', 'User@0000', '101826', 2),
	(22, '2026-01-03 22:03:29', '2026-01-04 15:48:53.335996', 'migara@gmail.com', 'Migara', 'Lakshan', 'User@0000', '113323', 10),
	(23, '2026-01-11 23:16:40', '2026-01-11 23:16:40.408773', 'tharun@gmail.com', 'Tharun', 'Rathnayaka', 'User@408453', '408453', 13);

CREATE TABLE IF NOT EXISTS `wishlist` (
  `id` int NOT NULL AUTO_INCREMENT,
  `qty` int NOT NULL,
  `stock_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKthstosd1qvtb48h7w7vlt4cyv` (`stock_id`),
  KEY `FK6wyu6kqklbujhm7oxsgqqaw33` (`user_id`),
  CONSTRAINT `FK6wyu6kqklbujhm7oxsgqqaw33` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKthstosd1qvtb48h7w7vlt4cyv` FOREIGN KEY (`stock_id`) REFERENCES `stock` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf32 COLLATE=utf32_bin;

INSERT INTO `wishlist` (`id`, `qty`, `stock_id`, `user_id`) VALUES
	(21, 1, 64, 1),
	(22, 1, 57, 1),
	(26, 1, 64, 16),
	(27, 1, 21, 22),
	(28, 1, 37, 22);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
