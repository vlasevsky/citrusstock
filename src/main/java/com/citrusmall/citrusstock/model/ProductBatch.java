package com.citrusmall.citrusstock.model;

import com.citrusmall.citrusstock.model.enums.GoodsStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product_batches")
@Data
@ToString(exclude = {"product", "supplier", "zone", "boxes"})
public class ProductBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

   // private Integer totalBoxes;
    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Enumerated(EnumType.STRING)
    private GoodsStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @OneToMany(mappedBy = "productBatch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Box> boxes;
}
