package com.ccqiuqiu.ftime.Model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by cc on 2016/3/8.
 */
@Table(name = "alarm")
public class Alarm {
    @Column(name = "id",isId = true)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "time")
    private String time;//时间
    @Column(name = "date")
    private String date;//日期
    @Column(name = "isOpen")
    private boolean isOpen;//是否打开
    @Column(name = "type")
    private int type;//闹钟类型
    @Column(name = "weeks")
    private String weeks;//星期
    @Column(name = "day")
    private int day;//每月几日
    @Column(name = "workDays")
    private int workDays;//响铃几天
    @Column(name = "unWorkDays")
    private int unWorkDays;//暂停几天
    @Column(name = "musicUri")
    private String musicUri;//铃声的路径
    @Column(name = "volume")
    private int volume;//音量
    @Column(name = "isVibration")
    private boolean isVibration;//是否震动
    @Column(name = "isJianqiang")
    private boolean isJianqiang;//是否铃声渐强
    @Column(name = "nextDate")
    private long nextDate;//下次响铃时间
    @Column(name = "order")
    private int order;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWorkDays() {
        return workDays;
    }

    public void setWorkDays(int workDays) {
        this.workDays = workDays;
    }

    public int getUnWorkDays() {
        return unWorkDays;
    }

    public void setUnWorkDays(int unWorkDays) {
        this.unWorkDays = unWorkDays;
    }

    public String getMusicUri() {
        return musicUri;
    }

    public void setMusicUri(String musicUri) {
        this.musicUri = musicUri;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public boolean isVibration() {
        return isVibration;
    }

    public void setIsVibration(boolean isVibration) {
        this.isVibration = isVibration;
    }

    public boolean isJianqiang() {
        return isJianqiang;
    }

    public void setIsJianqiang(boolean isJianqiang) {
        this.isJianqiang = isJianqiang;
    }

    public long getNextDate() {
        return nextDate;
    }

    public void setNextDate(long nextDate) {
        this.nextDate = nextDate;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
