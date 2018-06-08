-- Table: public.sets
-- DROP TABLE public.sets;
CREATE TABLE public.sets
(
  id bigint NOT NULL,
  setspec character varying(100) NOT NULL,
  setname character varying(100) NOT NULL,
  setdescription character varying(500),
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
  schemaurl character NOT NULL,
  namespace character NOT NULL,
  deleted boolean NOT NULL DEFAULT false,
  CONSTRAINT formats_pkey PRIMARY KEY (id),
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
  uid character varying(400) NOT NULL,
  datestamp timestamp with time zone,
  deleted boolean NOT NULL DEFAULT false,
  CONSTRAINT record_pkey PRIMARY KEY (id),
  CONSTRAINT record_unique UNIQUE (pid),
  CONSTRAINT record_uid_unique UNIQUE (uid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.records
  OWNER TO postgres;


-- Table: public.disseminations
-- DROP TABLE public.disseminations;
CREATE TABLE public.disseminations
(
  id bigint NOT NULL,
  id_format bigint NOT NULL,
  lastmoddate date,
  xmldata xml NOT NULL,
  deleted boolean NOT NULL DEFAULT false,
  id_record character varying(400) NOT NULL,
  CONSTRAINT dissemination_pkey PRIMARY KEY (id),
  CONSTRAINT dissemination_format_fkey FOREIGN KEY (id_format)
      REFERENCES public.formats (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT dissemination_record_fkey FOREIGN KEY (id_record)
      REFERENCES public.records (uid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.disseminations
  OWNER TO postgres;


-- Table: public.sets_to_records
-- DROP TABLE public.sets_to_records;
CREATE TABLE public.sets_to_records
(
  id_set bigint NOT NULL,
  id_record bigint NOT NULL,
  CONSTRAINT str_record_fkey FOREIGN KEY (id_record)
      REFERENCES public.records (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT str_set_fkey FOREIGN KEY (id_set)
      REFERENCES public.sets (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.sets_to_records
OWNER TO postgres;