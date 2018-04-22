package com.school.teachermanage.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户帐户
 * @author zhangsl
 * @date 2017-11-05
 */
@Entity
public class Account {

    @Id
    @GeneratedValue
    private Long id;
    private Date createDate;
    @Column(columnDefinition = "decimal(19,7)")
    private BigDecimal score;
    @Column(columnDefinition = "decimal(19,7)")
    private BigDecimal frozenScore;
    @Column(columnDefinition = "decimal(19,7)")
    private BigDecimal balance;
    private BigDecimal ticket;
    private BigDecimal bitcoin;


    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

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

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getFrozenScore() {
        return frozenScore;
    }

    public void setFrozenScore(BigDecimal frozenScore) {
        this.frozenScore = frozenScore;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Transient
    public BigDecimal getTotalScore(){
        return score.add(frozenScore).setScale(7);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getTicket() {
        return ticket;
    }

    public void setTicket(BigDecimal ticket) {
        this.ticket = ticket;
    }

    public BigDecimal getBitcoin() {
        return bitcoin;
    }

    public void setBitcoin(BigDecimal bitcoin) {
        this.bitcoin = bitcoin;
    }
}
