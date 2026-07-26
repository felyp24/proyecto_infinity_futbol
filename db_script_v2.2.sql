-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: infinity_futbol_db
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `alumno`
--

DROP TABLE IF EXISTS `alumno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alumno` (
  `id_alumno` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_usuario` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombres` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellidos` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_documento` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero_documento` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_registro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`id_alumno`),
  UNIQUE KEY `id_usuario` (`id_usuario`),
  UNIQUE KEY `numero_documento` (`numero_documento`),
  CONSTRAINT `fk_alumno_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alumno`
--

LOCK TABLES `alumno` WRITE;
/*!40000 ALTER TABLE `alumno` DISABLE KEYS */;
INSERT INTO `alumno` VALUES ('ALU-31DE6D50FD8A49CB','USR-31DE6D50FD8A49CB','Benigno','Garcia','DNI','874516891','1991-03-22','945126872','2026-07-13 20:13:41','ACTIVO'),('ALU-6EF690DE15E94664','USR-6EF690DE15E94664','Adrian','Cardenas','CARNET_EXTRANJERIA','0005445123','2001-06-20','974123541','2026-07-13 01:05:02','ACTIVO'),('ALU-97CC7A63E5034A4D','USR-97CC7A63E5034A4D','Han','Prueba','CARNET_EXTRANJERIA','000541698','1994-06-23','987156213','2026-07-15 16:53:10','ACTIVO'),('ALU-9ABF13E3E7264E3D','USR-9ABF13E3E7264E3D','usuario','prueba','DNI','751574951','2026-05-20','954781205','2026-07-20 09:09:41','ACTIVO'),('ALU-ACAF9D34FBBE48BC','USR-ACAF9D34FBBE48BC','adwdws','wdfwefe','CARNET_EXTRANJERIA','0000541365','2001-02-22','951324871','2026-07-20 20:30:56','ACTIVO'),('ALU-D4D816D5448A45B6','USR-D4D816D5448A45B6','Han','Yan','DNI','75080819','1999-07-22','95212562','2026-07-13 01:39:36','ACTIVO'),('ALU-DC9ACDAE37E94D31','USR-DC9ACDAE37E94D31','Juan','Casas','DNI','874156162','1998-06-17','987421056','2026-07-13 20:07:47','ACTIVO'),('ALU-PRUEBA-001','USR-PRUEBA-001','Carlos Alberto','Ramírez Soto','DNI','70000001','2000-01-15','987654321','2026-07-11 22:51:21','ACTIVO');
/*!40000 ALTER TABLE `alumno` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asistencia`
--

DROP TABLE IF EXISTS `asistencia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asistencia` (
  `id_asistencia` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_reserva` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado_asistencia` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `hora_marcacion` datetime DEFAULT NULL,
  `observacion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_asistencia`),
  UNIQUE KEY `id_reserva` (`id_reserva`),
  CONSTRAINT `fk_asistencia_reserva` FOREIGN KEY (`id_reserva`) REFERENCES `reserva` (`id_reserva`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asistencia`
--

LOCK TABLES `asistencia` WRITE;
/*!40000 ALTER TABLE `asistencia` DISABLE KEYS */;
/*!40000 ALTER TABLE `asistencia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cancha`
--

DROP TABLE IF EXISTS `cancha`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cancha` (
  `id_cancha` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero_cancha` int NOT NULL,
  `tipo_superficie` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DISPONIBLE',
  `id_sede` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_cancha`),
  KEY `fk_cancha_sede` (`id_sede`),
  CONSTRAINT `fk_cancha_sede` FOREIGN KEY (`id_sede`) REFERENCES `sede` (`id_sede`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cancha`
--

LOCK TABLES `cancha` WRITE;
/*!40000 ALTER TABLE `cancha` DISABLE KEYS */;
INSERT INTO `cancha` VALUES ('CAN-JM-001',1,'Sintética','DISPONIBLE','SED-JM-001'),('CAN-LI-001',1,'Sintética','DISPONIBLE','SED-LI-001'),('CAN-SM-001',1,'Sintética','DISPONIBLE','SED-SM-001'),('CAN-SU-001',1,'Sintética','DISPONIBLE','SED-SU-001');
/*!40000 ALTER TABLE `cancha` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clase`
--

DROP TABLE IF EXISTS `clase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clase` (
  `id_clase` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `titulo` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_clase` date NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fin` time NOT NULL,
  `cupo_maximo` int NOT NULL,
  `cupo_disponible` int NOT NULL,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PROGRAMADA',
  `id_cancha` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_entrenador` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_clase`),
  KEY `fk_clase_cancha` (`id_cancha`),
  KEY `fk_clase_entrenador` (`id_entrenador`),
  KEY `idx_clase_fecha` (`fecha_clase`),
  CONSTRAINT `fk_clase_cancha` FOREIGN KEY (`id_cancha`) REFERENCES `cancha` (`id_cancha`),
  CONSTRAINT `fk_clase_entrenador` FOREIGN KEY (`id_entrenador`) REFERENCES `entrenador` (`id_entrenador`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clase`
--

LOCK TABLES `clase` WRITE;
/*!40000 ALTER TABLE `clase` DISABLE KEYS */;
INSERT INTO `clase` VALUES ('CLS-384FA499C65347E1','ClasePrueba3',NULL,'2026-07-21','09:23:00','12:26:00',1,1,'PROGRAMADA','CAN-JM-001','ENT-PRUEBA-001'),('CLS-5B619E0DE390430B','Clase1','Clase de prueba','2026-07-21','03:15:00','04:18:00',18,17,'PROGRAMADA','CAN-JM-001','ENT-PRUEBA-001'),('CLS-BE5CA361F39147DF','Clase de arqueros',NULL,'2026-07-24','17:00:00','18:00:00',1,1,'PROGRAMADA','CAN-JM-001','ENT-PRUEBA-001'),('CLS-E1356B2807484DDC','Clase de arqueros',NULL,'2026-07-22','13:03:00','16:05:00',15,15,'PROGRAMADA','CAN-JM-001','ENT-PRUEBA-001'),('CLS-E83C787A23174579','Clase2','Prueba2','2026-07-21','04:18:00','07:19:00',15,15,'PROGRAMADA','CAN-JM-001','ENT-PRUEBA-001');
/*!40000 ALTER TABLE `clase` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comprobante`
--

DROP TABLE IF EXISTS `comprobante`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comprobante` (
  `id_comprobante` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_pago` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_comprobante` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `serie` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_emision` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `monto_total` decimal(10,2) NOT NULL,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'EMITIDO',
  PRIMARY KEY (`id_comprobante`),
  UNIQUE KEY `id_pago` (`id_pago`),
  UNIQUE KEY `uk_comprobante_serie_numero` (`serie`,`numero`),
  KEY `idx_comprobante_fecha` (`fecha_emision`),
  CONSTRAINT `fk_comprobante_pago` FOREIGN KEY (`id_pago`) REFERENCES `pago` (`id_pago`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comprobante`
--

LOCK TABLES `comprobante` WRITE;
/*!40000 ALTER TABLE `comprobante` DISABLE KEYS */;
/*!40000 ALTER TABLE `comprobante` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cuenta_credito`
--

DROP TABLE IF EXISTS `cuenta_credito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuenta_credito` (
  `id_cuenta_credito` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_alumno` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `saldo_actual` int NOT NULL DEFAULT '0',
  `fecha_actualizacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_cuenta_credito`),
  UNIQUE KEY `id_alumno` (`id_alumno`),
  CONSTRAINT `fk_cuenta_credito_alumno` FOREIGN KEY (`id_alumno`) REFERENCES `alumno` (`id_alumno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cuenta_credito`
--

LOCK TABLES `cuenta_credito` WRITE;
/*!40000 ALTER TABLE `cuenta_credito` DISABLE KEYS */;
INSERT INTO `cuenta_credito` VALUES ('CTC-97CC7A63E5034A4D','ALU-97CC7A63E5034A4D',32,'2026-07-26 13:55:49'),('CTC-9ABF13E3E7264E3D','ALU-9ABF13E3E7264E3D',12,'2026-07-26 14:58:47'),('CTC-ACAF9D34FBBE48BC','ALU-ACAF9D34FBBE48BC',0,'2026-07-20 20:30:56'),('CTC-PRUEBA-001','ALU-PRUEBA-001',5,'2026-07-22 08:49:13');
/*!40000 ALTER TABLE `cuenta_credito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cupon_descuento`
--

DROP TABLE IF EXISTS `cupon_descuento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cupon_descuento` (
  `id_cupon` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `porcentaje_descuento` decimal(5,2) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_expiracion` date NOT NULL,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`id_cupon`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cupon_descuento`
--

LOCK TABLES `cupon_descuento` WRITE;
/*!40000 ALTER TABLE `cupon_descuento` DISABLE KEYS */;
/*!40000 ALTER TABLE `cupon_descuento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `distrito`
--

DROP TABLE IF EXISTS `distrito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `distrito` (
  `id_distrito` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_distrito`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distrito`
--

LOCK TABLES `distrito` WRITE;
/*!40000 ALTER TABLE `distrito` DISABLE KEYS */;
INSERT INTO `distrito` VALUES ('JM','Jesús María',1),('LI','Lince',1),('MI','Miraflores',1),('SB','San Borja',1),('SM','San Miguel',1),('SU','Santiago de Surco',1);
/*!40000 ALTER TABLE `distrito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entrenador`
--

DROP TABLE IF EXISTS `entrenador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `entrenador` (
  `id_entrenador` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_usuario` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombres` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellidos` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `especialidad` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`id_entrenador`),
  UNIQUE KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `fk_entrenador_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entrenador`
--

LOCK TABLES `entrenador` WRITE;
/*!40000 ALTER TABLE `entrenador` DISABLE KEYS */;
INSERT INTO `entrenador` VALUES ('ENT-PRUEBA-001','USR-ENT-001','Miguel Ángel','Gómez Torres','987654321','Entrenamiento técnico','ACTIVO');
/*!40000 ALTER TABLE `entrenador` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movimiento_credito`
--

DROP TABLE IF EXISTS `movimiento_credito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento_credito` (
  `id_movimiento_credito` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_cuenta_credito` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_pago` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_reserva` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo_movimiento` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cantidad` int NOT NULL,
  `fecha_movimiento` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_expiracion` date DEFAULT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_movimiento_credito`),
  UNIQUE KEY `uk_movimiento_pago_tipo` (`id_pago`,`tipo_movimiento`),
  KEY `fk_movimiento_credito_cuenta` (`id_cuenta_credito`),
  KEY `fk_movimiento_credito_reserva` (`id_reserva`),
  KEY `idx_movimiento_credito_tipo` (`tipo_movimiento`),
  CONSTRAINT `fk_movimiento_credito_cuenta` FOREIGN KEY (`id_cuenta_credito`) REFERENCES `cuenta_credito` (`id_cuenta_credito`),
  CONSTRAINT `fk_movimiento_credito_pago` FOREIGN KEY (`id_pago`) REFERENCES `pago` (`id_pago`),
  CONSTRAINT `fk_movimiento_credito_reserva` FOREIGN KEY (`id_reserva`) REFERENCES `reserva` (`id_reserva`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimiento_credito`
--

LOCK TABLES `movimiento_credito` WRITE;
/*!40000 ALTER TABLE `movimiento_credito` DISABLE KEYS */;
INSERT INTO `movimiento_credito` VALUES ('MOV-0D70DB4097424294A96A','CTC-97CC7A63E5034A4D',NULL,'RSV-B7EADB9810C44BA9','DEVOLUCION',1,'2026-07-22 08:59:44',NULL,'Devolución de 1 crédito por cancelación de la clase: Clase de arqueros'),('MOV-39632593411D47D3B8AA','CTC-97CC7A63E5034A4D','PAG-E830E05F89064EDA',NULL,'RECARGA',4,'2026-07-26 13:35:59','2026-08-25','Recarga de 4 créditos por compra de Paquete 4 créditos'),('MOV-3D96E6C872714F7291E1','CTC-97CC7A63E5034A4D',NULL,'RSV-50F0E43295674C81','DEVOLUCION',1,'2026-07-20 10:09:03',NULL,'Devolución de 1 crédito por cancelación de la clase: ClasePrueba3'),('MOV-55FE44D060694B46827E','CTC-9ABF13E3E7264E3D','PAG-B0C36FDEA316426F',NULL,'RECARGA',4,'2026-07-26 14:58:47','2026-08-25','Recarga de 4 créditos por compra de Paquete 4 créditos'),('MOV-62C26B46890D4463AEC3','CTC-97CC7A63E5034A4D',NULL,'RSV-5A8F58FEBA444B33','CONSUMO',-1,'2026-07-22 10:07:41',NULL,'Consumo de 1 crédito por reserva de la clase: Clase de arqueros'),('MOV-6A21854AE1504BB88F2F','CTC-97CC7A63E5034A4D',NULL,'RSV-5A8F58FEBA444B33','DEVOLUCION',1,'2026-07-22 10:08:02',NULL,'Devolución de 1 crédito por cancelación de la clase: Clase de arqueros'),('MOV-753AB23548504CF79294','CTC-97CC7A63E5034A4D',NULL,'RSV-542332983D95486B','CONSUMO',-1,'2026-07-20 09:16:01',NULL,'Consumo de 1 crédito por reserva de la clase: Clase1'),('MOV-92DBF52608544B87960B','CTC-97CC7A63E5034A4D',NULL,'RSV-B7EADB9810C44BA9','DEVOLUCION',1,'2026-07-20 20:31:40',NULL,'Devolución de 1 crédito por cancelación de la clase: Clase de arqueros'),('MOV-AF31069A4C8448B88D13','CTC-97CC7A63E5034A4D',NULL,'RSV-B7EADB9810C44BA9','CONSUMO',-1,'2026-07-20 20:29:45',NULL,'Consumo de 1 crédito por reserva de la clase: Clase de arqueros'),('MOV-C04E9F98660445CCB4AC','CTC-9ABF13E3E7264E3D','PAG-0470A313045C4AAF',NULL,'RECARGA',8,'2026-07-26 13:58:01','2026-09-09','Recarga de 8 créditos por compra de Paquete 8 créditos'),('MOV-C27351DDECC349F6AA6B','CTC-97CC7A63E5034A4D','PAG-8670FB0F56324A1D',NULL,'RECARGA',12,'2026-07-26 13:55:49','2026-09-24','Recarga de 12 créditos por compra de Paquete 12 créditos'),('MOV-C54173C45A1F4F2C93E6','CTC-97CC7A63E5034A4D','PAG-EACC8D38AE524F44',NULL,'RECARGA',8,'2026-07-26 13:25:37','2026-09-09','Recarga de 8 créditos por compra de Paquete 8 créditos'),('MOV-E8522A34CEA04BC6AC04','CTC-97CC7A63E5034A4D','PAG-68AE4C213A43421D',NULL,'RECARGA',4,'2026-07-26 13:36:14','2026-08-25','Recarga de 4 créditos por compra de Paquete 4 créditos'),('MOV-F23B2F9B35684D2DBDAA','CTC-97CC7A63E5034A4D',NULL,'RSV-B7EADB9810C44BA9','CONSUMO',-1,'2026-07-20 20:32:11',NULL,'Consumo de 1 crédito por reserva de la clase: Clase de arqueros'),('MOV-F88F7BB65A654AF08870','CTC-97CC7A63E5034A4D',NULL,'RSV-50F0E43295674C81','CONSUMO',-1,'2026-07-20 09:24:13',NULL,'Consumo de 1 crédito por reserva de la clase: ClasePrueba3');
/*!40000 ALTER TABLE `movimiento_credito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notificacion`
--

DROP TABLE IF EXISTS `notificacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notificacion` (
  `id_notificacion` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_alumno` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_reserva` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `titulo` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mensaje` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_programada` datetime DEFAULT NULL,
  `fecha_envio` datetime DEFAULT NULL,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDIENTE',
  PRIMARY KEY (`id_notificacion`),
  UNIQUE KEY `uk_notificacion_reserva_tipo` (`id_reserva`,`tipo`),
  KEY `fk_notificacion_alumno` (`id_alumno`),
  KEY `idx_notificacion_estado` (`estado`),
  KEY `idx_notificacion_estado_fecha` (`estado`,`fecha_programada`),
  CONSTRAINT `fk_notificacion_alumno` FOREIGN KEY (`id_alumno`) REFERENCES `alumno` (`id_alumno`),
  CONSTRAINT `fk_notificacion_reserva` FOREIGN KEY (`id_reserva`) REFERENCES `reserva` (`id_reserva`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notificacion`
--

LOCK TABLES `notificacion` WRITE;
/*!40000 ALTER TABLE `notificacion` DISABLE KEYS */;
INSERT INTO `notificacion` VALUES ('NTF-1C13C9C6ED124620','ALU-97CC7A63E5034A4D','RSV-5A8F58FEBA444B33','Recordatorio de clase','Recuerda que tienes Clase de arqueros el 22/07/2026 a las 13:03 en Sede Jesús María, cancha 1.','RECORDATORIO_CLASE','2026-07-21 13:03:00',NULL,'CANCELADA');
/*!40000 ALTER TABLE `notificacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pago`
--

DROP TABLE IF EXISTS `pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago` (
  `id_pago` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_alumno` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_paquete_credito` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_cupon` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_pago` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_aprobacion` datetime DEFAULT NULL,
  `fecha_actualizacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `monto_bruto` decimal(10,2) NOT NULL,
  `monto_descuento` decimal(10,2) NOT NULL DEFAULT '0.00',
  `monto_total` decimal(10,2) NOT NULL,
  `moneda` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PEN',
  `metodo_pago` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `proveedor_pago` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'MERCADO_PAGO',
  `id_preferencia_externa` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_pago_externo` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado_pago` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDIENTE',
  `estado_detalle` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_pago`),
  UNIQUE KEY `uk_pago_preferencia_externa` (`id_preferencia_externa`),
  UNIQUE KEY `uk_pago_externo` (`id_pago_externo`),
  KEY `fk_pago_alumno` (`id_alumno`),
  KEY `fk_pago_paquete_credito` (`id_paquete_credito`),
  KEY `fk_pago_cupon` (`id_cupon`),
  KEY `idx_pago_fecha` (`fecha_pago`),
  KEY `idx_pago_estado` (`estado_pago`),
  CONSTRAINT `fk_pago_alumno` FOREIGN KEY (`id_alumno`) REFERENCES `alumno` (`id_alumno`),
  CONSTRAINT `fk_pago_cupon` FOREIGN KEY (`id_cupon`) REFERENCES `cupon_descuento` (`id_cupon`),
  CONSTRAINT `fk_pago_paquete_credito` FOREIGN KEY (`id_paquete_credito`) REFERENCES `paquete_credito` (`id_paquete_credito`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pago`
--

LOCK TABLES `pago` WRITE;
/*!40000 ALTER TABLE `pago` DISABLE KEYS */;
INSERT INTO `pago` VALUES ('PAG-0470A313045C4AAF','ALU-9ABF13E3E7264E3D','PKC-0002',NULL,'2026-07-26 13:57:03','2026-07-26 13:57:46','2026-07-26 13:58:01',150.00,0.00,150.00,'PEN','account_money','MERCADO_PAGO','3567128123-47afa5bc-f2a4-4eb2-9c82-6e6df7b9afb2','170657007436','APROBADO','accredited'),('PAG-21DFBEB2499B4701','ALU-97CC7A63E5034A4D','PKC-0001',NULL,'2026-07-26 12:36:59',NULL,'2026-07-26 12:37:00',80.00,0.00,80.00,'PEN','CHECKOUT_PRO','MERCADO_PAGO','3567128123-6ca9e4ed-8afa-4fff-b39f-10cca27b11af',NULL,'PENDIENTE',NULL),('PAG-262028A528E7466D','ALU-97CC7A63E5034A4D','PKC-0001',NULL,'2026-07-26 13:14:47',NULL,'2026-07-26 13:14:48',80.00,0.00,80.00,'PEN','CHECKOUT_PRO','MERCADO_PAGO','3567128123-9a1b4ea6-532e-4d1f-a91f-ad7311ec6e4c',NULL,'PENDIENTE',NULL),('PAG-3F4959E12D3F4399','ALU-97CC7A63E5034A4D','PKC-0002',NULL,'2026-07-26 12:51:17',NULL,'2026-07-26 12:51:18',150.00,0.00,150.00,'PEN','CHECKOUT_PRO','MERCADO_PAGO','3567128123-4886fe48-f492-4d17-9499-5050b3eee043',NULL,'PENDIENTE',NULL),('PAG-5396F6D86F5D479E','ALU-97CC7A63E5034A4D','PKC-0002',NULL,'2026-07-26 12:46:40',NULL,'2026-07-26 12:46:40',150.00,0.00,150.00,'PEN','CHECKOUT_PRO','MERCADO_PAGO','3567128123-c7715d5b-914b-4c74-8cb0-96e2e5ee567a',NULL,'PENDIENTE',NULL),('PAG-68AE4C213A43421D','ALU-97CC7A63E5034A4D','PKC-0001',NULL,'2026-07-26 12:42:48','2026-07-26 12:44:44','2026-07-26 13:36:14',80.00,0.00,80.00,'PEN','account_money','MERCADO_PAGO','3567128123-4e8120bc-80a8-43bc-9c3a-0f28533feee5','170649569308','APROBADO','accredited'),('PAG-6F62D898BAE84E0F','ALU-97CC7A63E5034A4D','PKC-0001',NULL,'2026-07-26 13:00:12',NULL,'2026-07-26 13:00:12',80.00,0.00,80.00,'PEN','CHECKOUT_PRO','MERCADO_PAGO','3567128123-a829b6f9-06ed-43a5-83be-3852a17beda9',NULL,'PENDIENTE',NULL),('PAG-7ACB8FB6E4484E49','ALU-97CC7A63E5034A4D','PKC-0001',NULL,'2026-07-26 12:24:03',NULL,'2026-07-26 12:24:04',80.00,0.00,80.00,'PEN','CHECKOUT_PRO','MERCADO_PAGO','3567128123-28c83710-6f58-44f1-ad9a-e64a8b6d6f3a',NULL,'PENDIENTE',NULL),('PAG-8670FB0F56324A1D','ALU-97CC7A63E5034A4D','PKC-0003',NULL,'2026-07-26 12:39:42','2026-07-26 13:55:12','2026-07-26 13:55:49',210.00,0.00,210.00,'PEN','account_money','MERCADO_PAGO','3567128123-25539020-4d88-4d4c-b8ab-e6f1c0acb2dd','169770321261','APROBADO','accredited'),('PAG-ABEE862CE2094AB1','ALU-97CC7A63E5034A4D','PKC-0001',NULL,'2026-07-26 12:31:14',NULL,'2026-07-26 12:31:15',80.00,0.00,80.00,'PEN','CHECKOUT_PRO','MERCADO_PAGO','3567128123-61863a24-378e-44ac-aca1-523507894d28',NULL,'PENDIENTE',NULL),('PAG-B0C36FDEA316426F','ALU-9ABF13E3E7264E3D','PKC-0001',NULL,'2026-07-26 14:56:53','2026-07-26 14:58:19','2026-07-26 14:58:47',80.00,0.00,80.00,'PEN','visa','MERCADO_PAGO','3567128123-29e85e78-ff2a-487b-bb13-9ab7f5f36fa6','169778580273','APROBADO','accredited'),('PAG-B67138D0331446C0','ALU-97CC7A63E5034A4D','PKC-0002',NULL,'2026-07-26 12:47:08',NULL,'2026-07-26 12:47:09',150.00,0.00,150.00,'PEN','CHECKOUT_PRO','MERCADO_PAGO','3567128123-f73d477a-5796-4b9c-869d-2e8f9c985a23',NULL,'PENDIENTE',NULL),('PAG-E830E05F89064EDA','ALU-97CC7A63E5034A4D','PKC-0001',NULL,'2026-07-26 12:57:08','2026-07-26 12:57:19','2026-07-26 13:35:59',80.00,0.00,80.00,'PEN','account_money','MERCADO_PAGO','3567128123-e762024e-39a5-42a6-aba9-32a333e68473','170651858688','APROBADO','accredited'),('PAG-EACC8D38AE524F44','ALU-97CC7A63E5034A4D','PKC-0002',NULL,'2026-07-26 13:03:23','2026-07-26 13:04:58','2026-07-26 13:25:37',150.00,0.00,150.00,'PEN','visa','MERCADO_PAGO','3567128123-d9621eae-b511-4cd8-99e4-bcb67379a260','169764144087','APROBADO','accredited'),('PAG-EE1AFF2FAB7B48EF','ALU-97CC7A63E5034A4D','PKC-0003',NULL,'2026-07-26 12:41:03',NULL,'2026-07-26 12:41:03',210.00,0.00,210.00,'PEN','CHECKOUT_PRO','MERCADO_PAGO','3567128123-b73474fd-57df-4fea-b36c-706d445ab5f7',NULL,'PENDIENTE',NULL);
/*!40000 ALTER TABLE `pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `paquete_credito`
--

DROP TABLE IF EXISTS `paquete_credito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `paquete_credito` (
  `id_paquete_credito` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cantidad_creditos` int NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `dias_vigencia` int NOT NULL,
  `estado` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_paquete_credito`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `paquete_credito`
--

LOCK TABLES `paquete_credito` WRITE;
/*!40000 ALTER TABLE `paquete_credito` DISABLE KEYS */;
INSERT INTO `paquete_credito` VALUES ('PKC-0001','Paquete 4 créditos',4,80.00,30,1),('PKC-0002','Paquete 8 créditos',8,150.00,45,1),('PKC-0003','Paquete 12 créditos',12,210.00,60,1);
/*!40000 ALTER TABLE `paquete_credito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reserva`
--

DROP TABLE IF EXISTS `reserva`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reserva` (
  `id_reserva` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_alumno` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_clase` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_reserva` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creditos_usados` int NOT NULL DEFAULT '1',
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CONFIRMADA',
  PRIMARY KEY (`id_reserva`),
  UNIQUE KEY `uk_reserva_alumno_clase` (`id_alumno`,`id_clase`),
  KEY `fk_reserva_clase` (`id_clase`),
  KEY `idx_reserva_estado` (`estado`),
  CONSTRAINT `fk_reserva_alumno` FOREIGN KEY (`id_alumno`) REFERENCES `alumno` (`id_alumno`),
  CONSTRAINT `fk_reserva_clase` FOREIGN KEY (`id_clase`) REFERENCES `clase` (`id_clase`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reserva`
--

LOCK TABLES `reserva` WRITE;
/*!40000 ALTER TABLE `reserva` DISABLE KEYS */;
INSERT INTO `reserva` VALUES ('RSV-50F0E43295674C81','ALU-97CC7A63E5034A4D','CLS-384FA499C65347E1','2026-07-20 09:24:13',1,'CANCELADA'),('RSV-542332983D95486B','ALU-97CC7A63E5034A4D','CLS-5B619E0DE390430B','2026-07-20 09:16:01',1,'CONFIRMADA'),('RSV-5A8F58FEBA444B33','ALU-97CC7A63E5034A4D','CLS-E1356B2807484DDC','2026-07-22 10:07:41',1,'CANCELADA'),('RSV-B7EADB9810C44BA9','ALU-97CC7A63E5034A4D','CLS-BE5CA361F39147DF','2026-07-20 20:32:11',1,'CANCELADA');
/*!40000 ALTER TABLE `reserva` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `id_rol` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_rol` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `nombre_rol` (`nombre_rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
INSERT INTO `rol` VALUES ('ROL-ADM','ADMINISTRADOR','Usuario encargado de administrar el sistema.',1),('ROL-COO','COORDINADOR','Usuario encargado de apoyar la coordinación de clases y reservas.',1),('ROL-ENT','ENTRENADOR','Usuario encargado de dictar y gestionar clases asignadas.',1),('ROL-USU','USUARIO','Usuario cliente/alumno del sistema.',1);
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sede`
--

DROP TABLE IF EXISTS `sede`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sede` (
  `id_sede` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `direccion` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `referencia` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_distrito` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_sede`),
  KEY `fk_sede_distrito` (`id_distrito`),
  CONSTRAINT `fk_sede_distrito` FOREIGN KEY (`id_distrito`) REFERENCES `distrito` (`id_distrito`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sede`
--

LOCK TABLES `sede` WRITE;
/*!40000 ALTER TABLE `sede` DISABLE KEYS */;
INSERT INTO `sede` VALUES ('SED-JM-001','Sede Jesús María','Av. Principal 123','Cerca del parque principal','JM',1),('SED-LI-001','Sede Lince','Av. Arequipa 1850, Lince','Cerca del parque Mariscal Castilla','LI',1),('SED-SM-001','Sede San Miguel','Av. La Marina 2100, San Miguel','Cerca de la avenida Universitaria','SM',1),('SED-SU-001','Sede Surco','Av. Caminos del Inca 1200, Santiago de Surco','Cerca del centro comercial','SU',1);
/*!40000 ALTER TABLE `sede` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `correo` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ultimo_acceso` datetime DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES ('USR-31DE6D50FD8A49CB','benignoxd','$2a$10$SqQ9NVBApDGzoyZKRcp.Te3O9r60f2bL7H/Ff5I0xpXtEXjIzFvp6','benigno@gmail.com','ACTIVO','2026-07-13 20:13:41','2026-07-26 13:56:35'),('USR-6EF690DE15E94664','adrving','$2a$10$HlM5B8fj8Cwf8hhL1tDvzO4ipcYSqsLEfbRpB6p1mX.Wm1QtCGPqe','adriancardenas@gmail.com','ACTIVO','2026-07-13 01:05:02','2026-07-20 09:23:09'),('USR-97CC7A63E5034A4D','hanprueba','$2a$10$0R4wiFvHYS1q2RDROyD0ceZHwFKfKHnS3wET0KHJxhnt/sbnC4ZVS','habp@gmail','ACTIVO','2026-07-15 16:53:10','2026-07-26 13:55:25'),('USR-9ABF13E3E7264E3D','usuarioprueba','$2a$10$RZtL9m4tXHOxDyou3D3kY.74ycsyOvCcVpr7fZjZlpjiiC0yei7r2','usuarioprueba@gmail.com','ACTIVO','2026-07-20 09:09:41','2026-07-26 14:58:44'),('USR-ACAF9D34FBBE48BC','prueba2','$2a$10$nkAxHuaey3frQRSGWr8QRex.zDP0qVhduvn0QbHy.T8Rg/E1ZvrV6','p@gmail.com','ACTIVO','2026-07-20 20:30:56','2026-07-20 20:31:04'),('USR-ADMIN-001','admin','$2a$10$nTKDcaXALE06PG4tHI1TAeW7QKuUh57rS7zBCkL8bHgxD97L/ohB.','admin@infinityfutbol.pe','ACTIVO','2026-07-11 16:24:43','2026-07-22 08:53:59'),('USR-COORD-001','coordinador','$2a$10$vaDAvSr.VVuuN3gNMykyCuhg0XBGZYkxvMantW.JTvYlp/lob8DeG','coordinador@infinityfutbol.pe','ACTIVO','2026-07-19 22:03:00','2026-07-22 09:32:22'),('USR-D4D816D5448A45B6','felip24','$2a$10$ST2kqBk1f6JLVADDoAITResB39Go6XqH0I2Y1MmifQeUp9eY.hX12','han@gmail.com','ACTIVO','2026-07-13 01:39:36','2026-07-13 20:08:08'),('USR-DC9ACDAE37E94D31','asasass','$2a$10$yxov.TaLsUWne8PbutjnCuVlGTJ3t3QiexUpC2Trqv4Wf1QkD6b32','sdadw@gmail.com','ACTIVO','2026-07-13 20:07:47',NULL),('USR-ENT-001','entrenador','$2a$10$s/wR5DgYvpOTz6TsO75AHe7VtvFfBfzxjNgZ42Ey2ya0cjrkKV9iu','entrenador@infinityfutbol.pe','ACTIVO','2026-07-19 22:03:00',NULL),('USR-PRUEBA-001','usuario.actualizado','$2a$10$EZkQK2fFwhqNxYYvMqQ9r.XPClK/BGOIuttbuCvs5f9sNEwcIFiEe','usuario.prueba@infinityfutbol.pe','ACTIVO','2026-07-11 18:45:00',NULL);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario_rol`
--

DROP TABLE IF EXISTS `usuario_rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_rol` (
  `id_usuario_rol` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_usuario` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_rol` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_usuario_rol`),
  UNIQUE KEY `uk_usuario_rol` (`id_usuario`,`id_rol`),
  KEY `fk_usuario_rol_rol` (`id_rol`),
  CONSTRAINT `fk_usuario_rol_rol` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`),
  CONSTRAINT `fk_usuario_rol_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario_rol`
--

LOCK TABLES `usuario_rol` WRITE;
/*!40000 ALTER TABLE `usuario_rol` DISABLE KEYS */;
INSERT INTO `usuario_rol` VALUES ('UROL-CCC57874024F40D48ABA','USR-31DE6D50FD8A49CB','ROL-COO'),('UROL-D7AC01A489804BE6B5EF','USR-6EF690DE15E94664','ROL-ENT'),('UROL-0B33BA8A3B4F4DF4B422','USR-97CC7A63E5034A4D','ROL-USU'),('UROL-79B3DEA602A74AF88A76','USR-9ABF13E3E7264E3D','ROL-USU'),('UROL-DA4CCC61ECC44287B5A3','USR-ACAF9D34FBBE48BC','ROL-USU'),('UROL-ADMIN-001','USR-ADMIN-001','ROL-ADM'),('UROL-COORD-001','USR-COORD-001','ROL-COO'),('UROL-A1CFECE788274F3FB92B','USR-D4D816D5448A45B6','ROL-USU'),('UROL-5AFE33A786534466AD99','USR-DC9ACDAE37E94D31','ROL-USU'),('UROL-ENT-001','USR-ENT-001','ROL-ENT'),('UROL-D2E9808ADDDA4FDCABA0','USR-PRUEBA-001','ROL-USU');
/*!40000 ALTER TABLE `usuario_rol` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-26 15:45:42
