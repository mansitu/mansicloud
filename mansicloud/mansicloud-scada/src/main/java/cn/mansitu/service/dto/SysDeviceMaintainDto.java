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
package cn.mansitu.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
* @website https://mansitu.cn
* @description /
* @author MansiCloud
* @date 2026-06-21
**/
@Data
public class SysDeviceMaintainDto implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "维保周期（天）")
    private Integer cycleDay;

    @ApiModelProperty(value = "提前N天预警")
    private Integer remindDays;

    @ApiModelProperty(value = "维保时间")
    private Timestamp lastMaintainTime;

    @ApiModelProperty(value = "下次维保时间")
    private Timestamp nextMaintainTime;

    @ApiModelProperty(value = "维保备注")
    private String remark;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除")
    private Integer delFlag;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "createTime")
    private Timestamp createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "updateTime")
    private Timestamp updateTime;

    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @ApiModelProperty(value = "设备名")
    private String deviceName;

    @ApiModelProperty(value = "维保内容")
    private String content;
}
