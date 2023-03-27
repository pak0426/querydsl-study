package study.querydsl.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@ActiveProfiles("local")
class MemberRepositoryTest {

    @Autowired EntityManager em;
    @Autowired MemberRepository memberRepository;

    @Test
    public void test() {
        Member memberABC = new Member("memberABC", 10);
        em.persist(memberABC);

        List<Member> result = memberRepository.findMemberByUsername("member1");

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }
}