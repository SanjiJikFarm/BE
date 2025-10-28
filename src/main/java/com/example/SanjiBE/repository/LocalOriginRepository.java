package com.example.SanjiBE.repository;

import com.example.SanjiBE.entity.LocalOrigin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalOriginRepository extends JpaRepository<LocalOrigin, Long> {
    Optional<LocalOrigin> findByItemNameContaining(String itemName);
}
