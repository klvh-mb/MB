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
(1,'tag_1','COMMUNITY_TAG','/assets/app/images/general/icons/tag_1.png'),                                                                           
(2,'tag_2','COMMUNITY_TAG','/assets/app/images/general/icons/tag_2.png'),  
(3,'tag_3','COMMUNITY_TAG','/assets/app/images/general/icons/tag_3.png'),  
(4,'tag_4','COMMUNITY_TAG','/assets/app/images/general/icons/tag_4.png'),
(5,'tag_5','COMMUNITY_TAG','/assets/app/images/general/icons/tag_5.png'),  
(6,'tag_6','COMMUNITY_TAG','/assets/app/images/general/icons/tag_6.png'),
(7,'zodiac_rat','COMMUNITY_ZODIAC','/assets/app/images/general/icons/zodiac/rat.png'),                                                                           
(8,'zodiac_ox','COMMUNITY_ZODIAC','/assets/app/images/general/icons/zodiac/ox.png'),  
(9,'zodiac_tiger','COMMUNITY_ZODIAC','/assets/app/images/general/icons/zodiac/tiger.png'),  
(10,'zodiac_rabbit','COMMUNITY_ZODIAC','/assets/app/images/general/icons/zodiac/rabbit.png'),  
(11,'zodiac_dragon','COMMUNITY_ZODIAC','/assets/app/images/general/icons/zodiac/dragon.png'),  
(12,'zodiac_snake','COMMUNITY_ZODIAC','/assets/app/images/general/icons/zodiac/snake.png'),  
(13,'zodiac_horse','COMMUNITY_ZODIAC','/assets/app/images/general/icons/zodiac/horse.png'),  
(14,'zodiac_goat','COMMUNITY_ZODIAC','/assets/app/images/general/icons/zodiac/goat.png'),  
(15,'zodiac_monkey','COMMUNITY_ZODIAC','/assets/app/images/general/icons/zodiac/monkey.png'),  
(16,'zodiac_rooster','COMMUNITY_ZODIAC','/assets/app/images/general/icons/zodiac/rooster.png'),  
(17,'zodiac_dog','COMMUNITY_ZODIAC','/assets/app/images/general/icons/zodiac/dog.png'),  
(18,'zodiac_pig','COMMUNITY_ZODIAC','/assets/app/images/general/icons/zodiac/pig.png');
/*!40000 ALTER TABLE `Icon` ENABLE KEYS */;
/*!40014 SET FOREIGN_KEY_CHECKS=1 */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
