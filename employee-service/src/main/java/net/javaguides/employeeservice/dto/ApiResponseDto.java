package net.javaguides.employeeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Thi Dto is used for the RestTemplate integration method
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto {
    private EmployeeDto employee;
    private DepartmentDto department;
    private OrganizationDto organization;
}
