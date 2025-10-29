package com.wjc.codetest.product.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
/*
 1. 문제 : DTO 클래스에 @Setter를 사용하는 것이 불필요한 설계로 판단됨

 2. 원인 : 일반적으로 DTO는 계층과 계층을 넘나들며 데이터를 전달하는 역할을 수행함.
    가지고 있던 데이터를 내부적으로 수정하는 것은 DTO이 역할과 거리가 멀다고 판단이 됨.

 3. 개선안 : @Setter를 제거하고 클래스에서 레코드 형태로 변경하는 것이 낫다고 판단됨.
    레코드 형태로 전환할 경우, 코드도 간략해짐과 더불어 @Getter 또한 제거하는 효과를 얻을 수 있음.
 */

/**
 1. 문제 : ProductController가  CreateProductRequest를 통해 값을 매핑받지 못하고 있음

 2. 원인 : 클래스 내부에 기본생성자가 정의되어있지 않아 스프링 측에서 객체를 생성하지 못하게 됨.

 3. 개선안 : @NoArgsConstructor 어노테이션 또는 자체적으로 기본생성자를 정의하는 것으로 이를 해결할 수 있음.
 */
public class CreateProductRequest {
    private String category;
    private String name;

    public CreateProductRequest(String category) {
        this.category = category;
    }

    public CreateProductRequest(String category, String name) {
        this.category = category;
        this.name = name;
    }
}

