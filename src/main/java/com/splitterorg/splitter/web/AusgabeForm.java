package com.splitterorg.splitter.web;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.javamoney.moneta.Money;

public record AusgabeForm(
    @NotEmpty(message = "Bitte wählen Sie einen Geldgeber aus.")
    //@Size(max = 50, message = "Darf nicht länger als 50 Zeichen sein")
    String geldgeberName,

        @NotEmpty(message = "Bitte wählen Sie mindestens einen Gelderhalter aus")
            //@Size(max = 300, message = "Darf nicht länger als 300 Zeichen sein")
            String gelderhalter,

        @NotEmpty(message = "Bitte geben Sie an, um welchen Betrag es sich bei der AusgabeTabelle handelt.")
        @Pattern(regexp = "[0-9]+\\.[0-9]{2}",
            message = "Bitte geben Sie den Betrag mit beliebig vielen Vor- und zwei "
              + "Nachkommastellen ein. Anstatt dem Komma sollten Sie einen Punkt benutzen.")
            String betrag,

        @NotEmpty(message = "Bitte geben Sie den Zweck der AusgabeTabelle an.")
        @Size(max = 80, message = "Der Zweck ist zu lang. Es sind maximal 80 Zeichen erlaubt.")
            String zweck
) {

  public String getGeldgeberName() {
    return geldgeberName;
  }

  public String getGelderhalter() {
    return gelderhalter;
  }

  public String getBetrag() {
    if (betrag == null) {
      return null;
    }
    return betrag.toString();
  }

  public Money getBetragAsMoney() {
    return Money.parse(betrag);
  }

  public String getZweck() {
    return zweck;
  }


  public String getGlaeubiger() {
    return geldgeberName;
  }

  public String getCent() {
    if (betrag == null) {
      return null;
    }
    return String.valueOf((int) (Double.parseDouble(betrag) * 100));
  }


  public String getGrund() {
    return zweck;
  }



  public String getSchuldner() {
    return gelderhalter;
  }
}
