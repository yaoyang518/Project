package com.school.teachermanage.entity;

import com.school.teachermanage.enumeration.UserLevel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 *
 * @author zhangsl
 * @date 2017-11-01
 */
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue
    @ApiParam()
    @ApiModelProperty(hidden = true)
    private Long id;//用户ID
    @ApiModelProperty(hidden = true)
    private Date createDate;//创建时间
    @ApiModelProperty(hidden = true)
    private Date updateDate;//创建时间
    @ApiModelProperty(hidden = true)
    private String username;//用户昵称
    private String password;//用户密码
    @Column(unique = true)
    private String mobile;//手机号

    @ApiModelProperty(hidden = true)
    private String salt;//用户密码盐值
    @ApiModelProperty(hidden = true)
    private byte state = 1;//用户状态, 1:正常,2：锁定.
    @ApiModelProperty(hidden = true)
    private Boolean shopKeeper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    @ApiModelProperty(hidden = true)
    private User parent;

    @OneToOne
    @JoinColumn(name = "accountId")
    @ApiModelProperty(hidden = true)
    private Account account;

    @Enumerated(EnumType.STRING)
    @ApiModelProperty(hidden = true)
    private UserLevel userLevel = UserLevel.USER_NORMAL;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public Boolean getShopKeeper() {
        return shopKeeper;
    }

    public void setShopKeeper(Boolean shopKeeper) {
        this.shopKeeper = shopKeeper;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public UserLevel getUserLevel() {
        return userLevel;
    }


    public void setUserLevel(UserLevel userLevel) {
        this.userLevel = userLevel;
    }
}
