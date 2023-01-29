package net.javaguides.employeeservice.service;

import net.javaguides.employeeservice.dto.ApiResponseDto;
import net.javaguides.employeeservice.dto.EmployeeDto;

public interface EmployeeService {
    EmployeeDto saveEmployee(EmployeeDto employeeDto);
    ApiResponseDto getEmployeeById(Long employeeId);
    ApiResponseDto getEmployeeById_With_RestTemplate(Long employeeId);
    ApiResponseDto getEmployeeById_With_WebClient(Long employeeId);
}
