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

import cn.mansitu.annotation.DataPermission;
import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.*;
import java.sql.Timestamp;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
* @website https://mansitu.cn
* @description /
* @author dan
* @date 2026-06-08
**/
@Entity
@Data
@Table(name="device_type")
@DataPermission(fieldName = "deptId")
public class DeviceType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @Column(name = "`dept_id`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    @Column(name = "`type_code`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "设备类型编码")
    private String typeCode;

    @Column(name = "`type_name`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "设备类型名称")
    private String typeName;

    @Column(name = "`version`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "版本号")
    private String version;

    @Column(name = "`model_json`")
    @ApiModelProperty(value = "IoT物模型JSON")
    private String modelJson;

    @Column(name = "`config_json`")
    @ApiModelProperty(value = "组态配置JSON")
    private String configJson;

    @Column(name = "`icon`")
    @ApiModelProperty(value = "设备图标")
    private String icon;

    @Column(name = "`remark`")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column(name = "`create_time`")
    @CreationTimestamp
    @ApiModelProperty(value = "createTime")
    private Timestamp createTime;

    @Column(name = "`update_time`")
    @UpdateTimestamp
    @ApiModelProperty(value = "updateTime")
    private Timestamp updateTime;

    @Column(name = "`is_deleted`",nullable = false)
    @NotNull
    @ApiModelProperty(value = "isDeleted")
    private Integer isDeleted;

    public void copy(DeviceType source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
