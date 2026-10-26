package de.wps.ddd.kino.einlass.infrastructure.web;

import java.util.UUID;

public record KinokarteValidierenUndEntwertenRequest(
        UUID kinokartenId
) {
}
