package org.example.authservice.service;

import org.example.authservice.model.Helloworld;
import org.example.authservice.repository.HelloworldRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HelloworldService {

    private final HelloworldRepository helloworldRepository;

    public HelloworldService(HelloworldRepository helloworldRepository) {
        this.helloworldRepository = helloworldRepository;
    }

    public List<Helloworld> getAllHelloworld() {
        return helloworldRepository.findAll();
    }


}
