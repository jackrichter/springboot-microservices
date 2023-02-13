package net.javaguides.organizationservice.controller;

import lombok.AllArgsConstructor;
import net.javaguides.organizationservice.dto.OrganizationDto;
import net.javaguides.organizationservice.service.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/organizations")
@AllArgsConstructor
public class OrganizationController {

    private OrganizationService organizationService;

    // Save REST API
    @PostMapping
    public ResponseEntity<OrganizationDto> saveOrganization(@RequestBody OrganizationDto organizationDto) {
        OrganizationDto saveOrganizationDto = organizationService.saveOrganization(organizationDto);
        return new ResponseEntity<>(saveOrganizationDto, HttpStatus.CREATED);
    }

    // Get Organization By Code REST API
    @GetMapping("{code}")
    public  ResponseEntity<OrganizationDto> getOrganization(@PathVariable("code") String organizationCode) {
        OrganizationDto organizationDto = organizationService.getOrganizationByCode(organizationCode);
//        return new ResponseEntity<>(organizationDto, HttpStatusCode.valueOf(HttpStatus.OK.value()));
        return ResponseEntity.ok(organizationDto);
    }
}
