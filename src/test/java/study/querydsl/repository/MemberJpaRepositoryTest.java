package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired EntityManager em;

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).get();
        assertThat(member).isEqualTo(findMember);

        List<Member> all = memberJpaRepository.findAll();
        assertThat(all).containsExactly(findMember);

        List<Member> byUsername = memberJpaRepository.findByUsername(member.getUsername());
        assertThat(byUsername).containsExactly(member);
    }

    @Test
    public void basicTest_Querydsl() {
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        List<Member> findMember = memberJpaRepository.findAll_Querydsl();
        assertThat(findMember).containsExactly(member);

        List<Member> byUsernameQuerydsl = memberJpaRepository.findByUsername_Querydsl(member.getUsername());
        assertThat(byUsernameQuerydsl).containsExactly(member);
    }

    @Test
    public void searchTest() {
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

        MemberSearchCondition searchCondition = new MemberSearchCondition();
//        searchCondition.setAgeGoe(35);
//        searchCondition.setAgeLoe(40);
        searchCondition.setTeamName("teamB");

        List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(searchCondition);

        for (MemberTeamDto memberTeamDto : result) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }

        assertThat(result).extracting("username").containsExactly("member3", "member4");
    }
}