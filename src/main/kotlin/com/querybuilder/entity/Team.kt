package com.querybuilder.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class Team (

    @Column(name = "team_id")
    @Id @GeneratedValue
    var id: String = "",
    var name: String = "",

    @OneToMany(mappedBy = "team")
    var members: List<Member> = mutableListOf()
)