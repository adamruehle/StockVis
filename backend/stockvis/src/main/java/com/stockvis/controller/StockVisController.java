package com.stockvis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * StockVisController has al
 */
@RestController
@RequestMapping("/api")
public class StockVisController {

    // Put APIs Here follow this design
    @GetMapping(value = "/hello")
    public String hello() {
        return "Hello World!";
    }
}
