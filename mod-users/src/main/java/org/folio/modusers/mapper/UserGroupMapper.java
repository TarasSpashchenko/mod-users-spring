package org.folio.modusers.mapper;

import java.util.List;
import org.folio.modusers.domain.entity.UserGroup;
import org.folio.modusers.dto.UserGroupDto;
import org.folio.modusers.dto.UserGroupsDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, uses = UserMapper.class)
public interface UserGroupMapper {

  @Mappings({
      @Mapping(target = "id", expression = "java(userGroup.getId() == null ? null : String.valueOf(userGroup.getId()))"),
      @Mapping(target = "desc", source = "description"),
      @Mapping(target = "metadata.createdDate", source = "createdDate"),
      @Mapping(target = "metadata.updatedDate", source = "updatedDate"),
      @Mapping(target = "metadata.createdByUserId", expression =
          "java(userGroup.getCreatedByUserId() == null ? null : String.valueOf(userGroup.getCreatedByUserId()))"),
      @Mapping(target = "metadata.updatedByUserId", expression =
          "java(userGroup.getUpdatedByUserId() == null ? null : String.valueOf(userGroup.getUpdatedByUserId()))"),

  })
  UserGroupDto mapEntityToDto(UserGroup userGroup);

  @Mappings({
      @Mapping(target = "id", expression = "java(userGroupDto.getId() == null ? null : java.util.UUID.fromString(userGroupDto.getId()))"),
      @Mapping(target = "description", source = "desc"),
      @Mapping(target = "createdDate",
          expression = "java(userGroupDto.getMetadata() == null ? null : userGroupDto.getMetadata().getCreatedDate())"),
      @Mapping(target = "updatedDate",
          expression = "java(userGroupDto.getMetadata() == null ? null : userGroupDto.getMetadata().getUpdatedDate())"),
      @Mapping(target = "createdByUserId", expression =
          "java(userGroupDto.getMetadata() == null ? null : java.util.UUID.fromString(userGroupDto.getMetadata().getCreatedByUserId()))"),
      @Mapping(target = "updatedByUserId", expression =
          "java(userGroupDto.getMetadata() == null ? null : java.util.UUID.fromString(userGroupDto.getMetadata().getUpdatedByUserId()))"),
  })
  UserGroup mapDtoToEntity(UserGroupDto userGroupDto);


  @Mappings({})
  List<UserGroupDto> mapEntitiesToDtos(List<UserGroup> userGroups);

  @InheritInverseConfiguration
  List<UserGroup> mapDtosToEntities(List<UserGroupDto> userGroup);

  @Mappings({
      @Mapping(target = "id", expression = "java(userGroup.getId() == null ? null : java.util.UUID.fromString(userGroupDto.getId()))"),
      @Mapping(target = "description", source = "desc")
  })
  void mapEntityToDto(UserGroupDto userGroupDto, @MappingTarget UserGroup userGroup);

  default UserGroupsDto mapToUserDataCollectionDto(List<UserGroup> userGroups) {
    UserGroupsDto userGroupsDto = new UserGroupsDto();
    List<UserGroupDto> groupDtos = mapEntitiesToDtos(userGroups);
    userGroupsDto.setUsergroups(groupDtos);
    userGroupsDto.setTotalRecords(groupDtos.size());
    return userGroupsDto;
  }

  default List<UserGroup> mapUserGroupsDtoToEntity(UserGroupsDto userGroupsDto) {
    return mapDtosToEntities(userGroupsDto.getUsergroups());
  }
}
