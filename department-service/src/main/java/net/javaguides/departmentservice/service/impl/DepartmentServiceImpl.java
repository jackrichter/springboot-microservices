package net.javaguides.departmentservice.service.impl;

import lombok.AllArgsConstructor;
import net.javaguides.departmentservice.dto.DepartmentDto;
import net.javaguides.departmentservice.entity.Department;
import net.javaguides.departmentservice.exception.DepartmentAlreadyExistsException;
import net.javaguides.departmentservice.exception.ResourceNotFoundException;
import net.javaguides.departmentservice.mapper.AutoDepartmentMapper;
import net.javaguides.departmentservice.repository.DepartmentRepository;
import net.javaguides.departmentservice.service.DepartmentService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private DepartmentRepository departmentRepository;

    @Override
    public DepartmentDto saveDepartment(DepartmentDto departmentDto) {

        Department byDepartmentCode = departmentRepository.findByDepartmentCode(departmentDto.getDepartmentCode());
        if (byDepartmentCode != null) {
            throw new DepartmentAlreadyExistsException("Department with same code already exists!");
        }

        // Convert department dto to department jpa entity using StructMapper
        Department department = AutoDepartmentMapper.MAPPER.toDepartment(departmentDto);

//        Department department = new Department(
//                departmentDto.getId(),
//                departmentDto.getDepartmentName(),
//                departmentDto.getDepartmentDescription(),
//                departmentDto.getDepartmentCode()
//        );

        Department savedDepartment = departmentRepository.save(department);
        DepartmentDto savedDepartmentDto = AutoDepartmentMapper.MAPPER.toDepartmentDto(savedDepartment);

//        DepartmentDto savedDepartmentDto = new DepartmentDto(
//                savedDepartment.getId(),
//                savedDepartment.getDepartmentName(),
//                savedDepartment.getDepartmentDescription(),
//                savedDepartment.getDepartmentCode()
//        );

        return savedDepartmentDto;
    }

    @Override
    public DepartmentDto getDepartmentByCode(String departmentCode) {
        Department department = departmentRepository.findByDepartmentCode(departmentCode);
        if (department == null) {
            //throw new RuntimeException("Something went wrong");
            throw new ResourceNotFoundException("Department", "Department-code", departmentCode);
        }

        DepartmentDto departmentDto = AutoDepartmentMapper.MAPPER.toDepartmentDto(department);

//        DepartmentDto departmentDto = new DepartmentDto(
//                department.getId(),
//                department.getDepartmentName(),
//                department.getDepartmentDescription(),
//                department.getDepartmentCode()
//        );

        return departmentDto;
    }
}
