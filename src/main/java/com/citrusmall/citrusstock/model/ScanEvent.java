package com.citrusmall.citrusstock.model;

import com.citrusmall.citrusstock.model.enums.ScanMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "scan_events")
@Getter
@Setter
@ToString(exclude = {"box", "user"})
public class ScanEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ссылка на коробку
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_id", nullable = false)
    private Box box;

    // Ссылка на оператора
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ScanMode scanMode;

    private LocalDateTime scanTime;
}