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
import cn.mansitu.domain.DeviceType;
import cn.mansitu.service.DeviceTypeService;
import cn.mansitu.service.dto.DeviceTypeQueryCriteria;
import cn.mansitu.utils.PageResult;
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
import cn.mansitu.service.dto.DeviceTypeDto;

/**
* @website https://mansitu.cn
* @author dan
* @date 2026-06-08
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "设备类型")
@RequestMapping("/api/deviceType")
public class DeviceTypeController {

    private final DeviceTypeService deviceTypeService;

    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('deviceType:list')")
    public void exportDeviceType(HttpServletResponse response, DeviceTypeQueryCriteria criteria) throws IOException {
        deviceTypeService.download(deviceTypeService.queryAll(criteria), response);
    }

    @GetMapping
    @ApiOperation("查询设备类型")
    @PreAuthorize("@el.check('deviceType:list')")
    public ResponseEntity<PageResult<DeviceTypeDto>> queryDeviceType(DeviceTypeQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(deviceTypeService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增设备类型")
    @ApiOperation("新增设备类型")
    @PreAuthorize("@el.check('deviceType:add')")
    public ResponseEntity<Object> createDeviceType(@Validated @RequestBody DeviceType resources){
        deviceTypeService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改设备类型")
    @ApiOperation("修改设备类型")
    @PreAuthorize("@el.check('deviceType:edit')")
    public ResponseEntity<Object> updateDeviceType(@Validated @RequestBody DeviceType resources){
        deviceTypeService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除设备类型")
    @ApiOperation("删除设备类型")
    @PreAuthorize("@el.check('deviceType:del')")
    public ResponseEntity<Object> deleteDeviceType(@ApiParam(value = "传ID数组[]") @RequestBody Long[] ids) {
        deviceTypeService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
