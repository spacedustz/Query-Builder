## 조건

- 전체 조회 시 [특정 카테고리, 이외 카테고리, 이외 카테고리] 순서를 반복 즉, 특정 카테고리와 다른 카테고리를  1:2 의 비율로 조회하기
- 게시글 수도 1:2 비율에 맟춰 15:30개 총 45개를 1개의 페이지로 한다.

---

## 구현

```kotlin
/**
 * @author 신건우
 * @param kind : 게시판 종류
 * @param location : 위치 정보
 * @param animal : 반려동물 정보
 * @param geometry : DB에 저장된 위치 정보 리스트
 * @param userGeometry : 유저 위치 정보
 * @param notInUsers : 포함되지 않는 유저들
 * @param pageable : 페이지 정보
 * @desc 투데이펫 15 : 이외 게시글 30으로 총 45개의 전체 게시글 조회 기능
 */
override fun findBoardByCondition3(kind: String, location: String, animal: String, geometry: List<GeometryDTO.SQLGeometry>, userGeometry : Geometry?, notInUsers: List<String>, pageable: Pageable,): List<Board> {

    var builder = BooleanBuilder()

    if (location == "1") {
        for (item in geometry) {
            builder.or((board.user.geometry.sido).eq(item.sido).and(board.user.geometry.sigungu.eq(item.sigungu)))
        }
    }
    else if (location == "2" && userGeometry != null) {
        builder.and(board.user.geometry.sido.eq(userGeometry.sido).and(board.user.geometry.sigungu.eq(userGeometry.sigungu)))
    }

    when (animal) {
        "1" -> builder.and(board.user.animal.eq("0"))
        "2" -> builder.and(board.user.animal.eq("1"))
    }

    var careGive = from(board)
        .where(
            board.user.animals.isNotEmpty
                .and(board.user.deleted.eq(false))
                .and(board.user.grade.eq("정회원"))
                .and(board.user.id.notIn(notInUsers))
                .and(board.deleted.eq(false))
                .and(board.kind.eq("돌봄제공"))
                .and(builder)
        )
        .offset(pageable.offset)
        .limit(pageable.pageSize.toLong() / 2)
        .orderBy(board.user.userToken.tokenCreatedAt.desc())
        .fetch()

    var entire = from(board)
        .where(
            board.user.animals.isNotEmpty
                .and(board.user.deleted.eq(false))
                .and(board.user.grade.eq("정회원"))
                .and(board.user.id.notIn(notInUsers))
                .and(board.deleted.eq(false))
                .and(board.kind.ne("돌봄제공"))
                .and(builder)
        )
        .offset(pageable.offset)
        .limit(pageable.pageSize.toLong())
        .orderBy(board.createdAt.desc())
        .fetch()

    var result : MutableList<Board> = mutableListOf()

    while(careGive.isNotEmpty() || entire.isNotEmpty()) {

       log.info("entire : ${entire.size} careGive : ${careGive.size}")

        var careGiveItem = careGive.removeFirstOrNull()
        var entireItem01 = entire.removeFirstOrNull()
        var entireItem02 = entire.removeFirstOrNull()

        if (careGiveItem != null) {
            result.add(careGiveItem)
        }

        if (entireItem01 != null) {
            result.add(entireItem01)
        }

        if (entireItem02 != null) {
            result.add(entireItem02)
        }

        if (careGive.size == 0 && entire.size == 0) {
            log.info("브레이크 entire : ${entire.size} careGive : ${careGive.size}")
            break;
        }

    }

    log.info("나머지글 개수 = ${entire.size}")
    log.info("돌봄제공 글 개수 = ${careGive.size}")

    log.info("총 글 개수 = ${result.size}")

    return result
}```