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
package cn.mansitu.service;

import cn.mansitu.domain.RawData;
import cn.mansitu.service.dto.RawDataQueryCriteria;
import cn.mansitu.service.dto.RawDataDto;
import cn.mansitu.service.dto.HistoryCurveDataDto;
import cn.mansitu.service.dto.LatestDeviceDataResponseDto;
import cn.mansitu.utils.PageResult;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface RawDataService {

    PageResult<Map<String, Object>> queryAll(RawDataQueryCriteria criteria, Pageable pageable);

    List<RawDataDto> queryAll(RawDataQueryCriteria criteria);

    RawDataDto findById(Long id);

    void create(RawData resources);

    void update(RawData resources);

    void deleteAll(Long[] ids);

    void download(List<RawDataDto> all, HttpServletResponse response) throws IOException;

    List<HistoryCurveDataDto> getHistoryCurveData(String devname, String startTime, String endTime);

    LatestDeviceDataResponseDto getLatestDeviceDataGrouped();
}
