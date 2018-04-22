package com.school.teachermanage.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现配置
 *
 * @author zhangsl
 * @date 2017/11/20
 */
@Entity
public class PayoutConfig {

    @Id
    @GeneratedValue
    private Long id;

    private boolean available;

    @Column(columnDefinition = "decimal(19,2)")
    private BigDecimal amount;

    private Date createDate;

    @ApiModelProperty(hidden = true)
    private Long adminId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    @Transient
    public String getRule() {
        return "当前提现最低金额为："+this.amount+"元";
    }
}
