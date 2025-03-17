package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryOld {

    // @PersistenceContext // RequiredArgsConstructor로 인해 생략 가능 final 필수
    public final EntityManager em;

    // 회원 정보 등록
    public void save(Member member) {
        em.persist(member);
    }

    // 회원 정보 조회
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    // 회원 목록 조회
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    // 이름으로 회원 정보 조회
    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

}
