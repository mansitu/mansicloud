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
import javax.validation.constraints.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.*;
import java.sql.Timestamp;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
* @website https://mansitu.cn
* @description /
* @author mansicloud
* @date 2026-06-15
**/
@Entity
@Data
@Table(name="raw_data")
public class RawData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @ApiModelProperty(value = "自增主键")
    private Long id;

    @Column(name = "`ts`",nullable = false)
    @NotNull
    @CreationTimestamp
    @ApiModelProperty(value = "采集时间戳")
    private Timestamp ts;

    @Column(name = "`device_name`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @Column(name = "`device_id`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "设备类型")
    private String deviceId;

    @Column(name = "`v1`")
    @ApiModelProperty(value = "测点值1")
    private Float v1;

    @Column(name = "`v2`")
    @ApiModelProperty(value = "测点值2")
    private Float v2;

    @Column(name = "`v3`")
    @ApiModelProperty(value = "测点值3")
    private Float v3;

    @Column(name = "`v4`")
    @ApiModelProperty(value = "测点值4")
    private Float v4;

    @Column(name = "`v5`")
    @ApiModelProperty(value = "测点值5")
    private Float v5;

    @Column(name = "`v6`")
    @ApiModelProperty(value = "测点值6")
    private Float v6;

    @Column(name = "`v7`")
    @ApiModelProperty(value = "测点值7")
    private Float v7;

    @Column(name = "`v8`")
    @ApiModelProperty(value = "测点值8")
    private Float v8;

    @Column(name = "`v9`")
    @ApiModelProperty(value = "测点值9")
    private Float v9;

    @Column(name = "`v10`")
    @ApiModelProperty(value = "测点值10")
    private Float v10;

    @Column(name = "`v11`")
    @ApiModelProperty(value = "测点值11")
    private Float v11;

    @Column(name = "`v12`")
    @ApiModelProperty(value = "测点值12")
    private Float v12;

    @Column(name = "`v13`")
    @ApiModelProperty(value = "测点值13")
    private Float v13;

    @Column(name = "`v14`")
    @ApiModelProperty(value = "测点值14")
    private Float v14;

    @Column(name = "`v15`")
    @ApiModelProperty(value = "测点值15")
    private Float v15;

    @Column(name = "`v16`")
    @ApiModelProperty(value = "测点值16")
    private Float v16;

    @Column(name = "`v17`")
    @ApiModelProperty(value = "测点值17")
    private Float v17;

    @Column(name = "`v18`")
    @ApiModelProperty(value = "测点值18")
    private Float v18;

    @Column(name = "`v19`")
    @ApiModelProperty(value = "测点值19")
    private Float v19;

    @Column(name = "`v20`")
    @ApiModelProperty(value = "测点值20")
    private Float v20;

    public void copy(RawData source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
