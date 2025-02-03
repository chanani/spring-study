package hellojpa;

import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        // 로딩 시점에 딱 하나만 만들어 놓는다. 데이터 베이스 당 한개씩 묶여서 돌아간다.
        // persistenceUnitName은 xml 파일에서 읽어온다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        // DB 커넥션을 하나 받았다고 생각하면 된다. 요청이 오면 썻다가 닫았다가.
        // 쓰레드간에 공유 X(사용하고 버려야 한다.)
        EntityManager em = emf.createEntityManager();

        // 꼭 트랜젝션 안에서 작업해야된다.
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            /* 추가
            FiledColumnMapping member = new FiledColumnMapping();
            member.setId(2L);
            member.setName("HelloB");
            em.persist(member);
            */

            /* 수정
            FiledColumnMapping findMember = em.find(FiledColumnMapping.class, 1L);
            findMember.setName("HelloJPA"); // 변경을 감지해서 update 쿼리가 날라감
             */

            /* JPQL로 조회 FiledColumnMapping 객체를 대상으로 전부 조회 (JPQL은 객체 지향 SQL)*/
            /*List<FiledColumnMapping> result = em.createQuery("SELECT m FROM FiledColumnMapping AS m", FiledColumnMapping.class)
                    .setFirstResult(1) // 1번부터
                    .setMaxResults(10) // 10개 가져와
                    .getResultList(); */


            // 단방햔 연관 관계
            /*Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUserName("member1");
            em.persist(member);

            team.addMember(member);

            em.flush();
            em.clear();

            Member findMember = em.find(Member.class, member.getId());
            List<Member> members = findMember.getTeam().getMembers();
            for (Member m : members) {
                System.out.println("m.getUserName() = " + m.getUserName());
            } */

           /*Member member = new Member();
           member.setCreatedBy("kim");
           member.setCreatedDate(LocalDateTime.now());

           em.persist(member);
            em.flush();
            em.clear(); */

            /* Member member1 = new Member();
            member1.setUserName("hello1");
            em.persist(member1);

            Member member2 = new Member();
            member1.setUserName("hello2");
            em.persist(member2);

            em.flush();
            em.clear();

            Member m1 = em.find(Member.class, member1.getId());
            // Member m2 = em.find(Member.class, member2.getId());
            Member m2 = em.getReference(Member.class, member2.getId());
            // System.out.println("m1 == m2 : " + (m1.getClass() == m2.getClass()));
            login(m1, m2); */

            ////////////////////////////////////////
            // 준영속 상태일 경우
            Member member1 = new Member();
            member1.setUserName("hello1");
            em.persist(member1);

            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("refMember = " + refMember.getClass());

            em.flush();
            // em.detach(refMember);
            em.clear();
            /*refMember.getUserName(); // 영속성의 도움을 받지 못해 에러 발생
            System.out.println("inLoaded = " + emf.getPersistenceUnitUtil().isLoaded(refMember)); // 프록시 인스턴스의 초기화 여부 확인
            Hibernate.initialize(refMember); // 강제 초기화 */

            /////////////////////////////////////////
            // ManyToOne 일 때 즉시 로딩 :
            // SQL 통해서 member의 정보를 조회하지만 Member 객체 안에는 team 변수가 EAGER로 설정 되어 있기 때문에 team 정보 조회가 같이 이루어진다.
            // Member 필드의 컬럼은 LAZY로 설정하고 JOIN FETCH를 통해 팀의 정보를 조회할 수 있다.
            List<Member> members = em.createQuery("SELECT m FROM Member m JOIN FETCH m.team", Member.class)
                    .getResultList();

            // commit 전 쓰기 지연 SQL 저장소에 저장
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }

    // 타입은 instanceof로 비교
    private static void login(Member m1, Member m2) {
        System.out.println("m1 == m2 : " + (m1 instanceof Member));
        System.out.println("m1 == m2 : " + (m2 instanceof Member));

    }
}
