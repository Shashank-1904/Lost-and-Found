package com.example.microprojectmad;

public class Item {
    private String title;
    private String date;

    private String uname;

    private String iimage;
    private String reportId;


    public Item(String reportId, String title, String date, String uname, String iimage) {
        this.reportId = reportId;
        this.title = title;
        this.date = date;
        this.uname = uname;
        this.iimage = iimage;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getUsername() {
        return uname;
    }

    public String getImage() {
        return iimage;
    }

    public String getReportId() { return reportId;}
}
