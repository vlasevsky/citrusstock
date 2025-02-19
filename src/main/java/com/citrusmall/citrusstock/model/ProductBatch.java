package com.citrusmall.citrusstock.model;
import com.citrusmall.citrusstock.model.enums.ProductBatchStatus;
import com.citrusmall.citrusstock.model.enums.Zone;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product_batches")
@Data
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
    private LocalDateTime receivedAt;

    @Enumerated(EnumType.STRING)
    private ProductBatchStatus status = ProductBatchStatus.REGISTERED;

    // New field: Zone of the batch. Default is RECEIVING.
    @Enumerated(EnumType.STRING)
    private Zone zone = Zone.RECEIVING;

    @OneToMany(mappedBy = "productBatch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Box> boxes;
}
