package study.querydsl.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Rollback(value = false)
class MemberTest {

    @Autowired EntityManager em;

    @Test
    public void MemberTest() {
        Team teamA = new Team("team1");
        em.persist(teamA);

        Member memberA = new Member("m1", 28, teamA);
        Member memberB = new Member("m2", 29, teamA);
        em.persist(memberA);
        em.persist(memberB);

        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        List<Member> members2 = em.createQuery("select m from Member m join fetch m.team t", Member.class)
                .getResultList();

        assertThat(memberA.getUsername()).isEqualTo("m1");
        assertThat(memberB.getTeam().getName()).isEqualTo("team1");
        assertThat(memberA.getTeam()).isEqualTo(memberB.getTeam());

        assertThat(members.size()).isEqualTo(members2.size());
        assertThat(members2.get(0).getTeam().getName()).isEqualTo("team1");

        for (Member member : members2) {
            System.out.println("member = " + member);
        }
    }
}