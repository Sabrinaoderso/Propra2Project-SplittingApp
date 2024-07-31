package com.splitterorg.splitter.persistence;

import java.util.Set;

import org.springframework.data.annotation.Id;

/**
 * Record AusgabeTabelle:
 * Datenspeicherobjekt zum Speichern einer AusgabeTabelle.
 * Speichert Geldgeber, eine Liste von GelderhalterInnen, einen Ausgabenzweck und einen Betrag ab.
 */
public record AusgabeTabelle(@Id int ausgabe_id,
                             String geldgeberIn,
                             Set<GelderhalterInnen> gelderhalterInnen,
                             String zweck,
                             int betrag) {


}
