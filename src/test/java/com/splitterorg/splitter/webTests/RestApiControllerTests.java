package com.splitterorg.splitter.webTests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.splitterorg.splitter.domain.Ausgabe;
import com.splitterorg.splitter.domain.Benutzer;
import com.splitterorg.splitter.persistence.AusgabeServicePersistence;
import com.splitterorg.splitter.persistence.GruppenServicePersistence;
import com.splitterorg.splitter.restapi.jsonSerializer.GruppeShortSerializer;
import com.splitterorg.splitter.domain.gruppe.Gruppe;
import com.splitterorg.splitter.archunitTests.helper.WithMockOAuth2User;
import com.splitterorg.splitter.restapi.RestApiController;
import com.splitterorg.splitter.web.security.SecurityConfig;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Random.class)
@WebMvcTest(RestApiController.class)
@Import({SecurityConfig.class})
public class RestApiControllerTests {

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

   @Test
  @DisplayName("Rest-API: Gruppen werden korrekt zu JSON serialisiert")
  public void testGruppeJsonSerialization() throws JsonProcessingException {
    // Arrange
    // =======
    //
    // Erstelle eine mock Gruppe mit 3 Benutzern
    Benutzer ronnie = new Benutzer("Ronnie");
    Benutzer keith = new Benutzer("Keith");
    Benutzer mick = new Benutzer("Mick");
    Gruppe gruppe = new Gruppe(List.of(ronnie, keith, mick), "Tour 2023");

    // Gruppe gruppe = new Gruppe(List.of(ronnie, keith, mick), "Tour 2023");

    // Act
    // ===
    //
    // Serialisiere die Gruppe zu JSON
    SimpleModule module = new SimpleModule();
    module.addSerializer(Gruppe.class, new GruppeShortSerializer());
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(module);
    String json = mapper.writeValueAsString(gruppe);
    String id = gruppe.getId().toString();
    // {"gruppe" : "rolling_stones456", "name" : "Tour 2023", "personen" : ["Mick", "Keith", "Ronnie"]}
    String erwartet = "{\"gruppe\":\"" + id + "\",\"name\":\"Tour 2023\",\"personen\":[\"Ronnie\",\"Keith\",\"Mick\"]}";

    // Assert
    // ======
    //
    // Prüfe, ob die Gruppe korrekt serialisiert wurde
    // Also ob der JSON string gleich:
    // {"name" : "Tour 2023", "personen" : ["Mick", "Keith", "Ronnie"] }
    // ist
    assert json.equals(erwartet);
  }

  /**
   * Das Erzeugen von neuen Gruppen erfolgt über einen POST-Request an die URL /api/gruppen. Der vom Client
   *
   * übermittelte JSON Request-Body sieht folgendermaßen aus:
   *    {"name" : "Tour 2023", "personen" : ["Mick", "Keith", "Ronnie"] }
   *
   * Als Antwort wird ein Status 201 erwartet und der Body der Antwort soll die vom backend erzeugte Gruppen-ID sein.
   * Die zurückgegebene ID wird in allen weiteren Requests verwendet, um die Gruppe zu identifizieren.
   *
   * Wenn die Anfrage nicht korrekt strukturiert ist, soll das Backend den Status 400 zurückgeben.
   * Gruppen müssen aus mindestens einer Person bestehen.
   * @throws Exception 400
   */
  @Test
  @DisplayName("Rest-API: Es kann eine einfache Gruppe erstellt werden")
  @WithMockOAuth2User(login = "Florian")
  public void testApiGruppeErstellen() throws Exception {
    // Arrange
    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.post("/api/gruppen")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    // [{\"name\":\"Florian\"},{\"name\":\"Sabrina\"},{\"name\":\"Gedeon\"},{\"name\":\"Paul\"},{\"name\":\"Luca\"}]
                    .content("{\"name\":\"Testgruppe\",\"personen\":[\"Florian\", \"Sabrina\", \"Gedeon\", \"Paul\", \"Luca\"]}")
                    .param("benutzername", "Florian"))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andReturn();

    // verifiziere, dass die Gruppe erstellt wurde
    verify(gruppenservice, times(1)).addGruppe(any(Gruppe.class));
  }

  @Test
  @DisplayName("Rest-API: Gruppe erstellen mit leerem Gruppennamen")
  public void testApiGruppeErstellenFalscherName() throws Exception {
    // Arrange
    // =======
    //

    // Act
    // ===
    //

    // Assert
    // ======
    //

    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.post("/api/gruppen")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"\",\"benutzerListe\":[]}")
                    .param("benutzername", "Florian"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
  }

  @Test
  @DisplayName("Rest-API: Gruppe erstellen mit falscher Benutzerliste")
  public void testApiGruppeErstellenFalscherBenutzerListe() throws Exception {
    // Arrange
    // =======
    //

    // Act
    // ===
    //

    // Assert
    // ======
    //

    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.post("/api/gruppen")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"test\",\"benutzerListe\":[]}")
                    .param("benutzername", "Florian"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
  }

  /**
   * Die Abfrage aller Gruppen, in der eine bestimmte Person ist, erfolgt über einen GET-Request an
   *
   *    /api/user/<GITHUB-LOGIN>/gruppen.
   *
   * Das Backend antwortet mit Status 200 und einem JSON-Dokument, das so aussieht:
   *
   *    [{"gruppe" : "rolling_stones456", "name" : "Tour 2023", "personen" : ["Mick", "Keith", "Ronnie"]}]
   *
   * In dem Beispiel ist nur eine Gruppe enthalten. Wenn es mehr Gruppen gibt, dann sollen alle in dem JSON-Dokument
   * enthalten sein. Wenn es keine Gruppen gibt oder die Person im System unbekannt ist,
   * dann sendet das Backend ein leeres Array.
   */
  @Test
  @DisplayName("Rest-API: Es können alle Gruppen einer Person abgefragt werden")
  public void testApiGruppenAbfragen() throws Exception {
    // Arrange
    // =======
    //

    // Act
    // ===
    //
    // Wir fügen keine Benutzer keiner Gruppe hinzu

    // Assert
    // ======
    //
    // Wir erwarten eine leere Liste "[]"

    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.get("/api/users/Quoteme/gruppen")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            // erwarte eine leere Liste
            .andExpect(MockMvcResultMatchers.content().string("[]"))
            .andReturn();

    //verify(service.add());
  }

  @Test
  @DisplayName("Rest-API: Es können alle Gruppen einer Person abgefragt werden, nachdem eine Gruppe erstellt wurde")
  public void testApiGruppenAbfragen2() throws Exception {
    // Arrange
    // =======
    //
    Gruppe gruppe = new Gruppe(new Benutzer("Bendisposto"), "Testgruppe");

    // Act
    // ===
    //
    // Wir fügen eine Benutzer einer Gruppe hinzu
    when(gruppenservice.getGruppenSetVonBenutzer(new Benutzer("Bendisposto")))
            .thenReturn(Set.of(gruppe));

    // Assert
    // ======
    //
    // Wir erwarten eine leere Liste "[]"

    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.get("/api/users/Bendisposto/gruppen")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            // erwarte eine nicht leere Liste
            .andExpect(MockMvcResultMatchers.content()
                    .string("[{\"gruppe\":\"" + gruppe.getId() + "\",\"name\":\"Testgruppe\",\"personen\":[\"Bendisposto\"]}]"))
            .andReturn();

    //verify(service.add());
  }

  @Test
  @DisplayName("Rest-API: Es können alle Gruppen einer Person abgefragt werden, nachdem eine Gruppe erstellt wurde, in der die Person nicht enthalten ist")
  public void testApiGruppenAbfragen3() throws Exception {
    // Arrange
    // =======
    //

    // Act
    // ===
    //
    // Wir fügen eine Benutzer einer Gruppe hinzu
    new Gruppe(new Benutzer("Bendisposto"), "Testgruppe");

    // Assert
    // ======
    //
    // Wir erwarten eine leere Liste "[]"

    MvcResult result = mvc.perform(
            MockMvcRequestBuilders.get("/api/users/Florian/gruppen")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            // erwarte eine leere Liste
            .andExpect(MockMvcResultMatchers.content().string("[]"))
            .andReturn();

    //verify(service.add());
  }

  @Test
  @DisplayName("Rest-API: Es werden alle gruppen angezeigt, wenn ein benutzer in mehreren gruppen ist")
  public void testApiGruppenAbfragen4() throws Exception {
    // Arrange
    // =======
    //
    Gruppe gruppe1 = new Gruppe(new Benutzer("Bendisposto"), "Testgruppe");
    Gruppe gruppe2 = new Gruppe(new Benutzer("Bendisposto"), "lalelul");
    Gruppe gruppe3 = new Gruppe(new Benutzer("Bendisposto"), "die coolen leute");
    String erwarteteAntwort =
            "[{\"gruppe\":\"" + gruppe1.getId() + "\",\"name\":\"Testgruppe\",\"personen\":[\"Bendisposto\"]}" +
            ",{\"gruppe\":\"" + gruppe2.getId() + "\",\"name\":\"lalelul\",\"personen\":[\"Bendisposto\"]}" +
            ",{\"gruppe\":\"" + gruppe3.getId() + "\",\"name\":\"die coolen leute\",\"personen\":[\"Bendisposto\"]}]";

    // Act
    // ===
    //
    // Wir fügen eine Benutzer einer Gruppe hinzu
    when(gruppenservice.getGruppenSetVonBenutzer(new Benutzer("Bendisposto")))
            .thenReturn(Set.of(gruppe1, gruppe2, gruppe3));
    // Assert
    // ======
    //
    // Wir erwarten eine leere Liste "[]"
    MvcResult result = mvc.perform(
                    MockMvcRequestBuilders.get("/api/users/Bendisposto/gruppen")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            // erwarte eine nicht leere Liste
            .andExpect(MockMvcResultMatchers.content()
                    .json(erwarteteAntwort))
            .andReturn();

    //verify(service.add());
  }

  @Test
  @DisplayName("Rest-API: Gruppen-info api ist erreichbar")
  public void testApiZeigeGruppenInfo() throws Exception {
    // Arrange
    // =======
    //
    Gruppe gruppe1 = new Gruppe(new Benutzer("Bendisposto"), "Testgruppe");

    // Act
    // ===
    //
    // Wir fügen eine Benutzer einer Gruppe hinzu
    when(gruppenservice.getGruppeById(gruppe1.getId())).thenReturn(gruppe1);

    // Assert
    // ======
    //
    // Wir erwarten eine leere Liste "[]"
    MvcResult result = mvc.perform(
                    MockMvcRequestBuilders.get("/api/gruppen/"+gruppe1.getId())
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            // erwarte eine nicht leere Liste
            .andReturn();
  }

  @Test
  @DisplayName("Rest-API: Zeige informationen zu einer Gruppe an")
  public void testApiZeigeGruppenInfo2() throws Exception {
    // Arrange
    // =======
    //
    Gruppe gruppe1 = new Gruppe(new Benutzer("Bendisposto"), "Testgruppe");
    String erwarteteAntwort =
      "{\"gruppe\":\"" + gruppe1.getId() + "\",\"name\":\"Testgruppe\",\"personen\":[\"Bendisposto\"],\"geschlossen\":false,\"ausgaben\":[]}";

    // Act
    // ===
    //
    // Wir fügen eine Benutzer einer Gruppe hinzu
    when(gruppenservice.getGruppeById(gruppe1.getId())).thenReturn(gruppe1);

    // Assert
    // ======
    //
    // Wir erwarten eine leere Liste "[]"
    MvcResult result = mvc.perform(
                    MockMvcRequestBuilders.get("/api/gruppen/"+gruppe1.getId())
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content()
                    .json(erwarteteAntwort))
            .andReturn();

    //verify(service.add());
  }

  @Test
  @DisplayName("Rest-API: Zeige informationen zu einer Gruppe an mit Testcase aus der Dokumentation (also mit ausgabe)")
  public void testApiZeigeGruppenInfo3() throws Exception {
    // Arrange
    // =======
    //
    Benutzer mick = new Benutzer("Mick");
    Benutzer keith = new Benutzer("Keith");
    Benutzer ronnie = new Benutzer("Ronnie");
    Gruppe gruppe = new Gruppe(mick, "Tour 2023");
    String erwarteteAntwort =
              "{\"gruppe\" : \"" + gruppe.getId() + "\"," +
              "\"name\" : \"Tour 2023\"," +
              "\"personen\" : [\"Mick\", \"Keith\", \"Ronnie\"]," +
              "\"geschlossen\": false," +
              "\"ausgaben\" : [" +
                      "{\"grund\": \"Black Paint\"," +
                      "\"glaeubiger\": \"Keith\"," +
                      "\"cent\" : 2599," +
                      "\"schuldner\" : [\"Mick\", \"Keith\", \"Ronnie\"]}" +
              "]}";

    // Act
    // ===
    //
    // Wir fügen eine Benutzer einer Gruppe hinzu
    when(gruppenservice.getGruppeById(gruppe.getId())).thenReturn(gruppe);
    gruppe.teilnehmerHinzfuegen(keith);
    gruppe.teilnehmerHinzfuegen(mick);
    gruppe.teilnehmerHinzfuegen(ronnie);
    gruppe.ausgabeHinzufuegen(new Ausgabe(
            new Benutzer("Keith"),
            List.of(
                    new Benutzer("Mick"),
                    new Benutzer("Keith"),
                    new Benutzer("Ronnie")
            ),
            "Black Paint",
            Money.of(25.99, "EUR")
    ));

    // Assert
    // ======
    //
    // Wir erwarten eine leere Liste "[]"
    MvcResult result2 = mvc.perform(
                    MockMvcRequestBuilders.get("/api/gruppen/"+gruppe.getId())
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content()
                    .json(erwarteteAntwort))
            .andReturn();
  }

  @Test
  @DisplayName("Rest-API: Gruppe kann geschlossen werden")
  public void testApiGruppeKannGeschlossenWerden() throws Exception {
    // Arrange
    // =======
    //
    Benutzer mick = new Benutzer("Mick");
    Gruppe gruppe = new Gruppe(mick, "Tour 2023");

    // Act
    // ===
    //
    // Füge eine Gruppe hinzu
    when(gruppenservice.getGruppeById(gruppe.getId())).thenReturn(gruppe);
    // schließe die Gruppe
    MvcResult result = mvc.perform(
                    MockMvcRequestBuilders.post("/api/gruppen/"+gruppe.getId()+"/schliessen")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

    // Assert
    // ======
    //
    // Verifiziere, dass die Gruppe geschlossen wurde
    verify(gruppenservice).schliesseGruppe(gruppe.getId());
  }

  @Test
  @DisplayName("Rest-API: Gruppe kann nicht geschlossen werden, wenn keine Gruppe mit der ID existiert")
  public void testGruppeKannNichtGeschlossenWerdenWennIdNichtGefunden() throws Exception {
    // Arrange
    // =======
    //
    Benutzer mick = new Benutzer("Mick");
    Gruppe nichtHinzugefuegteGruppe = new Gruppe(mick, "Die nicht vorhandene Gruppe");

    // Act
    // ===
    //
    doThrow(new NoSuchElementException()).when(gruppenservice).schliesseGruppe(nichtHinzugefuegteGruppe.getId());

    // Assert
    // ======
    //
    // Frage mit der API ab, ob die Gruppe geschlossen ist
    // hier sollte eine 404 zurückkommen
    MvcResult result = mvc.perform(
                    MockMvcRequestBuilders.post("/api/gruppen/"+nichtHinzugefuegteGruppe.getId()+"/schliessen")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();
    // verify that gruppenservice.schliesseGruppe() was called once
    verify(gruppenservice, times(1)).schliesseGruppe(nichtHinzugefuegteGruppe.getId());
  }

  @Test
  @DisplayName("Rest-API: AusgabeTabelle kann zu Gruppe hinzu gefügt werden")
  public void testAusgabeHinzufuegen() throws Exception {
    // Arrange
    // =======
    //
    Benutzer mick = new Benutzer("Mick");
    Benutzer keith = new Benutzer("Keith");
    Benutzer ronnie = new Benutzer("Ronnie");
    Gruppe gruppe = new Gruppe(List.of(mick, keith, ronnie), "Tour 2023");

    // Act
    // ===
    //
    // Füge eine Gruppe hinzu
    when(gruppenservice.getGruppeById(gruppe.getId())).thenReturn(gruppe);
    Ausgabe ausgabe = new Ausgabe(
            new Benutzer("Keith"),
            List.of(
                    new Benutzer("Mick"),
                    new Benutzer("Keith"),
                    new Benutzer("Ronnie")
            ),
            "Black Paint",
            Money.of(25.99, "EUR")
    );

    // Assert
    // ======
    //
    // Füge mit der API eine AusgabeTabelle hinzu
    MvcResult result = mvc.perform(
                    MockMvcRequestBuilders.post("/api/gruppen/"+gruppe.getId()+"/auslagen")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"grund\":\"Black Paint\",\"glaeubiger\":\"Keith\",\"cent\":2599,\"schuldner\":[\"Mick\",\"Keith\",\"Ronnie\"]}"))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andReturn();

    // verify if `ausgabeservice.addAusgabe2Gruppe(ausgabe, gruppe.getId())`  gets called
    verify(ausgabeservice, times(1)).addAusgabe2Gruppe(ausgabe, gruppe.getId());
  }

  @Test
  @DisplayName("Rest-API: AusgabeTabelle kann zu Gruppe hinzu gefügt werden und sagt, es hat alles geklappt")
  public void testAusgabeHinzufuegenKlappt() throws Exception {
    // Arrange
    // =======
    //
    Benutzer mick = new Benutzer("Mick");
    Benutzer keith = new Benutzer("Keith");
    Benutzer ronnie = new Benutzer("Ronnie");
    Gruppe gruppe = new Gruppe(List.of(mick, keith, ronnie), "Tour 2023");

    // Act
    // ===
    //
    // Füge eine Gruppe hinzu
    when(gruppenservice.getGruppeById(gruppe.getId())).thenReturn(gruppe);

    // Assert
    // ======
    //
    // Füge mit der API eine AusgabeTabelle hinzu
    MvcResult result = mvc.perform(
                    MockMvcRequestBuilders.post("/api/gruppen/"+gruppe.getId()+"/auslagen")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"grund\":\"Black Paint\",\"glaeubiger\":\"Keith\",\"cent\":2599,\"schuldner\":[\"Mick\",\"Keith\",\"Ronnie\"]}"))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            // teste, ob der response body die richtige AusgabeTabelle enthält
            .andExpect(MockMvcResultMatchers.content().string("AusgabeTabelle eingetragen"))
            .andReturn();

  }

  @Test
  @DisplayName("Rest-API: AusgabeTabelle kann nicht zu Gruppe hinzugefügt werden, wenn keine Gruppe mit der ID existiert")
  public void testAusgabeHinzufuegenScheitern() throws Exception {
    // Arrange
    // =======
    //
    Benutzer mick = new Benutzer("Mick");
    Benutzer keith = new Benutzer("Keith");
    Benutzer ronnie = new Benutzer("Ronnie");
    Gruppe gruppe = new Gruppe(List.of(mick, keith, ronnie), "Tour 2023");
    Gruppe nichtHinzugefuegteGruppe = new Gruppe(List.of(mick, keith, ronnie), "Die nicht vorhandene Gruppe");

    // Act
    // ===
    //
    // Füge eine Gruppe hinzu
    when(gruppenservice.getGruppeById(gruppe.getId())).thenReturn(gruppe);
    doThrow(new NoSuchElementException()).when(gruppenservice).getGruppeById(nichtHinzugefuegteGruppe.getId());

    // Assert
    // ======
    //
    // Füge mit der API eine AusgabeTabelle hinzu
    // hier sollte eine 404 zurückkommen
    MvcResult result = mvc.perform(
                    MockMvcRequestBuilders.post("/api/gruppen/"+nichtHinzugefuegteGruppe.getId()+"/auslagen")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"grund\":\"Black Paint\",\"glaeubiger\":\"Keith\",\"cent\":2599,\"schuldner\":[\"Mick\",\"Keith\",\"Ronnie\"]}"))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();
  }

  @Test
  @DisplayName("Rest-API: AusgabeTabelle kann nicht zu Gruppe hinzugefügt werden, wenn die Gruppe geschlossen ist")
  public void testAusgabeHinzufuegenScheiternWennGruppeGeschlossen() throws Exception {
    // Arrange
    // =======
    //
    Benutzer mick = new Benutzer("Mick");
    Benutzer keith = new Benutzer("Keith");
    Benutzer ronnie = new Benutzer("Ronnie");
    Gruppe gruppe = new Gruppe(List.of(mick, keith, ronnie), "Tour 2023");

    // Act
    // ===
    //
    // Füge eine Gruppe hinzu
    when(gruppenservice.getGruppeById(gruppe.getId())).thenReturn(gruppe);
    // schließe die Gruppe
    gruppe.schliessen();

    // Assert
    // ======
    //
    // Füge mit der API eine AusgabeTabelle hinzu
    // hier sollte eine 400 zurückkommen
    MvcResult result2 = mvc.perform(
                    MockMvcRequestBuilders.post("/api/gruppen/" + gruppe.getId() + "/auslagen")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"grund\":\"Black Paint\",\"glaeubiger\":\"Keith\",\"cent\":2599,\"schuldner\":[\"Mick\",\"Keith\",\"Ronnie\"]}"))
            .andExpect(MockMvcResultMatchers.status().isConflict())
            .andReturn();
  }

  @Test
  @DisplayName("Rest-API: AusgabeTabelle kann nicht zu Gruppe hinzugefügt werden, wenn der request body nicht valide ist")
  public void testeAusgabeHinzufuegenScheitertWennRequestBodyFalsch() throws Exception {
    // Arrange
    // =======
    //
    Benutzer mick = new Benutzer("Mick");
    Benutzer keith = new Benutzer("Keith");
    Benutzer ronnie = new Benutzer("Ronnie");
    Gruppe gruppe = new Gruppe(List.of(mick, keith, ronnie), "Tour 2023");

    // Act
    // ===
    //
    // Füge eine Gruppe hinzu
    when(gruppenservice.getGruppeById(gruppe.getId())).thenReturn(gruppe);

    // Assert
    // ======
    //
    // Schicke eine falsche Request
    // hier sollte eine 400 zurückkommen
    MvcResult result = mvc.perform(
                    MockMvcRequestBuilders.post("/api/gruppen/" + gruppe.getId() + "/auslagen")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"grund\":\"Black Paint\",\"glaeubiger\":\"Keith\",\"cent\":2599,\"schuldner\":[\"Mick\",\"Keith\",\"Ronnie\"]"))
                                                                                                                        // hier fehlt ein } am Ende
                                                                                                                        // DAS IST ABSICHTLICH !!!!!
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
  }

  @Test
  @DisplayName("Rest-API: Ausgleich kann berechnet werden")
  public void testAusgleichBerechnen() throws Exception {
    // Arrange
    // =======
    //
    Benutzer mick = new Benutzer("Mick");
    Benutzer keith = new Benutzer("Keith");
    Benutzer ronnie = new Benutzer("Ronnie");
    Gruppe gruppe = new Gruppe(List.of(mick, keith, ronnie), "Tour 2023");
    gruppe.ausgabeHinzufuegen(
            new Ausgabe(
                    keith,
                    List.of(mick, keith, ronnie),
                    "Black Paint",
                    Money.of(25.99, "EUR")
            )
    );

    // Act
    // ===
    //
    when(gruppenservice.getGruppeById(gruppe.getId())).thenReturn(gruppe);
    // Füge eine AusgabeTabelle in höhe von 2599 Cent hinzu
//    MvcResult result1 = mvc.perform(
//                    MockMvcRequestBuilders.post("/api/gruppen/" + gruppe.getId() + "/auslagen")
//                            .with(SecurityMockMvcRequestPostProcessors.csrf())
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content("{\"grund\":\"Black Paint\",\"glaeubiger\":\"Keith\",\"cent\":2599,\"schuldner\":[\"Mick\",\"Keith\",\"Ronnie\"]}"))
//            .andExpect(MockMvcResultMatchers.status().isCreated())
//            .andReturn();

    // Assert
    // ======
    //
    // Berechne mit der API den Ausgleich / die Schuldenkarte
    MvcResult result = mvc.perform(
                    MockMvcRequestBuilders.get("/api/gruppen/"+gruppe.getId()+"/ausgleich")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content()
                    .json("[{\"von\" : \"Mick\", \"an\" : \"Keith\", \"cents\" : 866}, {\"von\" : \"Ronnie\", \"an\" : \"Keith\", \"cents\" : 866}]"))
            .andReturn();

  }

  @Test
  @DisplayName("Rest-API: Ausgleich kann nicht berechnet werden, geldgebername null ist")
  public void testGeldgebernameFehlt() throws Exception {
    // Arrange
    // =======
    //
    Benutzer mick = new Benutzer("Mick");
    Benutzer keith = new Benutzer("Keith");
    Benutzer ronnie = new Benutzer("Ronnie");
    Gruppe gruppe = new Gruppe(List.of(mick, keith, ronnie), "Tour 2023");

    // Act
    // ===
    //
    when(gruppenservice.getGruppeById(gruppe.getId())).thenReturn(gruppe);
    // Füge eine AusgabeTabelle in höhe von 2599 Cent hinzu
    MvcResult result1 = mvc.perform(
                    MockMvcRequestBuilders.post("/api/gruppen/" + gruppe.getId() + "/auslagen")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"grund\":\"Black Paint\",\"cent\":2599,\"schuldner\":[\"Mick\",\"Keith\",\"Ronnie\"]}"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
  }

  @Test
  @DisplayName("Rest-API: Ausgleich kann nicht berechnet werden, gelderhalter null ist")
  public void testGelderhalterFehlt() throws Exception {
    // Arrange
    // =======
    //
    Benutzer mick = new Benutzer("Mick");
    Benutzer keith = new Benutzer("Keith");
    Benutzer ronnie = new Benutzer("Ronnie");
    Gruppe gruppe = new Gruppe(List.of(mick, keith, ronnie), "Tour 2023");

    // Act
    // ===
    //
    when(gruppenservice.getGruppeById(gruppe.getId())).thenReturn(gruppe);
    // Füge eine AusgabeTabelle in höhe von 2599 Cent hinzu
    MvcResult result1 = mvc.perform(
                    MockMvcRequestBuilders.post("/api/gruppen/" + gruppe.getId() + "/auslagen")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"grund\":\"Black Paint\",\"glaeubiger\":\"Keith\",\"cent\":2599}"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
  }

  @Test
  @DisplayName("Rest-API: Ausgleich kann nicht berechnet werden, Zweck null ist")
  public void testGZweckFehlt() throws Exception {
    // Arrange
    // =======
    //
    Benutzer mick = new Benutzer("Mick");
    Benutzer keith = new Benutzer("Keith");
    Benutzer ronnie = new Benutzer("Ronnie");
    Gruppe gruppe = new Gruppe(List.of(mick, keith, ronnie), "Tour 2023");

    // Act
    // ===
    //
    when(gruppenservice.getGruppeById(gruppe.getId())).thenReturn(gruppe);
    // Füge eine AusgabeTabelle in höhe von 2599 Cent hinzu
    MvcResult result1 = mvc.perform(
                    MockMvcRequestBuilders.post("/api/gruppen/" + gruppe.getId() + "/auslagen")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"glaeubiger\":\"Keith\",\"cent\":2599,\"schuldner\":[\"Mick\",\"Keith\",\"Ronnie\"]}"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
  }

  @Test
  @DisplayName("Rest-API: Ausgleich kann nicht berechnet werden, cent null ist")
  public void testCentFehlt() throws Exception {
    // Arrange
    // =======
    //
    Benutzer mick = new Benutzer("Mick");
    Benutzer keith = new Benutzer("Keith");
    Benutzer ronnie = new Benutzer("Ronnie");
    Gruppe gruppe = new Gruppe(List.of(mick, keith, ronnie), "Tour 2023");

    // Act
    // ===
    //
    when(gruppenservice.getGruppeById(gruppe.getId())).thenReturn(gruppe);
    // Füge eine AusgabeTabelle in höhe von 2599 Cent hinzu
    MvcResult result1 = mvc.perform(
                    MockMvcRequestBuilders.post("/api/gruppen/" + gruppe.getId() + "/auslagen")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"grund\":\"Black Paint\",\"glaeubiger\":\"Keith\",\"schuldner\":[\"Mick\",\"Keith\",\"Ronnie\"]}"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
  }
}
