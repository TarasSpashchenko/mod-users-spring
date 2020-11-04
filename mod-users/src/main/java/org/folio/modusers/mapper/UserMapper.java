package org.folio.modusers.mapper;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.folio.modusers.domain.entity.Address;
import org.folio.modusers.domain.entity.ProxyFor;
import org.folio.modusers.domain.entity.User;
import org.folio.modusers.dto.UserDto;
import org.folio.modusers.dto.UserdataCollectionDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.springframework.data.domain.Page;
import org.folio.modusers.utils.MappingUtils;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, ProxyForMapper.class},
        imports = {MappingUtils.class}, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

  @Mappings({
      @Mapping(target = "id", expression = "java(user.getId() == null ? null : String.valueOf(user.getId()))"),
      @Mapping(target = "externalSystemId", expression =
          "java(user.getExternalSystemId() == null ? null : String.valueOf(user.getExternalSystemId()))"),
      @Mapping(target = "patronGroup", expression =
          "java(user.getUserGroup() == null || user.getUserGroup().getId() == null  ? null : String.valueOf(user.getUserGroup().getId()))"),
      @Mapping(target = "personal.firstName", source = "firstName"),
      @Mapping(target = "personal.lastName", source = "lastName"),
      @Mapping(target = "personal.middleName", source = "middleName"),
      @Mapping(target = "personal.email", source = "email"),
      @Mapping(target = "personal.phone", source = "phone"),
      @Mapping(target = "personal.mobilePhone", source = "mobilePhone"),
      @Mapping(target = "personal.dateOfBirth", source = "dateOfBirth"),
      @Mapping(target = "personal.addresses", source = "addresses"),
      @Mapping(target = "personal.preferredContactTypeId", constant = "002"),
      @Mapping(target = "metadata.createdDate", source = "createdDate"),
      @Mapping(target = "metadata.updatedDate", source = "updatedDate"),
      @Mapping(target = "metadata.createdByUserId", expression =
          "java(user.getCreatedByUserId() == null ? null : String.valueOf(user.getCreatedByUserId()))"),
      @Mapping(target = "metadata.updatedByUserId", expression =
          "java(user.getUpdatedByUserId() == null ? null : String.valueOf(user.getUpdatedByUserId()))")

  })
  UserDto mapEntityToDto(User user);

  @Mappings({
      @Mapping(target = "id", expression =
          "java(userDto.getId() == null ? null : stringToUUIDSafe(userDto.getId()))"),
      @Mapping(target = "externalSystemId", expression =
          "java(MappingUtils.parseUUID(userDto.getExternalSystemId()))"),
      @Mapping(target = "userGroup.id", expression =
              "java(MappingUtils.parseUUID(userDto.getPatronGroup()))"),
      @Mapping(target = "lastName", expression = "java(userDto.getPersonal() == null ? null : userDto.getPersonal().getLastName())"),
      @Mapping(target = "createdByUserId", expression =
          "java(userDto.getMetadata() == null ? null : stringToUUIDSafe(userDto.getMetadata().getCreatedByUserId()))"),
      @Mapping(target = "updatedByUserId", expression =
          "java(userDto.getMetadata() == null ? null : stringToUUIDSafe(userDto.getMetadata().getUpdatedByUserId()))")
  })
  @InheritInverseConfiguration
  User mapDtoToEntity(UserDto userDto);

  @Mappings({})
  List<UserDto> mapEntitiesToDtos(Iterable<User> users);

  @InheritInverseConfiguration
  List<User> mapDtosToEntities(Iterable<UserDto> users);

  @Mappings({
      @Mapping(target = "id", expression = "java(MappingUtils.parseUUID(userDto.getId()))"),
      @Mapping(target = "externalSystemId", expression =
          "java(MappingUtils.parseUUID(userDto.getId()))"),
      @Mapping(target = "userGroup.id", expression =
          "java(MappingUtils.parseUUID(userDto.getPatronGroup()))")
  })
  void mapEntityToDto(UserDto userDto, @MappingTarget User user);

  @AfterMapping
  default void populateUserGroup(@MappingTarget User user) {
    if (user != null && user.getUserGroup().getId() == null) {
      user.setUserGroup(null);
    }
  }

  @AfterMapping
  default void populateUserToAddresses(@MappingTarget User user) {
    if (user.getAddresses() != null) {
      for (Address address : user.getAddresses()) {
        address.setUser(user);
      }
    }
  }

  @AfterMapping
  default void populateUserToProxyFor(@MappingTarget User user) {
    if (user.getProxyFor() != null) {
      for (ProxyFor proxyFor : user.getProxyFor()) {
        proxyFor.setProxyUser(user);
      }
    }
  }

  default UserdataCollectionDto mapToUserDataCollectionDto(Iterable<User> users) {
    UserdataCollectionDto userdataCollectionDto = new UserdataCollectionDto();
    List<UserDto> userDtoList = mapEntitiesToDtos(users);
    userdataCollectionDto.setUsers(userDtoList);
    userdataCollectionDto.setTotalRecords(userDtoList.size());
    return userdataCollectionDto;
  }

  default List<User> mapUserDataCollectionDtoToEntity(UserdataCollectionDto userdataCollectionDto) {
    return mapDtosToEntities(userdataCollectionDto.getUsers());
  }

  default String mapProxyForEntityToString(ProxyFor proxyFor) {
    return proxyFor.getId() == null ? null : String.valueOf(proxyFor.getUser().getId());
  }

  default ProxyFor mapProxyForStringToEntity(String proxyForId) {
    User user = new User();
    user.setId(stringToUUIDSafe(proxyForId));
    ProxyFor proxyFor = new ProxyFor();
    proxyFor.setUser(user);
    return proxyFor;
  }

  default UUID stringToUUIDSafe(String uuid) {
    return (StringUtils.isBlank(uuid)) ? null : java.util.UUID.fromString(uuid);
  }

}
