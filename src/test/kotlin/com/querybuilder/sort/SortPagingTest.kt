package com.querybuilder.sort

import com.querybuilder.entity.Member
import com.querybuilder.entity.QMember
import com.querydsl.jpa.impl.JPAQueryFactory
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@SpringBootTest
@Transactional
class SortPagingTest @Autowired constructor(
    @PersistenceContext
    private val em: EntityManager,
    private val queryFactory: JPAQueryFactory
) {

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순 (desc)
     * 2. 회원 이름 올림차순 (asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력 (nulls test)
     */
    @Test
    fun sort() {
        val m: QMember = QMember.member

        em.persist(Member(null, 100))
        em.persist(Member("member5", 100))
        em.persist(Member("member6", 100))

        val result = queryFactory
            .selectFrom(m)
            .where(m.age.eq(100))
            .orderBy(m.age.desc(), m.name.asc().nullsFirst())
            .fetch()

        val member5 = result.get(0)
        val member6 = result.get(1)
        val memberNull = result.get(2)
        assertThat(member5.name).isEqualTo("member5")
        assertThat(member6.name).isEqualTo("member6")
        assertThat(memberNull.name).isNull()

    }

    /** @desc 페이징 - 조회 건수 제한 */
    @Test
    fun paging1() {
        val m: QMember = QMember.member

        val result = queryFactory
            .selectFrom(m)
            .orderBy(m.name.desc())
            .offset(1)
            .limit(2)
            .fetch()

        assertThat(result.size).isEqualTo(2)
    }

    /** @desc 페이징 - 전체 조회 수가 필요할 때 */
    @Test
    fun paging2() {
        val m: QMember = QMember.member

        val result = queryFactory
            .selectFrom(m)
            .orderBy(m.name.desc())
            .offset(1)
            .limit(2)
            .fetchResults()

        assertThat(result.total).isEqualTo(4)
        assertThat(result.limit).isEqualTo(2)
        assertThat(result.offset).isEqualTo(1)
        assertThat(result.results).size().isEqualTo(2)
    }
}