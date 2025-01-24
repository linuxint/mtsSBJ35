package com.devkbil.mtssbj;

import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Validated
@RestController
public class ErrorRestControllerTest {

    @GetMapping("/products")
    public ResponseEntity<Void> search(
            @Min(1) @RequestParam(value="page") int page,
            @Min(1) @Max(100) @RequestParam(value="size") int size,
            @Range(min = 1, max = 10) @RequestParam(value="keyword") String keyword) {
        // page는 1보다 크고 size는 1~100 사이
        // keyword는 글자수 1~10 사이
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products/{productNo}")
    public ResponseEntity<Void> getProduct(
            @Min(1) @PathVariable("productNo") int productNo
    ) {
        // productNo는 최소 1이상
        return ResponseEntity.noContent().build();
    }
}
