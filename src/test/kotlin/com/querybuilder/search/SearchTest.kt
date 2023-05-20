package com.querybuilder.search

import com.querybuilder.entity.QMember
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
class SearchTest @Autowired constructor(
    @PersistenceContext
    val em: EntityManager,
    val queryFactory: JPAQueryFactory
) {

    /** @desc 기본 검색 쿼리 */
    @Test
    fun search() {
        val m: QMember = QMember("m")

        val findMember = queryFactory
            .selectFrom(m)
            .where(m.name.eq("member1").and(m.age.eq(10)))
            .fetchOne()

        assertThat(findMember?.name).isEqualTo("member1")

        m.name.eq("member1")
        m.name.ne("member2")
        m.name.eq("member1").not()

        m.name.isNotNull

        m.age.`in`(10, 20)
        m.age.notIn(10, 20)
        m.age.between(10, 30)

        m.age.goe(30) // age >= 30
        m.age.gt(30) // age > 30
        m.age.loe(30) // age <= 30
        m.age.lt(30) // age < 30

        m.name.like("member%") // like 검색
        m.name.contains("member") // like %member% 검색
        m.name.startsWith("member") // like member% 검색
    }
}