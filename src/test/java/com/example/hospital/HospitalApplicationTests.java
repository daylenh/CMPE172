package com.example.hospital;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.schema-initializer.enabled=false")
class HospitalApplicationTests {

    @Test
    void contextLoads() {
    }

}
