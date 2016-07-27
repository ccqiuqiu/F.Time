package com.ccqiuqiu.ftime.Model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by cc on 2016/3/8.
 */
@Table(name = "holiday")
public class Holiday {

    public static int XIUXI = 0;
    public static int JIABAN = 1;

    @Column(name = "id",isId = true)
    private Integer id;
    @Column(name = "year")
    private Integer year;
    @Column(name = "month")
    private Integer month;
    @Column(name = "date")
    private Integer date;
    @Column(name = "status")
    private Integer status; // 0-休息  1-加班

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
