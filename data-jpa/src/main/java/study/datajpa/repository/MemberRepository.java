package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 관례상 NamedQuery를 우선적으로 찾아서 진행 그래서 주석해도 정상적으로 작동됨
    // 실무에서 잘 사용 안함
    // @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);


}
