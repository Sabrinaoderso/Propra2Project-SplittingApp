package com.splitterorg.splitter.restapi.jsonSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.splitterorg.splitter.domain.Benutzer;
import com.splitterorg.splitter.domain.gruppe.Gruppe;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * Serialisiere eine Gruppe in einen kurzen JSON-String der Form:
 *   {"gruppe" : "rolling_stones456", "name" : "Tour 2023", "personen" : ["Mick", "Keith", "Ronnie"]}
 * Wir brauchen dies, wenn wir eine Übersicht aller Gruppen eines Benutzer anzeigen wollen.
 * Für genauere Informationen siehe {@link GruppeInfoSerializer}
 */
@JsonComponent
public class GruppeShortSerializer extends JsonSerializer<Gruppe> {

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
    gen.writeEndObject();
  }
}
