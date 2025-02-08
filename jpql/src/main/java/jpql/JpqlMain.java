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
            Team teamA= new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB= new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);


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

            //////////// JPQL 타입
            // ENUM
            /*String query = "select m.username, 'HELLO', TRUE from Member m " +
                    // "where m.type = jpql.MemberType.ADMIN";
                    "where m.type = :userType";
            List<Object[]> result = em.createQuery(query)
                    .setParameter("userType", MemberType.ADMIN)
                    .getResultList();
            for (Object[] objects : result) {
                System.out.println("objects = " + objects[0]);
                System.out.println("objects = " + objects[1]);
                System.out.println("objects = " + objects[2]);
            }*/

            //////////// 조건식
            // CASE 식
            /*String query = "select " +
                    "case when m.age <= 10 then '학생요금'" +
                    "     when m.age >= 60 then '경로요금'" +
                    "     else '일반요금'" +
                    "end AS price " +
                    "from Member m";

            List<String> result = em.createQuery(query, String.class)
                    .getResultList();
            for (String s : result) {
                System.out.println("s = " + s);
            } */

            // coalesce
            /*String query = "select coalesce(m.username, '이름 없는 회원') from Member m";
            List<String> result = em.createQuery(query, String.class)
                    .getResultList();
            for (String s : result) {
                System.out.println("s = " + s);
            } */

            //////////// 함수
            // String query = "select substring(m.username, 2, 3) from Member m";
            // String query = "select locate('de', 'abcdefg') from Member m";
            // String query = "select size(t.members) from Team t";

            // 사용자 정의함수
           /* String query = "select group_concat(m.username) from Member m";
            List<String> result = em.createQuery(query, String.class)
                    .getResultList();
            for (String s : result) {
                System.out.println("s = " + s);
            }*/

            //////////// 경로 표현식
            /* String query = "select m.team from Member m";
            List<Team> result = em.createQuery(query, Team.class)
                    .getResultList();
            for (Team s : result) {
                System.out.println("s = " + s);
            } */

            //////////// 페치 조인
            // 일반 쿼리
            /*String query = "select m from Member m";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();
            for (Member member : result) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
                // 회원1, 팀A(SQL)
                // 회원2, 팀A(1차캐시)
                // 회원3, 팀B(SQL)
                // 모든 다른 팀에 회원이 100명 있을 경우 -> N + 1
            } */

            // 1 : N 페치 조인
            /* String query = "select m from Member m join fetch m.team";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();
            for (Member member : result) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
            } */

            // M : 1 페치 조인
            String query = "select distinct t from Team t join fetch t.members";
            List<Team> result = em.createQuery(query, Team.class)
                    .getResultList();
            for (Team team : result) {
                System.out.println("team = " + team.getName() + "+|members = " + team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println("-> member = " + member);
                }
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
