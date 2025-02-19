package com.citrusmall.citrusstock.model;

import com.citrusmall.citrusstock.model.enums.AuditStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
//
//@Entity
//@Table(name = "inventory_audits")
//todo Это класса для аудита, задача для беклога
@Data
public class InventoryAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The date and time when the audit was started
    private LocalDateTime auditDate;

    // The status of the audit (e.g., OPEN, CLOSED)
    @Enumerated(EnumType.STRING)
    private AuditStatus status;

    // Optionally, a list of associated scan events
    @OneToMany(mappedBy = "inventoryAudit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ScanEvent> scanEvents;
}