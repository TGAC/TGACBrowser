-- Valentina Studio --
-- MySQL dump --
-- ---------------------------------------------------------


/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
-- ---------------------------------------------------------


-- CREATE TABLE "blast_params" -----------------------------
DROP TABLE IF EXISTS `blast_params` CASCADE;

CREATE TABLE `blast_params` (
  `id_blast`      VARCHAR(45)
                  CHARACTER SET latin1
                  COLLATE latin1_swedish_ci NOT NULL,
  `blast_db`      VARCHAR(255)
                  CHARACTER SET latin1
                  COLLATE latin1_swedish_ci NULL,
  `blast_seq`     LONGTEXT
                  CHARACTER SET latin1
                  COLLATE latin1_swedish_ci NULL,
  `blast_filter`  VARCHAR(45)
                  CHARACTER SET latin1
                  COLLATE latin1_bin        NULL,
  `blast_type`    VARCHAR(255)
                  CHARACTER SET latin1
                  COLLATE latin1_swedish_ci NULL,
  `link`          VARCHAR(45)
                  CHARACTER SET latin1
                  COLLATE latin1_swedish_ci NULL,
  `output_format` VARCHAR(255)
                  CHARACTER SET latin1
                  COLLATE latin1_swedish_ci NULL,
  PRIMARY KEY (`id_blast`)
)
  CHARACTER SET = latin1
  COLLATE = latin1_swedish_ci
  ENGINE = InnoDB;
-- ---------------------------------------------------------


-- CREATE TABLE "blast_result" -----------------------------
DROP TABLE IF EXISTS `blast_result` CASCADE;

CREATE TABLE `blast_result` (
  `id_blast`    VARCHAR(45)
                CHARACTER SET latin1
                COLLATE latin1_swedish_ci NOT NULL,
  `result_json` LONGTEXT
                CHARACTER SET latin1
                COLLATE latin1_swedish_ci NULL
)
  CHARACTER SET = latin1
  COLLATE = latin1_swedish_ci
  ENGINE = InnoDB;
-- ---------------------------------------------------------


-- CREATE TABLE "blast_status" -----------------------------
DROP TABLE IF EXISTS `blast_status` CASCADE;

CREATE TABLE `blast_status` (
  `id_blast` VARCHAR(45)
             CHARACTER SET latin1
             COLLATE latin1_swedish_ci NOT NULL,
  `status`   VARCHAR(45)
             CHARACTER SET latin1
             COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`id_blast`)
)
  CHARACTER SET = latin1
  COLLATE = latin1_swedish_ci
  ENGINE = InnoDB;
-- ---------------------------------------------------------


-- CREATE INDEX "id_blast_UNIQUE" --------------------------
CREATE UNIQUE INDEX `id_blast_UNIQUE` USING BTREE ON `blast_params` (`id_blast`);
-- ---------------------------------------------------------


-- CREATE INDEX "id_blast_UNIQUE" --------------------------
CREATE UNIQUE INDEX `id_blast_UNIQUE` USING BTREE ON `blast_status` (`id_blast`);
-- ---------------------------------------------------------


/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
-- ---------------------------------------------------------


