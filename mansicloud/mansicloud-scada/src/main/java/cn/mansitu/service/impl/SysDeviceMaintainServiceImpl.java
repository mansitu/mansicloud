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
package cn.mansitu.service.impl;

import cn.mansitu.domain.SysDeviceMaintain;
import cn.mansitu.repository.SysDeviceMaintainRepository;
import cn.mansitu.service.SysDeviceMaintainService;
import cn.mansitu.service.dto.SysDeviceMaintainDto;
import cn.mansitu.service.dto.SysDeviceMaintainQueryCriteria;
import cn.mansitu.service.mapstruct.SysDeviceMaintainMapper;
import cn.mansitu.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
* @website https://mansitu.cn
* @description 服务实现
* @author MansiCloud
* @date 2026-06-21
**/
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDeviceMaintainServiceImpl implements SysDeviceMaintainService {

    private final SysDeviceMaintainRepository sysDeviceMaintainRepository;
    private final SysDeviceMaintainMapper sysDeviceMaintainMapper;

    @Override
    public PageResult<SysDeviceMaintainDto> queryAll(SysDeviceMaintainQueryCriteria criteria, Pageable pageable){
        Page<SysDeviceMaintain> page = sysDeviceMaintainRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            var predicate = QueryHelp.getPredicate(root, criteria, criteriaBuilder);

            if (criteria.getStatus() == null && criteria.getDeviceName() == null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), 1));
            }

            return predicate;
        }, pageable);
        return PageUtil.toPage(page.map(sysDeviceMaintainMapper::toDto));
    }

    @Override
    public List<SysDeviceMaintainDto> queryAll(SysDeviceMaintainQueryCriteria criteria){
        List<SysDeviceMaintain> list = sysDeviceMaintainRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            var predicate = QueryHelp.getPredicate(root, criteria, criteriaBuilder);

            if (criteria.getStatus() == null && criteria.getDeviceName() == null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), 1));
            }

            return predicate;
        });
        return sysDeviceMaintainMapper.toDto(list);
    }

    @Override
    @Transactional
    public SysDeviceMaintainDto findById(Long id) {
        SysDeviceMaintain sysDeviceMaintain = sysDeviceMaintainRepository.findById(id).orElseGet(SysDeviceMaintain::new);
        ValidationUtil.isNull(sysDeviceMaintain.getId(),"SysDeviceMaintain","id",id);
        return sysDeviceMaintainMapper.toDto(sysDeviceMaintain);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SysDeviceMaintain resources) {
        sysDeviceMaintainRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysDeviceMaintain resources) {
        SysDeviceMaintain sysDeviceMaintain = sysDeviceMaintainRepository.findById(resources.getId()).orElseGet(SysDeviceMaintain::new);
        ValidationUtil.isNull( sysDeviceMaintain.getId(),"SysDeviceMaintain","id",resources.getId());

        cn.mansitu.modules.security.service.dto.JwtUserDto jwtUser =
            (cn.mansitu.modules.security.service.dto.JwtUserDto) cn.mansitu.utils.SecurityUtils.getCurrentUser();

        if (jwtUser != null && jwtUser.getUser() != null) {
            resources.setUpdateBy(jwtUser.getUser().getUsername());
        }

        java.sql.Timestamp currentTime = new java.sql.Timestamp(System.currentTimeMillis());
        resources.setUpdateTime(currentTime);

        sysDeviceMaintain.copy(resources);
        sysDeviceMaintainRepository.save(sysDeviceMaintain);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            SysDeviceMaintain sysDeviceMaintain = sysDeviceMaintainRepository.findById(id).orElse(null);
            if (sysDeviceMaintain != null) {
                sysDeviceMaintain.setDelFlag(1);
                sysDeviceMaintainRepository.save(sysDeviceMaintain);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableOtherRecords(String companyCode, String deviceCode) {
        List<SysDeviceMaintain> records = sysDeviceMaintainRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.and(
                criteriaBuilder.equal(root.get("deviceCode"), deviceCode),
                criteriaBuilder.equal(root.get("status"), 1),
                criteriaBuilder.equal(root.get("delFlag"), 0)
            );
        });

        if (!records.isEmpty()) {
            for (SysDeviceMaintain record : records) {
                record.setStatus(0);
                record.setUpdateTime(new java.sql.Timestamp(System.currentTimeMillis()));
                sysDeviceMaintainRepository.save(record);
            }
            log.info("已将 {} 条旧维保记录置为停用状态", records.size());
        }
    }

    @Override
    public void download(List<SysDeviceMaintainDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SysDeviceMaintainDto sysDeviceMaintain : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("维保周期（天）", sysDeviceMaintain.getCycleDay());
            map.put("预警天数", sysDeviceMaintain.getRemindDays());
            map.put("维保时间", sysDeviceMaintain.getLastMaintainTime());
            map.put("下次维保时间", sysDeviceMaintain.getNextMaintainTime());
            map.put("维保备注", sysDeviceMaintain.getRemark());
            map.put("状态", sysDeviceMaintain.getStatus());
            map.put("逻辑删除", sysDeviceMaintain.getDelFlag());
            map.put(" 创建人",  sysDeviceMaintain.getCreateBy());
            map.put(" 创建时间",  sysDeviceMaintain.getCreateTime());
            map.put(" 更新人",  sysDeviceMaintain.getUpdateBy());
            map.put(" 更新时间",  sysDeviceMaintain.getUpdateTime());
            map.put(" 设备编码",  sysDeviceMaintain.getDeviceCode());
            map.put(" 设备名",  sysDeviceMaintain.getDeviceName());
            map.put(" 维保内容",  sysDeviceMaintain.getContent());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
