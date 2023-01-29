package net.javaguides.employeeservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.javaguides.employeeservice.dto.ApiResponseDto;
import net.javaguides.employeeservice.dto.EmployeeDto;
import net.javaguides.employeeservice.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/employees")
@AllArgsConstructor
public class EmployeeController {

    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeDto> saveEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        EmployeeDto savedEmployee = employeeService.saveEmployee(employeeDto);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponseDto> getEmployee(@PathVariable("id") Long employeeId) {
        ApiResponseDto foundEmployee = employeeService.getEmployeeById(employeeId);
        return new ResponseEntity<>(foundEmployee, HttpStatus.OK);
    }

    @GetMapping("restTemplate/{id}")
    public ResponseEntity<ApiResponseDto> getEmployee_Using_RestTemplate(@PathVariable("id") Long employeeId) {
        ApiResponseDto apiResponseDto = employeeService.getEmployeeById_With_RestTemplate(employeeId);
        return new ResponseEntity<>(apiResponseDto, HttpStatus.OK);
    }

    @GetMapping("webClient/{id}")
    public ResponseEntity<ApiResponseDto> getEmployee_Using_WebClient(@PathVariable("id") Long employeeId) {
        ApiResponseDto apiResponseDto = employeeService.getEmployeeById_With_WebClient(employeeId);
        return new ResponseEntity<>(apiResponseDto, HttpStatus.OK);
    }
}
