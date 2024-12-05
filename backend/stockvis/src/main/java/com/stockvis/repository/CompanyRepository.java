package com.stockvis.repository;

import com.stockvis.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    List<Company> findByName(String name);

    @Query("""
                SELECT DISTINCT c.sector
                FROM Company c
                WHERE c.sector IS NOT NULL
                AND c.sector <> ''
                AND c.sector NOT IN (
                    SELECT co.name
                    FROM Company co
                )
                ORDER BY c.sector
            """)
    List<String> findDistinctSectors();
}
