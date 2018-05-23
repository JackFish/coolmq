CREATE DATABASE IF NOT EXISTS provider default charset utf8 COLLATE utf8_general_ci;
CREATE TABLE IF NOT EXISTS provider.user(
  `id` varchar(64) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `age` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;