-- --------------------------------------------------------
-- Host:                         localhost
-- Server version:               5.0.90-community-nt - MySQL Community Edition (GPL)
-- Server OS:                    Win32
-- HeidiSQL version:             6.0.0.4004
-- Date/time:                    2014-05-10 09:10:59
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;

-- Dumping structure for table parent-social.Icon
-- DROP TABLE IF EXISTS `Icon`;
CREATE TABLE IF NOT EXISTS `Icon` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `iconType` varchar(255) default NULL,
  `url` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- Dumping data for table parent-social.Icon: ~5 rows (approximately)
/*!40000 ALTER TABLE `Icon` DISABLE KEYS */;
insert into `Icon` (`id`,`name`,`iconType`,`url`) values
(1,'zodiac_rat','COMMUNITY_ZODIAC','/assets/app/images/general/icon_png/zodiac/rat.png'),                                                                           
(2,'zodiac_ox','COMMUNITY_ZODIAC','/assets/app/images/general/icon_png/zodiac/ox.png'),  
(3,'zodiac_tiger','COMMUNITY_ZODIAC','/assets/app/images/general/icon_png/zodiac/tiger.png'),  
(4,'zodiac_rabbit','COMMUNITY_ZODIAC','/assets/app/images/general/icon_png/zodiac/rabbit.png'),  
(5,'zodiac_dragon','COMMUNITY_ZODIAC','/assets/app/images/general/icon_png/zodiac/dragon.png'),  
(6,'zodiac_snake','COMMUNITY_ZODIAC','/assets/app/images/general/icon_png/zodiac/snake.png'),  
(7,'zodiac_horse','COMMUNITY_ZODIAC','/assets/app/images/general/icon_png/zodiac/horse.png'),  
(8,'zodiac_goat','COMMUNITY_ZODIAC','/assets/app/images/general/icon_png/zodiac/goat.png'),  
(9,'zodiac_monkey','COMMUNITY_ZODIAC','/assets/app/images/general/icon_png/zodiac/monkey.png'),  
(10,'zodiac_rooster','COMMUNITY_ZODIAC','/assets/app/images/general/icon_png/zodiac/rooster.png'),  
(11,'zodiac_dog','COMMUNITY_ZODIAC','/assets/app/images/general/icon_png/zodiac/dog.png'),  
(12,'zodiac_pig','COMMUNITY_ZODIAC','/assets/app/images/general/icon_png/zodiac/pig.png'),  
(13,'親子資訊','ARTICLE_CATEGORY','/assets/app/icons/info.png'),                                                                           
(14,'家長必讀','ARTICLE_CATEGORY','/assets/app/icons/info.png'),  
(15,'教養專題','ARTICLE_CATEGORY','/assets/app/icons/info.png'),  
(16,'分享專區','ARTICLE_CATEGORY','/assets/app/icons/info.png');
/*!40000 ALTER TABLE `Icon` ENABLE KEYS */;
/*!40014 SET FOREIGN_KEY_CHECKS=1 */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
