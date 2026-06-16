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
package cn.mansitu.rest;

import cn.mansitu.annotation.Log;
import cn.mansitu.domain.RawData;
import cn.mansitu.service.RawDataService;
import cn.mansitu.service.dto.RawDataQueryCriteria;
import cn.mansitu.service.dto.RawDataDto;
import cn.mansitu.service.dto.HistoryCurveDataDto;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import cn.mansitu.utils.PageResult;
import java.util.List;
import java.util.Map;

/**
* @website https://mansitu.cn
* @author mansicloud
* @date 2026-06-15
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "rawdata")
@RequestMapping("/api/rawData")
public class RawDataController {

    private final RawDataService rawDataService;

    @GetMapping("/latest")
    @PreAuthorize("@el.check('rawData:list')")
    @ApiOperation("获取最新设备数据")
    public ResponseEntity<Object> getLatestDeviceData() {
        return new ResponseEntity<>(rawDataService.getLatestDeviceDataGrouped(), HttpStatus.OK);
    }

    @GetMapping("/history")
    @ApiOperation("获取历史曲线数据")
    @PreAuthorize("@el.check('rawData:list')")
    public ResponseEntity<Object> getHistoryCurveData(
            @RequestParam String devname,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        return new ResponseEntity<>(rawDataService.getHistoryCurveData(devname, startTime, endTime), HttpStatus.OK);
    }

    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('rawData:list')")
    public void exportRawData(HttpServletResponse response, RawDataQueryCriteria criteria) throws IOException {
        rawDataService.download(rawDataService.queryAll(criteria), response);
    }

    @GetMapping
    @ApiOperation("查询rawdata")
    @PreAuthorize("@el.check('rawData:list')")
    public ResponseEntity<PageResult<Map<String, Object>>> queryRawData(RawDataQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(rawDataService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增rawdata")
    @ApiOperation("新增rawdata")
    @PreAuthorize("@el.check('rawData:add')")
    public ResponseEntity<Object> createRawData(@Validated @RequestBody RawData resources){
        rawDataService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改rawdata")
    @ApiOperation("修改rawdata")
    @PreAuthorize("@el.check('rawData:edit')")
    public ResponseEntity<Object> updateRawData(@Validated @RequestBody RawData resources){
        rawDataService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除rawdata")
    @ApiOperation("删除rawdata")
    @PreAuthorize("@el.check('rawData:del')")
    public ResponseEntity<Object> deleteRawData(@ApiParam(value = "传ID数组[]") @RequestBody Long[] ids) {
        rawDataService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
