package com.school.teachermanage.entity;

import com.school.teachermanage.enumeration.BalanceSource;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 余额记录
 *
 * @author zhangsl
 * @date 2017-11-04
 */
@Entity
public class BalanceRecord {

    @Id
    @GeneratedValue
    private Long id;
    private Date createDate;
    @Enumerated(EnumType.STRING)
    private BalanceSource balanceSource;
    @Column(name = "subtract")
    private boolean minus;
    @Column(columnDefinition = "decimal(19,7)")
    private BigDecimal amount;
    @Column(columnDefinition = "decimal(19,7)")
    private BigDecimal balance;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    @ManyToOne
    @JoinColumn(name = "consumeUserId")
    private User consumeUser;
    @ManyToOne
    @JoinColumn(name = "sourceUserId")
    private User sourceUser;

    @OneToOne
    @JoinColumn(name = "payoutRecordId")
    private PayoutRecord payoutRecord;
    private int level;

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

    public BalanceSource getBalanceSource() {
        return balanceSource;
    }

    public void setBalanceSource(BalanceSource balanceSource) {
        this.balanceSource = balanceSource;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getConsumeUser() {
        return consumeUser;
    }

    public void setConsumeUser(User consumeUser) {
        this.consumeUser = consumeUser;
    }

    public User getSourceUser() {
        return sourceUser;
    }

    public void setSourceUser(User sourceUser) {
        this.sourceUser = sourceUser;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public PayoutRecord getPayoutRecord() {
        return payoutRecord;
    }

    public void setPayoutRecord(PayoutRecord payoutRecord) {
        this.payoutRecord = payoutRecord;
    }
}
