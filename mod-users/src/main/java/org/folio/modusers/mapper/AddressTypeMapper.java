package org.folio.modusers.mapper;

import java.util.List;
import org.folio.modusers.domain.entity.AddressType;
import org.folio.modusers.dto.AddresstypeCollectionDto;
import org.folio.modusers.dto.AddresstypeDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AddressTypeMapper {

  @Mappings({
      @Mapping(target = "id", expression = "java(addressType.getId() == null ? null : String.valueOf(addressType.getId()))"),
      @Mapping(target = "desc", source = "description"),
      @Mapping(target = "metadata.createdDate", source = "createdDate"),
      @Mapping(target = "metadata.updatedDate", source = "updatedDate"),
      @Mapping(target = "metadata.createdByUserId",
          expression = "java(addressType.getCreatedByUserId() == null ? null : String.valueOf(addressType.getCreatedByUserId()))"),
      @Mapping(target = "metadata.updatedByUserId",
          expression = "java(addressType.getUpdatedByUserId() == null ? null : String.valueOf(addressType.getUpdatedByUserId()))")
  })
  AddresstypeDto mapEntityToDto(AddressType addressType);

  @Mappings({
      @Mapping(target = "id", expression = "java(addresstypeDto.getId() == null ? null : java.util.UUID.fromString(addresstypeDto.getId()))"),
      @Mapping(target = "description", source = "desc"),
      @Mapping(target = "createdDate",
          expression = "java(addresstypeDto.getMetadata() == null ? null : addresstypeDto.getMetadata().getCreatedDate())"),
      @Mapping(target = "updatedDate",
          expression = "java(addresstypeDto.getMetadata() == null ? null : addresstypeDto.getMetadata().getUpdatedDate())"),
      @Mapping(target = "createdByUserId", expression =
          "java(addresstypeDto.getMetadata() == null ? null : java.util.UUID.fromString(addresstypeDto.getMetadata().getCreatedByUserId()))"),
      @Mapping(target = "updatedByUserId", expression =
          "java(addresstypeDto.getMetadata() == null ? null : java.util.UUID.fromString(addresstypeDto.getMetadata().getUpdatedByUserId()))")
  })
  AddressType mapDtoToEntity(AddresstypeDto addresstypeDto);


  @Mappings({})
  List<AddresstypeDto> mapEntitiesToDtos(List<AddressType> addressTypes);

  @InheritInverseConfiguration
  List<AddressType> mapDtosToEntities(List<AddresstypeDto> addresses);

  default AddresstypeCollectionDto mapToUserDataCollectionDto(List<AddressType> addressTypes) {
    AddresstypeCollectionDto userdataCollectionDto = new AddresstypeCollectionDto();
    List<AddresstypeDto> userDtoList = mapEntitiesToDtos(addressTypes);
    userdataCollectionDto.setAddressTypes(userDtoList);
    userdataCollectionDto.setTotalRecords(userDtoList.size());
    return userdataCollectionDto;
  }

  default List<AddressType> mapAddressTypeCollectionDtoToEntity(
      AddresstypeCollectionDto addresstypeCollectionDto) {
    return mapDtosToEntities(addresstypeCollectionDto.getAddressTypes());
  }

  @Mappings({
      @Mapping(target = "id", expression = "java(addressType.getId() == null ? null : java.util.UUID.fromString(addressTypeDto.getId()))"),
      @Mapping(target = "description", source = "desc")
  })
  void mapEntityToDto(AddresstypeDto addressTypeDto, @MappingTarget AddressType addressType);
}
