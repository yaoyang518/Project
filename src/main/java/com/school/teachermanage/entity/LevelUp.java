package com.school.teachermanage.entity;

import com.school.teachermanage.constants.NumConstants;
import com.school.teachermanage.enumeration.UserLevel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户升级金额配置
 *
 * @author zhangsl
 * @date 2017-11-013
 */
@Entity
public class LevelUp {

    @Id
    @GeneratedValue
    private Long id;

    private Boolean available = Boolean.FALSE; // 状态：是否可用

    @Enumerated(EnumType.STRING)
    private UserLevel userLevel;

    @Column(columnDefinition = "decimal(19,2)")
    private BigDecimal amount;

    @Column(columnDefinition = "decimal(19,2)")
    private BigDecimal directAmount;

    private Date createDate;



    @ApiModelProperty(hidden = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adminId")
    private Admin admin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public UserLevel getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(UserLevel userLevel) {
        this.userLevel = userLevel;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getDirectAmount() {
        return directAmount;
    }

    public void setDirectAmount(BigDecimal directAmount) {
        this.directAmount = directAmount;
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

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        if (userLevel.getIndex() == UserLevel.LEVEL_SIX.getIndex()){
            stringBuffer.append("最低");
        }
        stringBuffer.append(this.amount + "元升级" + this.getUserLevel().getName()+"，");
        if (userLevel.getIndex() >= UserLevel.LEVEL_SIX.getIndex()){
            BigDecimal benefit = this.amount.multiply(NumConstants.ZERO_POINT_ONE_TWO).setScale(2,BigDecimal.ROUND_HALF_DOWN);
            stringBuffer.append("直接上级收益(12%)：" + benefit+"元");
        }else{
            stringBuffer.append("直接上级收益：" + this.directAmount+"元");
        }
        return stringBuffer.toString();
    }
}
