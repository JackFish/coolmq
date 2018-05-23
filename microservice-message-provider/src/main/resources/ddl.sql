DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(64) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `age` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;