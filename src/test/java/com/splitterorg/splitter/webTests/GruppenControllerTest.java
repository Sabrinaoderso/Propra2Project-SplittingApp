package com.splitterorg.splitter.webTests;

import com.splitterorg.splitter.domain.Ausgabe;
import com.splitterorg.splitter.domain.Benutzer;
import com.splitterorg.splitter.domain.gruppe.Gruppe;
import com.splitterorg.splitter.archunitTests.helper.WithMockOAuth2User;
import com.splitterorg.splitter.persistence.AusgabeServicePersistence;
import com.splitterorg.splitter.persistence.GruppenServicePersistence;
import com.splitterorg.splitter.web.controller.GruppenController;
import com.splitterorg.splitter.web.security.SecurityConfig;
import java.util.List;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// disable spring security for testing

@WebMvcTest(GruppenController.class)
@Import({SecurityConfig.class})
public class GruppenControllerTest {

  @Autowired
  MockMvc mvc;

  @MockBean
  GruppenServicePersistence gruppenservice;
  @MockBean
  AusgabeServicePersistence ausgabeservice;

  @AfterEach
  public void clearGruppen(){
    gruppenservice.dropAllTables();
  }

  // Disable spring security for testing
  // https://stackoverflow.com/questions/49667626/how-to-disable-spring-security-for-testing
  // https://stackoverflow.com/questions/40381580/how-to-disable-spring-security-for-testing-with-embedded-server

  @Test
  @WithMockOAuth2User(login = "Florian")
  @DisplayName("Mappingtest get Mapping: Teste, ob die Seite index aufgerufen werden kann")
  void UebersichtIstVorhandenTest() throws Exception {
    //arrange

    //act: Get Request auf index, assert: status ok wird zurückgegeben
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
  }

  @Test
  @DisplayName("MappingTest: Gruppe Hinzufügen: redirected auf startseite index")
  @WithMockOAuth2User(login = "Florian")
  void GruppeHinzufuegen01TestRedirect() throws Exception{
    //arrange
    //act: Post Request für Gruppe: Gruppenname, assert: es wird auf index zurückgeleitet
    MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/")
        .param("name", "Gruppenname")
        .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(MockMvcResultMatchers.redirectedUrl("/"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andReturn();
  }

  @Test
  @DisplayName("Teste AusgabeTabelle im Html: Gruppe Hinzufügen: hinzugefügte Gruppe wird im Html angezeigt")
  @WithMockOAuth2User(login = "Florian")
  void GruppeHinzufuegen02TestGruppeWirdAngezeigt() throws Exception{
    //arrange: Gruppe wird hinzugefügt
    Benutzer benutzer = new Benutzer("Gedeon");
    gruppenservice.addGruppe(new Gruppe(benutzer,"Gruppenname"));

    //act: get Request
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
    String resultHtml = result.getResponse().getContentAsString();

    //assert: Gruppenname wird auf index ausgegeben
    assertThat(resultHtml).contains("Gruppenname");
  }


  @Test
  @DisplayName("MappingTest: Teilnehmer hinzufügen, redirected auf die detailAnsicht")
  @WithMockOAuth2User(login = "Florian")
  void teilnehmerHinzufuegen01TestRedirect() throws Exception{

    //arrange Gruppe für GruppenID
    Benutzer benutzer1 = new Benutzer("Florian");
    Gruppe gruppe1 = new Gruppe(benutzer1,"gruppenname");
    String gruppenID = gruppe1.getId().toString();
    //gruppenservice.addGruppe(gruppe1);
    when(gruppenservice.getGruppeById(gruppe1.getId())).thenReturn(gruppe1);

    //act perform post und überprüfen, dass auf detailAnsicht redirected wird
    MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/detailAnsicht")
        .param("benutzername", "Benutzername")
        .param("gruppenID",gruppenID)
        .with(SecurityMockMvcRequestPostProcessors.csrf()))
      .andExpect(MockMvcResultMatchers.redirectedUrl("/detailAnsicht?gruppenID=" + gruppenID))
      .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
      .andReturn();
  }

  @Test
  @DisplayName("Teste AusgabeTabelle im Html: Teilnehmer hinzufügen: hinzugefügter Teilnehmer wird im Html detailAnsicht angezeigt")
  @WithMockOAuth2User(login = "Florian")
  void teilnehmerHinzufuegen02TestTeilnehmerWirdAngezeigt() throws Exception{

    //arrange Gruppe für GruppenID
    Benutzer benutzer1 = new Benutzer("Florian");
    Gruppe gruppe1 = new Gruppe(benutzer1,"gruppenname");
    String gruppenID = gruppe1.getId().toString();
    //gruppenservice.addGruppe(gruppe1);

    //arrange Teilnehmer hinzufügen
    Benutzer benutzer2 = new Benutzer("Gedeon2");
    gruppe1.teilnehmerHinzfuegen(benutzer2);
    when(gruppenservice.getGruppeById(gruppe1.getId())).thenReturn(gruppe1);
    //gruppenservice.addBenutzer2Gruppe(benutzer2, gruppe1.getId());

    //act
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/detailAnsicht")
            .param("gruppenID",gruppenID))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andReturn();
    String resultHtml = result.getResponse().getContentAsString();

    //assert
    assertThat(resultHtml).contains("Florian");
    assertThat(resultHtml).contains("Gedeon2");
  }

  @Test
  @DisplayName("MappingTest: AusgabeTabelle hinzufügen, redirected auf die detailAnsicht")
  @WithMockOAuth2User(login = "Florian")
  void ausgabeHinzufuegen01TestRedirect() throws Exception{

    //arrange Gruppe für GruppenID
    Benutzer benutzer1 = new Benutzer("Florian");
    Benutzer benutzer2 = new Benutzer("Gedeon");
    Gruppe gruppe1 = new Gruppe(benutzer1,"gruppenname");
    String gruppenID = gruppe1.getId().toString();
    when(gruppenservice.getGruppeById(gruppe1.getId())).thenReturn(gruppe1);
    gruppe1.teilnehmerHinzfuegen(benutzer2);

    MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/ausgabeHinzufuegen")
        .param("gruppenID",gruppenID)
        .param("geldgeberName", "Gedeon")
        .param("gelderhalter", "Florian")
        .param("betrag", "100000000.00")
        .param("zweck", "Gedeon ist super reich und spendierte Florian einen Porsche")
        .with(SecurityMockMvcRequestPostProcessors.csrf()))
      .andExpect(MockMvcResultMatchers.redirectedUrl("/detailAnsicht?gruppenID=" + gruppenID))
      .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
      .andReturn();
  }

  @Test
  @DisplayName("Teste AusgabeTabelle im Html: eine hinzugefügte AusgabeTabelle wird im html detailAnsicht angezeigt")
  @WithMockOAuth2User(login = "Florian")
  void ausgabeHinzufuegen02TestAusgabeWirdAngezeigt() throws Exception{

    //arrange Gruppe und hole GruppenID
    Benutzer benutzer1 = new Benutzer("Florian");
    Benutzer benutzer2 = new Benutzer("Gedeon");
    Gruppe gruppe1 = new Gruppe(benutzer1,"gruppenname");
    //gruppenservice.addGruppe(gruppe1);
    when(gruppenservice.getGruppeById(gruppe1.getId())).thenReturn(gruppe1);

    //gruppenservice.addBenutzer2Gruppe(benutzer2, gruppe1.getId());
    gruppe1.teilnehmerHinzfuegen(benutzer2);
    String gruppenID = gruppe1.getId().toString();
    //arrange AusgabeTabelle
    Ausgabe ausgabe = new Ausgabe(benutzer1, List.of(benutzer2),"Fahrt", Money.of(30,"EUR"));
    //ausgabeservice.addAusgabe2Gruppe(ausgabe, UUID.fromString(gruppenID));
    gruppe1.ausgabeHinzufuegen(ausgabe);

    //act
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/detailAnsicht")
        .param("gruppenID",gruppenID))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andReturn();
    String resultHtml = result.getResponse().getContentAsString();

    //assert
    assertThat(resultHtml).contains("Florian");
    assertThat(resultHtml).contains("Gedeon");
    assertThat(resultHtml).contains("Fahrt");
    assertThat(resultHtml).contains("30");
  }

  @Test
  @DisplayName("Teste AusgabeTabelle im Html: die offenen Schulden durch eine AusgabeTabelle werden im html detailAnsicht angezeigt")
  @WithMockOAuth2User(login = "Florian")
  void schulden01TestAusgabeWirdAngezeigt() throws Exception{

    //arrange Gruppe und hole GruppenID
    Benutzer benutzer1 = new Benutzer("Florian");
    Benutzer benutzer2 = new Benutzer("Gedeon");
    Gruppe gruppe1 = new Gruppe(benutzer1,"gruppenname");
    gruppe1.teilnehmerHinzfuegen(benutzer2);

    String gruppenID = gruppe1.getId().toString();
    Ausgabe ausgabe1 = new Ausgabe(benutzer1, List.of(benutzer2),"Fahrt", Money.of(30,"EUR"));
    Ausgabe ausgabe2 = new Ausgabe(benutzer1, List.of(benutzer2),"Fahrt", Money.of(30,"EUR"));

    gruppe1.ausgabeHinzufuegen(ausgabe1);
    gruppe1.ausgabeHinzufuegen(ausgabe2);
    when(gruppenservice.getGruppeById(gruppe1.getId())).thenReturn(gruppe1);

    //act
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/detailAnsicht")
        .param("gruppenID",gruppenID))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andReturn();
    String resultHtml = result.getResponse().getContentAsString();

    //assert
    assertThat(resultHtml).contains("60");
  }

  @Test
  @DisplayName("Teste AusgabeTabelle im Html: zwei offene Schulden durch mehrere AusgabeTabelle werden im html detailAnsicht angezeigt")
  @WithMockOAuth2User(login = "Florian")
  void schulden02TestAusgabeWirdAngezeigt() throws Exception{

    //arrange Gruppe und hole GruppenID
    Benutzer benutzer1 = new Benutzer("Florian");
    Benutzer benutzer2 = new Benutzer("Gedeon");
    Benutzer benutzer3 = new Benutzer("Gedeon2");
    Gruppe gruppe1 = new Gruppe(benutzer1,"gruppenname");
    //gruppenservice.addGruppe(gruppe1);
    when(gruppenservice.getGruppeById(gruppe1.getId())).thenReturn(gruppe1);
    //gruppenservice.addBenutzer2Gruppe(benutzer2, gruppe1.getId());
    gruppe1.teilnehmerHinzfuegen(benutzer2);
    //gruppenservice.addBenutzer2Gruppe(benutzer3, gruppe1.getId());
    gruppe1.teilnehmerHinzfuegen(benutzer3);
    String gruppenID = gruppe1.getId().toString();

    //arrange AusgabeTabelle
    Ausgabe ausgabe1 = new Ausgabe(benutzer1, List.of(benutzer2),"Fahrt", Money.of(30,"EUR"));
    Ausgabe ausgabe2 = new Ausgabe(benutzer1, List.of(benutzer2),"Fahrt2", Money.of(30,"EUR"));
    gruppe1.ausgabeHinzufuegen(ausgabe1);
    gruppe1.ausgabeHinzufuegen(ausgabe2);

    //act
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/detailAnsicht")
        .param("gruppenID",gruppenID))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andReturn();
    String resultHtml = result.getResponse().getContentAsString();

    //assert
    assertThat(resultHtml).contains("60");
    assertThat(resultHtml).contains("30");
  }
}