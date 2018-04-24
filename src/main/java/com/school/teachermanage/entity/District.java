package com.school.teachermanage.entity;

import javax.persistence.*;

/**
 * 地区模型
 *
 * @author zhangsl
 * @date 2017-11-15
 */
@Entity
public class District {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cityId")
    private City city;
    private String letter;

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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getLetter() { return letter; }

    public void setLetter(String letter) { this.letter = letter; }
}
