package com.example.SanjiBE.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "import_origin")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ImportOrigin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "country")
    private String country;

    public ImportOrigin(String itemName, String country) {
        this.itemName = itemName;
        this.country = country;
    }
}
