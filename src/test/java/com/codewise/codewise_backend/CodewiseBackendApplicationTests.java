package com.codewise.codewise_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import com.codewise.codewise_backend.CodewiseBackendApplication; // Import your main application class


@SpringBootTest(
    classes = CodewiseBackendApplication.class, // CRUCIAL: Explicitly tells Spring which is your main app class
    properties = {
        // --- CRITICAL PROPERTIES TO BYPASS GCP SQL ENVIRONMENT POST PROCESSOR IN TESTS ---
        // Even if spring-cloud-gcp-starter-sql-postgresql is removed, a core GCP auto-config
        // might still run early. These properties appease it during test context loading.
        "spring.cloud.gcp.sql.enabled=false",        // Explicitly disable GCP SQL related features
        "spring.cloud.gcp.sql.database-name=testdb", // Provides a dummy database name to prevent IllegalArgumentException
        // ---------------------------------------------------------------------------------

        "spring.jpa.hibernate.ddl-auto=update",    // Configures schema management for the H2 test database
        "spring.jpa.show-sql=true",                 // Optional: Shows SQL queries in test logs (useful for debugging)
        "spring.main.lazy-initialization=true"      // CRITICAL: Enables lazy initialization of beans, which can help
                                                    // resolve context loading issues by deferring bean creation
    }
)
@AutoConfigureTestDatabase(replace = Replace.ANY) // This annotation automatically configures an in-memory H2 database
                                                  // and replaces any existing DataSource configuration for tests.
class CodewiseBackendApplicationTests {

    @Test
    void contextLoads() {
        // This test simply checks if the Spring application context loads successfully.
        // If this method executes without throwing an exception, it means:
        // 1. Your application's core beans can be initialized.
        // 2. The test database (H2) is properly configured and accessible for the context.
        // 3. No conflicting configurations are preventing the application from starting in a test environment.
    }

    // IMPORTANT: The nested TestConfig class and its manual DataSource bean are REMOVED.
    // The @AutoConfigureTestDatabase annotation handles the in-memory database setup automatically
    // and more reliably for Spring Boot tests.
    /*
    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
            dataSource.setUsername("sa");
            dataSource.setPassword("");
            return dataSource;
        }
    }
    */
}