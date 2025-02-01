package hellojpa;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // 정규화 한것과 같이 테이블이 상생됩니다.
// @Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 한테이블에 모든 컬럼이 다 추가되어 생성됩니다.
// @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) // + 추상 클래스로 생성 부모 테이블이 생성되지 않습니다.
@DiscriminatorColumn // 테이블 생성 시 DTYPE 컬럼 생성(엔티티 명이 들어감)
public class Item{

    @Id @GeneratedValue
    private Long id;

    private String name;
    private int price;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
