package hellojpa;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity //(name = "Member")
// @Table(name = "USER", schema= "") USER라는 테이블에 저장하고 싶을 때 정의
public class Member {

    @Id
    private Long id;

    // @Column(unique = true, length = 10, nullable = false, columnDefinition = "default 'chan'")
    @Column(name = "name")
    private String userName;

    private Integer age;

    @Enumerated(EnumType.STRING) // Enum 타입을 사용할 때 ODINAL은 순서를 저장, STRING은 enum의 이름을 그대로 저장
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP) // 날짜 타입
    private Date CreatedDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    private LocalDate testLocalDate;

    private LocalDateTime testLocalDateTime;

    @Lob // TEXT와 같이 큰 컨텐츠
    private String description;

    @Transient // 컬럼에 추가하지 않고 메모리에서만 사용
    private int temp;

    public Member() {
    }

}
