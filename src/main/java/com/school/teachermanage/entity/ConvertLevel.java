package com.school.teachermanage.entity;

import com.school.teachermanage.util.NumberUtil;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值金额获得积分规则
 *
 * @author zhangsl
 * @date 2017-11-03
 */
@Entity
public class ConvertLevel {
    @Id
    @GeneratedValue
    private Long id;//记录Id

    private Date createDate = new Date();//创建时间

    private int firstLevel;//一级

    private byte firstMultiple;//一级倍数
    private byte secondMultiple;//二级级倍数

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

    public int getFirstLevel() {
        return firstLevel;
    }

    public void setFirstLevel(int firstLevel) {
        this.firstLevel = firstLevel;
    }

    public byte getFirstMultiple() {
        return firstMultiple;
    }

    public void setFirstMultiple(byte firstMultiple) {
        this.firstMultiple = firstMultiple;
    }

    public byte getSecondMultiple() {
        return secondMultiple;
    }

    public void setSecondMultiple(byte secondMultiple) {
        this.secondMultiple = secondMultiple;
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
    public BigDecimal getMultiple(BigDecimal amount) {
        BigDecimal unit = new BigDecimal(10000);
        //小于等于第一档
        if (NumberUtil.isLess(amount, new BigDecimal(firstLevel).multiply(unit))) {
            return new BigDecimal(firstMultiple);
        } else {
            return new BigDecimal(secondMultiple);
        }
    }

    @Transient
    public String getRule() {
        return "充值" + firstLevel + "万元以下，赠送" + firstMultiple + "倍积分；"
                + "充值" + firstLevel + "万元以上，赠送" + secondMultiple + "倍积分。";
    }
}
