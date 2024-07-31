package com.splitterorg.splitter.persistence;

import org.springframework.data.annotation.Id;

public record GelderhalterInnen(@Id int gelderhalter_innen_id, String benutzername) {
}
