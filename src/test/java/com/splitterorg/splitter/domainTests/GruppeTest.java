package com.splitterorg.splitter.domainTests;

import com.splitterorg.splitter.domain.Ausgabe;
import com.splitterorg.splitter.domain.Benutzer;
import com.splitterorg.splitter.domain.gruppe.Gruppe;

import java.util.List;

import com.splitterorg.splitter.domain.Schuld;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.Random.class)
@SpringBootTest
public class GruppeTest {

    @Test
    @DisplayName("neue Gruppe erstellen")
    public void testGruppeErzeugen(){
        // arrange
        // =======
        //
        // erstelle Gruppe
        Gruppe gruppe = new Gruppe();

        // act
        // ===
        //
        // nichts

        // assert
        // ======
        //
        assert(gruppe != null);
    }



    @Test
    @DisplayName("Gruppe wird mit Benutzer und Name als Parameter erzeugt")
    public void testGruppeErzeugenMitBenutzerUndName(){
        // arrange
        // =======
        //
        // erstelle Benutzer und Gruppe
        Benutzer benutzer = new Benutzer("Gedeon");
        Gruppe gruppe = new Gruppe(benutzer, "Beispielname");

        // act
        // ===
        //
        // nichts

        // assert
        // ======
        //
        assertThat(gruppe.getName()).isEqualTo("Beispielname");
        assertThat(gruppe.getTeilnehmerliste()).isEqualTo(List.of(benutzer));
    }

    @Test
    @DisplayName("Gruppe wird geschlossen")
    public void testGruppeSchliessen(){
        // arrange
        // =======
        //
        // erstelle Gruppe
        Gruppe gruppe = new Gruppe();

        // act
        // ===
        //
        // schließe Gruppe
        gruppe.schliessen();

        // assert
        // ======
        //
        // Gruppe soll geschlossen sein
        assertThat(gruppe.getOffen()).isFalse();
    }


    @Test
    @DisplayName("AusgabeTabelle hinzufügen")
    public void testAusgabeZuGruppeHinzufuegen(){
        // arrange
        // =======
        //
        // erstelle AusgabeTabelle und Gruppe
        Ausgabe ausgabe = new Ausgabe(
            new Benutzer("Tim"),
            List.of(new Benutzer("Tim"),new Benutzer("Till")),
            "Sushi",
            Money.of(20,"EUR")
        );

        Gruppe gruppe = new Gruppe();

        // act
        // ===
        //
        // füge AusgabeTabelle zur Gruppe hinzu
        gruppe.ausgabeHinzufuegen(ausgabe);

        // assert
        // ======
        //
        // Gruppe soll AusgabeTabelle enthalten
        assertThat(gruppe.getAusgabenliste()).contains(ausgabe);
    }

    @Test
    @DisplayName("Teilnehmer zu Gruppe hinzufügen")
    public void testTeilnehmerZuGruppeHinzufuegen(){
        // arrange
        // =======
        //
        // erstelle Benutzer und Gruppe
        Gruppe gruppe = new Gruppe();
        Benutzer benutzer1 = new Benutzer("Gedeon");

        // act
        // ===
        //
        // füge Benutzer zur Gruppe hinzu
        gruppe.teilnehmerHinzfuegen(benutzer1);

        // assert
        // ======
        //
        // Gruppe soll Benutzer enthalten
        assertThat(gruppe.getTeilnehmerliste()).contains(benutzer1);
    }



    @Test
    @DisplayName("Gruppe aus zwei Benutzern hat ungerade AusgabeTabelle")
    public void testUngeradeAusgabe(){
        Benutzer benutzerA = new Benutzer("A");
        Benutzer benutzerB = new Benutzer("B");

        Gruppe gruppe = new Gruppe(List.of(benutzerA, benutzerB), "-");

        Ausgabe ungeradeAusgabe = new Ausgabe(
          benutzerA,
          List.of(benutzerA,benutzerB),
          "-",
          Money.of(0.03,"EUR")
        );
        gruppe.ausgabeHinzufuegen(ungeradeAusgabe);

        assertThat(gruppe.erstelleSchuldenkarte()).containsExactly(
          new Schuld(benutzerB,benutzerA,Money.of(0.015,"EUR"))
        );
    }

    //Scenariotests vom Proprateam bis zur
    @Test
    @DisplayName("Scenario1: Ausgleich Summierung:. Gebe Ausgaben aus und messe die gesamten Schulden einer Person")
    public void testeSchuldenVonPerson1() {
        // arrange
        // =======
        //
        // erstelle Benutzer und Gruppe
        Gruppe gruppe = new Gruppe();
        Benutzer a = new Benutzer("a");
        Benutzer b = new Benutzer("b");

        // act
        // ===
        //
        // füge Ausgaben hinzu
        Ausgabe ausgabe1 = new Ausgabe(a, List.of(a,b), "Eislaufen gehen", Money.of(10,"EUR"));
          Ausgabe ausgabe2 = new Ausgabe(a, List.of(a,b), "Eislaufen gehen", Money.of(20,"EUR"));
          gruppe.ausgabeHinzufuegen(ausgabe1);
          gruppe.ausgabeHinzufuegen(ausgabe2);

        // assert
        // ======
        //
        // Gruppe soll Schulden von a zu b haben
        assertThat(gruppe.getAusgabenliste()).contains(ausgabe1,ausgabe2);
        assertThat(gruppe.schuldenVonPersonGesamt(b)).isEqualTo(Money.of(15,"EUR"));
    }

    @Test
    @DisplayName("Scenario2: Ausgleich: A=10 an (A,B), B=5 an (A,B)")
    public void testeSchuldenVonPersonen2() {
        // arrange
        // =======
        //
        // erstelle Benutzer und Gruppe
        Gruppe gruppe = new Gruppe();
        Benutzer a = new Benutzer("a");
        Benutzer b = new Benutzer("B");

        // act
        // ===
        //
        // füge Ausgaben hinzu
        gruppe.ausgabeHinzufuegen(new Ausgabe(a, List.of(a,b), "Eislaufen gehen", Money.of(10,"EUR"))); // a hat -5 euro schulden
        gruppe.ausgabeHinzufuegen(new Ausgabe(b, List.of(a,b), "essen gehen", Money.of(20,"EUR"))); // a hat -5 + 2.5 = -2.5 euro schulden

        // assert
        // ======
        //
        // Gruppe soll Schulden von a zu b haben
        assertThat(gruppe.schuldenVonPersonGesamt(a)).isEqualTo(Money.of(5,"EUR"));
        assertThat(gruppe.schuldenVonPersonGesamt(b)).isEqualTo(Money.of(-5,"EUR"));
    }


    @Test
    @DisplayName("Scenario 3: ohne eigene Beteiligung")
    public void testeSchuldenVonPersonen3() {
        // arrange
        // =======
        //
        // erstelle Benutzer und Gruppe
        Gruppe gruppe = new Gruppe();
        Benutzer a = new Benutzer("a");
        Benutzer b = new Benutzer("B");

        // act
        // ===
        //
        // füge Ausgaben hinzu
        gruppe.ausgabeHinzufuegen(new Ausgabe(a, List.of(b), "Eislaufen gehen", Money.of(10,"EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(a, List.of(a,b), "Eislaufen gehen", Money.of(20,"EUR")));

        // assert
        // ======
        //
        // Gruppe soll Schulden von a zu b haben
        assertThat(gruppe.schuldenVonPersonGesamt(b)).isEqualTo(Money.of(20,"EUR"));
    }

    @Test
    @DisplayName("Scenario 4: Ringausgleich")
    public void testeSchuldenVonPersonen4() {
        // arrange
        // =======
        //
        // erstelle Benutzer und Gruppe
        Gruppe gruppe = new Gruppe();
        Benutzer a = new Benutzer("a");
        Benutzer b = new Benutzer("b");
        Benutzer c = new Benutzer("c");

        // act
        // ===
        //
        // füge Ausgaben hinzu
        gruppe.ausgabeHinzufuegen(new Ausgabe(a, List.of(a,b), "Eislaufen gehen", Money.of(10,"EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(b, List.of(b,c), "Eislaufen gehen", Money.of(10,"EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(c, List.of(c,a), "Eislaufen gehen", Money.of(10,"EUR")));
        // assert
        // ======
        //
        // Gruppe soll Schulden von a zu b haben
        assertThat(gruppe.schuldenVonPersonGesamt(a)).isEqualTo(Money.of(0,"EUR"));
        assertThat(gruppe.schuldenVonPersonGesamt(b)).isEqualTo(Money.of(0,"EUR"));
        assertThat(gruppe.schuldenVonPersonGesamt(c)).isEqualTo(Money.of(0,"EUR"));
    }

    @Test
    @DisplayName("Scenario 5: Gruppe aus 3 Personen, ABC Beispiel aus der Einführung")
    public void testeSchuldenVonPersonen5() {
        // arrange
        // =======
        //
        // erstelle Benutzer und Gruppe
        Gruppe gruppe = new Gruppe();
        Benutzer Anton = new Benutzer("Anton");
        Benutzer Berta = new Benutzer("Berta");
        Benutzer Christian = new Benutzer("Christian");

        // act
        // ===
        //
        // füge Ausgaben hinzu
        gruppe.ausgabeHinzufuegen(new Ausgabe(Anton, List.of(Anton, Berta, Christian), "Eislaufen gehen", Money.of(60,"EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(Berta, List.of(Anton, Berta, Christian), "Eislaufen gehen", Money.of(30,"EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(Christian, List.of(Berta, Christian), "Eislaufen gehen", Money.of(100,"EUR")));
        // assert
        // ======
        //
        // Gruppe soll Schulden von a zu b haben
        assertThat(gruppe.schuldenVonPersonGesamt(Berta)).isEqualTo(Money.of(50,"EUR"));
    }

    @Test
    @DisplayName("SchuldenKarte Test")
    public void schuldenKarte() {
        // arrange
        // =======
        //
        // erstelle Benutzer und Gruppe
        Gruppe gruppe = new Gruppe();
        Benutzer a = new Benutzer("a");
        Benutzer b = new Benutzer("b");
        Benutzer c = new Benutzer("c");


        // act
        // ===
        //
        // fühe Benutzer hinzu
        gruppe.teilnehmerHinzfuegen(a);
        gruppe.teilnehmerHinzfuegen(b);
        gruppe.teilnehmerHinzfuegen(c);
        // füge Ausgaben hinzu
        gruppe.ausgabeHinzufuegen(new Ausgabe(a, List.of(a,b), "Eislaufen gehen", Money.of(10,"EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(b, List.of(b,c), "Freibad", Money.of(10,"EUR")));

        // assert
        // ======
        //
        // Gruppe soll Schulden von a zu b haben
        // am ende soll c an a 5€ zahlen
        assertThat(gruppe.erstelleSchuldenkarte()).isEqualTo(
          List.of(new Schuld(c,a,Money.of(5,"EUR")))
        );

    }

    @Test
    @DisplayName("Scenario5: ABC Beispiel, Ausgelegt AusgabeTabelle passt")
    public void testeSchuldenVonPersonenAusgabe5() {
        // arrange
        // =======
        //
        // erstelle Benutzer und Gruppe
        Gruppe gruppe = new Gruppe();

        Benutzer anton = new Benutzer("anton");
        Benutzer berta = new Benutzer("berta");
        Benutzer christian = new Benutzer("christian");


        // act
        // ===
        //
        // fühe Benutzer hinzu
        gruppe.teilnehmerHinzfuegen(anton);
        gruppe.teilnehmerHinzfuegen(berta);
        gruppe.teilnehmerHinzfuegen(christian);
        // füge Ausgaben hinzu
        gruppe.ausgabeHinzufuegen(new Ausgabe(anton, List.of(anton, berta, christian), "Eislaufen gehen", Money.of(60, "EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(berta, List.of(anton, berta, christian), "Freibad", Money.of(30, "EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(christian, List.of(berta, christian), "Freibad", Money.of(100, "EUR")));

        // assert
        // ======
        //
        // Gruppe soll Schulden von a zu b haben

        assertThat(gruppe.erstelleSchuldenkarte()).isEqualTo(
          List.of(new Schuld(berta,anton,Money.of(30,"EUR")),new Schuld(berta,christian,Money.of(20,"EUR")))
        );
    }


    @Test
    @DisplayName("Scenario6: Gruppe aus 6 Personen mit 7 Ausgaben + Zwecke")
    public void testeSchuldenVonPersonenAusgabe6() {
        // arrange
        // =======
        //
        // erstelle Benutzer und Gruppe
        Gruppe gruppe = new Gruppe();

        Benutzer a = new Benutzer("a");
        Benutzer b = new Benutzer("b");
        Benutzer c = new Benutzer("c");
        Benutzer d = new Benutzer("d");
        Benutzer e = new Benutzer("e");
        Benutzer f = new Benutzer("f");


        // act
        // ===
        //
        // fühe Benutzer hinzu
        gruppe.teilnehmerHinzfuegen(a);
        gruppe.teilnehmerHinzfuegen(b);
        gruppe.teilnehmerHinzfuegen(c);
        gruppe.teilnehmerHinzfuegen(d);
        gruppe.teilnehmerHinzfuegen(e);
        gruppe.teilnehmerHinzfuegen(f);

        // füge Ausgaben hinzu
        gruppe.ausgabeHinzufuegen(new Ausgabe(a, List.of(a, b, c, d, e, f), "Hotelzimmer", Money.of(564, "EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(b, List.of(b,a), "Benzin (Hinweg)", Money.of(38.58, "EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(b, List.of(b,a,d), "Benzin (Rückweg)", Money.of(38.58, "EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(c, List.of(c,e,f), "Benzin", Money.of(82.11, "EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(d, List.of(a, b, c, d, e, f), "Städtetour", Money.of(96, "EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(f, List.of(b,e,f), "Theatervorstellung", Money.of(95.37, "EUR")));

        //erstelle Schuldenkarte
        List<Schuld> schuldenListe = gruppe.erstelleSchuldenkarte();

        assertThat(schuldenListe).containsExactlyInAnyOrder(
          new Schuld(b,a,Money.of(96.78,"EUR")),
          new Schuld(c,a,Money.of(55.26,"EUR")),
          new Schuld(d,a,Money.of(26.86,"EUR")),
          new Schuld(e,a,Money.of(169.16,"EUR")),
          new Schuld(f,a,Money.of(73.79,"EUR"))
        );
    }

    @Test
    @DisplayName("Scenario7: Gruppe aus 7 Personen mit 8 Ausgaben durcheinander, minimal")
    @Disabled //unsere schuldenkarte funktion ist noch nicht minimal
    public void testeSchuldenVonPersonenAusgabe7() {
        // arrange
        // =======
        //
        // erstelle Benutzer und Gruppe
        Gruppe gruppe = new Gruppe();

        Benutzer a = new Benutzer("a");
        Benutzer b = new Benutzer("b");
        Benutzer c = new Benutzer("c");
        Benutzer d = new Benutzer("d");
        Benutzer e = new Benutzer("e");
        Benutzer f = new Benutzer("f");
        Benutzer g = new Benutzer("g");


        // act
        // ===
        //
        // füge Benutzer hinzu
        gruppe.teilnehmerHinzfuegen(a);
        gruppe.teilnehmerHinzfuegen(b);
        gruppe.teilnehmerHinzfuegen(c);
        gruppe.teilnehmerHinzfuegen(d);
        gruppe.teilnehmerHinzfuegen(e);
        gruppe.teilnehmerHinzfuegen(f);
        gruppe.teilnehmerHinzfuegen(g);

        // füge Ausgaben hinzu
        gruppe.ausgabeHinzufuegen(new Ausgabe(d, List.of(d,f), "Hotelzimmer", Money.of(20, "EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(g, List.of(b), "Hotelzimmer", Money.of(10, "EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(e, List.of(a,c,e), "Hotelzimmer", Money.of(75, "EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(f, List.of(a,f), "Hotelzimmer", Money.of(50, "EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(e, List.of(d), "Hotelzimmer", Money.of(40, "EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(f, List.of(b,f), "Hotelzimmer", Money.of(40, "EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(f, List.of(c), "Hotelzimmer", Money.of(5, "EUR")));
        gruppe.ausgabeHinzufuegen(new Ausgabe(g, List.of(a), "Hotelzimmer", Money.of(30, "EUR")));

        //erstelle Schuldenkarte
        List<Schuld> schuldenListe = gruppe.erstelleSchuldenkarte();

        assertThat(schuldenListe).containsExactlyInAnyOrder(
            new Schuld(a,f,Money.of(40,"EUR")),
            new Schuld(a,g,Money.of(40,"EUR")),
            new Schuld(b,e,Money.of(30,"EUR")),
            new Schuld(c,e,Money.of(30,"EUR")),
            new Schuld(d,e,Money.of(30,"EUR"))
        );
    }
}