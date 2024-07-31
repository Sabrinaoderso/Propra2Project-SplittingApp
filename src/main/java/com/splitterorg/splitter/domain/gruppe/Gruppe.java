package com.splitterorg.splitter.domain.gruppe;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.splitterorg.splitter.annotationes.AggregateRoot;
import com.splitterorg.splitter.domain.Ausgabe;
import com.splitterorg.splitter.domain.Benutzer;
import com.splitterorg.splitter.domain.Schuld;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.javamoney.moneta.Money;

/**
 * Stellt einzelne Gruppen dar.
 */
@AggregateRoot
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({"gruppe", "name", "personen"})
public class Gruppe {
  /**
   * Name der Gruppe.
   * Es wird nicht sichergestellt, dass dieser einzigartig ist.
   */
  String name;

  /**
   * Die ID ist ein eindeutiger Wert, welcher diese Gruppe von anderen unterscheidet.
   * (auch, wenn Sie diese Gruppe und eine andere den selben Namen haben)
   */
  UUID id;

  List<Benutzer> teilnehmerliste;

  List<Ausgabe> ausgabenliste;

  public Gruppe(String name, UUID id, List<Benutzer> teilnehmerliste, List<Ausgabe> ausgabenliste, boolean offen) {
    this.name = name;
    this.id = id;
    this.teilnehmerliste = teilnehmerliste;
    this.ausgabenliste = ausgabenliste;
    this.offen = offen;
  }

  /**
   * Gibt an, ob die Interaktion mit der Gruppe möglich ist bzw. sein soll.
   * Zu geschlossenen Gruppen sollten keine Teilnehmer und Ausgaben mehr hinzugefügt werden.
   */
  boolean offen = true;

  /**
   * Erstellt eine leere Gruppe mit dem Namen "kein Name".
   */
  public Gruppe() {
    this.name = "kein Name";
    this.id = UUID.randomUUID();
    teilnehmerliste = new ArrayList<>();
    ausgabenliste = new ArrayList<>();
  }

  public UUID getId() {
    return id;
  }


  /**
   * Erstellt eine Gruppe bestehend aus einem Teilnehmer.
   *
   * @param benutzer Teilnehmer
   * @param name Name der Gruppe
   */
  public Gruppe(Benutzer benutzer, String name) {
    this.name = name;
    this.id = UUID.randomUUID();
    teilnehmerliste = new ArrayList<>(List.of(benutzer));
    ausgabenliste = new ArrayList<>();
  }

  /**
   * Erstellt eine neue Gruppe bestehend aus den Teilnehmern, welche übergeben werden.
   *
   * @param teilnehmerliste Liste der Teilnehmer
   * @param name Name der Gruppe
   */
  public Gruppe(List<Benutzer> teilnehmerliste, String name) {
    this.name = name;
    this.id = UUID.randomUUID();
    this.teilnehmerliste = teilnehmerliste;
    ausgabenliste = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public List<Benutzer> getTeilnehmerliste() {
    return teilnehmerliste;
  }

  public List<Ausgabe> getAusgabenliste() {
    return ausgabenliste;
  }

  public boolean getOffen() {
    return offen;
  }

  public void ausgabeHinzufuegen(Ausgabe ausgabe) {
    this.ausgabenliste.add(ausgabe);
  }

  /**
   * Fügt einen Benutzer in die Teilnehmerliste einer Gruppe hinzu.
   *
   * @param benutzer der hinzuzufügende Benutzer
   */
  public void teilnehmerHinzfuegen(Benutzer benutzer) {
    // füge den benutzer nur in die Liste hinzu, wenn er nicht bereits in der Liste ist
    if (!this.teilnehmerliste.contains(benutzer)) {
      this.teilnehmerliste.add(benutzer);
    }
  }

  public void schliessen() {
    this.offen = false;
  }

  /**
   * Errechnet die Schulden, die eine Person insgesamt noch tilgen muss.
   *
   * @param benutzer Person, von der wir die Schulden wissen wollen.
   * @return Die Gesamtsumme der Schulden von benutzer.
   *         Ein negativer Wert bedeutet, dass benutzer noch Geld erhält.
   */
  public Money schuldenVonPersonGesamt(Benutzer benutzer) {
    Money ausgegebenes = this.ausgabenliste.stream()
            // filtere alle Ausgaben, in denen die Person beteiligt ist
            .filter(ausgabe -> ausgabe.getGelderhalterInnen().contains(benutzer))
            // berechne den Anteil der Person an der AusgabeTabelle
            .map(ausgabe -> ausgabe.getBetrag().divide(ausgabe.getGelderhalterInnen().size()))
            // summiere alle Schulden
            .reduce(Money.of(0, "EUR"), Money::add);
    Money vorgestrecktes = this.ausgabenliste.stream()
        .filter(ausgabe -> ausgabe.getGeldgeberIn().equals(benutzer))
        .map(Ausgabe::getBetrag)
        .reduce(Money.of(0, "EUR"), Money::add);

    return vorgestrecktes.subtract(ausgegebenes).negate();
  }

  /**
   * Erstellt eine zusammenfassende Liste durch die alle nötigen Transaktionen für den Geldausgleich
   * angegeben werden.
   *
   * @return Liste aus Schuldobjekten, die die nötigen Ausgleichs-Transaktionen zwischen den
   *         Teilnehmern angeben.
   */
  public List<Schuld> erstelleSchuldenkarte() {

    // erstelle eine Map, welche die Teilnehmer als Schlüssel hat und als Werte
    // die gesamten Schulden der Teilnehmer hat
    var schuldenVonPersonGesamt = this.teilnehmerliste.stream()
        .collect(Collectors.toMap(Function.identity(), this::schuldenVonPersonGesamt));

    // Eine Map, welche nur die Personen enthält, die Geld geben müssen
    var geberMap = schuldenVonPersonGesamt.entrySet().stream()
            .filter(entry -> entry.getValue().compareTo(Money.of(0, "EUR")) > 0)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    // Eine Map, welche nur die Personen enthält, die Geld von Gebern einnehmen werden
    var nehmerMap = schuldenVonPersonGesamt.entrySet().stream()
            .filter(entry -> entry.getValue().compareTo(Money.of(0, "EUR")) < 0)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));



    // Wir möchten nun, dass jeder Geber genau so viel an jeden Nehmer zahlt,
    // bis er keine Schulden mehr hat
    List<Schuld> schuldenListe = new ArrayList<>();

    // Sortiere die Geber nach Schulden von klein nach groß
    geberMap.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .forEach(geberEntity -> {

              // Sortiere die Nehmer nach Schulden von klein nach groß
              nehmerMap.entrySet().stream()
                      .sorted(Map.Entry.comparingByValue())
                      .forEach(nehmerEntry -> {
                        var geber = geberEntity.getKey();
                        var geberBetrag = geberEntity.getValue().abs();
                        var nehmer = nehmerEntry.getKey();
                        var nehmerBetrag = nehmerEntry.getValue().abs();

                        // Wir möchten nun den die Schulden des Nehmers mit den "Schulden"
                        // (also, das Geld, was der Nehmer noch eintreiben muss)
                        // des Gebers vergleichen

                        if (!nehmerBetrag.isZero()) {
                          // Wenn der Nehmerbetrag größer als der Geberbetrag ist
                          if (nehmerBetrag.compareTo(geberBetrag) > 0) {

                            if (!geberBetrag.isZero()) {
                              schuldenListe.add(new Schuld(geber, nehmer, geberBetrag));
                            }

                            nehmerEntry.setValue(nehmerBetrag.subtract(geberBetrag));
                            geberEntity.setValue(Money.of(0, "EUR"));

                            // Wenn der Nehmerbetrag kleiner oder gleich als der Geberbetrag ist
                          } else {
                            if (!nehmerBetrag.isZero()) {
                              schuldenListe.add(new Schuld(geber, nehmer, nehmerBetrag));
                            }
                            geberEntity.setValue(geberBetrag.subtract(nehmerBetrag));
                            nehmerEntry.setValue(Money.of(0, "EUR"));
                          }
                        }
                      });
            });

    return schuldenListe;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Gruppe gruppe = (Gruppe) o;
    return name.equals(gruppe.name) && id.equals(gruppe.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, id);
  }
}
