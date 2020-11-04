package org.folio.modusers.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.folio.modusers.departments.api.DepartmentsApi;
import org.folio.modusers.dto.DepartmentCollectionDto;
import org.folio.modusers.dto.DepartmentDto;
import org.folio.modusers.service.DepartmentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class DepartmentsController implements DepartmentsApi {
    private final DepartmentsService departmentsService;

    @Override
    public ResponseEntity<DepartmentDto> createDepartment(@Valid DepartmentDto department) {
        return null;
    }

    @Override
    public ResponseEntity<DepartmentCollectionDto> getDepartments(@Valid String query, @Min(0) @Max(2147483647) @Valid Integer offset, @Min(0) @Max(2147483647) @Valid Integer limit) {
log.warn("public ResponseEntity<DepartmentCollectionDto> getDepartments(@Valid String query, @Min(0) @Max(2147483647) @Valid Integer offset, @Min(0) @Max(2147483647) @Valid Integer limit)");
        return ResponseEntity.ok(departmentsService.getAllDepartments());
    }

    @Override
    public ResponseEntity<DepartmentDto> updateDepartment(@Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$") String departmentId, @Valid DepartmentDto department) {
        return null;
    }
}
