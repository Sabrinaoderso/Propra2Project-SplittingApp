package com.splitterorg.splitter.persistenceTests;


import com.splitterorg.splitter.persistence.*;

import java.util.HashSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
public class GruppenRepositoryTest {

  @Autowired
  GruppenRepository repoGruppe;

  @Test
  @DisplayName("Teste, ob repository gesaved werden kann")
  public void test() {
    // this.repo has the form CrudRepository<Gruppe, UUID>
    // test if it can be saved
    GruppeTabelle gruppe = new GruppeTabelle(
            "testgruppe",
            UUID.randomUUID(),
            false,
            Set.of(),
            new HashSet<>()
    );
    this.repoGruppe.save(gruppe);
  }

  @Test
  @DisplayName("Teste, ob wir mehrere Gruppen im Repository saven können")
  public void mehrereGruppenAufEinmalSaven(){
    GruppeTabelle gruppe1 = new GruppeTabelle("gruppe1", UUID.randomUUID(), true, Set.of(), new HashSet<>());
    GruppeTabelle gruppe2 = new GruppeTabelle("gruppe2", UUID.randomUUID(), false, Set.of(), new HashSet<>());

    this.repoGruppe.saveAll(Set.of(gruppe1,gruppe2));
  }
  @Test
  @DisplayName("Benutzer zu einer Gruppe hinzufuegen")
  public void gruppeSpeichernUndVerknuefungSpeichern(){
    TeilnehmerInGruppe teilnehmer_in_gruppe =
            new TeilnehmerInGruppe(0,"Benutzer1");
    GruppeTabelle gruppe = new GruppeTabelle("gruppe", UUID.randomUUID(), true, Set.of(teilnehmer_in_gruppe), new HashSet<>());
    this.repoGruppe.save(gruppe);
  }

  @Test
  @DisplayName("AusgabeTabelle zu einer Gruppe hinzufuegen")
  public void gruppeSpeichernUndAusgabeSpeichern(){
    GelderhalterInnen gelderhalter = new GelderhalterInnen(0,"Tom");
    AusgabeTabelle ausgabeTabelle = new AusgabeTabelle(0,"Tom",
      Set.of(gelderhalter),
      "Erdbermarmeladebrot mit Honig gekauft",
      200);

    GruppeTabelle gruppe = new GruppeTabelle(
      "gruppe",
      UUID.randomUUID(),
      true,
      Set.of(),
      Set.of(ausgabeTabelle));


    this.repoGruppe.save(gruppe);

    // repoTeilnehmer.save(teilnehmer_in_gruppe);
  }

  @Test
  @DisplayName("Teste, ob mehrere Gruppen im Repository vorhanden sind")
  public void mehrereGruppenAufEinmalSindAbgespeichertUndExistierenDanach(){

    //arrange
    UUID gruppeid1 = UUID.randomUUID();
    UUID gruppeid2 = UUID.randomUUID();
    GruppeTabelle gruppe1 = new GruppeTabelle("gruppe1", gruppeid1, true, Set.of(), new HashSet<>());
    GruppeTabelle gruppe2 = new GruppeTabelle("gruppe2", gruppeid2, false, Set.of(), new HashSet<>());


    //act

    this.repoGruppe.save(gruppe1);
    this.repoGruppe.save(gruppe2);

    boolean ex1 = this.repoGruppe.existsById(gruppeid1);
    boolean ex2 = this.repoGruppe.existsById(gruppeid2);

    // Assert

    assertThat(ex1).isTrue();
    assertThat(ex2).isTrue();
  }

  @Test
  @DisplayName("Teste, ob mehrere Gruppen im Repository vorhanden sind")
  public void wennEineGruppeNichtHinzugefuegtWeurdeIstSieNichtDa(){

    //arrange
    UUID gruppeid1 = UUID.randomUUID();
    UUID gruppeid2 = UUID.randomUUID();
    UUID gruppeid3 = UUID.randomUUID();
    GruppeTabelle gruppe1 = new GruppeTabelle("gruppe1", gruppeid1, true, Set.of(), new HashSet<>());
    GruppeTabelle gruppe2 = new GruppeTabelle("gruppe2", gruppeid2, false, Set.of(), new HashSet<>());


    //act

    this.repoGruppe.save(gruppe1);
    this.repoGruppe.save(gruppe2);

    boolean ex3 = this.repoGruppe.existsById(gruppeid3);

    // Assert

    assertThat(ex3).isFalse();
    //assert
  }

  @Test
  @DisplayName("Teste, ob mehrere Gruppen im Repository vorhanden sind")
  public void mehrereGruppenAufEinmalSindAbgespeichert(){

    //arrange
    UUID gruppeid1 = UUID.randomUUID();
    UUID gruppeid2 = UUID.randomUUID();
    GruppeTabelle gruppe1 = new GruppeTabelle("gruppe1", gruppeid1, true, Set.of(), new HashSet<>());
    GruppeTabelle gruppe2 = new GruppeTabelle("gruppe2", gruppeid2, false, Set.of(), new HashSet<>());


    //act

    this.repoGruppe.save(gruppe1);
    this.repoGruppe.save(gruppe2);

    // Assert
    var gruppe3 = this.repoGruppe.findById(gruppeid1);
    assertThat(gruppe3).isNotEmpty();
    //assertThat(this.repoGruppe.findAll().containsAll(Set.of(gruppe1,gruppe2))).isTrue();
  }

  @Test
  @DisplayName("Teste, ob man eine komplexere Gruppe finden kann")
  public void testFindeKomplexeGruppe(){
    // Arrange
    // =======
    GelderhalterInnen luca = new GelderhalterInnen(0,"Luca");
    AusgabeTabelle ausgabeTabelle = new AusgabeTabelle(
            0,"Tom",
       Set.of(luca),
      "Erdbermarmeladebrot mit Honig gekauft",
      200);

    UUID gruppeid = UUID.randomUUID();
    GruppeTabelle gruppe = new GruppeTabelle(
      "gruppe",
      gruppeid,
      true,
      Set.of(),
      Set.of(ausgabeTabelle));

    // Act
    // ===

    this.repoGruppe.save(gruppe);

    // Assert
    // ======

    assertThat(this.repoGruppe.findById(gruppeid)).contains(gruppe);

  }

  @Test
  @DisplayName("Teste, ob man mehrere komplexe Gruppen finden kann")
  public void testFindeMehrereKomplexeGruppe(){
    // Arrange
    // =======
    GelderhalterInnen luca = new GelderhalterInnen(0,"Luca");
    AusgabeTabelle ausgabeTabelle = new AusgabeTabelle(
      0,"Tom",
      Set.of(luca),
      "Erdbermarmeladebrot mit Honig gekauft",
      200);

    UUID gruppeid = UUID.randomUUID();
    GruppeTabelle gruppe = new GruppeTabelle(
      "gruppe",
      gruppeid,
      true,
      Set.of(),
      Set.of(ausgabeTabelle));
    // GRUPPE 2
    GelderhalterInnen luca2 = new GelderhalterInnen(0,"Luca");
    AusgabeTabelle ausgabeTabelle2 = new AusgabeTabelle(
      0,"Tom",
      Set.of(luca2),
      "Erdbermarmeladebrot mit Honig gekauft",
      200);

    UUID gruppeid2 = UUID.randomUUID();
    GruppeTabelle gruppe2 = new GruppeTabelle(
      "gruppe",
      gruppeid2,
      true,
      Set.of(),
      Set.of(ausgabeTabelle2));

    // Act
    // ===
    this.repoGruppe.save(gruppe);
    this.repoGruppe.save(gruppe2);

    // Assert
    // ======
    assertThat(this.repoGruppe.findAll()).contains(gruppe, gruppe2);

  }

  @Test
  @DisplayName("Teste ob man eine AusgabeTabelle hinzufügen kann ohne, dass die Gruppe doppelt gespeichert wird")
  public void testHinzugefuegteAusgabeSpeichern() {
    GelderhalterInnen luca = new GelderhalterInnen(0,"benutzername");
    AusgabeTabelle ausgabeTabelle = new AusgabeTabelle(0,"Tom",
      Set.of(luca),"Erdbermarmeladebrot mit Honig gekauft",200);
    GruppeTabelle gruppe = new GruppeTabelle(
      "gruppe",
      UUID.randomUUID(),
      true,
      Set.of(),
      new HashSet<>());

    repoGruppe.save(gruppe);

    gruppe.ausgabeHinzufuegen(ausgabeTabelle);
    repoGruppe.save(gruppe);

    assertThat(repoGruppe.findAll()).containsExactly(gruppe);
  }
}