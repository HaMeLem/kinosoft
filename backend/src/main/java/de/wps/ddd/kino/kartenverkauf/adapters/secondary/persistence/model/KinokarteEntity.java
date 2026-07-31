package de.wps.ddd.kino.kartenverkauf.adapters.secondary.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kinokarten", schema = "kartenverkauf")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KinokarteEntity {
    @Id
    @EqualsAndHashCode.Include
    private UUID kartenId;
    private UUID auftragsnummer;
    private UUID vorstellungId;
    private String filmName;
    private LocalDateTime beginn;
    private String saalName;
    private int reihe;
    private int platz;
}
