package com.example.SanjiBE.repository;

import com.example.SanjiBE.entity.LocalOrigin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LocalOriginRepository extends JpaRepository<LocalOrigin, Long> {
    Optional<LocalOrigin> findFirstByItemNameContaining(String itemName);
}
