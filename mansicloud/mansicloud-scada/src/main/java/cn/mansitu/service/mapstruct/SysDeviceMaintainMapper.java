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
package cn.mansitu.service.mapstruct;

import cn.mansitu.base.BaseMapper;
import cn.mansitu.domain.SysDeviceMaintain;
import cn.mansitu.service.dto.SysDeviceMaintainDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
* @website https://mansitu.cn
* @author MansiCloud
* @date 2026-06-21
**/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysDeviceMaintainMapper extends BaseMapper<SysDeviceMaintainDto, SysDeviceMaintain> {

}
