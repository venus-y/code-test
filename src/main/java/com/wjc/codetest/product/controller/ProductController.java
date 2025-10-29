package com.wjc.codetest.product.controller;

import com.wjc.codetest.product.model.request.CreateProductRequest;
import com.wjc.codetest.product.model.request.GetProductListRequest;
import com.wjc.codetest.product.model.domain.Product;
import com.wjc.codetest.product.model.request.UpdateProductRequest;
import com.wjc.codetest.product.model.response.ProductListResponse;
import com.wjc.codetest.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
/**
 문제 : ProductController에 정의된 메서드들이 Restful하게 정의되어 있지 않음

 원인 : 메서드들의 내부에 get, delete와 같은 행위들이 정의되어 있고, 모든 메서드들이 @GetMapping과 @PostMapping으로만 정의되어 있음

 개선안 : RequestMapping 내부에 "/products"/와 같은 형태로 공통적으로 사용하는 부분을 정의함으로써 간소화,
 메서드가 수행하는 역할에 따라 적절한 매핑 어노테이션을 사용하는 것 -> ex) 서버에 데이터를 새로 생성 @PostMapping, 데이터를 삭제 @DeleteMapping
 매핑 어노테이션을 통해 메서드가 수행하는 역할을 나타내므로, url 상에 존재하는 동작과 관련된 예를 들어 'get', 'update'와 같은 표현은 제거하는 것이 바람직할 것으로 보임.
 */
@RequestMapping
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping(value = "/get/product/by/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable(name = "productId") Long productId){
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @PostMapping(value = "/create/product")
/**
 문제 : 클라이언트로부터 받아온 DTO들에 대한 검증이 이루어지지 않고 있음

 원인 : dto 내부에 값을 검증할 때 사용되는 @NotNull, @NotBlank와 같은 어노테이션이 정의되지 않았고, 컨트롤러 측에서는 @Valid, @Validated와 같은 어노테이션을 정의하지 않았기 때문임

 개선안 : dto 내부에 검증이 필요한 필드에 대해 적절한 검증 어노테이션을 적용하고, 컨트롤러 측에서 받아오는 dto의 경우 @Valid, @Validated를 적용하는 것
 컨트롤러의 주요 역할은 데이터를 받아온 후 가공하여 서비스단으로 넘겨주는 것이기 때문에 위와 같은 설계가 적용되는 겅시 바람직할 것으로 보임.
*/
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest dto){
        Product product = productService.create(dto);
        return ResponseEntity.ok(product);
    }
/**
 문제 : 서버에서 클라이언트 측으로 요청받은 데이터를 전송할 때 불필요한 데이터까지 모두 전달되거나, 직렬화 과정에서 Json 파싱 오류가 발생할 수 있음

 원인 : 컨트롤러 측에서 엔티티를 즉시 반환하기 떄문에 발생하는 문제임. JPA는 엔티티 내부에서 다른 엔티티와 연관관계를 맺고 있을 경우, @FetchType 전략에 따라
 연관된 엔티티를 즉시 로딩, 또는 지연 로딩하게 됨. 지연 로딩 전략을 택했을 경우 해당 엔티티는 연관된 엔티티를 프록시 객체의 형태로 가지고 있게 됨.
 프록시 객체에 대한 실제 접근이 이루어지지 않은 상태에서 엔티티를 그대로 클라이언트 측으로 반환하게 된다면 Json 직렬화 과정에서 오류가 발생하게 됨.
 추가적으로 실제 클라이언트측에서 필요한 데이터는 한정적일 수 있을것으로 생각됨. 따라서 엔티티를 그대로 반환하는 형태보다는 필요한 데이터만을 추출하여
 반환할 수 있도록 별도의 DTO 클래스를 정의하는 것이 바람직할 것으로 보임.

 개선안 : 엔티티를 즉시 반환하는 형태에서 필요한 값만을 추출하여 반환할 수 있도록 DTO를 정의하고 이를 클라이언트 측에 넘겨주는 것
 */
    @PostMapping(value = "/delete/product/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable(name = "productId") Long productId){
        productService.deleteById(productId);
        return ResponseEntity.ok(true);
    }

    @PostMapping(value = "/update/product")
    public ResponseEntity<Product> updateProduct(@RequestBody UpdateProductRequest dto){
        Product product = productService.update(dto);
        return ResponseEntity.ok(product);
    }

    /**
     문제 : getProductListByCategory 메서드의 불필요한 오버로딩으로 인한 가독성 저하

     원인 : 각각 용도가 다른 메서드를 동일한 이름으로 오버로딩하였기 때문에 발생하는 문제임

     개선안 : 메서드의 목적에 맞게 적절한 이름을 각각의 메서드에 붙이는 것
     */
    @PostMapping(value = "/product/list")
    public ResponseEntity<ProductListResponse> getProductListByCategory(@RequestBody GetProductListRequest dto){
        Page<Product> productList = productService.getListByCategory(dto);
        return ResponseEntity.ok(new ProductListResponse(productList.getContent(), productList.getTotalPages(), productList.getTotalElements(), productList.getNumber()));
    }

    @GetMapping(value = "/product/category/list")
    public ResponseEntity<List<String>> getProductListByCategory(){
        List<String> uniqueCategories = productService.getUniqueCategories();
        return ResponseEntity.ok(uniqueCategories);
    }
}