-- Table: public.sets
-- DROP TABLE public.sets;

CREATE TABLE public.sets
(
  id bigint NOT NULL,
  setspec character varying(500) NOT NULL,
  predicate character varying(50) NOT NULL,
  doc xml,
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
  disstype character varying(50) NOT NULL,
  CONSTRAINT formats_pkey PRIMARY KEY (id),
  CONSTRAINT disstype_unique UNIQUE ("dissType"),
  CONSTRAINT mdprefix_unique UNIQUE (mdprefix)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.formats
  OWNER TO postgres;

  
-- Table: public.identifier
-- DROP TABLE public.identifier;

CREATE TABLE public.identifier
(
  id bigint NOT NULL,
  identifier character varying(255) NOT NULL,
  datestamp timestamp with time zone,
  CONSTRAINT identifier_pkey PRIMARY KEY (id),
  CONSTRAINT identifier_unique UNIQUE (identifier)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.identifier
  OWNER TO postgres;
  
  
-- Table: public.records
-- DROP TABLE public.records;

CREATE TABLE public.records
(
  id bigint NOT NULL,
  identifier_id bigint NOT NULL,
  format bigint NOT NULL,
  moddate date,
  xmldata xml NOT NULL,
  CONSTRAINT record_pkey PRIMARY KEY (id),
  CONSTRAINT format_fkey FOREIGN KEY (format)
      REFERENCES public.formats (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT identifier_fkey FOREIGN KEY (identifier_id)
      REFERENCES public.identifier (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)

  
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

  
-- procedure for inserts sets to records id combines  
CREATE OR REPLACE FUNCTION generate_sets_to_records() returns void AS $$
BEGIN
  CREATE temp table sets_from_records(id_record bigint, set_spec varchar(150));
  insert into sets_from_records(id_record, set_spec) 
  SELECT id, unnest(xpath('//record/header/setSpec/text()', xmldata)) AS spec FROM records;
  DELETE FROM sets_to_records;
  INSERT INTO sets_to_records (id_set, id_record)
  SELECT s.id, sfr.id_record FROM sets_from_records sfr 
  LEFT JOIN sets s ON s.setspec = sfr.set_spec;
  DROP TABLE sets_from_records;
END;
$$ language plpgsql;
