create database course;

create table point(
    id_point serial not null primary key,
    point int
);

--------course
insert into course(date) values('2024-06-01');
---------admin
insert into equipe values('admin1',1,'admin1',default);


-------user
insert into equipe values('EquipeA',0,'equipeA',default);
insert into equipe values('EquipeB',0,'equipeB',default);
-------coureur
insert into coureur(nom,dossard,genre,naissance,id_equipe) values('Lova','1','Homme','2004-12-5',1);
insert into coureur(nom,dossard,genre,naissance,id_equipe) values('Sabrinah','4','Femme','2004-12-5',1);
insert into coureur(nom,dossard,genre,naissance,id_equipe) values('John','3','Homme','2000-12-5',1);

insert into coureur(nom,dossard,genre,naissance,id_equipe) values('Vero','5','Femme','1999-12-5',2);
insert into coureur(nom,dossard,genre,naissance,id_equipe) values('Jill','6','Homme','2000-12-5',2);
insert into coureur(nom,dossard,genre,naissance,id_equipe) values('Justin','2','Homme','1999-12-5',2);

-----categorie
insert into categorie(nom) values('Homme');
insert into categorie(nom) values('Femme');
insert into categorie(nom) values('Senior');
insert into categorie(nom) values('Junior');


----etapes
insert into etapes(nom, longueur, nombre_equipe, temps_depart, rang, id_course) values ('Etape 1 de Betsizaraina', 5.5, 1, '2024-06-01 08:00:00', 1, 1);
insert into etapes(nom, longueur, nombre_equipe, temps_depart, rang, id_course) values ('Etape 2 d Ampasimbe', 8.5, 1, '2024-06-01 10:00:00', 2, 1);
insert into etapes(nom, longueur, nombre_equipe, temps_depart, rang, id_course) values ('Etape 2 d Ampasimbe', 8.5, 1, '2024-06-01 10:05:25', 2, 1);
insert into etapes(nom, longueur, nombre_equipe, temps_depart, rang, id_course) values ('Etape 4 d Ampasimbe', 8.5, 3, '2024-06-01 10:05:25', 2, 1);

delete from resultat where id_coureur=4 and id_etape=6 and temps='00:00:00';
insert into resultat values(default,'00:00:00',' 2024-06-02 14:35:47+00',4,6,2,0);

----POINT

insert into point(point) values(10),(6),(4),(2),(1);

-----Trigger
    --------------After insert function
        CREATE OR REPLACE FUNCTION insert_resultat() RETURNS TRIGGER AS $$
        DECLARE
            temps TIME;
            etape RECORD;
            resultListe RECORD;
        BEGIN
            SELECT * INTO etape FROM etapes WHERE id_etape = NEW.id_etape;

            IF NEW.temps_arrive IS NULL THEN
                temps := '00:00:00'::TIME;
            ELSE
                temps := (NEW.temps_arrive - etape.temps_depart)::TIME;
            END IF;

            RAISE NOTICE 'TEMPS de %',temps;

            EXECUTE 'UPDATE resultat SET temps = $1 WHERE id_resultat = $2' USING temps, NEW.id_resultat;

            FOR resultListe IN 
                WITH ranked_result AS (
                    SELECT
                        res.id_resultat,
                        res.id_etape,
                        res.id_coureur,
                        res.temps,
                        res.temps_arrive,
                        res.id_equipe,
                        DENSE_RANK() OVER (ORDER BY res.temps ASC) AS rank
                     FROM resultat AS res WHERE id_etape=etape.id_etape
                ),
                ranked_point AS (
                    SELECT
                        point,
                        ROW_NUMBER() OVER () AS row_num
                    FROM point
                )
                    SELECT
                        res.id_resultat,
                        res.id_etape,
                        res.id_coureur,
                        res.temps,
                        res.temps_arrive,
                        COALESCE(rp.point, 0) AS point,
                        res.id_equipe
                    FROM ranked_result res
                    LEFT JOIN ranked_point rp ON res.rank = rp.row_num
                LOOP
                    IF resultListe.temps_arrive IS NULL AND resultListe.point <> 0 THEN
                        UPDATE resultat SET point = 0 WHERE id_resultat = resultListe.id_resultat;
                    ELSE
                        UPDATE resultat SET point = resultListe.point WHERE id_resultat = resultListe.id_resultat;
                    END IF;
                END LOOP;

            RETURN NEW;
        END;
        $$ LANGUAGE plpgsql;

    ------------------trigger execute
        CREATE TRIGGER after_insert_resultat
        AFTER INSERT ON resultat
        FOR EACH ROW
        EXECUTE FUNCTION insert_resultat();

    -------After update function
        CREATE OR REPLACE FUNCTION update_resultat() RETURNS TRIGGER AS $$
            DECLARE
                temps TIME;
                etape RECORD;
                resultListe RECORD;
            BEGIN
                SELECT * INTO etape FROM etapes WHERE id_etape = NEW.id_etape;
                RAISE NOTICE 'NEW.temps_arrive %', NEW.temps_arrive;
                IF NEW.temps_arrive IS NULL THEN
                    temps := '00:00:00'::TIME;
                ELSE
                    RAISE NOTICE 'TEMPS NORMAL %', (NEW.temps_arrive - etape.temps_depart);
                    temps := (NEW.temps_arrive - etape.temps_depart)::TIME;
                END IF;

                IF NEW.temps_arrive < etape.temps_depart THEN
                    RAISE EXCEPTION 'Temps d''arrive impossible. % est inferieur a %', NEW.temps_arrive, etape.temps_depart;
                END IF;
                RAISE NOTICE 'temps %', temps;

                EXECUTE 'UPDATE resultat SET temps = $1 WHERE id_resultat = $2' USING temps, NEW.id_resultat;

                FOR resultListe IN 
                WITH ranked_result AS (
                    SELECT
                        res.id_resultat,
                        res.id_etape,
                        res.id_coureur,
                        res.temps,
                        res.temps_arrive,
                        res.id_equipe,
                        DENSE_RANK() OVER (ORDER BY res.temps ASC) AS rank
                     FROM resultat AS res WHERE id_etape=etape.id_etape
                ),
                ranked_point AS (
                    SELECT
                        point,
                        ROW_NUMBER() OVER () AS row_num
                    FROM point
                )
                    SELECT
                        res.id_resultat,
                        res.id_etape,
                        res.id_coureur,
                        res.temps,
                        res.temps_arrive,
                        COALESCE(rp.point, 0) AS point,
                        res.id_equipe
                    FROM ranked_result res
                    LEFT JOIN ranked_point rp ON res.rank = rp.row_num
                LOOP
                    IF resultListe.temps_arrive IS NULL AND resultListe.point <> 0 THEN
                        UPDATE resultat SET point = 0 WHERE id_resultat = resultListe.id_resultat;
                    ELSE
                        UPDATE resultat SET point = resultListe.point WHERE id_resultat = resultListe.id_resultat;
                    END IF;
                END LOOP;
                RETURN NEW;
            END;
            $$ LANGUAGE plpgsql;

    ------------------trigger execute
        CREATE TRIGGER after_update_resultat
        AFTER UPDATE OF temps_arrive ON resultat
        FOR EACH ROW
        EXECUTE FUNCTION update_resultat();

update resultat set temps_arrive='2024-06-03 00:34:47+00' where id_etape=6 and id_coureur=4;