package com.splitterorg.splitter.restapi;

import com.splitterorg.splitter.domain.Benutzer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GruppeErzeugenForm(
        @NotEmpty(message = "Bitte geben Sie einen Namen für die Gruppe ein.")
        @Size(max = 50, message = "Der Gruppenname ist zu lang. Es sind maximal 50 Zeichen erlaubt")
        @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Der Gruppenname darf nur aus Buchstaben, Zahlen und Unterstrichen bestehen.")
        String name,
        @NotEmpty(message = "Benutzerliste")
        @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Der Benutzername darf nur aus Buchstaben, Zahlen und Unterstrichen bestehen.")
        List<String> personen
) {
  List<Benutzer> getBenutzerliste() {
    return personen.stream().map(Benutzer::new).toList();
  }
}
