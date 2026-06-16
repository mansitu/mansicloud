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
package cn.mansitu.modules.maint.domain;

import io.swagger.annotations.ApiModelProperty;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.Data;
import cn.mansitu.base.BaseEntity;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
* @author zhanghouying
* @date 2019-08-24
* @description 应用部署实体
*/
@Entity
@Data
@Table(name = "mnt_app", indexes = {
    @Index(name = "idx_app_name", columnList = "name")
})
public class App extends BaseEntity implements Serializable {


    @Id
    @Column(name = "app_id", nullable = false, updatable = false)
    @ApiModelProperty(value = "ID", hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "应用名称不能为空")
    @Size(max = 100, message = "应用名称长度不能超过100字符")
    @ApiModelProperty(value = "名称")
    private String name;

    @Positive(message = "端口号必须为正数")
    @ApiModelProperty(value = "端口")
    private Integer port;

    @Pattern(regexp = "^(/[^/]+)+$", message = "上传路径格式不正确")
    @ApiModelProperty(value = "上传路径")
    private String uploadPath;

    @Pattern(regexp = "^(/[^/]+)+$", message = "部署路径格式不正确")
    @ApiModelProperty(value = "部署路径")
    private String deployPath;

    @Pattern(regexp = "^(/[^/]+)+$", message = "备份路径格式不正确")
    @ApiModelProperty(value = "备份路径")
    private String backupPath;

	@ApiModelProperty(value = "启动脚本")
	private String startScript;

	@ApiModelProperty(value = "部署脚本")
	private String deployScript;

    public void copy(App source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
