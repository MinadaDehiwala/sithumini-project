package com.aurabloom.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private Mail mail = new Mail();
    private Bootstrap bootstrap = new Bootstrap();

    @Getter
    @Setter
    public static class Jwt {
        private String secret = "replace-with-a-secure-256-bit-secret-key";
        private long accessTokenMinutes = 60;
        private long refreshTokenDays = 30;
    }

    @Getter
    @Setter
    public static class Cors {
        private List<String> allowedOrigins = new ArrayList<>(List.of("http://localhost:5173"));
    }

    @Getter
    @Setter
    public static class Mail {
        private boolean enabled;
    }

    @Getter
    @Setter
    public static class Bootstrap {
        private boolean createDefaultAdmin = true;
        private String adminEmail = "admin@aurabloom.local";
        private String adminPassword = "Admin123!";
        private String adminName = "AuraBloom Admin";
    }
}
