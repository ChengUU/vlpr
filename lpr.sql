-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: lpr
-- ------------------------------------------------------
-- Server version	5.7.18-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chartemplate`
--

DROP TABLE IF EXISTS `chartemplate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chartemplate` (
  `id` int(12) NOT NULL AUTO_INCREMENT,
  `typeid` int(12) NOT NULL,
  `width` int(12) NOT NULL,
  `height` int(12) NOT NULL,
  `path` varchar(128) DEFAULT NULL,
  `repChar` varchar(2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path_UNIQUE` (`path`),
  KEY `aaa_idx` (`typeid`),
  CONSTRAINT `aaa` FOREIGN KEY (`typeid`) REFERENCES `templatetype` (`typeNum`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chartemplate`
--

LOCK TABLES `chartemplate` WRITE;
/*!40000 ALTER TABLE `chartemplate` DISABLE KEYS */;
INSERT INTO `chartemplate` VALUES (1,3,45,90,'NUM_002.png','0'),(2,3,45,90,'NUM_102.png','1'),(3,3,45,90,'NUM_203.png','2'),(4,3,45,90,'NUM_302.png','3'),(5,3,45,90,'NUM_402.png','4'),(6,3,45,90,'NUM_501.png','5'),(7,3,45,90,'NUM_607.png','6'),(8,3,45,90,'NUM_701.png','7'),(9,3,45,90,'NUM_803.png','8'),(10,3,45,90,'NUM_902.png','9'),(11,2,45,90,'ALPHA_A02.png','A'),(12,2,45,90,'ALPHA_B03.png','B'),(13,2,45,90,'ALPHA_C02.png','C'),(14,2,45,90,'ALPHA_D02.png','D'),(15,2,45,90,'ALPHA_E01.png','E'),(16,2,45,90,'ALPHA_F01.png','F'),(17,2,45,90,'ALPHA_G01.png','G'),(18,2,45,90,'ALPHA_H01.png','H'),(19,2,45,90,'ALPHA_J01.png','J'),(20,2,45,90,'ALPHA_K01.png','K'),(21,2,45,90,'ALPHA_L01.png','L'),(22,2,45,90,'ALPHA_M01.png','M'),(23,2,45,90,'ALPHA_N03.png','N'),(24,2,45,90,'ALPHA_Q01.png','Q'),(25,2,45,90,'ALPHA_S01.png','S'),(26,2,45,90,'ALPHA_T01.png','T'),(27,2,45,90,'ALPHA_X01.png','X'),(28,2,45,90,'ALPHA_U02.png','U'),(29,2,45,90,'ALPHA_V01.png','V'),(30,2,45,90,'ALPHA_W01.png','W'),(31,2,45,90,'ALPHA_Y01.png','Y'),(32,2,45,90,'ALPHA_Z02.png','Z'),(33,2,45,90,'ALPHA_R01.png','R'),(34,1,45,90,'CHN_CHUAN02.png','川'),(35,1,45,90,'CHN_E02.png','鄂'),(36,1,45,90,'CHN_JIN02.png','晋'),(37,1,45,90,'CHN_CHUAN09.png','川'),(38,1,45,90,'CHN_CHUAN06.png','川'),(39,3,45,90,'NUM_001.png','0'),(40,3,45,90,'NUM_004.png','0'),(41,2,45,90,'ALPHA_P01.png','P'),(42,2,45,90,'ALPHA_A16.png','A'),(50,1,45,90,'CHN_YU01.png','渝'),(53,1,45,90,'CHN_YU02.png','渝'),(55,1,45,90,'CHN_SU01.png','苏'),(56,1,45,90,'CHN_ZHE01.png','浙'),(57,1,45,90,'CHN_JING01.png','京');
/*!40000 ALTER TABLE `chartemplate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `templatetype`
--

DROP TABLE IF EXISTS `templatetype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `templatetype` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `typeNum` int(12) NOT NULL DEFAULT '1',
  `typename` varchar(5) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `un_type` (`typeNum`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `templatetype`
--

LOCK TABLES `templatetype` WRITE;
/*!40000 ALTER TABLE `templatetype` DISABLE KEYS */;
INSERT INTO `templatetype` VALUES (1,1,'汉字'),(3,2,'字母'),(4,3,'数字');
/*!40000 ALTER TABLE `templatetype` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-05-27 19:25:08
