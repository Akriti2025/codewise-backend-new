    package com.codewise.codewise_backend;

    import com.codewise.codewise_backend.jwt.JwtProperties;
    import com.codewise.codewise_backend.jwt.JwtService;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.web.client.RestTemplate; // Correct and only one import

    @Configuration
    public class ApplicationConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public JwtProperties jwtProperties() {
            return new JwtProperties();
        }

        @Bean
        public JwtService jwtService(JwtProperties jwtProperties) {
            return new JwtService(jwtProperties);
        }

        @Bean // This method is crucial for providing the RestTemplate bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
    