package hellojpa;

import javax.persistence.*;

@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String userName;

    // @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 : Proxy 객체를 조회한다. 객체 생성 시점이 아닌 값을 사용할 때 초기화 요청
    @ManyToOne(fetch = FetchType.EAGER) // 즉시 로딩 : em.find() 사용 시 즉시 DB에 접근해서 값을 조회
    @JoinColumn(name = "TEAM_ID")
    private Team team;

/*
    @OneToOne
    @JoinColumn(name = "LOCKER_ID")
    private Locker locker;

    @OneToMany(mappedBy = "member")
    private List<MemberProduct> memberProducts = new ArrayList<>();
*/

    public Member() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

}
