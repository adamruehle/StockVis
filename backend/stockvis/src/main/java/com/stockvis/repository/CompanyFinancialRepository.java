package com.stockvis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockvis.entity.CompanyFinancial;
import com.stockvis.entity.CompanyFinancialId;

public interface CompanyFinancialRepository extends JpaRepository<CompanyFinancial, CompanyFinancialId> {

    List<CompanyFinancial> findByName(String name);

}
