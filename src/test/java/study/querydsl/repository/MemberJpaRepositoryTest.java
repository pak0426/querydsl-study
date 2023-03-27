package study.querydsl.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;

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
}