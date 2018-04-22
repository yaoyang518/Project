package com.school.teachermanage.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 运费省份
 *
 * @author zhangsl
 * @date 2017-11-17
 */
@Entity
public class PostageProvince {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provinceId")
    private Province province;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postageRuleId")
    private PostageRule postageRule;

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

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public PostageRule getPostageRule() {
        return postageRule;
    }

    public void setPostageRule(PostageRule postageRule) {
        this.postageRule = postageRule;
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
