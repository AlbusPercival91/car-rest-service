package ua.foxminded.car.microservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/welcome")
    public String openEndPoint() {
        return "Open end point";
    }

    @GetMapping("/secured")
    public String securedEndPoint() {
        return "Secured end point";
    }
}
