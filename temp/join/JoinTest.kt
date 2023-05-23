package com.querybuilder.join

import com.querybuilder.entity.QMember
import com.querybuilder.entity.QTeam
import com.querydsl.jpa.impl.JPAQueryFactory
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@SpringBootTest
@Transactional
class JoinTest @Autowired constructor(
    @PersistenceContext
    private val em: EntityManager,
    private val queryFactory: JPAQueryFactory
) {

    /**
     * 팀 A에 소속된 모든 회원 조인
     * @desc Inner Join
     */
    @Test
    fun join() {
        val m = QMember.member
        val t = QTeam.team

        val result = queryFactory
            .selectFrom(m)
            .join(m.team, t)
            .where(t.name.eq("teamA"))
            .fetch()

        assertThat(result)
            .extracting("name")
            .containsExactly("member1", "member2")
    }

    /**
     * 크로스 조인 (연관관계가 없는 필드로 조인)
     * 회원의 이름이 팀 이름과 같은 회원 조회
     * @desc Cross Join
     */
    @Test
    fun crossJoin() {
        val m = QMember.member
        val t = QTeam.team

        val result = queryFactory
            .select(m)
            .from(m, t)
            .where(m.name.eq(t.name))
            .fetch()

        assertThat(result)
            .extracting("name")
            .containsExactly("teamA", "teamB")
    }

    /**
     * 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL : SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'teamA'
     * SQL : SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and t.name='teamA'
     * @desc ON 절을 활용한 조인 (조인 대상 필터링)
     */
    @Test
    fun filterJoin() {
        val m = QMember.member
        val t = QTeam.team

        val result = queryFactory
            .select(m, t)
            .from(m)
            .leftJoin(m.team, t).on(t.name.eq("teamA"))
            .fetch()

        result.forEach { println("tuple = $it") }
    }
}