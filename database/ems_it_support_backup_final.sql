CREATE DATABASE  IF NOT EXISTS `ems_it_support` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `ems_it_support`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: ems_it_support
-- ------------------------------------------------------
-- Server version	8.4.11

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `districts`
--

DROP TABLE IF EXISTS `districts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `districts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKd107o8vh2gcyb9tgjuw56pc0n` (`name`),
  UNIQUE KEY `UKgkpvb55aiiyu9n55qoxcwmkds` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `districts`
--

LOCK TABLES `districts` WRITE;
/*!40000 ALTER TABLE `districts` DISABLE KEYS */;
INSERT INTO `districts` VALUES (1,_binary '','COE','Ekurhuleni'),(2,_binary '','COJ','Johannesburg'),(3,_binary '','SED','Sedibeng'),(4,_binary '','COT','Tshwane'),(5,_binary '','WR','West Rand'),(6,_binary '','ECC','ECC');
/*!40000 ALTER TABLE `districts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_tokens`
--

DROP TABLE IF EXISTS `password_reset_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) NOT NULL,
  `token` varchar(100) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK71lqwbwtklmljk3qlsugr1mig` (`token`),
  UNIQUE KEY `UKla2ts67g4oh2sreayswhox1i6` (`user_id`),
  CONSTRAINT `FKk3ndxg5xp6v7wd4gjyusp15gq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_tokens`
--

LOCK TABLES `password_reset_tokens` WRITE;
/*!40000 ALTER TABLE `password_reset_tokens` DISABLE KEYS */;
INSERT INTO `password_reset_tokens` VALUES (8,'2026-08-21 13:01:19.643695','1fa66881-88d9-45bc-812f-e2fd89c26269',16);
/*!40000 ALTER TABLE `password_reset_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stations`
--

DROP TABLE IF EXISTS `stations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `district_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKmgv4ev32p0hw385uwygu8uiaw` (`district_id`,`name`),
  UNIQUE KEY `UKp81oh2ugminryrkr4879q91kk` (`district_id`,`code`),
  CONSTRAINT `FKp4rtd15xpw5k5gdouckn6n1ot` FOREIGN KEY (`district_id`) REFERENCES `districts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stations`
--

LOCK TABLES `stations` WRITE;
/*!40000 ALTER TABLE `stations` DISABLE KEYS */;
INSERT INTO `stations` VALUES (1,_binary '','GER01','Germiston EMS station',1),(2,_binary '\0','GER01','Germiston EMS station',6),(3,_binary '','ECC01','ecc1',6),(4,_binary '','TMP-COE-001','Bertha Gxowa EMS Station',1),(5,_binary '','TMP-COE-002','Phillip Moyo EMS Station',1),(6,_binary '','TMP-COE-003','Daggafontein EMS Station',1),(7,_binary '','TMP-COE-004','Devon EMS Station',1),(8,_binary '','TMP-COE-005','Far East Rand EMS Station',1),(9,_binary '','TMP-COE-006','Goba EMS Station',1),(10,_binary '','TMP-COE-007','Itireleng EMS Station',1),(11,_binary '','TMP-COE-008','Nokuthela Ngwenya EMS Station',1),(12,_binary '','TMP-COE-009','Pholosong EMS Station',1),(13,_binary '','TMP-COE-010','Phola Park EMS Station',1),(14,_binary '','TMP-COE-011','Tambo Memorial EMS Station',1),(15,_binary '','TMP-COE-012','Tembisa EMS Station',1),(16,_binary '','TMP-COE-013','Thelle Mogoerane EMS Station',1),(17,_binary '','TMP-COE-014','Springs EMS Station',1),(18,_binary '','TMP-COE-015','Dunswart EMS Station',1),(19,_binary '','TMP-COJ-001','Edenvale EMS Station',2),(20,_binary '','TMP-COJ-002','Discoveries EMS Station',2),(21,_binary '','TMP-COJ-003','Chiawelo EMS Station',2),(22,_binary '','TMP-COJ-004','Mofolo EMS Station',2),(23,_binary '','TMP-COJ-005','Hillbrow EMS Station',2),(24,_binary '','TMP-COJ-006','Imbalenhle EMS Station',2),(25,_binary '','TMP-COJ-007','Lenasia EMS Station',2),(26,_binary '','TMP-COJ-008','Lenasia South EMS Station',2),(27,_binary '','TMP-COJ-009','DiepslootEMS Station',2),(28,_binary '','TMP-COJ-010','Tara EMS Station',2),(29,_binary '','TMP-COJ-011','Midrand EMS Station',2),(30,_binary '','TMP-COJ-012','Ebony EMS Station',2),(31,_binary '','TMP-COJ-013','Orlando East EMS Station',2),(32,_binary '','TMP-COJ-014','BARA/ELDOS EMS Station',2),(33,_binary '','TMP-COJ-015','Selby EMS Station',2),(34,_binary '','TMP-COJ-016','Alex EMS Station',2),(35,_binary '','TMP-COJ-017','Zola EMS Station',2),(36,_binary '','TMP-SED-001','Vanderbijlpark EMS Station',3),(37,_binary '','TMP-SED-002','Vereeniging EMS Station',3),(38,_binary '','TMP-SED-003','Heidelberg EMS Station',3),(39,_binary '','TMP-SED-004','Pontshong EMS Station',3),(40,_binary '','TMP-SED-005','Sebokeng EMS Station',3),(41,_binary '','TMP-SED-006','Evaton EMS Station',3),(42,_binary '','TMP-COT-001','Prinshof Ems Station',4),(43,_binary '','TMP-COT-002','Odi Ems Station',4),(44,_binary '','TMP-COT-003','Temba Ems Station',4),(45,_binary '','TMP-COT-004','Cullinan Ems Station',4),(46,_binary '','TMP-COT-005','Ekangala Ems Station',4),(47,_binary '','TMP-COT-006','Bronkhorstspruit Ems Station',4),(48,_binary '','TMP-COT-007','Block JJ Ems Station',4),(49,_binary '','TMP-COT-008','Laudium Ems Station',4),(50,_binary '','TMP-COT-009','Mamelodi Ems Station',4),(51,_binary '','TMP-COT-010','Kalafong Ems Station',4),(52,_binary '','TMP-COT-011','DGMAH Ems Station',4),(53,_binary '','TMP-WR-001','Dr. Yusuf Dadoo EMS Station',5),(54,_binary '','TMP-WR-002','Leratong EMS Station',5),(55,_binary '','TMP-WR-003','Bekkersdal EMS Station',5),(56,_binary '','TMP-WR-004','Caltonville EMS Station',5),(57,_binary '','TMP-WR-005','Fochville EMS Station',5),(58,_binary '','TMP-WR-006','Khutshong EMS Station',5),(59,_binary '','TMP-WR-007','Mohlakeng EMS Station',5),(60,_binary '','TMP-WR-008','Westonaria EMS Station',5),(61,_binary '','TMP-WR-009','Wedela EMS Station',5),(62,_binary '','TMP-WR-010','Sterkfontein EMS Station',5),(63,_binary '','TMP-WR-011','Maggaliesburg EMS Station',5),(64,_binary '','TMP-WR-012','Mulderdrift EMS Station',5);
/*!40000 ALTER TABLE `stations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `technician_attendance`
--

DROP TABLE IF EXISTS `technician_attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `technician_attendance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `check_in` datetime(6) DEFAULT NULL,
  `check_out` datetime(6) DEFAULT NULL,
  `lunch_end` datetime(6) DEFAULT NULL,
  `lunch_start` datetime(6) DEFAULT NULL,
  `status` enum('AWAY','CHECKED_OUT','LUNCH','WORKING') NOT NULL,
  `work_date` date NOT NULL,
  `technician_id` bigint NOT NULL,
  `lunch_allowed_until` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKbt6b1jkjaa9e1yhw1245bwes6` (`technician_id`,`work_date`),
  CONSTRAINT `FKonrbyjsywrraalsem8mi7wc5h` FOREIGN KEY (`technician_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `technician_attendance`
--

LOCK TABLES `technician_attendance` WRITE;
/*!40000 ALTER TABLE `technician_attendance` DISABLE KEYS */;
INSERT INTO `technician_attendance` VALUES (1,'2026-08-15 19:21:12.720991',NULL,NULL,NULL,'WORKING','2026-08-15',5,NULL),(2,'2026-08-16 19:01:52.531705','2026-08-16 19:17:24.089280','2026-08-16 19:17:12.183022','2026-08-16 19:02:01.416561','CHECKED_OUT','2026-08-16',5,'2026-08-16 20:32:01.416561'),(3,'2026-08-17 09:16:54.724096','2026-08-17 09:18:25.617726','2026-08-17 09:18:17.256192','2026-08-17 09:17:04.682254','CHECKED_OUT','2026-08-17',5,NULL),(4,'2026-08-18 10:28:54.527185',NULL,NULL,NULL,'WORKING','2026-08-18',5,NULL),(5,'2026-08-19 09:06:53.721920',NULL,NULL,NULL,'WORKING','2026-08-19',5,NULL),(6,'2026-08-19 09:07:54.107726',NULL,NULL,'2026-08-19 09:07:59.008447','LUNCH','2026-08-19',1,NULL),(7,'2026-08-21 12:23:26.961467',NULL,NULL,'2026-08-21 12:28:20.793908','LUNCH','2026-08-21',16,NULL),(8,'2026-08-21 12:52:28.407846',NULL,NULL,NULL,'WORKING','2026-08-21',15,NULL),(9,'2026-08-23 17:14:52.111139',NULL,NULL,NULL,'WORKING','2026-08-23',16,NULL);
/*!40000 ALTER TABLE `technician_attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tickets`
--

DROP TABLE IF EXISTS `tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tickets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assigned_technician` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `description` text,
  `priority` varchar(255) DEFAULT NULL,
  `requester_email` varchar(255) DEFAULT NULL,
  `requester_name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `ticket_number` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `asset_number` varchar(255) DEFAULT NULL,
  `device_type` varchar(255) DEFAULT NULL,
  `district` varchar(255) DEFAULT NULL,
  `manager_email` varchar(255) DEFAULT NULL,
  `manager_first_name` varchar(255) DEFAULT NULL,
  `manager_ip_address` varchar(255) DEFAULT NULL,
  `manager_phone` varchar(255) DEFAULT NULL,
  `manager_role` varchar(255) DEFAULT NULL,
  `manager_surname` varchar(255) DEFAULT NULL,
  `problem_type` varchar(255) DEFAULT NULL,
  `station_unit` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK4ks48wgrew48dpkh0wd1rbe2b` (`ticket_number`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tickets`
--

LOCK TABLES `tickets` WRITE;
/*!40000 ALTER TABLE `tickets` DISABLE KEYS */;
INSERT INTO `tickets` VALUES (1,'Tonylynn Lee Mabasa','Network',NULL,'IT','The workstation cannot connect to the EMS network and is unable to access required systems.','HIGH','test@gauteng.gov.za','Tony Mabasa','RESOLVED','EMS-1786797249477','computer not connecting to network','2026-08-16 14:47:04.550482',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'Tonylynn Lee Mabasa','Hardware',NULL,'HR','The icons are glitching','URGENT','tumi@ems.gov.za','Tumi Maluleke','IN_PROGRESS','EMS-1786818956980','Monitor is glitching','2026-08-16 15:03:39.103367',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,NULL,'Software','2026-08-20 22:38:35.929279','HR','THE WINDOWS FAILED','CRITICAL','tumijane.maluleke@gmail.com','Tumelo Jane Maluleke','OPEN','EMS-1787258315904','software damage','2026-08-20 22:38:35.929279','EMS-IT-1233','Laptop','City of Johannesburg','tumijane.maluleke@gmail.com','Tumelo Jane','','0640390186','','Maluleke','Software installation','MIDRAND EMS '),(4,'John Smith','Network','2026-08-21 12:49:39.618123','HR','Laptop does not have internet connection.','MEDIUM','Victor132@gmail.com','Victor','IN_PROGRESS','EMS-1787309379608','Password reset','2026-08-21 12:53:37.500100','GEMS28561','Laptop','Sedibeng','','','','','','','Network connection','Vereeniging'),(5,NULL,'Active Directory','2026-08-23 21:55:07.999703','Supply Chain Management','My password is expiring soon','URGENT','tonylynnleemabasa2000@gmail.com','Tumelo Maluleke','OPEN','EMS-1787514907975','Ess password reset','2026-08-23 21:55:07.999703','','Other','City of Johannesburg','tonylynnleemabasa2000@gmail.com','Tumelo','','0650266456','STATION_MANAGER','Maluleke','Active Directory account','Hillbrow EMS Station');
/*!40000 ALTER TABLE `tickets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `department` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `personal_number` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','TECHNICIAN','STATION_MANAGER') NOT NULL,
  `surname` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `district` varchar(255) DEFAULT NULL,
  `station_unit` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK6ff9eqia6nd9gavmrxp1e93di` (`personal_number`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,_binary '','IT Management','admin@ems.gov.za','IT','$2a$10$/T6rSJOIPNmIoA.t/mI.NO43rUmCif2m595CO3FEmQsieM.oH90pa','EMS000001','','ADMIN','Administrator','admin',NULL,NULL),(5,_binary '','IT Support','tonylynnleemabasa@ems.gov.za','Tonylynn Lee','$2a$10$e73mOnjFXBsWDb6C2u/tMOHd2pyPLhoz448bAa8SGAhZTu1nmzRzS','EMS000006','0659207519','TECHNICIAN','Mabasa','tony2000',NULL,NULL),(14,_binary '','Supply Chain Management','tonylynnleemabasa2000@gmail.com','Tumelo','$2a$10$OyLHO13UoOok0tGAgCu.pe9vmwhl2dqQQz5rnzJeXvqrByZ.5bAFy','EMS0000014','0650266456','STATION_MANAGER','Maluleke','tumelo04','City of Johannesburg','Hillbrow EMS Station'),(15,_binary '','Human Resources','john@ems.co.za','John','$2a$10$r4xtKpR4yT6S3mPQcTzLGefTN/yFn1msIyO4lkfJFklHaldeNsoxy','EMS0000015','0833964301','TECHNICIAN','Smith','admin2','City of Tshwane','MAMELODI'),(16,_binary '','IT Department','mkhabelevutlhari122@gmail.com','Paddy','$2a$10$XdSHFc7Jto.SQ89diGphPu8ir2OllSHNaB5ClvkGJh5/oFH4IHUxS','EMS0000018','0731846991','TECHNICIAN','Mkhabela','AdminPaddy','City of Johannesburg','Midrand EMS Station');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-23 23:06:24
