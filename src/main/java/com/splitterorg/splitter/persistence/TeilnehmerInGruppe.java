package com.splitterorg.splitter.persistence;

import org.springframework.data.annotation.Id;

public record TeilnehmerInGruppe(@Id int teilnehmer_in_gruppe_id, String benutzername) {
}
