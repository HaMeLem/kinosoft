package de.wps.ddd.kino.einlasskontrolle.domain;

public class EinlassAbgelehntException extends RuntimeException
{
    private final Ablehnungsgrund grund;

    public EinlassAbgelehntException(Ablehnungsgrund grund)
    {
        super(grund.meldung());
        this.grund = grund;
    }

    public Ablehnungsgrund getGrund()
    {
        return grund;
    }
}