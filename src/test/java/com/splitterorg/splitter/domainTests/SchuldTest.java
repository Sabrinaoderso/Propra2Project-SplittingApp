package com.splitterorg.splitter.domainTests;

import static org.assertj.core.api.Assertions.assertThat;

import com.splitterorg.splitter.domain.Benutzer;
import com.splitterorg.splitter.domain.Schuld;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

@TestMethodOrder(MethodOrderer.Random.class)
@SpringBootTest
public class SchuldTest {
  @Test
  @DisplayName("Benutzername ist nicht an Schuld beteiligt")
  public void testBeteiligungAnSchuld() {
    Schuld testSchuld = new Schuld(
      new Benutzer("X"),
      new Benutzer("Y"),
      Money.of(1,"EUR")
    );

    assertThat(!testSchuld.istBeteiligt("zuTesten"));
  }

  @Test
  @DisplayName("Benutzername ist an Schuld beteiligt (als Geber)")
  public void testBeteiligungAnSchuldGeber() {
    String benutzername = "zuTesten";
    Schuld testSchuld = new Schuld(
      new Benutzer(benutzername),
      new Benutzer("Y"),
      Money.of(1,"EUR")
    );

    assertThat(testSchuld.istBeteiligt("zuTesten"));
  }

  @Test
  @DisplayName("Benutzername ist an AusgabeTabelle beteiligt (als Erhalter)")
  public void testBeteiligungAnSchuldErhalter() {
    String benutzername = "zuTesten";
    Schuld testSchuld = new Schuld(
      new Benutzer("X"),
      new Benutzer(benutzername),
      Money.of(1,"EUR")
    );

    assertThat(testSchuld.istBeteiligt("zuTesten"));
  }
}
