package com.hiking.app.line.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hiking.app.line.entity.Line;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LineMapper extends BaseMapper<Line> {
}