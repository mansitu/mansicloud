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
package cn.mansitu.repository;

import cn.mansitu.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
* @website https://mansitu.cn
* @author dan
* @date 2026-06-08
**/
public interface DeviceRepository extends JpaRepository<Device, Long>, JpaSpecificationExecutor<Device> {
    /**
    * 根据 DeviceCode 查询
    * @param device_code /
    * @return /
    */
    Device findByDeviceCode(String device_code);

    @Query("SELECT d FROM Device d WHERE d.online = 1 AND d.updateTime < ?1")
    List<Device> findOfflineDevices(Timestamp threshold);

    /**
    * 批量更新超过指定时间未更新的在线设备为离线状态
    * @param threshold 时间阈值
    * @return 更新的设备数量
    */
    @Modifying
    @Query("UPDATE Device d SET d.online = 0, d.updateTime = CURRENT_TIMESTAMP WHERE d.online = 1 AND d.updateTime < ?1")
    int batchSetOfflineByUpdateTime(Timestamp threshold);

    /**
    * 批量删除设备
    * @param ids /
    */
    @Modifying
    @Query("DELETE FROM Device d WHERE d.id IN :ids")
    void deleteAllByIds(List<Long> ids);
}
