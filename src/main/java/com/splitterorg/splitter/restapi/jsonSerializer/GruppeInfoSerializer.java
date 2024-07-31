package com.splitterorg.splitter.restapi.jsonSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.splitterorg.splitter.domain.Ausgabe;
import com.splitterorg.splitter.domain.Benutzer;
import com.splitterorg.splitter.domain.gruppe.Gruppe;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * Serialisiere eine Gruppe in einen ausfühlichen JSON-String der Form:
 *   {
 *     "gruppe" : "rolling_stones456",
 *     "name" : "Tour 2023",
 *     "personen" : [
 *       "Mick",
 *       "Keith",
 *       "Ronnie"
 *     ],
 *     geschlossen: false,
 *     "ausgaben" : [
 *       {
 *         "grund": "Black Paint",
 *         "glaeubiger": "Keith",
 *         "cent" : 2599,
 *         "schuldner" : [
 *           "Mick",
 *           "Keith",
 *           "Ronnie"
 *         ]
 *       }
 *     ]
 *   }
 * Wir brauchen dies, wenn wir eine Detailansicht einer Gruppe anzeigen wollen.
 * Für weniger Informationen siehe {@link GruppeShortSerializer}
 */
@JsonComponent
public class GruppeInfoSerializer extends JsonSerializer<Gruppe> {


  @Override
  public void serialize(Gruppe value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("gruppe", value.getId().toString());
    gen.writeStringField("name", value.getName());
    gen.writeArrayFieldStart("personen");
    for (Benutzer teilnehmer : value.getTeilnehmerliste()) {
      gen.writeString(teilnehmer.benutzername());
    }
    gen.writeEndArray();
    gen.writeBooleanField("geschlossen", !value.getOffen());
    gen.writeArrayFieldStart("ausgaben");
    for (Ausgabe ausgabe : value.getAusgabenliste()) {
      AusgabeSerializer ausgabenSerializer = new AusgabeSerializer();
      ausgabenSerializer.serialize(ausgabe, gen, serializers);
    }
  }
}
