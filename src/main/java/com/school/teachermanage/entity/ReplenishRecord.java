package com.school.teachermanage.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 补货记录
 *
 * @author zhangsl
 * @date 2017-11-28
 */
@Entity
public class ReplenishRecord {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    private Long adminId;
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
