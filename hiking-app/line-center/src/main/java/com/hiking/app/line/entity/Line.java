package com.hiking.app.line.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * <p>
 * 线路表
 * </p>
 *
 * @author hiking-app
 * @since 2026-10-24
 */
@Getter
@Setter
@ToString
@TableName("t_line")
public class Line extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 线路标题
     */
    @TableField("title")
    private String title;

    /**
     * 线路描述
     */
    @TableField("description")
    private String description;

    /**
     * 距离(公里)
     */
    @TableField("distance")
    private BigDecimal distance;

    /**
     * 难度 1-简单 2-中等 3-困难 4-极难
     */
    @TableField("difficulty")
    private Integer difficulty;

    /**
     * 起点
     */
    @TableField("start_point")
    private String startPoint;

    /**
     * 终点
     */
    @TableField("end_point")
    private String endPoint;

    /**
     * 封面图
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 状态 1-上线 0-下线
     */
    @TableField("status")
    private Integer status;
}
