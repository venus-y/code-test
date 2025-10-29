package com.wjc.codetest.product.service;

import com.wjc.codetest.product.model.request.CreateProductRequest;
import com.wjc.codetest.product.model.request.GetProductListRequest;
import com.wjc.codetest.product.model.domain.Product;
import com.wjc.codetest.product.model.request.UpdateProductRequest;
import com.wjc.codetest.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
/**
 문제 : 로깅을 하지 않고 있는 서비스 클래스에 @Slf4j 어노테이션이 적용되어 있음

 원인 : 클래스 내에 로깅을 수행하는 부분이 없는데, 불필요하게 어노테이션을 붙이는 것은 가독성 저하로 이어짐

 개선안 : 사용목적이 불분명한 어노테이션을 제거하는 것이 바람직할 것으로 생각됨.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product create(CreateProductRequest dto) {
        Product product = new Product(dto.getCategory(), dto.getName());
        return productRepository.save(product);
    }

/**
 문제 : 불필요한 Optional 객체 생성으로 코드라인 증가

 원인 : productRepository로부터 조회해온 객체를 Optinoal로 받은 후, 다시 if문을 통해 검증하는 부분이 추가됨으로써 코드라인 증가로 가독성을 저하시킴

 개선안 : productRepository.findById(productId)에서 메서드 체이닝으로 orElseThrow()를 호출하고
 데이터가 없을 경우 "product not found"를 메시지에 담아 예외를 발생
*/
    public Product getProductById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            throw new RuntimeException("product not found");
        }
        return productOptional.get();
    }

    public Product update(UpdateProductRequest dto) {
        Product product = getProductById(dto.getId());
        product.setCategory(dto.getCategory());
        product.setName(dto.getName());
        Product updatedProduct = productRepository.save(product);
        return updatedProduct;

    }

    public void deleteById(Long productId) {
        Product product = getProductById(productId);
        productRepository.delete(product);
    }

    public Page<Product> getListByCategory(GetProductListRequest dto) {
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Sort.Direction.ASC, "category"));
        return productRepository.findAllByCategory(dto.getCategory(), pageRequest);
    }

/**
 문제 : getUniqueCategories 메서드의 불필요한 정의

 원인 : 메서드 내부에 비즈니스 로직이 전혀 존재하지 않은 상태에서, DB에 접근하는 로직만이 있음.
 특별한 비즈니스 로직이 존재하지 않는 메서드를 정의한 불필요한 설계로 보임

 개선안 : getUniqueCategories를 제거하고 컨트롤러에서 DB를 직접 호출하는 식으로 간소화
*/
    public List<String> getUniqueCategories() {
        return productRepository.findDistinctCategories();
    }
}