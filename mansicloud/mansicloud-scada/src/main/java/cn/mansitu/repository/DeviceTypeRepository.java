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

import cn.mansitu.domain.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
* @website https://mansitu.cn
* @author dan
* @date 2026-06-08
**/
public interface DeviceTypeRepository extends JpaRepository<DeviceType, Long>, JpaSpecificationExecutor<DeviceType> {

    @Modifying
    @Query("UPDATE DeviceType d SET d.isDeleted = :isDeleted WHERE d.id IN :ids")
    void updateIsDeletedByIds(List<Long> ids, Integer isDeleted);

    Optional<DeviceType> findByTypeCode(String typeCode);
}
