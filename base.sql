create database course;

create table point(
    id_point serial not null primary key,
    classement int,
    point int
);

UPDATE POINT SET classement=1 WHERE id_point=6;
UPDATE POINT SET classement=2 WHERE id_point=7; 
UPDATE POINT SET classement=3 WHERE id_point=8; 
UPDATE POINT SET classement=4 WHERE id_point=9; 
UPDATE POINT SET classement=5 WHERE id_point=10; 


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
insert into categorie(nom) values('H');
insert into categorie(nom) values('F');
insert into categorie(nom) values('Senior');
insert into categorie(nom) values('Junior');

CREATE TABLE categorie_coureur(
    id_categorie int REFERENCES categorie(id_categorie),
    id_coureur int REFERENCES coureur(id_coureur)
);

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

            temps := (NEW.temps_arrive - etape.temps_depart)::TIME;

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
                        classement AS row_num
                    FROM point ORDER BY classement
                )
                    SELECT
                        res.id_resultat,
                        res.id_etape,
                        res.id_coureur,
                        res.temps,
                        res.temps_arrive,
                        rp.point,
                        res.id_equipe
                    FROM ranked_result res
                    LEFT JOIN ranked_point rp ON res.rank = rp.row_num
                LOOP
                    IF resultListe.temps_arrive IS NULL AND resultListe.point <> 0 THEN
                        UPDATE resultat SET point = 0 WHERE id_resultat = resultListe.id_resultat;
                    ELSE
                        IF resultListe.point IS NULL THEN
                            UPDATE resultat SET point = 0 WHERE id_resultat = resultListe.id_resultat;
                        ELSE
                            UPDATE resultat SET point = resultListe.point WHERE id_resultat = resultListe.id_resultat;
                        END IF;
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
                        classement AS row_num
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

update resultat set temps_arrive=NULL;

select * from resultat


INSERT INTO coureur (dossard,nom,genre,naissance,id_equipe) SELECT DISTINCT 
numero_dossard,nom,genre,date_naissance,id_equipe
FROM tempresultatcsv
JOIN equipe ON tempresultatcsv.equipe=equipe.authentification;


SELECT 
arrivee,coureur.id_coureur,etape_rang,equipe ,id_etape,coureur.id_equipe
from tempresultatcsv
join coureur on tempresultatcsv.numero_dossard=coureur.dossard
join etapes on tempresultatcsv.etape_rang=etapes.rang



SELECT id_coureur,SUM(point) as point,CAST(SUM(r.temps) AS VARCHAR) as temps FROM resultat GROUP BY id_coureur;


CREATE OR REPLACE FUNCTION updateCategorieCoureur() RETURNS VOID AS $$
DECLARE
    table_coureur coureur%ROWTYPE;
    categorie_genre categorie%ROWTYPE;
BEGIN
    FOR table_coureur IN
        SELECT *
        FROM coureur
    LOOP
        SELECT * INTO categorie_genre FROM categorie WHERE nom = table_coureur.genre;
        RAISE NOTICE '%',categorie_genre;
        EXECUTE 'INSERT INTO categorie_coureur(id_coureur,id_categorie) VALUES ($1, $2)' USING table_coureur.id_coureur, categorie_genre.id_categorie;
        IF date_part('year', age(table_coureur.naissance)) < 18 THEN
            EXECUTE 'INSERT INTO categorie_coureur(id_coureur,id_categorie) VALUES ($1, 7)' USING table_coureur.id_coureur;
        ELSIF date_part('year', age(table_coureur.naissance)) >= 18 THEN
            EXECUTE 'INSERT INTO categorie_coureur(id_coureur,id_categorie) VALUES ($1, 8)' USING table_coureur.id_coureur;
        END IF;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

SELECT id_equipe,SUM(point) point FROM resultat GROUP BY id_equipe;


WITH resultRank AS (
    SELECT id_resultat, temps, resultat.id_coureur, id_etape, id_equipe, id_categorie, DENSE_RANK() OVER (ORDER BY resultat.temps ASC) AS rank
    FROM resultat
    WHERE resultRank.id_etape = 142 AND resultRank.id_categorie = 8
    JOIN categorie_coureur ON resultat.id_coureur = categorie_coureur.id_coureur
),
pointRank AS (
    SELECT point, classement
    FROM point
)
SELECT 
resultRank.id_resultat, 
resultRank.temps, 
resultRank.id_coureur, 
resultRank.id_etape, 
resultRank.id_equipe, 
resultRank.id_categorie, 
COALESCE(pointRank.point, 0),
resultRank.rank,
pointRank.classement
FROM resultRank
LEFT JOIN pointRank ON resultRank.rank = pointRank.classement
ORDER BY resultRank.temps;



WITH resultRank AS (
    SELECT 
        id_resultat, 
        temps, 
        resultat.id_coureur, 
        id_etape, 
        id_equipe, 
        id_categorie, 
        DENSE_RANK() OVER (ORDER BY resultat.temps ASC) AS rank
    FROM resultat
    JOIN categorie_coureur ON resultat.id_coureur = categorie_coureur.id_coureur
    WHERE id_categorie=5 AND id_etape=144
),
pointRank AS (
    SELECT point, classement
    FROM point
)
SELECT 
    resultRank.id_resultat,
    resultRank.temps, 
    resultRank.id_coureur, 
    resultRank.id_etape, 
    resultRank.id_equipe, 
    resultRank.id_categorie, 
    COALESCE(pointRank.point, 0) AS point
FROM resultRank
LEFT JOIN pointRank ON resultRank.rank = pointRank.classement
ORDER BY resultRank.temps;


WITH resultRank AS (
    SELECT 
        id_resultat, 
        temps, 
        resultat.id_coureur, 
        id_etape, 
        id_equipe, 
        id_categorie, 
        DENSE_RANK() OVER (ORDER BY resultat.temps ASC) AS rank
    FROM resultat
    JOIN categorie_coureur ON resultat.id_coureur = categorie_coureur.id_coureur
    WHERE id_etape = 140 AND id_categorie = 8
),
pointRank AS (
    SELECT point, classement
    FROM point
)
SELECT 
    resultRank.id_equipe,
    MIN(resultRank.temps) AS min_temps,
    MAX(resultRank.temps) AS max_temps,
    COALESCE(SUM(pointRank.point), 0) AS total_point
FROM resultRank
LEFT JOIN pointRank ON resultRank.rank = pointRank.classement
GROUP BY resultRank.id_equipe
ORDER BY min_temps;