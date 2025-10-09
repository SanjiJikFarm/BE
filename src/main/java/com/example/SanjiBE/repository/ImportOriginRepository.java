package com.example.SanjiBE.repository;

import com.example.SanjiBE.entity.ImportOrigin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ImportOriginRepository extends JpaRepository<ImportOrigin, Long> {
    Optional<ImportOrigin> findFirstByItemNameContaining(String itemName);
}
