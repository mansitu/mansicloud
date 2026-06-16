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

import cn.mansitu.service.DeviceService;
import cn.mansitu.domain.Device;
import cn.mansitu.service.mapstruct.DeviceMapper;
import cn.mansitu.repository.DeviceRepository;
import cn.mansitu.exception.BadRequestException;
import cn.mansitu.exception.EntityExistException;
import cn.mansitu.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import cn.mansitu.service.dto.DeviceDto;
import cn.mansitu.service.dto.DeviceQueryCriteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import cn.mansitu.utils.PageUtil;
import cn.mansitu.utils.QueryHelp;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import cn.mansitu.utils.PageResult;

/**
* @website https://mansitu.cn
* @description 服务实现
* @author dan
* @date 2026-06-08
**/
@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;

    private static final int MAX_DELETE_COUNT = 1000;
    private static final int MAX_EXPORT_COUNT = 10000;

    @Override
    public PageResult<DeviceDto> queryAll(DeviceQueryCriteria criteria, Pageable pageable){
        Page<Device> page = deviceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(deviceMapper::toDto));
    }

    @Override
    public List<DeviceDto> queryAll(DeviceQueryCriteria criteria){
        return deviceMapper.toDto(deviceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DeviceDto findById(Long id) {
        Device device = deviceRepository.findById(id).orElse(null);
        if (device == null) {
            throw new BadRequestException("设备不存在");
        }
        return deviceMapper.toDto(device);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Device resources) {
        if (deviceRepository.findByDeviceCode(resources.getDeviceCode()) != null) {
            throw new EntityExistException(Device.class, "device_code", resources.getDeviceCode());
        }
        deviceRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Device resources) {
        Device device = deviceRepository.findById(resources.getId())
                .orElseThrow(() -> new BadRequestException("设备不存在"));

        if (resources.getDeviceCode() != null && !resources.getDeviceCode().trim().isEmpty()) {
            Device existingDevice = deviceRepository.findByDeviceCode(resources.getDeviceCode());
            if (existingDevice != null
                    && existingDevice.getId() != null
                    && !existingDevice.getId().equals(resources.getId())) {
                throw new EntityExistException(Device.class, "device_code", resources.getDeviceCode());
            }
        }

        device.copy(resources);
        deviceRepository.save(device);
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

        deviceRepository.deleteAllByIds(Arrays.asList(ids));
    }

    @Override
    public void download(List<DeviceDto> all, HttpServletResponse response) throws IOException {
        if (all.size() > MAX_EXPORT_COUNT) {
            throw new IllegalArgumentException("导出数据量过大，请缩小查询范围（最多" + MAX_EXPORT_COUNT + "条）");
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (DeviceDto device : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("设备类型编码", device.getTypeCode());
            map.put("网关编号", device.getGatewaySn());
            map.put("设备名称", device.getDeviceName());
            map.put("设备唯一编码", device.getDeviceCode());
            map.put("状态", device.getStatus());
            map.put("在线状态", device.getOnline());
            map.put("备注", device.getRemark());
            map.put("创建时间", device.getCreateTime());
            map.put("更新时间", device.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
