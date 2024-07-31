package com.splitterorg.splitter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@TestMethodOrder(MethodOrderer.Random.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SplitterApplicationTests {

    @Autowired
    private SplitterApplication splitterApplication;

    @Test
    @DisplayName("Teste, ob der Server erreichbar ist")
    void contextLoads() {
        //ResponseEntity<String> response = this.splitterApplication
    }

}
