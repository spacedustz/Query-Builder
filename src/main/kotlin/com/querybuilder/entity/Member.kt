package com.querybuilder.entity

import java.util.UUID
import javax.persistence.*

@Entity
data class Member(

    @Column(name = "member_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: String = "",
    var name: String,
    var age: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var team: Team
) {

    constructor(name: String): this(UUID.randomUUID().toString().substring(0, 8), name, 0, Team())

    constructor(name: String, age: Long, team: Team): this(UUID.randomUUID().toString().substring(0, 8), name, age, team)

    fun changeTeam(team: Team) {
        this.team = team
        team.members.plus(this)
    }

    fun generateId(): String {
        return UUID.randomUUID().toString().substring(0, 8)
    }
}