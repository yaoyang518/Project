package com.school.teachermanage.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值记录实体类
 *
 * @author zhangsl
 * @date 2017-11-01
 */
@Entity
public class RechargeRecord {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    private Long adminId;
    @Column(columnDefinition = "decimal(19,7)")
    private BigDecimal amount;
    private Date createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
