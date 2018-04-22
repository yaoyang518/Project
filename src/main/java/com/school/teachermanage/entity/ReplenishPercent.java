package com.school.teachermanage.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 补货配置
 *
 * @author zhangsl
 * @date 2017/11/27
 */
@Entity
public class ReplenishPercent {

    @Id
    @GeneratedValue
    private Long id;//记录Id

    private Date createDate = new Date();//创建时间

    private int percent;//百分占比，如：50%，则数据库存如50

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adminId")
    private Admin admin;//创建人

    private Boolean available = Boolean.FALSE; // 状态：是否可用

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
    @Transient
    public String getRule() {
        return "当前补货率为："+this.percent+"%";
    }

}
