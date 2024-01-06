package com.service.inspection.utils;

import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

public class VaultConfigReaderUtil {
    public static final String TOKEN;
    public static final String URI = "http://127.0.0.1:8200";
    public static final String VAULT_PATH = "kv/base";

    static {
        // Путь с файлом, содержащим токен для общения с vault
        Path rootTokenPath = Paths.get("").toAbsolutePath().resolve("config\\root_token.txt");
        try {
            TOKEN = Files.readString(Paths.get(rootTokenPath.toString())).trim();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private VaultConfigReaderUtil() {
    }

    public static Properties read() {
        VaultEndpoint endpoint;
        Properties properties = new Properties();

        try {
            endpoint = VaultEndpoint.from(new URI(URI));
            VaultTemplate vaultTemplate = new VaultTemplate(endpoint, new TokenAuthentication(TOKEN));
            VaultResponse response = vaultTemplate.read(VAULT_PATH);
            Map<String, Object> mapData = response.getRequiredData();
            properties.putAll(mapData);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return properties;
    }
}
