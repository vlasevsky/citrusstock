package com.citrusmall.citrusstock.mapper;

import com.citrusmall.citrusstock.dto.ZoneCreateRequest;
import com.citrusmall.citrusstock.dto.ZoneResponse;
import com.citrusmall.citrusstock.model.Zone;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ZoneMapper {
    Zone toZone(ZoneCreateRequest request);
    ZoneResponse toZoneResponse(Zone zone);
}