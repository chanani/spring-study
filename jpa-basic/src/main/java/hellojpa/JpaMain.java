package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        // 로딩 시점에 딱 하나만 만들어 놓는다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        // DB 커넥션을 하나 받았다고 생각하면 된다. 요청이 오면 썻다가 닫았다가.
        // 쓰레드간에 공유 X(사용하고 버려야 한다.)
        EntityManager em = emf.createEntityManager();

        // 꼭 트랜젝션 안에서 작업해야된다.
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            /* 추가
            Member member = new Member();
            member.setId(2L);
            member.setName("HelloB");
            em.persist(member);
            */

            /* 수정
            Member findMember = em.find(Member.class, 1L);
            findMember.setName("HelloJPA");
             */

            /* JPQL로 조회 Member 객체를 대상으로 전부 조회 (JPQL은 객체 지향 SQL)*/
            List<Member> result = em.createQuery("SELECT m FROM Member AS m", Member.class)
                    .setFirstResult(1) // 1번부터
                    .setMaxResults(10) // 10개 가져와
                    .getResultList();

            for (Member member : result) {
                System.out.println("member = " + member.getName());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
