-- DROP SEQUENCE public.oaiprovider;

CREATE SEQUENCE public.oaiprovider
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 211
  CACHE 1;
ALTER TABLE public.oaiprovider
  OWNER TO postgres;

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
  oaiid character varying(100) NOT NULL,
  pid character varying(100) NOT NULL,
  deleted boolean NOT NULL DEFAULT false,
  visible boolean NOT NULL DEFAULT false,
  CONSTRAINT record_pkey PRIMARY KEY (id),
  CONSTRAINT record_unique UNIQUE (oaiid)
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
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT dissemination_record_fkey FOREIGN KEY (id_record)
      REFERENCES public.records (oaiid) MATCH SIMPLE
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
      ON UPDATE NO ACTION ON DELETE CASCADE
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
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT fk_rst_id FOREIGN KEY (rst_id)
      REFERENCES public.resumption_tokens (token_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.rst_to_identifiers
  OWNER TO postgres;

-- VIEW: public.oai_pmh_list
-- DROP VIEW public.oai_pmh_list;
CREATE OR REPLACE VIEW public.oai_pmh_list AS
 SELECT rc.id AS record_id,
    rc.oaiid,
    rc.deleted AS record_status,
    f.id AS format_id,
    f.mdprefix,
    diss.lastmoddate,
    diss.xmldata,
    diss.deleted AS diss_status,
    ( SELECT json_agg(json_build_object('setspec', st.setspec, 'setname', st.setname)) AS json_agg
           FROM sets st
             LEFT JOIN sets_to_records str ON str.id_set = st.id
          WHERE str.id_record = rc.id) AS set
   FROM records rc
     LEFT JOIN disseminations diss ON rc.oaiid::text = diss.id_record::text
     LEFT JOIN formats f ON diss.id_format = f.id
   WHERE rc.visible = true;

ALTER TABLE public.oai_pmh_list
  OWNER TO postgres;

-- VIEW: public.oai_pmh_list_by_token
-- DROP VIEW public.oai_pmh_list_by_token;
CREATE OR REPLACE VIEW public.oai_pmh_list_by_token AS
 SELECT rti.rst_id,
    rt.expiration_date,
    rc.oaiid,
    rc.id AS record_id,
    rc.deleted AS record_status,
    diss.lastmoddate,
    diss.xmldata,
    diss.deleted AS dissemination_status,
    fm.id AS format,
    ( SELECT json_agg(json_build_object('setspec', st.setspec, 'setname', st.setname)) AS json_agg
           FROM sets st
             LEFT JOIN sets_to_records str ON str.id_set = st.id
          WHERE str.id_record = rc.id) AS set
   FROM rst_to_identifiers rti
     LEFT JOIN resumption_tokens rt ON rti.rst_id::text = rt.token_id::text
     LEFT JOIN records rc ON rti.record_id = rc.id
     LEFT JOIN disseminations diss ON rc.oaiid::text = diss.id_record::text
     LEFT JOIN formats fm ON fm.id = diss.id_format
   WHERE rc.visible = true;

ALTER TABLE public.oai_pmh_list_by_token
  OWNER TO postgres;