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
public class DeviceTypeDto implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    @ApiModelProperty(value = "设备类型编码")
    private String typeCode;

    @ApiModelProperty(value = "设备类型名称")
    private String typeName;

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "IoT物模型JSON")
    private String modelJson;

    @ApiModelProperty(value = "组态配置JSON")
    private String configJson;

    @ApiModelProperty(value = "设备图标")
    private String icon;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "createTime")
    private Timestamp createTime;

    @ApiModelProperty(value = "updateTime")
    private Timestamp updateTime;

    @ApiModelProperty(value = "isDeleted")
    private Integer isDeleted;
}
