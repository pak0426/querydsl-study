package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
public class QueryDslBasicTest {

    @Autowired EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL() {
        //member1 찾아라.
        String qlString = "select m from Member m " +
                "where m.username = :username";

        Member result = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(result.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {
//        alias 지정 - yml 파일에서 use_sql_comments설정을 해주면 쿼리 조회시 반영
//        -> 같은 테이블을 조회할때 사용한다.
//        QMember qMember = new QMember("m");
//        QMember qMember = QMember.member;

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.between(10, 30)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
        assertThat(findMember.getAge()).isEqualTo(10);
    }

    @Test
    public void searchAndParam() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        (member.age.eq(10))
                )
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
        assertThat(findMember.getAge()).isEqualTo(10);
    }

    /*
    fetch() : 리스트 조회, 데이터 없으면 빈 리스트 반환
    fetchOne() : 단 건 조회
    결과가 없으면 : null
    결과가 둘 이상이면 : com.querydsl.core.NonUniqueResultException
    fetchFirst() : limit(1).fetchOne()
    fetchResults() : 페이징 정보 포함, total count 쿼리 추가 실행
    fetchCount() : count 쿼리로 변경해서 count 수 조회
     */
    @Test
    public void resultFetch() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch();

        Member fetchOne = queryFactory
                .selectFrom(member)
                .fetchOne();

        Member fetchFirst = queryFactory
                .selectFrom(member)
                .fetchFirst();

        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();

        results.getTotal();
        List<Member> content = results.getResults();

        Long count = queryFactory
                .selectFrom(member)
                .fetchCount();
    }

    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));


        List<Member> members = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = members.get(0);
        Member member6 = members.get(1);
        Member memberNull = members.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getAge()).isEqualTo(member5.getAge());
        assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    public void paging() {
        List<Member> members = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) //N번째 row 부터
                .limit(3) //N개를 가져온다.
                .fetch();

        assertThat(members.size()).isEqualTo(3);
    }

    @Test
    public void paging2() {
        QueryResults<Member> fetchResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) //N번째 row 부터
                .limit(3) //N개를 가져온다.
                .fetchResults();

        assertThat(fetchResults.getTotal()).isEqualTo(4);
        assertThat(fetchResults.getLimit()).isEqualTo(3);
        assertThat(fetchResults.getOffset()).isEqualTo(1);
        assertThat(fetchResults.getResults().size()).isEqualTo(3);

    }

}
