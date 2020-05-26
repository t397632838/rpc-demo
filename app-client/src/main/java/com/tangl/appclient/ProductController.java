package com.tangl.appclient;

import com.tangl.appcommon.service.IProductService;
import com.tangl.appcommon.vo.Product;
import com.tangl.rpcclient.RpcProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("testSave")
    public void testSave() throws Exception {
        productService.save(new Product(2L, "002", "内衣", BigDecimal.TEN));
    }

    @GetMapping("testDelete")
    public void testDelete() throws Exception {
        productService.deleteById(2L);
    }

    @GetMapping("testUpdate")
    public void testUpdate() throws Exception {
        productService.update(new Product(2L, "002", "内衣", BigDecimal.ONE));
    }

    @GetMapping("testGet")
    public void testGet() throws Exception {
        Product product = productService.getProduct(1L);
        System.out.println("获取到的产品信息为:" + product);
    }
}
