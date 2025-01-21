package com.example.literatura.literalura;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConsumoAPI {
    private final RestTemplate restTemplate = new RestTemplate();

    public String obtenerDatos(String url) {
        return restTemplate.getForObject(url, String.class);
    }
}
