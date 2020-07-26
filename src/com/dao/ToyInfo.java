package com.dao;

public class ToyInfo {
    private String toyCode;
    private String toyName;
    private String description;
    private String price;
    private String manufacturingDate;
    private String batchNo;
    private String companyName;
    private String streetAddress;
    private String zipCode;
    private String comment;

    public String getToyCode() {
        return toyCode;
    }
    public void setToyCode(String toyCode) {
        this.toyCode = toyCode;
    }
    public String getToyName() {
        return toyName;
    }
    public void setToyName(String toyName) {
        this.toyName = toyName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getManufacturingDate() {
        return manufacturingDate;
    }
    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }
    public String getBatchNo() {
        return batchNo;
    }
    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getStreetAddress() {
        return streetAddress;
    }
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }
    public String getZipCode() {
        return zipCode;
    }
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    @Override
    public String toString() {
        return "ToyInfo [toyCode=" + toyCode + ", toyName=" + toyName + ", description=" + description + ", price="
                + price + ", manufacturingDate=" + manufacturingDate + ", batchNo=" + batchNo + ", companyName="
                + companyName + ", streetAddress=" + streetAddress + ", zipCode=" + zipCode + ", comment=" + comment
                + "]";
    }
}
