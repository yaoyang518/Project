package com.school.teachermanage.entity;

import javax.persistence.*;

/**
 * 用户地址类
 *
 * @author zhangsl
 * @date 2017-11-15
 */
@Entity
public class Address {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String address;
    private String mobile;
    private boolean isDefault;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provinceId")
    private Province province;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cityId")
    private City city;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "districtId")
    private District district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
