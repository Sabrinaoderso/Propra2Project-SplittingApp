package com.splitterorg.splitter.web;

import com.splitterorg.splitter.domain.Benutzer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record GruppeForm(
    // Name der Gruppe, die angelegt werden soll.
    @NotEmpty(message = "Bitte geben Sie einen Namen für die Gruppe ein.")
    @Size(max = 50, message = "Der Gruppenname ist zu lang. Es sind maximal 50 Zeichen erlaubt")
    String name,
    List<Benutzer> benutzerListe
) {

  public String getName() {
    return name;
  }

  public List<Benutzer> getBenutzerListe() {
    return benutzerListe;
  }
}
