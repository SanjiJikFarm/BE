package com.example.SanjiBE.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "local_origin")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LocalOrigin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "region")
    private String region;

    public LocalOrigin(String itemName, String region) {
        this.itemName = itemName;
        this.region = region;
    }
}
