package cn.mansitu.modules.system.service.mapstruct;

import cn.mansitu.base.BaseMapper;
import cn.mansitu.modules.system.domain.User;
import cn.mansitu.modules.system.service.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Mapper(componentModel = "spring", 
        uses = {RoleMapper.class, DeptSmallMapper.class, JobMapper.class}, 
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends BaseMapper<UserDto, User> {
    
    /**
     * Entity转DTO - 显式映射isAdmin字段
     */
    @Mapping(source = "isAdmin", target = "isAdmin")
    @Override
    UserDto toDto(User entity);
    
    /**
     * DTO转Entity - 显式映射isAdmin字段
     */
    @Mapping(source = "isAdmin", target = "isAdmin")
    @Override
    User toEntity(UserDto dto);
}
