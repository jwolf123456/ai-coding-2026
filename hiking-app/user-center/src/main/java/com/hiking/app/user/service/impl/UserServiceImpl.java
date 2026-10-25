package com.hiking.app.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hiking.app.user.entity.User;
import com.hiking.app.user.mapper.UserMapper;
import com.hiking.app.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}