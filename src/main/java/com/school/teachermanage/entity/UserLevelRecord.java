package com.school.teachermanage.entity;

import com.school.teachermanage.enumeration.UserLevel;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户等级变更记录
 *
 * @author zhangsl
 * @date 2017-11-13
 */
@Entity

public class UserLevelRecord {

    @Id
    @GeneratedValue
    private Long id;
    //变更人
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;
    //变更前
    @Enumerated(EnumType.STRING)
    private UserLevel changeFrom;
    //变更后
    @Enumerated(EnumType.STRING)
    private UserLevel changeTo;
    //创建时间
    private Date createDate;
    //管理员
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adminId")
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "levelUpId")
    private LevelUp levelUp;
    //备注
    private String remark;

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

    public UserLevel getChangeFrom() {
        return changeFrom;
    }

    public void setChangeFrom(UserLevel changeFrom) {
        this.changeFrom = changeFrom;
    }

    public UserLevel getChangeTo() {
        return changeTo;
    }

    public void setChangeTo(UserLevel changeTo) {
        this.changeTo = changeTo;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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

    public LevelUp getLevelUp() {
        return levelUp;
    }

    public void setLevelUp(LevelUp levelUp) {
        this.levelUp = levelUp;
    }
}
