package com.tangl.appcommon.vo;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Product {

    private Long id;
    // 编号
    private String sn;
    // 产品名称
    private String name;
    // 产品价格
    private BigDecimal price;
}
