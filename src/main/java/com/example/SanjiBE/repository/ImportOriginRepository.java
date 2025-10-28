package com.example.SanjiBE.repository;

import com.example.SanjiBE.entity.ImportOrigin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImportOriginRepository extends JpaRepository<ImportOrigin, Long> {
    Optional<ImportOrigin> findByItemNameContaining(String itemName);
}
