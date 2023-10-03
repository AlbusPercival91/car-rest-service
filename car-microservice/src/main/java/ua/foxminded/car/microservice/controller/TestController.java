package ua.foxminded.car.microservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class TestController {

    @GetMapping("v1/welcome")
    public String openEndPoint() {
        return "Open end point";
    }

    @GetMapping("secured")
    public String securedEndPoint() {
        return "Secured end point";
    }
}
