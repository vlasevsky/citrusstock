package com.citrusmall.citrusstock.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "zones")
@Data
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
}