package org.example.mediaconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MediaConnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(MediaConnectApplication.class, args);
	}

    @GetMapping
    public String helloWorld() {
        return "Hello World";
    }

}
