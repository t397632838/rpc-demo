package com.tangl.appcommon.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private Long id;
    // 编号
    private String sn;
    // 产品名称
    private String name;
    // 产品价格
    private BigDecimal price;
}
