package com.citrusmall.citrusstock.mapper;

import com.citrusmall.citrusstock.dto.ZoneCreateRequest;
import com.citrusmall.citrusstock.dto.ZoneResponse;
import com.citrusmall.citrusstock.model.Zone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        imports = {com.citrusmall.citrusstock.util.EnumLocalizer.class})

public interface ZoneMapper {
    Zone toZone(ZoneCreateRequest request);

    @Mapping(target = "name", expression = "java(EnumLocalizer.localizeZone(zone.getName()))")
    ZoneResponse toZoneResponse(Zone zone);
}