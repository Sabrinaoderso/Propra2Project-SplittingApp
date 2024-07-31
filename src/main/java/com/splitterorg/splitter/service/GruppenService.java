package com.splitterorg.splitter.service;

import com.splitterorg.splitter.domain.Benutzer;
import com.splitterorg.splitter.domain.gruppe.Gruppe;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class GruppenService {

  Set<Gruppe> gruppen = new HashSet<>();


  public Gruppe erstelleGruppe(String gruppenName, Benutzer aktuellerBenutzer) {
    return new Gruppe(aktuellerBenutzer, gruppenName);
  }

  public boolean addGruppe(Gruppe gruppe) {
    if (gruppe != null) {
      gruppen.add(gruppe);
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
    return gruppen.stream()
        .filter(benutzer::istInGruppe)
        .collect(Collectors.toSet());
  }

  public Gruppe getGruppeById(UUID gruppenId) {
    return gruppen.stream().filter(x -> x.getId().equals(gruppenId)).findAny().get();
  }

  public void addBenutzer2Gruppe(Benutzer benutzer, UUID gruppenId) {
    getGruppeById(gruppenId).teilnehmerHinzfuegen(benutzer);
  }


  //setzt boolean geschlossen von Gruppe auf true, damit diese unter geschlossene Gruppen gelistet
  //wird und man nurnoch die offenen Schulden ansehen kann
  public void schliesseGruppe(UUID gruppenId) {
    getGruppeById(gruppenId).schliessen();
  }

  public Set<Gruppe> getGruppen() {
    return gruppen;
  }

  /**
   * Setzte die Gruppen zurück
   * Relevant: https://xkcd.com/327/
   */
  public void dropAllTables() {
    gruppen.clear();
  }

  public void gruppeZuRepositoryHinzufuegen(Gruppe gruppe) {
    //repo.save(gruppe);
  }
}
