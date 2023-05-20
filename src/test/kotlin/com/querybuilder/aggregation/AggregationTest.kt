package com.querybuilder.aggregation

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
import javax.persistence.Tuple

@SpringBootTest
@Transactional
class AggregationTest @Autowired constructor(
    @PersistenceContext
    private val em: EntityManager,
    private val queryFactory: JPAQueryFactory
) {

    /**
     * COUNT(m) - 회원 수
     * SUM(m.age) - 나이 합
     * AVG(m.age) - 평균 나이
     * MAX(m.age) - 최대 나이
     * MIN(m.age) - 최소 나이
     * @desc 집합 함수
     */
    @Test
    fun aggregation() {
        val m: QMember = QMember.member

        val result: MutableList<com.querydsl.core.Tuple>? = queryFactory
            .select(
                m.count(),
                m.age.sum(),
                m.age.avg(),
                m.age.max(),
                m.age.min()
            )
            .from(m)
            .fetch()

        val tuple = result?.get(0)

        assertThat(tuple?.get(m.count())).isEqualTo(4)
        assertThat(tuple?.get(m.age.sum())).isEqualTo(100)
        assertThat(tuple?.get(m.age.avg())).isEqualTo(25)
        assertThat(tuple?.get(m.age.max())).isEqualTo(40)
        assertThat(tuple?.get(m.age.min())).isEqualTo(10)
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령 구하기
     * @desc Group By 사용
     */
    @Test
    fun group() {
        val m: QMember = QMember.member
        val t: QTeam = QTeam.team

        val result: MutableList<com.querydsl.core.Tuple>? = queryFactory
            .select(t.name, m.age.avg())
            .from(m)
            .join(m.team, t)
            .groupBy(t.name)
            .fetch()

        val teamA = result?.get(0)
        val teamB = result?.get(1)

        assertThat(teamA?.get(t.name)).isEqualTo("teamA")
        assertThat(teamA?.get(m.age.avg())).isEqualTo(15)

        assertThat(teamB?.get(t.name)).isEqualTo("teamB")
        assertThat(teamB?.get(m.age.avg())).isEqualTo(35)
    }
}