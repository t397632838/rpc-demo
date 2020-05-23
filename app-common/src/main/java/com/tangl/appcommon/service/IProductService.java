package com.tangl.appcommon.service;

import com.tangl.appcommon.vo.Product;

public interface IProductService {

    void save(Product product);

    void deleteById(Long id);

    void update(Product product);

    Product getProduct(Long id);
}
