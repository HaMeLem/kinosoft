package de.wps.ddd.kino.filmauswahl.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.List;

@AggregateRoot
@Entity
@Table(name = "filme", schema = "filmauswahl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {
    @Identity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @EqualsAndHashCode.Include
    private Long id;
    private String titel;
    private Integer laufzeit;
    private String posterUrl;
    private Integer fsk;
    @Lob
    private String beschreibung;
    private String genre;
    private String hauptdarsteller;
    private String regie;
    private String sprache;
    // Lesende Bequemlichkeits-Assoziation für die Programm-Abfrage (join fetch). Den Fremdschlüssel
    // filmId schreibt das gleichnamige Basisfeld auf Vorstellung; deshalb ist die Assoziation hier
    // schreibgeschützt (sonst würde die Spalte filmId doppelt gemappt).
    @OneToMany
    @JoinColumn(name = "filmId", insertable = false, updatable = false)
    private List<Vorstellung> vorstellungen;
}
