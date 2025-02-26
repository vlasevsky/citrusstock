package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.model.Zone;
import com.citrusmall.citrusstock.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ZoneService {

    @Autowired
    private ZoneRepository zoneRepository;

    public Zone createZone(Zone zone) {
        return zoneRepository.save(zone);
    }

    public Zone getZoneById(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Zone not found with id " + id));
    }

    public Page<Zone> getAllZones(Pageable pageable) {
        return zoneRepository.findAll(pageable);
    }

    public Zone updateZone(Long id, Zone zoneDetails) {
        Zone zone = getZoneById(id);
        zone.setName(zoneDetails.getName());
        zone.setColor(zoneDetails.getColor());
        return zoneRepository.save(zone);
    }

    public void deleteZone(Long id) {
        zoneRepository.deleteById(id);
    }
}
