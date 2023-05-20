package com.querybuilder.join

import com.querybuilder.entity.QMember
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import javax.persistence.PersistenceContext

@SpringBootTest
@Transactional
class SubQueryJoinTest @Autowired constructor(
    @PersistenceContext
    private val queryFactory: JPAQueryFactory
) {

    /**  @desc SubQuery Join - eq 사용 */
    @Test
    fun subQueryJoinEq() {
        val m = QMember.member
        val sub = QMember("sub")

        val result = queryFactory
            .selectFrom(m)
            .where(m.age.eq(JPAExpressions
                .select(sub.age.max())
                .from(sub)
            ))
            .fetch()

        assertThat(result).extracting("age").containsExactly(40)
    }

    /** @desc SubQuery Join - geo 사용 */
    @Test
    fun subQueryJoinGoe() {
        val m = QMember.member
        val sub = QMember("sub")

        val result = queryFactory
            .selectFrom(m)
            .where(m.age.goe(JPAExpressions
                .select(sub.age.avg())
                .from(sub)
            ))
            .fetch()

        assertThat(result).extracting("age").containsExactly(30, 40)
    }
}