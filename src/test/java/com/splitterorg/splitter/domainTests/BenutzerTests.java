package com.splitterorg.splitter.domainTests;

import com.splitterorg.splitter.domain.Benutzer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

@TestMethodOrder(MethodOrderer.Random.class)
@SpringBootTest
class BenutzerTests {

    @Test
    @DisplayName("Teste benutzer kann erzeugt werden")
    public void testBenutzerKannErzeugtWerden() {
        // arrange
        // =======
        //
        // erstelle Benutzer
        Benutzer benutzer = new Benutzer("Sabrina");

        // act
        // ===
        //
        // nichts

        // assert
        // ======
        //
        assert benutzer != null;
    }

    @Test
    @DisplayName("Teste ob getName klappt")
    public void getnameKlappt(){
        // arrange
        // =======
        //
        // erstelle Benutzer
        Benutzer benutzer = new Benutzer("Diter");

        // act
        // ===
        //
        // nichts

        // assert
        // ======
        //
        assert Objects.equals(benutzer.getBenutzername(), "Diter");
    }
}
