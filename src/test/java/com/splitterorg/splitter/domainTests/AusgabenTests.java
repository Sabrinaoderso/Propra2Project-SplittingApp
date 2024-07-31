package com.splitterorg.splitter.domainTests;

import com.splitterorg.splitter.domain.Ausgabe;
import com.splitterorg.splitter.domain.Benutzer;
import java.util.List;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@TestMethodOrder(MethodOrderer.Random.class)
@SpringBootTest
class AusgabenTests{

    @Test
    @DisplayName("Benutzername ist nicht an AusgabeTabelle beteiligt")
    public void testBeteiligungAnAusgabe() {
        Ausgabe testAusgabe = new Ausgabe(
          new Benutzer("X"),
          List.of(new Benutzer("X")),
          "zweck",
          Money.of(1,"EUR")
        );

        assertThat(!testAusgabe.istBeteiligt("zuTesten"));
    }

    @Test
    @DisplayName("Benutzername ist an AusgabeTabelle beteiligt (als Geldgeber)")
    public void testBeteiligungAnAusgabeGeldgeber() {
        String benutzername = "zuTesten";
        Ausgabe testAusgabe = new Ausgabe(
          new Benutzer(benutzername),
          List.of(new Benutzer("X")),
          "zweck",
          Money.of(1,"EUR")
        );

        assertThat(testAusgabe.istBeteiligt("zuTesten"));
    }

    @Test
    @DisplayName("Benutzername ist an AusgabeTabelle beteiligt (als Gelderhalter)")
    public void testBeteiligungAnAusgabeGelderhalter() {
        String benutzername = "zuTesten";
        Ausgabe testAusgabe = new Ausgabe(
          new Benutzer("X"),
          List.of(new Benutzer("X"), new Benutzer(benutzername)),
          "zweck",
          Money.of(1,"EUR")
        );

        assertThat(testAusgabe.istBeteiligt("zuTesten"));
    }
}