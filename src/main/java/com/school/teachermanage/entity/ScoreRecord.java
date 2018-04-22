package com.school.teachermanage.entity;

import com.school.teachermanage.enumeration.ScoreSource;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 积分记录
 *
 * @author zhangsl
 * @date 2017-11-04
 */
@Entity
public class ScoreRecord {

    @Id
    @GeneratedValue
    private Long id;
    private Date createDate;
    @Enumerated(EnumType.STRING)
    private ScoreSource scoreSource;
    @Column(name = "subtract")
    private boolean minus;
    private boolean frozened;
    @Column(columnDefinition = "decimal(19,7)")
    private BigDecimal amount;
    @Column(columnDefinition = "decimal(19,7)")
    private BigDecimal frozen;
    @Column(columnDefinition = "decimal(19,7)")
    private BigDecimal score;
    @Column(columnDefinition = "decimal(19,7)")
    private BigDecimal total;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    @ManyToOne
    @JoinColumn(name = "consumeUserId")
    private User consumeUser;
    @ManyToOne
    @JoinColumn(name = "sourceUserId")
    private User sourceUser;
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

    public ScoreSource getScoreSource() {
        return scoreSource;
    }

    public void setScoreSource(ScoreSource scoreSource) {
        this.scoreSource = scoreSource;
    }

    public Boolean getMinus() {
        return minus;
    }

    public void setMinus(Boolean minus) {
        this.minus = minus;
    }

    public boolean getFrozened() {
        return frozened;
    }

    public void setFrozened(boolean frozened) {
        this.frozened = frozened;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFrozen() {
        return frozen;
    }

    public void setFrozen(BigDecimal frozen) {
        this.frozen = frozen;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
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

}
