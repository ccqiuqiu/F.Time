package com.ccqiuqiu.ftime.Model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by cc on 2016/3/17.
 */
@Table(name = "progress")
public class Progress {
    @Column(name = "id", isId = true)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private int type;
    @Column(name = "start")
    private String start;
    @Column(name = "end")
    private String end;
    @Column(name = "icon")
    private int icon;
    @Column(name = "tixing")
    private int tixing;
    @Column(name = "order")
    private int order;
    @Column(name = "nextDate")
    private long nextDate;

    private boolean isShowTipDesc;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getTixing() {
        return tixing;
    }

    public void setTixing(int tixing) {
        this.tixing = tixing;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public long getNextDate() {
        return nextDate;
    }

    public boolean isShowTipDesc() {
        return isShowTipDesc;
    }

    public void setIsShowTipDesc(boolean isShowTipDesc) {
        this.isShowTipDesc = isShowTipDesc;
    }

    public void setNextDate(long nextDate) {
        this.nextDate = nextDate;
    }
}
