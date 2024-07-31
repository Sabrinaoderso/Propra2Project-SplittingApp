package com.splitterorg.splitter.domain;

import java.util.List;
import org.javamoney.moneta.Money;

/**
 * Record AusgabeTabelle:
 * Datenspeicherobjekt zum Speichern einer AusgabeTabelle.
 * Speichert Geldgeber, eine Liste von GelderhalterInnen, einen Ausgabenzweck und einen Betrag ab.
 */
public record Ausgabe(Benutzer geldgeberIn,
                      List<Benutzer> gelderhalterInnen,
                      String zweck,
                      Money betrag) {
  public Benutzer getGeldgeberIn() {
    return geldgeberIn;
  }

  public List<Benutzer> getGelderhalterInnen() {
    return gelderhalterInnen;
  }

  public String getZweck() {
    return zweck;
  }

  public Money getBetrag() {
    return betrag;
  }

  public boolean istBeteiligt(String benutzername) {
    Benutzer benutzer = new Benutzer(benutzername);
    return geldgeberIn.equals(benutzer) || gelderhalterInnen.contains(benutzer);
  }
}
