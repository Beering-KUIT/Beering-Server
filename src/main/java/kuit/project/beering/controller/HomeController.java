package kuit.project.beering.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping
    public String hello() {
        return "hello world";
    }

    @GetMapping("/test")
    public String authTest() {
        return "인가 성공";
    }
}
