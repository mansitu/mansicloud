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
import cn.mansitu.service.DeviceService;
import cn.mansitu.domain.Device;
import cn.mansitu.utils.PageResult;
import cn.mansitu.service.dto.DeviceQueryCriteria;
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

import cn.mansitu.service.dto.DeviceDto;

/**
* @website https://mansitu.cn
* @author dan
* @date 2026-06-08
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "设备管理")
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceService deviceService;

    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('device:list')")
    public void exportDevice(HttpServletResponse response, DeviceQueryCriteria criteria) throws IOException {
        deviceService.download(deviceService.queryAll(criteria), response);
    }

    @GetMapping
    @ApiOperation("查询设备管理")
    @PreAuthorize("@el.check('device:list')")
    public ResponseEntity<PageResult<DeviceDto>> queryDevice(DeviceQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(deviceService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PutMapping
    @Log("修改设备管理")
    @ApiOperation("修改设备管理")
    @PreAuthorize("@el.check('device:edit')")
    public ResponseEntity<Object> updateDevice(@Validated @RequestBody Device resources){
        deviceService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
