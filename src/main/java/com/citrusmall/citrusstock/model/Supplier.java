package com.citrusmall.citrusstock.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "suppliers")
@Data
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}