package com.querybuilder.entity

import javax.persistence.*

@Entity
class Team (

    @Column(name = "team_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    var name: String = "",

    @OneToMany(mappedBy = "team")
    var members: List<Member> = mutableListOf()
) {
    constructor(name: String): this(id = 0L, name = name)
}