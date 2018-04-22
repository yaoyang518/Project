package com.school.teachermanage.entity;

import com.school.teachermanage.enumeration.BitcoinSource;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 报单积分记录
 *
 * @author zhangsl
 * @date 2017-11-27
 */
@Entity
public class BitcoinRecord {

    @Id
    @GeneratedValue
    private Long id;
    private Date createDate;
    @Enumerated(EnumType.STRING)
    private BitcoinSource bitcoinSource;
    @Column(name = "subtract")
    private boolean minus;
    @Column(columnDefinition = "decimal(19,7)")
    private BigDecimal amount;
    @Column(columnDefinition = "decimal(19,7)")
    private BigDecimal total;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    @ManyToOne
    @JoinColumn(name = "personId")
    private User person;
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

    public BitcoinSource getBitcoinSource() {
        return bitcoinSource;
    }

    public void setBitcoinSource(BitcoinSource bitcoinSource) {
        this.bitcoinSource = bitcoinSource;
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

    public User getPerson() {
        return person;
    }

    public void setPerson(User person) {
        this.person = person;
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
