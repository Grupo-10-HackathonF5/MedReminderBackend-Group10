CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','USER') NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `medications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `dosage_quantity` int NOT NULL,
  `dosage_unit` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `notes` varchar(1000) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsae8ns7nscnqntu61xu8xxwl3` (`user_id`),
  CONSTRAINT `FKsae8ns7nscnqntu61xu8xxwl3` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `posologies` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `day_time` time(6) NOT NULL,
  `doses_number` double NOT NULL,
  `end_date` date DEFAULT NULL,
  `frequency_unit` enum('DAYS','HOURS','MONTHS','WEEKS') NOT NULL,
  `frequency_value` int NOT NULL,
  `quantity` double NOT NULL,
  `reminder_message` varchar(255) DEFAULT NULL,
  `start_date` date NOT NULL,
  `medication_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK84ap64hx3g63c1aloa51eqk3` (`medication_id`),
  KEY `FK5d8o1ymv0ourvecoxmd7rs2e4` (`user_id`),
  CONSTRAINT `FK5d8o1ymv0ourvecoxmd7rs2e4` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK84ap64hx3g63c1aloa51eqk3` FOREIGN KEY (`medication_id`) REFERENCES `medications` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `doses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_taken` bit(1) DEFAULT NULL,
  `scheduled_date_time` datetime(6) DEFAULT NULL,
  `scheduled_day` date DEFAULT NULL,
  `taken_time` datetime(6) DEFAULT NULL,
  `posology_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2wnxxhiqkqwl6uak6pn2h2wyk` (`posology_id`),
  KEY `FK31ck2prxo9c95rqumfmaiuoyu` (`user_id`),
  CONSTRAINT `FK2wnxxhiqkqwl6uak6pn2h2wyk` FOREIGN KEY (`posology_id`) REFERENCES `posologies` (`id`),
  CONSTRAINT `FK31ck2prxo9c95rqumfmaiuoyu` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
