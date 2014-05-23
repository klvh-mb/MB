-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.1.44-community - MySQL Community Server (GPL)
-- Server OS:                    Win32
-- HeidiSQL Version:             8.0.0.4396
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping structure for table parent-social.album
CREATE TABLE IF NOT EXISTS `album` (
  `description` longtext,
  `name` varchar(255) DEFAULT NULL,
  `system` tinyint(1) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `cover_photo_id` bigint(20) DEFAULT NULL,
  `folder_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_lfq1ro8w1onxkhnapkgsvpvoh` (`cover_photo_id`),
  KEY `FK_e54v8t83gmu0nk4krmumkrfbl` (`folder_id`),
  KEY `FK_l11vmb82cvicf6o3lsq8u0wtc` (`id`),
  CONSTRAINT `FK_e54v8t83gmu0nk4krmumkrfbl` FOREIGN KEY (`folder_id`) REFERENCES `folder` (`id`),
  CONSTRAINT `FK_l11vmb82cvicf6o3lsq8u0wtc` FOREIGN KEY (`id`) REFERENCES `socialobject` (`id`),
  CONSTRAINT `FK_lfq1ro8w1onxkhnapkgsvpvoh` FOREIGN KEY (`cover_photo_id`) REFERENCES `resource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.album: ~0 rows (approximately)
/*!40000 ALTER TABLE `album` DISABLE KEYS */;
/*!40000 ALTER TABLE `album` ENABLE KEYS */;


-- Dumping structure for table parent-social.album_resource
CREATE TABLE IF NOT EXISTS `album_resource` (
  `Album_id` bigint(20) NOT NULL,
  `resources_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_b3m2y2bctyorjxcii2dqwe7yn` (`resources_id`),
  KEY `FK_b3m2y2bctyorjxcii2dqwe7yn` (`resources_id`),
  KEY `FK_2xpdc1ucyenkkpbfj4la39jq5` (`Album_id`),
  CONSTRAINT `FK_2xpdc1ucyenkkpbfj4la39jq5` FOREIGN KEY (`Album_id`) REFERENCES `album` (`id`),
  CONSTRAINT `FK_b3m2y2bctyorjxcii2dqwe7yn` FOREIGN KEY (`resources_id`) REFERENCES `resource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.album_resource: ~0 rows (approximately)
/*!40000 ALTER TABLE `album_resource` DISABLE KEYS */;
/*!40000 ALTER TABLE `album_resource` ENABLE KEYS */;


-- Dumping structure for table parent-social.comment
CREATE TABLE IF NOT EXISTS `comment` (
  `body` longtext,
  `commentType` int(11) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `socialObject_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_88r7ycad7d5vnju2xrvewxily` (`socialObject_id`),
  KEY `FK_efqbpcxw2bbqrq93yvvkswr7e` (`id`),
  CONSTRAINT `FK_88r7ycad7d5vnju2xrvewxily` FOREIGN KEY (`socialObject_id`) REFERENCES `socialobject` (`id`),
  CONSTRAINT `FK_efqbpcxw2bbqrq93yvvkswr7e` FOREIGN KEY (`id`) REFERENCES `socialobject` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.comment: ~0 rows (approximately)
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` (`body`, `commentType`, `date`, `id`, `socialObject_id`) VALUES
	('This is Comment 1 from user1', 1, '2014-03-26 16:19:36', 75, 69),
	('This is Comment 2 from user1', 1, '2014-03-26 16:19:37', 76, 69),
	('This is Comment 3 from user1', 1, '2014-03-26 16:19:37', 77, 69),
	('This is Comment 4 from user1', 1, '2014-03-26 16:19:37', 78, 70),
	('This is Comment 51 from user1', 1, '2014-03-26 16:19:37', 79, 71),
	('This is Comment 44 from user1', 1, '2014-03-26 18:57:14', 80, 73),
	('This is Comment 34 from user1', 1, '2014-03-26 18:57:14', 81, 73),
	('This is Comment 44 from user1', 1, '2014-03-26 18:59:17', 82, 74),
	('This is Comment 34 from user1', 1, '2014-03-26 18:59:17', 83, 74);
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;


-- Dumping structure for table parent-social.community
CREATE TABLE IF NOT EXISTS `community` (
  `communityType` int(11) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `albumPhotoProfile_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_b058waapuh276jf9f71nu3rgr` (`albumPhotoProfile_id`),
  KEY `FK_6s4jynqge5ubelkxw00yxrvo9` (`id`),
  CONSTRAINT `FK_6s4jynqge5ubelkxw00yxrvo9` FOREIGN KEY (`id`) REFERENCES `socialobject` (`id`),
  CONSTRAINT `FK_b058waapuh276jf9f71nu3rgr` FOREIGN KEY (`albumPhotoProfile_id`) REFERENCES `folder` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.community: ~7 rows (approximately)
/*!40000 ALTER TABLE `community` DISABLE KEYS */;
INSERT INTO `community` (`communityType`, `id`, `albumPhotoProfile_id`) VALUES
	(1, 18, 37),
	(1, 19, 35),
	(1, 20, 29),
	(1, 21, 25),
	(1, 22, 27),
	(1, 23, 33),
	(1, 24, 31);
/*!40000 ALTER TABLE `community` ENABLE KEYS */;


-- Dumping structure for table parent-social.community_folder
CREATE TABLE IF NOT EXISTS `community_folder` (
  `Community_id` bigint(20) NOT NULL,
  `folders_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_kc0p3fnldieqtmu3od4mkkq7b` (`folders_id`),
  KEY `FK_kc0p3fnldieqtmu3od4mkkq7b` (`folders_id`),
  KEY `FK_gfqbi2q1up7vitmfy9hdxd1be` (`Community_id`),
  CONSTRAINT `FK_gfqbi2q1up7vitmfy9hdxd1be` FOREIGN KEY (`Community_id`) REFERENCES `community` (`id`),
  CONSTRAINT `FK_kc0p3fnldieqtmu3od4mkkq7b` FOREIGN KEY (`folders_id`) REFERENCES `folder` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.community_folder: ~7 rows (approximately)
/*!40000 ALTER TABLE `community_folder` DISABLE KEYS */;
INSERT INTO `community_folder` (`Community_id`, `folders_id`) VALUES
	(18, 37),
	(19, 35),
	(20, 29),
	(21, 25),
	(22, 27),
	(23, 33),
	(24, 31);
/*!40000 ALTER TABLE `community_folder` ENABLE KEYS */;


-- Dumping structure for table parent-social.community_post
CREATE TABLE IF NOT EXISTS `community_post` (
  `Community_id` bigint(20) NOT NULL,
  `posts_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Community_id`,`posts_id`),
  UNIQUE KEY `UK_ipip877dx7tgd0w3882gon40n` (`posts_id`),
  KEY `FK_ipip877dx7tgd0w3882gon40n` (`posts_id`),
  KEY `FK_ini7yg8fvnn5ucm4vcp8l6n5o` (`Community_id`),
  CONSTRAINT `FK_ini7yg8fvnn5ucm4vcp8l6n5o` FOREIGN KEY (`Community_id`) REFERENCES `community` (`id`),
  CONSTRAINT `FK_ipip877dx7tgd0w3882gon40n` FOREIGN KEY (`posts_id`) REFERENCES `post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.community_post: ~6 rows (approximately)
/*!40000 ALTER TABLE `community_post` DISABLE KEYS */;
INSERT INTO `community_post` (`Community_id`, `posts_id`) VALUES
	(18, 69),
	(18, 70),
	(18, 71),
	(19, 72),
	(19, 73),
	(18, 74);
/*!40000 ALTER TABLE `community_post` ENABLE KEYS */;


-- Dumping structure for table parent-social.community_user
CREATE TABLE IF NOT EXISTS `community_user` (
  `Community_id` bigint(20) NOT NULL,
  `members_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Community_id`,`members_id`),
  UNIQUE KEY `UK_t2m89y0gl72x8ri1b6av3c2ke` (`members_id`),
  KEY `FK_t2m89y0gl72x8ri1b6av3c2ke` (`members_id`),
  KEY `FK_h57kmp8lqimc6b18qhwcqb1p5` (`Community_id`),
  CONSTRAINT `FK_h57kmp8lqimc6b18qhwcqb1p5` FOREIGN KEY (`Community_id`) REFERENCES `community` (`id`),
  CONSTRAINT `FK_t2m89y0gl72x8ri1b6av3c2ke` FOREIGN KEY (`members_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.community_user: ~7 rows (approximately)
/*!40000 ALTER TABLE `community_user` DISABLE KEYS */;
INSERT INTO `community_user` (`Community_id`, `members_id`) VALUES
	(22, 4),
	(19, 5),
	(18, 6),
	(20, 10),
	(23, 11),
	(21, 13),
	(24, 15);
/*!40000 ALTER TABLE `community_user` ENABLE KEYS */;


-- Dumping structure for table parent-social.conversation
CREATE TABLE IF NOT EXISTS `conversation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `user1_id` bigint(20) DEFAULT NULL,
  `user2_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_1b6hw0s67n2wmae5lqjnx6hau` (`user1_id`),
  KEY `FK_g461g0pu6d1yruhbuassax5p8` (`user2_id`),
  CONSTRAINT `FK_1b6hw0s67n2wmae5lqjnx6hau` FOREIGN KEY (`user1_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_g461g0pu6d1yruhbuassax5p8` FOREIGN KEY (`user2_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.conversation: ~0 rows (approximately)
/*!40000 ALTER TABLE `conversation` DISABLE KEYS */;
/*!40000 ALTER TABLE `conversation` ENABLE KEYS */;


-- Dumping structure for table parent-social.folder
CREATE TABLE IF NOT EXISTS `folder` (
  `description` longtext,
  `name` varchar(255) DEFAULT NULL,
  `system` tinyint(1) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_idx3wiwdm8yp2qkkddi726n8o` (`id`),
  CONSTRAINT `FK_idx3wiwdm8yp2qkkddi726n8o` FOREIGN KEY (`id`) REFERENCES `socialobject` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.folder: ~22 rows (approximately)
/*!40000 ALTER TABLE `folder` DISABLE KEYS */;
INSERT INTO `folder` (`description`, `name`, `system`, `id`) VALUES
	('album.photo-profile.description', 'profile', 1, 25),
	('album.photo-profile.description', 'profile', 1, 27),
	('album.photo-profile.description', 'profile', 1, 29),
	('album.photo-profile.description', 'profile', 1, 31),
	('album.photo-profile.description', 'profile', 1, 33),
	('album.photo-profile.description', 'profile', 1, 35),
	('album.photo-profile.description', 'profile', 1, 37),
	('album.photo-profile.description', 'profile', 1, 39),
	('album.photo-profile.description', 'profile', 1, 41),
	('album.photo-profile.description', 'profile', 1, 43),
	('album.photo-profile.description', 'profile', 1, 45),
	('album.photo-profile.description', 'profile', 1, 47),
	('album.photo-profile.description', 'profile', 1, 49),
	('album.photo-profile.description', 'profile', 1, 51),
	('album.photo-profile.description', 'profile', 1, 53),
	('album.photo-profile.description', 'profile', 1, 55),
	('album.photo-profile.description', 'profile', 1, 57),
	('album.photo-profile.description', 'profile', 1, 59),
	('album.photo-cover.description', 'cover', 1, 61),
	('album.photo-cover.description', 'cover', 1, 63),
	('album.photo-profile.description', 'profile', 1, 65),
	('album.photo-cover.description', 'cover', 1, 67);
/*!40000 ALTER TABLE `folder` ENABLE KEYS */;


-- Dumping structure for table parent-social.linkedaccount
CREATE TABLE IF NOT EXISTS `linkedaccount` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `providerKey` varchar(255) DEFAULT NULL,
  `providerUserId` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ars8xm3e8rv9qp2kwtjgcj6ib` (`user_id`),
  CONSTRAINT `FK_ars8xm3e8rv9qp2kwtjgcj6ib` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.linkedaccount: ~17 rows (approximately)
/*!40000 ALTER TABLE `linkedaccount` DISABLE KEYS */;
INSERT INTO `linkedaccount` (`id`, `CREATED_BY`, `CREATED_DATE`, `UPDATED_BY`, `UPDATED_DATE`, `providerKey`, `providerUserId`, `user_id`) VALUES
	(1, NULL, NULL, NULL, NULL, 'password', '$2a$10$R/smDmGPO6pooVV3ci8tHeAubFEdZxDd34ySwvsIbZg3VUlkPIAfW', 1),
	(2, NULL, NULL, NULL, NULL, 'password', '$2a$10$fjfoLQMhLc/OzZkiRD/H6Ogbo4//1Wx2UeFhGafESrLzNgNLb1A/.', 2),
	(3, NULL, NULL, NULL, NULL, 'password', '$2a$10$YBDUx.t967U70TaxvFWYduO9pEPevW.zStfv9YmHhkr8STYginGGG', 3),
	(4, NULL, NULL, NULL, NULL, 'password', '$2a$10$7r.lJcRKOMAW5zNsIjIYO.ELmxE4NhewjcfNvqCHYcMwYXFaGGojC', 4),
	(5, NULL, NULL, NULL, NULL, 'password', '$2a$10$FOCztGwmvL4QPP.ptZIcueogo487V.wRY2OwThNQEzDRC9n4eLeE2', 5),
	(6, NULL, NULL, NULL, NULL, 'password', '$2a$10$NuvkOhQmCaJtmG/Ce9lsTefk0pYvz33GxzfTCvtx6kUXcsNPIjlpS', 6),
	(7, NULL, NULL, NULL, NULL, 'password', '$2a$10$z1AAkFqJ3u6xhxJmyyaVw./p.8tshfryZVGOH5t5shSc7lCuHw2Vu', 7),
	(8, NULL, NULL, NULL, NULL, 'password', '$2a$10$NEDo8fk1NIvujuhGePb/hegeX1K89W0H3hcwGPppksllsbfL0wbJi', 8),
	(9, NULL, NULL, NULL, NULL, 'password', '$2a$10$ZbAmxUGjzGzRtay5XOY2LOxW.3phX3P02ELe4VGqNl.XYNGGvaJNG', 9),
	(10, NULL, NULL, NULL, NULL, 'password', '$2a$10$7Ljt2Eh0OyGQ286cOwE/QOQ53NmeZk0h5ICqwB/uE8pSOFg/LFpr6', 10),
	(11, NULL, NULL, NULL, NULL, 'password', '$2a$10$7VWEbc2NH1z7vLZmApfdDexJm6gVWA/aiJxzVZj1Sfq6Qi6wuj/Le', 11),
	(12, NULL, NULL, NULL, NULL, 'password', '$2a$10$jsKqZzXYH7VIDSGUZo1I2edC1kcvkfVx8Vzfcx5LwBvOpkNsrzHkK', 12),
	(13, NULL, NULL, NULL, NULL, 'password', '$2a$10$R.pNZ0bK69OFXzLd43f2W.BNUvjrXFM6c58s3jx0e4UjGr9fP7Aa.', 13),
	(14, NULL, NULL, NULL, NULL, 'password', '$2a$10$FgIL5SdvUHd6nOSxI0nGT.dmNZhlCsi6i/vC558L91STnCwtossCC', 14),
	(15, NULL, NULL, NULL, NULL, 'password', '$2a$10$fo3lnmuaxdhjcwhvcArE5O8ieWUOVrkTC4CjTUNzt.nJdkTiLIyxa', 15),
	(16, NULL, NULL, NULL, NULL, 'password', '$2a$10$IEt0pJ/foW6szaGcWoP24eezJc0.feG15qQY2uMy004MGtLzgPtba', 16),
	(17, NULL, NULL, NULL, NULL, 'password', '$2a$10$/6O6u6QWuWRLwlVZNZBgpuROK0MITDteiRGwqmaw63wgjLPUrHQVK', 17);
/*!40000 ALTER TABLE `linkedaccount` ENABLE KEYS */;


-- Dumping structure for table parent-social.message
CREATE TABLE IF NOT EXISTS `message` (
  `body` longtext,
  `date` datetime DEFAULT NULL,
  `readed` tinyint(1) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `conversation_id` bigint(20) DEFAULT NULL,
  `userFrom_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_aqf1kc84k9po560lyiuh9vut8` (`conversation_id`),
  KEY `FK_fw45mcwudv6fy725hfx5y5iwi` (`userFrom_id`),
  KEY `FK_1lk61g74f6w90a3jabg0gkr68` (`id`),
  CONSTRAINT `FK_1lk61g74f6w90a3jabg0gkr68` FOREIGN KEY (`id`) REFERENCES `socialobject` (`id`),
  CONSTRAINT `FK_aqf1kc84k9po560lyiuh9vut8` FOREIGN KEY (`conversation_id`) REFERENCES `conversation` (`id`),
  CONSTRAINT `FK_fw45mcwudv6fy725hfx5y5iwi` FOREIGN KEY (`userFrom_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.message: ~0 rows (approximately)
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
/*!40000 ALTER TABLE `message` ENABLE KEYS */;


-- Dumping structure for table parent-social.notification
CREATE TABLE IF NOT EXISTS `notification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `notificationType` varchar(255) DEFAULT NULL,
  `readed` tinyint(1) DEFAULT NULL,
  `recipetent_id` bigint(20) DEFAULT NULL,
  `socialAction_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_sduqq5js90t5uwrye95dl5b1v` (`recipetent_id`),
  KEY `FK_kjriabg6hjtt71txt6t7i2i5a` (`socialAction_id`),
  CONSTRAINT `FK_kjriabg6hjtt71txt6t7i2i5a` FOREIGN KEY (`socialAction_id`) REFERENCES `socialrelation` (`id`),
  CONSTRAINT `FK_sduqq5js90t5uwrye95dl5b1v` FOREIGN KEY (`recipetent_id`) REFERENCES `socialobject` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.notification: ~29 rows (approximately)
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` (`id`, `CREATED_BY`, `CREATED_DATE`, `UPDATED_BY`, `UPDATED_DATE`, `message`, `notificationType`, `readed`, `recipetent_id`, `socialAction_id`) VALUES
	(1, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'You are now member of Test Group4', NULL, 0, 13, 1),
	(2, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'You are now member of Test Group5', NULL, 0, 4, 2),
	(3, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'You are now member of Test Group3', NULL, 0, 10, 3),
	(4, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'You are now member of Test Group7', NULL, 0, 15, 4),
	(5, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'You are now member of Test Group6', NULL, 0, 11, 5),
	(6, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'You are now member of Test Group2', NULL, 0, 5, 6),
	(7, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'You are now member of Test Group1', NULL, 0, 6, 7),
	(8, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'Ankush P wants to be Your Friend Jagbir P', NULL, 0, 10, 25),
	(9, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'Ajinkya G wants to be Your Friend Jagbir P', NULL, 0, 10, 26),
	(10, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'Ashish D wants to be Your Friend Jagbir P', NULL, 0, 10, 27),
	(11, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'Dhananjay b wants to be Your Friend Jagbir P', NULL, 0, 10, 28),
	(12, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'Tushar G wants to be Your Friend Jagbir P', NULL, 0, 10, 29),
	(13, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'Pankaj B wants to be Your Friend Jagbir P', NULL, 0, 10, 30),
	(14, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'Deepak b wants to be Your Friend Jagbir P', NULL, 0, 10, 31),
	(15, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'Cheatan G wants to be Your Friend Jagbir P', NULL, 0, 10, 32),
	(16, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'Pushkar B wants to be Your Friend Jagbir P', NULL, 0, 10, 33),
	(17, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'Sharad P wants to be Your Friend Jagbir P', NULL, 0, 10, 34),
	(18, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'You are now Friend of Jagbir P', NULL, 0, 4, 25),
	(19, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'You are now Friend of Jagbir P', NULL, 0, 13, 26),
	(20, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'You are now Friend of Jagbir P', NULL, 0, 11, 27),
	(21, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'You are now Friend of Jagbir P', NULL, 0, 15, 28),
	(22, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'You are now Friend of Jagbir P', NULL, 0, 6, 29),
	(23, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'You are now Friend of Jagbir P', NULL, 0, 5, 30),
	(24, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'You are now Friend of Jagbir P', NULL, 0, 8, 31),
	(25, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'You are now Friend of Jagbir P', NULL, 0, 12, 32),
	(26, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'You are now Friend of Jagbir P', NULL, 0, 14, 33),
	(27, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'You are now Friend of Jagbir P', NULL, 0, 1, 34),
	(28, 'TODO', '2014-03-25 15:34:14', NULL, NULL, 'Dherej b wants to be Your Friend Jagbir P', NULL, 0, 10, 40),
	(29, 'TODO', '2014-03-25 19:53:00', NULL, NULL, 'Dherej b wants to be Your Friend Dhananjay b', NULL, 0, 15, 41),
	(30, 'TODO', '2014-03-26 16:19:36', NULL, NULL, 'Ankush P Commented on your Post', NULL, 0, 4, 48),
	(31, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'Ankush P Commented on your Post', NULL, 0, 4, 49),
	(32, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'Jagbir P Commented on your Post', NULL, 0, 4, 50),
	(33, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'Jagbir P Commented on your Post', NULL, 0, 10, 51),
	(34, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'Pankaj B Commented on your Post', NULL, 0, 4, 52),
	(35, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'Pankaj B Commented on your Post', NULL, 0, 5, 53),
	(36, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'Ankush P Commented on your Post', NULL, 0, 4, 54),
	(37, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'Ankush P Commented on your Post', NULL, 0, 4, 55),
	(38, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'Pankaj B Commented on your Post', NULL, 0, 4, 56),
	(39, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'Pankaj B Commented on your Post', NULL, 0, 5, 57),
	(40, 'TODO', '2014-03-26 18:57:14', NULL, NULL, 'Pankaj B Commented on your Post', NULL, 0, 10, 58),
	(41, 'TODO', '2014-03-26 18:57:14', NULL, NULL, 'Pankaj B Commented on your Post', NULL, 0, 5, 59),
	(42, 'TODO', '2014-03-26 18:57:14', NULL, NULL, 'Ankush P Commented on your Post', NULL, 0, 10, 60),
	(43, 'TODO', '2014-03-26 18:57:14', NULL, NULL, 'Ankush P Commented on your Post', NULL, 0, 4, 61),
	(44, 'TODO', '2014-03-26 18:59:17', NULL, NULL, 'Pankaj B Commented on your Post', NULL, 0, 10, 62),
	(45, 'TODO', '2014-03-26 18:59:17', NULL, NULL, 'Pankaj B Commented on your Post', NULL, 0, 5, 63),
	(46, 'TODO', '2014-03-26 18:59:17', NULL, NULL, 'Ankush P Commented on your Post', NULL, 0, 10, 64),
	(47, 'TODO', '2014-03-26 18:59:17', NULL, NULL, 'Ankush P Commented on your Post', NULL, 0, 4, 65);
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;


-- Dumping structure for table parent-social.post
CREATE TABLE IF NOT EXISTS `post` (
  `body` longtext,
  `postType` int(11) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `community_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_66xeneooiiqbycn7fq1ujovs6` (`community_id`),
  KEY `FK_ivsybk5l0421bcegcgjh4bnwh` (`id`),
  CONSTRAINT `FK_66xeneooiiqbycn7fq1ujovs6` FOREIGN KEY (`community_id`) REFERENCES `community` (`id`),
  CONSTRAINT `FK_ivsybk5l0421bcegcgjh4bnwh` FOREIGN KEY (`id`) REFERENCES `socialobject` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.post: ~6 rows (approximately)
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
INSERT INTO `post` (`body`, `postType`, `id`, `community_id`) VALUES
	('Hello Community 1, Post 2', 1, 69, 18),
	('Hello Community 1, Post 2', 1, 70, 18),
	('Hello Community 1', 1, 71, 18),
	('Hello Community 2, Post 1', 1, 72, 19),
	('Hello Community 2', 1, 73, 19),
	('Hello Community 1', 1, 74, 18);
/*!40000 ALTER TABLE `post` ENABLE KEYS */;


-- Dumping structure for table parent-social.post_comment
CREATE TABLE IF NOT EXISTS `post_comment` (
  `Post_id` bigint(20) NOT NULL,
  `comments_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Post_id`,`comments_id`),
  UNIQUE KEY `UK_5nd2ucnctmvwq31ovab2vi00x` (`comments_id`),
  KEY `FK_5nd2ucnctmvwq31ovab2vi00x` (`comments_id`),
  KEY `FK_er6ity1dxt1bobfug8s7ibn2o` (`Post_id`),
  CONSTRAINT `FK_5nd2ucnctmvwq31ovab2vi00x` FOREIGN KEY (`comments_id`) REFERENCES `comment` (`id`),
  CONSTRAINT `FK_er6ity1dxt1bobfug8s7ibn2o` FOREIGN KEY (`Post_id`) REFERENCES `post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.post_comment: ~0 rows (approximately)
/*!40000 ALTER TABLE `post_comment` DISABLE KEYS */;
INSERT INTO `post_comment` (`Post_id`, `comments_id`) VALUES
	(69, 75),
	(69, 76),
	(69, 77),
	(70, 78),
	(71, 79),
	(73, 80),
	(73, 81),
	(74, 82),
	(74, 83);
/*!40000 ALTER TABLE `post_comment` ENABLE KEYS */;


-- Dumping structure for table parent-social.questions
CREATE TABLE IF NOT EXISTS `questions` (
  `body` longtext,
  `id` bigint(20) NOT NULL,
  `community_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_8ke8lcgk69gkxd75p6abtx323` (`community_id`),
  KEY `FK_5qeq81e7ybyahkx95bbxwe47m` (`id`),
  CONSTRAINT `FK_5qeq81e7ybyahkx95bbxwe47m` FOREIGN KEY (`id`) REFERENCES `socialobject` (`id`),
  CONSTRAINT `FK_8ke8lcgk69gkxd75p6abtx323` FOREIGN KEY (`community_id`) REFERENCES `community` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.questions: ~0 rows (approximately)
/*!40000 ALTER TABLE `questions` DISABLE KEYS */;
/*!40000 ALTER TABLE `questions` ENABLE KEYS */;


-- Dumping structure for table parent-social.questions_comment
CREATE TABLE IF NOT EXISTS `questions_comment` (
  `Questions_id` bigint(20) NOT NULL,
  `comments_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Questions_id`,`comments_id`),
  UNIQUE KEY `UK_ffp8kmcydxltdcpgp433k1kn0` (`comments_id`),
  KEY `FK_ffp8kmcydxltdcpgp433k1kn0` (`comments_id`),
  KEY `FK_iqrgcbmss2fhiv06253faf9rs` (`Questions_id`),
  CONSTRAINT `FK_ffp8kmcydxltdcpgp433k1kn0` FOREIGN KEY (`comments_id`) REFERENCES `comment` (`id`),
  CONSTRAINT `FK_iqrgcbmss2fhiv06253faf9rs` FOREIGN KEY (`Questions_id`) REFERENCES `questions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.questions_comment: ~0 rows (approximately)
/*!40000 ALTER TABLE `questions_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `questions_comment` ENABLE KEYS */;


-- Dumping structure for table parent-social.resource
CREATE TABLE IF NOT EXISTS `resource` (
  `description` longtext,
  `priority` int(11) DEFAULT NULL,
  `resourceName` varchar(255) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `folder_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_4g66gc49k78dib7s9lg1j5glg` (`folder_id`),
  KEY `FK_i40ciggob8g05377l8xdptx59` (`id`),
  CONSTRAINT `FK_4g66gc49k78dib7s9lg1j5glg` FOREIGN KEY (`folder_id`) REFERENCES `folder` (`id`),
  CONSTRAINT `FK_i40ciggob8g05377l8xdptx59` FOREIGN KEY (`id`) REFERENCES `socialobject` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.resource: ~22 rows (approximately)
/*!40000 ALTER TABLE `resource` DISABLE KEYS */;
INSERT INTO `resource` (`description`, `priority`, `resourceName`, `id`, `folder_id`) VALUES
	('album.photo-profile.description', 1, 'people1.jpg', 26, 25),
	('album.photo-profile.description', 1, 'people2.jpg', 28, 27),
	('album.photo-profile.description', 1, 'people3.jpg', 30, 29),
	('album.photo-profile.description', 1, 'people4.jpg', 32, 31),
	('album.photo-profile.description', 1, 'people3.jpg', 34, 33),
	('album.photo-profile.description', 1, 'people2.jpg', 36, 35),
	('album.photo-profile.description', 1, 'people1.jpg', 38, 37),
	('album.photo-profile.description', 1, 'profile2.jpg', 40, 39),
	('album.photo-profile.description', 1, 'profile.jpg', 42, 41),
	('album.photo-profile.description', 1, 'profile2.jpg', 44, 43),
	('album.photo-profile.description', 1, 'profile.jpg', 46, 45),
	('album.photo-profile.description', 1, 'profile2.jpg', 48, 47),
	('album.photo-profile.description', 1, 'profile.jpg', 50, 49),
	('album.photo-profile.description', 1, 'profile2.jpg', 52, 51),
	('album.photo-profile.description', 1, 'profile.jpg', 54, 53),
	('album.photo-profile.description', 1, 'profile2.jpg', 56, 55),
	('album.photo-profile.description', 1, 'profile.jpg', 58, 57),
	('album.photo-profile.description', 1, 'Winter.jpg', 60, 59),
	('album.photo-cover.description', 1, 'Blue hills.jpg', 62, 61),
	('album.photo-cover.description', 1, 'Winter.jpg', 64, 63),
	('album.photo-profile.description', 1, 'Sunset.jpg', 66, 65),
	('album.photo-cover.description', 1, 'Blue hills.jpg', 68, 67);
/*!40000 ALTER TABLE `resource` ENABLE KEYS */;


-- Dumping structure for table parent-social.resource_comment
CREATE TABLE IF NOT EXISTS `resource_comment` (
  `Resource_id` bigint(20) NOT NULL,
  `comments_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Resource_id`,`comments_id`),
  UNIQUE KEY `UK_7x62qol9cvb8sk2c966r4ko30` (`comments_id`),
  KEY `FK_7x62qol9cvb8sk2c966r4ko30` (`comments_id`),
  KEY `FK_71n7k767wo3cwr4e5sg2wo3xq` (`Resource_id`),
  CONSTRAINT `FK_71n7k767wo3cwr4e5sg2wo3xq` FOREIGN KEY (`Resource_id`) REFERENCES `resource` (`id`),
  CONSTRAINT `FK_7x62qol9cvb8sk2c966r4ko30` FOREIGN KEY (`comments_id`) REFERENCES `comment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.resource_comment: ~0 rows (approximately)
/*!40000 ALTER TABLE `resource_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `resource_comment` ENABLE KEYS */;


-- Dumping structure for table parent-social.securityrole
CREATE TABLE IF NOT EXISTS `securityrole` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `roleName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.securityrole: ~1 rows (approximately)
/*!40000 ALTER TABLE `securityrole` DISABLE KEYS */;
INSERT INTO `securityrole` (`id`, `CREATED_BY`, `CREATED_DATE`, `UPDATED_BY`, `UPDATED_DATE`, `roleName`) VALUES
	(1, NULL, NULL, NULL, NULL, 'user');
/*!40000 ALTER TABLE `securityrole` ENABLE KEYS */;


-- Dumping structure for table parent-social.socialobject
CREATE TABLE IF NOT EXISTS `socialobject` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `objectType` varchar(255) DEFAULT NULL,
  `owner_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_bvxe4ddjag6a50c508c0c6ebi` (`owner_id`),
  CONSTRAINT `FK_bvxe4ddjag6a50c508c0c6ebi` FOREIGN KEY (`owner_id`) REFERENCES `socialobject` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.socialobject: ~74 rows (approximately)
/*!40000 ALTER TABLE `socialobject` DISABLE KEYS */;
INSERT INTO `socialobject` (`id`, `CREATED_BY`, `CREATED_DATE`, `UPDATED_BY`, `UPDATED_DATE`, `name`, `objectType`, `owner_id`) VALUES
	(1, 'TODO', '2014-03-25 14:51:55', NULL, NULL, 'Sharad P', 'USER', NULL),
	(2, 'TODO', '2014-03-25 14:51:57', NULL, NULL, 'Harshad G', 'USER', NULL),
	(3, 'TODO', '2014-03-25 14:51:58', NULL, NULL, 'Pravin B', 'USER', NULL),
	(4, 'TODO', '2014-03-25 14:51:59', NULL, NULL, 'Ankush P', 'USER', NULL),
	(5, 'TODO', '2014-03-25 14:52:00', NULL, NULL, 'Pankaj B', 'USER', NULL),
	(6, 'TODO', '2014-03-25 14:52:01', NULL, NULL, 'Tushar G', 'USER', NULL),
	(7, 'TODO', '2014-03-25 14:52:02', NULL, NULL, 'Dherej b', 'USER', NULL),
	(8, 'TODO', '2014-03-25 14:52:03', NULL, NULL, 'Deepak b', 'USER', NULL),
	(9, 'TODO', '2014-03-25 14:52:04', NULL, NULL, 'Nagesh D', 'USER', NULL),
	(10, 'TODO', '2014-03-25 14:52:05', NULL, NULL, 'Jagbir P', 'USER', NULL),
	(11, 'TODO', '2014-03-25 14:52:06', NULL, NULL, 'Ashish D', 'USER', NULL),
	(12, 'TODO', '2014-03-25 14:52:07', NULL, NULL, 'Cheatan G', 'USER', NULL),
	(13, 'TODO', '2014-03-25 14:52:08', NULL, NULL, 'Ajinkya G', 'USER', NULL),
	(14, 'TODO', '2014-03-25 14:52:09', NULL, NULL, 'Pushkar B', 'USER', NULL),
	(15, 'TODO', '2014-03-25 14:52:10', NULL, NULL, 'Dhananjay b', 'USER', NULL),
	(16, 'TODO', '2014-03-25 14:52:11', NULL, NULL, 'amit G', 'USER', NULL),
	(17, 'TODO', '2014-03-25 14:52:11', NULL, NULL, 'Harbir P', 'USER', NULL),
	(18, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'Test Group1', 'COMMUNITY', 6),
	(19, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'Test Group2', 'COMMUNITY', 5),
	(20, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'Test Group3', 'COMMUNITY', 10),
	(21, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'Test Group4', 'COMMUNITY', 13),
	(22, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'Test Group5', 'COMMUNITY', 4),
	(23, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'Test Group6', 'COMMUNITY', 11),
	(24, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'Test Group7', 'COMMUNITY', 15),
	(25, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'profile', 'FOLDER', 21),
	(26, 'TODO', '2014-03-25 14:52:39', NULL, NULL, NULL, 'PHOTO', 21),
	(27, 'TODO', '2014-03-25 14:52:40', NULL, NULL, 'profile', 'FOLDER', 22),
	(28, 'TODO', '2014-03-25 14:52:40', NULL, NULL, NULL, 'PHOTO', 22),
	(29, 'TODO', '2014-03-25 14:52:40', NULL, NULL, 'profile', 'FOLDER', 20),
	(30, 'TODO', '2014-03-25 14:52:40', NULL, NULL, NULL, 'PHOTO', 20),
	(31, 'TODO', '2014-03-25 14:52:40', NULL, NULL, 'profile', 'FOLDER', 24),
	(32, 'TODO', '2014-03-25 14:52:40', NULL, NULL, NULL, 'PHOTO', 24),
	(33, 'TODO', '2014-03-25 14:52:40', NULL, NULL, 'profile', 'FOLDER', 23),
	(34, 'TODO', '2014-03-25 14:52:40', NULL, NULL, NULL, 'PHOTO', 23),
	(35, 'TODO', '2014-03-25 14:52:40', NULL, NULL, 'profile', 'FOLDER', 19),
	(36, 'TODO', '2014-03-25 14:52:40', NULL, NULL, NULL, 'PHOTO', 19),
	(37, 'TODO', '2014-03-25 14:52:40', NULL, NULL, 'profile', 'FOLDER', 18),
	(38, 'TODO', '2014-03-25 14:52:40', NULL, NULL, NULL, 'PHOTO', 18),
	(39, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'profile', 'FOLDER', 4),
	(40, 'TODO', '2014-03-25 14:52:53', NULL, NULL, NULL, 'PHOTO', 4),
	(41, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'profile', 'FOLDER', 13),
	(42, 'TODO', '2014-03-25 14:52:53', NULL, NULL, NULL, 'PHOTO', 13),
	(43, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'profile', 'FOLDER', 11),
	(44, 'TODO', '2014-03-25 14:52:53', NULL, NULL, NULL, 'PHOTO', 11),
	(45, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'profile', 'FOLDER', 15),
	(46, 'TODO', '2014-03-25 14:52:53', NULL, NULL, NULL, 'PHOTO', 15),
	(47, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'profile', 'FOLDER', 6),
	(48, 'TODO', '2014-03-25 14:52:53', NULL, NULL, NULL, 'PHOTO', 6),
	(49, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'profile', 'FOLDER', 5),
	(50, 'TODO', '2014-03-25 14:52:53', NULL, NULL, NULL, 'PHOTO', 5),
	(51, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'profile', 'FOLDER', 8),
	(52, 'TODO', '2014-03-25 14:52:53', NULL, NULL, NULL, 'PHOTO', 8),
	(53, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'profile', 'FOLDER', 12),
	(54, 'TODO', '2014-03-25 14:52:53', NULL, NULL, NULL, 'PHOTO', 12),
	(55, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'profile', 'FOLDER', 14),
	(56, 'TODO', '2014-03-25 14:52:53', NULL, NULL, NULL, 'PHOTO', 14),
	(57, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'profile', 'FOLDER', 1),
	(58, 'TODO', '2014-03-25 14:52:53', NULL, NULL, NULL, 'PHOTO', 1),
	(59, 'TODO', '2014-03-25 14:54:03', NULL, NULL, 'profile', 'FOLDER', 10),
	(60, 'TODO', '2014-03-25 14:54:03', NULL, NULL, NULL, 'PHOTO', 10),
	(61, 'TODO', '2014-03-25 14:54:14', NULL, NULL, 'cover', 'FOLDER', 10),
	(62, 'TODO', '2014-03-25 14:54:14', NULL, NULL, NULL, 'PHOTO', 10),
	(63, 'TODO', '2014-03-25 15:09:49', NULL, NULL, 'cover', 'FOLDER', 15),
	(64, 'TODO', '2014-03-25 15:09:49', NULL, NULL, NULL, 'PHOTO', 15),
	(65, 'TODO', '2014-03-25 15:32:31', NULL, NULL, 'profile', 'FOLDER', 7),
	(66, 'TODO', '2014-03-25 15:32:31', NULL, NULL, NULL, 'PHOTO', 7),
	(67, 'TODO', '2014-03-25 15:32:35', NULL, NULL, 'cover', 'FOLDER', 7),
	(68, 'TODO', '2014-03-25 15:32:35', NULL, NULL, NULL, 'PHOTO', 7),
	(69, 'TODO', '2014-03-25 21:08:49', NULL, NULL, NULL, 'POST', 4),
	(70, 'TODO', '2014-03-25 21:08:49', NULL, NULL, NULL, 'POST', 4),
	(71, 'TODO', '2014-03-25 21:08:49', NULL, NULL, NULL, 'POST', 4),
	(72, 'TODO', '2014-03-25 21:08:49', NULL, NULL, NULL, 'POST', 4),
	(73, 'TODO', '2014-03-25 21:08:49', NULL, NULL, NULL, 'POST', 10),
	(74, 'TODO', '2014-03-25 21:08:49', NULL, NULL, NULL, 'POST', 10),
	(75, 'TODO', '2014-03-26 16:19:36', NULL, NULL, NULL, NULL, 4),
	(76, 'TODO', '2014-03-26 16:19:37', NULL, NULL, NULL, NULL, 10),
	(77, 'TODO', '2014-03-26 16:19:37', NULL, NULL, NULL, NULL, 5),
	(78, 'TODO', '2014-03-26 16:19:37', NULL, NULL, NULL, NULL, 4),
	(79, 'TODO', '2014-03-26 16:19:37', NULL, NULL, NULL, NULL, 5),
	(80, 'TODO', '2014-03-26 18:57:14', NULL, NULL, NULL, NULL, 5),
	(81, 'TODO', '2014-03-26 18:57:14', NULL, NULL, NULL, NULL, 4),
	(82, 'TODO', '2014-03-26 18:59:17', NULL, NULL, NULL, NULL, 5),
	(83, 'TODO', '2014-03-26 18:59:17', NULL, NULL, NULL, NULL, 4);
/*!40000 ALTER TABLE `socialobject` ENABLE KEYS */;


-- Dumping structure for table parent-social.socialrelation
CREATE TABLE IF NOT EXISTS `socialrelation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `action` varchar(255) DEFAULT NULL,
  `actionType` varchar(255) DEFAULT NULL,
  `relationWeight` int(11) DEFAULT NULL,
  `actor_id` bigint(20) DEFAULT NULL,
  `target_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_iegw3vajbxb4o3qgqil49i7y6` (`actor_id`),
  KEY `FK_ptd9ked1m9bt5au8tp8f4vu5p` (`target_id`),
  CONSTRAINT `FK_iegw3vajbxb4o3qgqil49i7y6` FOREIGN KEY (`actor_id`) REFERENCES `socialobject` (`id`),
  CONSTRAINT `FK_ptd9ked1m9bt5au8tp8f4vu5p` FOREIGN KEY (`target_id`) REFERENCES `socialobject` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.socialrelation: ~47 rows (approximately)
/*!40000 ALTER TABLE `socialrelation` DISABLE KEYS */;
INSERT INTO `socialrelation` (`id`, `CREATED_BY`, `CREATED_DATE`, `UPDATED_BY`, `UPDATED_DATE`, `action`, `actionType`, `relationWeight`, `actor_id`, `target_id`) VALUES
	(1, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'MEMBER', NULL, NULL, 13, 21),
	(2, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'MEMBER', NULL, NULL, 4, 22),
	(3, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'MEMBER', NULL, NULL, 10, 20),
	(4, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'MEMBER', NULL, NULL, 15, 24),
	(5, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'MEMBER', NULL, NULL, 11, 23),
	(6, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'MEMBER', NULL, NULL, 5, 19),
	(7, 'TODO', '2014-03-25 14:52:39', NULL, NULL, 'MEMBER', NULL, NULL, 6, 18),
	(8, 'TODO', '2014-03-25 14:52:40', NULL, NULL, 'ADDED', NULL, NULL, 21, 25),
	(9, 'TODO', '2014-03-25 14:52:40', NULL, NULL, 'ADDED', NULL, NULL, 22, 27),
	(10, 'TODO', '2014-03-25 14:52:40', NULL, NULL, 'ADDED', NULL, NULL, 20, 29),
	(11, 'TODO', '2014-03-25 14:52:40', NULL, NULL, 'ADDED', NULL, NULL, 24, 31),
	(12, 'TODO', '2014-03-25 14:52:40', NULL, NULL, 'ADDED', NULL, NULL, 23, 33),
	(13, 'TODO', '2014-03-25 14:52:40', NULL, NULL, 'ADDED', NULL, NULL, 19, 35),
	(14, 'TODO', '2014-03-25 14:52:40', NULL, NULL, 'ADDED', NULL, NULL, 18, 37),
	(15, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'ADDED', NULL, NULL, 4, 39),
	(16, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'ADDED', NULL, NULL, 13, 41),
	(17, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'ADDED', NULL, NULL, 11, 43),
	(18, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'ADDED', NULL, NULL, 15, 45),
	(19, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'ADDED', NULL, NULL, 6, 47),
	(20, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'ADDED', NULL, NULL, 5, 49),
	(21, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'ADDED', NULL, NULL, 8, 51),
	(22, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'ADDED', NULL, NULL, 12, 53),
	(23, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'ADDED', NULL, NULL, 14, 55),
	(24, 'TODO', '2014-03-25 14:52:53', NULL, NULL, 'ADDED', NULL, NULL, 1, 57),
	(25, 'TODO', '2014-03-25 14:52:53', 'TODO', '2014-03-25 14:52:53', NULL, 'UNFRIEND', NULL, 4, 10),
	(26, 'TODO', '2014-03-25 14:52:53', 'TODO', '2014-03-25 14:52:53', 'FRIEND', NULL, NULL, 13, 10),
	(27, 'TODO', '2014-03-25 14:52:53', 'TODO', '2014-03-25 14:52:53', 'FRIEND', NULL, NULL, 11, 10),
	(28, 'TODO', '2014-03-25 14:52:53', 'TODO', '2014-03-25 14:52:53', NULL, 'UNFRIEND', NULL, 15, 10),
	(29, 'TODO', '2014-03-25 14:52:53', 'TODO', '2014-03-25 14:52:53', 'FRIEND', NULL, NULL, 6, 10),
	(30, 'TODO', '2014-03-25 14:52:53', 'TODO', '2014-03-25 14:52:53', 'FRIEND', NULL, NULL, 5, 10),
	(31, 'TODO', '2014-03-25 14:52:53', 'TODO', '2014-03-25 14:52:53', 'FRIEND', NULL, NULL, 8, 10),
	(32, 'TODO', '2014-03-25 14:52:53', 'TODO', '2014-03-25 14:52:53', 'FRIEND', NULL, NULL, 12, 10),
	(33, 'TODO', '2014-03-25 14:52:53', 'TODO', '2014-03-25 14:52:53', 'FRIEND', NULL, NULL, 14, 10),
	(34, 'TODO', '2014-03-25 14:52:53', 'TODO', '2014-03-25 14:52:53', 'FRIEND', NULL, NULL, 1, 10),
	(35, 'TODO', '2014-03-25 14:54:03', NULL, NULL, 'ADDED', NULL, NULL, 10, 59),
	(36, 'TODO', '2014-03-25 14:54:14', NULL, NULL, 'ADDED', NULL, NULL, 10, 61),
	(37, 'TODO', '2014-03-25 15:09:49', NULL, NULL, 'ADDED', NULL, NULL, 15, 63),
	(38, 'TODO', '2014-03-25 15:32:31', NULL, NULL, 'ADDED', NULL, NULL, 7, 65),
	(39, 'TODO', '2014-03-25 15:32:35', NULL, NULL, 'ADDED', NULL, NULL, 7, 67),
	(40, 'TODO', '2014-03-25 15:34:14', NULL, NULL, NULL, 'FRIEND_REQUESTED', NULL, 7, 10),
	(41, 'TODO', '2014-03-25 19:53:00', NULL, NULL, NULL, 'FRIEND_REQUESTED', NULL, 7, 15),
	(42, 'TODO', '2014-03-25 21:08:49', NULL, NULL, 'POSTED', NULL, NULL, 4, 69),
	(43, 'TODO', '2014-03-25 21:08:49', NULL, NULL, 'POSTED', NULL, NULL, 4, 70),
	(44, 'TODO', '2014-03-25 21:08:49', NULL, NULL, 'POSTED', NULL, NULL, 4, 71),
	(45, 'TODO', '2014-03-25 21:08:49', NULL, NULL, 'POSTED', NULL, NULL, 4, 72),
	(46, 'TODO', '2014-03-25 21:08:49', NULL, NULL, 'POSTED', NULL, NULL, 10, 73),
	(47, 'TODO', '2014-03-25 21:08:49', NULL, NULL, 'POSTED', NULL, NULL, 10, 74),
	(48, 'TODO', '2014-03-26 16:19:36', NULL, NULL, 'COMMENTED', NULL, NULL, 4, 69),
	(49, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'COMMENTED', NULL, NULL, 4, 75),
	(50, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'COMMENTED', NULL, NULL, 10, 69),
	(51, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'COMMENTED', NULL, NULL, 10, 76),
	(52, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'COMMENTED', NULL, NULL, 5, 69),
	(53, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'COMMENTED', NULL, NULL, 5, 77),
	(54, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'COMMENTED', NULL, NULL, 4, 70),
	(55, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'COMMENTED', NULL, NULL, 4, 78),
	(56, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'COMMENTED', NULL, NULL, 5, 71),
	(57, 'TODO', '2014-03-26 16:19:37', NULL, NULL, 'COMMENTED', NULL, NULL, 5, 79),
	(58, 'TODO', '2014-03-26 18:57:14', NULL, NULL, 'COMMENTED', NULL, NULL, 5, 73),
	(59, 'TODO', '2014-03-26 18:57:14', NULL, NULL, 'COMMENTED', NULL, NULL, 5, 80),
	(60, 'TODO', '2014-03-26 18:57:14', NULL, NULL, 'COMMENTED', NULL, NULL, 4, 73),
	(61, 'TODO', '2014-03-26 18:57:14', NULL, NULL, 'COMMENTED', NULL, NULL, 4, 81),
	(62, 'TODO', '2014-03-26 18:59:17', NULL, NULL, 'COMMENTED', NULL, NULL, 5, 74),
	(63, 'TODO', '2014-03-26 18:59:17', NULL, NULL, 'COMMENTED', NULL, NULL, 5, 82),
	(64, 'TODO', '2014-03-26 18:59:17', NULL, NULL, 'COMMENTED', NULL, NULL, 4, 74),
	(65, 'TODO', '2014-03-26 18:59:17', NULL, NULL, 'COMMENTED', NULL, NULL, 4, 83);
/*!40000 ALTER TABLE `socialrelation` ENABLE KEYS */;


-- Dumping structure for table parent-social.tokenaction
CREATE TABLE IF NOT EXISTS `tokenaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `CREATED_BY` varchar(255) DEFAULT NULL,
  `CREATED_DATE` datetime DEFAULT NULL,
  `UPDATED_BY` varchar(255) DEFAULT NULL,
  `UPDATED_DATE` datetime DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `expires` datetime DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `targetUser_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_d8u0wwuu9m6ay51v7ja0ae4ar` (`token`),
  KEY `FK_liyigxbh4wmvm8b23wvrdwyd8` (`targetUser_id`),
  CONSTRAINT `FK_liyigxbh4wmvm8b23wvrdwyd8` FOREIGN KEY (`targetUser_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.tokenaction: ~0 rows (approximately)
/*!40000 ALTER TABLE `tokenaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `tokenaction` ENABLE KEYS */;


-- Dumping structure for table parent-social.user
CREATE TABLE IF NOT EXISTS `user` (
  `aboutMe` longtext,
  `active` tinyint(1) NOT NULL,
  `date_of_birth` datetime DEFAULT NULL,
  `displayName` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `emailValidated` tinyint(1) NOT NULL,
  `firstName` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `lastLogin` datetime DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `albumCoverProfile_id` bigint(20) DEFAULT NULL,
  `albumPhotoProfile_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_6otxo1iro9dhyyftxupdxkwcv` (`albumCoverProfile_id`),
  KEY `FK_euwtr6htf2llqdum2br90wvv9` (`albumPhotoProfile_id`),
  KEY `FK_omjl8ijwi58yx57d9ejn4c0jt` (`id`),
  CONSTRAINT `FK_6otxo1iro9dhyyftxupdxkwcv` FOREIGN KEY (`albumCoverProfile_id`) REFERENCES `folder` (`id`),
  CONSTRAINT `FK_euwtr6htf2llqdum2br90wvv9` FOREIGN KEY (`albumPhotoProfile_id`) REFERENCES `folder` (`id`),
  CONSTRAINT `FK_omjl8ijwi58yx57d9ejn4c0jt` FOREIGN KEY (`id`) REFERENCES `socialobject` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.user: ~17 rows (approximately)
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`aboutMe`, `active`, `date_of_birth`, `displayName`, `email`, `emailValidated`, `firstName`, `gender`, `lastLogin`, `lastName`, `location`, `username`, `id`, `albumCoverProfile_id`, `albumPhotoProfile_id`) VALUES
	(NULL, 1, NULL, 'Sharad P', 'jagbir.friend14@test.com', 1, NULL, NULL, '2014-03-25 14:51:55', NULL, NULL, NULL, 1, NULL, 57),
	(NULL, 1, NULL, 'Harshad G', 'jagbir.friend.Request1@test.com', 1, NULL, NULL, '2014-03-25 14:51:57', NULL, NULL, NULL, 2, NULL, NULL),
	(NULL, 1, NULL, 'Pravin B', 'jagbir.friend.Request2@test.com', 1, NULL, NULL, '2014-03-25 14:51:58', NULL, NULL, NULL, 3, NULL, NULL),
	(NULL, 1, NULL, 'Ankush P', 'jagbir.friend4@test.com', 1, NULL, NULL, '2014-03-25 14:51:59', NULL, NULL, NULL, 4, NULL, 39),
	(NULL, 1, NULL, 'Pankaj B', 'jagbir.friend9@test.com', 1, NULL, NULL, '2014-03-25 14:52:00', NULL, NULL, NULL, 5, NULL, 49),
	(NULL, 1, NULL, 'Tushar G', 'jagbir.friend8@test.com', 1, NULL, NULL, '2014-03-25 14:52:01', NULL, NULL, NULL, 6, NULL, 47),
	(NULL, 1, NULL, 'Dherej b', 'jagbir.friend3@test.com', 1, NULL, NULL, '2014-03-26 11:24:00', NULL, NULL, NULL, 7, 67, 65),
	(NULL, 1, NULL, 'Deepak b', 'jagbir.friend11@test.com', 1, NULL, NULL, '2014-03-25 14:52:03', NULL, NULL, NULL, 8, NULL, 51),
	(NULL, 1, NULL, 'Nagesh D', 'jagbir.friend2@test.com', 1, NULL, NULL, '2014-03-25 14:52:04', NULL, NULL, NULL, 9, NULL, NULL),
	(NULL, 1, NULL, 'Jagbir P', 'jagbir.singh@test.com', 1, NULL, NULL, '2014-03-26 15:49:03', NULL, NULL, NULL, 10, 61, 59),
	(NULL, 1, NULL, 'Ashish D', 'jagbir.friend6@test.com', 1, NULL, NULL, '2014-03-25 15:30:39', NULL, NULL, NULL, 11, NULL, 43),
	(NULL, 1, NULL, 'Cheatan G', 'jagbir.friend12@test.com', 1, NULL, NULL, '2014-03-25 14:52:07', NULL, NULL, NULL, 12, NULL, 53),
	(NULL, 1, NULL, 'Ajinkya G', 'jagbir.friend5@test.com', 1, NULL, NULL, '2014-03-25 14:52:08', NULL, NULL, NULL, 13, NULL, 41),
	(NULL, 1, NULL, 'Pushkar B', 'jagbir.friend13@test.com', 1, NULL, NULL, '2014-03-25 14:52:09', NULL, NULL, NULL, 14, NULL, 55),
	(NULL, 1, NULL, 'Dhananjay b', 'jagbir.friend7@test.com', 1, NULL, NULL, '2014-03-25 15:09:13', NULL, NULL, NULL, 15, 63, 45),
	(NULL, 1, NULL, 'amit G', 'jagbir.friend1@test.com', 1, NULL, NULL, '2014-03-25 14:52:11', NULL, NULL, NULL, 16, NULL, NULL),
	(NULL, 1, NULL, 'Harbir P', 'jagbir.father@test.com', 1, NULL, NULL, '2014-03-25 14:52:11', NULL, NULL, NULL, 17, NULL, NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;


-- Dumping structure for table parent-social.userpermission
CREATE TABLE IF NOT EXISTS `userpermission` (
  `id` bigint(20) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.userpermission: ~0 rows (approximately)
/*!40000 ALTER TABLE `userpermission` DISABLE KEYS */;
/*!40000 ALTER TABLE `userpermission` ENABLE KEYS */;


-- Dumping structure for table parent-social.user_album
CREATE TABLE IF NOT EXISTS `user_album` (
  `User_id` bigint(20) NOT NULL,
  `album_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_9iarvqqeintelrj1i1diaeflx` (`album_id`),
  KEY `FK_9iarvqqeintelrj1i1diaeflx` (`album_id`),
  KEY `FK_tmfdpxyi11da6546kx0p12xqh` (`User_id`),
  CONSTRAINT `FK_9iarvqqeintelrj1i1diaeflx` FOREIGN KEY (`album_id`) REFERENCES `album` (`id`),
  CONSTRAINT `FK_tmfdpxyi11da6546kx0p12xqh` FOREIGN KEY (`User_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.user_album: ~0 rows (approximately)
/*!40000 ALTER TABLE `user_album` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_album` ENABLE KEYS */;


-- Dumping structure for table parent-social.user_conversation
CREATE TABLE IF NOT EXISTS `user_conversation` (
  `User_id` bigint(20) NOT NULL,
  `conversation_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_ea98cb8os87ikd4mcv27pivnx` (`conversation_id`),
  KEY `FK_ea98cb8os87ikd4mcv27pivnx` (`conversation_id`),
  KEY `FK_72tfyf5kydbf1dyfcdlvsmvfq` (`User_id`),
  CONSTRAINT `FK_72tfyf5kydbf1dyfcdlvsmvfq` FOREIGN KEY (`User_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_ea98cb8os87ikd4mcv27pivnx` FOREIGN KEY (`conversation_id`) REFERENCES `conversation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.user_conversation: ~0 rows (approximately)
/*!40000 ALTER TABLE `user_conversation` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_conversation` ENABLE KEYS */;


-- Dumping structure for table parent-social.user_folder
CREATE TABLE IF NOT EXISTS `user_folder` (
  `User_id` bigint(20) NOT NULL,
  `folders_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_1xke7ysp7lp7s9x3vnpepkwuw` (`folders_id`),
  KEY `FK_1xke7ysp7lp7s9x3vnpepkwuw` (`folders_id`),
  KEY `FK_37riuogkmyxxv9kjq2uxk3id8` (`User_id`),
  CONSTRAINT `FK_1xke7ysp7lp7s9x3vnpepkwuw` FOREIGN KEY (`folders_id`) REFERENCES `folder` (`id`),
  CONSTRAINT `FK_37riuogkmyxxv9kjq2uxk3id8` FOREIGN KEY (`User_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.user_folder: ~15 rows (approximately)
/*!40000 ALTER TABLE `user_folder` DISABLE KEYS */;
INSERT INTO `user_folder` (`User_id`, `folders_id`) VALUES
	(1, 57),
	(4, 39),
	(5, 49),
	(6, 47),
	(7, 65),
	(7, 67),
	(8, 51),
	(10, 59),
	(10, 61),
	(11, 43),
	(12, 53),
	(13, 41),
	(14, 55),
	(15, 45),
	(15, 63);
/*!40000 ALTER TABLE `user_folder` ENABLE KEYS */;


-- Dumping structure for table parent-social.user_linkedaccount
CREATE TABLE IF NOT EXISTS `user_linkedaccount` (
  `User_id` bigint(20) NOT NULL,
  `linkedAccounts_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_1ap4j3hxsd2vuutufm2kbrv9t` (`linkedAccounts_id`),
  KEY `FK_1ap4j3hxsd2vuutufm2kbrv9t` (`linkedAccounts_id`),
  KEY `FK_6bcxwhi4mg8o4d5qs7wqlr3y1` (`User_id`),
  CONSTRAINT `FK_1ap4j3hxsd2vuutufm2kbrv9t` FOREIGN KEY (`linkedAccounts_id`) REFERENCES `linkedaccount` (`id`),
  CONSTRAINT `FK_6bcxwhi4mg8o4d5qs7wqlr3y1` FOREIGN KEY (`User_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.user_linkedaccount: ~17 rows (approximately)
/*!40000 ALTER TABLE `user_linkedaccount` DISABLE KEYS */;
INSERT INTO `user_linkedaccount` (`User_id`, `linkedAccounts_id`) VALUES
	(1, 1),
	(2, 2),
	(3, 3),
	(4, 4),
	(5, 5),
	(6, 6),
	(7, 7),
	(8, 8),
	(9, 9),
	(10, 10),
	(11, 11),
	(12, 12),
	(13, 13),
	(14, 14),
	(15, 15),
	(16, 16),
	(17, 17);
/*!40000 ALTER TABLE `user_linkedaccount` ENABLE KEYS */;


-- Dumping structure for table parent-social.user_securityrole
CREATE TABLE IF NOT EXISTS `user_securityrole` (
  `User_id` bigint(20) NOT NULL,
  `roles_id` bigint(20) NOT NULL,
  KEY `FK_99o8hnxtx05oaqc4e4bkulcbg` (`roles_id`),
  KEY `FK_4m593tv6uv1j5l8maxvvq8uod` (`User_id`),
  CONSTRAINT `FK_4m593tv6uv1j5l8maxvvq8uod` FOREIGN KEY (`User_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_99o8hnxtx05oaqc4e4bkulcbg` FOREIGN KEY (`roles_id`) REFERENCES `securityrole` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.user_securityrole: ~17 rows (approximately)
/*!40000 ALTER TABLE `user_securityrole` DISABLE KEYS */;
INSERT INTO `user_securityrole` (`User_id`, `roles_id`) VALUES
	(1, 1),
	(2, 1),
	(3, 1),
	(4, 1),
	(5, 1),
	(6, 1),
	(7, 1),
	(8, 1),
	(9, 1),
	(10, 1),
	(11, 1),
	(12, 1),
	(13, 1),
	(14, 1),
	(15, 1),
	(16, 1),
	(17, 1);
/*!40000 ALTER TABLE `user_securityrole` ENABLE KEYS */;


-- Dumping structure for table parent-social.user_userpermission
CREATE TABLE IF NOT EXISTS `user_userpermission` (
  `User_id` bigint(20) NOT NULL,
  `permissions_id` bigint(20) NOT NULL,
  KEY `FK_jqg5r75lvy97j5e0ufu1yc4n1` (`permissions_id`),
  KEY `FK_kqt2th2a783n7o5u609dpi6mc` (`User_id`),
  CONSTRAINT `FK_jqg5r75lvy97j5e0ufu1yc4n1` FOREIGN KEY (`permissions_id`) REFERENCES `userpermission` (`id`),
  CONSTRAINT `FK_kqt2th2a783n7o5u609dpi6mc` FOREIGN KEY (`User_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.user_userpermission: ~0 rows (approximately)
/*!40000 ALTER TABLE `user_userpermission` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_userpermission` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
