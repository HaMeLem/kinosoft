package de.wps.ddd.kino.einlasskontrolle.infrastructure.web;

import java.util.UUID;

public record KinokarteValidierenUndEntwertenRequest(
        UUID kinokartenId
) {
}
