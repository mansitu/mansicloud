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
package cn.mansitu.service.impl;

import cn.mansitu.domain.DeviceType;
import cn.mansitu.modules.security.service.dto.JwtUserDto;
import cn.mansitu.modules.system.domain.Dept;
import cn.mansitu.modules.system.repository.DeptRepository;
import cn.mansitu.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import cn.mansitu.repository.DeviceTypeRepository;
import cn.mansitu.service.DeviceTypeService;
import cn.mansitu.service.dto.DeviceTypeDto;
import cn.mansitu.service.dto.DeviceTypeQueryCriteria;
import cn.mansitu.service.mapstruct.DeviceTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Optional;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceTypeServiceImpl implements DeviceTypeService {

    private final DeviceTypeRepository deviceTypeRepository;
    private final DeviceTypeMapper deviceTypeMapper;
    private final DeptRepository deptRepository;

    private static final int MAX_JSON_SIZE = 65535;
    private static final int MAX_DELETE_COUNT = 1000;
    private static final int MAX_EXPORT_COUNT = 10000;

    @Override
    public PageResult<DeviceTypeDto> queryAll(DeviceTypeQueryCriteria criteria, Pageable pageable){
        Page<DeviceType> page = deviceTypeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(deviceTypeMapper::toDto));
    }

    @Override
    public List<DeviceTypeDto> queryAll(DeviceTypeQueryCriteria criteria){
        return deviceTypeMapper.toDto(deviceTypeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DeviceTypeDto findById(Long id) {
        DeviceType deviceType = deviceTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DeviceType不存在，id: " + id));
        return deviceTypeMapper.toDto(deviceType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DeviceType resources) {
        Optional.ofNullable(SecurityUtils.getCurrentUser()).ifPresent(user -> {
            if (user instanceof JwtUserDto) {
                JwtUserDto jwtUser = (JwtUserDto) user;

                Long deptId = jwtUser.getUser().getDept().getId();
                if (deptId != null) {
                    resources.setDeptId(deptId);
                    log.info("设置部门信息 - deptId: {}", deptId);
                } else {
                    throw new IllegalArgumentException("用户未分配部门");
                }
            }
        });

        deviceTypeRepository.save(resources);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DeviceType resources) {
        DeviceType deviceType = deviceTypeRepository.findById(resources.getId())
                .orElseThrow(() -> new IllegalArgumentException("DeviceType不存在，id: " + resources.getId()));
        handleJsonFields(resources);
        deviceType.copy(resources);
        deviceTypeRepository.save(deviceType);
    }

    private void handleJsonFields(DeviceType deviceType) {
        deviceType.setModelJson(cleanJson(deviceType.getModelJson(), "设备模型JSON"));
        deviceType.setConfigJson(cleanJson(deviceType.getConfigJson(), "组态配置JSON"));
    }

    private String cleanJson(String json, String fieldName) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        if (json.length() > MAX_JSON_SIZE) {
            throw new IllegalArgumentException(fieldName + "不能超过64KB");
        }
        return json;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }

        if (ids.length > MAX_DELETE_COUNT) {
            throw new IllegalArgumentException("单次最多删除" + MAX_DELETE_COUNT + "条记录");
        }

        deviceTypeRepository.updateIsDeletedByIds(Arrays.asList(ids), 1);
    }

    @Override
    public void download(List<DeviceTypeDto> all, HttpServletResponse response) throws IOException {
        if (all.size() > MAX_EXPORT_COUNT) {
            throw new IllegalArgumentException("导出数据量过大，请缩小查询范围（最多" + MAX_EXPORT_COUNT + "条）");
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (DeviceTypeDto deviceType : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("设备类型编码", deviceType.getTypeCode());
            map.put("设备类型名称", deviceType.getTypeName());
            map.put("版本号", deviceType.getVersion());
            map.put("IoT物模型JSON", deviceType.getModelJson());
            map.put("组态配置JSON", deviceType.getConfigJson());
            map.put("设备图标", deviceType.getIcon());
            map.put("备注", deviceType.getRemark());
            map.put("创建时间", deviceType.getCreateTime());
            map.put("更新时间", deviceType.getUpdateTime());
            map.put("是否删除", deviceType.getIsDeleted());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
