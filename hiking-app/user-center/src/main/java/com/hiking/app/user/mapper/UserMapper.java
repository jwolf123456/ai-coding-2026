package com.hiking.app.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hiking.app.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}