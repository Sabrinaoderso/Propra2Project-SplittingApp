package com.splitterorg.splitter.persistence;

import com.splitterorg.splitter.domain.Ausgabe;
import com.splitterorg.splitter.domain.Benutzer;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;


@Service
public class AusgabeServicePersistence {

  GruppenServicePersistence gruppenService;


  public AusgabeServicePersistence(GruppenServicePersistence gruppenService) {
    this.gruppenService = gruppenService;
  }

  public void addAusgabe2Gruppe(Ausgabe ausgabe, UUID gruppenID) {
    gruppenService.addAusgabe2Gruppe(ausgabe, gruppenID);
  }

  public Ausgabe erstelleAusgabe(Benutzer geldgeber,
                                 List<Benutzer> gelderhalterList,
                                 Money geldbetrag,
                                 String zweck) {
    return new Ausgabe(geldgeber, gelderhalterList, zweck, geldbetrag);
  }

  public  List<Benutzer> nutzerListeAusString(String gelderhalter) {
    if (gelderhalter.charAt(0) == '[') {
      gelderhalter = gelderhalter.substring(1);
    }
    if (gelderhalter.charAt(gelderhalter.length() - 1) == ']') {
      gelderhalter = gelderhalter.substring(0, gelderhalter.length() - 1);
    }
    List<String> items = Arrays.asList(gelderhalter.split("\\s*,\\s*"));
    return items.stream().map(Benutzer::new).collect(Collectors.toList());
  }

}

