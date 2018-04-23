package com.school.teachermanage.entity;

import javax.persistence.*;

/**
 * 学校实体
 */
@Entity
public class School {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addressId")
    private Address address;

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

    public Address getAddress() { return address; }

    public void setAddress(Address address) { this.address = address; }
}
