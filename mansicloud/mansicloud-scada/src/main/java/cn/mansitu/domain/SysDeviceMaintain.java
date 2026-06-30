/*
*  Copyright 2025-2026 Zhao Dan
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package cn.mansitu.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

/**
* @website https://mansitu.cn
* @description /
* @author MansiCloud
* @date 2026-06-21
**/
@Entity
@Data
@Table(name="sys_device_maintain")
public class SysDeviceMaintain implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @Column(name = "`cycle_day`",nullable = false)
    @NotNull
    @ApiModelProperty(value = "维保周期（天）")
    private Integer cycleDay;

    @Column(name = "`remind_days`",nullable = false)
    @NotNull
    @ApiModelProperty(value = "提前N天预警")
    private Integer remindDays;

    @Column(name = "`last_maintain_time`")
    @ApiModelProperty(value = "最新一次维保时间（页面展示最新保养记录）")
    private Timestamp lastMaintainTime;

    @Column(name = "`next_maintain_time`",nullable = false)
    @NotNull
    @ApiModelProperty(value = "计划下次维保时间")
    private Timestamp nextMaintainTime;

    @Column(name = "`remark`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "维保备注")
    private String remark;

    @Column(name = "`status`",nullable = false)
    @NotNull
    @ApiModelProperty(value = "状态 0停用 1启用")
    private Integer status;

    @Column(name = "`del_flag`",nullable = false)
    @NotNull
    @ApiModelProperty(value = "逻辑删除")
    private Integer delFlag;

    @Column(name = "`create_by`")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "`create_time`")
    @ApiModelProperty(value = "createTime")
    private Timestamp createTime;

    @Column(name = "`update_by`")
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @Column(name = "`update_time`")
    @ApiModelProperty(value = "updateTime")
    private Timestamp updateTime;

    @Column(name = "`device_code`")
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @Column(name = "`device_name`")
    @ApiModelProperty(value = "设备名")
    private String deviceName;

    @Column(name = "`content`", length = 512)
    @ApiModelProperty(value = "维保内容")
    private String content;

    public void copy(SysDeviceMaintain source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
