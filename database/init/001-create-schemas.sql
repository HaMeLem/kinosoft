-- Die Rollenaufteilung ist vorbereitet, wird vom aktuellen Backend aber noch
-- nicht verwendet. Solange das Backend mit dem Benutzer "postgres" (Superuser) verbindet,
-- findet praktisch keine Zugriffstrennung zwischen den Modulen statt.
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'filmauswahl_app') THEN
        CREATE ROLE filmauswahl_app NOLOGIN;
    END IF;
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'kartenverkauf_app') THEN
        CREATE ROLE kartenverkauf_app NOLOGIN;
    END IF;
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'einlasskontrolle_app') THEN
        CREATE ROLE einlasskontrolle_app NOLOGIN;
    END IF;
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'nutzerverwaltung_app') THEN
        CREATE ROLE nutzerverwaltung_app NOLOGIN;
    END IF;
END
$$;

CREATE SCHEMA IF NOT EXISTS filmauswahl AUTHORIZATION filmauswahl_app;
CREATE SCHEMA IF NOT EXISTS kartenverkauf AUTHORIZATION kartenverkauf_app;
CREATE SCHEMA IF NOT EXISTS einlasskontrolle AUTHORIZATION einlasskontrolle_app;
CREATE SCHEMA IF NOT EXISTS nutzerverwaltung AUTHORIZATION nutzerverwaltung_app;

ALTER SCHEMA filmauswahl OWNER TO filmauswahl_app;
ALTER SCHEMA kartenverkauf OWNER TO kartenverkauf_app;
ALTER SCHEMA einlasskontrolle OWNER TO einlasskontrolle_app;
ALTER SCHEMA nutzerverwaltung OWNER TO nutzerverwaltung_app;

REVOKE ALL ON SCHEMA filmauswahl FROM PUBLIC;
REVOKE ALL ON SCHEMA kartenverkauf FROM PUBLIC;
REVOKE ALL ON SCHEMA einlasskontrolle FROM PUBLIC;
REVOKE ALL ON SCHEMA nutzerverwaltung FROM PUBLIC;
