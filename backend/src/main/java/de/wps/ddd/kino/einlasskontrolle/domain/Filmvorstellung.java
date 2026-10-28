package de.wps.ddd.kino.einlasskontrolle.domain;

import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@AggregateRoot
@Getter
public class Filmvorstellung
{
    @Identity
    private final VorstellungsId id;
    private final GeplanteStartzeit start;
    private final GeplanteEndzeit ende;

    public Filmvorstellung(
            VorstellungsId id,
            GeplanteStartzeit start,
            GeplanteEndzeit ende
    )
    {
        this.id = id;
        this.start = start;
        this.ende = ende;
    }
}
