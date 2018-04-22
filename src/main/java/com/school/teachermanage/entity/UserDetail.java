package com.school.teachermanage.entity;

import com.school.teachermanage.enumeration.UserLevel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户详情
 *
 * @author zhangsl
 * @date 2017/11/17
 */
@Entity
public class UserDetail {

    @Id
    @GeneratedValue
    private Long id;

    @ApiModelProperty(hidden = true)
    private Date shopKeeperDate;//升级店主时间

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    private Date lastLogin;

    private Date lastIp;
    @Enumerated(EnumType.STRING)
    private UserLevel userLevel;
    @Enumerated(EnumType.STRING)
    private UserLevel nextLevel;

    private String image;

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

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getLastIp() {
        return lastIp;
    }

    public void setLastIp(Date lastIp) {
        this.lastIp = lastIp;
    }

    public UserLevel getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(UserLevel userLevel) {
        this.userLevel = userLevel;
    }

    public UserLevel getNextLevel() {
        return nextLevel;
    }

    public void setNextLevel(UserLevel nextLevel) {
        this.nextLevel = nextLevel;
    }

    public String getImage() {
        return image;
    }

    public Date getShopKeeperDate() {
        return shopKeeperDate;
    }

    public void setShopKeeperDate(Date shopKeeperDate) {
        this.shopKeeperDate = shopKeeperDate;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
