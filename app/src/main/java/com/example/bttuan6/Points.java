package com.example.bttuan6;

public class Points {
    private String sdt;
    private String point;
    private String note;
    private String cur_date;

    public Points(String sdt, String point, String note, String cur_date) {
        this.sdt = sdt;
        this.point = point;
        this.note = note;
        this.cur_date = cur_date;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCur_date() {
        return cur_date;
    }

    public void setCur_date(String cur_date) {
        this.cur_date = cur_date;
    }
}
