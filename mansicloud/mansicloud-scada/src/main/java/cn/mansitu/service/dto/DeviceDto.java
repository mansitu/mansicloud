/*
*  Copyright 2019-2025 Zheng Jie
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

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;
import io.swagger.annotations.ApiModelProperty;

/**
* @website https://mansitu.cn
* @description /
* @author dan
* @date 2026-06-08
**/
@Data
public class DeviceDto implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    @ApiModelProperty(value = "设备类型编码")
    private String typeCode;

    @ApiModelProperty(value = "网关编号")
    private String gatewaySn;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备唯一编码")
    private String deviceCode;

    @ApiModelProperty(value = "状态 1=正常 0=禁用")
    private Integer status;

    @ApiModelProperty(value = "在线状态 0=离线 1=在线")
    private Integer online;

    @ApiModelProperty(value = "remark")
    private String remark;

    @ApiModelProperty(value = "createTime")
    private Timestamp createTime;

    @ApiModelProperty(value = "updateTime")
    private Timestamp updateTime;
}
