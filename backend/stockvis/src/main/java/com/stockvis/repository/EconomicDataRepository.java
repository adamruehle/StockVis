package com.stockvis.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.stockvis.entity.EconomicData;
import com.stockvis.entity.Price;
import com.stockvis.entity.PriceId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EconomicDataRepository extends JpaRepository<EconomicData, LocalDate> {

}
