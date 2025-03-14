package com.xzp.forum.dao;

import com.xzp.forum.model.UserModeratorPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserModeratorPermissionDao {
    String TABLE_NAME = "user_moderator_permissions";
    String INSERT_FIELDS = "user_id, section";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS + ", created_time";

    // 插入单个权限记录
    int addPermission(UserModeratorPermission permission);

    // 批量插入权限记录
    int addPermissions(@Param("permissions") List<UserModeratorPermission> permissions);

    // 获取用户的所有版块权限
    List<String> findSectionsByUserId(@Param("userId") Long userId);

    // 删除用户的所有版块权限
    int deleteByUserId(@Param("userId") Long userId);

    // 删除用户的指定版块权限
    int deleteByUserIdAndSection(@Param("userId") Long userId, @Param("section") String section);
}
