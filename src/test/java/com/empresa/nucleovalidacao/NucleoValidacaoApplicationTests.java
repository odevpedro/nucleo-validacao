package com.empresa.nucleovalidacao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:oracle:thin:@localhost:1521/FREEPDB1",
    "spring.datasource.username=APP_VALIDACAO",
    "spring.datasource.password=app123",
    "spring.datasource.driver-class-name=oracle.jdbc.OracleDriver",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect"
})
class NucleoValidacaoApplicationTests {

    @Test
    void contextLoads() {
    }
}
