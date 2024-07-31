package com.splitterorg.splitter.persistence;

import com.splitterorg.splitter.domain.Ausgabe;
import com.splitterorg.splitter.domain.Benutzer;
import com.splitterorg.splitter.domain.gruppe.Gruppe;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class GruppenServicePersistence {

  GruppenRepository repo;

  public GruppenServicePersistence(GruppenRepository repo) {
    this.repo = repo;
  }

  private GruppeTabelle gruppenParser(Gruppe gruppe){
    return new GruppeTabelle(
      gruppe.getName(),
      gruppe.getId(),
      gruppe.getOffen(),

      gruppe.getTeilnehmerliste().stream()
        .map(e-> new TeilnehmerInGruppe(0,e.getBenutzername()))
        .collect(Collectors.toSet()),

      gruppe.getAusgabenliste().stream()
        .map(this::ausgabeParser)
        .collect(Collectors.toSet())
    );
  }

  private AusgabeTabelle ausgabeParser(Ausgabe ausgabe){
    return new AusgabeTabelle(
      0,
      ausgabe.getGeldgeberIn().toString(),

      ausgabe.getGelderhalterInnen().stream()
        .map(e -> new GelderhalterInnen(0, e.getBenutzername()))
        .collect(Collectors.toSet()),

      ausgabe.getZweck(),
      ausgabe.getBetrag().getNumberStripped().multiply(new BigDecimal(100)).intValue()
    );
  }


  public boolean addGruppe(Gruppe gruppe) {

    if (gruppe != null) {
     repo.save(gruppenParser(gruppe));
     
      return true;
    }
    return false;
  }


  /**
   * Gibt alle Gruppen an, in denen der Benutzer enthalten ist.
   *
   * @param benutzer Der Benutzer, der in jeder Gruppe der ausgabe sein sollte.
   * @return Set von allen Gruppen, in denen der Benutzer drin ist.
   */
  public Set<Gruppe> getGruppenSetVonBenutzer(Benutzer benutzer) {
    return this.repo.findAll().stream()
        .filter(gruppe -> gruppe.teilnehmerListe.stream()
            .anyMatch(teilnehmer -> teilnehmer.benutzername().equals(benutzer.getBenutzername())))
            .map(GruppeTabelle::toGruppe)
        .collect(Collectors.toSet());
  }

  public Gruppe getGruppeById(UUID gruppenId) {
    return this.repo.findById(gruppenId).map(GruppeTabelle::toGruppe).orElse(null);
  }

  public void addBenutzer2Gruppe(Benutzer benutzer, UUID gruppenId) {
    GruppeTabelle zuSpeichern = repo.findById(gruppenId).get().teilnehmerHinzufuegen(
      new TeilnehmerInGruppe(0,benutzer.getBenutzername()));
    repo.save(zuSpeichern);
  }

  public void addAusgabe2Gruppe(Ausgabe ausgabe, UUID gruppenId) {
    GruppeTabelle zuSpeichern = repo.findById(gruppenId).get().ausgabeHinzufuegen(
      ausgabeParser(ausgabe));
    repo.save(zuSpeichern);
  }

  //setzt boolean geschlossen von Gruppe auf true, damit diese unter geschlossene Gruppen gelistet
  //wird und man nurnoch die offenen Schulden ansehen kann
  public void schliesseGruppe(UUID gruppenId) {

    repo.save(repo.findById(gruppenId).get().schliessen());
  }

  public Set<Gruppe> getGruppen() {
    return this.repo.findAll().stream().map(GruppeTabelle::toGruppe).collect(Collectors.toSet());
  }

  /**
   * Setzte die Gruppen zurück
   * Relevant: https://xkcd.com/327/
   */
  public void dropAllTables() {
    this.repo.deleteAll();
  }
}
