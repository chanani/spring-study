package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    // order 테이블에 있는 member 필드에 의해 매핑된 것임을 표시(읽기 전용)
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

}
