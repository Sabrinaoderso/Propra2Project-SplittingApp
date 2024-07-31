package com.splitterorg.splitter.persistence;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.splitterorg.splitter.domain.Ausgabe;
import com.splitterorg.splitter.domain.Benutzer;
import com.splitterorg.splitter.domain.gruppe.Gruppe;
import org.javamoney.moneta.Money;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Version;

public class GruppeTabelle {

  String gruppenname;
  @Id
  UUID gruppe_id;
  boolean offen;
  @Version
  int version;

  Set<TeilnehmerInGruppe> teilnehmerListe;
  Set<AusgabeTabelle> ausgabenSet;

  @PersistenceCreator
  public GruppeTabelle(String gruppenname,
                       UUID gruppe_id,
                       boolean offen,
                       Set<TeilnehmerInGruppe> teilnehmerListe,
                       Set<AusgabeTabelle> ausgabenSet) {
    this.gruppenname = gruppenname;
    this.gruppe_id = gruppe_id;
    this.offen = offen;
    this.teilnehmerListe = teilnehmerListe;
    this.ausgabenSet = ausgabenSet;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GruppeTabelle gruppe = (GruppeTabelle) o;
    return gruppenname.equals(gruppe.gruppenname) && gruppe_id.equals(gruppe.gruppe_id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gruppenname, gruppe_id);
  }

  public GruppeTabelle ausgabeHinzufuegen(AusgabeTabelle ausgabeTabelle) {
    this.ausgabenSet.add(ausgabeTabelle);
    return this;
  }

  public GruppeTabelle teilnehmerHinzufuegen(TeilnehmerInGruppe teilnehmerInGruppe) {
    this.teilnehmerListe.add(teilnehmerInGruppe);
    return this;
  }

  public GruppeTabelle schliessen() {
    this.offen = false;
    return this;
  }

  public Gruppe toGruppe() {
    return new Gruppe(                    // name
                    this.gruppenname,
                    // UUID
                    this.gruppe_id,
                    // teilnehmerliste
                     this.teilnehmerListe.stream()
                             .map(teilnehmer -> new Benutzer(teilnehmer.benutzername()))
                             .toList(),
                    // ausgabenliste
                     this.ausgabenSet.stream()
                             .map(ausgabe -> new Ausgabe(
                                             new Benutzer(ausgabe.geldgeberIn()),
                                             ausgabe.gelderhalterInnen().stream()
                                                     .map(gelderhalter -> new Benutzer(gelderhalter.benutzername()))
                                                     .collect(Collectors.toList()),
                                             ausgabe.zweck(),
                                             Money.of(ausgabe.betrag(), "EUR").divide(100)
                                     )
                             )
                             .toList(),
                    // Offen
                    this.offen
    );
  }
}
