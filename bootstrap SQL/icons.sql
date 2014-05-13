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

-- Dumping structure for table parent-social.Icons
DROP TABLE IF EXISTS `Icons`;
CREATE TABLE IF NOT EXISTS `Icons` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `url` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- Dumping data for table parent-social.Icons: ~5 rows (approximately)
/*!40000 ALTER TABLE `Icons` DISABLE KEYS */;
insert into `Icons` (`name`, `url`) values
('親子資訊','/assets/app/icons/info.png'),                                                                           
('家長必讀','/assets/app/icons/info.png'),  
('教養專題','/assets/app/icons/info.png'),  
('分享專區','/assets/app/icons/info.png');
/*!40000 ALTER TABLE `Icons` ENABLE KEYS */;
/*!40014 SET FOREIGN_KEY_CHECKS=1 */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
