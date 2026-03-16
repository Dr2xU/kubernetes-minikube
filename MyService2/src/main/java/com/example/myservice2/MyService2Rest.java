package com.example.myservice2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyService2Rest {

    @GetMapping("/")
    public String sayHello(){
        return "Hello from MyService2";
    }

}
