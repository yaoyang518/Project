package com.school.teachermanage.entity;

import com.school.teachermanage.enumeration.TicketSource;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 消费券记录
 *
 * @author zhangsl
 * @date 2017-11-04
 */
@Entity
public class TicketRecord {

    @Id
    @GeneratedValue
    private Long id;
    private Date createDate;
    @Enumerated(EnumType.STRING)
    private TicketSource ticketSource;
    @Column(name = "subtract")
    private boolean minus;
    @Column(columnDefinition = "decimal(19,7)")
    private BigDecimal amount;
    @Column(columnDefinition = "decimal(19,7)")
    private BigDecimal total;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adminId")
    private Admin admin;
    private String remark;

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

    public TicketSource getTicketSource() {
        return ticketSource;
    }

    public void setTicketSource(TicketSource ticketSource) {
        this.ticketSource = ticketSource;
    }

    public boolean isMinus() {
        return minus;
    }

    public void setMinus(boolean minus) {
        this.minus = minus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
