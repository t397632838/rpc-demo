package com.tangl.appclient;

import com.tangl.appcommon.service.IProductService;
import com.tangl.appcommon.vo.Product;
import com.tangl.rpcclient.RpcProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@RestController
public class ProductController {
    @Autowired
    private RpcProxy proxy;

    private IProductService productService;


    @PostConstruct
    public void init() {
        productService = proxy.getInstance(IProductService.class);
    }

    public void testSave() throws Exception {
        productService.save(new Product(2L, "002", "内衣", BigDecimal.TEN));
    }

    public void testDelete() throws Exception {
        productService.deleteById(2L);
    }

    public void testUpdate() throws Exception {
        productService.update(new Product(2L, "002", "内衣", BigDecimal.ONE));
    }

    public void testGet() throws Exception {
        Product product = productService.getProduct(1L);
        System.out.println("获取到的产品信息为:" + product);
    }
}
