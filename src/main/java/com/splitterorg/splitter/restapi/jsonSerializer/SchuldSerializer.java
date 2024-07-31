package com.splitterorg.splitter.restapi.jsonSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.splitterorg.splitter.domain.Schuld;

import com.splitterorg.splitter.restapi.RestApiController;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Wenn mit {@link RestApiController#getAusgleich}
 * eine Liste von Schuldobjekten zurückgegeben wird, dann wird diese Klasse
 * verwendet, um die Schuldobjekte in JSON zu serialisieren.
 * Die Form der JSON-Datei ist in {@link RestApiController#getAusgleich}, also:
 *    [{"von" : "Mick", "an" : "Keith", "cents" : 866}, {"von" : "Ronnie", "an" : "Keith", "cents" : 866}]
 */
public class SchuldSerializer extends JsonSerializer<Schuld> {

  @Override
  public void serialize(Schuld value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    int cents = value.getBetrag().getNumberStripped().multiply(new BigDecimal(100)).intValue();
    gen.writeStartObject();
    gen.writeStringField("von", value.getGeber().benutzername());
    gen.writeStringField("an", value.erhalter().benutzername());
    gen.writeNumberField("cents", cents);
    gen.writeEndObject();
  }
}