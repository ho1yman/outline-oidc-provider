package com.oidc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class PasswordService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean verify(String password, String secretDataJson, String credentialDataJson) {
        try {
            JsonNode secretData = objectMapper.readTree(secretDataJson);
            JsonNode credentialData = objectMapper.readTree(credentialDataJson);

            String hashBase64 = secretData.get("value").asText();
            String saltBase64 = secretData.get("salt").asText();

            String algorithm = credentialData.get("algorithm").asText();
            if (!"argon2".equals(algorithm)) {
                // Try PBKDF2 fallback
                return verifyPbkdf2(password, secretData, credentialData);
            }

            JsonNode params = credentialData.get("additionalParameters");

            byte[] salt = Base64.getDecoder().decode(saltBase64);
            byte[] storedHash = Base64.getDecoder().decode(hashBase64);
            int iterations = credentialData.get("hashIterations").asInt();
            int memory = Integer.parseInt(params.get("memory").get(0).asText());
            int parallelism = Integer.parseInt(params.get("parallelism").get(0).asText());
            int hashLength = Integer.parseInt(params.get("hashLength").get(0).asText());
            String type = params.get("type").get(0).asText();
            String version = params.get("version").get(0).asText();

            int argon2Type;
            if ("id".equals(type)) {
                argon2Type = Argon2Parameters.ARGON2_id;
            } else if ("i".equals(type)) {
                argon2Type = Argon2Parameters.ARGON2_i;
            } else {
                argon2Type = Argon2Parameters.ARGON2_d;
            }

            int argon2Version;
            if ("1.3".equals(version)) {
                argon2Version = Argon2Parameters.ARGON2_VERSION_13;
            } else {
                argon2Version = Argon2Parameters.ARGON2_VERSION_10;
            }

            Argon2Parameters.Builder builder = new Argon2Parameters.Builder(argon2Type)
                    .withSalt(salt)
                    .withParallelism(parallelism)
                    .withMemoryAsKB(memory)
                    .withIterations(iterations)
                    .withVersion(argon2Version);

            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(builder.build());

            byte[] computedHash = new byte[hashLength];
            generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), computedHash);

            return MessageDigest.isEqual(storedHash, computedHash);

        } catch (Exception e) {
            return false;
        }
    }

    private boolean verifyPbkdf2(String password, JsonNode secretData, JsonNode credentialData) throws Exception {
        // PBKDF2 fallback (older Keycloak versions)
        String hashBase64 = secretData.get("value").asText();
        String saltBase64 = secretData.get("salt").asText();
        int iterations = credentialData.get("hashIterations").asInt();

        byte[] salt = Base64.getDecoder().decode(saltBase64);
        byte[] storedHash = Base64.getDecoder().decode(hashBase64);

        javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
                password.toCharArray(), salt, iterations, storedHash.length * 8);
        byte[] computedHash = factory.generateSecret(spec).getEncoded();

        return MessageDigest.isEqual(storedHash, computedHash);
    }
}
