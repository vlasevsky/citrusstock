package com.citrusmall.citrusstock.model;

import com.citrusmall.citrusstock.model.enums.GoodsStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "boxes")
@Getter
@Setter
@ToString(exclude = {"productBatch", "scannedBy", "scanEvents"})
public class Box {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Код (QR или штрих-код)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_batch_id")
    private ProductBatch productBatch;

    @Enumerated(EnumType.STRING)
    private GoodsStatus status;

    @Column(name = "scanned_at")
    private LocalDateTime scannedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scanned_by")
    private User scannedBy;

    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScanEvent> scanEvents;
}