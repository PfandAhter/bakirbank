package com.bakirbank.bakirbank.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "branch")
@AllArgsConstructor
@NoArgsConstructor

public class Branch {

    @Id
    @Column(name = "branch_id")
    private String branchId;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "active")
    private int active;

}