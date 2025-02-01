package hellojpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;

    // mappedBy team에 의해 mapping 되었음을 의미(주인울 지정), Owner일 경우 사용 X
    // 1:N일 경우 N이 Owner(주인)
    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    // 양방양일 경우 두 객체에 모두 데이터를 넣기 위해
    public void addMember(Member member) {
        member.setTeam(this);
        members.add(member);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }


}
