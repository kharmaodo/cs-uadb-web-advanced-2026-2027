package org.openapitools.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/hello")
@Tag(name = "Hello", description = "Endpoint de test")
public class HelloController {

    @GetMapping
    @Operation(summary = "Retourne un message de bienvenue")
    public Map<String, String> hello() {
        return Map.of("message", "Hello from backend");
    }
}