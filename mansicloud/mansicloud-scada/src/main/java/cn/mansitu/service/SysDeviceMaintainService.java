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
package cn.mansitu.service;

import cn.mansitu.domain.SysDeviceMaintain;
import cn.mansitu.service.dto.SysDeviceMaintainDto;
import cn.mansitu.service.dto.SysDeviceMaintainQueryCriteria;
import cn.mansitu.utils.PageResult;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
* @website https://mansitu.cn
* @description 服务接口
* @author MansiCloud
* @date 2026-06-21
**/
public interface SysDeviceMaintainService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    PageResult<SysDeviceMaintainDto> queryAll(SysDeviceMaintainQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<SysDeviceMaintainDto>
    */
    List<SysDeviceMaintainDto> queryAll(SysDeviceMaintainQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return SysDeviceMaintainDto
     */
    SysDeviceMaintainDto findById(Long id);

    /**
    * 创建
    * @param resources /
    */
    void create(SysDeviceMaintain resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(SysDeviceMaintain resources);

    /**
    * 多选删除
    * @param ids /
    */
    void deleteAll(Long[] ids);

    void disableOtherRecords(String companyCode, String deviceCode);

    void download(List<SysDeviceMaintainDto> all, HttpServletResponse response) throws IOException;
}
