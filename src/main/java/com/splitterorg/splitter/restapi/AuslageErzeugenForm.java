package com.splitterorg.splitter.restapi;

import com.splitterorg.splitter.domain.Benutzer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.javamoney.moneta.Money;

import java.util.List;

public record AuslageErzeugenForm(
        @NotEmpty(message = "Grund der AusgabeTabelle")
        @Size(max = 50, message = "Der Ausgabengrund ist zu lang. Es sind maximal 50 Zeichen erlaubt")
        String grund,
        @NotEmpty(message = "Gläubiger")
        @Size(max = 50, message = "Der Gläubiger ist zu lang. Es sind maximal 50 Zeichen erlaubt")
        String glaeubiger,
        @NotEmpty(message = "Cent")
        String cent,
        @NotEmpty(message = "Schuldner")
        List<String> schuldner
) {
        List<Benutzer> getSchuldnerBenutzer() {
                return schuldner.stream().map(Benutzer::new).toList();
        }

        Benutzer getGlaeubigerBenutzer() {
                return new Benutzer(glaeubiger);
        }

        Money getBetragAsMoney() {
                return Money.of(Double.parseDouble(cent)/100,"EUR");
        }
}
