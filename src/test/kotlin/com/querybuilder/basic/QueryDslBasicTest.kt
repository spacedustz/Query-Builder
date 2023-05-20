package com.querybuilder.basic

import com.querybuilder.entity.Member
import com.querybuilder.entity.QMember
import com.querybuilder.entity.Team
import com.querydsl.jpa.impl.JPAQueryFactory
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceContext


@SpringBootTest
@Transactional
class QueryDslBasicTest @Autowired constructor(
    @PersistenceContext
    val em: EntityManager,
    val emf: EntityManagerFactory,
    val queryFactory: JPAQueryFactory
) {

    @BeforeEach
    fun before() {
        val teamA = Team("teamA")
        val teamB = Team("teamB")
        em.persist(teamA)
        em.persist(teamB)
        val member1 = Member("member1", 10, teamA)
        val member2 = Member("member2", 20, teamA)
        val member3 = Member("member3", 30, teamB)
        val member4 = Member("member4", 40, teamB)
        em.persist(member1)
        em.persist(member2)
        em.persist(member3)
        em.persist(member4)
    }

    @Test fun startJPQL() {

        val qlString: String = """
            select m from Member m
            where m.name =: name
        """.trimIndent()

        val findMember = em.createQuery(qlString, Member::class.java)
            .setParameter("name", "member1")
            .singleResult

        assertThat(findMember.name).isEqualTo("member1")
    }

    @Test
    fun startQuerydsl() {

        // Q Type 별칭 지정
        val m = QMember("m")

        val findMember = queryFactory
            .select(m)
            .from(m)
            .where(m.name.eq("member1"))
            .fetchOne()

        assertThat(findMember?.name).isEqualTo("member1")
    }
}