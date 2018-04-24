package com.school.teachermanage.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 省份模型
 *
 * @author zhangsl
 * @date 2017-11-15
 */
@Entity
public class Province {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
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

    public String getLetter() { return letter; }

    public void setLetter(String letter) { this.letter = letter; }
}
