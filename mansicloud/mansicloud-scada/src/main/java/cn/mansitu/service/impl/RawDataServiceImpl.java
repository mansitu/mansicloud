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

import cn.mansitu.domain.Device;
import cn.mansitu.domain.DeviceType;
import cn.mansitu.domain.RawData;
import cn.mansitu.repository.DeviceRepository;
import cn.mansitu.repository.DeviceTypeRepository;
import cn.mansitu.utils.ValidationUtil;
import cn.mansitu.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import cn.mansitu.repository.RawDataRepository;
import cn.mansitu.service.RawDataService;
import cn.mansitu.service.dto.RawDataDto;
import cn.mansitu.service.dto.RawDataQueryCriteria;
import cn.mansitu.service.dto.HistoryCurveDataDto;
import cn.mansitu.service.dto.LatestDeviceDataResponseDto;
import cn.mansitu.service.mapstruct.RawDataMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import cn.mansitu.utils.PageUtil;
import cn.mansitu.utils.QueryHelp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;

import cn.mansitu.utils.PageResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class RawDataServiceImpl implements RawDataService {

    private final RawDataRepository rawDataRepository;
    private final RawDataMapper rawDataMapper;
    private final DeviceRepository deviceRepository;
    private final DeviceTypeRepository deviceTypeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PageResult<Map<String, Object>> queryAll(RawDataQueryCriteria criteria, Pageable pageable){
        Page<RawData> page = rawDataRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        
        // 转换为动态 DTO
        List<Map<String, Object>> dynamicDataList = new ArrayList<>();
        for (RawData rawData : page.getContent()) {
            Map<String, Object> dynamicData = convertToDynamicMap(rawData);
            dynamicDataList.add(dynamicData);
        }

        PageResult<Map<String, Object>> result = new PageResult<>();
        result.setContent(dynamicDataList);
        result.setTotalElements(page.getTotalElements());
        return result;
    }

    private Map<String, Object> convertToDynamicMap(RawData rawData) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 添加基础字段
        result.put("id", rawData.getId());
        result.put("ts", rawData.getTs());
        result.put("deviceName", rawData.getDeviceName());
        result.put("deviceId", rawData.getDeviceId());

        // 解析 modelJson 并构建动态测点数据
        Map<String, Float> measurePoints = parseMeasurePoints(rawData);
        result.putAll(measurePoints);

        return result;
    }

    @Override
    public List<RawDataDto> queryAll(RawDataQueryCriteria criteria){
        return rawDataMapper.toDto(rawDataRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public RawDataDto findById(Long id) {
        RawData rawData = rawDataRepository.findById(id).orElseGet(RawData::new);
        ValidationUtil.isNull(rawData.getId(),"RawData","id",id);
        return rawDataMapper.toDto(rawData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(RawData resources) {
        rawDataRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RawData resources) {
        RawData rawData = rawDataRepository.findById(resources.getId()).orElseGet(RawData::new);
        ValidationUtil.isNull( rawData.getId(),"RawData","id",resources.getId());
        rawData.copy(resources);
        rawDataRepository.save(rawData);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            rawDataRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<RawDataDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RawDataDto rawData : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("采集时间戳", rawData.getTs());
            map.put("设备名称", rawData.getDeviceName());
            map.put("设备类型", rawData.getDeviceId());
            map.put("测点1", rawData.getV1());
            map.put("测点2", rawData.getV2());
            map.put("测点3", rawData.getV3());
            map.put("测点4", rawData.getV4());
            map.put("测点5", rawData.getV5());
            map.put("测点6", rawData.getV6());
            map.put("测点7", rawData.getV7());
            map.put("测点8", rawData.getV8());
            map.put("测点9", rawData.getV9());
            map.put("测点10", rawData.getV10());
            map.put("测点11", rawData.getV11());
            map.put("测点12", rawData.getV12());
            map.put("测点13", rawData.getV13());
            map.put("测点14", rawData.getV14());
            map.put("测点15", rawData.getV15());
            map.put("测点16", rawData.getV16());
            map.put("测点17", rawData.getV17());
            map.put("测点18", rawData.getV18());
            map.put("测点19", rawData.getV19());
            map.put("测点20", rawData.getV20());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public LatestDeviceDataResponseDto getLatestDeviceDataGrouped() {
        LatestDeviceDataResponseDto response = new LatestDeviceDataResponseDto();
        
        try {
            Map<String, List<Map<String, Object>>> groupedData = new LinkedHashMap<>();
            int totalCount = 0;

            // 查询所有设备
            List<Device> devices = deviceRepository.findAll();

            for (Device device : devices) {
                // 根据设备名称查询最新的原始数据
                Specification<RawData> spec = (root, query, cb) -> {
                    query.orderBy(cb.desc(root.get("ts")));
                    return cb.equal(root.get("deviceName"), device.getDeviceCode());
                };

                Pageable pageable = PageRequest.of(0, 1);
                Page<RawData> latestDataPage = rawDataRepository.findAll(spec, pageable);

                if (!latestDataPage.isEmpty()) {
                    RawData latestData = latestDataPage.getContent().get(0);

                    // 转换为 Map 结构
                    Map<String, Object> dataMap = convertToMap(latestData);

                    // 按设备类型分组
                    String typeCode = latestData.getDeviceId();
                    groupedData.computeIfAbsent(typeCode, k -> new ArrayList<>()).add(dataMap);
                    
                    totalCount++;
                }
            }

            response.setGroupedData(groupedData);
            response.setTotalCount(totalCount);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    public List<HistoryCurveDataDto> getHistoryCurveData(String devname, String startTime, String endTime) {
        List<HistoryCurveDataDto> result = new ArrayList<>();

        try {
            // 解析时间范围
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(startTime, formatter);
            LocalDateTime end = LocalDateTime.parse(endTime, formatter);

            // 构建查询条件
            Specification<RawData> spec = (root, query, cb) -> {
                List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();

                // 设备名称条件
                if (devname != null && !devname.isEmpty()) {
                    predicates.add(cb.equal(root.get("deviceName"), devname));
                }

                // 时间范围条件
                if (start != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("ts"), java.sql.Timestamp.valueOf(start)));
                }
                if (end != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("ts"), java.sql.Timestamp.valueOf(end)));
                }

                return cb.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
            };

            // 按时间升序查询
            org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.ASC, "ts"
            );
            List<RawData> rawDataList = rawDataRepository.findAll(spec, sort);

            // 转换为简化的历史曲线 DTO
            for (RawData rawData : rawDataList) {
                HistoryCurveDataDto dto = new HistoryCurveDataDto();
                dto.setTs(rawData.getTs());

                // 解析测点数据并展开到顶层
                Map<String, Float> measurePoints = parseMeasurePoints(rawData);
                for (Map.Entry<String, Float> entry : measurePoints.entrySet()) {
                    dto.setDynamicField(entry.getKey(), entry.getValue());
                }

                result.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private Map<String, Float> parseMeasurePoints(RawData rawData) {
        Map<String, Float> measurePoints = new LinkedHashMap<>();

        try {
            // 根据 deviceId (typeCode) 查询设备类型
            Optional<DeviceType> deviceTypeOpt = deviceTypeRepository.findByTypeCode(rawData.getDeviceId());
            if (!deviceTypeOpt.isPresent()) {
                return measurePoints;
            }

            DeviceType deviceType = deviceTypeOpt.get();
            String modelJson = deviceType.getModelJson();
            if (modelJson == null || modelJson.isEmpty()) {
                return measurePoints;
            }

            // 解析 modelJson
            Map<String, Object> modelMap = objectMapper.readValue(modelJson, new TypeReference<Map<String, Object>>() {});
            List<Map<String, Object>> properties = (List<Map<String, Object>>) modelMap.get("properties");

            if (properties == null || properties.isEmpty()) {
                return measurePoints;
            }

            // 根据 dataFlag 映射到实际的测点值
            for (Map<String, Object> property : properties) {
                String dataFlag = (String) property.get("dataFlag");
                String name = (String) property.get("name");
                String unit = (String) property.get("unit");

                if (dataFlag == null || name == null) {
                    continue;
                }

                // 构建 key: name + unit (如果 unit 存在)
                String key = unit != null && !unit.isEmpty() ? name + "_" + unit : name;

                // 从 rawData 中获取对应的值
                Float value = getMeasurePointValue(rawData, dataFlag);
                if (value != null) {
                    measurePoints.put(key, value);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return measurePoints;
    }

    private Float getMeasurePointValue(RawData rawData, String dataFlag) {
        switch (dataFlag.toLowerCase()) {
            case "v1": return rawData.getV1();
            case "v2": return rawData.getV2();
            case "v3": return rawData.getV3();
            case "v4": return rawData.getV4();
            case "v5": return rawData.getV5();
            case "v6": return rawData.getV6();
            case "v7": return rawData.getV7();
            case "v8": return rawData.getV8();
            case "v9": return rawData.getV9();
            case "v10": return rawData.getV10();
            case "v11": return rawData.getV11();
            case "v12": return rawData.getV12();
            case "v13": return rawData.getV13();
            case "v14": return rawData.getV14();
            case "v15": return rawData.getV15();
            case "v16": return rawData.getV16();
            case "v17": return rawData.getV17();
            case "v18": return rawData.getV18();
            case "v19": return rawData.getV19();
            case "v20": return rawData.getV20();
            default: return null;
        }
    }

    private Map<String, Object> convertToMap(RawData rawData) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", rawData.getId());
        map.put("ts", rawData.getTs());
        map.put("devname", rawData.getDeviceName());
        map.put("devtype", rawData.getDeviceId());

        // 解析 modelJson 并构建动态测点数据
        Map<String, Float> measurePoints = parseMeasurePoints(rawData);
        map.putAll(measurePoints);

        return map;
    }
}
