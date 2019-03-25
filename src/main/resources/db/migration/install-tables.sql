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
  schemaurl character varying(255) NOT NULL,
  namespace character varying(255) NOT NULL,
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
  pid character varying(50) NOT NULL,
  uid character varying(100) NOT NULL,
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
  lastmoddate timestamp with time zone,
  xmldata xml NOT NULL,
  deleted boolean NOT NULL DEFAULT false,
  id_record character varying(100) NOT NULL,
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


-- Table: public.resumption_tokens
-- DROP TABLE public.resumption_tokens;
CREATE TABLE public.resumption_tokens
(
  expiration_date timestamp with time zone NOT NULL,
  cursor bigint NOT NULL,
  token_id character varying(150) NOT NULL,
  format_id bigint NOT NULL,
  CONSTRAINT pk_token_id PRIMARY KEY (token_id),
  CONSTRAINT fk_id_format FOREIGN KEY (format_id)
      REFERENCES public.formats (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.resumption_tokens
  OWNER TO postgres;


-- Table: public.rst_to_identifiers
-- DROP TABLE public.rst_to_identifiers;
CREATE TABLE public.rst_to_identifiers
(
  record_id bigint NOT NULL,
  rst_id character varying(150) NOT NULL,
  CONSTRAINT fk_record_id FOREIGN KEY (record_id)
      REFERENCES public.records (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_rst_id FOREIGN KEY (rst_id)
      REFERENCES public.resumption_tokens (token_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.rst_to_identifiers
  OWNER TO postgres;


-- VIEW: public.oai_pmh_lists
-- DROP VIEW public.oai_pmh_lists;
CREATE OR REPLACE VIEW public.oai_pmh_lists AS
 SELECT rti.rst_id,
    rt.expiration_date,
    rt.format_id AS format,
    rc.uid,
    rc.id AS record_id,
    rc.deleted AS record_status,
    diss.lastmoddate,
    diss.xmldata,
    diss.deleted AS dissemination_status
   FROM rst_to_identifiers rti
     LEFT JOIN resumption_tokens rt ON rti.rst_id::text = rt.token_id::text
     LEFT JOIN records rc ON rti.record_id = rc.id
     LEFT JOIN disseminations diss ON rc.uid::text = diss.id_record::text;

ALTER TABLE public.oai_pmh_lists
  OWNER TO postgres;