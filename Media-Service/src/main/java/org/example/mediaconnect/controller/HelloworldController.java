package org.example.mediaconnect.controller;

import org.example.mediaconnect.model.Helloworld;
import org.example.mediaconnect.service.HelloworldService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
