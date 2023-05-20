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
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceContext

@SpringBootTest
@Transactional
class FetchJoinTest @Autowired constructor(
    @PersistenceContext
    private val em: EntityManager,
    private val emf: EntityManagerFactory,
    private val queryFactory: JPAQueryFactory
) {

    /**
     * 지연로딩으로 Member, Team SQL 쿼리 각각 실행
     * @desc Fetch Join 미적용 예시
     */
    @Test
    fun noFetchJoin() {
        val m = QMember.member

        em.flush()
        em.clear()

        val findMember = queryFactory
            .selectFrom(m)
            .where(m.name.eq("member1"))
            .fetchOne()

        val loaded: Boolean = emf.persistenceUnitUtil.isLoaded(findMember?.team)

        assertThat(loaded).`as`("Fetch Join 미적용").isFalse
    }

    /** @desc Fetch Join 적용 예시 */
    @Test
    fun fetchJoin() {
        val m = QMember.member
        val t = QTeam.team

        em.flush()
        em.clear()

        val findMember = queryFactory
            .selectFrom(m)
            .join(m.team, t).fetchJoin()
            .where(m.name.eq("member1"))
            .fetchOne()

        val loaded: Boolean = emf.persistenceUnitUtil.isLoaded(findMember?.team)

        assertThat(loaded).`as`("Fetch Join 적용").isTrue
    }


    /**
     *
     * @desc SubQuery Join
     */
    @Test
    fun subQueryJoin() {

    }
}