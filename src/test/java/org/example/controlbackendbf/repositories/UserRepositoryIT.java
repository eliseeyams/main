package org.example.controlbackendbf.repositories;

import org.example.controlbackendbf.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryIT {

    @Container
     static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // wichtig, damit JPA kein Schema selbst erzeugt (wenn du Liquibase nutzt)
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.liquibase.enabled", () -> "true");
    }

    @Autowired
    public UserRepository userRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldSaveUser_andFindByExist() {

        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("email@aol.com");
//        userRepository.save(new UserEntity() {{
//            setUsername("testuser");
//            setEmail("email@aol.com");}});

        userRepository.save(user);


        boolean existst = userRepository.existsByUsername("testuser");
        assertThat(existst).isTrue();
    }


}
