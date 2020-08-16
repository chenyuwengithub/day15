package com.xiaoshu.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

@Table(name = "tb_stu_day")
public class Stu implements Serializable {
    @Id
    @Column(name = "sd_id")
    private Integer sdId;

    @Column(name = "sd_name")
    private String sdName;

    private String sdsex;

    @Column(name = "sd_hobby")
    private String sdHobby;

	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date sdbirth;

    private Integer mid;

    private static final long serialVersionUID = 1L;

    /**
     * @return sd_id
     */
    public Integer getSdId() {
        return sdId;
    }

    /**
     * @param sdId
     */
    public void setSdId(Integer sdId) {
        this.sdId = sdId;
    }

    /**
     * @return sd_name
     */
    public String getSdName() {
        return sdName;
    }

    /**
     * @param sdName
     */
    public void setSdName(String sdName) {
        this.sdName = sdName == null ? null : sdName.trim();
    }

    /**
     * @return sdsex
     */
    public String getSdsex() {
        return sdsex;
    }

    /**
     * @param sdsex
     */
    public void setSdsex(String sdsex) {
        this.sdsex = sdsex == null ? null : sdsex.trim();
    }

    /**
     * @return sd_hobby
     */
    public String getSdHobby() {
        return sdHobby;
    }

    /**
     * @param sdHobby
     */
    public void setSdHobby(String sdHobby) {
        this.sdHobby = sdHobby == null ? null : sdHobby.trim();
    }

    /**
     * @return sdbirth
     */
    public Date getSdbirth() {
        return sdbirth;
    }

    /**
     * @param sdbirth
     */
    public void setSdbirth(Date sdbirth) {
        this.sdbirth = sdbirth;
    }

    /**
     * @return mid
     */
    public Integer getMid() {
        return mid;
    }

    /**
     * @param mid
     */
    public void setMid(Integer mid) {
        this.mid = mid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", sdId=").append(sdId);
        sb.append(", sdName=").append(sdName);
        sb.append(", sdsex=").append(sdsex);
        sb.append(", sdHobby=").append(sdHobby);
        sb.append(", sdbirth=").append(sdbirth);
        sb.append(", mid=").append(mid);
        sb.append("]");
        return sb.toString();
    }
}