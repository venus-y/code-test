package com.wjc.codetest.product.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
/**
 문제 : 엔티티가 가지고 있는 데이터들이 여러 곳에서 수정될 수 있는 문제가 있음.

 원인 : 엔티티 내부에서 @Setter를 사용하고 있기 때문임. 엔티티에 대한 값의 변경은
 신중히 이루어져야 하고, 무분별한 수정은 방죄되는 것이 바람직함.

 개선안 : @Setter를 제거하고, 값의 수정이 필요한 필드에 대해서는 updateField()와 같은
 형태의 메서드를 별도로 정의하여 사용하는 것이 낫다고 생각됨.
 *
 */

public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "category")
    private String category;

    @Column(name = "name")
    private String name;

    protected Product() {
    }

    public Product(String category, String name) {
        this.category = category;
        this.name = name;
    }

    /**
     문제 : 카테고리와 이름에 대한 get메서드가 불필요하게 정의되어 있음.

     원인 : 클래스 내부에 @Getter를 사용하여, 내부적으로 get메서드가 정의되어있는 상황에서, 추가적으로
     get메서드를 정의하였기 때문임.

     개선안 : 별도로 정의한 get메서드는 제거하고, @Getter를 통해 필드에 접근하는 것이 바람직할 것으로 판단됨.
     */
    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }
}
