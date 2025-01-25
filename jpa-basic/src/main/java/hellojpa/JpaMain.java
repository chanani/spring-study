package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

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


            // commit 전 쓰기 지연 SQL 저장소에 저장
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
