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

import cn.mansitu.annotation.Query;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @website https://mansitu.cn
* @author MansiCloud
* @date 2026-06-21
**/
@Data
public class SysDeviceMaintainQueryCriteria{

    @Query(propName = "delFlag", type = Query.Type.EQUAL)
    @ApiModelProperty(value = "删除标记（默认查询未删除的数据）")
    private Integer delFlag = 0;

    @Query(propName = "deviceName", type = Query.Type.EQUAL)
    @ApiModelProperty(value = "设备名称（精确查询）")
    private String deviceName;

    @Query(propName = "status", type = Query.Type.EQUAL)
    @ApiModelProperty(value = "状态（不传则查询所有状态）")
    private Integer status;
}
