package org.example.mediaconnect.service;

import org.example.mediaconnect.model.Helloworld;
import org.example.mediaconnect.repository.HelloworldRepository;
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
