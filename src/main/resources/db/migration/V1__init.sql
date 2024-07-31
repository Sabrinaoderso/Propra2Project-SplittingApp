drop table if exists gruppe_tabelle;

create table if not exists gruppe_tabelle
(
    gruppenname     varchar(50),
    gruppe_id       UUID primary key,
    version         int,
    offen           boolean
);

drop table if exists ausgabe_tabelle;

create table if not exists ausgabe_tabelle
(
    ausgabe_id serial primary key,
    geldgeber_in varchar(50),
    zweck varchar(80),
    betrag int,
    gruppe_tabelle uuid,
    version int,
    constraint ausgaben_gruppe foreign key (gruppe_tabelle) references gruppe_tabelle(gruppe_id)
);

drop table if exists teilnehmer_in_gruppe;

create table if not exists teilnehmer_in_gruppe
(
    teilnehmer_in_gruppe_id serial primary key /*not null*/,
    benutzername varchar(39),
    gruppe_tabelle uuid,
    version int,
    constraint teilnehmer_gruppe foreign key (gruppe_tabelle) references gruppe_tabelle(gruppe_id)
);

drop table if exists gelderhalter_innen;

create table if not exists gelderhalter_innen
(
    gelderhalter_innen_id serial primary key,
    benutzername varchar(39),
    ausgabe_tabelle int,
    constraint geldgeber_ausgaben foreign key (ausgabe_tabelle) references ausgabe_tabelle(ausgabe_id),
    /* warum ist das hier ?*/
    gruppe_tabelle uuid,
    version int,
    constraint geldgeber_gruppe foreign key (gruppe_tabelle) references gruppe_tabelle(gruppe_id)
);
