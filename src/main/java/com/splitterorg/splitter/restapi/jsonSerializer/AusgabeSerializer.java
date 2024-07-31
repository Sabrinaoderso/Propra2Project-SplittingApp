package com.splitterorg.splitter.restapi.jsonSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.splitterorg.splitter.domain.Ausgabe;
import com.splitterorg.splitter.domain.Benutzer;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Serialisiere Ausgaben in die Form:
 *   {"grund": "Black Paint", "glaeubiger": "Keith", "cent" : 2599, "schuldner" : ["Mick", "Keith", "Ronnie"]}
 */

public class AusgabeSerializer extends JsonSerializer<Ausgabe> {

  @Override
  public void serialize(Ausgabe value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    // nutze javamoney um die Cent zu berechnen
    int cents = value.getBetrag().getNumberStripped().multiply(new BigDecimal(100)).intValue();
    gen.writeStartObject();
    gen.writeStringField("grund", value.getZweck());
    gen.writeStringField("glaeubiger", value.getGeldgeberIn().getBenutzername());
    gen.writeNumberField("cent", cents);
    gen.writeArrayFieldStart("schuldner");
    for (Benutzer schuldner : value.getGelderhalterInnen()) {
      gen.writeString(schuldner.getBenutzername());
    }
    gen.writeEndArray();
    gen.writeEndObject();
  }
}
