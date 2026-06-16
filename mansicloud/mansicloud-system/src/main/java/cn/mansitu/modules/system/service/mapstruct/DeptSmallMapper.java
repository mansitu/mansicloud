package cn.mansitu.modules.system.service.mapstruct;

import cn.mansitu.base.BaseMapper;
import cn.mansitu.modules.system.domain.Dept;
import cn.mansitu.modules.system.service.dto.DeptSmallDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
* @author Zheng Jie
* @date 2019-03-25
*/
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeptSmallMapper extends BaseMapper<DeptSmallDto, Dept> {

    /**
     * Entity转DTO - 显式映射companyCode字段
     */
//    @Mapping(source = "companyCode", target = "companyCode")
//    @Override
//    DeptSmallDto toDto(Dept entity);
}
