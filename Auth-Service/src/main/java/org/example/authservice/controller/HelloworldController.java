package org.example.authservice.controller;
//test
import org.example.authservice.model.Helloworld;
import org.example.authservice.service.HelloworldService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
//test message!
@RestController
@RequestMapping("api/helloworld")
public class HelloworldController {

    private final HelloworldService helloworldService;

    public HelloworldController(HelloworldService helloworldService) {
        this.helloworldService = helloworldService;
    }

    @GetMapping
    public List<Helloworld> getAllHelloworld() {
        return helloworldService.getAllHelloworld();
    }

}
