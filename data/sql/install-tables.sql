-- Table: public.sets
-- DROP TABLE public.sets;
CREATE TABLE public.sets
(
  id bigint NOT NULL,
  setspec character varying(500) NOT NULL,
  predicate character varying(50) NOT NULL,
  doc xml,
  deleted boolean NOT NULL DEFAULT false,
  CONSTRAINT sets_pkey PRIMARY KEY (id),
  CONSTRAINT setspec_unique UNIQUE (setspec)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.sets
  OWNER TO postgres;


-- Table: public.formats
-- DROP TABLE public.formats;
CREATE TABLE public.formats
(
  id bigint NOT NULL,
  mdprefix character varying(255) NOT NULL,
  lastpolldate timestamp with time zone,
  deleted boolean NOT NULL DEFAULT false,
  CONSTRAINT formats_pkey PRIMARY KEY (id),
  CONSTRAINT disstype_unique UNIQUE ("disstype"),
  CONSTRAINT mdprefix_unique UNIQUE (mdprefix)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.formats
  OWNER TO postgres;


-- Table: public.records
-- DROP TABLE public.records;
CREATE TABLE public.records
(
  id bigint NOT NULL,
  pid character varying(255) NOT NULL,
  datestamp timestamp with time zone,
  deleted boolean NOT NULL DEFAULT false,
  CONSTRAINT record_pkey PRIMARY KEY (id),
  CONSTRAINT record_unique UNIQUE (record)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.records
  OWNER TO postgres;


-- Table: public.dissemnitations
-- DROP TABLE public.dissemnitations;
CREATE TABLE public.dissemnitations
(
  id bigint NOT NULL,
  id_record bigint NOT NULL,
  id_format bigint NOT NULL,
  lastmoddate date,
  xmldata xml NOT NULL,
  deleted boolean NOT NULL DEFAULT false,
  CONSTRAINT dissemnitation_pkey PRIMARY KEY (id),
  CONSTRAINT format_fkey FOREIGN KEY (id_format)
      REFERENCES public.formats (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT record_fkey FOREIGN KEY (id_record)
      REFERENCES public.records (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.dissemnitations
  OWNER TO postgres;


-- Table: public.sets_to_records
-- DROP TABLE public.sets_to_records;
CREATE TABLE public.sets_to_records
(
  id_set bigint NOT NULL,
  id_record bigint NOT NULL,
  CONSTRAINT str_record_fkey FOREIGN KEY (id_record)
      REFERENCES public.records (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT str_set_fkey FOREIGN KEY (id_set)
      REFERENCES public.sets (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.sets_to_records
  OWNER TO postgres;


-- Function: public.generate_sets_to_records()

-- DROP FUNCTION public.generate_sets_to_records();

CREATE OR REPLACE FUNCTION public.generate_sets_to_records()
  RETURNS void AS
$BODY$BEGIN
  CREATE temp table sets_from_records(id_record bigint, set_spec varchar(150));
  insert into sets_from_records(id_record, set_spec)
  SELECT id_record, unnest(xpath('//record/header/setSpec/text()', xmldata)) AS spec FROM disseminations;
  DELETE FROM sets_to_records;
  INSERT INTO sets_to_records (id_set, id_record)
  SELECT s.id, sfr.id_record FROM sets_from_records sfr
  LEFT JOIN sets s ON s.setspec = sfr.set_spec
  WHERE s.id is not null;
  DROP TABLE sets_from_records;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.generate_sets_to_records()
  OWNER TO postgres;