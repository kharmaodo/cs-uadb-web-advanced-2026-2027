package sn.uadb.repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ApiKeyRepository {

    private final JdbcClient jdbc;

    public ApiKeyRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** Retourne le propriétaire de la clé si elle existe et est active. */
    public Optional<String> findOwnerByKey(String rawKey) {
        return jdbc.sql("SELECT owner FROM api_key WHERE key_hash = :h AND active = 1")
                .param("h", hash(rawKey))
                .query(String.class)
                .optional();
    }

    public void insertIfAbsent(String rawKey, String owner) {
        jdbc.sql("INSERT OR IGNORE INTO api_key (key_hash, owner) VALUES (:h, :o)")
                .param("h", hash(rawKey))
                .param("o", owner)
                .update();
    }

    static String hash(String raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}