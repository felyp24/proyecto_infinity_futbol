-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema infinity_futbol_db
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema infinity_futbol_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `infinity_futbol_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
USE `infinity_futbol_db` ;

-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`usuario` (
  `id_usuario` VARCHAR(20) NOT NULL,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `correo` VARCHAR(100) NOT NULL,
  `estado` VARCHAR(30) NOT NULL DEFAULT 'ACTIVO',
  `fecha_creacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ultimo_acceso` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE INDEX `username` (`username` ASC) VISIBLE,
  UNIQUE INDEX `correo` (`correo` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`alumno`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`alumno` (
  `id_alumno` VARCHAR(20) NOT NULL,
  `id_usuario` VARCHAR(20) NOT NULL,
  `nombres` VARCHAR(100) NOT NULL,
  `apellidos` VARCHAR(100) NOT NULL,
  `tipo_documento` VARCHAR(30) NOT NULL,
  `numero_documento` VARCHAR(20) NOT NULL,
  `fecha_nacimiento` DATE NULL DEFAULT NULL,
  `telefono` VARCHAR(20) NULL DEFAULT NULL,
  `fecha_registro` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` VARCHAR(30) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`id_alumno`),
  UNIQUE INDEX `id_usuario` (`id_usuario` ASC) VISIBLE,
  UNIQUE INDEX `numero_documento` (`numero_documento` ASC) VISIBLE,
  CONSTRAINT `fk_alumno_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `infinity_futbol_db`.`usuario` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`distrito`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`distrito` (
  `id_distrito` VARCHAR(10) NOT NULL,
  `nombre` VARCHAR(100) NOT NULL,
  `estado` TINYINT NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_distrito`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`sede`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`sede` (
  `id_sede` VARCHAR(20) NOT NULL,
  `nombre` VARCHAR(100) NOT NULL,
  `direccion` VARCHAR(150) NOT NULL,
  `referencia` VARCHAR(150) NULL DEFAULT NULL,
  `id_distrito` VARCHAR(10) NOT NULL,
  `estado` TINYINT NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_sede`),
  INDEX `fk_sede_distrito` (`id_distrito` ASC) VISIBLE,
  CONSTRAINT `fk_sede_distrito`
    FOREIGN KEY (`id_distrito`)
    REFERENCES `infinity_futbol_db`.`distrito` (`id_distrito`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`cancha`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`cancha` (
  `id_cancha` VARCHAR(20) NOT NULL,
  `numero_cancha` INT NOT NULL,
  `tipo_superficie` VARCHAR(50) NULL DEFAULT NULL,
  `estado` VARCHAR(30) NOT NULL DEFAULT 'DISPONIBLE',
  `id_sede` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id_cancha`),
  INDEX `fk_cancha_sede` (`id_sede` ASC) VISIBLE,
  CONSTRAINT `fk_cancha_sede`
    FOREIGN KEY (`id_sede`)
    REFERENCES `infinity_futbol_db`.`sede` (`id_sede`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`entrenador`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`entrenador` (
  `id_entrenador` VARCHAR(20) NOT NULL,
  `id_usuario` VARCHAR(20) NOT NULL,
  `nombres` VARCHAR(100) NOT NULL,
  `apellidos` VARCHAR(100) NOT NULL,
  `telefono` VARCHAR(20) NULL DEFAULT NULL,
  `especialidad` VARCHAR(100) NULL DEFAULT NULL,
  `estado` VARCHAR(30) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`id_entrenador`),
  UNIQUE INDEX `id_usuario` (`id_usuario` ASC) VISIBLE,
  CONSTRAINT `fk_entrenador_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `infinity_futbol_db`.`usuario` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`clase`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`clase` (
  `id_clase` VARCHAR(20) NOT NULL,
  `titulo` VARCHAR(100) NOT NULL,
  `descripcion` VARCHAR(255) NULL DEFAULT NULL,
  `fecha_clase` DATE NOT NULL,
  `hora_inicio` TIME NOT NULL,
  `hora_fin` TIME NOT NULL,
  `cupo_maximo` INT NOT NULL,
  `cupo_disponible` INT NOT NULL,
  `estado` VARCHAR(30) NOT NULL DEFAULT 'PROGRAMADA',
  `id_cancha` VARCHAR(20) NOT NULL,
  `id_entrenador` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id_clase`),
  INDEX `fk_clase_cancha` (`id_cancha` ASC) VISIBLE,
  INDEX `fk_clase_entrenador` (`id_entrenador` ASC) VISIBLE,
  INDEX `idx_clase_fecha` (`fecha_clase` ASC) VISIBLE,
  CONSTRAINT `fk_clase_cancha`
    FOREIGN KEY (`id_cancha`)
    REFERENCES `infinity_futbol_db`.`cancha` (`id_cancha`),
  CONSTRAINT `fk_clase_entrenador`
    FOREIGN KEY (`id_entrenador`)
    REFERENCES `infinity_futbol_db`.`entrenador` (`id_entrenador`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`reserva`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`reserva` (
  `id_reserva` VARCHAR(20) NOT NULL,
  `id_alumno` VARCHAR(20) NOT NULL,
  `id_clase` VARCHAR(20) NOT NULL,
  `fecha_reserva` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creditos_usados` INT NOT NULL DEFAULT '1',
  `estado` VARCHAR(30) NOT NULL DEFAULT 'CONFIRMADA',
  PRIMARY KEY (`id_reserva`),
  UNIQUE INDEX `uk_reserva_alumno_clase` (`id_alumno` ASC, `id_clase` ASC) VISIBLE,
  INDEX `fk_reserva_clase` (`id_clase` ASC) VISIBLE,
  INDEX `idx_reserva_estado` (`estado` ASC) VISIBLE,
  CONSTRAINT `fk_reserva_alumno`
    FOREIGN KEY (`id_alumno`)
    REFERENCES `infinity_futbol_db`.`alumno` (`id_alumno`),
  CONSTRAINT `fk_reserva_clase`
    FOREIGN KEY (`id_clase`)
    REFERENCES `infinity_futbol_db`.`clase` (`id_clase`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`asistencia`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`asistencia` (
  `id_asistencia` VARCHAR(20) NOT NULL,
  `id_reserva` VARCHAR(20) NOT NULL,
  `estado_asistencia` VARCHAR(30) NOT NULL,
  `hora_marcacion` DATETIME NULL DEFAULT NULL,
  `observacion` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_asistencia`),
  UNIQUE INDEX `id_reserva` (`id_reserva` ASC) VISIBLE,
  CONSTRAINT `fk_asistencia_reserva`
    FOREIGN KEY (`id_reserva`)
    REFERENCES `infinity_futbol_db`.`reserva` (`id_reserva`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`cupon_descuento`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`cupon_descuento` (
  `id_cupon` VARCHAR(20) NOT NULL,
  `codigo` VARCHAR(50) NOT NULL,
  `porcentaje_descuento` DECIMAL(5,2) NOT NULL,
  `fecha_inicio` DATE NOT NULL,
  `fecha_expiracion` DATE NOT NULL,
  `estado` VARCHAR(30) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`id_cupon`),
  UNIQUE INDEX `codigo` (`codigo` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`paquete_credito`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`paquete_credito` (
  `id_paquete_credito` VARCHAR(20) NOT NULL,
  `nombre` VARCHAR(100) NOT NULL,
  `cantidad_creditos` INT NOT NULL,
  `precio` DECIMAL(10,2) NOT NULL,
  `dias_vigencia` INT NOT NULL,
  `estado` TINYINT NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_paquete_credito`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`pago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`pago` (
  `id_pago` VARCHAR(20) NOT NULL,
  `id_alumno` VARCHAR(20) NOT NULL,
  `id_paquete_credito` VARCHAR(20) NOT NULL,
  `id_cupon` VARCHAR(20) NULL DEFAULT NULL,
  `fecha_pago` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_aprobacion` DATETIME NULL DEFAULT NULL,
  `fecha_actualizacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `monto_bruto` DECIMAL(10,2) NOT NULL,
  `monto_descuento` DECIMAL(10,2) NOT NULL DEFAULT '0.00',
  `monto_total` DECIMAL(10,2) NOT NULL,
  `moneda` VARCHAR(3) NOT NULL DEFAULT 'PEN',
  `metodo_pago` VARCHAR(50) NOT NULL,
  `proveedor_pago` VARCHAR(30) NOT NULL DEFAULT 'MERCADO_PAGO',
  `id_preferencia_externa` VARCHAR(100) NULL DEFAULT NULL,
  `id_pago_externo` VARCHAR(100) NULL DEFAULT NULL,
  `estado_pago` VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
  `estado_detalle` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`id_pago`),
  UNIQUE INDEX `uk_pago_preferencia_externa` (`id_preferencia_externa` ASC) VISIBLE,
  UNIQUE INDEX `uk_pago_externo` (`id_pago_externo` ASC) VISIBLE,
  INDEX `fk_pago_alumno` (`id_alumno` ASC) VISIBLE,
  INDEX `fk_pago_paquete_credito` (`id_paquete_credito` ASC) VISIBLE,
  INDEX `fk_pago_cupon` (`id_cupon` ASC) VISIBLE,
  INDEX `idx_pago_fecha` (`fecha_pago` ASC) VISIBLE,
  INDEX `idx_pago_estado` (`estado_pago` ASC) VISIBLE,
  CONSTRAINT `fk_pago_alumno`
    FOREIGN KEY (`id_alumno`)
    REFERENCES `infinity_futbol_db`.`alumno` (`id_alumno`),
  CONSTRAINT `fk_pago_cupon`
    FOREIGN KEY (`id_cupon`)
    REFERENCES `infinity_futbol_db`.`cupon_descuento` (`id_cupon`),
  CONSTRAINT `fk_pago_paquete_credito`
    FOREIGN KEY (`id_paquete_credito`)
    REFERENCES `infinity_futbol_db`.`paquete_credito` (`id_paquete_credito`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`comprobante`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`comprobante` (
  `id_comprobante` VARCHAR(20) NOT NULL,
  `id_pago` VARCHAR(20) NOT NULL,
  `tipo_comprobante` VARCHAR(30) NOT NULL,
  `serie` VARCHAR(10) NOT NULL,
  `numero` VARCHAR(20) NOT NULL,
  `fecha_emision` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `monto_total` DECIMAL(10,2) NOT NULL,
  `estado` VARCHAR(30) NOT NULL DEFAULT 'EMITIDO',
  PRIMARY KEY (`id_comprobante`),
  UNIQUE INDEX `id_pago` (`id_pago` ASC) VISIBLE,
  UNIQUE INDEX `uk_comprobante_serie_numero` (`serie` ASC, `numero` ASC) VISIBLE,
  INDEX `idx_comprobante_fecha` (`fecha_emision` ASC) VISIBLE,
  CONSTRAINT `fk_comprobante_pago`
    FOREIGN KEY (`id_pago`)
    REFERENCES `infinity_futbol_db`.`pago` (`id_pago`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`cuenta_credito`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`cuenta_credito` (
  `id_cuenta_credito` VARCHAR(20) NOT NULL,
  `id_alumno` VARCHAR(20) NOT NULL,
  `saldo_actual` INT NOT NULL DEFAULT '0',
  `fecha_actualizacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_cuenta_credito`),
  UNIQUE INDEX `id_alumno` (`id_alumno` ASC) VISIBLE,
  CONSTRAINT `fk_cuenta_credito_alumno`
    FOREIGN KEY (`id_alumno`)
    REFERENCES `infinity_futbol_db`.`alumno` (`id_alumno`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`movimiento_credito`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`movimiento_credito` (
  `id_movimiento_credito` VARCHAR(25) NOT NULL,
  `id_cuenta_credito` VARCHAR(20) NOT NULL,
  `id_pago` VARCHAR(20) NULL DEFAULT NULL,
  `id_reserva` VARCHAR(20) NULL DEFAULT NULL,
  `tipo_movimiento` VARCHAR(30) NOT NULL,
  `cantidad` INT NOT NULL,
  `fecha_movimiento` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_expiracion` DATE NULL DEFAULT NULL,
  `descripcion` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_movimiento_credito`),
  UNIQUE INDEX `uk_movimiento_pago_tipo` (`id_pago` ASC, `tipo_movimiento` ASC) VISIBLE,
  INDEX `fk_movimiento_credito_cuenta` (`id_cuenta_credito` ASC) VISIBLE,
  INDEX `fk_movimiento_credito_reserva` (`id_reserva` ASC) VISIBLE,
  INDEX `idx_movimiento_credito_tipo` (`tipo_movimiento` ASC) VISIBLE,
  CONSTRAINT `fk_movimiento_credito_cuenta`
    FOREIGN KEY (`id_cuenta_credito`)
    REFERENCES `infinity_futbol_db`.`cuenta_credito` (`id_cuenta_credito`),
  CONSTRAINT `fk_movimiento_credito_pago`
    FOREIGN KEY (`id_pago`)
    REFERENCES `infinity_futbol_db`.`pago` (`id_pago`),
  CONSTRAINT `fk_movimiento_credito_reserva`
    FOREIGN KEY (`id_reserva`)
    REFERENCES `infinity_futbol_db`.`reserva` (`id_reserva`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`notificacion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`notificacion` (
  `id_notificacion` VARCHAR(20) NOT NULL,
  `id_alumno` VARCHAR(20) NOT NULL,
  `id_reserva` VARCHAR(20) NULL DEFAULT NULL,
  `titulo` VARCHAR(100) NOT NULL,
  `mensaje` VARCHAR(255) NOT NULL,
  `tipo` VARCHAR(50) NOT NULL,
  `fecha_programada` DATETIME NULL DEFAULT NULL,
  `fecha_envio` DATETIME NULL DEFAULT NULL,
  `estado` VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
  PRIMARY KEY (`id_notificacion`),
  UNIQUE INDEX `uk_notificacion_reserva_tipo` (`id_reserva` ASC, `tipo` ASC) VISIBLE,
  INDEX `fk_notificacion_alumno` (`id_alumno` ASC) VISIBLE,
  INDEX `idx_notificacion_estado` (`estado` ASC) VISIBLE,
  INDEX `idx_notificacion_estado_fecha` (`estado` ASC, `fecha_programada` ASC) VISIBLE,
  CONSTRAINT `fk_notificacion_alumno`
    FOREIGN KEY (`id_alumno`)
    REFERENCES `infinity_futbol_db`.`alumno` (`id_alumno`),
  CONSTRAINT `fk_notificacion_reserva`
    FOREIGN KEY (`id_reserva`)
    REFERENCES `infinity_futbol_db`.`reserva` (`id_reserva`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`rol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`rol` (
  `id_rol` VARCHAR(20) NOT NULL,
  `nombre_rol` VARCHAR(50) NOT NULL,
  `descripcion` VARCHAR(150) NULL DEFAULT NULL,
  `estado` TINYINT NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_rol`),
  UNIQUE INDEX `nombre_rol` (`nombre_rol` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `infinity_futbol_db`.`usuario_rol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `infinity_futbol_db`.`usuario_rol` (
  `id_usuario_rol` VARCHAR(25) NOT NULL,
  `id_usuario` VARCHAR(20) NOT NULL,
  `id_rol` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id_usuario_rol`),
  UNIQUE INDEX `uk_usuario_rol` (`id_usuario` ASC, `id_rol` ASC) VISIBLE,
  INDEX `fk_usuario_rol_rol` (`id_rol` ASC) VISIBLE,
  CONSTRAINT `fk_usuario_rol_rol`
    FOREIGN KEY (`id_rol`)
    REFERENCES `infinity_futbol_db`.`rol` (`id_rol`),
  CONSTRAINT `fk_usuario_rol_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `infinity_futbol_db`.`usuario` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
