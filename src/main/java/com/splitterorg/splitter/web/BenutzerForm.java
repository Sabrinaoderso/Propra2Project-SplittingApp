package com.splitterorg.splitter.web;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BenutzerForm(
    @NotEmpty(message = "Bitte geben Sie einen Benutzernamen ein.")
    @Size(max = 39, message = "Bitte geben Sie einen gültigen Benutzernamen ein. (zu lang)")
    @Pattern(regexp = "[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9]",
        message = "Bitte geben Sie einen gültigen Benutzernamen ein. (invalide Zeichen)")
    String benutzername
) {

  public String getBenutzername() {
    return benutzername;
  }
}
