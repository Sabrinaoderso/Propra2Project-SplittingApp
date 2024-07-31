package com.splitterorg.splitter.serviceTests;

import static org.assertj.core.api.Assertions.assertThat;

import com.splitterorg.splitter.domain.Ausgabe;
import com.splitterorg.splitter.domain.Benutzer;
import com.splitterorg.splitter.domain.gruppe.Gruppe;
import com.splitterorg.splitter.persistence.AusgabeServicePersistence;
import com.splitterorg.splitter.persistence.GruppenServicePersistence;
import java.util.List;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@TestMethodOrder(MethodOrderer.Random.class)
@SpringBootTest
public class AusgabeServicePersistenceTests {

    @Autowired
    GruppenServicePersistence gruppeservice;
    @Autowired
    AusgabeServicePersistence ausgabeservice;


    @Test
    @DisplayName("Teste Ausgabeservice funktion: neue AusgabeTabelle erstellen")
    public void Test01(){

      //arrange
      Benutzer geldgeber = new Benutzer("Hallo1");
      List<Benutzer> gelderhalterList = List.of(new Benutzer("Hallo2"),new Benutzer("Hallo3"));
      String zweck = "Fahrt";
      Money betrag = Money.of(40,"EUR");
      Ausgabe ausgabe1 = new Ausgabe(geldgeber, gelderhalterList ,zweck,betrag );

      //act
      Ausgabe ausgabe2 = ausgabeservice.erstelleAusgabe(geldgeber,gelderhalterList,betrag,zweck);

      //assert
      assertThat(ausgabe1.getBetrag()).isEqualTo(ausgabe2.getBetrag());
      assertThat(ausgabe1.getZweck()).isEqualTo(ausgabe2.getZweck());
      assertThat(ausgabe1.getGeldgeberIn()).isEqualTo(ausgabe2.getGeldgeberIn());
      assertThat(ausgabe1.getGelderhalterInnen()).isEqualTo(ausgabe2.getGelderhalterInnen());

    }

    @Test
    @DisplayName("Teste Ausgabeservice funktion: füge AusgabeTabelle zu Gruppe hinzu")
    public void Test02(){

      //arrange
      Benutzer benutzer1 = new Benutzer("Hallo1");
      Ausgabe ausgabe1 = new Ausgabe(benutzer1, List.of(new Benutzer("Hallo2"),new Benutzer("Hallo3")),"Fahrt", Money.of(40,"EUR"));
      Gruppe gruppe1 = new Gruppe(benutzer1,"gruppe1");
      gruppeservice.addGruppe(gruppe1);

      //act
      ausgabeservice.addAusgabe2Gruppe(ausgabe1,gruppe1.getId());

      //assert
      assertThat(gruppeservice.getGruppeById(gruppe1.getId()).getAusgabenliste()).contains(ausgabe1);
    }

    @Test
    @DisplayName("BenutzerListe wird aus einem String erstellt.")
    public void testeNutzerListeAusString(){
      //arrange
      String benutzerListeInString = "[Tim,Till,Timon,Ute,Ben]";
      Benutzer tim = new Benutzer("Tim");
      Benutzer till = new Benutzer("Till");
      Benutzer timon = new Benutzer("Timon");
      Benutzer ute = new Benutzer("Ute");
      Benutzer ben = new Benutzer("Ben");
      List<Benutzer> erwarteteBenutzerliste = List.of(tim,till,timon,ute,ben);

      //act
      List<Benutzer> ausgabe = ausgabeservice.nutzerListeAusString(benutzerListeInString);

      //assert
      assertThat(ausgabe).containsExactlyInAnyOrderElementsOf(erwarteteBenutzerliste);
    }
}
