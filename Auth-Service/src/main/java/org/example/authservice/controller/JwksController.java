package org.example.authservice.controller;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/.well-known")
@RequiredArgsConstructor
public class JwksController {

    private final RSAPublicKey publicKey;

    @GetMapping("/jwks.json")
    public Map getJwks() {
        RSAKey jwk = new RSAKey.Builder(publicKey)
                .keyID("1")
                .algorithm(JWSAlgorithm.RS256)
                .keyUse(KeyUse.SIGNATURE)
                .build();

        return Map.of("keys", List.of(jwk.toJSONObject()));
    }
}
