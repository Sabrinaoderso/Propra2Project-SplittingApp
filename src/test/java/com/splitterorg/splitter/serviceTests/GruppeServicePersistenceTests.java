package com.splitterorg.splitter.serviceTests;

import static org.assertj.core.api.Assertions.assertThat;

import com.splitterorg.splitter.domain.Benutzer;
import com.splitterorg.splitter.domain.gruppe.Gruppe;
import com.splitterorg.splitter.persistence.GruppenServicePersistence;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@TestMethodOrder(MethodOrderer.Random.class)
public class GruppeServicePersistenceTests {

    @Autowired
    GruppenServicePersistence service;

    @Test
    @Disabled
    @DisplayName("Testet das erstelleGruppe eine Gruppe erzeugt")
    // GruppenIDs stimmen nicht überein (sollen sie auch nicht)
    public void erstelleGruppeErzeugtEineGruppeTest(){

      //arrange
      Benutzer benutzer1 = new Benutzer("Gustav");
      Gruppe gruppe1 = new Gruppe(benutzer1,"gruppe1");
      //gruppe1.teilnehmerHinzfuegen(benutzer1);

      //act
      Gruppe gruppe2 = new Gruppe(benutzer1, "gruppe1");

      //assert
      assertThat(gruppe1).isEqualTo(gruppe2);

    }
    @Test
    @DisplayName("Teste das addGruppe eine Gruppe zum Set hinzufügt")
    public void addGruppeFuegtEineGruppeHinzu(){

      //arrange
      Benutzer benutzer1 = new Benutzer("Gustav");
      Gruppe gruppe1 = new Gruppe(benutzer1,"gruppe1");

      //act
      service.addGruppe(gruppe1);

      //assert
      assertThat(service.getGruppenSetVonBenutzer(benutzer1)).contains(gruppe1);
    }
    @Test
    @DisplayName("Teste getGruppenSetVonBenutzer gibt alle Gruppen zurück in denen der Benutzer Teilnehmer ist")
    public void getGruppenSetVonBenutzerGibtAlleGruppeDesBenutzersZurueck(){

      //arrange
      Benutzer benutzer1 = new Benutzer("Gustav");
      Gruppe gruppe1 = new Gruppe(benutzer1,"gruppe1");
      Gruppe gruppe2 = new Gruppe(benutzer1,"gruppe2");
      Gruppe gruppe3 = new Gruppe(benutzer1,"gruppe3");
      gruppe1.teilnehmerHinzfuegen(benutzer1);
      gruppe2.teilnehmerHinzfuegen(benutzer1);

      //act
      service.addGruppe(gruppe1);
      service.addGruppe(gruppe2);
      service.addGruppe(gruppe3);

      //assert
      assertThat(service.getGruppenSetVonBenutzer(benutzer1)).contains(gruppe1,gruppe2);
    }

  }
