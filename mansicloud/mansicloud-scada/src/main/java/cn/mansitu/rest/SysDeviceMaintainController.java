package cn.mansitu.rest;

import cn.mansitu.annotation.Log;
import cn.mansitu.domain.SysDeviceMaintain;
import cn.mansitu.modules.system.domain.Dept;
import cn.mansitu.modules.system.repository.DeptRepository;
import cn.mansitu.service.SysDeviceMaintainService;
import cn.mansitu.service.dto.SysDeviceMaintainDto;
import cn.mansitu.service.dto.SysDeviceMaintainQueryCriteria;
import cn.mansitu.utils.PageResult;
import cn.mansitu.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* @website https://mansitu.cn
* @author MansiCloud
* @date 2026-06-21
**/
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "DeviceMnt")
@RequestMapping("/api/sysDeviceMaintain")
public class SysDeviceMaintainController {

    private final SysDeviceMaintainService sysDeviceMaintainService;
    private final DeptRepository deptRepository;

    @ApiOperation("导出数据")
    @Log("导出设备维保数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('sysDeviceMaintain:list')")
    public void exportSysDeviceMaintain(HttpServletResponse response, SysDeviceMaintainQueryCriteria criteria) throws IOException {
        sysDeviceMaintainService.download(sysDeviceMaintainService.queryAll(criteria), response);
    }

    @GetMapping
    @ApiOperation("查询DeviceMnt")
    @PreAuthorize("@el.check('sysDeviceMaintain:list')")
    public ResponseEntity<PageResult<SysDeviceMaintainDto>> querySysDeviceMaintain(SysDeviceMaintainQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(sysDeviceMaintainService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @PostMapping
    @Log("新增DeviceMnt")
    @ApiOperation("新增DeviceMnt")
    @PreAuthorize("@el.check('sysDeviceMaintain:add')")
    public ResponseEntity<Object> createSysDeviceMaintain(@Validated @RequestBody SysDeviceMaintain resources) {
        cn.mansitu.modules.security.service.dto.JwtUserDto jwtUser =
                (cn.mansitu.modules.security.service.dto.JwtUserDto) SecurityUtils.getCurrentUser();

        if (jwtUser != null && jwtUser.getUser() != null) {
            String username = jwtUser.getUser().getUsername();
            resources.setCreateBy(username);
            resources.setUpdateBy(username);
        }

        java.sql.Timestamp currentTime = new java.sql.Timestamp(System.currentTimeMillis());
        resources.setCreateTime(currentTime);
        resources.setUpdateTime(currentTime);

        if (resources.getDelFlag() == null) {
            resources.setDelFlag(0);
        }

        if (resources.getStatus() == null) {
            resources.setStatus(1);
        }

        sysDeviceMaintainService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改DeviceMnt")
    @ApiOperation("修改DeviceMnt")
    @PreAuthorize("@el.check('sysDeviceMaintain:edit')")
    public ResponseEntity<Object> updateSysDeviceMaintain(@Validated @RequestBody SysDeviceMaintain resources) {
        SysDeviceMaintainDto existing = sysDeviceMaintainService.findById(resources.getId());
        sysDeviceMaintainService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除DeviceMnt")
    @ApiOperation("删除DeviceMnt")
    @PreAuthorize("@el.check('sysDeviceMaintain:del')")
    public ResponseEntity<Object> deleteSysDeviceMaintain(@ApiParam(value = "传ID数组[]") @RequestBody Long[] ids) {
        sysDeviceMaintainService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
