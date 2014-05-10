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

-- Dumping structure for table parent-social.icons
CREATE TABLE IF NOT EXISTS `icons` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `url` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

-- Dumping data for table parent-social.icons: ~5 rows (approximately)
/*!40000 ALTER TABLE `icons` DISABLE KEYS */;
INSERT INTO `icons` (`id`, `name`, `url`) VALUES
	(1, 'Football', '/assets/app/icons/common.png'),
	(2, 'School', '/assets/app/icons/info.png'),
	(3, 'Employee', '/assets/app/icons/legan.png'),
	(4, 'Play', '/assets/app/icons/partners.png'),
	(5, 'Office', '/assets/app/icons/contact.png');
/*!40000 ALTER TABLE `icons` ENABLE KEYS */;
/*!40014 SET FOREIGN_KEY_CHECKS=1 */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
