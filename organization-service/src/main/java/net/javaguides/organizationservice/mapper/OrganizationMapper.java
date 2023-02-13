package net.javaguides.organizationservice.mapper;

import net.javaguides.organizationservice.dto.OrganizationDto;
import net.javaguides.organizationservice.entity.Organization;

public class OrganizationMapper {

    public static OrganizationDto toDto(Organization organization) {
        return new OrganizationDto(
                organization.getId(),
                organization.getOrganizationName(),
                organization.getOrganizationDescription(),
                organization.getOrganizationCode(),
                organization.getCreatedDate()
        );
    }

    public static Organization toEntity(OrganizationDto dto) {
        return new Organization(
                dto.getId(),
                dto.getOrganizationName(),
                dto.getOrganizationDescription(),
                dto.getOrganizationCode(),
                dto.getCreatedDate()
        );
    }
}
