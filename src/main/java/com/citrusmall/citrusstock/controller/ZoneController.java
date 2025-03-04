package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.dto.ZoneCreateRequest;
import com.citrusmall.citrusstock.dto.ZoneResponse;
import com.citrusmall.citrusstock.dto.ZoneStats;
import com.citrusmall.citrusstock.mapper.ZoneMapper;
import com.citrusmall.citrusstock.model.Zone;
import com.citrusmall.citrusstock.repository.ZoneRepository;
import com.citrusmall.citrusstock.service.ZoneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/zones")
public class ZoneController {

    @Autowired
    private ZoneService zoneService;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private ZoneMapper zoneMapper;

    @PostMapping
    public ResponseEntity<ZoneResponse> createZone(@Valid @RequestBody ZoneCreateRequest request) {
        Zone zone = zoneMapper.toZone(request);
        Zone savedZone = zoneService.createZone(zone);
        return ResponseEntity.ok(zoneMapper.toZoneResponse(savedZone));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneResponse> getZone(@PathVariable Long id) {
        Zone zone = zoneService.getZoneById(id);
        return ResponseEntity.ok(zoneMapper.toZoneResponse(zone));
    }

    @GetMapping
    public ResponseEntity<Page<ZoneResponse>> getAllZones(Pageable pageable) {
        Page<Zone> zones = zoneService.getAllZones(pageable);
        Page<ZoneResponse> response = zones.map(zoneMapper::toZoneResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ZoneStats>> getZoneStats() {
        List<ZoneStats> stats = zoneRepository.findZoneStats();
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneResponse> updateZone(@PathVariable Long id, @Valid @RequestBody ZoneCreateRequest request) {
        Zone zone = zoneMapper.toZone(request);
        Zone updatedZone = zoneService.updateZone(id, zone);
        return ResponseEntity.ok(zoneMapper.toZoneResponse(updatedZone));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }
}
