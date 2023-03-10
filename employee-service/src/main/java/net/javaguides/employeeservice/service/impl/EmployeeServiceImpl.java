package net.javaguides.employeeservice.service.impl;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import net.javaguides.employeeservice.dto.ApiResponseDto;
import net.javaguides.employeeservice.dto.DepartmentDto;
import net.javaguides.employeeservice.dto.EmployeeDto;
import net.javaguides.employeeservice.dto.OrganizationDto;
import net.javaguides.employeeservice.entity.Employee;
import net.javaguides.employeeservice.exception.EmailAlreadyExistsException;
import net.javaguides.employeeservice.exception.ResourceNotFoundException;
import net.javaguides.employeeservice.repository.EmployeeRepository;
import net.javaguides.employeeservice.service.ApiFeignClient;
import net.javaguides.employeeservice.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private EmployeeRepository employeeRepository;
    private ModelMapper modelMapper;

    // Integration using RestTemplate (to be replaced by WebClient)
    private RestTemplate restTemplate;

    // Integration using Reactive(WebFlux) WebClient
    private WebClient webClient;

    // Integration using Spring Cloud OpenFeign method
    private ApiFeignClient apiFeignClient;

    // We need the constructor to inject employeeRepository, but Lombok @AllArgsConstuctor handles it
//    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
//        this.employeeRepository = employeeRepository;
//    }

    @Override
    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {

        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(employeeDto.getEmail());
        if (optionalEmployee.isPresent()) {
            throw new EmailAlreadyExistsException("Employee with same email already exists!");
        }

        // Convert employee dto to employee jpa entity using ModelMapper
        Employee employee = modelMapper.map(employeeDto, Employee.class);

//        Employee employee = new Employee(
//                employeeDto.getId(),
//                employeeDto.getFirstName(),
//                employeeDto.getLastName(),
//                employeeDto.getEmail(),
//                  employeeDto.getDepartmentCode()
//        );

        Employee savedEmployee = employeeRepository.save(employee);
        EmployeeDto savedEmployeeDto = modelMapper.map(savedEmployee, EmployeeDto.class);

//        EmployeeDto savedEmployeeDto = new EmployeeDto(
//                savedEmployee.getId(),
//                savedEmployee.getFirstName(),
//                savedEmployee.getLastName(),
//                savedEmployee.getEmail(),
//                savedEmployee.getDepartmentCode();
//        );

        return savedEmployeeDto;
    }

    /**
     * Here we use the OpenFeign solution as the general way to perform getEmployeeById,
     * returning also Department info for where the employee works.
     *
     * @param employeeId The Id of the employee
     * @return The employee and department
     */
    @Override
    public ApiResponseDto getEmployeeById(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new ResourceNotFoundException("Employee", "id", employeeId)
        );
        EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);

        DepartmentDto departmentDto = apiFeignClient.getDepartment(employee.getDepartmentCode());

        // The response object (without Organization)
        ApiResponseDto apiResponseDto = new ApiResponseDto();
        apiResponseDto.setEmployee(employeeDto);
        apiResponseDto.setDepartment(departmentDto);

//        EmployeeDto employeeDto = new EmployeeDto(
//                employee.getId(),
//                employee.getFirstName(),
//                employee.getLastName(),
//                employee.getEmail(),
//                employee.getDepartmentCode()
//        );

        return apiResponseDto;
    }

    /**
     * API for usage with Rest-template integration method.
     * Attention:
     * The RestTemplate is to be deprecated from Spring 5.0.
     * WebClient is the recommended integration method!
     */
    public ApiResponseDto getEmployeeById_With_RestTemplate(Long employeeId) {

        // Get the employee
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new ResourceNotFoundException("Employee", "id", employeeId)
        );
        EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);

        // Get the Department this employee works at
        ResponseEntity<DepartmentDto> responseEntity = restTemplate.getForEntity("http://localhost:8080/api/departments/" + employee.getDepartmentCode(), DepartmentDto.class);
        DepartmentDto departmentDto = responseEntity.getBody();

        // The response object (without Organization)
        ApiResponseDto apiResponseDto = new ApiResponseDto();
        apiResponseDto.setEmployee(employeeDto);
        apiResponseDto.setDepartment(departmentDto);

        return apiResponseDto;
    }

    /**
     * API for usage with Reactive sync/async integration method.
     * We use this method to get the complete chain: Employee, Department and Organization.
     */
    //@CircuitBreaker(name = "${spring.application.name}", fallbackMethod = "getDefaultDepartment")
    @Retry(name = "&{sping.application.name}", fallbackMethod = "getDefaultDepartment")
    public ApiResponseDto getEmployeeById_With_WebClient(Long employeeId) {

        LOGGER.info("inside getEmployeeById_With_WebClient() method");

        // Get the employee
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new ResourceNotFoundException("Employee", "id", employeeId)
        );
        EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);

        // Get synchronously the Department this employee works at
        DepartmentDto departmentDto = webClient.get()
                .uri("http://localhost:8080/api/departments/" + employee.getDepartmentCode())
                .retrieve()
                .bodyToMono(DepartmentDto.class)
                .block();       // synchronous

        // Get synchronously the Organization this employee works at
        OrganizationDto organizationDto = webClient.get()
                .uri("http://localhost:8083/api/organizations/" + employee.getOrganizationCode())
                .retrieve()
                .bodyToMono(OrganizationDto.class)
                .block();

        // The response object including Organization
        ApiResponseDto apiResponseDto = new ApiResponseDto();
        apiResponseDto.setEmployee(employeeDto);
        apiResponseDto.setDepartment(departmentDto);
        apiResponseDto.setOrganization(organizationDto);

        return apiResponseDto;
    }

    /**
     * Fallback Method
     */
    public ApiResponseDto getDefaultDepartment(Long employeeId, Exception exception) {
        Employee employee = employeeRepository.findById(employeeId).get();

        LOGGER.info("inside getDefaultDepartment() method");

        // The default department
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setDepartmentName("R&D Department");
        departmentDto.setDepartmentDescription("Research and Development Department");
        departmentDto.setDepartmentCode("RD003");

        EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);

        ApiResponseDto apiResponseDto = new ApiResponseDto();
        apiResponseDto.setDepartment(departmentDto);
        apiResponseDto.setEmployee(employeeDto);

        return apiResponseDto;
    }
}
