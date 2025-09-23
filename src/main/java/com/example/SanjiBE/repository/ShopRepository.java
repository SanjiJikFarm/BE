package com.example.SanjiBE.repository;

import com.example.SanjiBE.entity.Product;
import com.example.SanjiBE.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}

