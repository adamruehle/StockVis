package com.stockvis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.sql.Date;

@Entity
public class Company {
    @Id
    private Long companyID;

    private String name;
    private String sector;
    private String industry;
    private String headQuarters;
    private Date foundedYear;

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

    public String getHeadQuarters() {
        return headQuarters;
    }

    public void setHeadQuarters(String headQuarters) {
        this.headQuarters = headQuarters;
    }

    public Date getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(Date foundedYear) {
        this.foundedYear = foundedYear;
    }

    public void setCompanyID(Long companyID) {
        this.companyID = companyID;
    }

    public Long getCompanyID() {
        return companyID;
    }
}
