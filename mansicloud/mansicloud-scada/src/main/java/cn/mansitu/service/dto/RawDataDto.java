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
* @author mansicloud
* @date 2026-06-15
**/
@Data
public class RawDataDto implements Serializable {

    @ApiModelProperty(value = "自增主键")
    private Long id;

    @ApiModelProperty(value = "采集时间戳")
    private Timestamp ts;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备类型")
    private String deviceId;

    @ApiModelProperty(value = "测点值1")
    private Float v1;

    @ApiModelProperty(value = "测点值2")
    private Float v2;

    @ApiModelProperty(value = "测点值3")
    private Float v3;

    @ApiModelProperty(value = "测点值4")
    private Float v4;

    @ApiModelProperty(value = "测点值5")
    private Float v5;

    @ApiModelProperty(value = "测点值6")
    private Float v6;

    @ApiModelProperty(value = "测点值7")
    private Float v7;

    @ApiModelProperty(value = "测点值8")
    private Float v8;

    @ApiModelProperty(value = "测点值9")
    private Float v9;

    @ApiModelProperty(value = "测点值10")
    private Float v10;

    @ApiModelProperty(value = "测点值11")
    private Float v11;

    @ApiModelProperty(value = "测点值12")
    private Float v12;

    @ApiModelProperty(value = "测点值13")
    private Float v13;

    @ApiModelProperty(value = "测点值14")
    private Float v14;

    @ApiModelProperty(value = "测点值15")
    private Float v15;

    @ApiModelProperty(value = "测点值16")
    private Float v16;

    @ApiModelProperty(value = "测点值17")
    private Float v17;

    @ApiModelProperty(value = "测点值18")
    private Float v18;

    @ApiModelProperty(value = "测点值19")
    private Float v19;

    @ApiModelProperty(value = "测点值20")
    private Float v20;
}
