ALTER TABLE `user`
ADD COLUMN `email` varchar(100) DEFAULT NULL,
ADD COLUMN `avatar` varchar(255) DEFAULT NULL,
ADD COLUMN `is_admin` boolean DEFAULT FALSE,
ADD COLUMN `is_moderator` boolean DEFAULT FALSE,
ADD COLUMN `role` varchar(20) NOT NULL DEFAULT 'USER';

-- 创建版主权限表
CREATE TABLE IF NOT EXISTS `user_moderator_permissions` (
  `user_id` bigint(20) NOT NULL,
  `section` varchar(50) NOT NULL,
  PRIMARY KEY (`user_id`, `section`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
