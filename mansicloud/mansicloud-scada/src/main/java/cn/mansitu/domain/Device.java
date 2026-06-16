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
package cn.mansitu.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import java.sql.Timestamp;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
* @website https://mansitu.cn
* @description /
* @author dan
* @date 2026-06-08
**/
@Entity
@Data
@Table(name="device")
public class Device implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "`type_code`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "设备类型编码 device_type.type_code")
    private String typeCode;

    @Column(name = "`dept_id`")
    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    @Column(name = "`gateway_sn`")
    @ApiModelProperty(value = "网关编号")
    private String gatewaySn;

    @Column(name = "`device_name`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @Column(name = "`device_code`",unique = true)
    @ApiModelProperty(value = "设备唯一编码")
    private String deviceCode;

    @Column(name = "`status`")
    @ApiModelProperty(value = "状态 1=正常 0=禁用")
    private Integer status;

    @Column(name = "`online`")
    @ApiModelProperty(value = "在线状态 0=离线 1=在线")
    private Integer online;

    @Column(name = "`remark`")
    @ApiModelProperty(value = "remark")
    private String remark;

    @Column(name = "`create_time`")
    @ApiModelProperty(value = "createTime")
    private Timestamp createTime;

    @Column(name = "`update_time`")
    @ApiModelProperty(value = "updateTime")
    private Timestamp updateTime;

    public void copy(Device source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
