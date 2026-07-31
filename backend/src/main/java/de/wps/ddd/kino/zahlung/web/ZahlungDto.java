package de.wps.ddd.kino.zahlung.web;

record ZahlungDto(String referenz, long betragInCent, String status) {
}
