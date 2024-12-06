package com.stockvis.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "company")
public class Company {

    @Id
    private String name;
    private String sector;
    private String industry;
    private String headquarters;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Stock> stocks;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<CompanyFinancial> companyFinancials;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public void setHeadquarters(String headquarters) {
        this.headquarters = headquarters;
    }

}
