package com.splitterorg.splitter.web.controller;


import com.splitterorg.splitter.domain.Ausgabe;
import com.splitterorg.splitter.domain.Benutzer;
import com.splitterorg.splitter.domain.gruppe.Gruppe;
import com.splitterorg.splitter.persistence.AusgabeServicePersistence;
import com.splitterorg.splitter.persistence.GruppenServicePersistence;
import com.splitterorg.splitter.web.AusgabeForm;
import com.splitterorg.splitter.web.BenutzerForm;
import com.splitterorg.splitter.web.GruppeForm;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.javamoney.moneta.Money;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class GruppenController {

  GruppenServicePersistence gruppenservice;
  AusgabeServicePersistence ausgabenservice;

  public GruppenController(GruppenServicePersistence service, AusgabeServicePersistence service2) {
    this.gruppenservice = service;
    this.ausgabenservice = service2;
  }


  //Get Mapping für die Splitterapplikation Startseite/Gruppenübersicht
  @GetMapping("/")
  public String index(Model model, OAuth2AuthenticationToken token) {

    String loginName = token.getPrincipal().getAttribute("login");
    Benutzer aktuellerBenutzer = new Benutzer(loginName);
    model.addAttribute("loginName", loginName);
    model.addAttribute("benutzer", aktuellerBenutzer);
    List<Gruppe> gruppenVonBenutzer =
        gruppenservice.getGruppenSetVonBenutzer(aktuellerBenutzer).stream().toList();
    model.addAttribute("gruppen", gruppenVonBenutzer);
    model.addAttribute(new GruppeForm("GruppenName", new ArrayList<>()));

    return "index";
  }

  //Post Mapping für die Splitterapplikation Startseite/Gruppenübersicht: Gruppe hinzufügen
  @PostMapping("/")
  public String addGruppe(Model model,
                          @Valid GruppeForm gruppeform,
                          BindingResult bindingResult,
                          OAuth2AuthenticationToken token) {

    String loginName = token.getPrincipal().getAttribute("login");
    Benutzer aktuellerBenutzer = new Benutzer(loginName);
    model.addAttribute("loginName", loginName);
    model.addAttribute("benutzer", aktuellerBenutzer);
    List<Gruppe> gruppenVonBenutzer =
        gruppenservice.getGruppenSetVonBenutzer(aktuellerBenutzer).stream().toList();
    model.addAttribute("gruppen", gruppenVonBenutzer);

    //Validation: Abfangen von falschen Eingaben, redirect zurück auf die Gruppenübersicht ohne Post
    if (bindingResult.hasErrors()) {
      return "index";
    }

    Gruppe erstellteGruppe = new Gruppe(aktuellerBenutzer, gruppeform.getName());
    gruppenservice.addGruppe(erstellteGruppe);

    return "redirect:/";
  }

  //Get-Mapping für die detailAnsicht der Gruppe
  @GetMapping("/detailAnsicht")
  public String detailAnsicht(Model model, OAuth2AuthenticationToken token, String gruppenID) {
    String loginName = token.getPrincipal().getAttribute("login");
    Gruppe gruppe = gruppenservice.getGruppeById(UUID.fromString(gruppenID));

    if (!gruppe.getTeilnehmerliste().contains(new Benutzer(loginName))) {
      return "redirect:/";
    }

    model.addAttribute(new AusgabeForm("Geldgebername",
        "Gelderhalter",
        "0",
        "AusgabenZweck"));
    model.addAttribute(new BenutzerForm(""));
    model.addAttribute("loginName", loginName);
    model.addAttribute("gruppe", gruppe);
    model.addAttribute("gruppenName", gruppe.getName());
    model.addAttribute("schuldenListe", gruppe.erstelleSchuldenkarte());
    model.addAttribute("teilnehmerListe", gruppe.getTeilnehmerliste());
    model.addAttribute("ausgabenListe", gruppe.getAusgabenliste());



    return "detailAnsicht";
  }

  //Post-Mapping für die detailAnsicht der Gruppe: Teilnehmer zu gruppe hinzufügen
  @PostMapping("/detailAnsicht")
  public String addTeilnehmer(Model model,
                              OAuth2AuthenticationToken token,
                              @Valid BenutzerForm benutzerForm,
                              BindingResult bindingResult,
                              String gruppenID) {
    String loginName = token.getPrincipal().getAttribute("login");
    Gruppe gruppe = gruppenservice.getGruppeById(UUID.fromString(gruppenID));

    if (!gruppe.getTeilnehmerliste().contains(new Benutzer(loginName))) {
      return "redirect:/";
    }

    //Validation: Abfangen von falschen Eingaben, redirect zurück auf die detailAnsicht ohne Post
    if (bindingResult.hasErrors()) {
      model.addAttribute("loginName", loginName);
      model.addAttribute("gruppe", gruppe);
      model.addAttribute("gruppenName", gruppe.getName());
      model.addAttribute("schuldenListe", gruppe.erstelleSchuldenkarte());
      model.addAttribute("teilnehmerListe", gruppe.getTeilnehmerliste());
      model.addAttribute("ausgabenListe", gruppe.getAusgabenliste());

      return "detailAnsicht";
    }

    gruppenservice.addBenutzer2Gruppe(
        new Benutzer(benutzerForm.getBenutzername()), UUID.fromString(gruppenID));
    model.addAttribute(new BenutzerForm(""));

    return "redirect:/detailAnsicht?gruppenID=" + gruppenID;
  }


  //Get-Mapping für das ausgabeHinzufuegen-Form
  @GetMapping("/ausgabeHinzufuegen")
  public String ausgabeHinzufuegen(Model model, OAuth2AuthenticationToken token, String gruppenID) {
    String loginName = token.getPrincipal().getAttribute("login");
    Gruppe gruppe = gruppenservice.getGruppeById(UUID.fromString(gruppenID));

    if (!gruppe.getTeilnehmerliste().contains(new Benutzer(loginName))) {
      return "redirect:/";
    }
    model.addAttribute(new AusgabeForm("Geldgebername",
        "Gelderhalter",
        "0",
        "AusgabenZweck"));
    List<Benutzer> teilnehmerListe = gruppe.getTeilnehmerliste();
    model.addAttribute("gruppe", gruppe);
    model.addAttribute("teilnehmerListe", teilnehmerListe);
    return "ausgabeHinzufuegen";
  }

  //Post-Mapping für das ausgabeHinzufuegen-Form: Ausgaben wird hinzugefügt Post
  @PostMapping("/ausgabeHinzufuegen")
  public String ausgabeHinzufuegen(Model model,
                                   OAuth2AuthenticationToken token,
                                   @Valid AusgabeForm ausgabeForm,
                                   BindingResult bindingResult,
                                   String gruppenID) {

    String loginName = token.getPrincipal().getAttribute("login");
    Gruppe gruppe = gruppenservice.getGruppeById(UUID.fromString(gruppenID));

    if (!gruppe.getTeilnehmerliste().contains(new Benutzer(loginName))) {
      return "redirect:/";
    }
    //Validation: Abfangen von falschen Eingaben, redirect zurück auf die detailAnsicht ohne Post
    if (bindingResult.hasErrors()) {
      List<Benutzer> teilnehmerListe = gruppe.getTeilnehmerliste();
      model.addAttribute("gruppe", gruppe);
      model.addAttribute("teilnehmerListe", teilnehmerListe);
      return "ausgabeHinzufuegen";
    }

    System.out.println(ausgabeForm.getGeldgeberName());

    //hole eingegeben AusgabeTabelle aus Ausgabeform
    Ausgabe erstellteAusgabe = ausgabenservice.erstelleAusgabe(
        new Benutzer(ausgabeForm.getGeldgeberName()),
        ausgabenservice.nutzerListeAusString(ausgabeForm.getGelderhalter()),
        Money.of(Double.parseDouble(ausgabeForm.getBetrag()), "EUR"),
        ausgabeForm.getZweck()
        );
    //füge AusgabeTabelle zu Gruppe hinzu, falls die Gruppe offen ist.
    if (gruppenservice.getGruppeById(UUID.fromString(gruppenID)).getOffen()) {
      ausgabenservice.addAusgabe2Gruppe(erstellteAusgabe, UUID.fromString(gruppenID));
    }
    model.addAttribute(new AusgabeForm("Geldgebername",
        "Gelderhalter",
        "0",
        "AusgabenZweck"));

    return "redirect:/detailAnsicht?gruppenID=" + gruppenID;
  }

  //Post-Mapping um die Gruppe zu schließen: Gruppe wird auf geschlossen gesetzt
  @PostMapping(value = "/detailAnsicht/schliessen")
  public String schliesseGruppe(OAuth2AuthenticationToken token, String gruppenID) {
    String loginName = token.getPrincipal().getAttribute("login");
    Gruppe gruppe = gruppenservice.getGruppeById(UUID.fromString(gruppenID));
    if (!gruppe.getTeilnehmerliste().contains(new Benutzer(loginName))) {
      return "redirect:/";
    }
    gruppenservice.schliesseGruppe(UUID.fromString(gruppenID));
    return "redirect:/detailAnsicht?gruppenID=" + gruppenID;
  }
}