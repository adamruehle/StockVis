package com.stockvis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class EconomicData {

    @Id
    private Long companyId;


    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCompanyId() {
        return companyId;
    }
}
