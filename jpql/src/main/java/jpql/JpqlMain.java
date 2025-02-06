package jpql;

import javax.persistence.*;
import java.util.List;

public class JpqlMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);

            member.setTeam(team);

            em.persist(member);

            em.flush();
            em.clear();

            /*Member result = em.createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", "member1")
                    .getSingleResult();
            System.out.println("singleResult = " + result.getUsername());
            List<Member> resultList = query.getResultList(); // 다중 레코드가 반환될 경우
            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }
            Member singleResult = query.getSingleResult(); // 정확하게 1개의 레코드만 반환될 경우

            TypedQuery<String> query2 = em.createQuery("select m.username, m.age from Member m", String.class);
            // 반환 타입이 명확하지 않을 경우
            Query query3 = em.createQuery("select m.username, m.age from Member m");*/

            /////////// 프로젝션 : SELECT 절에 조회할 대상을 지정하는 것 - 영속성 컨텍스트에서 관리된다.
            /*List<MemberDTO> result = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList(); */

            /////////// 페이징
            /*List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(1)
                    .setMaxResults(10)
                    .getResultList();
            for (Member member1 : result) {
                System.out.println("member1.toString() = " + member1.toString());
            } */

            /////////// 조인
            /*String query = "select m from Member m left join m.team t on t.name = 'A'";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList(); */



            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
