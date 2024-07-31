package com.splitterorg.splitter.domain;

import org.javamoney.moneta.Money;

/**
 * Die Klasse Schuld soll uns einen Datentyp liefern,
 * in dem wir die schulden übersichtlich und einfach abspeichern können.
 *
 * @param geber Benutzer, der Geld bezahlen muss
 * @param erhalter Benutzer, der das Geld erhält
 * @param betrag Geldbetrag in Euro
 */
public record Schuld(Benutzer geber, Benutzer erhalter, Money betrag) {

  public Benutzer getGeber() {
    return geber;
  }

  public Benutzer getErhalter() {
    return erhalter;
  }

  public Money getBetrag() {
    return betrag;
  }

  /**
   * Vergleicht zwei Schuldobjekte darauf ob Geber, Erhalter und Betrag gleich sind.
   *
   * @param o Schuld mit der verglichen werden soll
   * @return Wahrheitswert über die Gleichheit der Schuldobjekte
   */
  @Override
  public boolean equals(Object o) {
    // wenn es dasselbe Objekt ist
    if (this == o) {
      return true;
    }
    // wenn es null, oder eine Instanz einer anderen Klasse ist
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Schuld schuld = (Schuld) o;

    // Diese Attribute sollten alle auf Gleichheit überprüft werden:
    //
    // private Benutzer geber;
    // private Benutzer erhalter;
    // private Money betrag;
    //

    return geber.equals(schuld.getGeber())
        && erhalter.equals(schuld.getErhalter())
        && betrag.equals(schuld.getBetrag());
  }

  @Override
  public String toString() {
    return "Schuld{"
            + "geber=" + geber
            + ", erhalter=" + erhalter
            + ", betrag=" + betrag
            + '}';
  }

  /**
   * Methode prüft, ob der angegebene Benutzer als Geldgeber oder Erhalter an einer Schuld (also
   * einer notwendigen Transaktion) beteiligt ist und gibt wahr zurück, falls dies der Fall ist.
   *
   * @param benutzername Benutzername des Nutzers, dessen Beteiligung untersucht wird.
   * @return boolean der true ist, wenn der Benutzer beteiligt ist
   */
  public boolean istBeteiligt(String benutzername) {
    Benutzer benutzer = new Benutzer(benutzername);
    if (geber.equals(benutzer) || erhalter.equals(benutzer)) {
      return true;
    }
    return false;
  }
}
