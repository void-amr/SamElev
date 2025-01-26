package com.SE.SamElev;

public class inquiryData {
    private String name;
    private String phoneNo;
    private String email;
    private String inquiry;

    public inquiryData(String name, String phoneNo, String email, String inquiry) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
        this.inquiry = inquiry;
    }

    // Getters and setters (or you can make them public)
    public String getName() {
        return name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public String getInquiry() {
        return inquiry;
    }
}
