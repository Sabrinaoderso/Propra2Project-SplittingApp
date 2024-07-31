package com.splitterorg.splitter.restapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.splitterorg.splitter.domain.*;
import com.splitterorg.splitter.domain.gruppe.Gruppe;
import com.splitterorg.splitter.persistence.AusgabeServicePersistence;
import com.splitterorg.splitter.persistence.GruppenServicePersistence;
import com.splitterorg.splitter.restapi.jsonSerializer.GruppeInfoSerializer;
import com.splitterorg.splitter.restapi.jsonSerializer.GruppeShortSerializer;
import com.splitterorg.splitter.restapi.jsonSerializer.SchuldSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Controller
public class RestApiController {

  public GruppenServicePersistence gruppenservice;

  private AusgabeServicePersistence ausgabenservice;

  public RestApiController(GruppenServicePersistence service, AusgabeServicePersistence service2) {
    this.gruppenservice = service;
    this.ausgabenservice = service2;
  }

  /**
   * Das Erzeugen von neuen Gruppen erfolgt über einen POST-Request an die URL /api/gruppen. Der vom Client übermittelte JSON Request-Body sieht folgendermaßen aus:
   * {"name" : "Tour 2023", "personen" : ["Mick", "Keith", "Ronnie"] }
   * Als Antwort wird ein Status 201 erwartet und der Body der Antwort soll die vom backend erzeugte Gruppen-ID sein. Die zurückgegebene ID wird in allen weiteren Requests verwendet, um die Gruppe zu identifizieren.
   * Wenn die Anfrage nicht korrekt strukturiert ist, soll das Backend den Status 400 zurückgeben. Gruppen müssen aus mindestens einer Person bestehen.
   *
   * @param gruppeForm Gruppe, die erstellt werden soll (als JSON)
   *                   (GruppeForm enthält nur den Namen der Gruppe)
   * @return Gruppe, die erstellt wurde (als JSON)
   */
  @CrossOrigin(origins = "*")
  @PostMapping("/api/gruppen")
  @ResponseBody
  public ResponseEntity<String> erstelleGruppe(@RequestBody GruppeErzeugenForm gruppeForm) {
    // Wenn die Anfrage nicht korrekt strukturiert ist, soll das Backend den Status 400 zurückgeben.
    System.out.println("Gruppe hinzugefügt");
    if (gruppeForm.name() == null || gruppeForm.name().isEmpty()) {
      return new ResponseEntity<>("Gruppenname darf nicht leer sein!", HttpStatus.BAD_REQUEST);
    }
    if (gruppeForm.personen() == null || gruppeForm.personen().isEmpty()) {
      return new ResponseEntity<>("Gruppe muss mindestens einen Benutzer enthalten!", HttpStatus.BAD_REQUEST);
    }
    Gruppe gruppe = new Gruppe(gruppeForm.getBenutzerliste(), gruppeForm.name());
    gruppenservice.addGruppe(gruppe);
    return new ResponseEntity<>(gruppe.getId().toString(), HttpStatus.CREATED);
  }

  /**
   * Wir möchten nun unsere REST API mit der Webanwendung verbinden.
   * Wir fangen damit an, dass wir GET-Mappings an die URL /api/gruppen/ansicht/{benutzername} hinzufügen.
   * Die Methode soll eine Liste von Gruppen zurückgeben, die dem Benutzer gehören.
   * Nach der Aufgabenstellung möchten wir auf Authentifizierung verzichten.
   * Wir verwenden daher die Annotation @CrossOrigin, um die Anforderung von einem beliebigen
   * Quellhost zu akzeptieren.
   *
   * @param benutzername Benutzername von der Person, deren Gruppen abgerufen werden (groß/kleinschreibung beachten!)
   * @return Liste von Gruppen, die dem Benutzer gehören (als JSON-String)
   *
   */
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/api/users/{benutzername}/gruppen")
  @ResponseBody
  public String getGruppenVonBenutzer(@PathVariable String benutzername) throws JsonProcessingException {
    List<Gruppe> data = gruppenservice
            .getGruppenSetVonBenutzer(new Benutzer(benutzername)).stream().toList();

    // serialisiere die Gruppen mittels GruppeOverviewSerializer
    SimpleModule module = new SimpleModule();
    module.addSerializer(Gruppe.class, new GruppeShortSerializer());
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(module);
    return mapper.writeValueAsString(data);
  }

  /**
   * Anzeigen der Informationen zu einer Gruppe
   * Mithilfe eines GET-Requests an /api/gruppen/<ID> erhalten wir die Informationen zu einer Gruppe.
   * Falls es keine Gruppe mit der ID gibt, wird 404 als Status zurückgegeben. Ansonsten antwortet das Backend mit
   * Status 200 und einem JSON-Dokument, das die Basisinformationen der Gruppe und alle eingetragenen Ausgaben
   * (aber nicht die Ausgleichszahlungen) enthält. Das JSON-Dokument soll folgendermaßen aussehen:
   * {
   *   "gruppe" : "rolling_stones456",
   *   "name" : "Tour 2023",
   *   "personen" : [
   *     "Mick",
   *     "Keith",
   *     "Ronnie"
   *   ],
   *   geschlossen: false,
   *   "ausgaben" : [
   *     {
   *       "grund": "Black Paint",
   *       "glaeubiger": "Keith",
   *       "cent" : 2599,
   *       "schuldner" : [
   *         "Mick",
   *         "Keith",
   *         "Ronnie"
   *       ]
   *     }
   *   ]
   * }
   */
  @CrossOrigin(origins = "*")
  @GetMapping(
          value = "/api/gruppen/{gruppenID}"
  )
  @ResponseBody
  public ResponseEntity<String> getGruppe(@PathVariable String gruppenID) throws JsonProcessingException {
    // erstelle ein Jackson JSON-Serializer Objekt, bei dem wir einstellen, dass
    SimpleModule module = new SimpleModule();
    module.addSerializer(Gruppe.class, new GruppeInfoSerializer());
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(module);
    try {
      return new ResponseEntity<>(mapper.writeValueAsString(gruppenservice.getGruppeById(UUID.fromString(gruppenID))), HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Schließen einer Gruppe
   * Eine Gruppe wird mit einem POST-Request an /api/gruppen/<ID>/schliessen geschlossen.
   * Wenn keine Gruppe mit der ID existiert, ist der Antwort-Status 404. Ansonsten sendet das Backend Status 200.
   *
   * Die Antwort darf einen String enthalten, dieser wird aber ignoriert.
   */
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/api/gruppen/{id}/schliessen")
  @ResponseBody
  public ResponseEntity<String> schliesseGruppe(@PathVariable String id) {
    try {
      gruppenservice.schliesseGruppe(UUID.fromString(id));
    } catch (NoSuchElementException | IllegalArgumentException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>("Gruppe geschlossen", HttpStatus.OK);
  }

  /**
   * Eintragen von Auslagen
   * Eine Auslage wird durch einen POST-Request an die URL /api/gruppen/<ID>/auslagen hinzugefügt.
   * Der Request-Body hat dabei folgende Struktur:
   *     {"grund": "Black Paint", "glaeubiger": "Keith", "cent" : 2599, "schuldner" : ["Mick", "Keith", "Ronnie"]}
   * Als Rückgabe wird erwartet:
   *     Bedingung	                                 | HTTP Status
   *     --------------------------------------------|------------
   *     Die Gruppe ist nicht vorhanden              | 404
   *     Die Gruppe ist bereits geschlossen          | 409
   *     Das JSON Dokument ist fehlerhaft            | 400
   *     Die AusgabeTabelle wurde korrekt eingetragen       | 201
   * Die Antwort darf einen String enthalten, dieser wird aber ignoriert.
   */
  @CrossOrigin(origins = "*")
  @PostMapping(value = "/api/gruppen/{id}/auslagen")
  @ResponseBody
  public ResponseEntity<String> addAusgabe(@PathVariable String id, @RequestBody AuslageErzeugenForm auslageForm) {
    Gruppe gruppe;

    // Prüfe, ob die Gruppe existiert
    try {
      gruppe = gruppenservice.getGruppeById(UUID.fromString(id));
    }
    catch (NoSuchElementException e) {
      return new ResponseEntity<>("Gruppe nicht gefunden", HttpStatus.NOT_FOUND);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Prüfe, ob die Gruppe bereits geschlossen ist
    if (!gruppe.getOffen()) {
      return new ResponseEntity<>("Gruppe ist bereits geschlossen", HttpStatus.CONFLICT);
    }

    // Prüfe, ob der JSON-Body korrekt ist
    if(auslageForm.glaeubiger() == null
            || auslageForm.schuldner() == null
            || auslageForm.schuldner().isEmpty()
            || auslageForm.grund() == null
            || auslageForm.cent() == null) {
      return new ResponseEntity<>("AusgabeTabelle konnte nicht eingetragen werden", HttpStatus.BAD_REQUEST);
    }

    // Prüfe, ob die AusgabeTabelle korrekt eingetragen werden kann
    try {
      ausgabenservice.addAusgabe2Gruppe(new Ausgabe(
              auslageForm.getGlaeubigerBenutzer(),
              auslageForm.getSchuldnerBenutzer(),
              auslageForm.grund(),
              auslageForm.getBetragAsMoney()
      ), gruppe.getId());
    }
    catch (IllegalArgumentException e) {
      return new ResponseEntity<>("AusgabeTabelle konnte nicht eingetragen werden", HttpStatus.CONFLICT);
    }

    // return "AusgabeTabelle eingetragen"; mit 201
    return new ResponseEntity<>("AusgabeTabelle eingetragen", HttpStatus.CREATED);
  }

  /**
   * Berechnen der Ausgleichszahlungen
   * Über einen GET-Request an /api/gruppen/<ID>/ausgleich können die Ausgleichszahlungen abgefragt werden.
   * Es wird kein Request-Body mitgeschickt.
   * Falls es keine Gruppe mit der ID gibt, wird 404 als Status zurückgegeben.
   * Ansonsten wird der Status 200 zurückgegeben und der Responsebody enthält ein JSON-Dokument,
   * das folgendermaßen aussehen soll:
   *     [{"von" : "Mick", "an" : "Keith", "cents" : 866}, {"von" : "Ronnie", "an" : "Keith", "cents" : 866}]
   */
  @CrossOrigin(origins = "*")
  @GetMapping(value = "/api/gruppen/{id}/ausgleich")
  @ResponseBody
  public ResponseEntity<String> getAusgleich(@PathVariable String id) throws JsonProcessingException {
    Gruppe gruppe;

    // Prüfe, ob die Gruppe existiert
    try {
      gruppe = gruppenservice.getGruppeById(UUID.fromString(id));
    }
    catch (NoSuchElementException e) {
      return new ResponseEntity<>("Gruppe nicht gefunden", HttpStatus.NOT_FOUND);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Prüfe, ob die Gruppe bereits geschlossen ist
    if (!gruppe.getOffen()) {
      return new ResponseEntity<>("Gruppe ist bereits geschlossen", HttpStatus.CONFLICT);
    }

    // erstelle ein Jackson JSON-Serializer Objekt, bei dem wir einstellen, dass
    SimpleModule module = new SimpleModule();
    module.addSerializer(Schuld.class, new SchuldSerializer());
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(module);
    return new ResponseEntity<>(
            mapper.writeValueAsString(
                    gruppenservice
                            .getGruppeById(UUID.fromString(id))
                            .erstelleSchuldenkarte()
            ),
            HttpStatus.OK);
  }
}
