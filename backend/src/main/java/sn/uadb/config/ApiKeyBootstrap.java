package sn.uadb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import sn.uadb.repository.ApiKeyRepository;

@Component
public class ApiKeyBootstrap implements ApplicationRunner {

    private final ApiKeyRepository repository;
    private final String bootstrapKey;

    public ApiKeyBootstrap(ApiKeyRepository repository,
                           @Value("${app.security.bootstrap-key:}") String bootstrapKey) {
        this.repository = repository;
        this.bootstrapKey = bootstrapKey;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!bootstrapKey.isBlank()) {
            repository.insertIfAbsent(bootstrapKey, "bootstrap");
        }
    }
}