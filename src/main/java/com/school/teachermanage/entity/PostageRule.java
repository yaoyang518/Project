package com.school.teachermanage.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 运费规则
 *
 * @author zhangsl
 * @date 2017-11-15
 */
@Entity
public class PostageRule {

    @Id
    @GeneratedValue
    private Long id;
    private double weight;
    private BigDecimal price;
    private double appendWeight;
    private BigDecimal appendPrice;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postageTemplateId")
    private PostageTemplate postageTemplate;

    private Date createDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adminId")
    private Admin admin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public double getAppendWeight() {
        return appendWeight;
    }

    public void setAppendWeight(double appendWeight) {
        this.appendWeight = appendWeight;
    }

    public BigDecimal getAppendPrice() {
        return appendPrice;
    }

    public void setAppendPrice(BigDecimal appendPrice) {
        this.appendPrice = appendPrice;
    }

    public PostageTemplate getPostageTemplate() {
        return postageTemplate;
    }

    public void setPostageTemplate(PostageTemplate postageTemplate) {
        this.postageTemplate = postageTemplate;
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
}
