package org.folio.modusers.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.folio.modusers.dto.DepartmentCollectionDto;
import org.folio.modusers.dto.DepartmentDto;
import org.folio.modusers.dto.MetadataDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentsService {

    public DepartmentCollectionDto getAllDepartments() {
        DepartmentCollectionDto departmentCollectionDto = new DepartmentCollectionDto();

        DepartmentDto departmentDto = new DepartmentDto();

        departmentDto.setId("5b49afe7-f141-4e1d-9183-a91dc2cb7766");
        departmentDto.setCode("Main");
        departmentDto.setName("Main department");
        departmentDto.setUsageNumber(0);

        MetadataDto metadataDto = new MetadataDto();
        metadataDto.setCreatedDate(Date.from(Instant.now()));
        departmentDto.setMetadata(metadataDto);

        departmentCollectionDto.addDepartmentsItem(departmentDto);
        departmentCollectionDto.setTotalRecords(1);
log.warn("departmentCollectionDto: " + departmentCollectionDto);
        return departmentCollectionDto;

    }

}
