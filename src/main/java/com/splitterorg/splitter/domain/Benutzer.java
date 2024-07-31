package com.splitterorg.splitter.domain;

import com.splitterorg.splitter.domain.gruppe.Gruppe;
import java.util.Objects;

/**
 * Stellt einzelne Benutzer dar.
 * Dabei ist nicht sichergestellt, dass jeder Benutzer auch wirklich ein GitHub Konto widerspiegelt.
 * Falls ein Nutzer zum Beispiel den GitHub Benutzernamen bei der Angabe falsch schreibt,
 * existiert kein zugehöriges GitHub Konto.
 */
public record Benutzer(String benutzername) {
  /**
   * Entspricht dem GitHub Benutzernamen.
   * Gilt als einzigartig. (Primärschlüssel)
   */

  public String getBenutzername() {
    return benutzername;
  }

  public boolean istInGruppe(Gruppe gruppe) {
    return gruppe.getTeilnehmerliste().contains(this);
  }

  @Override
  public String toString() {
    return benutzername;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Benutzer benutzer = (Benutzer) o;
    return benutzername.equals(benutzer.benutzername);
  }

  @Override
  public int hashCode() {
    return Objects.hash(benutzername);
  }
}
