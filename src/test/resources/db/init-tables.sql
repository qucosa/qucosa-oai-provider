--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.8
-- Dumped by pg_dump version 9.5.8

-- Started on 2019-07-01 11:04:31 CEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 12395)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2308 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 267 (class 1259 OID 32062)
-- Name: disseminations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE disseminations (
    id bigint NOT NULL,
    id_format bigint NOT NULL,
    lastmoddate timestamp with time zone,
    xmldata xml NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    id_record character varying(400) NOT NULL
);


ALTER TABLE disseminations OWNER TO postgres;

--
-- TOC entry 273 (class 1259 OID 34501)
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE flyway_schema_history OWNER TO postgres;

--
-- TOC entry 265 (class 1259 OID 32041)
-- Name: formats; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE formats (
    id bigint NOT NULL,
    mdprefix character varying(255) NOT NULL,
    schemaurl character varying(255) NOT NULL,
    namespace character varying(100) NOT NULL,
    deleted boolean DEFAULT false NOT NULL
);


ALTER TABLE formats OWNER TO postgres;

--
-- TOC entry 266 (class 1259 OID 32049)
-- Name: records; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE records (
    id bigint NOT NULL,
    pid character varying(255) NOT NULL,
    oaiid character varying(400) NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    visible boolean NOT NULL DEFAULT false
);


ALTER TABLE records OWNER TO postgres;

--
-- TOC entry 264 (class 1259 OID 32030)
-- Name: sets; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE sets (
    id bigint NOT NULL,
    setspec character varying(100) NOT NULL,
    setname character varying(100) NOT NULL,
    setdescription character varying(500),
    deleted boolean DEFAULT false NOT NULL
);


ALTER TABLE sets OWNER TO postgres;

--
-- TOC entry 268 (class 1259 OID 32081)
-- Name: sets_to_records; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE sets_to_records (
    id_set bigint NOT NULL,
    id_record bigint NOT NULL
);


ALTER TABLE sets_to_records OWNER TO postgres;

--
-- TOC entry 272 (class 1259 OID 34481)
-- Name: oai_pmh_list; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oai_pmh_list AS
 SELECT rc.id AS record_id,
    rc.pid,
    rc.oaiid,
    rc.deleted AS record_status,
    rc.visible,
    f.id AS format_id,
    f.mdprefix,
    diss.lastmoddate,
    diss.xmldata,
    diss.deleted AS diss_status,
    ( SELECT json_agg(json_build_object('setspec', st.setspec, 'setname', st.setname)) AS json_agg
           FROM (sets st
             LEFT JOIN sets_to_records str ON ((str.id_set = st.id)))
          WHERE (str.id_record = rc.id)) AS set
   FROM ((records rc
     LEFT JOIN disseminations diss ON (((rc.oaiid)::text = (diss.id_record)::text)))
     LEFT JOIN formats f ON ((diss.id_format = f.id)));


ALTER TABLE oai_pmh_list OWNER TO postgres;

--
-- TOC entry 269 (class 1259 OID 34196)
-- Name: resumption_tokens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE resumption_tokens (
    expiration_date timestamp with time zone NOT NULL,
    cursor bigint NOT NULL,
    token_id character varying(150) NOT NULL,
    format_id bigint NOT NULL
);


ALTER TABLE resumption_tokens OWNER TO postgres;

--
-- TOC entry 270 (class 1259 OID 34201)
-- Name: rst_to_identifiers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE rst_to_identifiers (
    record_id bigint NOT NULL,
    rst_id character varying(150) NOT NULL
);


ALTER TABLE rst_to_identifiers OWNER TO postgres;

--
-- TOC entry 271 (class 1259 OID 34476)
-- Name: oai_pmh_list_by_token; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oai_pmh_list_by_token AS
 SELECT rti.rst_id,
    rt.expiration_date,
    rc.oaiid,
    rc.id AS record_id,
    rc.deleted AS record_status,
    rc.visible,
    diss.lastmoddate,
    diss.xmldata,
    diss.deleted AS dissemination_status,
    fm.id AS format,
    ( SELECT json_agg(json_build_object('setspec', st.setspec, 'setname', st.setname)) AS json_agg
           FROM (sets st
             LEFT JOIN sets_to_records str ON ((str.id_set = st.id)))
          WHERE (str.id_record = rc.id)) AS set
   FROM ((((rst_to_identifiers rti
     LEFT JOIN resumption_tokens rt ON (((rti.rst_id)::text = (rt.token_id)::text)))
     LEFT JOIN records rc ON ((rti.record_id = rc.id)))
     LEFT JOIN disseminations diss ON (((rc.oaiid)::text = (diss.id_record)::text)))
     LEFT JOIN formats fm ON ((fm.id = diss.id_format)));


ALTER TABLE oai_pmh_list_by_token OWNER TO postgres;

--
-- TOC entry 263 (class 1259 OID 19391)
-- Name: oaiprovider; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE oaiprovider
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE oaiprovider OWNER TO postgres;

--
-- TOC entry 2296 (class 0 OID 32062)
-- Dependencies: 267
-- Data for Name: disseminations; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (21, 17, '2019-03-01 13:53:21.063+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Advanced symptoms are associated with myocardial damage in patients with severe aortic stenosis</dc:title>
   <dc:identifier>urn:nbn:de:bsz:15-qucosa2-323948</dc:identifier>
   <dc:language>eng</dc:language>
   <dc:relation>10.1016/j.jjcc.2016.10.006</dc:relation>
   <dc:description>Background: Once aortic stenosis (AS) is severe, patients develop symptoms at different stages. Indeed, symptom status may correlate poorly with the grade of valve narrowing. Multiple pathophysiological mechanisms, other than valvular load, may explain the link between AS and symptom severity. We aimed to describe the relationship between the severity of symptoms and the characteristics of a cohort of patients with severe AS already referred for aortic valve replacement (AVR).
        Methods: We analyzed 118 consecutive patients (70 ± 9 years, 55% men) with severe AS referred for AVR. We identified 84 patients with New York Heart Association (NYHA) I–II, and 34 with NYHA III–IV symptoms. Clinical and echocardiographic parameters were compared between these two groups. Left ventricular ejection fraction (LVEF), global longitudinal peak systolic strain (GLPS), NT-pro-B-type natriuretic peptide (BNP), and high-sensitive troponin T (hs-TNT) were determined at the time of admission.
        Results: AS severity was similar between groups. Compared with the NYHA I–II group, patients in NYHA III–IV group were older and more likely to have comorbidities, worse intracardiac hemodynamics and more LV damage. Variables independently associated with NYHA III–IV symptomatology were the absence of sinus rhythm, higher E/e0 ratio, and increased hs-TNT. GLPS showed a good correlation not only with hs-TNT as a marker of myocardial damage, but also with markers of increased afterload imposed on LV, being not directly related with advanced symptoms.
        Conclusions: Advanced symptoms in patients with severe AS referred for AVR are associated with worse intracardiac hemodynamics, absence of sinus rhythm, and more myocardial damage. It supports the concept of transition from adaptive LV remodeling to myocyte death as an important determinant of symptoms of heart failure.
    :Einführung................................................................................................3 Publikationsmanuskript...........................................................................12 Zusammenfassung..................................................................................19 Literaturverzeichnis..................................................................................24
        Erklärung über die eigenständige Abfassung der Arbeit.........................28
        Darstellung des eigenen Beitrags............................................................29
        Curriculum vitae.......................................................................................30 Danksagung.............................................................................................36
    </dc:description>
   <dc:subject>Aortic stenosis, myocardial damage, troponin</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/610</dc:subject>
   <dc:creator>Spampinato Torcivia,Ricardo</dc:creator>
   <dc:contributor>Universität Leipzig</dc:contributor>
   <dc:date>2018-03-08</dc:date>
   <dc:date>2018-11-08</dc:date>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2018-12-11</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>
                    doctoralThesis
                </dc:type>
</oai_dc:dc>', false, 'qucosa:32394');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (27, 22, '2019-03-01 13:53:21.063+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="eng" xsi:type="ddb:titleISO639-2">Advanced symptoms are associated with myocardial damage in patients with severe aortic stenosis</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Ricardo</pc:foreName>
            <pc:surName>Spampinato Torcivia</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Aortic stenosis</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">myocardial damage</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">troponin</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">610</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">610</dc:subject>
   <dcterms:tableOfContents ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">Einführung................................................................................................3 Publikationsmanuskript...........................................................................12 Zusammenfassung..................................................................................19 Literaturverzeichnis..................................................................................24
        Erklärung über die eigenständige Abfassung der Arbeit.........................28
        Darstellung des eigenen Beitrags............................................................29
        Curriculum vitae.......................................................................................30 Danksagung.............................................................................................36
    </dcterms:tableOfContents>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">Background: Once aortic stenosis (AS) is severe, patients develop symptoms at different stages. Indeed, symptom status may correlate poorly with the grade of valve narrowing. Multiple pathophysiological mechanisms, other than valvular load, may explain the link between AS and symptom severity. We aimed to describe the relationship between the severity of symptoms and the characteristics of a cohort of patients with severe AS already referred for aortic valve replacement (AVR).
        Methods: We analyzed 118 consecutive patients (70 ± 9 years, 55% men) with severe AS referred for AVR. We identified 84 patients with New York Heart Association (NYHA) I–II, and 34 with NYHA III–IV symptoms. Clinical and echocardiographic parameters were compared between these two groups. Left ventricular ejection fraction (LVEF), global longitudinal peak systolic strain (GLPS), NT-pro-B-type natriuretic peptide (BNP), and high-sensitive troponin T (hs-TNT) were determined at the time of admission.
        Results: AS severity was similar between groups. Compared with the NYHA I–II group, patients in NYHA III–IV group were older and more likely to have comorbidities, worse intracardiac hemodynamics and more LV damage. Variables independently associated with NYHA III–IV symptomatology were the absence of sinus rhythm, higher E/e0 ratio, and increased hs-TNT. GLPS showed a good correlation not only with hs-TNT as a marker of myocardial damage, but also with markers of increased afterload imposed on LV, being not directly related with advanced symptoms.
        Conclusions: Advanced symptoms in patients with severe AS referred for AVR are associated with worse intracardiac hemodynamics, absence of sinus rhythm, and more myocardial damage. It supports the concept of transition from adaptive LV remodeling to myocyte death as an important determinant of symptoms of heart failure.
    </dcterms:abstract>
   <dcterms:dateSubmitted xsi:type="dcterms:W3CDTF">2018-03-08</dcterms:dateSubmitted>
   <dcterms:dateAccepted xsi:type="dcterms:W3CDTF">2018-11-08</dcterms:dateAccepted>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-12-11</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-03-01</dcterms:modified>
   <dc:type xsi:type="dini:PublType">doctoralThesis</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:15-qucosa2-323948</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">eng</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>thesis.doctoral</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Universität Leipzig</cc:name>
            <cc:place>Leipzig</cc:place>
            <cc:department>
               <cc:name>Medizin</cc:name>
            </cc:department>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://ubl.example.com/qucosa:32394/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:15-qucosa2-323948</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:32394');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (29, 17, '2019-03-01 14:34:37.715+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>New journal for the promotion of Vietnamese environmental research:Editorial</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-88127</dc:identifier>
   <dc:identifier>372029159</dc:identifier>
   <dc:language>eng</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa-88705</dc:relation>
   <dc:description>The Vietnamese science and high education system plays a major role in the country’s social and economical development. Due to a mixed influence of international education systems, the contribution of the Vietnamese research to the international scientific landscape is still modest. Over the past decades, the most scientific programmes focused mostly on rather theoretical sciences and less on applied sciences. The results are reflected by a rather low rate of international publications on experimental science. Together with the country’s efforts on the efficient use of natural resources, there is an urgent demand for strengthening the scientific activity on environmental sciences. The new Journal of Vietnamese Environment was created to respond to the increasing interest in environmental research. The journal was founded as part of an academic network initiated by the Dresden University of Technology in the framework of Vietnamese-German cooperation programs on training and education. With multidisciplinary fields of interest and several types of manuscripts, the journal has a predominant academic character, the submission of manuscripts is open to students, graduates, researchers and staff members of research and academic institutions, as well as to any individual willing to disseminate the knowledge about the management of Vietnamese environment.</dc:description>
   <dc:description>Hệ thống khoa học và giáo dục đại học Việt Nam đóng vai trò quan trọng trong sự phát triển kinh tế và xã hội của đất nước. Trong xu hướng giao thoa mạnh mẽ giữa các hệ thống giáo dục quốc tế, đóng góp của các nhà nghiên cứu ở Việt Nam cho cộng đồng khoa học quốc tế còn khiêm tốn. Trong những thập niên qua, hầu hết các hoạt động khoa học tập trung vào khoa học lý thuyết hơn là các lĩnh vực khoa học ứng dụng. Điều này đã được phản ánh qua tỷ lệ khá thấp các ấn phẩm quốc tế về khoa học thực nghiệm. Cùng với những nỗ lực của đất nước để sử dụng hiệu quả các nguồn tài nguyên thiên nhiên, một nhu cầu cấp bách đặt ra là tăng cường các hoạt động nghiên cứu về khoa học môi trường. Tạp chí Môi trường Việt Nam ra đời nhằm hưởng ứng sự quan tâm ngày một gia tăng trong nghiên cứu môi trường. Tạp chí được thành lập như một phần của mạng lưới học thuật được đề xuất bởi Trường Đại học Tổng hợp Kỹ Thuật Dresden trong khuôn khổ chương trình hợp tác Việt Nam - CHLB Đức về đào tạo và giáo dục. Với mối quan tâm đa ngành và đa dạng trong ấn phẩm, tạp chí chủ yếu mang tính học thuật, cơ hội gửi đăng bài viết mở rộng cho cả sinh viên, kỹ sư / cử nhân, nghiên cứu viên và các thành viên của Viện nghiên cứu và giáo dục, các cá nhân có mong muốn phổ biến kiến thức về quản lý môi trường ở Việt Nam.</dc:description>
   <dc:subject>info:eu-repo/classification/ddc/363</dc:subject>
   <dc:subject>education system, research, publication, journal, environment</dc:subject>
   <dc:creator>Stefan,Catalin</dc:creator>
   <dc:publisher>Technische Universität Dresden</dc:publisher>
   <dc:date>2012-08-06</dc:date>
   <dc:source>Journal of Vietnamese Environment, 2011, Vol. 1, No. 1, pp. 1-4</dc:source>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>article</dc:type>
</oai_dc:dc>', false, 'qucosa:24994');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (30, 22, '2019-03-01 14:34:37.715+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="eng" xsi:type="ddb:titleISO639-2">New journal for the promotion of Vietnamese environmental research</dc:title>
   <dcterms:alternative xml:lang="eng" xsi:type="ddb:talternativeISO639-2">Editorial</dcterms:alternative>
   <dcterms:alternative ddb:type="translated" xml:lang="vie" xsi:type="ddb:titleISO639-2">Tạp chí mới khuyến khích nghiên cứu môi trường ở Việt Nam</dcterms:alternative>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Catalin</pc:foreName>
            <pc:surName>Stefan</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="dcterms:DDC">363</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">363</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AR 11900</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">education system</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">research</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">publication</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">journal</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">environment</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">The Vietnamese science and high education system plays a major role in the country’s social and economical development. Due to a mixed influence of international education systems, the contribution of the Vietnamese research to the international scientific landscape is still modest. Over the past decades, the most scientific programmes focused mostly on rather theoretical sciences and less on applied sciences. The results are reflected by a rather low rate of international publications on experimental science. Together with the country’s efforts on the efficient use of natural resources, there is an urgent demand for strengthening the scientific activity on environmental sciences. The new Journal of Vietnamese Environment was created to respond to the increasing interest in environmental research. The journal was founded as part of an academic network initiated by the Dresden University of Technology in the framework of Vietnamese-German cooperation programs on training and education. With multidisciplinary fields of interest and several types of manuscripts, the journal has a predominant academic character, the submission of manuscripts is open to students, graduates, researchers and staff members of research and academic institutions, as well as to any individual willing to disseminate the knowledge about the management of Vietnamese environment.</dcterms:abstract>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="vie" xsi:type="ddb:contentISO639-2">Hệ thống khoa học và giáo dục đại học Việt Nam đóng vai trò quan trọng trong sự phát triển kinh tế và xã hội của đất nước. Trong xu hướng giao thoa mạnh mẽ giữa các hệ thống giáo dục quốc tế, đóng góp của các nhà nghiên cứu ở Việt Nam cho cộng đồng khoa học quốc tế còn khiêm tốn. Trong những thập niên qua, hầu hết các hoạt động khoa học tập trung vào khoa học lý thuyết hơn là các lĩnh vực khoa học ứng dụng. Điều này đã được phản ánh qua tỷ lệ khá thấp các ấn phẩm quốc tế về khoa học thực nghiệm. Cùng với những nỗ lực của đất nước để sử dụng hiệu quả các nguồn tài nguyên thiên nhiên, một nhu cầu cấp bách đặt ra là tăng cường các hoạt động nghiên cứu về khoa học môi trường. Tạp chí Môi trường Việt Nam ra đời nhằm hưởng ứng sự quan tâm ngày một gia tăng trong nghiên cứu môi trường. Tạp chí được thành lập như một phần của mạng lưới học thuật được đề xuất bởi Trường Đại học Tổng hợp Kỹ Thuật Dresden trong khuôn khổ chương trình hợp tác Việt Nam - CHLB Đức về đào tạo và giáo dục. Với mối quan tâm đa ngành và đa dạng trong ấn phẩm, tạp chí chủ yếu mang tính học thuật, cơ hội gửi đăng bài viết mở rộng cho cả sinh viên, kỹ sư / cử nhân, nghiên cứu viên và các thành viên của Viện nghiên cứu và giáo dục, các cá nhân có mong muốn phổ biến kiến thức về quản lý môi trường ở Việt Nam.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Technische Universität Dresden</cc:name>
         <cc:place>Pirna</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2012-08-06</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-03-01</dcterms:modified>
   <dc:type xsi:type="dini:PublType">article</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-88127</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">eng</dc:language>
   <dc:source xsi:type="ddb:noScheme">Journal of Vietnamese Environment, 2011, Vol. 1, No. 1, pp. 1-4</dc:source>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa-88705</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://tud.example.com/qucosa:24994/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-88127</ddb:identifier>
   <ddb:identifier ddb:type="Erstkat-ID">372029159</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:24994');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (34, 17, '2018-11-15 12:24:02.493+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Sind E-Autos alltagstauglich oder werden sie dies in der Zukunft sein?</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-234062</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:description>Die vorliegende Bachelorarbeit befasst sich mit der Frage über die Alltagstauglichkeit eines Elektroautos. Bekanntermaßen ist die geringe Reichweite, die ein Elektroauto fahren kann, die Hauptproblematik, welche zusätzlich von verschiedenen Faktoren beeinflusst wird. Besonders hohe Geschwindigkeiten als auch zu hohe oder zu niedrige Außentemperaturen können die Reichweite wirksam beeinträchtigen. 
Zur Beschreibung dieser Auswirkung wird für die Reichweite in Abhängigkeit von der Geschwindigkeit ein physikbasiertes Modell verwendet. Hinzufügend wird anhand von Daten ein Regressionsmodell für die Temperaturabhängigkeit erstellt. Mithilfe dieser Modelle können Aussagen über die Nutzungsmöglichkeit von Elektroautos in den Sommer- und Wintermonaten als auch für Hochgeschwindigkeitsstraßen wie beispielsweise auf Autobahnen getroffen werden.</dc:description>
   <dc:subject>Elektroauto, alltagstauglich, Reichweite, Temperatur, Geschwindigkeit</dc:subject>
   <dc:subject>electric vehicle, road compability, range, temperature, speed</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/380</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/621.3</dc:subject>
   <dc:creator>Nguyen,Bich Lien</dc:creator>
   <dc:contributor>Treiber,Martin</dc:contributor>
   <dc:contributor>Okhrin,Ostap</dc:contributor>
   <dc:contributor>Technische Universität Dresden</dc:contributor>
   <dc:date>2018-10-11</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>bachelorThesis</dc:type>
</oai_dc:dc>', false, 'qucosa:30859');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (35, 22, '2018-11-15 12:24:02.493+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Sind E-Autos alltagstauglich oder werden sie dies in der Zukunft sein?</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Bich Lien</pc:foreName>
            <pc:surName>Nguyen</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Elektroauto</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">alltagstauglich</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Reichweite</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Temperatur</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Geschwindigkeit</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">electric vehicle</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">road compability</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">range</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">temperature</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">speed</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">621.3</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">621.3</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 4480</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Die vorliegende Bachelorarbeit befasst sich mit der Frage über die Alltagstauglichkeit eines Elektroautos. Bekanntermaßen ist die geringe Reichweite, die ein Elektroauto fahren kann, die Hauptproblematik, welche zusätzlich von verschiedenen Faktoren beeinflusst wird. Besonders hohe Geschwindigkeiten als auch zu hohe oder zu niedrige Außentemperaturen können die Reichweite wirksam beeinträchtigen. 
Zur Beschreibung dieser Auswirkung wird für die Reichweite in Abhängigkeit von der Geschwindigkeit ein physikbasiertes Modell verwendet. Hinzufügend wird anhand von Daten ein Regressionsmodell für die Temperaturabhängigkeit erstellt. Mithilfe dieser Modelle können Aussagen über die Nutzungsmöglichkeit von Elektroautos in den Sommer- und Wintermonaten als auch für Hochgeschwindigkeitsstraßen wie beispielsweise auf Autobahnen getroffen werden.</dcterms:abstract>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Martin</pc:foreName>
            <pc:surName>Treiber</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Ostap</pc:foreName>
            <pc:surName>Okhrin</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-10-11</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">bachelorThesis</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-234062</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>bachelor</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Technische Universität Dresden</cc:name>
            <cc:place>Dresden</cc:place>
            <cc:department>
               <cc:name>Fakultät Verkehrswissenschaften ''Friedrich List''</cc:name>
            </cc:department>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://tud.example.com/qucosa:30859/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-234062</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:30859');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (37, 17, '2018-11-15 12:24:09.625+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Schätzung maximaler Wartezeiten mittels Extremwertverteilung an lichtsignalgesteuerten Knotenpunkten</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-232144</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:description>In dieser Arbeit wurde erstmalig die Anwendung der Extremwerttheorie auf Wartezeiten eines lichtsignalgesteuerten Verkehrsknotenpunktes untersucht. Anhand der Verkehrsstärken eines realen Knotenpunktes wurden mit der Simulationssoftware PTV Vissim 100 Datensätze mit individuellen Wartezeiten erzeugt. Als Referenz wurde eine zweite Simulationsreihe durchgeführt. Diese erfolgte mit 15 % höherer Verkehrsstärke. Mittels der Blockmaximum-Methode wurden aus den erzeugten Datensätzen die Maxima ausgewählt, welche mit der Maximum-Likelihood Methode an eine Extremwertverteilung angepasst wurden. Die Bewertung der Schätzung wurde mit dem Kolmogorov-Smirnov Test vorgenommen. Anschließend wurde die Wahrscheinlichkeit, dass bestimmte Wartezeiten überschritten werden (Value at Risk) berechnet. Im Ergebnis konnten 22 % der geschätzten Extremwertverteilungen mit ausreichender Güte angepasst werden. Für die restlichen Datensätze sollte nach Alternativen zur angemessenen Beschreibung gesucht werden.</dc:description>
   <dc:subject>info:eu-repo/classification/ddc/380, 510</dc:subject>
   <dc:subject>Extremwerttheorie, Wartezeiten</dc:subject>
   <dc:subject>Extreme Value Theorie, Waiting times</dc:subject>
   <dc:creator>Drache,Lisa</dc:creator>
   <dc:contributor>Okhrin,Ostap</dc:contributor>
   <dc:contributor>Technische Universität Dresden</dc:contributor>
   <dc:date>2018-07-04</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>
                    masterThesis
                </dc:type>
</oai_dc:dc>', false, 'qucosa:30725');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (39, 22, '2018-11-15 12:24:09.625+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Schätzung maximaler Wartezeiten mittels Extremwertverteilung an lichtsignalgesteuerten Knotenpunkten</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Lisa</pc:foreName>
            <pc:surName>Drache</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">510</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">510</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 4600</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">SK 970</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Extremwerttheorie</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Wartezeiten</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Extreme Value Theorie</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Waiting times</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">In dieser Arbeit wurde erstmalig die Anwendung der Extremwerttheorie auf Wartezeiten eines lichtsignalgesteuerten Verkehrsknotenpunktes untersucht. Anhand der Verkehrsstärken eines realen Knotenpunktes wurden mit der Simulationssoftware PTV Vissim 100 Datensätze mit individuellen Wartezeiten erzeugt. Als Referenz wurde eine zweite Simulationsreihe durchgeführt. Diese erfolgte mit 15 % höherer Verkehrsstärke. Mittels der Blockmaximum-Methode wurden aus den erzeugten Datensätzen die Maxima ausgewählt, welche mit der Maximum-Likelihood Methode an eine Extremwertverteilung angepasst wurden. Die Bewertung der Schätzung wurde mit dem Kolmogorov-Smirnov Test vorgenommen. Anschließend wurde die Wahrscheinlichkeit, dass bestimmte Wartezeiten überschritten werden (Value at Risk) berechnet. Im Ergebnis konnten 22 % der geschätzten Extremwertverteilungen mit ausreichender Güte angepasst werden. Für die restlichen Datensätze sollte nach Alternativen zur angemessenen Beschreibung gesucht werden.</dcterms:abstract>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Kathrin</pc:foreName>
            <pc:surName>Kormoll</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Ostap</pc:foreName>
            <pc:surName>Okhrin</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-07-04</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">masterThesis</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-232144</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>master</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Technische Universität Dresden</cc:name>
            <cc:place>Dresden</cc:place>
            <cc:department>
               <cc:name>Fakultät Verkehrswissenschaften ''Friedrich List''</cc:name>
            </cc:department>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://tud.example.com/qucosa:30725/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-232144</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:30725');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (41, 17, '2018-11-15 12:24:12.443+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Die Belastung von Nutzern im Straßenverkehr mit Luftschadstoffen: Das Fahrrad als mobiler Messträger zur Feinstaubmessung im Straßenraum</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-232490</dc:identifier>
   <dc:identifier>2367-315X</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa-201073</dc:relation>
   <dc:description>Die gesundheitsschädliche Wirkung unreiner Luft ist Gegenstand unzähliger Studien und wurde bereits hinreichend nachgewiesen. Der Straßenverkehr ist dabei eine der wesentlichsten Schadstoffquellen, denen der Mensch im Alltag ausgesetzt ist. Ziel dieser Arbeit ist es, die Schadstoffbelastung von Nutzern des Straßenverkehrs vertiefend abzubilden.
Im Rahmen eines Literaturreviews werden insgesamt 50 wissenschaftliche Studien analysiert. Obwohl sich viele Studien mit dem Vergleich der Verkehrsmittel bezüglich der Immissionsexposition auseinandersetzen, widersprechen sich ihre Ergebnisse je nach Schadstoff regelmäßig hinsichtlich der Reihenfolge der Verkehrsmittel oder auch grundsätzlich bezüglich der Signifikanz der Verkehrsmittelwahl. Ursache dafür sind die zahlreichen Einflussfaktoren, die sich von Arbeit zu Arbeit unterscheiden und eine Vergleichbarkeit der Messergebnisse stark erschweren. Trotz einer steigenden Anzahl an Studien zum Thema mangelt es den Messmethoden an einem strengen Qualitätsstandard sowie einer ausführlichen Dokumentation der Messbedingungen. Eine Verallgemeinerung und Vergleichbarkeit der Forschungsergebnisse untereinander ist damit bislang nicht gewährleistet. Die Betrachtung der Luftqualität bildet die wissenschaftliche Grundlage, um Grenzwertüberschreitungen aufzuspüren, Gegenmaßnahmen zu erarbeiten und diese auf ihre Wirksamkeit zu kontrollieren. Die Werte stationärer Messeinrichtungen geben dabei ein nur unzureichend genaues Bild über die Immissionen, denen die Nutzer des Straßenverkehrs ausgesetzt sind. Um ein realistischeres Bild über die Immissionsbelastung von Verkehrsteilnehmern zu gewinnen, wird mittels eines mobilen Messgeräts die PM10-Belastung für Radfahrer in Teilen des Dresdner Straßennetzes bestimmt. Dabei wird deutlich, dass die gemessene Schadstoffkonzentration sowohl räumlich innerhalb einer Messstrecke als auch zeitlich im Laufe des Tages variiert. Um den weitestgehend emissionsarmen Rad- und Fußverkehr weiter zu fördern und auszubauen, ist es nötig, die Schadstoffbelastung dieser Verkehrsteilnehmer noch besser zu verstehen und quantifizieren zu können. Nur dann sind Verkehrsplaner und Entscheidungsträger in der Lage, eine Infrastruktur zu schaffen, die ihre Nutzer so wenig wie möglich gesundheitlich beeinträchtigt.</dc:description>
   <dc:subject>Straßenverkehr, Radfahrer, Luftschadstoffe, Feinstaub, PM10, Immissionen, Monitoring</dc:subject>
   <dc:subject>road traffic, cyclist, air pollutants, particulates, immissions, monitoring</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/380</dc:subject>
   <dc:creator>Scherzer,Laura</dc:creator>
   <dc:contributor>Lißner,Sven</dc:contributor>
   <dc:contributor>Becker,Udo</dc:contributor>
   <dc:contributor>Günther,Edeltraud</dc:contributor>
   <dc:contributor>Technische Universität Dresden</dc:contributor>
   <dc:date>2017</dc:date>
   <dc:date>2018-01-19</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>
                    masterThesis
                </dc:type>
</oai_dc:dc>', false, 'qucosa:30751');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (42, 22, '2018-11-15 12:24:12.443+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Die Belastung von Nutzern im Straßenverkehr mit Luftschadstoffen: Das Fahrrad als mobiler Messträger zur Feinstaubmessung im Straßenraum</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Laura</pc:foreName>
            <pc:surName>Scherzer</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Straßenverkehr</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Radfahrer</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Luftschadstoffe</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Feinstaub</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">PM10</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Immissionen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Monitoring</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">road traffic</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">cyclist</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">air pollutants</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">particulates</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">immissions</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">monitoring</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 2800</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Die gesundheitsschädliche Wirkung unreiner Luft ist Gegenstand unzähliger Studien und wurde bereits hinreichend nachgewiesen. Der Straßenverkehr ist dabei eine der wesentlichsten Schadstoffquellen, denen der Mensch im Alltag ausgesetzt ist. Ziel dieser Arbeit ist es, die Schadstoffbelastung von Nutzern des Straßenverkehrs vertiefend abzubilden.
Im Rahmen eines Literaturreviews werden insgesamt 50 wissenschaftliche Studien analysiert. Obwohl sich viele Studien mit dem Vergleich der Verkehrsmittel bezüglich der Immissionsexposition auseinandersetzen, widersprechen sich ihre Ergebnisse je nach Schadstoff regelmäßig hinsichtlich der Reihenfolge der Verkehrsmittel oder auch grundsätzlich bezüglich der Signifikanz der Verkehrsmittelwahl. Ursache dafür sind die zahlreichen Einflussfaktoren, die sich von Arbeit zu Arbeit unterscheiden und eine Vergleichbarkeit der Messergebnisse stark erschweren. Trotz einer steigenden Anzahl an Studien zum Thema mangelt es den Messmethoden an einem strengen Qualitätsstandard sowie einer ausführlichen Dokumentation der Messbedingungen. Eine Verallgemeinerung und Vergleichbarkeit der Forschungsergebnisse untereinander ist damit bislang nicht gewährleistet. Die Betrachtung der Luftqualität bildet die wissenschaftliche Grundlage, um Grenzwertüberschreitungen aufzuspüren, Gegenmaßnahmen zu erarbeiten und diese auf ihre Wirksamkeit zu kontrollieren. Die Werte stationärer Messeinrichtungen geben dabei ein nur unzureichend genaues Bild über die Immissionen, denen die Nutzer des Straßenverkehrs ausgesetzt sind. Um ein realistischeres Bild über die Immissionsbelastung von Verkehrsteilnehmern zu gewinnen, wird mittels eines mobilen Messgeräts die PM10-Belastung für Radfahrer in Teilen des Dresdner Straßennetzes bestimmt. Dabei wird deutlich, dass die gemessene Schadstoffkonzentration sowohl räumlich innerhalb einer Messstrecke als auch zeitlich im Laufe des Tages variiert. Um den weitestgehend emissionsarmen Rad- und Fußverkehr weiter zu fördern und auszubauen, ist es nötig, die Schadstoffbelastung dieser Verkehrsteilnehmer noch besser zu verstehen und quantifizieren zu können. Nur dann sind Verkehrsplaner und Entscheidungsträger in der Lage, eine Infrastruktur zu schaffen, die ihre Nutzer so wenig wie möglich gesundheitlich beeinträchtigt.</dcterms:abstract>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Sven</pc:foreName>
            <pc:surName>Lißner</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Udo</pc:foreName>
            <pc:surName>Becker</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Edeltraud</pc:foreName>
            <pc:surName>Günther</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-01-19</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2017</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">masterThesis</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-232490</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="ddb:noScheme">Verkehrsökologische Schriftenreihe ; 11/2017</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa-201073</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>master</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Technische Universität Dresden</cc:name>
            <cc:place>Dresden</cc:place>
            <cc:department>
               <cc:name>Fakultät Verkehrswissenschaften ''Friedrich List''</cc:name>
            </cc:department>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://tud.example.com/qucosa:30751/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-232490</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:30751');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (44, 17, '2018-11-15 12:24:49.217+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Subsystemmethodik für die Auslegung des niederfrequenten Schwingungskomforts von PKW</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-232314</dc:identifier>
   <dc:identifier>978-3-7369-96694</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:description>Um eine zielgerichtete Ableitung von Fahrzeugeigenschaften in frühen Entwicklungsphasen zu ermöglichen, ist eine Subsystemebene erforderlich, die eine konzeptunabhängige Auslegung des Gesamtfahrzeugverhaltens zulässt. In der vorliegenden Arbeit wird daher eine neue Methodik zur Auslegung von Fahrkomfort-Kennwerten auf Basis von Subsystemeigenschaften vorgestellt. Neben der Entwicklung eines geeigneten Modellansatzes, in dem die Subsysteme des Gesamtfahrzeugs durch Greybox-Modelle ohne Komponentenbezug miteinander verknüpft werden, stehen dabei auch dessen Parametrierung sowie die Integration der Methodik im Entwicklungsprozess im Vordergrund. Zur Ableitung der damit verbundenen physikalisch-mechanischen Zusammenhänge werden statische und dynamische Achsprüfstände sowie Simulationen eingesetzt. Die Anwendung der Methodik lässt eine gezielte Eigenschaftsableitung zwischen Gesamtfahrzeug-, Subsystem- und Komponentenebene im Fahrkomfort zu, bei der die Subsystemebene als neue Referenz für die Ableitung von Komponenteneigenschaften dient. Weiterhin erlaubt das Vorgehen eine eigenschaftsbasierte Vorauswahl optimaler Komponentenkonzepte sowie detaillierte Wettbewerbsanalysen. Dadurch wird eine nachhaltige Steigerung der Effizienz im Entwicklungsprozess des Fahrkomforts ermöglicht.</dc:description>
   <dc:subject>info:eu-repo/classification/ddc/380, 620</dc:subject>
   <dc:subject>Fahrkomfort, Subsystem, Simulation, Messung, Eigenschaften, Entwicklungsprozess, Zielwertableitung, Frontloading, Fahrwerk, Achsreibung, Blackbox, Parametrierung, Wettbewerbsanalyse, Prüfstand</dc:subject>
   <dc:subject>ride comfort, subsystem, simulation, measurement, properties, development process, target cascading, frontloading, suspension, axle friction, blackbox, parametrisation, competitor analysis, test rig</dc:subject>
   <dc:creator>Angrick,Christian</dc:creator>
   <dc:contributor>Prokop,Günther</dc:contributor>
   <dc:contributor>Winner,Hermann</dc:contributor>
   <dc:contributor>Technische Universität Dresden</dc:contributor>
   <dc:date>2018-01-16</dc:date>
   <dc:date>2017-03-17</dc:date>
   <dc:date>2017-08-14</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>
                    doctoralThesis
                </dc:type>
</oai_dc:dc>', false, 'qucosa:30738');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (46, 22, '2018-11-15 12:24:49.217+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Subsystemmethodik für die Auslegung des niederfrequenten Schwingungskomforts von PKW</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Christian</pc:foreName>
            <pc:surName>Angrick</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">620</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">620</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 4215</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Fahrkomfort</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Subsystem</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Simulation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Messung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Eigenschaften</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Entwicklungsprozess</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Zielwertableitung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Frontloading</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Fahrwerk</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Achsreibung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Blackbox</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Parametrierung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Wettbewerbsanalyse</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Prüfstand</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">ride comfort</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">subsystem</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">simulation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">measurement</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">properties</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">development process</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">target cascading</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">frontloading</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">suspension</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">axle friction</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">blackbox</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">parametrisation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">competitor analysis</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">test rig</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Um eine zielgerichtete Ableitung von Fahrzeugeigenschaften in frühen Entwicklungsphasen zu ermöglichen, ist eine Subsystemebene erforderlich, die eine konzeptunabhängige Auslegung des Gesamtfahrzeugverhaltens zulässt. In der vorliegenden Arbeit wird daher eine neue Methodik zur Auslegung von Fahrkomfort-Kennwerten auf Basis von Subsystemeigenschaften vorgestellt. Neben der Entwicklung eines geeigneten Modellansatzes, in dem die Subsysteme des Gesamtfahrzeugs durch Greybox-Modelle ohne Komponentenbezug miteinander verknüpft werden, stehen dabei auch dessen Parametrierung sowie die Integration der Methodik im Entwicklungsprozess im Vordergrund. Zur Ableitung der damit verbundenen physikalisch-mechanischen Zusammenhänge werden statische und dynamische Achsprüfstände sowie Simulationen eingesetzt. Die Anwendung der Methodik lässt eine gezielte Eigenschaftsableitung zwischen Gesamtfahrzeug-, Subsystem- und Komponentenebene im Fahrkomfort zu, bei der die Subsystemebene als neue Referenz für die Ableitung von Komponenteneigenschaften dient. Weiterhin erlaubt das Vorgehen eine eigenschaftsbasierte Vorauswahl optimaler Komponentenkonzepte sowie detaillierte Wettbewerbsanalysen. Dadurch wird eine nachhaltige Steigerung der Effizienz im Entwicklungsprozess des Fahrkomforts ermöglicht.</dcterms:abstract>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Günther</pc:foreName>
            <pc:surName>Prokop</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Günther</pc:foreName>
            <pc:surName>Prokop</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Hermann</pc:foreName>
            <pc:surName>Winner</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:dateSubmitted xsi:type="dcterms:W3CDTF">2017-03-17</dcterms:dateSubmitted>
   <dcterms:dateAccepted xsi:type="dcterms:W3CDTF">2017-08-14</dcterms:dateAccepted>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-01-16</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">doctoralThesis</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-232314</dc:identifier>
   <dc:identifier xsi:type="urn:isbn">978-3-7369-96694</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="ddb:noScheme">Schriftenreihe des Lehrstuhls Kraftfahrzeugtechnik ; Band 5</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>thesis.doctoral</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Technische Universität Dresden</cc:name>
            <cc:place>Dresden</cc:place>
            <cc:department>
               <cc:name>Fakultät Verkehrswissenschaften "Friedrich List"</cc:name>
            </cc:department>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:30738/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-232314</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:30738');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (49, 17, '2018-11-15 12:25:06.825+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Report on the results of a comparative Russian-German research on sustainable mobility:Perception, Priorities and Trends of Sustainable mobility in Russia and Germany</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-237776</dc:identifier>
   <dc:language>eng</dc:language>
   <dc:description>The report presents the results of a comparative Russian-German research on the topic of sustainable mobility. On the basis of two stages of sequential, electronic, anonymous interviews of 23 Russian and 24 German experts in the field of transport, there was identified the role of a sustainable mobility in achieving the Sustainable development goals (SDG’s); the most relevant objectives of sustainable mobility in Russia and Germany; the barriers and contributors of the inclusion of these objectives in Russian transport policy. Furthermore, specific strategies that can effectively contribute to tackling transport problems under specific national conditions were outlined. In addition, the perception of the role of sustainable mobility in the expert circles of Russia and Germany were compared. In conclusion, the author provides a number of problematic issues that require further research in order to promote the concept of sustainable mobility in Russia.:Introduction.
Part I. Sustainable Transport Impacts on Achieving the Sustainable Development Goals (SDG\''s).
Part II. Priority objectives of sustainable mobility in Germany and Russia.
Part III. The effectiveness of sustainable mobility strategies.
General conclusions.
Further research.
References.
Annex 1. The questionnaire of the first stage of the research.
Annex 2. The questionnaire of the second stage of the research.
Annex 3. Statistics of evaluations on question №1: \''Sustainable Transport Impacts on Achieving the Sustainable Development Goals (SDG’s)\''.
Annex 4. Statistics of evaluations on question №2: \''The relevance of sustainable mobility objectives in Russia / Germany\''.
Annex 5. Statistics of evaluations on question №2: \''The effectiveness of sustainable mobility strategies in Russia / Germany\''.</dc:description>
   <dc:subject>Nachhaltige Mobilität, Nachhaltige Verkehrsentwicklung, Delphi-Studie</dc:subject>
   <dc:subject>Sustainable mobility, sustainable transport development, the Delphi method</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/380</dc:subject>
   <dc:creator>Evseeva,Anastasia</dc:creator>
   <dc:contributor>Gerike,Regine</dc:contributor>
   <dc:contributor>Ahrens,Gerd-Axel</dc:contributor>
   <dc:publisher>Dresden University of Technology</dc:publisher>
   <dc:date>2018-08-28</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>report</dc:type>
</oai_dc:dc>', false, 'qucosa:31145');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (50, 22, '2018-11-15 12:25:06.825+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="eng" xsi:type="ddb:titleISO639-2">Report on the results of a comparative Russian-German research on sustainable mobility</dc:title>
   <dcterms:alternative xml:lang="eng" xsi:type="ddb:talternativeISO639-2">Perception, Priorities and Trends of Sustainable mobility in Russia and Germany</dcterms:alternative>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Anastasia</pc:foreName>
            <pc:surName>Evseeva</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Nachhaltige Mobilität</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Nachhaltige Verkehrsentwicklung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Delphi-Studie</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Sustainable mobility</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">sustainable transport development</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">the Delphi method</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">QR 800</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 3100</dc:subject>
   <dcterms:tableOfContents ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">Introduction.
Part I. Sustainable Transport Impacts on Achieving the Sustainable Development Goals (SDG\''s).
Part II. Priority objectives of sustainable mobility in Germany and Russia.
Part III. The effectiveness of sustainable mobility strategies.
General conclusions.
Further research.
References.
Annex 1. The questionnaire of the first stage of the research.
Annex 2. The questionnaire of the second stage of the research.
Annex 3. Statistics of evaluations on question №1: \''Sustainable Transport Impacts on Achieving the Sustainable Development Goals (SDG’s)\''.
Annex 4. Statistics of evaluations on question №2: \''The relevance of sustainable mobility objectives in Russia / Germany\''.
Annex 5. Statistics of evaluations on question №2: \''The effectiveness of sustainable mobility strategies in Russia / Germany\''.</dcterms:tableOfContents>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">The report presents the results of a comparative Russian-German research on the topic of sustainable mobility. On the basis of two stages of sequential, electronic, anonymous interviews of 23 Russian and 24 German experts in the field of transport, there was identified the role of a sustainable mobility in achieving the Sustainable development goals (SDG’s); the most relevant objectives of sustainable mobility in Russia and Germany; the barriers and contributors of the inclusion of these objectives in Russian transport policy. Furthermore, specific strategies that can effectively contribute to tackling transport problems under specific national conditions were outlined. In addition, the perception of the role of sustainable mobility in the expert circles of Russia and Germany were compared. In conclusion, the author provides a number of problematic issues that require further research in order to promote the concept of sustainable mobility in Russia.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Dresden University of Technology</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Regine</pc:foreName>
            <pc:surName>Gerike</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Gerd-Axel</pc:foreName>
            <pc:surName>Ahrens</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-08-28</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">report</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-237776</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">eng</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:31145/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-237776</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:31145');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (53, 17, '2018-11-15 12:26:12.786+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Comparative analysis of the usage of free-floating carsharing between Berlin and Calgary</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-318341</dc:identifier>
   <dc:identifier>2367-315X</dc:identifier>
   <dc:language>eng</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa-201073</dc:relation>
   <dc:description>Objectives: The purpose of this paper is to investigate possible reasons, based on economic and city characteristics, for the different usage of free-floating carsharing between a car-dependent city (Calgary) and one non-car-dependent city (Berlin). This paper identifies factors that help a free-floating carsharing system to be successful in a city that scores poorly on com-monly known success factors of carsharing. 
Methods: Various factors were evaluated, namely, geographic and demographic market char-acteristics, the available transport systems and the costs and household spending of both cities. A dataset which describes the usage of free-floating carsharing in Berlin and Calgary from Au-gust 2016 to November 2016 was analyzed in this study.
Results: Calgary’s car2go system has fewer rentals and fewer members than Berlin. Possible reasons lie in the different city characteristics and different cost structures. Both 85th percentile of the travel distance is approximately as long as the radius of the respective home area in both cities. Thus, the median travel distance and the median reservation/rental duration is shorter in Calgary than in Berlin. The fact that more than 70 % of rentals in Calgary arrive in, depart from or travel within areas with active parking management could be due to the fact that free-floating carsharing users do not need to pay extra for parking fees. The carsharing bookings in Calgary peak at midnight when the public transportation service shuts down. The peak could also be the result of the high number of 3-minute long rentals at this time. Neither the high number of 3-minute bookings, the midnight peak, nor the public transport service close down during night, can be observed in Berlin. Given that employees in downtown Calgary may prefer to use free-floating carsharing to run errands during lunchbreak, the carsharing bookings do not plummet during midday, in contrast to Berlin, which only has a limited number of short distance rentals and where the free-floating carsharing bookings follow a similar pattern to the two-humped car traffic volume graph. 
Conclusion: Given the focus of the departures and arrivals in Calgary in areas where parking fees are charged, active parking management could be a success factor for free-floating car-sharing in car-dependent cities. However, it is not advisable to solely enforce parking fees within select parts of the home area as individuals generally prefer to use the less expensive mode of transport; which is free-floating carsharing to travel from and to areas with active parking man-agement and their own car for any other trip. As a result, the city would not gain the benefits free-floating carsharing could provide.  
Recommendations: Based on the results of this study, it is advisable to investigate whether home area wide or city wide parking management and surcharges for trips to downtown could encourage Calgary’s members in to use the car2go in a way that it provides the most benefits from a city perspective.</dc:description>
   <dc:subject>carsharing, free floating</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/380</dc:subject>
   <dc:creator>Schnieder,Maren</dc:creator>
   <dc:contributor>Becker,Udo</dc:contributor>
   <dc:contributor>Becker,Thilo</dc:contributor>
   <dc:contributor>Pessier,René</dc:contributor>
   <dc:contributor>Lindner,Martin</dc:contributor>
   <dc:contributor>Technische Universität Dresden</dc:contributor>
   <dc:contributor>Technische Universität Dresden</dc:contributor>
   <dc:date>2017-03-01</dc:date>
   <dc:date>2017</dc:date>
   <dc:date>2018-09-28</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>StudyThesis</dc:type>
</oai_dc:dc>', false, 'qucosa:31834');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (54, 22, '2018-11-15 12:26:12.786+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="eng" xsi:type="ddb:titleISO639-2">Comparative analysis of the usage of free-floating carsharing between Berlin and Calgary</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Maren</pc:foreName>
            <pc:surName>Schnieder</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">carsharing</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">free floating</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 2800</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">Objectives: The purpose of this paper is to investigate possible reasons, based on economic and city characteristics, for the different usage of free-floating carsharing between a car-dependent city (Calgary) and one non-car-dependent city (Berlin). This paper identifies factors that help a free-floating carsharing system to be successful in a city that scores poorly on com-monly known success factors of carsharing. 
Methods: Various factors were evaluated, namely, geographic and demographic market char-acteristics, the available transport systems and the costs and household spending of both cities. A dataset which describes the usage of free-floating carsharing in Berlin and Calgary from Au-gust 2016 to November 2016 was analyzed in this study.
Results: Calgary’s car2go system has fewer rentals and fewer members than Berlin. Possible reasons lie in the different city characteristics and different cost structures. Both 85th percentile of the travel distance is approximately as long as the radius of the respective home area in both cities. Thus, the median travel distance and the median reservation/rental duration is shorter in Calgary than in Berlin. The fact that more than 70 % of rentals in Calgary arrive in, depart from or travel within areas with active parking management could be due to the fact that free-floating carsharing users do not need to pay extra for parking fees. The carsharing bookings in Calgary peak at midnight when the public transportation service shuts down. The peak could also be the result of the high number of 3-minute long rentals at this time. Neither the high number of 3-minute bookings, the midnight peak, nor the public transport service close down during night, can be observed in Berlin. Given that employees in downtown Calgary may prefer to use free-floating carsharing to run errands during lunchbreak, the carsharing bookings do not plummet during midday, in contrast to Berlin, which only has a limited number of short distance rentals and where the free-floating carsharing bookings follow a similar pattern to the two-humped car traffic volume graph. 
Conclusion: Given the focus of the departures and arrivals in Calgary in areas where parking fees are charged, active parking management could be a success factor for free-floating car-sharing in car-dependent cities. However, it is not advisable to solely enforce parking fees within select parts of the home area as individuals generally prefer to use the less expensive mode of transport; which is free-floating carsharing to travel from and to areas with active parking man-agement and their own car for any other trip. As a result, the city would not gain the benefits free-floating carsharing could provide.  
Recommendations: Based on the results of this study, it is advisable to investigate whether home area wide or city wide parking management and surcharges for trips to downtown could encourage Calgary’s members in to use the car2go in a way that it provides the most benefits from a city perspective.</dcterms:abstract>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Udo</pc:foreName>
            <pc:surName>Becker</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Thilo</pc:foreName>
            <pc:surName>Becker</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>René</pc:foreName>
            <pc:surName>Pessier</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Martin</pc:foreName>
            <pc:surName>Lindner</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:dateSubmitted xsi:type="dcterms:W3CDTF">2017-03-01</dcterms:dateSubmitted>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-09-28</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2017</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">StudyThesis</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-318341</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">eng</dc:language>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa-201073</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:31834/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-318341</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:31834');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (57, 17, '2018-11-15 12:26:29.194+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Big Data in Bicycle Traffic:A user-oriented goaiide to the use of smartphone-generated bicycle traffic data</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-233278</dc:identifier>
   <dc:language>eng</dc:language>
   <dc:description>For cycling to be attractive, the infrastructure must be of high quality. Due to the high level of resources required to record it locally, the available data on the volume of cycling traffic has to date been patchy. At the moment, the most reliable and usable numbers seem to be derived from permanently installed automatic cycling traffic counters, already used by many local authorities. One disadvantage of these is that the number of data collection points is generally far too low to cover the entirety of a city or other municipality in a way that achieves truly meaningful results. The effect of side roads on cycling traffic is therefore only incompletely assessed. Furthermore, there is usually no data at all on other parameters, such as waiting times, route choices and cyclists’ speed. This gap might in future be filled by methods such as GPS route data, as is now possible by today’s widespread use of smartphones and the relevant tracking apps. The results of the project presented in this goaiide have been supported by the BMVI [Federal Ministry of Transport and Digital Infrastructure] within the framework of its 2020 National Cycling Plan. This research project seeks to investigate the usability of user data generated using a smartphone app for bicycle traffic planning by local authorities.

In summary, it can be stated that, taking into account the factors described in this goaiide, GPS data are usable for bicycle traffic planning within certain limitations. (The GPS data evaluated in this case were provided by Strava Inc.) Nowadays it is already possible to assess where, when and how cyclists are moving around across the entire network. The data generated by the smartphone app could be most useful to local authorities as a supplement to existing permanent traffic counters. However, there are a few aspects that need to be considered when evaluating and interpreting the data, such as the rather fitness-oriented context of the routes surveyed in the examples examined. Moreover, some of the data is still provided as database or GIS files, although some online templates that are easier to use are being set up, and some can already be used in a basic initial form. This means that evaluation and interpretation still require specialist expertise as well as human resources. However, the need for these is expected to reduce in the future with the further development of web interfaces and supporting evaluation templates. For this to work, developers need to collaborate with local authorities to work out what parameters are needed as well as the most suitable formats. This research project carried out an approach to extrapolating cycling traffic volumes from random samples of GPS data over the whole network. This was also successfully verified in another municipality. Further research is still nevertheless required in the future, as well as adaptation to the needs of different localities.

Evidence for the usability of GPS data in practice still needs to be acquired in the near future. The cities of Dresden, Leipzig and Mainz could be taken as examples for this, as they have all already taken their first steps in the use of GPS data in planning for and supporting cycling. These steps make sense in the light of the increasing digitisation of traffic and transport and the growing amount of data available as a result – despite the limitations on these data to date – so that administrative bodies can start early in building up the appropriate skills among their staff. The use of GPS data would yield benefits for bicycle traffic planning in the long run. In addition, the active involvement of cyclists opens up new possibilities in communication and citizen participation – even without requiring specialist knowledge. This goaiide delivers a practical introduction to the topic, giving a comprehensive overview of the opportunities, obstacles and potential offered by GPS data.</dc:description>
   <dc:subject>info:eu-repo/classification/ddc/380</dc:subject>
   <dc:subject>Dresden; Radfahrer; Radfahrerverkehr; Verkehrsverhalten; Datensammlung; Smarthphone; Orientierung</dc:subject>
   <dc:subject>GPS-Daten, Radverkehr, Verhaltensdaten</dc:subject>
   <dc:subject>GPS, Cycling</dc:subject>
   <dc:creator>Francke,Angela</dc:creator>
   <dc:creator>Lißner,Sven</dc:creator>
   <dc:publisher>Technische Universität Dresden</dc:publisher>
   <dc:date>2018-03-02</dc:date>
   <dc:date>2017</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>book</dc:type>
</oai_dc:dc>', false, 'qucosa:30805');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (58, 22, '2018-11-15 12:26:29.194+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="eng" xsi:type="ddb:titleISO639-2">Big Data in Bicycle Traffic</dc:title>
   <dcterms:alternative xml:lang="eng" xsi:type="ddb:talternativeISO639-2">A user-oriented goaiide to the use of smartphone-generated bicycle traffic data</dcterms:alternative>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Angela</pc:foreName>
            <pc:surName>Francke</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Sven</pc:foreName>
            <pc:surName>Lißner</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 4340</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Dresden</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Radfahrer</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Radfahrerverkehr</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Verkehrsverhalten</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Datensammlung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Smarthphone</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Orientierung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">GPS-Daten</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Radverkehr</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Verhaltensdaten</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">GPS</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Cycling</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">For cycling to be attractive, the infrastructure must be of high quality. Due to the high level of resources required to record it locally, the available data on the volume of cycling traffic has to date been patchy. At the moment, the most reliable and usable numbers seem to be derived from permanently installed automatic cycling traffic counters, already used by many local authorities. One disadvantage of these is that the number of data collection points is generally far too low to cover the entirety of a city or other municipality in a way that achieves truly meaningful results. The effect of side roads on cycling traffic is therefore only incompletely assessed. Furthermore, there is usually no data at all on other parameters, such as waiting times, route choices and cyclists’ speed. This gap might in future be filled by methods such as GPS route data, as is now possible by today’s widespread use of smartphones and the relevant tracking apps. The results of the project presented in this goaiide have been supported by the BMVI [Federal Ministry of Transport and Digital Infrastructure] within the framework of its 2020 National Cycling Plan. This research project seeks to investigate the usability of user data generated using a smartphone app for bicycle traffic planning by local authorities.

In summary, it can be stated that, taking into account the factors described in this goaiide, GPS data are usable for bicycle traffic planning within certain limitations. (The GPS data evaluated in this case were provided by Strava Inc.) Nowadays it is already possible to assess where, when and how cyclists are moving around across the entire network. The data generated by the smartphone app could be most useful to local authorities as a supplement to existing permanent traffic counters. However, there are a few aspects that need to be considered when evaluating and interpreting the data, such as the rather fitness-oriented context of the routes surveyed in the examples examined. Moreover, some of the data is still provided as database or GIS files, although some online templates that are easier to use are being set up, and some can already be used in a basic initial form. This means that evaluation and interpretation still require specialist expertise as well as human resources. However, the need for these is expected to reduce in the future with the further development of web interfaces and supporting evaluation templates. For this to work, developers need to collaborate with local authorities to work out what parameters are needed as well as the most suitable formats. This research project carried out an approach to extrapolating cycling traffic volumes from random samples of GPS data over the whole network. This was also successfully verified in another municipality. Further research is still nevertheless required in the future, as well as adaptation to the needs of different localities.

Evidence for the usability of GPS data in practice still needs to be acquired in the near future. The cities of Dresden, Leipzig and Mainz could be taken as examples for this, as they have all already taken their first steps in the use of GPS data in planning for and supporting cycling. These steps make sense in the light of the increasing digitisation of traffic and transport and the growing amount of data available as a result – despite the limitations on these data to date – so that administrative bodies can start early in building up the appropriate skills among their staff. The use of GPS data would yield benefits for bicycle traffic planning in the long run. In addition, the active involvement of cyclists opens up new possibilities in communication and citizen participation – even without requiring specialist knowledge. This goaiide delivers a practical introduction to the topic, giving a comprehensive overview of the opportunities, obstacles and potential offered by GPS data.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Technische Universität Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-03-02</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2017</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">book</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-233278</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">eng</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:30805/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-233278</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:30805');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (60, 17, '2018-11-15 12:26:42.506+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>European Hub Airports – Assessment of Constraints for Market Power in the Local Catchment and on the Transfer Market</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-237553</dc:identifier>
   <dc:language>eng</dc:language>
   <dc:description>Airports have long been considered as an industry in which firms are able to exert significant market power. Nowadays, there is controversial discussion whether airports face a degree of competition which is sufficient to constrain potentially abusive behaviour resulting from this market power. The level of competition encountered by European airports has hence been evaluated by analysing the switching potential of both airlines and passengers between different airports, for example. The research within this thesis contributes to the field of airport competition by analysing the degree of potential competition 36 European hub airports face on their origin-destination market in their local catchments as well as on the transfer market within the period from 2000 to 2016. For this purpose, a two-step approach is applied for each market, with first analysing the degree of market concentration, using the Herfindahl Hirschman Index as a measure, for each destination offered at the hub airports and the respective development over time. In the second step, the effect of market concentration on the seat capacities at the hub airports is estimated.
This analysis shows that the majority of European hub airports has a dominant position on both the origin-destination and transfer market. However, it can be observed that the level of market concentration has been decreasing over time, thus implying a higher overlap between destinations offered at hub airports and their competitive counterparts. Passengers thus have more alternatives available when travelling between two points, this increasing switching ability therefore imposes potential constraints on airport market power. In the second step of the analysis, the above approach is complemented by empirically estimating the impact of an increase in market concentration, and additional factors such as the presence of low cost carriers at competing airports, on the seat capacities offered on a particular destination. Using panel data for the considered time period, the statistically significant results show that an increase in market concentration leads to a decrease in the amount of seats as well the flight frequencies offered to a destination. These findings are coherent for both the origin-destination and transfer market. Considering the decrease in market concentration across the majority of European hub airports, it can in turn be inferred that more seats and frequencies are supplied on the respective routes, resulting in an increase in consumer welfare. 
This approach and the respective findings in this thesis serve as further goaiidance to policy makers deciding on the extent of economic regulation feasible for individual hub airports in Europe. From an airport and airline standpoint these results can, of course, also be applied to gain insight as to which airports are their main competitors, and which routes face a high overlap with other airports and airlines, thus designing their network structure accordingly.</dc:description>
   <dc:subject>Flughafenwettbewerb, Europa</dc:subject>
   <dc:subject>Airport competition, Europe</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/380</dc:subject>
   <dc:creator>Paul,Annika</dc:creator>
   <dc:contributor>Wieland,Bernhard</dc:contributor>
   <dc:contributor>Niemeier,Hans-Martin</dc:contributor>
   <dc:contributor>Technische Universität Dresden</dc:contributor>
   <dc:date>2018-01-23</dc:date>
   <dc:date>2018-06-04</dc:date>
   <dc:date>2018-08-30</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>
                    doctoralThesis
                </dc:type>
</oai_dc:dc>', false, 'qucosa:31127');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (61, 22, '2018-11-15 12:26:42.506+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="eng" xsi:type="ddb:titleISO639-2">European Hub Airports – Assessment of Constraints for Market Power in the Local Catchment and on the Transfer Market</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Annika</pc:foreName>
            <pc:surName>Paul</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Flughafenwettbewerb</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Europa</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Airport competition</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Europe</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">QR 840</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">Airports have long been considered as an industry in which firms are able to exert significant market power. Nowadays, there is controversial discussion whether airports face a degree of competition which is sufficient to constrain potentially abusive behaviour resulting from this market power. The level of competition encountered by European airports has hence been evaluated by analysing the switching potential of both airlines and passengers between different airports, for example. The research within this thesis contributes to the field of airport competition by analysing the degree of potential competition 36 European hub airports face on their origin-destination market in their local catchments as well as on the transfer market within the period from 2000 to 2016. For this purpose, a two-step approach is applied for each market, with first analysing the degree of market concentration, using the Herfindahl Hirschman Index as a measure, for each destination offered at the hub airports and the respective development over time. In the second step, the effect of market concentration on the seat capacities at the hub airports is estimated.
This analysis shows that the majority of European hub airports has a dominant position on both the origin-destination and transfer market. However, it can be observed that the level of market concentration has been decreasing over time, thus implying a higher overlap between destinations offered at hub airports and their competitive counterparts. Passengers thus have more alternatives available when travelling between two points, this increasing switching ability therefore imposes potential constraints on airport market power. In the second step of the analysis, the above approach is complemented by empirically estimating the impact of an increase in market concentration, and additional factors such as the presence of low cost carriers at competing airports, on the seat capacities offered on a particular destination. Using panel data for the considered time period, the statistically significant results show that an increase in market concentration leads to a decrease in the amount of seats as well the flight frequencies offered to a destination. These findings are coherent for both the origin-destination and transfer market. Considering the decrease in market concentration across the majority of European hub airports, it can in turn be inferred that more seats and frequencies are supplied on the respective routes, resulting in an increase in consumer welfare. 
This approach and the respective findings in this thesis serve as further goaiidance to policy makers deciding on the extent of economic regulation feasible for individual hub airports in Europe. From an airport and airline standpoint these results can, of course, also be applied to gain insight as to which airports are their main competitors, and which routes face a high overlap with other airports and airlines, thus designing their network structure accordingly.</dcterms:abstract>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Bernhard</pc:foreName>
            <pc:surName>Wieland</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Bernhard</pc:foreName>
            <pc:surName>Wieland</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Hans-Martin</pc:foreName>
            <pc:surName>Niemeier</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:dateSubmitted xsi:type="dcterms:W3CDTF">2018-01-23</dcterms:dateSubmitted>
   <dcterms:dateAccepted xsi:type="dcterms:W3CDTF">2018-06-04</dcterms:dateAccepted>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-08-30</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">doctoralThesis</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-237553</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">eng</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>thesis.doctoral</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Technische Universität Dresden</cc:name>
            <cc:place>Dresden</cc:place>
            <cc:department>
               <cc:name>Fakultät Verkehrswissenschaften ''Friedrich List''</cc:name>
            </cc:department>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:31127/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-237553</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:31127');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (87, 22, '2018-11-15 17:33:05.632+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Erweiterung des ''generalized'' p-Median-Problems</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Alisa</pc:foreName>
            <pc:surName>Futlik</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">betriebliche Standortplanung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">MINISUM-Modelle</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Median</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">''generalized'' p-Median-Problem</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">WLP</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">330</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">330</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">510</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">510</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">QH 420</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">SK 970</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Die vorliegende Masterarbeit beschäftigt sich mit den MINISUM-Modellen auf einem Graphen. Die Eigenschaften des „generalized“ p-Median-Problem werden neben den Eigenschaften des ordinären p-Median-Problems untersucht. Dabei kommt folgende Diskrepanz zum Vorschein: Obwohl das „generalized“ p-Median-Problem eine unendliche
Anzahl an potenziellen Lösungsmöglichkeiten besitzt und der optimale Standort bei einer derartigen Problemstellung sowohl im Knoten als auch auf der Kante des Graphen
liegen kann, wird der Median oft ausschließlich in den Knoten des Graphen gesucht.
Dadurch entsteht das Risiko, dass beim Lösen des Problems der optimale Standort von Anfang an nicht mitberücksichtigt wird. Die Forschungsaufgabe dieser Arbeit ist, das „generalized“ p-Median-Problem so zu erweitern, dass aus einem Problem mit unendlicher Anzahl an Lösungsmöglichkeiten ein endliches Problem wird, welches optimal mit einer diskreten Methode gelöst werden kann.
Im ersten Schritt werden die potenziellen Standorte auf den Kanten (die sogenannten fiktiven Knoten) ermittelt. Sie werden mit den Knoten des Graphen gleichgestellt und bei der Auffindung des kostenminimalen Standortes einkalkuliert. Damit sind alle potenziellen Standorte abgedeckt und das Problem erhält eine endliche Anzahl an Lösungsmöglichkeiten.
Eine weitere Herausforderung liegt in der unkonventionellen Formulierung des Kostenparameters, der beim „generalized“ p-Median-Problem zusätzlich berücksichtigt wird.
Die Kosten stellen eine logarithmische Kostenfunktion dar, die von der Verteilung der Nachfrage auf die Mediane abhängig ist. Diese Variable wird als Zuteilung bezeichnet und muss zusätzlich vor der Formulierung des Optimierungsproblems bestimmt werden.
Die Zuteilung ist für die Ermittlung der Kosten zuständig und fließt in das Modell nur indirekt mit ein. Abschließend wird die Funktionsfähigkeit des neuen Modells überprüft und dem ursprünglichen Modell (dem umformulierten Warehouse Location Problem) gegenübergestellt. Tatsächlich werden bei dem erweiterten Modell durch die Platzierung der Mediane
auf die Kante zusätzliche Kosten eingespart. Die vorliegende Arbeit zeigt das Prinzip, wie das „generalized“ p-Median-Problem erweitert werden kann, und liefert den Beweis über die Funktionstüchtigkeit dieser Methode.</dcterms:abstract>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">The following master’s thesis deals with the MINISUM models on a graph. In this regard the properties of the generalized p-median problem have been investigated alongside
the properties of the ordinary p-median problem. In the course of the investigation, the following discrepancy comes to the fore: although the generalized p-median problem
has an infinite number of potential solutions, and the optimal location for such a problem may lie in both the vertex and on the edge of the graph, the median is often
searched for exclusively in the vertex of the graph. This creates the risk that, upon attempting to find a solution, the optimal location to place the median may not be
taken into consideration right from the start. 
The goal of the following thesis is to extend the generalized p-median problem so that a problem with an infinite number of possible solutions becomes a finite problem which
can best be solved with a discrete method. In the first step, all potential locations along the edges (the so-called fictitious vertices) are determined using an empirical-analytical approach. They are equated with the vertices of the graph and taken into account when locating the minimum cost location.
This covers all potential locations and through this method the problem receives a finite number of possible solutions. Another challenge lies in the unconventional formulation of the cost parameter, which is additionally taken into account in the generalized p-median problem. The cost represents a logarithmic cost function that depends on the distribution of demand on the median. In the following work, this variable shall be called the allocation and must first be determined in order to formulate the optimization problem framework. The allocation is responsible for determining the costs and is included only indirectly in the model.
Finally, the functionality of the new model is checked and compared with the original model, the rewritten warehouse location problem. In fact, the placement of medians
on an edge saves additional costs in the extended model. The following elaboration shows the principle of how the generalized p-median problem can be extended, and provides proof of the functionality of this extension.</dcterms:abstract>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Kathrin</pc:foreName>
            <pc:surName>Kormoll</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Roman</pc:foreName>
            <pc:surName>Stein</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:dateSubmitted xsi:type="dcterms:W3CDTF">2018-05-31</dcterms:dateSubmitted>
   <dcterms:dateAccepted xsi:type="dcterms:W3CDTF">2018-06-20</dcterms:dateAccepted>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-10-15</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2018</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">masterThesis</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-319055</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>master</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Technische Universität Dresden</cc:name>
            <cc:place>Dresden</cc:place>
            <cc:department>
               <cc:name>Fakultät Verkehrswissenschaften „Friedrich List“</cc:name>
            </cc:department>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:31905/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-319055</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:31905');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (63, 17, '2018-11-15 12:27:16.594+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Metaanalyse von Eisenbahnunfällen anhand von Untersuchungsberichten</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-319017</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:description>Die vorliegende Studienarbeit befasst sich mit Berichten und Statistiken zu Daten der Eisenbahnsicherheit sowie Untersuchungsberichten von Eisenbahnunfällen. In diesem Zusammenhang werden sowohl deutsche Behörden und Institutionen als auch EU-weite Entwicklungen beschrieben. Es folgt eine Erläuterung und Abgrenzung zweier möglicher Kategorisierungen von gefährlichen Ereignissen im Bahnbetrieb. Daten ermittelter Quellen werden im Anschluss statistisch analysiert und Unterschiede dieser herausgearbeitet. Weiterhin wird die Durchführung einer ereignisbezogenen Unfalluntersuchung beschrieben sowie auf veröffentlichte Untersuchungsberichte eingegangen. Nach Bewertung der Berichte werden abschließend Vorschläge zur Verbesserung von Unfallanalysen und Berichten der Eisenbahnsicherheit unterbreitet.</dc:description>
   <dc:description>This paper deals with reports and statistics on railway safety data and with investigation reports on railway accidents. Relevant German authorities and institutions as well as EU-wide developments are described. This is followed by an explanation of and dissociation between two possible categorisations of dangerous events in railway operations. Data from selected sources are then analysed statistically and differences within these are ascertained. Furthermore, the implementation of individual accident investigations is described and published investigation reports are outlined. After evaluating these reports, improvements for accident analyses and railway safety reports are suggested.</dc:description>
   <dc:subject>Eisenbahn, Betriebssicherheit, Unfalluntersuchung, Eisenbahnunfall</dc:subject>
   <dc:subject>Railway, operational safety, accident investigation, railway accident</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/380</dc:subject>
   <dc:creator>Wernitz,Dario</dc:creator>
   <dc:contributor>Kunze,Michael</dc:contributor>
   <dc:contributor>Technische Universität Dresden</dc:contributor>
   <dc:date>2018-10-12</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>StudyThesis</dc:type>
</oai_dc:dc>', false, 'qucosa:31901');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (64, 22, '2018-11-15 12:27:16.594+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Metaanalyse von Eisenbahnunfällen anhand von Untersuchungsberichten</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Dario</pc:foreName>
            <pc:surName>Wernitz</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Eisenbahn</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Betriebssicherheit</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Unfalluntersuchung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Eisenbahnunfall</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Railway</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">operational safety</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">accident investigation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">railway accident</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 5875</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Die vorliegende Studienarbeit befasst sich mit Berichten und Statistiken zu Daten der Eisenbahnsicherheit sowie Untersuchungsberichten von Eisenbahnunfällen. In diesem Zusammenhang werden sowohl deutsche Behörden und Institutionen als auch EU-weite Entwicklungen beschrieben. Es folgt eine Erläuterung und Abgrenzung zweier möglicher Kategorisierungen von gefährlichen Ereignissen im Bahnbetrieb. Daten ermittelter Quellen werden im Anschluss statistisch analysiert und Unterschiede dieser herausgearbeitet. Weiterhin wird die Durchführung einer ereignisbezogenen Unfalluntersuchung beschrieben sowie auf veröffentlichte Untersuchungsberichte eingegangen. Nach Bewertung der Berichte werden abschließend Vorschläge zur Verbesserung von Unfallanalysen und Berichten der Eisenbahnsicherheit unterbreitet.</dcterms:abstract>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">This paper deals with reports and statistics on railway safety data and with investigation reports on railway accidents. Relevant German authorities and institutions as well as EU-wide developments are described. This is followed by an explanation of and dissociation between two possible categorisations of dangerous events in railway operations. Data from selected sources are then analysed statistically and differences within these are ascertained. Furthermore, the implementation of individual accident investigations is described and published investigation reports are outlined. After evaluating these reports, improvements for accident analyses and railway safety reports are suggested.</dcterms:abstract>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Michael</pc:foreName>
            <pc:surName>Kunze</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-10-12</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">StudyThesis</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-319017</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:31901/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-319017</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:31901');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (66, 17, '2018-11-15 12:27:31.226+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Verkehrsnachfragemodellierung am Beispiel der Stadt Brandenburg an der Havel</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-233233</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:description>Das Thema der vorliegenden Bachelorarbeit ist, das Vier-Stufen-Modell der Verkehrsnachfrage nach Lohse, auch als Kennwertmodell bekannt, auf die Stadt Brandenburg an der Havel anzuwenden, um die Verkehrserzeugung, die Verkehrsverteilung und die Verkehrsmittelwahl zu berechnen. Dies ist für alle Leser interessant, die damit beginnen, sich mit der integrierten Verkehrsplanung zu beschäftigen, denn diese Arbeit stellt das theoretische, rechnerische und praktische Vorgehen formal vor. Die Berechnungen wurden mit dem Programm „Excel 2013“ realisiert. Zudem ist der Arbeit ein USB-Stick beigelegt, aus dem Sie die formalen Rechnungen aus der Bachelorarbeit besser nachvollziehen können, da auf dem USB-Stick alle Rechnungen hinterlegt sind, die dem Verfahren zugrunde liegen. Zusätzlich enthält der USB-Stick einige Grafiken, welche die Verteilung der Verkehrsmittel in der Stadt Brandenburg an der Havel darstellen.</dc:description>
   <dc:subject>info:eu-repo/classification/ddc/380</dc:subject>
   <dc:subject>Verkehrsnachfragemodellierung, Verkehr, Verkehrserzeugung, Verkehrsverteilung, Verkehrsmittelwahl, Brandenburg an der Havel</dc:subject>
   <dc:subject>traffic, travel demand models, traffic generation, mode allocation, mode preference</dc:subject>
   <dc:creator>Schwarz,Matthias</dc:creator>
   <dc:contributor>Treiber,Martin</dc:contributor>
   <dc:contributor>Technische Universität Dresden</dc:contributor>
   <dc:date>2018-03-26</dc:date>
   <dc:date>2018-02-12</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>bachelorThesis</dc:type>
</oai_dc:dc>', false, 'qucosa:30803');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (67, 22, '2018-11-15 12:27:31.226+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Verkehrsnachfragemodellierung am Beispiel der Stadt Brandenburg an der Havel</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Matthias</pc:foreName>
            <pc:surName>Schwarz</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 3300</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">SK 970</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Verkehrsnachfragemodellierung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Verkehr</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Verkehrserzeugung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Verkehrsverteilung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Verkehrsmittelwahl</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Brandenburg an der Havel</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">traffic</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">travel demand models</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">traffic generation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">mode allocation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">mode preference</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Das Thema der vorliegenden Bachelorarbeit ist, das Vier-Stufen-Modell der Verkehrsnachfrage nach Lohse, auch als Kennwertmodell bekannt, auf die Stadt Brandenburg an der Havel anzuwenden, um die Verkehrserzeugung, die Verkehrsverteilung und die Verkehrsmittelwahl zu berechnen. Dies ist für alle Leser interessant, die damit beginnen, sich mit der integrierten Verkehrsplanung zu beschäftigen, denn diese Arbeit stellt das theoretische, rechnerische und praktische Vorgehen formal vor. Die Berechnungen wurden mit dem Programm „Excel 2013“ realisiert. Zudem ist der Arbeit ein USB-Stick beigelegt, aus dem Sie die formalen Rechnungen aus der Bachelorarbeit besser nachvollziehen können, da auf dem USB-Stick alle Rechnungen hinterlegt sind, die dem Verfahren zugrunde liegen. Zusätzlich enthält der USB-Stick einige Grafiken, welche die Verteilung der Verkehrsmittel in der Stadt Brandenburg an der Havel darstellen.</dcterms:abstract>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Martin</pc:foreName>
            <pc:surName>Treiber</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Martin</pc:foreName>
            <pc:surName>Treiber</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:dateSubmitted xsi:type="dcterms:W3CDTF">2018-02-12</dcterms:dateSubmitted>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-03-26</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">bachelorThesis</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-233233</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>bachelor</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Technische Universität Dresden</cc:name>
            <cc:place>Dresden</cc:place>
            <cc:department>
               <cc:name>Fakultät Verkehrswissenschaften ''Friedrich List''</cc:name>
            </cc:department>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:30803/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-233233</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:30803');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (69, 17, '2018-11-15 12:27:49.203+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Differenzierungsmodell für eine anforderungsorientierte verkehrliche Kapazitätsplanung im ÖPNV</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-319716</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:description>Die verkehrliche Kapazitätsplanung ist für ÖPNV-Unternehmen ein wichtiger Geschäftsprozess. Die Planungsergebnisse entscheiden maßgeblich über den Einsatz kostenträchtiger Ressourcen und über den Erfolg der ÖPNV-Dienstleistung am Verkehrsmarkt. Trotz dieser Bedeutung beschränkt sich die Planung bisher noch weitgehend auf die Umsetzung von Aufgabenträgervorgaben und vernachlässigt ergänzende Anforderungen der Kunden und Unternehmen.
Die vorliegende Arbeit ermittelt die Anforderungen aller relevanten Anspruchsgruppen und benennt Umsetzungsdefizite der heutigen Planungspraxis. Diese Defizite bilden die Grundlage für die Entwicklung einer neuen, anforderungsorientierten Planungsmethodik. Für diese Methodik wird die Qualität des Platzangebotes aus der Perspektive der Kunden definiert und ermittelt. Aus Kundensicht stellt dabei eine uneingeschränkte Sitzplatzverfügbarkeit das höchste Qualitätsniveau dar, während die zulässige Mindestqualität von der kundenseitigen Akzeptanzgrenze für Qualitätsverluste durch Sitzplatzmangel bestimmt wird.
Unter Anwendung anerkannter Regeln der Risikobewertung werden zur Bestimmung dieser Qualitätsverluste die Risikoparameter ‚Stehdichte‘, ‚Stehdauer‘ und ‚Stehplatzwahrscheinlichkeit‘ für sämtliche Linienabschnitte und alle Kundenfahrten einer Fahrplanfahrt ermittelt. Dies geschieht auf der Grundlage realisierter Fahrten in Form von haltestellenbasierten Quelle/Ziel-Matrizen.
Dem dynamischen Charakter der Risikoparameter im Fahrtverlauf folgend zeigen die Rechenergebnisse stark variierende Qualitätsverluste und liefern so ein transparentes Bild der von den Kunden erlebten Platzqualität. Damit ermöglichen sie die Ermittlung spezifischer Qualitätsniveaus für jede Quelle/Ziel-Gruppe der Matrix und, sofern im elektronischen Fahrgeldmanagementsystem eine Zuordnung von Fahrten zu Kunden erfolgt, auch für unterschiedliche Marktsegmente.
Aus den detaillierten Ergebnissen lassen sich zielgerichtete Angebotsmaßnahmen ableiten, deren Realisierung eine bessere Erfüllung der Anforderungen der relevanten Anspruchsgruppen verspricht und Ansätze für ein stärker marktorientiertes Vorgehen bei der Angebotsgestaltung liefert.:Abbildungsverzeichnis VI
Verzeichnis der Abkürzungen und Glossar XI
Verzeichnis der Formelzeichen und Symbole XIII
1.	Einleitung, Zielsetzung und Aufbau der Arbeit 1
2.	Status Quo der verkehrlichen Kapazitätsplanung im ÖPNV 5
2.1.	Verkehrliche und betriebliche Kapazitätsplanung 5
2.2.	Ziele der verkehrlichen Kapazitätsplanung 7
2.3.	Ermittlung der Platznachfrage 7
2.3.1.	Manuelle Zählungen 8
2.3.2.	Automatische Zählungen 9
2.3.3.	Auswertung von Vertriebsdaten 9
2.3.4.	Fahrgastbefragungen	 10
2.3.5.	Sonstige Erhebungsmethoden 10
2.4.	Ergebnisse der Nachfrageerhebung 10
2.4.1.	Verteilung und Schwankungen der Platznachfrage im Netz 10
2.4.2.	Stochastische Nachfrageschwankungen 14
2.5.	Einfluss der Erhebungsmethoden auf die Durchführung des Planungsprozesses 16
2.6.	Ermittlung des Platzangebotes 17
2.6.1.	Platzangebotes eines Fahrzeugs	 18
2.6.2.	Sitzplätze eines Fahrzeugs	 18
2.6.3.	Stehplätze eines Fahrzeugs 19
2.6.3.1.	Ermittlung der Stehplatzfläche eines Fahrzeugs 19
2.6.3.2.	Ermittlung der zulässigen Stehdichte im Fahrzeug 19
2.6.4.	Sitzplatzanteil eines Fahrzeugs 21
2.6.5.	Platzangebot eines Zeitintervalls	 23
2.6.6.	Vergleich von Platzangebot und Platznachfrage für ein Zeitintervall 24
2.7.	Berücksichtigung von Schwankungen der Nachfrage 25
2.8.	Begrenzung der Stehdauer der Fahrgäste 28
2.9.	Prüfung der Ergebnisse und Anpassung des Platzangebotes	29
2.10.	Auswirkung qualitätsbezogener Festlegungen auf das Planungsergebnis 30
2.11.	Praxis der verkehrlichen Kapazitätsplanung in Verkehrsunternehmen 34
3.	Anforderungen an die verkehrliche Kapazitätsplanung im ÖPNV 39
3.1.	Bestimmung der Anspruchsgruppen 39
3.2.	Struktur des Planungsprozesses	 40
3.3.	Anforderungen der Kunden	 45
3.3.1.	Anforderungen aus der Wahrnehmung von Dienstleistungsqualität 45
3.3.1.1.	Anforderungen aus der Diskonfirmationstheorie 46
3.3.1.2.	Anforderungen aus der Bildung von Erwartungen	 47
3.3.1.3.	Anforderungen aus der Wahrnehmung der Leistung 50
3.3.1.4.	Anforderungen aus der Einteilung in Zufriedenheitsfaktoren 51
3.3.1.5.	Anforderungen aus Einflüssen auf die Kundenzufriedenheit 53
3.3.1.5.1.	Assimilations-Kontrast-Theorie 54
3.3.1.5.2.	Attributionstheorie 54
3.3.1.5.3.	Gerechtigkeitstheorie	 55
3.3.1.5.4.	Theorie des wahrgenommenen Risikos 56
3.3.1.5.5.	Sitzplatz- und Stehflächenmangel als funktionales Risiko 58
3.3.2.	Anforderungen der Kunden aus Kundenbefragungen 63
3.3.2.1.	Befragungen zur Bevorzugung von Sitzplätzen 64
3.3.2.2.	Untersuchungen zur Akzeptanz von Stehdichte 69
3.3.2.3.	Untersuchungen zur Akzeptanz von Stehdauer 73
3.3.2.4.	Untersuchungen zum Zusammenhang zwischen Stehdichte und Stehdauer 79
3.4.	Anforderungen des Unternehmens 82
3.4.1.	Anforderungen aus dem Leistungsaustausch am Markt 83
3.4.2.	Anforderungen aus den Besonderheiten von Dienstleistungen 88
3.4.2.1.	Anforderungen aus der Immaterialität/Intangibilität von Dienstleistungen 89
3.4.2.2.	Anforderungen aus der Nichtlagerbarkeit/Nichttransportfähigkeit von Dienstleistungen 90
3.4.2.3.	Anforderungen aus der Integration des externen Faktors von Dienstleistungen 90
3.4.2.4.	Anforderungen aus der Heterogenität/Individualität von Dienstleistungen 92
3.4.2.5.	Zusammenfassung der Anforderungen aus den Besonderheiten von Dienstleistungen 92
3.4.3.	Anforderungen aus den Unternehmenszielen 93
3.4.3.1.	Anforderungen aus den Marketingstrategien des Unternehmens 96
3.4.3.1.1.	Anforderungen aus der Marktfeldstrategie 98
3.4.3.1.2.	Anforderungen aus der Marktsegmentierungsstrategie 99
3.4.3.1.3.	Anforderungen aus der auf die Abnehmer gerichteten Strategie 103 
3.4.4.	Anforderungen aus den Modellen der Dienstleistungsqualität 105
3.4.4.1.	Anforderungen aus dem GAP-Modell 107
3.4.4.2.	Anforderungen aus dem Dienstleistungsqualitätsmodell von Grönroos 110
3.4.4.3.	Anforderungen aus dem Dienstleistungsqualitätsmodell von Meyer/Mattmüller 111
3.4.4.4.	Anforderungen aus dem Dynamischen Prozessmodell von Boulding/Kalra/Staelin/Zeithaml 112
3.4.4.5.	Anforderungen aus dem Beziehungs-Qualitätsmodell von Liljander/Strandvik 113
3.4.4.6.	Anforderungen aus dem Qualitativen Zufriedenheitsmodell von Stauss/Neuhaus 115
3.4.5.	Anforderungen aus dem operativen Qualitätsmanagement 115
3.4.5.1.	Anforderungen aus der Qualitätsplanung 116
3.4.5.2.	Anforderungen aus der Qualitätslenkung 117
3.4.5.3.	Anforderungen aus der Qualitätsprüfung 118
3.4.5.4.	Anforderungen der DIN EN 13816 2002 zur Messung der Dienstleistungsqualität 122
3.4.5.5.	Anforderungen aus der Qualitätsmanagementdarlegung 126
3.4.6.	Anforderungen aus dem Prozessmanagement 127
3.4.7.	Anforderungen an die Erbringung von Kompatibilitätsnachweisen 129
3.5.	Anforderungen des Aufgabenträgers 129
3.6.	Defizite bei der Erfüllung von Anforderungen durch den Status quo der verkehrlichen Kapazitätsplanung 132
4.	Differenzierungsmodell  für eine anforderungsorientierte verkehrliche Kapazitätsplanung im ÖPNV 138	
4.1.	Entwicklungslinien einer anforderungsorientierten Kapazitätsplanung 138
4.2.	Entwicklungsschritte des Differenzierungsmodells 140
4.2.1.	Stärkung der Nachfrageorientierung 140
4.2.2.	Stärkung der Qualitätsorientierung 141
4.2.3.	Stärkung der Marktorientierung 143  
4.2.4.	Stärkung der Kostenorientierung 144
4.3.	Methodische Verbesserung der Prozesselemente 145
4.3.1.	Arbeitsgrundlagen des Planungsprozesses 146
4.3.2.	Prozesselement Planungsvorgaben 146
4.3.3.	Prozesselement Nachfrage	 146
4.3.4.	Prozesselement Angebot 148
4.3.5.	Prozesselement Messverfahren 148
4.3.5.1.	Definition der zu messenden Platzqualität 150
4.3.5.2.	Erläuterungen zur Messung der Platzqualität 152
4.3.5.3.	Messung der Risikoparameter für Platzqualität 155
4.3.5.4.	Ermittlung der Qualitätsverluste und der Platzqualität 158
4.3.5.5.	Variation des Qualitätsziels im Hinblick auf Marktsegmente 162
4.3.6.	Prozesselement Ermittlung der Planungsergebnisse 165
4.3.6.1.	Ermittlung qualitätsbezogener Kennzahlen 165
4.3.6.2.	Ermittlung von Kennzahlen zu Ressourceneinsatz, Betriebsleistung und Kosten 166
4.3.7.	Prozesselement Prüfung 166
4.3.7.1.	Prüfung der Konformität mit den Unternehmenszielen 167
4.3.7.2.	Prüfung der Konformität mit Anforderungen des Aufgabenträgers	 167
4.3.8.	Prozesselement Veränderung 169
5.	Anwendung des Differenzierungsmodells 171
5.1.	Gestaltung des Anwendungsbeispiels	171
5.1.1.	Festlegungen zur Infrastruktur 171
5.1.2.	Festlegungen zum Fahrbetrieb 172
5.1.3.	Festlegungen zum Platzangebot	 173
5.1.4.	Festlegungen zur Platznachfrage 173
5.1.5	Festlegungen zur Platzqualität 174
5.2.	Ergebnisse der anwendungsorientierten Planung 175
5.2.1.	Standardergebnisse 175
5.2.2.	Relevante Einflüsse 182
5.2.2.1.	Bemessungsnachfrage 182
5.2.2.2.	Platzangebot 183
5.2.2.3.	Taktverdichtung	 184
5.2.2.4.	Qualitätsziel 186
5.2.2.5.	Sitzplatzanteil des Fahrzeugs  187
5.2.2.6.	Beförderungsgeschwindigkeit 187
5.2.2.7.	Fahrgastwechsel 188
5.2.3.	Anforderungsorientierung 189
5.2.3.1.	Verbesserung der Nachfrageorientierung 190
5.2.3.2.	Stärkung der Qualitätsorientierung 190
5.2.3.3.	Implementierung der Marktorientierung 191
5.2.3.4.	Stärkung der Kostenorientierung 192
6.	Fazit und Ausblick 195
Quellenverzeichnis 199
Verzeichnis der Anhänge 208
Anhang A:	Befragung größerer Verkehrsunternehmen zur Praxis der verkehrliche Kapazitätsplanung im schienengebundenen ÖPNV 208 
Anhang B:	Befragung der Fahrgäste zum Sitzplatzwunsch und zur Fahrtdauer 211
Anhang C:	Befragung der U-Bahn-Fahrgäste zum Sitzplatzbedarf im Zusammenhang mit der Beschäftigung während der Fahrt sowie mit dem Alter und dem Geschlecht 214
Anhang D:	Befragung der U-Bahn-Fahrgäste zur akzeptierten Stehdauer im Zusammenhang mit der Stehplatzdichte sowie mit dem Alter und dem Geschlecht 216
Anhang E:	Befragung der U-Bahn-Fahrgäste der Linie U3 zum Sitzplatzwunsch und zur akzeptierten Stehdauer im Zusammenhang mit der während der Befragung vorgefundenen Stehplatzdichte 	217</dc:description>
   <dc:description>Transport related capacity planning constitutes an important business process for public transport companies. Respective results have a crucial impact on the allocation of costly resources and on public transport services. Despite this significance, planning is mostly limited to implementing standards put forth by authorities thereby neglecting to address complementary customer and corporate needs.
The paper determines relevant stakeholder requirements and depicts implementation deficits of current planning methods. Furthermore, these deficiencies allow for laying the foundation to develop a new requirement based planning methodology. Against this backdrop the quality of available space from a customer perspective is defined and derived. Moreover, from the aforementioned perspective the ample provision of available space is brought to focus while bearing a minimal customer based quality threshold - determined by loss of seating capacity -  in mind.
By applying all renowned standards pertaining to risk assessment relevant parameters such as standing density, - duration and -probability are determined for all customer related trips of a schedule. The aforementioned approach is based on realized trips in relation to an underlying stop-oriented origin-destination-matrix.
Following dynamic characteristics of risk parameters en route the calculation results depict a stark variation in outcome as to loss of quality. Hence, a vivid picture attributed to customer`s perceived seating quality emerges. In so far as an electronic fare management system is in place specific quality levels with regard to an underlying origin-destination-matrix based on assigned customer trips can be derived while also taking various market segments into consideration.
Emphasis is laid upon a market-oriented approach bringing to focus enhanced services. Moreover, detailed results allow for deriving concise measures, which in turn improve compliance pertaining to relevant stakeholder requirements.:Abbildungsverzeichnis VI
Verzeichnis der Abkürzungen und Glossar XI
Verzeichnis der Formelzeichen und Symbole XIII
1.	Einleitung, Zielsetzung und Aufbau der Arbeit 1
2.	Status Quo der verkehrlichen Kapazitätsplanung im ÖPNV 5
2.1.	Verkehrliche und betriebliche Kapazitätsplanung 5
2.2.	Ziele der verkehrlichen Kapazitätsplanung 7
2.3.	Ermittlung der Platznachfrage 7
2.3.1.	Manuelle Zählungen 8
2.3.2.	Automatische Zählungen 9
2.3.3.	Auswertung von Vertriebsdaten 9
2.3.4.	Fahrgastbefragungen	 10
2.3.5.	Sonstige Erhebungsmethoden 10
2.4.	Ergebnisse der Nachfrageerhebung 10
2.4.1.	Verteilung und Schwankungen der Platznachfrage im Netz 10
2.4.2.	Stochastische Nachfrageschwankungen 14
2.5.	Einfluss der Erhebungsmethoden auf die Durchführung des Planungsprozesses 16
2.6.	Ermittlung des Platzangebotes 17
2.6.1.	Platzangebotes eines Fahrzeugs	 18
2.6.2.	Sitzplätze eines Fahrzeugs	 18
2.6.3.	Stehplätze eines Fahrzeugs 19
2.6.3.1.	Ermittlung der Stehplatzfläche eines Fahrzeugs 19
2.6.3.2.	Ermittlung der zulässigen Stehdichte im Fahrzeug 19
2.6.4.	Sitzplatzanteil eines Fahrzeugs 21
2.6.5.	Platzangebot eines Zeitintervalls	 23
2.6.6.	Vergleich von Platzangebot und Platznachfrage für ein Zeitintervall 24
2.7.	Berücksichtigung von Schwankungen der Nachfrage 25
2.8.	Begrenzung der Stehdauer der Fahrgäste 28
2.9.	Prüfung der Ergebnisse und Anpassung des Platzangebotes	29
2.10.	Auswirkung qualitätsbezogener Festlegungen auf das Planungsergebnis 30
2.11.	Praxis der verkehrlichen Kapazitätsplanung in Verkehrsunternehmen 34
3.	Anforderungen an die verkehrliche Kapazitätsplanung im ÖPNV 39
3.1.	Bestimmung der Anspruchsgruppen 39
3.2.	Struktur des Planungsprozesses	 40
3.3.	Anforderungen der Kunden	 45
3.3.1.	Anforderungen aus der Wahrnehmung von Dienstleistungsqualität 45
3.3.1.1.	Anforderungen aus der Diskonfirmationstheorie 46
3.3.1.2.	Anforderungen aus der Bildung von Erwartungen	 47
3.3.1.3.	Anforderungen aus der Wahrnehmung der Leistung 50
3.3.1.4.	Anforderungen aus der Einteilung in Zufriedenheitsfaktoren 51
3.3.1.5.	Anforderungen aus Einflüssen auf die Kundenzufriedenheit 53
3.3.1.5.1.	Assimilations-Kontrast-Theorie 54
3.3.1.5.2.	Attributionstheorie 54
3.3.1.5.3.	Gerechtigkeitstheorie	 55
3.3.1.5.4.	Theorie des wahrgenommenen Risikos 56
3.3.1.5.5.	Sitzplatz- und Stehflächenmangel als funktionales Risiko 58
3.3.2.	Anforderungen der Kunden aus Kundenbefragungen 63
3.3.2.1.	Befragungen zur Bevorzugung von Sitzplätzen 64
3.3.2.2.	Untersuchungen zur Akzeptanz von Stehdichte 69
3.3.2.3.	Untersuchungen zur Akzeptanz von Stehdauer 73
3.3.2.4.	Untersuchungen zum Zusammenhang zwischen Stehdichte und Stehdauer 79
3.4.	Anforderungen des Unternehmens 82
3.4.1.	Anforderungen aus dem Leistungsaustausch am Markt 83
3.4.2.	Anforderungen aus den Besonderheiten von Dienstleistungen 88
3.4.2.1.	Anforderungen aus der Immaterialität/Intangibilität von Dienstleistungen 89
3.4.2.2.	Anforderungen aus der Nichtlagerbarkeit/Nichttransportfähigkeit von Dienstleistungen 90
3.4.2.3.	Anforderungen aus der Integration des externen Faktors von Dienstleistungen 90
3.4.2.4.	Anforderungen aus der Heterogenität/Individualität von Dienstleistungen 92
3.4.2.5.	Zusammenfassung der Anforderungen aus den Besonderheiten von Dienstleistungen 92
3.4.3.	Anforderungen aus den Unternehmenszielen 93
3.4.3.1.	Anforderungen aus den Marketingstrategien des Unternehmens 96
3.4.3.1.1.	Anforderungen aus der Marktfeldstrategie 98
3.4.3.1.2.	Anforderungen aus der Marktsegmentierungsstrategie 99
3.4.3.1.3.	Anforderungen aus der auf die Abnehmer gerichteten Strategie 103 
3.4.4.	Anforderungen aus den Modellen der Dienstleistungsqualität 105
3.4.4.1.	Anforderungen aus dem GAP-Modell 107
3.4.4.2.	Anforderungen aus dem Dienstleistungsqualitätsmodell von Grönroos 110
3.4.4.3.	Anforderungen aus dem Dienstleistungsqualitätsmodell von Meyer/Mattmüller 111
3.4.4.4.	Anforderungen aus dem Dynamischen Prozessmodell von Boulding/Kalra/Staelin/Zeithaml 112
3.4.4.5.	Anforderungen aus dem Beziehungs-Qualitätsmodell von Liljander/Strandvik 113
3.4.4.6.	Anforderungen aus dem Qualitativen Zufriedenheitsmodell von Stauss/Neuhaus 115
3.4.5.	Anforderungen aus dem operativen Qualitätsmanagement 115
3.4.5.1.	Anforderungen aus der Qualitätsplanung 116
3.4.5.2.	Anforderungen aus der Qualitätslenkung 117
3.4.5.3.	Anforderungen aus der Qualitätsprüfung 118
3.4.5.4.	Anforderungen der DIN EN 13816 2002 zur Messung der Dienstleistungsqualität 122
3.4.5.5.	Anforderungen aus der Qualitätsmanagementdarlegung 126
3.4.6.	Anforderungen aus dem Prozessmanagement 127
3.4.7.	Anforderungen an die Erbringung von Kompatibilitätsnachweisen 129
3.5.	Anforderungen des Aufgabenträgers 129
3.6.	Defizite bei der Erfüllung von Anforderungen durch den Status quo der verkehrlichen Kapazitätsplanung 132
4.	Differenzierungsmodell  für eine anforderungsorientierte verkehrliche Kapazitätsplanung im ÖPNV 138	
4.1.	Entwicklungslinien einer anforderungsorientierten Kapazitätsplanung 138
4.2.	Entwicklungsschritte des Differenzierungsmodells 140
4.2.1.	Stärkung der Nachfrageorientierung 140
4.2.2.	Stärkung der Qualitätsorientierung 141
4.2.3.	Stärkung der Marktorientierung 143  
4.2.4.	Stärkung der Kostenorientierung 144
4.3.	Methodische Verbesserung der Prozesselemente 145
4.3.1.	Arbeitsgrundlagen des Planungsprozesses 146
4.3.2.	Prozesselement Planungsvorgaben 146
4.3.3.	Prozesselement Nachfrage	 146
4.3.4.	Prozesselement Angebot 148
4.3.5.	Prozesselement Messverfahren 148
4.3.5.1.	Definition der zu messenden Platzqualität 150
4.3.5.2.	Erläuterungen zur Messung der Platzqualität 152
4.3.5.3.	Messung der Risikoparameter für Platzqualität 155
4.3.5.4.	Ermittlung der Qualitätsverluste und der Platzqualität 158
4.3.5.5.	Variation des Qualitätsziels im Hinblick auf Marktsegmente 162
4.3.6.	Prozesselement Ermittlung der Planungsergebnisse 165
4.3.6.1.	Ermittlung qualitätsbezogener Kennzahlen 165
4.3.6.2.	Ermittlung von Kennzahlen zu Ressourceneinsatz, Betriebsleistung und Kosten 166
4.3.7.	Prozesselement Prüfung 166
4.3.7.1.	Prüfung der Konformität mit den Unternehmenszielen 167
4.3.7.2.	Prüfung der Konformität mit Anforderungen des Aufgabenträgers	 167
4.3.8.	Prozesselement Veränderung 169
5.	Anwendung des Differenzierungsmodells 171
5.1.	Gestaltung des Anwendungsbeispiels	171
5.1.1.	Festlegungen zur Infrastruktur 171
5.1.2.	Festlegungen zum Fahrbetrieb 172
5.1.3.	Festlegungen zum Platzangebot	 173
5.1.4.	Festlegungen zur Platznachfrage 173
5.1.5	Festlegungen zur Platzqualität 174
5.2.	Ergebnisse der anwendungsorientierten Planung 175
5.2.1.	Standardergebnisse 175
5.2.2.	Relevante Einflüsse 182
5.2.2.1.	Bemessungsnachfrage 182
5.2.2.2.	Platzangebot 183
5.2.2.3.	Taktverdichtung	 184
5.2.2.4.	Qualitätsziel 186
5.2.2.5.	Sitzplatzanteil des Fahrzeugs  187
5.2.2.6.	Beförderungsgeschwindigkeit 187
5.2.2.7.	Fahrgastwechsel 188
5.2.3.	Anforderungsorientierung 189
5.2.3.1.	Verbesserung der Nachfrageorientierung 190
5.2.3.2.	Stärkung der Qualitätsorientierung 190
5.2.3.3.	Implementierung der Marktorientierung 191
5.2.3.4.	Stärkung der Kostenorientierung 192
6.	Fazit und Ausblick 195
Quellenverzeichnis 199
Verzeichnis der Anhänge 208
Anhang A:	Befragung größerer Verkehrsunternehmen zur Praxis der verkehrliche Kapazitätsplanung im schienengebundenen ÖPNV 208 
Anhang B:	Befragung der Fahrgäste zum Sitzplatzwunsch und zur Fahrtdauer 211
Anhang C:	Befragung der U-Bahn-Fahrgäste zum Sitzplatzbedarf im Zusammenhang mit der Beschäftigung während der Fahrt sowie mit dem Alter und dem Geschlecht 214
Anhang D:	Befragung der U-Bahn-Fahrgäste zur akzeptierten Stehdauer im Zusammenhang mit der Stehplatzdichte sowie mit dem Alter und dem Geschlecht 216
Anhang E:	Befragung der U-Bahn-Fahrgäste der Linie U3 zum Sitzplatzwunsch und zur akzeptierten Stehdauer im Zusammenhang mit der während der Befragung vorgefundenen Stehplatzdichte 	217</dc:description>
   <dc:subject>Akzeptanzgrenze, Anspruchsgruppe, Bemessungsnachfrage, Differenzierungsmodell, Elektronisches Fahrgeldmanagement, Kapazitätsplanung, Kundenorientierung, Marktsegmentierung, Nachfrageschwankung, ÖPNV, Platzangebot, Platzqualität, Prozessfähigkeit, Qualitätsniveau, Qualitätsrisiko, Qualitätsverlust, Quelle-Ziel-Gruppe, Quelle-Ziel-Matrix, Risikoparameter, Stehdauer, Stehdichte, Stehplatzwahrscheinlichkeit</dc:subject>
   <dc:subject>Limit of acceptance, Stakeholder, Assessment need, Differentiation model, Electronic fare management, Capacity planning, Customer orientation, Market segmentation, Demand variation, Local public transport, Space availability, Space quality, Process capability, Quality level, Quality risk, Quality loss, Origin-Destination-Flow, Origin-Destination-Matrix, Risk parameter, Standing duration, Standing density, Standing room likelihood</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/380</dc:subject>
   <dc:creator>Bergner,Ulrich</dc:creator>
   <dc:contributor>König,Rainer</dc:contributor>
   <dc:contributor>Sommer,Carsten</dc:contributor>
   <dc:contributor>Technische Universität Dresden</dc:contributor>
   <dc:date>2018-02-21</dc:date>
   <dc:date>2018-08-23</dc:date>
   <dc:date>2018</dc:date>
   <dc:date>2018-10-22</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>
                    doctoralThesis
                </dc:type>
</oai_dc:dc>', false, 'qucosa:31971');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (70, 22, '2018-11-15 12:27:49.203+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Differenzierungsmodell für eine anforderungsorientierte verkehrliche Kapazitätsplanung im ÖPNV</dc:title>
   <dcterms:alternative ddb:type="translated" xml:lang="eng" xsi:type="ddb:talternativeISO639-2">Differentiation model for requirement oriented planning of transportation capacity in local public transport</dcterms:alternative>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Ulrich</pc:foreName>
            <pc:surName>Bergner</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Akzeptanzgrenze</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Anspruchsgruppe</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Bemessungsnachfrage</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Differenzierungsmodell</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Elektronisches Fahrgeldmanagement</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Kapazitätsplanung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Kundenorientierung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Marktsegmentierung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Nachfrageschwankung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">ÖPNV</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Platzangebot</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Platzqualität</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Prozessfähigkeit</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Qualitätsniveau</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Qualitätsrisiko</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Qualitätsverlust</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Quelle-Ziel-Gruppe</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Quelle-Ziel-Matrix</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Risikoparameter</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Stehdauer</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Stehdichte</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Stehplatzwahrscheinlichkeit</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Limit of acceptance</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Stakeholder</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Assessment need</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Differentiation model</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Electronic fare management</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Capacity planning</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Customer orientation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Market segmentation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Demand variation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Local public transport</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Space availability</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Space quality</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Process capability</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Quality level</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Quality risk</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Quality loss</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Origin-Destination-Flow</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Origin-Destination-Matrix</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Risk parameter</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Standing duration</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Standing density</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Standing room likelihood</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">QR 860</dc:subject>
   <dcterms:tableOfContents ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Abbildungsverzeichnis VI
Verzeichnis der Abkürzungen und Glossar XI
Verzeichnis der Formelzeichen und Symbole XIII
1.	Einleitung, Zielsetzung und Aufbau der Arbeit 1
2.	Status Quo der verkehrlichen Kapazitätsplanung im ÖPNV 5
2.1.	Verkehrliche und betriebliche Kapazitätsplanung 5
2.2.	Ziele der verkehrlichen Kapazitätsplanung 7
2.3.	Ermittlung der Platznachfrage 7
2.3.1.	Manuelle Zählungen 8
2.3.2.	Automatische Zählungen 9
2.3.3.	Auswertung von Vertriebsdaten 9
2.3.4.	Fahrgastbefragungen	 10
2.3.5.	Sonstige Erhebungsmethoden 10
2.4.	Ergebnisse der Nachfrageerhebung 10
2.4.1.	Verteilung und Schwankungen der Platznachfrage im Netz 10
2.4.2.	Stochastische Nachfrageschwankungen 14
2.5.	Einfluss der Erhebungsmethoden auf die Durchführung des Planungsprozesses 16
2.6.	Ermittlung des Platzangebotes 17
2.6.1.	Platzangebotes eines Fahrzeugs	 18
2.6.2.	Sitzplätze eines Fahrzeugs	 18
2.6.3.	Stehplätze eines Fahrzeugs 19
2.6.3.1.	Ermittlung der Stehplatzfläche eines Fahrzeugs 19
2.6.3.2.	Ermittlung der zulässigen Stehdichte im Fahrzeug 19
2.6.4.	Sitzplatzanteil eines Fahrzeugs 21
2.6.5.	Platzangebot eines Zeitintervalls	 23
2.6.6.	Vergleich von Platzangebot und Platznachfrage für ein Zeitintervall 24
2.7.	Berücksichtigung von Schwankungen der Nachfrage 25
2.8.	Begrenzung der Stehdauer der Fahrgäste 28
2.9.	Prüfung der Ergebnisse und Anpassung des Platzangebotes	29
2.10.	Auswirkung qualitätsbezogener Festlegungen auf das Planungsergebnis 30
2.11.	Praxis der verkehrlichen Kapazitätsplanung in Verkehrsunternehmen 34
3.	Anforderungen an die verkehrliche Kapazitätsplanung im ÖPNV 39
3.1.	Bestimmung der Anspruchsgruppen 39
3.2.	Struktur des Planungsprozesses	 40
3.3.	Anforderungen der Kunden	 45
3.3.1.	Anforderungen aus der Wahrnehmung von Dienstleistungsqualität 45
3.3.1.1.	Anforderungen aus der Diskonfirmationstheorie 46
3.3.1.2.	Anforderungen aus der Bildung von Erwartungen	 47
3.3.1.3.	Anforderungen aus der Wahrnehmung der Leistung 50
3.3.1.4.	Anforderungen aus der Einteilung in Zufriedenheitsfaktoren 51
3.3.1.5.	Anforderungen aus Einflüssen auf die Kundenzufriedenheit 53
3.3.1.5.1.	Assimilations-Kontrast-Theorie 54
3.3.1.5.2.	Attributionstheorie 54
3.3.1.5.3.	Gerechtigkeitstheorie	 55
3.3.1.5.4.	Theorie des wahrgenommenen Risikos 56
3.3.1.5.5.	Sitzplatz- und Stehflächenmangel als funktionales Risiko 58
3.3.2.	Anforderungen der Kunden aus Kundenbefragungen 63
3.3.2.1.	Befragungen zur Bevorzugung von Sitzplätzen 64
3.3.2.2.	Untersuchungen zur Akzeptanz von Stehdichte 69
3.3.2.3.	Untersuchungen zur Akzeptanz von Stehdauer 73
3.3.2.4.	Untersuchungen zum Zusammenhang zwischen Stehdichte und Stehdauer 79
3.4.	Anforderungen des Unternehmens 82
3.4.1.	Anforderungen aus dem Leistungsaustausch am Markt 83
3.4.2.	Anforderungen aus den Besonderheiten von Dienstleistungen 88
3.4.2.1.	Anforderungen aus der Immaterialität/Intangibilität von Dienstleistungen 89
3.4.2.2.	Anforderungen aus der Nichtlagerbarkeit/Nichttransportfähigkeit von Dienstleistungen 90
3.4.2.3.	Anforderungen aus der Integration des externen Faktors von Dienstleistungen 90
3.4.2.4.	Anforderungen aus der Heterogenität/Individualität von Dienstleistungen 92
3.4.2.5.	Zusammenfassung der Anforderungen aus den Besonderheiten von Dienstleistungen 92
3.4.3.	Anforderungen aus den Unternehmenszielen 93
3.4.3.1.	Anforderungen aus den Marketingstrategien des Unternehmens 96
3.4.3.1.1.	Anforderungen aus der Marktfeldstrategie 98
3.4.3.1.2.	Anforderungen aus der Marktsegmentierungsstrategie 99
3.4.3.1.3.	Anforderungen aus der auf die Abnehmer gerichteten Strategie 103 
3.4.4.	Anforderungen aus den Modellen der Dienstleistungsqualität 105
3.4.4.1.	Anforderungen aus dem GAP-Modell 107
3.4.4.2.	Anforderungen aus dem Dienstleistungsqualitätsmodell von Grönroos 110
3.4.4.3.	Anforderungen aus dem Dienstleistungsqualitätsmodell von Meyer/Mattmüller 111
3.4.4.4.	Anforderungen aus dem Dynamischen Prozessmodell von Boulding/Kalra/Staelin/Zeithaml 112
3.4.4.5.	Anforderungen aus dem Beziehungs-Qualitätsmodell von Liljander/Strandvik 113
3.4.4.6.	Anforderungen aus dem Qualitativen Zufriedenheitsmodell von Stauss/Neuhaus 115
3.4.5.	Anforderungen aus dem operativen Qualitätsmanagement 115
3.4.5.1.	Anforderungen aus der Qualitätsplanung 116
3.4.5.2.	Anforderungen aus der Qualitätslenkung 117
3.4.5.3.	Anforderungen aus der Qualitätsprüfung 118
3.4.5.4.	Anforderungen der DIN EN 13816 2002 zur Messung der Dienstleistungsqualität 122
3.4.5.5.	Anforderungen aus der Qualitätsmanagementdarlegung 126
3.4.6.	Anforderungen aus dem Prozessmanagement 127
3.4.7.	Anforderungen an die Erbringung von Kompatibilitätsnachweisen 129
3.5.	Anforderungen des Aufgabenträgers 129
3.6.	Defizite bei der Erfüllung von Anforderungen durch den Status quo der verkehrlichen Kapazitätsplanung 132
4.	Differenzierungsmodell  für eine anforderungsorientierte verkehrliche Kapazitätsplanung im ÖPNV 138	
4.1.	Entwicklungslinien einer anforderungsorientierten Kapazitätsplanung 138
4.2.	Entwicklungsschritte des Differenzierungsmodells 140
4.2.1.	Stärkung der Nachfrageorientierung 140
4.2.2.	Stärkung der Qualitätsorientierung 141
4.2.3.	Stärkung der Marktorientierung 143  
4.2.4.	Stärkung der Kostenorientierung 144
4.3.	Methodische Verbesserung der Prozesselemente 145
4.3.1.	Arbeitsgrundlagen des Planungsprozesses 146
4.3.2.	Prozesselement Planungsvorgaben 146
4.3.3.	Prozesselement Nachfrage	 146
4.3.4.	Prozesselement Angebot 148
4.3.5.	Prozesselement Messverfahren 148
4.3.5.1.	Definition der zu messenden Platzqualität 150
4.3.5.2.	Erläuterungen zur Messung der Platzqualität 152
4.3.5.3.	Messung der Risikoparameter für Platzqualität 155
4.3.5.4.	Ermittlung der Qualitätsverluste und der Platzqualität 158
4.3.5.5.	Variation des Qualitätsziels im Hinblick auf Marktsegmente 162
4.3.6.	Prozesselement Ermittlung der Planungsergebnisse 165
4.3.6.1.	Ermittlung qualitätsbezogener Kennzahlen 165
4.3.6.2.	Ermittlung von Kennzahlen zu Ressourceneinsatz, Betriebsleistung und Kosten 166
4.3.7.	Prozesselement Prüfung 166
4.3.7.1.	Prüfung der Konformität mit den Unternehmenszielen 167
4.3.7.2.	Prüfung der Konformität mit Anforderungen des Aufgabenträgers	 167
4.3.8.	Prozesselement Veränderung 169
5.	Anwendung des Differenzierungsmodells 171
5.1.	Gestaltung des Anwendungsbeispiels	171
5.1.1.	Festlegungen zur Infrastruktur 171
5.1.2.	Festlegungen zum Fahrbetrieb 172
5.1.3.	Festlegungen zum Platzangebot	 173
5.1.4.	Festlegungen zur Platznachfrage 173
5.1.5	Festlegungen zur Platzqualität 174
5.2.	Ergebnisse der anwendungsorientierten Planung 175
5.2.1.	Standardergebnisse 175
5.2.2.	Relevante Einflüsse 182
5.2.2.1.	Bemessungsnachfrage 182
5.2.2.2.	Platzangebot 183
5.2.2.3.	Taktverdichtung	 184
5.2.2.4.	Qualitätsziel 186
5.2.2.5.	Sitzplatzanteil des Fahrzeugs  187
5.2.2.6.	Beförderungsgeschwindigkeit 187
5.2.2.7.	Fahrgastwechsel 188
5.2.3.	Anforderungsorientierung 189
5.2.3.1.	Verbesserung der Nachfrageorientierung 190
5.2.3.2.	Stärkung der Qualitätsorientierung 190
5.2.3.3.	Implementierung der Marktorientierung 191
5.2.3.4.	Stärkung der Kostenorientierung 192
6.	Fazit und Ausblick 195
Quellenverzeichnis 199
Verzeichnis der Anhänge 208
Anhang A:	Befragung größerer Verkehrsunternehmen zur Praxis der verkehrliche Kapazitätsplanung im schienengebundenen ÖPNV 208 
Anhang B:	Befragung der Fahrgäste zum Sitzplatzwunsch und zur Fahrtdauer 211
Anhang C:	Befragung der U-Bahn-Fahrgäste zum Sitzplatzbedarf im Zusammenhang mit der Beschäftigung während der Fahrt sowie mit dem Alter und dem Geschlecht 214
Anhang D:	Befragung der U-Bahn-Fahrgäste zur akzeptierten Stehdauer im Zusammenhang mit der Stehplatzdichte sowie mit dem Alter und dem Geschlecht 216
Anhang E:	Befragung der U-Bahn-Fahrgäste der Linie U3 zum Sitzplatzwunsch und zur akzeptierten Stehdauer im Zusammenhang mit der während der Befragung vorgefundenen Stehplatzdichte 	217</dcterms:tableOfContents>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Die verkehrliche Kapazitätsplanung ist für ÖPNV-Unternehmen ein wichtiger Geschäftsprozess. Die Planungsergebnisse entscheiden maßgeblich über den Einsatz kostenträchtiger Ressourcen und über den Erfolg der ÖPNV-Dienstleistung am Verkehrsmarkt. Trotz dieser Bedeutung beschränkt sich die Planung bisher noch weitgehend auf die Umsetzung von Aufgabenträgervorgaben und vernachlässigt ergänzende Anforderungen der Kunden und Unternehmen.
Die vorliegende Arbeit ermittelt die Anforderungen aller relevanten Anspruchsgruppen und benennt Umsetzungsdefizite der heutigen Planungspraxis. Diese Defizite bilden die Grundlage für die Entwicklung einer neuen, anforderungsorientierten Planungsmethodik. Für diese Methodik wird die Qualität des Platzangebotes aus der Perspektive der Kunden definiert und ermittelt. Aus Kundensicht stellt dabei eine uneingeschränkte Sitzplatzverfügbarkeit das höchste Qualitätsniveau dar, während die zulässige Mindestqualität von der kundenseitigen Akzeptanzgrenze für Qualitätsverluste durch Sitzplatzmangel bestimmt wird.
Unter Anwendung anerkannter Regeln der Risikobewertung werden zur Bestimmung dieser Qualitätsverluste die Risikoparameter ‚Stehdichte‘, ‚Stehdauer‘ und ‚Stehplatzwahrscheinlichkeit‘ für sämtliche Linienabschnitte und alle Kundenfahrten einer Fahrplanfahrt ermittelt. Dies geschieht auf der Grundlage realisierter Fahrten in Form von haltestellenbasierten Quelle/Ziel-Matrizen.
Dem dynamischen Charakter der Risikoparameter im Fahrtverlauf folgend zeigen die Rechenergebnisse stark variierende Qualitätsverluste und liefern so ein transparentes Bild der von den Kunden erlebten Platzqualität. Damit ermöglichen sie die Ermittlung spezifischer Qualitätsniveaus für jede Quelle/Ziel-Gruppe der Matrix und, sofern im elektronischen Fahrgeldmanagementsystem eine Zuordnung von Fahrten zu Kunden erfolgt, auch für unterschiedliche Marktsegmente.
Aus den detaillierten Ergebnissen lassen sich zielgerichtete Angebotsmaßnahmen ableiten, deren Realisierung eine bessere Erfüllung der Anforderungen der relevanten Anspruchsgruppen verspricht und Ansätze für ein stärker marktorientiertes Vorgehen bei der Angebotsgestaltung liefert.</dcterms:abstract>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">Transport related capacity planning constitutes an important business process for public transport companies. Respective results have a crucial impact on the allocation of costly resources and on public transport services. Despite this significance, planning is mostly limited to implementing standards put forth by authorities thereby neglecting to address complementary customer and corporate needs.
The paper determines relevant stakeholder requirements and depicts implementation deficits of current planning methods. Furthermore, these deficiencies allow for laying the foundation to develop a new requirement based planning methodology. Against this backdrop the quality of available space from a customer perspective is defined and derived. Moreover, from the aforementioned perspective the ample provision of available space is brought to focus while bearing a minimal customer based quality threshold - determined by loss of seating capacity -  in mind.
By applying all renowned standards pertaining to risk assessment relevant parameters such as standing density, - duration and -probability are determined for all customer related trips of a schedule. The aforementioned approach is based on realized trips in relation to an underlying stop-oriented origin-destination-matrix.
Following dynamic characteristics of risk parameters en route the calculation results depict a stark variation in outcome as to loss of quality. Hence, a vivid picture attributed to customer`s perceived seating quality emerges. In so far as an electronic fare management system is in place specific quality levels with regard to an underlying origin-destination-matrix based on assigned customer trips can be derived while also taking various market segments into consideration.
Emphasis is laid upon a market-oriented approach bringing to focus enhanced services. Moreover, detailed results allow for deriving concise measures, which in turn improve compliance pertaining to relevant stakeholder requirements.</dcterms:abstract>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Rainer</pc:foreName>
            <pc:surName>König</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Carsten</pc:foreName>
            <pc:surName>Sommer</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:dateSubmitted xsi:type="dcterms:W3CDTF">2018-02-21</dcterms:dateSubmitted>
   <dcterms:dateAccepted xsi:type="dcterms:W3CDTF">2018-08-23</dcterms:dateAccepted>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-10-22</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2018</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">doctoralThesis</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-319716</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>thesis.doctoral</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Technische Universität Dresden</cc:name>
            <cc:place>01069 Dresden</cc:place>
            <cc:department>
               <cc:name>Fakultät Verkehrswissenschaften ''Friedrich List''</cc:name>
            </cc:department>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:31971/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-319716</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:31971');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (72, 17, '2018-11-15 12:28:21.396+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Analyse eines Kreisverkehrs in verschiedenen Verkehrsnachfragesituationen</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-237739</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:description>Eine realistische Simulation von Verkehrsabläufen kann einen wichtigen Beitrag zur Verkehrsplanung leisten. In dieser Arbeit wird dafür in den bestehenden mikroskopischen Simulator MovSim ein Kreisverkehr implementiert. Anschließende Analysen, die unter Verwendung des Intelligent-Driver-Modells und des Spurwechselmodells MOBIL durchgeführt wurden, legen nahe, dass die relativen Ergebnisse, die sich durch Veränderung des Verkehrsflusses und der Modellparameter ergeben, realitätsnah sind. Zudem konnte gezeigt werden, dass der Einfluss des Verkehrsstromes in der Kreisfahrbahn im Handbuch für die Bemessung von Straßenverkehrsanlagen deutlich stärker ist als in der Simulation.</dc:description>
   <dc:subject>info:eu-repo/classification/ddc/380</dc:subject>
   <dc:subject>Kreisverkehr, MovSim, IDM, Verkehrssimulation</dc:subject>
   <dc:subject>Roundabout, MovSim, IDM, traffic simulation</dc:subject>
   <dc:creator>Schelp,Jonas</dc:creator>
   <dc:contributor>Treiber,Martin</dc:contributor>
   <dc:contributor>Technische Universität Dresden</dc:contributor>
   <dc:date>2018-07-11</dc:date>
   <dc:date>2018-07-10</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>bachelorThesis</dc:type>
</oai_dc:dc>', false, 'qucosa:31141');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (73, 22, '2018-11-15 12:28:21.396+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Analyse eines Kreisverkehrs in verschiedenen Verkehrsnachfragesituationen</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Jonas</pc:foreName>
            <pc:surName>Schelp</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 4600</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ST 620</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Kreisverkehr</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">MovSim</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">IDM</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Verkehrssimulation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Roundabout</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">MovSim</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">IDM</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">traffic simulation</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Eine realistische Simulation von Verkehrsabläufen kann einen wichtigen Beitrag zur Verkehrsplanung leisten. In dieser Arbeit wird dafür in den bestehenden mikroskopischen Simulator MovSim ein Kreisverkehr implementiert. Anschließende Analysen, die unter Verwendung des Intelligent-Driver-Modells und des Spurwechselmodells MOBIL durchgeführt wurden, legen nahe, dass die relativen Ergebnisse, die sich durch Veränderung des Verkehrsflusses und der Modellparameter ergeben, realitätsnah sind. Zudem konnte gezeigt werden, dass der Einfluss des Verkehrsstromes in der Kreisfahrbahn im Handbuch für die Bemessung von Straßenverkehrsanlagen deutlich stärker ist als in der Simulation.</dcterms:abstract>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Martin</pc:foreName>
            <pc:surName>Treiber</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Martin</pc:foreName>
            <pc:surName>Treiber</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:dateSubmitted xsi:type="dcterms:W3CDTF">2018-07-10</dcterms:dateSubmitted>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-07-11</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">bachelorThesis</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-237739</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>bachelor</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Technische Universität Dresden</cc:name>
            <cc:place>Dresden</cc:place>
            <cc:department>
               <cc:name>Fakultät Verkehrswissenschaften "Friedrich List"</cc:name>
            </cc:department>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:31141/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-237739</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:31141');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (76, 17, '2018-11-15 12:29:02.556+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Stickoxide, Partikel und Kohlendioxid: Grenzwerte, Konflikte und Handlungsmöglichkeiten kommunaler Luftreinhaltung im Verkehrsbereich:Informationen und Empfehlungen für Mitarbeiter deutscher Kommunen</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-216747</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:description>Die ab dem 1.1.2010 geltenden erweiterten Luftqualitätsgrenzwerte stellen die Kom-munen vor allem in verkehrlich belasteten Gebieten vor Probleme. Zum einen haben die Kommunen sicherzustellen, dass die Immissionsgrenzwerte eingehalten werden, zum anderen stehen ihnen aber nur eine Reihe beschränkt wirkungsvoller Maßnahmen zur Verfügung. Wie können die (Groß-) Städte darauf reagieren?

Zunächst kann festgehalten werden, dass der Verkehrsbereich zukünftig den Schwerpunkt von Maßnahmen zu Klimaschutz und Luftreinhaltung bilden muss. Die wesentlichen urbanen Problemfelder werden durch den Verkehr bestimmt; bei den relevanten Luftschadstoffen stellen Fahrzeuge mit Dieselmotoren die Hauptemittenten dar und zur Reduktion der CO2-Emissionen müssen alle Fahrzeuge deutlich mehr beitragen als bisher.

In der Vergangenheit war die Reduktion von Verkehrsemissionen vorrangig als Frage der Weiterentwicklung der Fahrzeugtechnik interpretiert worden. Da die technischen Weiterentwicklungen allein für die Problemlösung nicht ausreichen, sind grundsätzliche Änderungen von Verkehrsverhalten und Verkehrssystemen unumgänglich. Eine Verbesserung der Raumordnung, weniger Zersiedelung, eine multifunktionale Stadt der kurzen Wege und ein anderes Mobilitätsverhalten der Bevölkerung weisen die höchsten Reduktionspotentiale auf, wirken aber vor allem langfristig. 

Eine Übersicht mit denkbaren Maßnahmengruppen zur Erreichung der Luftqualitäts-
und Klimaschutzziele wurde erarbeitet, die zur Entwicklung spezifischer Pakete von Kommunen genutzt werden kann.</dc:description>
   <dc:subject>info:eu-repo/classification/ddc/380</dc:subject>
   <dc:subject>Luftreinhaltung Verkehr, Stickoxide</dc:subject>
   <dc:subject>air quality management, transporation, air quality, nitrogen oxide</dc:subject>
   <dc:creator>Becker,Udo J.</dc:creator>
   <dc:creator>Clarus,Elke</dc:creator>
   <dc:creator>Schmidt,Wolfram</dc:creator>
   <dc:creator>Winter,Matthias</dc:creator>
   <dc:publisher>Technische Universität Dresden</dc:publisher>
   <dc:date>2017-01-18</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>workingPaper</dc:type>
</oai_dc:dc>', false, 'qucosa:30089');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (77, 22, '2018-11-15 12:29:02.556+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Stickoxide, Partikel und Kohlendioxid: Grenzwerte, Konflikte und Handlungsmöglichkeiten kommunaler Luftreinhaltung im Verkehrsbereich</dc:title>
   <dcterms:alternative xml:lang="ger" xsi:type="ddb:talternativeISO639-2">Informationen und Empfehlungen für Mitarbeiter deutscher Kommunen</dcterms:alternative>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Udo J.</pc:foreName>
            <pc:surName>Becker</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Elke</pc:foreName>
            <pc:surName>Clarus</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Wolfram</pc:foreName>
            <pc:surName>Schmidt</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Matthias</pc:foreName>
            <pc:surName>Winter</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AR 277700</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 3100</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Luftreinhaltung Verkehr</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Stickoxide</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">air quality management</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">transporation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">air quality</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">nitrogen oxide</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Die ab dem 1.1.2010 geltenden erweiterten Luftqualitätsgrenzwerte stellen die Kom-munen vor allem in verkehrlich belasteten Gebieten vor Probleme. Zum einen haben die Kommunen sicherzustellen, dass die Immissionsgrenzwerte eingehalten werden, zum anderen stehen ihnen aber nur eine Reihe beschränkt wirkungsvoller Maßnahmen zur Verfügung. Wie können die (Groß-) Städte darauf reagieren?

Zunächst kann festgehalten werden, dass der Verkehrsbereich zukünftig den Schwerpunkt von Maßnahmen zu Klimaschutz und Luftreinhaltung bilden muss. Die wesentlichen urbanen Problemfelder werden durch den Verkehr bestimmt; bei den relevanten Luftschadstoffen stellen Fahrzeuge mit Dieselmotoren die Hauptemittenten dar und zur Reduktion der CO2-Emissionen müssen alle Fahrzeuge deutlich mehr beitragen als bisher.

In der Vergangenheit war die Reduktion von Verkehrsemissionen vorrangig als Frage der Weiterentwicklung der Fahrzeugtechnik interpretiert worden. Da die technischen Weiterentwicklungen allein für die Problemlösung nicht ausreichen, sind grundsätzliche Änderungen von Verkehrsverhalten und Verkehrssystemen unumgänglich. Eine Verbesserung der Raumordnung, weniger Zersiedelung, eine multifunktionale Stadt der kurzen Wege und ein anderes Mobilitätsverhalten der Bevölkerung weisen die höchsten Reduktionspotentiale auf, wirken aber vor allem langfristig. 

Eine Übersicht mit denkbaren Maßnahmengruppen zur Erreichung der Luftqualitäts-
und Klimaschutzziele wurde erarbeitet, die zur Entwicklung spezifischer Pakete von Kommunen genutzt werden kann.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Technische Universität Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2017-01-18</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">workingPaper</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-216747</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:30089/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-216747</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:30089');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (79, 17, '2018-11-15 12:29:11.692+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Entwicklung und Untersuchung einer Stresstestmethode für die Risikoanalyse von soziotechnischen Systemen in der Luftfahrt</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-226990</dc:identifier>
   <dc:identifier>491684703</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:description>Die Unfallstatistik der kommerziellen Luftfahrt verzeichnet in diesem Jahrzehnt einen Höchststand betrieblicher Sicherheit. Es ist das Ergebnis einer jahrzehntewährenden Entwicklung der Luftfahrt, Vor- und Unfälle systematisch zu dokumentieren und aus ihnen lernen zu können, um zukünftig Unfälle zu vermeiden. Zwangsläufig führt diese Sicherheitsaffinität zu einer Überregulierung, die den Flugbetrieb in seinem bestehenden Konzept zunehmend konserviert und die Einführung von Innovation zunehmend hemmt. Einer der Gründe für diesen Trend ist, innovative Konzepte und Systeme nicht gefahrenlos während der Konzept-, Implementierungs- und Migrationsphase des Lebenszyklus hinsichtlich des Risikos analysieren zu können. Die Erlangung von Sicherheitsnachweisen gestaltet sich schwierig, wenn der bestehende Flugbetrieb kein innovationsinduziertes Risiko akzeptieren kann. Insbesondere bei sogenannten soziotechnischen Systemen, die den Menschen und die Interaktionsvorgänge mit der Technik berücksichtigen, können innovative Konzepte zu neuartigen Gefahren führen, die aus einem komplexen Zusammenspiel, z.B. zwischen den Piloten und den Fluglotsen mit Verfahren sowie Technik, resultieren. Ein Nachweis unter Berücksichtigung einer Zielsicherheit, wie z.B. ein Unfall auf eine Milliarde Flugstunden vor Inbetriebnahme, ist praktisch nicht leistbar. 

Diese Arbeit befasst sich mit der simulationsgestützten Risikoanalyse und liefert ein Konzept zur Nutzung von Echtzeitsimulatoren für die Erbringung des Sicherheitsnachweises. Das Konzept adressiert dabei ein statistisches Problem, seltene und unbekannte Gefahrensituationen mit Hilfe von Echtzeitsimulationen nicht oder unzureichend häufig beobachten zu können, um einen Rückschluss auf die Risiken eines neuartigen Systems ziehen zu können. Dieses Problem ist bekannt als statistische Rechtszensur. Das Kernelement des Konzeptes ist die Stressinduktion mit Hilfe von Zeitdruck, die ein hohes Maß an Kontrollierbarkeit und Reproduzierbarkeit des induzierten Stresses verspricht. Nach Vorbild eines beschleunigten Ermüdungstests sorgen die stressintensivierten Simulationsbedingungen für ein zunehmendes Fehlerverhalten des Operateurs. Gefahrenereignisse und Vorfälle sollen aufgrund der Stressreaktion verstärkt zu beobachten sein, ohne diese direkt hervorzurufen. Nicht bekannte oder seltene Gefahren können somit beobachten werden, die ansonsten aufgrund der kleinen Eintrittswahrscheinlichkeit innerhalb der Simulationszeit nicht zu beobachten wären. Die Bestimmung des Risikos und der Sicherheitsnachweis werden unter Einbezug seltener Gefahren möglich. Bei gezieltem Anfahren von zwei bis drei Zeitdrucklasten lassen die aufgezeichneten Ereignisdaten einen Rückschluss von den beschleunigten auf die unbelasteten Bedingungen (Designstress) zu.

Eine experimentelle Studie testet eine prototypische Implementierung eines Zeitdruckinduktionsverfahrens hinsichtlich der Kontrollierbarkeit der Stressreaktion. Dies soll sowohl den Zeitdruck als geeigneten Stressor bestätigen als auch Hinweise auf die Effektivität des Induktionsverfahrens liefern. Der Versuchsaufbau umfasst die Arbeitsposition des Platzverkehrslotsen am Flughafen Frankfurt am Main mit drei zu kontrollierenden Pisten, Rollwegen und Vorfeld an einem Surface Movement Manager. Es wurden drei studentische Probanden aus 14 ausgewählt, trainiert und mit Hilfe des Induktionsverfahrens getestet. Die aufgezeichneten Stressreaktionen zeigen kontrollierbare Reaktionen in der beobachteten Häufigkeit des Vorfalltyps Runway Incursion und Zeitfehler. Individuelle Stressreaktionen, wie z.B. die individuelle Basisleistung und die Freiheitsgrade des Probanden, zwischen Risiko und Schnelligkeit abzuwägen, tragen zur Streuung der Stressreaktion erheblich bei. Ein modellbasierter Ansatz konnte eine Erklärung zu diesen Varianzen liefern und eine systematische Abhängigkeit der Stressreaktion der Probanden zum induzierten Zeitdruck verifizieren. Die Ergebnisse zeigen, dass ein lastenunabhängiges Verhältnis zwischen Risiko und Schnelligkeit für den Fall zu erwarten ist, dass der Operateur höhere Ziele seines Aufgabenbereiches bedienen kann. Bei zunehmender Zeitdrucklast ist dieses Verhältnis zunehmend variabel. Auf Basis der Ergebnisse werden weiterführende Hypothesen und Erklärungsansätze vorgestellt, die für die Realisierung des beschleunigten Stresstests und somit für die Unterstützung einer Risikoanalyse soziotechnischer Systeme dienen.:1	Wissenschaftliche Zielsetzung und Lösungsansatz	1
1.1	Einleitung	1
1.2	Der menschliche Fehler in der Luftfahrt	7
1.2.1	Der statistische Unfallbeitrag	7
1.2.2	Modelle des menschlichen Unfallbeitrags	10
1.3	Die Probleme gegenwärtiger Methoden der Risikoanalyse	11
1.3.1	Die „Safety Assessment Methodology“	13
1.3.2	Modellbasierte Methoden	14
1.3.3	Die experimentelle Stresstestanalyse	16
1.3.4	Die Unfalluntersuchung	16
1.3.5	Das „Accident-Incident-Model“	21
1.3.6	Die Problemanalyse	21
1.3.7	Die Schlussfolgerung	23
1.4	Die Echtzeitsimulation als mögliche Lösung	24
1.4.1	Der gegenwärtige Nutzen von Echtzeitsimulatoren im Flugbetrieb	24
1.4.2	Ein Konzept zur simulationsgestützten Risikoanalyse	26
1.4.3	Mögliche Einschränkungen bei der Nutzung der Echtzeitsimulation	33
1.4.4	Anwendungsszenarien der simulationsgestützten Risikoanalyse	37
1.5	Die Zielsetzung und Struktur der Arbeit	39
1.5.1	Die Problemdefinition und der Lösungsansatz	39
1.5.2	Die Untersuchungshypothese	41
1.5.3	Die Struktur der Arbeit	42

2	Konzeptentwicklung „Accelerated Risk Analysis“	43
2.1	Die grundlegenden Begriffe	44
2.1.1	Die Wahl der Risikometrik	44
2.1.2	Die Wahl der Zielsicherheit	46
2.1.3	Die Wahl der Grundgesamtheit	46
2.1.4	Die Wahl der Unfallereignisverteilung	47
2.2	Das Sicherheitsargument	47
2.3	Die Abhängigkeit der Irrtumswahrscheinlichkeit zur Anzahl der Stichproben	48
2.3.1	Die Wahl der Irrtumswahrscheinlichkeit	48
2.3.2	Die Schätzung der Abhängigkeit anhand des Tschebyscheff-Ansatzes	48
2.3.3	Die Schätzung der Abhängigkeit anhand des Binomialansatzes	49
2.4	Ein Konzept zur Beschleunigung der Konvergenz	52
2.4.1	Die Beschleunigung durch Stresseinwirkung	52
2.4.2	Die Beschleunigung nach Tschebyscheff	53
2.4.3	Die Beschleunigung nach Binomialverteilung	53
2.5	Ein Konzept zur Stimulation sicherheitsrelevanter Ereignisse	54
2.5.1	Das Ziel der Stimulation	54
2.5.2	Die Wahl des Zeitdrucks als Stressor	54
2.5.3	Der Status Quo der Verfahren zur Induktion von Zeitdruck	57
2.5.4	Ein Verfahren zur Induktion von Stress	60
2.6	Die Regressionsanalyse zur Verifikation der Zielsicherheit	64
2.6.1	Die Regressionsanalyse zur Bestimmung der Unfallereignisverteilung	64
2.6.2	Die Regressionsanalyse zur Verifikation der Zielsicherheit	66
2.7	Die Verifikationsziele	71

3	Experimentelle Studie	73
3.1	Das Konzept der Studie	75
3.1.1	Die Wahl der unabhängigen und abhängigen Größen	75
3.1.2	Die Kausalhypothesen	76
3.1.3	Die Anforderungen an die Pilotstudie	76
3.1.4	Die Wahl der statistischen Tests	78
3.2	Das experimentelle Design	79
3.2.1	Die Auswahl der Arbeitsposition	79
3.2.2	Die Auswahl der Risikometrik	81
3.2.3	Die Definition der Primäraufgabe	82
3.2.4	Der Surface Movement Manager	85
3.2.5	Die Implementierung des Verfahrens „Konkurrenzdruck“	86
3.2.6	Das Messverfahren	99
3.2.7	Die Befragung	101
3.2.8	Die Szenarienentwicklung	102
3.3	Die Steuerung des Wettbewerbs	106
3.3.1	Die Reaktion der Aktivzeit	107
3.3.2	Die Reaktion der Anzahl aktiver Verkehrsbewegungen	107
3.3.3	Das Verifikationsergebnis	108
3.4	Die Organisation der Durchführung	109
3.4.1	Die Akquisition der Probanden	109
3.4.2	Die Versuchsplanung	110
3.5	Die Kalibrierung der Basislast	110
3.6	Die Kalibrierung der Zeitdrucklastszenarien	113
3.6.1	Die Analyse der Reaktionszeit	114
3.6.2	Die Analyse des Zeitfehlers	116
3.6.3	Die Analyse der subjektiven Arbeitsbeanspruchung und des Zeitdrucks	117
3.6.4	Die Analyse der Runway Incursion	118
3.6.5	Die Bestimmung der Zeitdrucklastparameter	119
3.7	Die Analyse der Lastenabhängigkeit der Aktivzeiten	119
3.8	Die Analyse der Lern- und Müdigkeitseffekte	121
3.8.1	Die Lerneffekte	121
3.8.2	Die Erschöpfungsseffekte	121
3.9	Die Analyse der Stressreaktionen	122
3.9.1	Die Reaktionszeit	122
3.9.2	Die Aktivzeit	122
3.9.3	Das angepasste Zeitbudget	123
3.9.4	Die Arbeitsbeanspruchung und der subjektive Zeitdruck	123
3.9.5	Die Runway Incursion und der Zeitfehler	124
3.10	Diskussion der Ergebnisse	129

4	Abschlussdiskussion	133
4.1	Das Fazit über das Konzept „Accelerated Risk Analysis“	133
4.1.1	Die Effektivität des Verfahrens und mögliche Anwendungsszenarien	134
4.1.2	Rückschluss auf die Modelle zur Bestimmung des Risikos	138
4.1.3	Implikationen durch Novizen- und Expertenprobanden	139
4.2	Die Anwendung auf andere Arbeitspositionen des Flugbetriebs	140
4.3	Ein Erklärungsansatz mit dem Contextual Control Modell	142
4.4	Ein Modell zur Beschreibung von Risikobereitschaft und Schnelligkeit	143
4.5	Mögliche Hypothesen für eine Folgestudie	144
4.6	Schlusswort	145

Abkürzungsverzeichnis	147
Formelzeichenverzeichnis	149
Abbildungsverzeichnis 	153
Tabellenverzeichnis	 155
Literaturverzeichnis	 157
Danksagung	163
Anhang	165</dc:description>
   <dc:subject>Luftfahrt, Sicherheit, Risiko, Stresstest, soziotechnische Systeme</dc:subject>
   <dc:subject>aviation, safety, risk, stresstesting, socio-technical systems</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/380, 620</dc:subject>
   <dc:subject>Luftfahrt; Sicherheit; Risiko; Risikoanalyse</dc:subject>
   <dc:creator>Meyer,Lothar</dc:creator>
   <dc:contributor>Fricke,Hartmut</dc:contributor>
   <dc:contributor>Pannasch,Sebastian</dc:contributor>
   <dc:contributor>Technische Universität Dresden</dc:contributor>
   <dc:date>2017-01-03</dc:date>
   <dc:date>2017-07-11</dc:date>
   <dc:date>2017-07-31</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>
                    doctoralThesis
                </dc:type>
</oai_dc:dc>', false, 'qucosa:30413');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (80, 22, '2018-11-15 12:29:11.692+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Entwicklung und Untersuchung einer Stresstestmethode für die Risikoanalyse von soziotechnischen Systemen in der Luftfahrt</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Lothar</pc:foreName>
            <pc:surName>Meyer</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Luftfahrt</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Sicherheit</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Risiko</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Stresstest</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">soziotechnische Systeme</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">aviation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">safety</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">risk</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">stresstesting</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">socio-technical systems</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">620</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">620</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 7874</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Luftfahrt</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Sicherheit</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Risiko</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Risikoanalyse</dc:subject>
   <dcterms:tableOfContents ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">1	Wissenschaftliche Zielsetzung und Lösungsansatz	1
1.1	Einleitung	1
1.2	Der menschliche Fehler in der Luftfahrt	7
1.2.1	Der statistische Unfallbeitrag	7
1.2.2	Modelle des menschlichen Unfallbeitrags	10
1.3	Die Probleme gegenwärtiger Methoden der Risikoanalyse	11
1.3.1	Die „Safety Assessment Methodology“	13
1.3.2	Modellbasierte Methoden	14
1.3.3	Die experimentelle Stresstestanalyse	16
1.3.4	Die Unfalluntersuchung	16
1.3.5	Das „Accident-Incident-Model“	21
1.3.6	Die Problemanalyse	21
1.3.7	Die Schlussfolgerung	23
1.4	Die Echtzeitsimulation als mögliche Lösung	24
1.4.1	Der gegenwärtige Nutzen von Echtzeitsimulatoren im Flugbetrieb	24
1.4.2	Ein Konzept zur simulationsgestützten Risikoanalyse	26
1.4.3	Mögliche Einschränkungen bei der Nutzung der Echtzeitsimulation	33
1.4.4	Anwendungsszenarien der simulationsgestützten Risikoanalyse	37
1.5	Die Zielsetzung und Struktur der Arbeit	39
1.5.1	Die Problemdefinition und der Lösungsansatz	39
1.5.2	Die Untersuchungshypothese	41
1.5.3	Die Struktur der Arbeit	42

2	Konzeptentwicklung „Accelerated Risk Analysis“	43
2.1	Die grundlegenden Begriffe	44
2.1.1	Die Wahl der Risikometrik	44
2.1.2	Die Wahl der Zielsicherheit	46
2.1.3	Die Wahl der Grundgesamtheit	46
2.1.4	Die Wahl der Unfallereignisverteilung	47
2.2	Das Sicherheitsargument	47
2.3	Die Abhängigkeit der Irrtumswahrscheinlichkeit zur Anzahl der Stichproben	48
2.3.1	Die Wahl der Irrtumswahrscheinlichkeit	48
2.3.2	Die Schätzung der Abhängigkeit anhand des Tschebyscheff-Ansatzes	48
2.3.3	Die Schätzung der Abhängigkeit anhand des Binomialansatzes	49
2.4	Ein Konzept zur Beschleunigung der Konvergenz	52
2.4.1	Die Beschleunigung durch Stresseinwirkung	52
2.4.2	Die Beschleunigung nach Tschebyscheff	53
2.4.3	Die Beschleunigung nach Binomialverteilung	53
2.5	Ein Konzept zur Stimulation sicherheitsrelevanter Ereignisse	54
2.5.1	Das Ziel der Stimulation	54
2.5.2	Die Wahl des Zeitdrucks als Stressor	54
2.5.3	Der Status Quo der Verfahren zur Induktion von Zeitdruck	57
2.5.4	Ein Verfahren zur Induktion von Stress	60
2.6	Die Regressionsanalyse zur Verifikation der Zielsicherheit	64
2.6.1	Die Regressionsanalyse zur Bestimmung der Unfallereignisverteilung	64
2.6.2	Die Regressionsanalyse zur Verifikation der Zielsicherheit	66
2.7	Die Verifikationsziele	71

3	Experimentelle Studie	73
3.1	Das Konzept der Studie	75
3.1.1	Die Wahl der unabhängigen und abhängigen Größen	75
3.1.2	Die Kausalhypothesen	76
3.1.3	Die Anforderungen an die Pilotstudie	76
3.1.4	Die Wahl der statistischen Tests	78
3.2	Das experimentelle Design	79
3.2.1	Die Auswahl der Arbeitsposition	79
3.2.2	Die Auswahl der Risikometrik	81
3.2.3	Die Definition der Primäraufgabe	82
3.2.4	Der Surface Movement Manager	85
3.2.5	Die Implementierung des Verfahrens „Konkurrenzdruck“	86
3.2.6	Das Messverfahren	99
3.2.7	Die Befragung	101
3.2.8	Die Szenarienentwicklung	102
3.3	Die Steuerung des Wettbewerbs	106
3.3.1	Die Reaktion der Aktivzeit	107
3.3.2	Die Reaktion der Anzahl aktiver Verkehrsbewegungen	107
3.3.3	Das Verifikationsergebnis	108
3.4	Die Organisation der Durchführung	109
3.4.1	Die Akquisition der Probanden	109
3.4.2	Die Versuchsplanung	110
3.5	Die Kalibrierung der Basislast	110
3.6	Die Kalibrierung der Zeitdrucklastszenarien	113
3.6.1	Die Analyse der Reaktionszeit	114
3.6.2	Die Analyse des Zeitfehlers	116
3.6.3	Die Analyse der subjektiven Arbeitsbeanspruchung und des Zeitdrucks	117
3.6.4	Die Analyse der Runway Incursion	118
3.6.5	Die Bestimmung der Zeitdrucklastparameter	119
3.7	Die Analyse der Lastenabhängigkeit der Aktivzeiten	119
3.8	Die Analyse der Lern- und Müdigkeitseffekte	121
3.8.1	Die Lerneffekte	121
3.8.2	Die Erschöpfungsseffekte	121
3.9	Die Analyse der Stressreaktionen	122
3.9.1	Die Reaktionszeit	122
3.9.2	Die Aktivzeit	122
3.9.3	Das angepasste Zeitbudget	123
3.9.4	Die Arbeitsbeanspruchung und der subjektive Zeitdruck	123
3.9.5	Die Runway Incursion und der Zeitfehler	124
3.10	Diskussion der Ergebnisse	129

4	Abschlussdiskussion	133
4.1	Das Fazit über das Konzept „Accelerated Risk Analysis“	133
4.1.1	Die Effektivität des Verfahrens und mögliche Anwendungsszenarien	134
4.1.2	Rückschluss auf die Modelle zur Bestimmung des Risikos	138
4.1.3	Implikationen durch Novizen- und Expertenprobanden	139
4.2	Die Anwendung auf andere Arbeitspositionen des Flugbetriebs	140
4.3	Ein Erklärungsansatz mit dem Contextual Control Modell	142
4.4	Ein Modell zur Beschreibung von Risikobereitschaft und Schnelligkeit	143
4.5	Mögliche Hypothesen für eine Folgestudie	144
4.6	Schlusswort	145

Abkürzungsverzeichnis	147
Formelzeichenverzeichnis	149
Abbildungsverzeichnis 	153
Tabellenverzeichnis	 155
Literaturverzeichnis	 157
Danksagung	163
Anhang	165</dcterms:tableOfContents>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Die Unfallstatistik der kommerziellen Luftfahrt verzeichnet in diesem Jahrzehnt einen Höchststand betrieblicher Sicherheit. Es ist das Ergebnis einer jahrzehntewährenden Entwicklung der Luftfahrt, Vor- und Unfälle systematisch zu dokumentieren und aus ihnen lernen zu können, um zukünftig Unfälle zu vermeiden. Zwangsläufig führt diese Sicherheitsaffinität zu einer Überregulierung, die den Flugbetrieb in seinem bestehenden Konzept zunehmend konserviert und die Einführung von Innovation zunehmend hemmt. Einer der Gründe für diesen Trend ist, innovative Konzepte und Systeme nicht gefahrenlos während der Konzept-, Implementierungs- und Migrationsphase des Lebenszyklus hinsichtlich des Risikos analysieren zu können. Die Erlangung von Sicherheitsnachweisen gestaltet sich schwierig, wenn der bestehende Flugbetrieb kein innovationsinduziertes Risiko akzeptieren kann. Insbesondere bei sogenannten soziotechnischen Systemen, die den Menschen und die Interaktionsvorgänge mit der Technik berücksichtigen, können innovative Konzepte zu neuartigen Gefahren führen, die aus einem komplexen Zusammenspiel, z.B. zwischen den Piloten und den Fluglotsen mit Verfahren sowie Technik, resultieren. Ein Nachweis unter Berücksichtigung einer Zielsicherheit, wie z.B. ein Unfall auf eine Milliarde Flugstunden vor Inbetriebnahme, ist praktisch nicht leistbar. 

Diese Arbeit befasst sich mit der simulationsgestützten Risikoanalyse und liefert ein Konzept zur Nutzung von Echtzeitsimulatoren für die Erbringung des Sicherheitsnachweises. Das Konzept adressiert dabei ein statistisches Problem, seltene und unbekannte Gefahrensituationen mit Hilfe von Echtzeitsimulationen nicht oder unzureichend häufig beobachten zu können, um einen Rückschluss auf die Risiken eines neuartigen Systems ziehen zu können. Dieses Problem ist bekannt als statistische Rechtszensur. Das Kernelement des Konzeptes ist die Stressinduktion mit Hilfe von Zeitdruck, die ein hohes Maß an Kontrollierbarkeit und Reproduzierbarkeit des induzierten Stresses verspricht. Nach Vorbild eines beschleunigten Ermüdungstests sorgen die stressintensivierten Simulationsbedingungen für ein zunehmendes Fehlerverhalten des Operateurs. Gefahrenereignisse und Vorfälle sollen aufgrund der Stressreaktion verstärkt zu beobachten sein, ohne diese direkt hervorzurufen. Nicht bekannte oder seltene Gefahren können somit beobachten werden, die ansonsten aufgrund der kleinen Eintrittswahrscheinlichkeit innerhalb der Simulationszeit nicht zu beobachten wären. Die Bestimmung des Risikos und der Sicherheitsnachweis werden unter Einbezug seltener Gefahren möglich. Bei gezieltem Anfahren von zwei bis drei Zeitdrucklasten lassen die aufgezeichneten Ereignisdaten einen Rückschluss von den beschleunigten auf die unbelasteten Bedingungen (Designstress) zu.

Eine experimentelle Studie testet eine prototypische Implementierung eines Zeitdruckinduktionsverfahrens hinsichtlich der Kontrollierbarkeit der Stressreaktion. Dies soll sowohl den Zeitdruck als geeigneten Stressor bestätigen als auch Hinweise auf die Effektivität des Induktionsverfahrens liefern. Der Versuchsaufbau umfasst die Arbeitsposition des Platzverkehrslotsen am Flughafen Frankfurt am Main mit drei zu kontrollierenden Pisten, Rollwegen und Vorfeld an einem Surface Movement Manager. Es wurden drei studentische Probanden aus 14 ausgewählt, trainiert und mit Hilfe des Induktionsverfahrens getestet. Die aufgezeichneten Stressreaktionen zeigen kontrollierbare Reaktionen in der beobachteten Häufigkeit des Vorfalltyps Runway Incursion und Zeitfehler. Individuelle Stressreaktionen, wie z.B. die individuelle Basisleistung und die Freiheitsgrade des Probanden, zwischen Risiko und Schnelligkeit abzuwägen, tragen zur Streuung der Stressreaktion erheblich bei. Ein modellbasierter Ansatz konnte eine Erklärung zu diesen Varianzen liefern und eine systematische Abhängigkeit der Stressreaktion der Probanden zum induzierten Zeitdruck verifizieren. Die Ergebnisse zeigen, dass ein lastenunabhängiges Verhältnis zwischen Risiko und Schnelligkeit für den Fall zu erwarten ist, dass der Operateur höhere Ziele seines Aufgabenbereiches bedienen kann. Bei zunehmender Zeitdrucklast ist dieses Verhältnis zunehmend variabel. Auf Basis der Ergebnisse werden weiterführende Hypothesen und Erklärungsansätze vorgestellt, die für die Realisierung des beschleunigten Stresstests und somit für die Unterstützung einer Risikoanalyse soziotechnischer Systeme dienen.</dcterms:abstract>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Hartmut</pc:foreName>
            <pc:surName>Fricke</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Sebastian</pc:foreName>
            <pc:surName>Pannasch</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:dateSubmitted xsi:type="dcterms:W3CDTF">2017-01-03</dcterms:dateSubmitted>
   <dcterms:dateAccepted xsi:type="dcterms:W3CDTF">2017-07-11</dcterms:dateAccepted>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2017-07-31</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">doctoralThesis</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-226990</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>thesis.doctoral</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Technische Universität Dresden</cc:name>
            <cc:place>Dresden</cc:place>
            <cc:department>
               <cc:name>Fakultät Verkehrswissenschaften ''Friedrich List''</cc:name>
            </cc:department>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>4</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:30413/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-226990</ddb:identifier>
   <ddb:identifier ddb:type="Erstkat-ID">491684703</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:30413');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (82, 17, '2018-11-15 17:32:37.704+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Klassifizierung verschiedener Stadtteile Hamburgs hinsichtlich der Bikesharing-Nutzung</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-319745</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:description>Die vorliegende Arbeit analysiert am Beispiel Hamburg die Stadtteile hinsichtlich der Bikesharing-Nutzung. Ein Großteil der Untersuchungen im Bereich der öffentlichen Fahrradverleihsysteme geben einen Überblick über verschiedene Kundengruppen. Nur Wenige spezialisieren sich auf eine räumlich städtische Betrachtung bezüglich des Bikesharing. Für zukünftige Auswertungen ist es bedeutsam für Städte, die Bikesharing-Systeme betreiben, Auswirkungen eines solchen Systems auf verschiedene Räumlichkeiten in einer Stadt zu prüfen. Das Ziel dieser Forschung ist es zu erfassen, wie sich ausgewählte zeitbezogene und technische Merkmale der Bikesharing-Nutzung auf Stadtteile auswirken. Über einen Zeitraum vom Mai 2016 bis Mai 2017 werden Fahrten in ausgewählten Stadtteilen 24 Stunden lang betrachtet. Die dabei entstehenden Gruppen sollen untereinander möglichst heterogen sein. Als Datengrundlage wurden Daten des „Call a Bike“ Dienstes der Deutschen Bahn aufbereitet. Der Datensatz beinhaltet alle Stadtteile, in denen sich eine oder mehrere Verleihstationen befinden. Eine Clusteranalyse wurde durchgeführt. Drei in sich homogene Cluster sind entstanden, die daraufhin in allen ihren Merkmalsausprägungen ausgewertet wurden. Diese Gruppen unterscheiden sich hauptsächlich in der durchschnittlichen Dauer einer Fahrt und im Anteil der Kurzfahrten unter 30 Minuten. Je weiter ein Ortsteil vom Zentrum entfernt ist, desto länger dauert eine Fahrt. Der Kurzfahrtenanteil sinkt ebenfalls mit zunehmender Entfernung. Diese Erkenntnisse beweisen, dass die Dauer einer Fahrt den größten Einfluss auf das Klassifizieren besitzt. Die meisten Fahrten in den Stadtteilen beginnen primär am Nachmittag. In Hinblick auf die Wochentage fahren Kunden des ersten Clusters vermehrt am Wochenende. In den anderen beiden Clustern bewegen sich die Personen mehr unter der Woche. Bei der technischen Ausleihe ist festzustellen, dass die ersten beiden Cluster mehr Android-Nutzer beinhalten im dritten Cluster mehr iPhone-Nutzer. Die technische Ausleihe ist unabhängig von der Lage der Stadtteile. Untersuchungen haben ergeben, dass die Stadtteile in drei heterogene Cluster zu unterscheiden sind. Das zweite und das dritte Cluster ähneln sich in Zeiträumen sowie in Wochentagen. Weitere Forschung könnte auf andere zeitbezogene Eigenschaften wie Monate und Jahreszeiten eingehen. Der Einfluss der Techniker wäre ebenfalls interessant.</dc:description>
   <dc:subject>Bikesharing, Hamburg, Clusteranalyse, Ökonometrie, Multivariate Analyse</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/380</dc:subject>
   <dc:creator>Li,Alina</dc:creator>
   <dc:contributor>Okhrin,Ostap</dc:contributor>
   <dc:contributor>Kormoll,Kathrin</dc:contributor>
   <dc:contributor>Technische Universität Dresden</dc:contributor>
   <dc:date>2018-07-10</dc:date>
   <dc:date>2018</dc:date>
   <dc:date>2018-10-22</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>bachelorThesis</dc:type>
</oai_dc:dc>', false, 'qucosa:31974');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (83, 22, '2018-11-15 17:32:37.704+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Klassifizierung verschiedener Stadtteile Hamburgs hinsichtlich der Bikesharing-Nutzung</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Alina</pc:foreName>
            <pc:surName>Li</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Bikesharing</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Hamburg</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Clusteranalyse</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Ökonometrie</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Multivariate Analyse</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">QR 860</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 330</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Die vorliegende Arbeit analysiert am Beispiel Hamburg die Stadtteile hinsichtlich der Bikesharing-Nutzung. Ein Großteil der Untersuchungen im Bereich der öffentlichen Fahrradverleihsysteme geben einen Überblick über verschiedene Kundengruppen. Nur Wenige spezialisieren sich auf eine räumlich städtische Betrachtung bezüglich des Bikesharing. Für zukünftige Auswertungen ist es bedeutsam für Städte, die Bikesharing-Systeme betreiben, Auswirkungen eines solchen Systems auf verschiedene Räumlichkeiten in einer Stadt zu prüfen. Das Ziel dieser Forschung ist es zu erfassen, wie sich ausgewählte zeitbezogene und technische Merkmale der Bikesharing-Nutzung auf Stadtteile auswirken. Über einen Zeitraum vom Mai 2016 bis Mai 2017 werden Fahrten in ausgewählten Stadtteilen 24 Stunden lang betrachtet. Die dabei entstehenden Gruppen sollen untereinander möglichst heterogen sein. Als Datengrundlage wurden Daten des „Call a Bike“ Dienstes der Deutschen Bahn aufbereitet. Der Datensatz beinhaltet alle Stadtteile, in denen sich eine oder mehrere Verleihstationen befinden. Eine Clusteranalyse wurde durchgeführt. Drei in sich homogene Cluster sind entstanden, die daraufhin in allen ihren Merkmalsausprägungen ausgewertet wurden. Diese Gruppen unterscheiden sich hauptsächlich in der durchschnittlichen Dauer einer Fahrt und im Anteil der Kurzfahrten unter 30 Minuten. Je weiter ein Ortsteil vom Zentrum entfernt ist, desto länger dauert eine Fahrt. Der Kurzfahrtenanteil sinkt ebenfalls mit zunehmender Entfernung. Diese Erkenntnisse beweisen, dass die Dauer einer Fahrt den größten Einfluss auf das Klassifizieren besitzt. Die meisten Fahrten in den Stadtteilen beginnen primär am Nachmittag. In Hinblick auf die Wochentage fahren Kunden des ersten Clusters vermehrt am Wochenende. In den anderen beiden Clustern bewegen sich die Personen mehr unter der Woche. Bei der technischen Ausleihe ist festzustellen, dass die ersten beiden Cluster mehr Android-Nutzer beinhalten im dritten Cluster mehr iPhone-Nutzer. Die technische Ausleihe ist unabhängig von der Lage der Stadtteile. Untersuchungen haben ergeben, dass die Stadtteile in drei heterogene Cluster zu unterscheiden sind. Das zweite und das dritte Cluster ähneln sich in Zeiträumen sowie in Wochentagen. Weitere Forschung könnte auf andere zeitbezogene Eigenschaften wie Monate und Jahreszeiten eingehen. Der Einfluss der Techniker wäre ebenfalls interessant.</dcterms:abstract>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Ostap</pc:foreName>
            <pc:surName>Okhrin</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Kathrin</pc:foreName>
            <pc:surName>Kormoll</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:dateSubmitted xsi:type="dcterms:W3CDTF">2018-07-10</dcterms:dateSubmitted>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-10-22</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2018</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">bachelorThesis</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-319745</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>bachelor</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Technische Universität Dresden</cc:name>
            <cc:place>Dresden</cc:place>
            <cc:department>
               <cc:name>Verkehrswissenschaften ''Friedrich List''</cc:name>
            </cc:department>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:31974/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-319745</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:31974');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (86, 17, '2018-11-15 17:33:05.632+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Erweiterung des ''generalized'' p-Median-Problems</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-319055</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:description>Die vorliegende Masterarbeit beschäftigt sich mit den MINISUM-Modellen auf einem Graphen. Die Eigenschaften des „generalized“ p-Median-Problem werden neben den Eigenschaften des ordinären p-Median-Problems untersucht. Dabei kommt folgende Diskrepanz zum Vorschein: Obwohl das „generalized“ p-Median-Problem eine unendliche
Anzahl an potenziellen Lösungsmöglichkeiten besitzt und der optimale Standort bei einer derartigen Problemstellung sowohl im Knoten als auch auf der Kante des Graphen
liegen kann, wird der Median oft ausschließlich in den Knoten des Graphen gesucht.
Dadurch entsteht das Risiko, dass beim Lösen des Problems der optimale Standort von Anfang an nicht mitberücksichtigt wird. Die Forschungsaufgabe dieser Arbeit ist, das „generalized“ p-Median-Problem so zu erweitern, dass aus einem Problem mit unendlicher Anzahl an Lösungsmöglichkeiten ein endliches Problem wird, welches optimal mit einer diskreten Methode gelöst werden kann.
Im ersten Schritt werden die potenziellen Standorte auf den Kanten (die sogenannten fiktiven Knoten) ermittelt. Sie werden mit den Knoten des Graphen gleichgestellt und bei der Auffindung des kostenminimalen Standortes einkalkuliert. Damit sind alle potenziellen Standorte abgedeckt und das Problem erhält eine endliche Anzahl an Lösungsmöglichkeiten.
Eine weitere Herausforderung liegt in der unkonventionellen Formulierung des Kostenparameters, der beim „generalized“ p-Median-Problem zusätzlich berücksichtigt wird.
Die Kosten stellen eine logarithmische Kostenfunktion dar, die von der Verteilung der Nachfrage auf die Mediane abhängig ist. Diese Variable wird als Zuteilung bezeichnet und muss zusätzlich vor der Formulierung des Optimierungsproblems bestimmt werden.
Die Zuteilung ist für die Ermittlung der Kosten zuständig und fließt in das Modell nur indirekt mit ein. Abschließend wird die Funktionsfähigkeit des neuen Modells überprüft und dem ursprünglichen Modell (dem umformulierten Warehouse Location Problem) gegenübergestellt. Tatsächlich werden bei dem erweiterten Modell durch die Platzierung der Mediane
auf die Kante zusätzliche Kosten eingespart. Die vorliegende Arbeit zeigt das Prinzip, wie das „generalized“ p-Median-Problem erweitert werden kann, und liefert den Beweis über die Funktionstüchtigkeit dieser Methode.</dc:description>
   <dc:description>The following master’s thesis deals with the MINISUM models on a graph. In this regard the properties of the generalized p-median problem have been investigated alongside
the properties of the ordinary p-median problem. In the course of the investigation, the following discrepancy comes to the fore: although the generalized p-median problem
has an infinite number of potential solutions, and the optimal location for such a problem may lie in both the vertex and on the edge of the graph, the median is often
searched for exclusively in the vertex of the graph. This creates the risk that, upon attempting to find a solution, the optimal location to place the median may not be
taken into consideration right from the start. 
The goal of the following thesis is to extend the generalized p-median problem so that a problem with an infinite number of possible solutions becomes a finite problem which
can best be solved with a discrete method. In the first step, all potential locations along the edges (the so-called fictitious vertices) are determined using an empirical-analytical approach. They are equated with the vertices of the graph and taken into account when locating the minimum cost location.
This covers all potential locations and through this method the problem receives a finite number of possible solutions. Another challenge lies in the unconventional formulation of the cost parameter, which is additionally taken into account in the generalized p-median problem. The cost represents a logarithmic cost function that depends on the distribution of demand on the median. In the following work, this variable shall be called the allocation and must first be determined in order to formulate the optimization problem framework. The allocation is responsible for determining the costs and is included only indirectly in the model.
Finally, the functionality of the new model is checked and compared with the original model, the rewritten warehouse location problem. In fact, the placement of medians
on an edge saves additional costs in the extended model. The following elaboration shows the principle of how the generalized p-median problem can be extended, and provides proof of the functionality of this extension.</dc:description>
   <dc:subject>betriebliche Standortplanung, MINISUM-Modelle, Median, ''generalized'' p-Median-Problem, WLP</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/330</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/510</dc:subject>
   <dc:creator>Futlik,Alisa</dc:creator>
   <dc:contributor>Kormoll,Kathrin</dc:contributor>
   <dc:contributor>Stein,Roman</dc:contributor>
   <dc:contributor>Technische Universität Dresden</dc:contributor>
   <dc:date>2018-05-31</dc:date>
   <dc:date>2018-06-20</dc:date>
   <dc:date>2018</dc:date>
   <dc:date>2018-10-15</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>
                    masterThesis
                </dc:type>
</oai_dc:dc>', false, 'qucosa:31905');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (89, 17, '2018-11-15 17:34:49.165+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Mobilität 5.0</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-704917</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:subject>mobilität</dc:subject>
   <dc:date>2018-10-23</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>article</dc:type>
</oai_dc:dc>', false, 'qucosa:70491');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (90, 22, '2018-11-15 17:34:49.165+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Mobilität 5.0</dc:title>
   <dc:subject xsi:type="xMetaDiss:noScheme">mobilität</dc:subject>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-10-23</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-15</dcterms:modified>
   <dc:type xsi:type="dini:PublType">article</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-704917</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:70491/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-704917</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70491');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (92, 17, '2018-11-22 08:31:42.265+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Simulation von Fahrspielen und Energieflüssen in Nahverkehrssystemen</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-704931</dc:identifier>
   <dc:identifier>978-3-7315-0740-6</dc:identifier>
   <dc:identifier>1869-6058</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:description>Straßen-, Stadt-, U- und S-Bahnen sind schienengebundene und gleichstromgespeiste
Nahverkehrssysteme, die in den wachsenden Metropolregionen
einen wichtigen Grundpfeiler der modernen Verkehrsinfrastruktur darstellen.
Die Energieeffizienz solcher Systeme rückt aus ökologischen und ökonomischen
Gründen weiter in den Fokus. Eine wichtige Fragestellung ist daher
die quantitative Untersuchung des Systemenergieverbrauchs. Dazu müssen
alle relevanten infrastruktur-, fahrzeug- und betriebsbezogenen Systemzusammenhänge
berücksichtigt werden. Hierfür wird ein Simulationsprogramm
in der Programmiersprache C# entwickelt. Die Eingabe erfolgt über
eine grafische Benutzeroberfläche, mit der beliebige Nahverkehrssysteme
definiert werden können. Das Strecken- und Leitungsnetz sowie die Fahr- und
Umlaufpläne lassen sich individuell anpassen. Der Simulationsablauf erfolgt im
Zeitschrittverfahren in Form einer gekoppelten Fahrspiel- und Energieflussberechnung,
deren Ausgabewerte in einer Datenbank abgespeichert werden.
Am Beispiel des Karlsruher Straßen- und Stadtbahnnetzes werden 16 Simulationsszenarien
analysiert, in denen jeweils unterschiedliche Maßnahmen zur
Optimierung der Energiebilanz durchgeführt werden. Die Simulationsergebnisse
der einzelnen Szenarien werden gegenübergestellt und detailliert
ausgewertet.</dc:description>
   <dc:subject>Simulation Energieflüsse Nahverkehrssysteme</dc:subject>
   <dc:creator>Kühn,Christoph</dc:creator>
   <dc:contributor>Karlsruher Institut für Technologie KIT-Fakultät für Maschinenbau</dc:contributor>
   <dc:date>2018-11-01</dc:date>
   <dc:date>2017-07-23</dc:date>
   <dc:date>2018</dc:date>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2018-11-22</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>
                    doctoralThesis
                </dc:type>
</oai_dc:dc>', false, 'qucosa:70493');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (93, 22, '2018-11-22 08:31:42.265+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Simulation von Fahrspielen und Energieflüssen in Nahverkehrssystemen</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Christoph</pc:foreName>
            <pc:surName>Kühn</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Simulation Energieflüsse Nahverkehrssysteme</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Straßen-, Stadt-, U- und S-Bahnen sind schienengebundene und gleichstromgespeiste
Nahverkehrssysteme, die in den wachsenden Metropolregionen
einen wichtigen Grundpfeiler der modernen Verkehrsinfrastruktur darstellen.
Die Energieeffizienz solcher Systeme rückt aus ökologischen und ökonomischen
Gründen weiter in den Fokus. Eine wichtige Fragestellung ist daher
die quantitative Untersuchung des Systemenergieverbrauchs. Dazu müssen
alle relevanten infrastruktur-, fahrzeug- und betriebsbezogenen Systemzusammenhänge
berücksichtigt werden. Hierfür wird ein Simulationsprogramm
in der Programmiersprache C# entwickelt. Die Eingabe erfolgt über
eine grafische Benutzeroberfläche, mit der beliebige Nahverkehrssysteme
definiert werden können. Das Strecken- und Leitungsnetz sowie die Fahr- und
Umlaufpläne lassen sich individuell anpassen. Der Simulationsablauf erfolgt im
Zeitschrittverfahren in Form einer gekoppelten Fahrspiel- und Energieflussberechnung,
deren Ausgabewerte in einer Datenbank abgespeichert werden.
Am Beispiel des Karlsruher Straßen- und Stadtbahnnetzes werden 16 Simulationsszenarien
analysiert, in denen jeweils unterschiedliche Maßnahmen zur
Optimierung der Energiebilanz durchgeführt werden. Die Simulationsergebnisse
der einzelnen Szenarien werden gegenübergestellt und detailliert
ausgewertet.</dcterms:abstract>
   <dcterms:dateSubmitted xsi:type="dcterms:W3CDTF">2018-11-01</dcterms:dateSubmitted>
   <dcterms:dateAccepted xsi:type="dcterms:W3CDTF">2017-07-23</dcterms:dateAccepted>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-11-22</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2018</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-22</dcterms:modified>
   <dc:type xsi:type="dini:PublType">doctoralThesis</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:isbn">978-3-7315-0740-6</dc:identifier>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-704931</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>thesis.doctoral</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Karlsruher Institut für Technologie KIT-Fakultät für Maschinenbau</cc:name>
            <cc:place>Karlsruhe</cc:place>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:70493/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-704931</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70493');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (95, 17, '2018-11-22 08:35:06.637+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Road Financing and Management in the Baltic States</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-704941</dc:identifier>
   <dc:language>eng</dc:language>
   <dc:description>Until the early 1990s, the Baltic states, namely, Estonia, Latvia, and
Lithuania, were constituents of the former Soviet Union. When the
Baltic states achieved independence, they faced challenges in finding
ways to develop and maintain their neglected road networks. The new
republics organized individual road administrations according to
their specific needs and governmental frameworks. They chose different
means of collecting and distributing the funds needed for their
national road networks. Before independence, all road construction
and maintenance were done by force account. Since independence, the
road administrative structures and financing arrangements have
undergone considerable change. A common concern in the three Baltic
states is that the current levels of funding for roads are lower than
those needed to maintain their national road networks. If adequate
funding is not available, it is likely that there will be increased backlogs
of periodic maintenance, resulting in a greater need for reconstruction
later. The present system of management and financing for
roads in each of the three governments is discussed. A brief comparative
analysis of the markedly different approaches to management
and financing of the road networks in each of the three Baltic states
is also presented. The sources of the information include publications
and interviews with road administrations during missions to the
Baltic states.</dc:description>
   <dc:subject>Road Financing Management</dc:subject>
   <dc:creator>Rodzik,Ewa</dc:creator>
   <dc:creator>Queiroz,Cesar</dc:creator>
   <dc:publisher>SAGE Journals</dc:publisher>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2018-11-22</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>article</dc:type>
</oai_dc:dc>', false, 'qucosa:70494');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (96, 22, '2018-11-22 08:35:06.637+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="eng" xsi:type="ddb:titleISO639-2">Road Financing and Management in the Baltic States</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Ewa</pc:foreName>
            <pc:surName>Rodzik</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Cesar</pc:foreName>
            <pc:surName>Queiroz</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Road Financing Management</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">Until the early 1990s, the Baltic states, namely, Estonia, Latvia, and
Lithuania, were constituents of the former Soviet Union. When the
Baltic states achieved independence, they faced challenges in finding
ways to develop and maintain their neglected road networks. The new
republics organized individual road administrations according to
their specific needs and governmental frameworks. They chose different
means of collecting and distributing the funds needed for their
national road networks. Before independence, all road construction
and maintenance were done by force account. Since independence, the
road administrative structures and financing arrangements have
undergone considerable change. A common concern in the three Baltic
states is that the current levels of funding for roads are lower than
those needed to maintain their national road networks. If adequate
funding is not available, it is likely that there will be increased backlogs
of periodic maintenance, resulting in a greater need for reconstruction
later. The present system of management and financing for
roads in each of the three governments is discussed. A brief comparative
analysis of the markedly different approaches to management
and financing of the road networks in each of the three Baltic states
is also presented. The sources of the information include publications
and interviews with road administrations during missions to the
Baltic states.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>SAGE Journals</cc:name>
         <cc:place>USA</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-11-22</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-22</dcterms:modified>
   <dc:type xsi:type="dini:PublType">article</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-704941</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">eng</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:70494/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-704941</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70494');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (99, 17, '2018-11-22 08:35:09.749+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Boarding Efficiency:How to enter an aircraft – the most efficient way?</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-704955</dc:identifier>
   <dc:language>eng</dc:language>
   <dc:description>offen</dc:description>
   <dc:subject>Aircraft Boarding Efficiency</dc:subject>
   <dc:creator>Schultz,Michael</dc:creator>
   <dc:creator>Deutsche Zentrum für Luft- und Raumfahrt e. V..Institute of Flight Goaiidance, Department of Air Transportation German Aerospace Center (DLR e.V.)</dc:creator>
   <dc:date>2014</dc:date>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2018-11-22</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>lecture</dc:type>
</oai_dc:dc>', false, 'qucosa:70495');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (100, 22, '2018-11-22 08:35:09.749+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="eng" xsi:type="ddb:titleISO639-2">Boarding Efficiency</dc:title>
   <dcterms:alternative xml:lang="eng" xsi:type="ddb:talternativeISO639-2">How to enter an aircraft – the most efficient way?</dcterms:alternative>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Michael</pc:foreName>
            <pc:surName>Schultz</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Aircraft Boarding Efficiency</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">offen</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Deutsche Zentrum für Luft- und Raumfahrt e. V.</cc:name>
         <cc:place>Köln</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-11-22</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2014</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-22</dcterms:modified>
   <dc:type xsi:type="dini:PublType">lecture</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-704955</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">eng</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:70495/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-704955</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70495');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (102, 17, '2018-11-22 08:35:12.733+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Weissbuch innovativer Eisenbahngüterwagen 2030</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-704962</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:subject>Eisenbahngüterverkehr</dc:subject>
   <dc:creator>König,Rainer</dc:creator>
   <dc:creator>Hecht,Markus</dc:creator>
   <dc:creator>TU Dresden.</dc:creator>
   <dc:date>2012</dc:date>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2018-11-22</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>book</dc:type>
</oai_dc:dc>', false, 'qucosa:70496');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (103, 22, '2018-11-22 08:35:12.733+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Weissbuch innovativer Eisenbahngüterwagen 2030</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Rainer</pc:foreName>
            <pc:surName>König</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Markus</pc:foreName>
            <pc:surName>Hecht</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Eisenbahngüterverkehr</dc:subject>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>TU Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-11-22</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2012</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-22</dcterms:modified>
   <dc:type xsi:type="dini:PublType">book</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-704962</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:70496/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-704962</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70496');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (107, 17, '2018-11-28 16:56:38.966+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Test Bachelorarbeit DIU XY:Untertitel DIU</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-704811</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>978-3249085-456</dc:relation>
   <dc:relation>1.andereausgabe_DOI</dc:relation>
   <dc:description>xxx:Kurzfassung..................................................................................................... II
Aufgabenstellung ............................................................................................ III
Inhaltsverzeichnis ............................................................................................ V
Nomenklatur ................................................................................................... VII
Abbildungs- und Tabellenverzeichnis ............................................................... IX
Vorwort ............................................................................................................ XI
1 Einleitung .......................................................................................................1
2 Theorie thermischer Energiespeicher ............................................................3
2.1 Beschreibung von Wärmespeichern ...........................................................3
2.2 Sensible Wärmespeicher ............................................................................4
2.3 Latente Wärmespeicher.............................................................................. 9
2.4 Sorptive Wärmespeicher ............................................................................12
2.5 Chemische Wärmespeicher ....................................................................... 14
3 Spezifikation des thermochemischen Speichersystems ................................ 17
3.1 Thermochemische Grundlagen .................................................................. 17
3.2 Motivation der Aufgabenstellung ............................................................... 20
3.3 Charakterisierung des Reaktionssystems ................................................. 21
4 Systembeschreibung des Speicherkonzepts ................................................ 26
4.1 Kurzdarstellung der Ausgangssituation .................................................... 26
4.2 Weiterentwicklung zum bewegten Reaktionsbett ..................................... 27
4.2.1 Theorie des bewegten Reaktionsbettes ................................................ 27
4.2.2 Konstruktion des Reaktors .................................................................... 28
4.2.3 Förderung des Speichermaterials .......................................................... 31
4.3 Periphere Anlagenteile ............................................................................. 33
4.3.1 Anlagenschema ..................................................................................... 33
4.3.2 Entwurf des Druckhalters ...................................................................... 35
INHALTSVERZEICHNIS VI
4.3.3 Ausführung der Elektro- und Messtechnik ............................................. 37
5 Experimentelle Untersuchungen ................................................................. 39
5.1 Versuchsdurchführung ............................................................................. 39
5.2 Betrieb der Fördereinheiten .................................................................... 40
5.3 Optimierung der Fördereinheiten ............................................................ 44
5.3.1 Inaktive Mischpaddel ............................................................................ 44
5.3.2 Modifizierte Mischpaddel ....................................................................... 47
5.4 Erkenntnisse ............................................................................................ 49
6 Finales Konzept des Versuchsstandes ........................................................ 50
6.1 Lösungsansätze für den Massenfluss ...................................................... 50
6.2 Gestaltung der Austragshilfe ................................................................... 54
7 Zusammenfassung und Ausblick ................................................................. 57
Eidesstattliche Erklärung ............................................................................... 59
Literatur- und Quellenverzeichnis .................................................................. 60
Anlagen ......................................................................................................... 63
A.1. Parametrierung des Temperaturwächters (Kapitel 4.3.3) ....................... 63
A.2. Inhalt des beigelegten Datenträgers (Einband) ..................................... 63
A.3. Berechnung der Aufheizstrecke des Stickstoffstroms (Kapitel 4.3.1) ...... 64
A.4. Konstruktionszeichnung des Druckhalters (Kapitel 4.3.2) ...................... 65
A.5. Dampftafel: Sättigungsdampfdruck von Wasserdampf (Kapitel 4.3.2) .... 66
A.6. Stromlaufpläne und Baugruppenliste des Teststandes (Kapitel 4.3.3) ... ....67
A.7. Ermittlung der Kabelquerschnitte für Stromlaufplan (Kapitel 4.3.3) ........73
A.8. Parametrierung der Frequenzumrichter (Kapitel 5.1) ....................... 74
A.9. Ergebnisse der Kalibiermessungen (Kapitel 5.2) ............................75
A.10. Berechnungen zur Dynamik des Schlitzschiebers (Kapitel 6.1) ............. 76
A.11. Konstruktionszeichnungen der Austragshilfe (Kapitel 6.2) .................77</dc:description>
   <dc:subject>xxx</dc:subject>
   <dc:subject>xxx</dc:subject>
   <dc:subject>test freie Schlagwörter2</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/600</dc:subject>
   <dc:creator>Renner,Angelika</dc:creator>
   <dc:contributor>Freitag,Andreas</dc:contributor>
   <dc:contributor>Heimann,Ingrid</dc:contributor>
   <dc:contributor>Gründler,Helmut</dc:contributor>
   <dc:contributor>Dresden International University</dc:contributor>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2018-08-01</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>bookPart</dc:type>
</oai_dc:dc>', false, 'qucosa:70481');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (108, 22, '2018-11-28 16:56:38.966+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Test Bachelorarbeit DIU XY</dc:title>
   <dcterms:alternative xml:lang="ger" xsi:type="ddb:talternativeISO639-2">Untertitel DIU</dcterms:alternative>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Angelika</pc:foreName>
            <pc:surName>Renner</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">xxx</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">xxx</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">test freie Schlagwörter2</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">600</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">600</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">yb 123</dc:subject>
   <dcterms:tableOfContents ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Kurzfassung..................................................................................................... II
Aufgabenstellung ............................................................................................ III
Inhaltsverzeichnis ............................................................................................ V
Nomenklatur ................................................................................................... VII
Abbildungs- und Tabellenverzeichnis ............................................................... IX
Vorwort ............................................................................................................ XI
1 Einleitung .......................................................................................................1
2 Theorie thermischer Energiespeicher ............................................................3
2.1 Beschreibung von Wärmespeichern ...........................................................3
2.2 Sensible Wärmespeicher ............................................................................4
2.3 Latente Wärmespeicher.............................................................................. 9
2.4 Sorptive Wärmespeicher ............................................................................12
2.5 Chemische Wärmespeicher ....................................................................... 14
3 Spezifikation des thermochemischen Speichersystems ................................ 17
3.1 Thermochemische Grundlagen .................................................................. 17
3.2 Motivation der Aufgabenstellung ............................................................... 20
3.3 Charakterisierung des Reaktionssystems ................................................. 21
4 Systembeschreibung des Speicherkonzepts ................................................ 26
4.1 Kurzdarstellung der Ausgangssituation .................................................... 26
4.2 Weiterentwicklung zum bewegten Reaktionsbett ..................................... 27
4.2.1 Theorie des bewegten Reaktionsbettes ................................................ 27
4.2.2 Konstruktion des Reaktors .................................................................... 28
4.2.3 Förderung des Speichermaterials .......................................................... 31
4.3 Periphere Anlagenteile ............................................................................. 33
4.3.1 Anlagenschema ..................................................................................... 33
4.3.2 Entwurf des Druckhalters ...................................................................... 35
INHALTSVERZEICHNIS VI
4.3.3 Ausführung der Elektro- und Messtechnik ............................................. 37
5 Experimentelle Untersuchungen ................................................................. 39
5.1 Versuchsdurchführung ............................................................................. 39
5.2 Betrieb der Fördereinheiten .................................................................... 40
5.3 Optimierung der Fördereinheiten ............................................................ 44
5.3.1 Inaktive Mischpaddel ............................................................................ 44
5.3.2 Modifizierte Mischpaddel ....................................................................... 47
5.4 Erkenntnisse ............................................................................................ 49
6 Finales Konzept des Versuchsstandes ........................................................ 50
6.1 Lösungsansätze für den Massenfluss ...................................................... 50
6.2 Gestaltung der Austragshilfe ................................................................... 54
7 Zusammenfassung und Ausblick ................................................................. 57
Eidesstattliche Erklärung ............................................................................... 59
Literatur- und Quellenverzeichnis .................................................................. 60
Anlagen ......................................................................................................... 63
A.1. Parametrierung des Temperaturwächters (Kapitel 4.3.3) ....................... 63
A.2. Inhalt des beigelegten Datenträgers (Einband) ..................................... 63
A.3. Berechnung der Aufheizstrecke des Stickstoffstroms (Kapitel 4.3.1) ...... 64
A.4. Konstruktionszeichnung des Druckhalters (Kapitel 4.3.2) ...................... 65
A.5. Dampftafel: Sättigungsdampfdruck von Wasserdampf (Kapitel 4.3.2) .... 66
A.6. Stromlaufpläne und Baugruppenliste des Teststandes (Kapitel 4.3.3) ... ....67
A.7. Ermittlung der Kabelquerschnitte für Stromlaufplan (Kapitel 4.3.3) ........73
A.8. Parametrierung der Frequenzumrichter (Kapitel 5.1) ....................... 74
A.9. Ergebnisse der Kalibiermessungen (Kapitel 5.2) ............................75
A.10. Berechnungen zur Dynamik des Schlitzschiebers (Kapitel 6.1) ............. 76
A.11. Konstruktionszeichnungen der Austragshilfe (Kapitel 6.2) .................77</dcterms:tableOfContents>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">xxx</dcterms:abstract>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Andreas</pc:foreName>
            <pc:surName>Freitag</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Ingrid</pc:foreName>
            <pc:surName>Heimann</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Helmut</pc:foreName>
            <pc:surName>Gründler</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-08-01</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-11-28</dcterms:modified>
   <dc:type xsi:type="dini:PublType">bookPart</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-704811</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="ddb:noScheme">Testtitel Buchaufsatz</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://diu.example.com/qucosa:70481/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-704811</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70481');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (111, 17, '2018-12-04 13:08:48.199+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Schriftenreihe des Instituts für Verkehrsplanung und Straßenverkehr</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-94856</dc:identifier>
   <dc:identifier>1432-5500</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:description>Schriftenreihe des Instituts für Verkehrsplanung und Straßenverkehr
- Professur für Integrierte Verkehrsplanung und Straßenverkehrs­technik
- Professur für Verkehrsökologie
- Professur für Verkehrspsycho­logie
- Professur für Gestaltung von Straßenverkehrs­anlagen
- Fachbereich Verkehrsnachfra­gemodellierung

Verkehrswissen­schaften in Dresden
Die Fakultät Verkehrswissenschaften "Friedrich List" ist das größte akademische Kompetenzzentrum auf dem Gebiet der Verkehrswissenschaften in Deutschland mit einer seit über 60 Jahren in Dresden beheimateten universitären Forschung und Lehre. Mit ihrem komplexen systemwissenschaftlichen Ansatz orientiert sie sich an der Komplexität des Transport- und Nachrichtenwesens und trägt damit den dynamischen Herausforderungen der Verkehrsmärkte Rechnung.  Sie leistet mit ihrem interdisziplinären Lehr- und Forschungsverbund aus Verkehrsökonomie, Verkehrsingenieurwesen, Verkehrsinfrastruktur und Verkehrsmitteltechnik einen wichtigen Beitrag für die nachhaltige Entwicklung aller Verkehrs- und Infrastruktursysteme zur Erfüllung der stetig wachsenden Mobilitätsbedürfnisse der Gesellschaft.

Der Fakultät gehören 7 Institute mit über 20 Professoren und mehr als 300 Mitarbeitern an.
Insgesamt studieren etwa 2.000 Studierende in den Diplom-, Bachelor- und Masterstudiengängen der Fakultät sowie fakultätsübergreifend.</dc:description>
   <dc:subject>info:eu-repo/classification/ddc/620</dc:subject>
   <dc:subject>Schriftenreihe, Verkehrsnachfrage, Modellierung</dc:subject>
   <dc:publisher>Technische Universität Dresden</dc:publisher>
   <dc:date>2018-01-09</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>Periodical</dc:type>
</oai_dc:dc>', false, 'qucosa:26121');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (112, 22, '2018-12-04 13:08:48.199+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Schriftenreihe des Instituts für Verkehrsplanung und Straßenverkehr</dc:title>
   <dc:subject xsi:type="dcterms:DDC">620</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">620</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 3300</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">QR 800</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Schriftenreihe</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Verkehrsnachfrage</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Modellierung</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Schriftenreihe des Instituts für Verkehrsplanung und Straßenverkehr
- Professur für Integrierte Verkehrsplanung und Straßenverkehrs­technik
- Professur für Verkehrsökologie
- Professur für Verkehrspsycho­logie
- Professur für Gestaltung von Straßenverkehrs­anlagen
- Fachbereich Verkehrsnachfra­gemodellierung

Verkehrswissen­schaften in Dresden
Die Fakultät Verkehrswissenschaften "Friedrich List" ist das größte akademische Kompetenzzentrum auf dem Gebiet der Verkehrswissenschaften in Deutschland mit einer seit über 60 Jahren in Dresden beheimateten universitären Forschung und Lehre. Mit ihrem komplexen systemwissenschaftlichen Ansatz orientiert sie sich an der Komplexität des Transport- und Nachrichtenwesens und trägt damit den dynamischen Herausforderungen der Verkehrsmärkte Rechnung.  Sie leistet mit ihrem interdisziplinären Lehr- und Forschungsverbund aus Verkehrsökonomie, Verkehrsingenieurwesen, Verkehrsinfrastruktur und Verkehrsmitteltechnik einen wichtigen Beitrag für die nachhaltige Entwicklung aller Verkehrs- und Infrastruktursysteme zur Erfüllung der stetig wachsenden Mobilitätsbedürfnisse der Gesellschaft.

Der Fakultät gehören 7 Institute mit über 20 Professoren und mehr als 300 Mitarbeitern an.
Insgesamt studieren etwa 2.000 Studierende in den Diplom-, Bachelor- und Masterstudiengängen der Fakultät sowie fakultätsübergreifend.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Technische Universität Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-01-09</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-12-04</dcterms:modified>
   <dc:type xsi:type="dini:PublType">Periodical</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-94856</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="ddb:noScheme">Fakultät Verkehrswissenschaften "Friedrich List", Institut für Verkehrsplanung und Straßenverkehr, Technische Universität Dresden</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>0</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:26121/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-94856</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:26121');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (114, 17, '2018-12-04 14:25:19.86+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Fehlereinflüsse und Teilnahmebereitschaft bei Haushaltsbefragungen zum Verkehrsverhalten</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-231510</dc:identifier>
   <dc:identifier>1432-5500</dc:identifier>
   <dc:identifier>498523853</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa-94856</dc:relation>
   <dc:description>Die vorliegende Dissertationsschrift geht der Frage nach, wie die Qualität von Haushaltsbefragungen zum Verkehrsverhalten bestimmt, bewertet und für zukünftige Erhebungen sichergestellt werden kann. Dabei spielt die Teilnahmebereitschaft als ein bedeutsamer, keinesfalls aber alleiniger Qualitätsindikator eine wichtige Rolle. Eine zentrale Forschungsfrage der Arbeit beschäftigt sich mit der Eignung gruppenspezifischer Ansprache und Befragung zur Erhöhung der Befragungsqualität.

Ausgehend von grundsätzlichen Betrachtungen zu Bedeutung, wesentlichen Designelementen und insbesondere Fehlerquellen von Befragungen in der empirischen Sozialforschung, sind verschiedene Ansätze zur Einschätzung der Qualität einer Befragung Gegenstand dieses Teils der Arbeit. Darauf aufbauend werden stichtagsbezogene Haushaltsbefragungen zum Verkehrsverhalten im Alltag als Spezialfall von Befragungen eingeführt und Ansätze zur Qualitätssicherung bei derartigen Haushaltsbefragungen zusammengetragen.

Eine Gegenüberstellung von Designelementen und möglichen Fehlern bei Verkehrsverhaltensbefragungen ermöglicht die Identifizierung und Beschreibung von neun Handlungsfeldern. Im Kontext von Erkenntnissen aus der Erhebungspraxis sowie Erfahrungen bei der Durchführung mehrerer Durchgänge des Forschungsprojektes „Mobilität in Städten – SrV“ wird eingeschätzt, inwieweit die jeweiligen Handlungsfelder zur Verminderung von Fehlereinflüssen und zur Qualitätssicherung beitragen können. Auf dieser Grundlage werden konkrete Handlungsoptionen ausgewählt. Für die Einschätzung der Handlungsoptionen hinsichtlich ihrer Wirksamkeit bei der Sicherung und Steigerung der Erhebungsqualität können auf Basis umfangreicher Literaturauswertungen insgesamt 24 Qualitätsindikatoren zusammengestellt werden. Diese ermöglichen die systematische Bewertung der Erfassungs-, Inhalts- und Durchführungsqualität.

Unter Einbeziehung der Untersuchungsgruppen und Qualitätsindikatoren wird in einer vertieften empirischen Analyse untersucht, in welchem Maße die ausgewählten Handlungsoptionen zur gruppenspezifischen Ansprache und Befragung geeignet sind und ob sich dadurch die Erhebungsqualität steigern lässt. Neben umfangreichen Gruppenvergleichen findet eine Befragungssimulation nach der Monte-Carlo-Methode statt. Die Datenbasis dieser Analysen besteht überwiegend aus den Erhebungsdurchgängen und Sondererhebungen von „Mobilität in Städten – SrV“. Im Ergebnis der Analysen lassen sich Empfehlungen für die Weiterentwicklung von Haushaltsbefragungen zur Erfassung von Verkehrsverhaltensdaten ableiten. Diese gliedern sich in einen gruppenübergreifenden Teil und einen Abschnitt für spezielle Gruppen. Noch vorangestellt ist die klare Empfehlung, bei Haushaltsbefragungen zukünftig nur noch eine Person des Haushalts zu ihren Wegen am Stichtag zu befragen. Dieses Vorgehen erleichtert die zukünftige Implementierung (sogar trennscharfer) gruppenspezifischer Ansätze deutlich.

Die zusammengestellten Empfehlungen bieten eine konsistente, praktikable und auf andere Befragungen übertragbare Basis, das Erhebungsdesign bestehender und zukünftiger Haushaltsbefragungen zum Verkehrsverhalten so anzupassen, dass durch die vollständige oder zumindest teilweise Umsetzung gruppenspezifischer Ansätze eine Verbesserung der Erhebungsqualität insgesamt erzielt werden kann.</dc:description>
   <dc:description>This dissertation addresses the question of how the quality of household travel surveys can be determined, evaluated, and ensured for the development of future surveys. The willingness of individuals to participate is an important, but by no means exclusive, indicator of quality. A central research question of this work deals with the concept of combining group-specific survey methods to increase survey quality.

Beginning with some fundamental terminological considerations, this work then focuses on essential design elements and, in particular, sources of error in surveys in empirical social research in order to establish various approaches for assessing the quality of a survey. Building on this, household travel surveys are introduced in specific examples, and approaches for quality assurance in such household travel surveys are compiled.

A comparison of design elements and possible errors in household travel surveys provides for the identification and description of nine areas of activity. In the context of findings from the surveying practice as well as experience in conducting several waves of the research project ''Mobility in Cities –  SrV'', the extent to which the respective fields of action can contribute to the reduction of errors and to quality assurance was assessed. On this basis, concrete options for action were selected. In order to evaluate the courses for action in terms of their effectiveness in securing and increasing the quality of surveys, a total of 24 quality indicators were compiled on the basis of extensive literature review. These allowed for the systematic assessment of three areas: quality of collection, content, and of application.

With the involvement of analysis groups and quality indicators, a detailed empirical analysis was carried out to examine the extent to which the selected courses for action are suitable for group-specific combinations of survey methods and whether this can increase survey quality. In addition to extensive group comparisons, a survey simulation was implemented using the Monte Carlo method. The data foundation for these analyses primarily consisted of the surveys carried out for the research project ''Mobility in Cities – SrV''. These analyses enabled the establishment of recommendations which further the development of household travel surveys; these were divided into a cross-group section as well as a section for specific groups. Prior to these recommendations, it became clear that for future household travel surveys, only one individual in the household should be questioned regarding their trips on specific reference days. This decision significantly facilitates the future implementation of (even more selective) group-specific approaches.

The compiled recommendations provide a consistent, practical foundation that can be applied to other surveys in order to adapt the design of existing and future household travel surveys, thus providing an overall or at least partial implementation of group-specific approaches which can improve overall survey quality.</dc:description>
   <dc:subject>Haushaltsbefragung, Verkehrsverhalten, Mobilität, Querschnittsbefragung, Befragungsmethoden, Erhebungsqualität, Qualitätsindikatoren, Erhebungsfehler, Teilnahmebereitschaft, Antwortquote, Ausschöpfung</dc:subject>
   <dc:subject>household travel survey, travel behavior, mobility, cross-sectional survey, survey modes, survey quality, quality indicators, survey errors, bias, nonresponse, response rate</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/380</dc:subject>
   <dc:creator>Hubrich,Stefan</dc:creator>
   <dc:contributor>Gerike,Regine</dc:contributor>
   <dc:contributor>Sommer,Carsten</dc:contributor>
   <dc:contributor>Technische Universität Dresden</dc:contributor>
   <dc:date>2017-07-03</dc:date>
   <dc:date>2017-11-24</dc:date>
   <dc:date>2018-01-09</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>
                    doctoralThesis
                </dc:type>
</oai_dc:dc>', false, 'qucosa:30686');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (115, 22, '2018-12-04 14:25:19.86+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Fehlereinflüsse und Teilnahmebereitschaft bei Haushaltsbefragungen zum Verkehrsverhalten</dc:title>
   <dcterms:alternative ddb:type="translated" xml:lang="eng" xsi:type="ddb:titleISO639-2">Causes for Error and Willingness to Participate in Household Travel Surveys</dcterms:alternative>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Stefan</pc:foreName>
            <pc:surName>Hubrich</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Haushaltsbefragung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Verkehrsverhalten</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Mobilität</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Querschnittsbefragung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Befragungsmethoden</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Erhebungsqualität</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Qualitätsindikatoren</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Erhebungsfehler</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Teilnahmebereitschaft</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Antwortquote</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Ausschöpfung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">household travel survey</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">travel behavior</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">mobility</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">cross-sectional survey</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">survey modes</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">survey quality</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">quality indicators</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">survey errors</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">bias</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">nonresponse</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">response rate</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">MS 5950</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 3300</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Die vorliegende Dissertationsschrift geht der Frage nach, wie die Qualität von Haushaltsbefragungen zum Verkehrsverhalten bestimmt, bewertet und für zukünftige Erhebungen sichergestellt werden kann. Dabei spielt die Teilnahmebereitschaft als ein bedeutsamer, keinesfalls aber alleiniger Qualitätsindikator eine wichtige Rolle. Eine zentrale Forschungsfrage der Arbeit beschäftigt sich mit der Eignung gruppenspezifischer Ansprache und Befragung zur Erhöhung der Befragungsqualität.

Ausgehend von grundsätzlichen Betrachtungen zu Bedeutung, wesentlichen Designelementen und insbesondere Fehlerquellen von Befragungen in der empirischen Sozialforschung, sind verschiedene Ansätze zur Einschätzung der Qualität einer Befragung Gegenstand dieses Teils der Arbeit. Darauf aufbauend werden stichtagsbezogene Haushaltsbefragungen zum Verkehrsverhalten im Alltag als Spezialfall von Befragungen eingeführt und Ansätze zur Qualitätssicherung bei derartigen Haushaltsbefragungen zusammengetragen.

Eine Gegenüberstellung von Designelementen und möglichen Fehlern bei Verkehrsverhaltensbefragungen ermöglicht die Identifizierung und Beschreibung von neun Handlungsfeldern. Im Kontext von Erkenntnissen aus der Erhebungspraxis sowie Erfahrungen bei der Durchführung mehrerer Durchgänge des Forschungsprojektes „Mobilität in Städten – SrV“ wird eingeschätzt, inwieweit die jeweiligen Handlungsfelder zur Verminderung von Fehlereinflüssen und zur Qualitätssicherung beitragen können. Auf dieser Grundlage werden konkrete Handlungsoptionen ausgewählt. Für die Einschätzung der Handlungsoptionen hinsichtlich ihrer Wirksamkeit bei der Sicherung und Steigerung der Erhebungsqualität können auf Basis umfangreicher Literaturauswertungen insgesamt 24 Qualitätsindikatoren zusammengestellt werden. Diese ermöglichen die systematische Bewertung der Erfassungs-, Inhalts- und Durchführungsqualität.

Unter Einbeziehung der Untersuchungsgruppen und Qualitätsindikatoren wird in einer vertieften empirischen Analyse untersucht, in welchem Maße die ausgewählten Handlungsoptionen zur gruppenspezifischen Ansprache und Befragung geeignet sind und ob sich dadurch die Erhebungsqualität steigern lässt. Neben umfangreichen Gruppenvergleichen findet eine Befragungssimulation nach der Monte-Carlo-Methode statt. Die Datenbasis dieser Analysen besteht überwiegend aus den Erhebungsdurchgängen und Sondererhebungen von „Mobilität in Städten – SrV“. Im Ergebnis der Analysen lassen sich Empfehlungen für die Weiterentwicklung von Haushaltsbefragungen zur Erfassung von Verkehrsverhaltensdaten ableiten. Diese gliedern sich in einen gruppenübergreifenden Teil und einen Abschnitt für spezielle Gruppen. Noch vorangestellt ist die klare Empfehlung, bei Haushaltsbefragungen zukünftig nur noch eine Person des Haushalts zu ihren Wegen am Stichtag zu befragen. Dieses Vorgehen erleichtert die zukünftige Implementierung (sogar trennscharfer) gruppenspezifischer Ansätze deutlich.

Die zusammengestellten Empfehlungen bieten eine konsistente, praktikable und auf andere Befragungen übertragbare Basis, das Erhebungsdesign bestehender und zukünftiger Haushaltsbefragungen zum Verkehrsverhalten so anzupassen, dass durch die vollständige oder zumindest teilweise Umsetzung gruppenspezifischer Ansätze eine Verbesserung der Erhebungsqualität insgesamt erzielt werden kann.</dcterms:abstract>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">This dissertation addresses the question of how the quality of household travel surveys can be determined, evaluated, and ensured for the development of future surveys. The willingness of individuals to participate is an important, but by no means exclusive, indicator of quality. A central research question of this work deals with the concept of combining group-specific survey methods to increase survey quality.

Beginning with some fundamental terminological considerations, this work then focuses on essential design elements and, in particular, sources of error in surveys in empirical social research in order to establish various approaches for assessing the quality of a survey. Building on this, household travel surveys are introduced in specific examples, and approaches for quality assurance in such household travel surveys are compiled.

A comparison of design elements and possible errors in household travel surveys provides for the identification and description of nine areas of activity. In the context of findings from the surveying practice as well as experience in conducting several waves of the research project ''Mobility in Cities –  SrV'', the extent to which the respective fields of action can contribute to the reduction of errors and to quality assurance was assessed. On this basis, concrete options for action were selected. In order to evaluate the courses for action in terms of their effectiveness in securing and increasing the quality of surveys, a total of 24 quality indicators were compiled on the basis of extensive literature review. These allowed for the systematic assessment of three areas: quality of collection, content, and of application.

With the involvement of analysis groups and quality indicators, a detailed empirical analysis was carried out to examine the extent to which the selected courses for action are suitable for group-specific combinations of survey methods and whether this can increase survey quality. In addition to extensive group comparisons, a survey simulation was implemented using the Monte Carlo method. The data foundation for these analyses primarily consisted of the surveys carried out for the research project ''Mobility in Cities – SrV''. These analyses enabled the establishment of recommendations which further the development of household travel surveys; these were divided into a cross-group section as well as a section for specific groups. Prior to these recommendations, it became clear that for future household travel surveys, only one individual in the household should be questioned regarding their trips on specific reference days. This decision significantly facilitates the future implementation of (even more selective) group-specific approaches.

The compiled recommendations provide a consistent, practical foundation that can be applied to other surveys in order to adapt the design of existing and future household travel surveys, thus providing an overall or at least partial implementation of group-specific approaches which can improve overall survey quality.</dcterms:abstract>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Regine</pc:foreName>
            <pc:surName>Gerike</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Carsten</pc:foreName>
            <pc:surName>Sommer</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:dateSubmitted xsi:type="dcterms:W3CDTF">2017-07-03</dcterms:dateSubmitted>
   <dcterms:dateAccepted xsi:type="dcterms:W3CDTF">2017-11-24</dcterms:dateAccepted>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-01-09</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-12-04</dcterms:modified>
   <dc:type xsi:type="dini:PublType">doctoralThesis</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-231510</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="ddb:noScheme">Schriftenreihe des Instituts für Verkehrsplanung und Straßenverkehr ; Heft 18/2017</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa-94856</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>thesis.doctoral</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Technische Universität Dresden</cc:name>
            <cc:place>Dresden</cc:place>
            <cc:department>
               <cc:name>Fakultät Verkehrswissenschaften ''Friedrich List''</cc:name>
            </cc:department>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:30686/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-231510</ddb:identifier>
   <ddb:identifier ddb:type="Erstkat-ID">498523853</ddb:identifier>
   <ddb:identifier ddb:type="VG-Wort-Pixel">3e8546be49c44b8ea35efc008304d6a2</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:30686');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (118, 17, '2018-12-05 09:02:18.125+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Differenzierte Bioaerosolmessungen:Größenfraktionierte und kontinuierliche Messung von Bioaerosolen in der Emission von Geflügelhaltungen</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-316358</dc:identifier>
   <dc:identifier/>
   <dc:language>ger</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa-38419</dc:relation>
   <dc:description>Es wurden erstmals die Größenverteilungen von Bioaerosolen, sowie deren tageszeitlicher Konzentrationsverlauf in der Emission von Anlagen zur Geflügelhaltung mittels standardisierter Verfahren bestimmt.
Die Ergebnisse richten sich im Rahmen von Genehmigungsverfahren an Anlagenbetreiber, Gutachter und Behörden. Sie legen nahe, dass zukünftig sowohl die Partikelgrößenverteilung, als auch die tageszeitlichen Emissionsschwankungen bei der Ausbreitungsrechnung von Bioaerosolen berücksichtigt werden sollten. Bezüglich der verwendeten Emissionsfaktoren wird eine Halbierung des Konventionswertes für Legehennen und Putenmast aufgezeigt, bezüglich des Größenklassenspektrums für Anlagen der Geflügelhaltung eine Aufteilung von 15 % PM 2,5; 25 % PM 10; und 60 % TSP.</dc:description>
   <dc:subject>Klimaschutz, Gesundheit, Aerosole</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/590</dc:subject>
   <dc:subject>Sachsen; Truthuhnhaltung; Truthuhnmast; Schadstoffemission; Bioaerosol</dc:subject>
   <dc:creator>Clauß,Marcus</dc:creator>
   <dc:creator>Linke,Stefan</dc:creator>
   <dc:creator>Springorum,Annette Christiane</dc:creator>
   <dc:publisher>Sächsisches Landesamt für Umwelt, Landwirtschaft und Geologie</dc:publisher>
   <dc:date>2018</dc:date>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2018-11-08</dc:date>
   <dc:source/>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>book</dc:type>
</oai_dc:dc>', false, 'qucosa:31635');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (119, 22, '2018-12-05 09:02:18.125+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Differenzierte Bioaerosolmessungen</dc:title>
   <dcterms:alternative xml:lang="ger" xsi:type="ddb:talternativeISO639-2">Größenfraktionierte und kontinuierliche Messung von Bioaerosolen in der Emission von Geflügelhaltungen</dcterms:alternative>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Marcus</pc:foreName>
            <pc:surName>Clauß</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Stefan</pc:foreName>
            <pc:surName>Linke</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Annette Christiane</pc:foreName>
            <pc:surName>Springorum</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Klimaschutz</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Gesundheit</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Aerosole</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">590</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">590</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZD 24218</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AR 23400</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Sachsen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Truthuhnhaltung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Truthuhnmast</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Schadstoffemission</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Bioaerosol</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Es wurden erstmals die Größenverteilungen von Bioaerosolen, sowie deren tageszeitlicher Konzentrationsverlauf in der Emission von Anlagen zur Geflügelhaltung mittels standardisierter Verfahren bestimmt.
Die Ergebnisse richten sich im Rahmen von Genehmigungsverfahren an Anlagenbetreiber, Gutachter und Behörden. Sie legen nahe, dass zukünftig sowohl die Partikelgrößenverteilung, als auch die tageszeitlichen Emissionsschwankungen bei der Ausbreitungsrechnung von Bioaerosolen berücksichtigt werden sollten. Bezüglich der verwendeten Emissionsfaktoren wird eine Halbierung des Konventionswertes für Legehennen und Putenmast aufgezeigt, bezüglich des Größenklassenspektrums für Anlagen der Geflügelhaltung eine Aufteilung von 15 % PM 2,5; 25 % PM 10; und 60 % TSP.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Sächsisches Landesamt für Umwelt, Landwirtschaft und Geologie</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-11-08</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2018</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-12-05</dcterms:modified>
   <dc:type xsi:type="dini:PublType">book</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-316358</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa-38419</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid.example.com/qucosa:31635/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-316358</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:31635');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (121, 17, '2018-12-11 13:05:20.629+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>SLUB Zeitschrift</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-704887</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:subject>Zeitschrift</dc:subject>
   <dc:date>2018-10-16</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>Periodical</dc:type>
</oai_dc:dc>', false, 'qucosa:70488');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (122, 22, '2018-12-11 13:05:20.629+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">SLUB Zeitschrift</dc:title>
   <dc:subject xsi:type="xMetaDiss:noScheme">Zeitschrift</dc:subject>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-10-16</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-12-11</dcterms:modified>
   <dc:type xsi:type="dini:PublType">Periodical</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-704887</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>0</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:70488/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-704887</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70488');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (124, 17, '2018-12-18 14:38:17.371+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Ein Beitrag zur funkgestützten Indoor-Positionierung auf der Basis von Leckwellenleitern in Fahrgastzellen</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-319009</dc:identifier>
   <dc:identifier>512025681</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:description>In dieser Arbeit wird der Einsatz von Leckwellenleiter (engl. Leaky Coaxial Cable, LCX) zur funkgestützten Indoor-Positionierung in Fahrgastzellen untersucht. Mit Hilfe eines erstellten Vorgehensmodells werden zwei unterschiedliche LCX-Prototypen speziell für den Ortungseinsatz entwickelt. Hierbei wird die elektromagnetische Feldberechnung verwendet, um sowohl Leckwellenleiterstrukturen als auch deren Einsatz in einer Fahrgastzelle zu bewerten. Nach Fertigung beider Leckwellenleiter erfolgt eine messtechnische Validierung in einer vordefinierten Fahrgastzellenumgebung. Der Einsatz dieser Prototypen zur Indoor-Positionierung wird sowohl in Modell- als auch in realen Fahrzeugumgebungen, wie der AutoTram Extra Grand des Fraunhofer IVI, durchgeführt. Eine statistische Betrachtung von Messergebnissen sowie
die Vorstellung eines zonenselektiven Positionierungsansatzes schließen diese Arbeit.</dc:description>
   <dc:subject>Indoor-Positionierung, Leckwellenleiter, Fahrgastzelle, funkgestützte Positionierung, Leckwellenleitersimulation</dc:subject>
   <dc:subject>Indoor positioning, leaky fiber, passenger cabin, radio-based positioning, leaky-wavegoaiide simulation</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/621.3</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/380</dc:subject>
   <dc:creator>Engelbrecht,Julia Maria</dc:creator>
   <dc:contributor>Michler,Oliver</dc:contributor>
   <dc:contributor>Collmann,Ralf</dc:contributor>
   <dc:publisher>Technische Universität Dresden</dc:publisher>
   <dc:date>2016-07-27</dc:date>
   <dc:date>2018-06-06</dc:date>
   <dc:date>2018-10-12</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>
                    doctoralThesis
                </dc:type>
</oai_dc:dc>', false, 'qucosa:31900');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (125, 22, '2018-12-18 14:38:17.371+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Ein Beitrag zur funkgestützten Indoor-Positionierung auf der Basis von Leckwellenleitern in Fahrgastzellen</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Julia Maria</pc:foreName>
            <pc:surName>Engelbrecht</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Indoor-Positionierung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Leckwellenleiter</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Fahrgastzelle</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">funkgestützte Positionierung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Leckwellenleitersimulation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Indoor positioning</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">leaky fiber</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">passenger cabin</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">radio-based positioning</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">leaky-wavegoaiide simulation</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">621.3</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">621.3</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZQ 3950</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">In dieser Arbeit wird der Einsatz von Leckwellenleiter (engl. Leaky Coaxial Cable, LCX) zur funkgestützten Indoor-Positionierung in Fahrgastzellen untersucht. Mit Hilfe eines erstellten Vorgehensmodells werden zwei unterschiedliche LCX-Prototypen speziell für den Ortungseinsatz entwickelt. Hierbei wird die elektromagnetische Feldberechnung verwendet, um sowohl Leckwellenleiterstrukturen als auch deren Einsatz in einer Fahrgastzelle zu bewerten. Nach Fertigung beider Leckwellenleiter erfolgt eine messtechnische Validierung in einer vordefinierten Fahrgastzellenumgebung. Der Einsatz dieser Prototypen zur Indoor-Positionierung wird sowohl in Modell- als auch in realen Fahrzeugumgebungen, wie der AutoTram Extra Grand des Fraunhofer IVI, durchgeführt. Eine statistische Betrachtung von Messergebnissen sowie
die Vorstellung eines zonenselektiven Positionierungsansatzes schließen diese Arbeit.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Technische Universität Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Oliver</pc:foreName>
            <pc:surName>Michler</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="referee" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Ralf</pc:foreName>
            <pc:surName>Collmann</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:dateSubmitted xsi:type="dcterms:W3CDTF">2016-07-27</dcterms:dateSubmitted>
   <dcterms:dateAccepted xsi:type="dcterms:W3CDTF">2018-06-06</dcterms:dateAccepted>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-10-12</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-12-18</dcterms:modified>
   <dc:type xsi:type="dini:PublType">doctoralThesis</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-319009</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>thesis.doctoral</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Technische Universität Dresden</cc:name>
            <cc:place>Dresden</cc:place>
            <cc:department>
               <cc:name>Fakultät Verkehrswissenschaften ''Friedrich List''</cc:name>
            </cc:department>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:31900/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-319009</ddb:identifier>
   <ddb:identifier ddb:type="Erstkat-ID">512025681</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:31900');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (127, 17, '2018-12-18 14:38:25.355+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Big Data im Radverkehr:Ergebnisbericht ''Mit Smartphones generierte Verhaltensdaten im Radverkehr''</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-236003</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa-230730</dc:relation>
   <dc:relation>urn:nbn:de:bsz:14-qucosa-233278</dc:relation>
   <dc:description>Zusammenfassend lässt sich sagen, dass unter Beachtung der im folgenden Bericht beschriebenen Faktoren GPS-Daten, im vorliegenden Fall exemplarisch untersucht anhand von Daten der Firma Strava Inc., mit einigen Einschränkungen für die Radverkehrsplanung nutzbar sind. Bereits heute sind damit Auswertungen möglich, die zeigen, wo, wann und wie sich Radfahrende im gesamten Netz bewegen. Die mittels Smartphone-App generierten Daten können sehr sinnvoll als Ergänzung zu bestehenden Dauerzählstellen von Kommunen genutzt werden. Berücksichtigt werden sollten bei der Auswertung und Interpretation der Daten jedoch einige Aspekte, wie der eher sportlich orientierte Kontext der erfassten Routen in den untersuchten Beispielen. Des Weiteren werden aktuell die Daten zum Teil noch als Datenbank- oder GIS-Dateien zur Verfügung gestellt bzw. befinden sich Online-
Masken zur einfacheren Nutzung im Aufbau oder einem ersten Nutzungsstadium. Die Auswertung und Interpretation erfordert also weiterhin Fachkompetenz und auch personelle Ressourcen. Der Einsatz dieser wird jedoch voraussichtlich durch die Weiterentwicklung von Web-Oberflächen und unterstützenden Auswertemasken abnehmen. Hier gilt es zukünftig, in Zusammenarbeit mit den Kommunen, die benötigten Parameter sowie die geeignetsten Aufbereitungsformen zu erarbeiten.
Im Forschungsprojekt erfolgte ein Ansatz der Hochrechnung von Radverkehrsstärken aus Stichproben von GPS-Daten auf das gesamte Netz. Dieser konnte auch erfolgreich in einer weiteren Kommune verifiziert werden. Jedoch ist auch hier in Zukunft noch Forschungsbedarf vorhanden bzw. die Anpassung auf lokale Gegebenheiten notwendig.</dc:description>
   <dc:subject>Radverkehr, GPS-Daten, Verkehrsverhalten</dc:subject>
   <dc:subject>cycling, GPS-tracks, traffic</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/380</dc:subject>
   <dc:subject>Dresden</dc:subject>
   <dc:subject>Radfahrer</dc:subject>
   <dc:subject>Radfahrerverkehr</dc:subject>
   <dc:subject>Verkehrsverhalten</dc:subject>
   <dc:subject>Datensammlung</dc:subject>
   <dc:subject>Smarthphone</dc:subject>
   <dc:subject>Orientierung</dc:subject>
   <dc:creator>Francke,Angela</dc:creator>
   <dc:creator>Becker,Thilo</dc:creator>
   <dc:creator>Lißner,Sven</dc:creator>
   <dc:creator>Technische Universität Dresden.Fakultät Verkehrswissenschaften ''Friedrich List''.Institut für Verkehrsplanung und Straßenverkehr.Professur für Verkehrsökologie</dc:creator>
   <dc:date>2018</dc:date>
   <dc:date>2018-10-19</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>workingPaper</dc:type>
</oai_dc:dc>', false, 'qucosa:31011');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (128, 22, '2018-12-18 14:38:25.355+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Big Data im Radverkehr</dc:title>
   <dcterms:alternative xml:lang="ger" xsi:type="ddb:talternativeISO639-2">Ergebnisbericht ''Mit Smartphones generierte Verhaltensdaten im Radverkehr''</dcterms:alternative>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Angela</pc:foreName>
            <pc:surName>Francke</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Thilo</pc:foreName>
            <pc:surName>Becker</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Sven</pc:foreName>
            <pc:surName>Lißner</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Radverkehr</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">GPS-Daten</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Verkehrsverhalten</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">cycling</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">GPS-tracks</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">traffic</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">380</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">380</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ZO 4340</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Dresden</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Radfahrer</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Radfahrerverkehr</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Verkehrsverhalten</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Datensammlung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Smarthphone</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Orientierung</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Zusammenfassend lässt sich sagen, dass unter Beachtung der im folgenden Bericht beschriebenen Faktoren GPS-Daten, im vorliegenden Fall exemplarisch untersucht anhand von Daten der Firma Strava Inc., mit einigen Einschränkungen für die Radverkehrsplanung nutzbar sind. Bereits heute sind damit Auswertungen möglich, die zeigen, wo, wann und wie sich Radfahrende im gesamten Netz bewegen. Die mittels Smartphone-App generierten Daten können sehr sinnvoll als Ergänzung zu bestehenden Dauerzählstellen von Kommunen genutzt werden. Berücksichtigt werden sollten bei der Auswertung und Interpretation der Daten jedoch einige Aspekte, wie der eher sportlich orientierte Kontext der erfassten Routen in den untersuchten Beispielen. Des Weiteren werden aktuell die Daten zum Teil noch als Datenbank- oder GIS-Dateien zur Verfügung gestellt bzw. befinden sich Online-
Masken zur einfacheren Nutzung im Aufbau oder einem ersten Nutzungsstadium. Die Auswertung und Interpretation erfordert also weiterhin Fachkompetenz und auch personelle Ressourcen. Der Einsatz dieser wird jedoch voraussichtlich durch die Weiterentwicklung von Web-Oberflächen und unterstützenden Auswertemasken abnehmen. Hier gilt es zukünftig, in Zusammenarbeit mit den Kommunen, die benötigten Parameter sowie die geeignetsten Aufbereitungsformen zu erarbeiten.
Im Forschungsprojekt erfolgte ein Ansatz der Hochrechnung von Radverkehrsstärken aus Stichproben von GPS-Daten auf das gesamte Netz. Dieser konnte auch erfolgreich in einer weiteren Kommune verifiziert werden. Jedoch ist auch hier in Zukunft noch Forschungsbedarf vorhanden bzw. die Anpassung auf lokale Gegebenheiten notwendig.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Technische Universität Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-10-19</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2018</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-12-18</dcterms:modified>
   <dc:type xsi:type="dini:PublType">workingPaper</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-236003</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://tud.example.com/qucosa:31011/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-236003</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:31011');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (130, 17, '2019-01-08 15:16:46.18+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Test Bachelorarbeit DIU XYZ:Untertitel DIU</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-704793</dc:identifier>
   <dc:identifier/>
   <dc:language>ger</dc:language>
   <dc:relation>1.ausgabe_doi</dc:relation>
   <dc:description>xxx:Kurzfassung..................................................................................................... II
Aufgabenstellung ............................................................................................ III
Inhaltsverzeichnis ............................................................................................ V
Nomenklatur ................................................................................................... VII
Abbildungs- und Tabellenverzeichnis ............................................................... IX
Vorwort ............................................................................................................ XI
1 Einleitung .......................................................................................................1
2 Theorie thermischer Energiespeicher ............................................................3
2.1 Beschreibung von Wärmespeichern ...........................................................3
2.2 Sensible Wärmespeicher ............................................................................4
2.3 Latente Wärmespeicher.............................................................................. 9
2.4 Sorptive Wärmespeicher ............................................................................12
2.5 Chemische Wärmespeicher ....................................................................... 14
3 Spezifikation des thermochemischen Speichersystems ................................ 17
3.1 Thermochemische Grundlagen .................................................................. 17
3.2 Motivation der Aufgabenstellung ............................................................... 20
3.3 Charakterisierung des Reaktionssystems ................................................. 21
4 Systembeschreibung des Speicherkonzepts ................................................ 26
4.1 Kurzdarstellung der Ausgangssituation .................................................... 26
4.2 Weiterentwicklung zum bewegten Reaktionsbett ..................................... 27
4.2.1 Theorie des bewegten Reaktionsbettes ................................................ 27
4.2.2 Konstruktion des Reaktors .................................................................... 28
4.2.3 Förderung des Speichermaterials .......................................................... 31
4.3 Periphere Anlagenteile ............................................................................. 33
4.3.1 Anlagenschema ..................................................................................... 33
4.3.2 Entwurf des Druckhalters ...................................................................... 35
INHALTSVERZEICHNIS VI
4.3.3 Ausführung der Elektro- und Messtechnik ............................................. 37
5 Experimentelle Untersuchungen ................................................................. 39
5.1 Versuchsdurchführung ............................................................................. 39
5.2 Betrieb der Fördereinheiten .................................................................... 40
5.3 Optimierung der Fördereinheiten ............................................................ 44
5.3.1 Inaktive Mischpaddel ............................................................................ 44
5.3.2 Modifizierte Mischpaddel ....................................................................... 47
5.4 Erkenntnisse ............................................................................................ 49
6 Finales Konzept des Versuchsstandes ........................................................ 50
6.1 Lösungsansätze für den Massenfluss ...................................................... 50
6.2 Gestaltung der Austragshilfe ................................................................... 54
7 Zusammenfassung und Ausblick ................................................................. 57
Eidesstattliche Erklärung ............................................................................... 59
Literatur- und Quellenverzeichnis .................................................................. 60
Anlagen ......................................................................................................... 63
A.1. Parametrierung des Temperaturwächters (Kapitel 4.3.3) ....................... 63
A.2. Inhalt des beigelegten Datenträgers (Einband) ..................................... 63
A.3. Berechnung der Aufheizstrecke des Stickstoffstroms (Kapitel 4.3.1) ...... 64
A.4. Konstruktionszeichnung des Druckhalters (Kapitel 4.3.2) ...................... 65
A.5. Dampftafel: Sättigungsdampfdruck von Wasserdampf (Kapitel 4.3.2) .... 66
A.6. Stromlaufpläne und Baugruppenliste des Teststandes (Kapitel 4.3.3) ... ....67
A.7. Ermittlung der Kabelquerschnitte für Stromlaufplan (Kapitel 4.3.3) ........73
A.8. Parametrierung der Frequenzumrichter (Kapitel 5.1) ....................... 74
A.9. Ergebnisse der Kalibiermessungen (Kapitel 5.2) ............................75
A.10. Berechnungen zur Dynamik des Schlitzschiebers (Kapitel 6.1) ............. 76
A.11. Konstruktionszeichnungen der Austragshilfe (Kapitel 6.2) .................77</dc:description>
   <dc:subject>xxx</dc:subject>
   <dc:subject>xxx</dc:subject>
   <dc:subject>test freie Schlagwörter2</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/600</dc:subject>
   <dc:creator>Renner,Angelika</dc:creator>
   <dc:contributor>Freitag,Andreas</dc:contributor>
   <dc:contributor>Heimann,Ingrid</dc:contributor>
   <dc:contributor>Gründler,Helmut</dc:contributor>
   <dc:contributor>Dresden International University</dc:contributor>
   <dc:date>2018-07-19</dc:date>
   <dc:date>2018-07-31</dc:date>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2018-07-31</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>bachelorThesis</dc:type>
</oai_dc:dc>', false, 'qucosa:70479');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (131, 22, '2019-01-08 15:16:46.18+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Test Bachelorarbeit DIU XYZ</dc:title>
   <dcterms:alternative xml:lang="ger" xsi:type="ddb:talternativeISO639-2">Untertitel DIU</dcterms:alternative>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Angelika</pc:foreName>
            <pc:surName>Renner</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">xxx</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">xxx</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">test freie Schlagwörter2</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">600</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">600</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">yb 123</dc:subject>
   <dcterms:tableOfContents ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Kurzfassung..................................................................................................... II
Aufgabenstellung ............................................................................................ III
Inhaltsverzeichnis ............................................................................................ V
Nomenklatur ................................................................................................... VII
Abbildungs- und Tabellenverzeichnis ............................................................... IX
Vorwort ............................................................................................................ XI
1 Einleitung .......................................................................................................1
2 Theorie thermischer Energiespeicher ............................................................3
2.1 Beschreibung von Wärmespeichern ...........................................................3
2.2 Sensible Wärmespeicher ............................................................................4
2.3 Latente Wärmespeicher.............................................................................. 9
2.4 Sorptive Wärmespeicher ............................................................................12
2.5 Chemische Wärmespeicher ....................................................................... 14
3 Spezifikation des thermochemischen Speichersystems ................................ 17
3.1 Thermochemische Grundlagen .................................................................. 17
3.2 Motivation der Aufgabenstellung ............................................................... 20
3.3 Charakterisierung des Reaktionssystems ................................................. 21
4 Systembeschreibung des Speicherkonzepts ................................................ 26
4.1 Kurzdarstellung der Ausgangssituation .................................................... 26
4.2 Weiterentwicklung zum bewegten Reaktionsbett ..................................... 27
4.2.1 Theorie des bewegten Reaktionsbettes ................................................ 27
4.2.2 Konstruktion des Reaktors .................................................................... 28
4.2.3 Förderung des Speichermaterials .......................................................... 31
4.3 Periphere Anlagenteile ............................................................................. 33
4.3.1 Anlagenschema ..................................................................................... 33
4.3.2 Entwurf des Druckhalters ...................................................................... 35
INHALTSVERZEICHNIS VI
4.3.3 Ausführung der Elektro- und Messtechnik ............................................. 37
5 Experimentelle Untersuchungen ................................................................. 39
5.1 Versuchsdurchführung ............................................................................. 39
5.2 Betrieb der Fördereinheiten .................................................................... 40
5.3 Optimierung der Fördereinheiten ............................................................ 44
5.3.1 Inaktive Mischpaddel ............................................................................ 44
5.3.2 Modifizierte Mischpaddel ....................................................................... 47
5.4 Erkenntnisse ............................................................................................ 49
6 Finales Konzept des Versuchsstandes ........................................................ 50
6.1 Lösungsansätze für den Massenfluss ...................................................... 50
6.2 Gestaltung der Austragshilfe ................................................................... 54
7 Zusammenfassung und Ausblick ................................................................. 57
Eidesstattliche Erklärung ............................................................................... 59
Literatur- und Quellenverzeichnis .................................................................. 60
Anlagen ......................................................................................................... 63
A.1. Parametrierung des Temperaturwächters (Kapitel 4.3.3) ....................... 63
A.2. Inhalt des beigelegten Datenträgers (Einband) ..................................... 63
A.3. Berechnung der Aufheizstrecke des Stickstoffstroms (Kapitel 4.3.1) ...... 64
A.4. Konstruktionszeichnung des Druckhalters (Kapitel 4.3.2) ...................... 65
A.5. Dampftafel: Sättigungsdampfdruck von Wasserdampf (Kapitel 4.3.2) .... 66
A.6. Stromlaufpläne und Baugruppenliste des Teststandes (Kapitel 4.3.3) ... ....67
A.7. Ermittlung der Kabelquerschnitte für Stromlaufplan (Kapitel 4.3.3) ........73
A.8. Parametrierung der Frequenzumrichter (Kapitel 5.1) ....................... 74
A.9. Ergebnisse der Kalibiermessungen (Kapitel 5.2) ............................75
A.10. Berechnungen zur Dynamik des Schlitzschiebers (Kapitel 6.1) ............. 76
A.11. Konstruktionszeichnungen der Austragshilfe (Kapitel 6.2) .................77</dcterms:tableOfContents>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">xxx</dcterms:abstract>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Andreas</pc:foreName>
            <pc:surName>Freitag</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Ingrid</pc:foreName>
            <pc:surName>Heimann</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dc:contributor thesis:role="advisor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Helmut</pc:foreName>
            <pc:surName>Gründler</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:dateSubmitted xsi:type="dcterms:W3CDTF">2018-07-19</dcterms:dateSubmitted>
   <dcterms:dateAccepted xsi:type="dcterms:W3CDTF">2018-07-31</dcterms:dateAccepted>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-07-31</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-01-08</dcterms:modified>
   <dc:type xsi:type="dini:PublType">bachelorThesis</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-704793</dc:identifier>
   <dc:identifier xsi:type="urn:isbn"/>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <thesis:degree>
      <thesis:level>bachelor</thesis:level>
      <thesis:grantor xsi:type="cc:Corporate">
         <cc:universityOrInstitution>
            <cc:name>Dresden International University</cc:name>
            <cc:place>Dresden</cc:place>
            <cc:department>
               <cc:name>Medizin</cc:name>
            </cc:department>
         </cc:universityOrInstitution>
      </thesis:grantor>
   </thesis:degree>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://diu.example.com/qucosa:70479/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-704793</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70479');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (133, 17, '2019-01-08 15:53:59.046+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Test Artikel2...:Untertitel DIU</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-704809</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:language>ger</dc:language>
   <dc:relation>nbn:q89e42398tzg</dc:relation>
   <dc:relation>10.testquelledoi</dc:relation>
   <dc:relation>1234-1234</dc:relation>
   <dc:relation>3456-3456</dc:relation>
   <dc:relation>123456</dc:relation>
   <dc:relation>10.testverweis</dc:relation>
   <dc:relation>10.doi/schriftenreihetest</dc:relation>
   <dc:relation>10.12345/oparatest</dc:relation>
   <dc:description>xxx:Kurzfassung..................................................................................................... II
Aufgabenstellung ............................................................................................ III
Inhaltsverzeichnis ............................................................................................ V
Nomenklatur ................................................................................................... VII
Abbildungs- und Tabellenverzeichnis ............................................................... IX
Vorwort ............................................................................................................ XI
1 Einleitung .......................................................................................................1
2 Theorie thermischer Energiespeicher ............................................................3
2.1 Beschreibung von Wärmespeichern ...........................................................3
2.2 Sensible Wärmespeicher ............................................................................4
2.3 Latente Wärmespeicher.............................................................................. 9
2.4 Sorptive Wärmespeicher ............................................................................12
2.5 Chemische Wärmespeicher ....................................................................... 14
3 Spezifikation des thermochemischen Speichersystems ................................ 17
3.1 Thermochemische Grundlagen .................................................................. 17
3.2 Motivation der Aufgabenstellung ............................................................... 20
3.3 Charakterisierung des Reaktionssystems ................................................. 21
4 Systembeschreibung des Speicherkonzepts ................................................ 26
4.1 Kurzdarstellung der Ausgangssituation .................................................... 26
4.2 Weiterentwicklung zum bewegten Reaktionsbett ..................................... 27
4.2.1 Theorie des bewegten Reaktionsbettes ................................................ 27
4.2.2 Konstruktion des Reaktors .................................................................... 28
4.2.3 Förderung des Speichermaterials .......................................................... 31
4.3 Periphere Anlagenteile ............................................................................. 33
4.3.1 Anlagenschema ..................................................................................... 33
4.3.2 Entwurf des Druckhalters ...................................................................... 35
INHALTSVERZEICHNIS VI
4.3.3 Ausführung der Elektro- und Messtechnik ............................................. 37
5 Experimentelle Untersuchungen ................................................................. 39
5.1 Versuchsdurchführung ............................................................................. 39
5.2 Betrieb der Fördereinheiten .................................................................... 40
5.3 Optimierung der Fördereinheiten ............................................................ 44
5.3.1 Inaktive Mischpaddel ............................................................................ 44
5.3.2 Modifizierte Mischpaddel ....................................................................... 47
5.4 Erkenntnisse ............................................................................................ 49
6 Finales Konzept des Versuchsstandes ........................................................ 50
6.1 Lösungsansätze für den Massenfluss ...................................................... 50
6.2 Gestaltung der Austragshilfe ................................................................... 54
7 Zusammenfassung und Ausblick ................................................................. 57
Eidesstattliche Erklärung ............................................................................... 59
Literatur- und Quellenverzeichnis .................................................................. 60
Anlagen ......................................................................................................... 63
A.1. Parametrierung des Temperaturwächters (Kapitel 4.3.3) ....................... 63
A.2. Inhalt des beigelegten Datenträgers (Einband) ..................................... 63
A.3. Berechnung der Aufheizstrecke des Stickstoffstroms (Kapitel 4.3.1) ...... 64
A.4. Konstruktionszeichnung des Druckhalters (Kapitel 4.3.2) ...................... 65
A.5. Dampftafel: Sättigungsdampfdruck von Wasserdampf (Kapitel 4.3.2) .... 66
A.6. Stromlaufpläne und Baugruppenliste des Teststandes (Kapitel 4.3.3) ... ....67
A.7. Ermittlung der Kabelquerschnitte für Stromlaufplan (Kapitel 4.3.3) ........73
A.8. Parametrierung der Frequenzumrichter (Kapitel 5.1) ....................... 74
A.9. Ergebnisse der Kalibiermessungen (Kapitel 5.2) ............................75
A.10. Berechnungen zur Dynamik des Schlitzschiebers (Kapitel 6.1) ............. 76
A.11. Konstruktionszeichnungen der Austragshilfe (Kapitel 6.2) .................77</dc:description>
   <dc:subject>xxx</dc:subject>
   <dc:subject>xxx</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/600</dc:subject>
   <dc:subject>Weihnachten &lt;Motiv&gt;</dc:subject>
   <dc:subject>Milchkuhhaltung</dc:subject>
   <dc:subject>Mondeinfluss</dc:subject>
   <dc:creator>Höricht,Josephine</dc:creator>
   <dc:contributor>Dresden International University</dc:contributor>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2018-09-10</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>article</dc:type>
</oai_dc:dc>', false, 'qucosa:70480');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (134, 22, '2019-01-08 15:53:59.046+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Test Artikel2...</dc:title>
   <dcterms:alternative xml:lang="ger" xsi:type="ddb:talternativeISO639-2">Untertitel DIU</dcterms:alternative>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Josephine</pc:foreName>
            <pc:surName>Höricht</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">xxx</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">xxx</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">600</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">600</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">sdfs</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Weihnachten &lt;Motiv&gt;</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Milchkuhhaltung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Mondeinfluss</dc:subject>
   <dcterms:tableOfContents ddb:type="subject:noScheme" xml:lang="ger ger" xsi:type="ddb:contentISO639-2">Kurzfassung..................................................................................................... II
Aufgabenstellung ............................................................................................ III
Inhaltsverzeichnis ............................................................................................ V
Nomenklatur ................................................................................................... VII
Abbildungs- und Tabellenverzeichnis ............................................................... IX
Vorwort ............................................................................................................ XI
1 Einleitung .......................................................................................................1
2 Theorie thermischer Energiespeicher ............................................................3
2.1 Beschreibung von Wärmespeichern ...........................................................3
2.2 Sensible Wärmespeicher ............................................................................4
2.3 Latente Wärmespeicher.............................................................................. 9
2.4 Sorptive Wärmespeicher ............................................................................12
2.5 Chemische Wärmespeicher ....................................................................... 14
3 Spezifikation des thermochemischen Speichersystems ................................ 17
3.1 Thermochemische Grundlagen .................................................................. 17
3.2 Motivation der Aufgabenstellung ............................................................... 20
3.3 Charakterisierung des Reaktionssystems ................................................. 21
4 Systembeschreibung des Speicherkonzepts ................................................ 26
4.1 Kurzdarstellung der Ausgangssituation .................................................... 26
4.2 Weiterentwicklung zum bewegten Reaktionsbett ..................................... 27
4.2.1 Theorie des bewegten Reaktionsbettes ................................................ 27
4.2.2 Konstruktion des Reaktors .................................................................... 28
4.2.3 Förderung des Speichermaterials .......................................................... 31
4.3 Periphere Anlagenteile ............................................................................. 33
4.3.1 Anlagenschema ..................................................................................... 33
4.3.2 Entwurf des Druckhalters ...................................................................... 35
INHALTSVERZEICHNIS VI
4.3.3 Ausführung der Elektro- und Messtechnik ............................................. 37
5 Experimentelle Untersuchungen ................................................................. 39
5.1 Versuchsdurchführung ............................................................................. 39
5.2 Betrieb der Fördereinheiten .................................................................... 40
5.3 Optimierung der Fördereinheiten ............................................................ 44
5.3.1 Inaktive Mischpaddel ............................................................................ 44
5.3.2 Modifizierte Mischpaddel ....................................................................... 47
5.4 Erkenntnisse ............................................................................................ 49
6 Finales Konzept des Versuchsstandes ........................................................ 50
6.1 Lösungsansätze für den Massenfluss ...................................................... 50
6.2 Gestaltung der Austragshilfe ................................................................... 54
7 Zusammenfassung und Ausblick ................................................................. 57
Eidesstattliche Erklärung ............................................................................... 59
Literatur- und Quellenverzeichnis .................................................................. 60
Anlagen ......................................................................................................... 63
A.1. Parametrierung des Temperaturwächters (Kapitel 4.3.3) ....................... 63
A.2. Inhalt des beigelegten Datenträgers (Einband) ..................................... 63
A.3. Berechnung der Aufheizstrecke des Stickstoffstroms (Kapitel 4.3.1) ...... 64
A.4. Konstruktionszeichnung des Druckhalters (Kapitel 4.3.2) ...................... 65
A.5. Dampftafel: Sättigungsdampfdruck von Wasserdampf (Kapitel 4.3.2) .... 66
A.6. Stromlaufpläne und Baugruppenliste des Teststandes (Kapitel 4.3.3) ... ....67
A.7. Ermittlung der Kabelquerschnitte für Stromlaufplan (Kapitel 4.3.3) ........73
A.8. Parametrierung der Frequenzumrichter (Kapitel 5.1) ....................... 74
A.9. Ergebnisse der Kalibiermessungen (Kapitel 5.2) ............................75
A.10. Berechnungen zur Dynamik des Schlitzschiebers (Kapitel 6.1) ............. 76
A.11. Konstruktionszeichnungen der Austragshilfe (Kapitel 6.2) .................77</dcterms:tableOfContents>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">xxx</dcterms:abstract>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-09-10</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-01-08</dcterms:modified>
   <dc:type xsi:type="dini:PublType">article</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-704809</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="ddb:noScheme">Test</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="ddb:ISSN">1234-1234</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/nbn:q89e42398tzg</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="ddb:noScheme">Schriftenreihe Test</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://diu.example.com/qucosa:70480/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-704809</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70480');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (136, 17, '2019-01-22 14:31:34.171+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>erste testzeitschrift</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-704926</dc:identifier>
   <dc:identifier>1234-3456</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:subject>Test</dc:subject>
   <dc:date>2018-10-23</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>Periodical</dc:type>
</oai_dc:dc>', false, 'qucosa:70492');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (137, 22, '2019-01-22 14:31:34.171+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">erste testzeitschrift</dc:title>
   <dc:subject xsi:type="xMetaDiss:noScheme">Test</dc:subject>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-10-23</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-01-22</dcterms:modified>
   <dc:type xsi:type="dini:PublType">Periodical</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-704926</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>0</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://fid-move.example.com/qucosa:70492/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-704926</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70492');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (155, 22, '2019-01-23 12:22:24.014+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Strandkorb-Revue</dc:title>
   <dcterms:alternative xml:lang="ger" xsi:type="ddb:talternativeISO639-2">News aus der gestreiften Welt</dcterms:alternative>
   <dc:subject xsi:type="xMetaDiss:noScheme">Zeitschrift</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Meer</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Wellen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Wind</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">000</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">000</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AA 1234</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">BB 1234</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">CC 1234</dc:subject>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Verlag Meeresrauschen</cc:name>
         <cc:place>Warnemünde</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2019-01-17</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2018</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-01-23</dcterms:modified>
   <dc:type xsi:type="dini:PublType">PeriodicalPart</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-705007</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="ddb:ISSN">1234-5678</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa2-704985</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:70500/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-705007</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70500');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (140, 17, '2019-01-22 16:24:58.985+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Der Thesenanschlag, der 1517 die Welt veränderte</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-79449</dc:identifier>
   <dc:identifier>489452108</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>1866-0665</dc:relation>
   <dc:relation>urn:nbn:de:bsz:14-qucosa2-79239</dc:relation>
   <dc:relation>1866-0665</dc:relation>
   <dc:relation>2418048-8</dc:relation>
   <dc:description>Vergangenheit, Gegenwart und Zukunft – stehen 2017 ganz im Zeichen von Martin Luther und seinen Weggefährten. Auf den folgenden Seiten geben wir Ihnen einen kleinen Überblick über ein paar vergangene, gegenwärtig laufende und bald startende Veranstaltungen an unseren sächsischen Bibliotheken.</dc:description>
   <dc:subject>Reformationsjubiläum, 2017, Sachsen, Bibliothek, Veranstaltung Luther, Martin, Reformation, Rezeption</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/020</dc:subject>
   <dc:subject>Reformationsjubiläum 2017; Sachsen; Bibliothek; Veranstaltung</dc:subject>
   <dc:subject>Luther; Martin; Reformation; Rezeption</dc:subject>
   <dc:subject>Luther; Martin Thesen; Jubiläum</dc:subject>
   <dc:publisher>SLUB Dresden</dc:publisher>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2017-04-11</dc:date>
   <dc:source>BIS - Das Magazin der Bibliotheken in Sachsen 10 (1), S. 52-53. ISSN: 1866-0665</dc:source>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>article</dc:type>
</oai_dc:dc>', false, 'qucosa:7944');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (141, 22, '2019-01-22 16:24:58.985+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Der Thesenanschlag, der 1517 die Welt veränderte</dc:title>
   <dc:subject xsi:type="xMetaDiss:noScheme">Reformationsjubiläum</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">2017</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Sachsen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Bibliothek</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Veranstaltung Luther</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Martin</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Reformation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Rezeption</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">020</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">020</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">BW 14793</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">LB 62080</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Reformationsjubiläum 2017</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Sachsen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Bibliothek</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Veranstaltung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Luther</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Martin</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Reformation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Rezeption</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Luther</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Martin Thesen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Jubiläum</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Vergangenheit, Gegenwart und Zukunft – stehen 2017 ganz im Zeichen von Martin Luther und seinen Weggefährten. Auf den folgenden Seiten geben wir Ihnen einen kleinen Überblick über ein paar vergangene, gegenwärtig laufende und bald startende Veranstaltungen an unseren sächsischen Bibliotheken.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>SLUB Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2017-04-11</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-01-22</dcterms:modified>
   <dc:type xsi:type="dini:PublType">article</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-79449</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="ddb:noScheme">BIS - Das Magazin der Bibliotheken in Sachsen 10(2017)1, S. 52-53, ISSN: 1866-0665</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="ddb:ISSN">1866-0665</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="ddb:ISSN">1866-0665</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa2-79239</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:7944/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-79449</ddb:identifier>
   <ddb:identifier ddb:type="Erstkat-ID">489452108</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:7944');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (143, 17, '2019-01-22 16:25:00.941+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Selbst ist die Bibliothek...:Mit Open Source zur interaktiven Standortkarte</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-119005</dc:identifier>
   <dc:identifier>391724053</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa-118975</dc:relation>
   <dc:description>Die Technische Universität Chemnitz setzt seit vielen Jahren auf den Einsatz von Open-Source-Software im gesamten Campus. Da liegt es nahe, dass auch die Universitätsbibliothek Chemnitz auf frei verfügbaren Code zurückgreift. Die Möglichkeiten für Bibliotheken, Anwendungen und Dienstleistungen auf Open Source zu stützen, haben sich in den letzten Jahren deutlich verbessert, da zahlreiche spezielle Softwarelösungen für Bibliotheken entwickelt wurden.
Neben dem Katalog auf VuFind-Basis, dem ERMSystem Coral und weiteren im internen Bereich eingesetzten Speziallösungen wird seit April 2013 auch eine interaktive Standortkarte auf Grundlage von Open-Source-Code in der Universitätsbibliothek Chemnitz eingesetzt.</dc:description>
   <dc:subject>info:eu-repo/classification/ddc/020</dc:subject>
   <dc:subject>Universitätsbibliothek Chemnitz; Standortkarte; Open Source; Bibliothek; Interaktive Medien; Open Source</dc:subject>
   <dc:subject>Universitätsbibliothek Chemnitz, Standortkarte, Open Source, Bibliothek, Interaktive Medien, Open Source</dc:subject>
   <dc:creator>Hoffmann,Tracy</dc:creator>
   <dc:publisher>SLUB Dresden</dc:publisher>
   <dc:date>2013-07-26</dc:date>
   <dc:source>BIS - Das Magazin der Bibliotheken in Sachsen - 6(2013)2, S. 77</dc:source>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>article</dc:type>
</oai_dc:dc>', false, 'qucosa:3445');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (144, 22, '2019-01-22 16:25:00.941+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Selbst ist die Bibliothek...</dc:title>
   <dcterms:alternative xml:lang="ger" xsi:type="ddb:talternativeISO639-2">Mit Open Source zur interaktiven Standortkarte</dcterms:alternative>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Tracy</pc:foreName>
            <pc:surName>Hoffmann</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="dcterms:DDC">020</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">020</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AN 80159</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AN 73000</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Universitätsbibliothek Chemnitz</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Standortkarte</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Open Source</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Bibliothek</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Interaktive Medien</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Open Source</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Universitätsbibliothek Chemnitz</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Standortkarte</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Open Source</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Bibliothek</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Interaktive Medien</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Open Source</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Die Technische Universität Chemnitz setzt seit vielen Jahren auf den Einsatz von Open-Source-Software im gesamten Campus. Da liegt es nahe, dass auch die Universitätsbibliothek Chemnitz auf frei verfügbaren Code zurückgreift. Die Möglichkeiten für Bibliotheken, Anwendungen und Dienstleistungen auf Open Source zu stützen, haben sich in den letzten Jahren deutlich verbessert, da zahlreiche spezielle Softwarelösungen für Bibliotheken entwickelt wurden.
Neben dem Katalog auf VuFind-Basis, dem ERMSystem Coral und weiteren im internen Bereich eingesetzten Speziallösungen wird seit April 2013 auch eine interaktive Standortkarte auf Grundlage von Open-Source-Code in der Universitätsbibliothek Chemnitz eingesetzt.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>SLUB Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2013-07-26</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-01-22</dcterms:modified>
   <dc:type xsi:type="dini:PublType">article</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-119005</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:source xsi:type="ddb:noScheme">BIS - Das Magazin der Bibliotheken in Sachsen - 6(2013)2, S. 77</dc:source>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa-118975</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:3445/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-119005</ddb:identifier>
   <ddb:identifier ddb:type="Erstkat-ID">391724053</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:3445');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (157, 17, '2019-01-23 12:32:24.91+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Strandkorb-Revue:News aus der gestreiften Welt</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-704990</dc:identifier>
   <dc:identifier>1234-5678</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa2-704985</dc:relation>
   <dc:subject>Zeitschrift, Meer, Wellen, Wind</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/000</dc:subject>
   <dc:publisher>Verlag Meeresrauschen</dc:publisher>
   <dc:date>2018</dc:date>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2019-01-17</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>PeriodicalPart</dc:type>
</oai_dc:dc>', false, 'qucosa:70499');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (146, 17, '2019-01-23 11:06:43.388+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Zur Rolle des Stifts in der Digitalen Bibliothek:Wie wir mit der persönlichen Handschrift den „Flow“ finden</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-169544</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>1866-0665</dc:relation>
   <dc:relation>urn:nbn:de:bsz:14-qucosa2-169537</dc:relation>
   <dc:description>Auch das wissenschaftliche Schreiben will gelernt sein. Dabei ist es hilfreich, inmitten der digitalen Welt seine persönliche Handschrift wieder zu entdecken. Denn Schreiben mit Schwung unterstützt den Schreibfluss und erzeugt den beglückenden „Flow“.</dc:description>
   <dc:subject>Sächsische Landesbibliothek - Staats- und Universitätsbibliothek Dresden, Wissenschaftliche Beratung, Kursangebot, Wissenschaftliches Arbeiten, Kreatives Schreiben, Schreibwerkstatt</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/020</dc:subject>
   <dc:subject>Sächsische Landesbibliothek - Staats- und Universitätsbibliothek Dresden; Wissenschaftliche Beratung; Kursangebot; Wissenschaftliches Arbeiten; Kreatives Schreiben; Schreibwerkstatt</dc:subject>
   <dc:creator>Meyer,Julia</dc:creator>
   <dc:publisher>SLUB Dresden</dc:publisher>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2018-01-05</dc:date>
   <dc:source>BIS – Das Magazin der Bibliotheken in Sachsen 10 (3), S. 140-141. ISSN: 1866-0665</dc:source>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>article</dc:type>
</oai_dc:dc>', false, 'qucosa:16954');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (147, 22, '2019-01-23 11:06:43.388+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Zur Rolle des Stifts in der Digitalen Bibliothek</dc:title>
   <dcterms:alternative xml:lang="ger" xsi:type="ddb:talternativeISO639-2">Wie wir mit der persönlichen Handschrift den „Flow“ finden</dcterms:alternative>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Julia</pc:foreName>
            <pc:surName>Meyer</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Sächsische Landesbibliothek - Staats- und Universitätsbibliothek Dresden</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Wissenschaftliche Beratung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Kursangebot</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Wissenschaftliches Arbeiten</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Kreatives Schreiben</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Schreibwerkstatt</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">020</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">020</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AN 80190</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">DM 4200</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">ES 680</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Sächsische Landesbibliothek - Staats- und Universitätsbibliothek Dresden</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Wissenschaftliche Beratung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Kursangebot</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Wissenschaftliches Arbeiten</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Kreatives Schreiben</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Schreibwerkstatt</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Auch das wissenschaftliche Schreiben will gelernt sein. Dabei ist es hilfreich, inmitten der digitalen Welt seine persönliche Handschrift wieder zu entdecken. Denn Schreiben mit Schwung unterstützt den Schreibfluss und erzeugt den beglückenden „Flow“.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>SLUB Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-01-05</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-01-23</dcterms:modified>
   <dc:type xsi:type="dini:PublType">article</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-169544</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="ddb:noScheme">BIS – Das Magazin der Bibliotheken in Sachsen 10(2017)3, S. 140-141, ISSN: 1866-0665</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="ddb:ISSN">1866-0665</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa2-169537</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:16954/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-169544</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:16954');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (150, 17, '2019-01-23 12:21:41.929+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Strandkorb-Revue:News aus der gestreiften Welt</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-704985</dc:identifier>
   <dc:identifier>1234-5678</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:description>Zeitschrift für Autorenbeiträge und Essays zum Thema Wohlfühlen am Meer.</dc:description>
   <dc:subject>Zeitschrift, Meer, Wellen, Wind</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/000</dc:subject>
   <dc:contributor>Sturm,Windfried</dc:contributor>
   <dc:publisher>Verlag Meeresrauschen</dc:publisher>
   <dc:date>2019-01-17</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>Periodical</dc:type>
</oai_dc:dc>', false, 'qucosa:70498');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (151, 22, '2019-01-23 12:21:41.929+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Strandkorb-Revue</dc:title>
   <dcterms:alternative xml:lang="ger" xsi:type="ddb:talternativeISO639-2">News aus der gestreiften Welt</dcterms:alternative>
   <dc:subject xsi:type="xMetaDiss:noScheme">Zeitschrift</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Meer</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Wellen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Wind</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">000</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">000</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AA 1234</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">BB 1234</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">CC 1234</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Zeitschrift für Autorenbeiträge und Essays zum Thema Wohlfühlen am Meer.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Verlag Meeresrauschen</cc:name>
         <cc:place>Warnemünde</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dc:contributor thesis:role="editor" xsi:type="pc:Contributor">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Windfried</pc:foreName>
            <pc:surName>Sturm</pc:surName>
         </pc:name>
      </pc:person>
   </dc:contributor>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2019-01-17</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-01-23</dcterms:modified>
   <dc:type xsi:type="dini:PublType">Periodical</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-704985</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>0</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:70498/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-704985</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70498');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (154, 17, '2019-01-23 12:22:24.014+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Strandkorb-Revue:News aus der gestreiften Welt</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-705007</dc:identifier>
   <dc:identifier>1234-5678</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa2-704985</dc:relation>
   <dc:relation>1234-5678</dc:relation>
   <dc:relation>2-12345678</dc:relation>
   <dc:subject>Zeitschrift, Meer, Wellen, Wind</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/000</dc:subject>
   <dc:publisher>Verlag Meeresrauschen</dc:publisher>
   <dc:date>2018</dc:date>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2019-01-17</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>PeriodicalPart</dc:type>
</oai_dc:dc>', false, 'qucosa:70500');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (158, 22, '2019-01-23 12:32:24.91+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Strandkorb-Revue</dc:title>
   <dcterms:alternative xml:lang="ger" xsi:type="ddb:talternativeISO639-2">News aus der gestreiften Welt</dcterms:alternative>
   <dc:subject xsi:type="xMetaDiss:noScheme">Zeitschrift</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Meer</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Wellen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Wind</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">000</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">000</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AA 1234</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">BB 1234</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">CC 1234</dc:subject>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Verlag Meeresrauschen</cc:name>
         <cc:place>Warnemünde</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2019-01-17</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2018</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-01-23</dcterms:modified>
   <dc:type xsi:type="dini:PublType">PeriodicalPart</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-704990</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa2-704985</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:70499/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-704990</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70499');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (161, 17, '2019-01-29 12:08:49.746+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Hippocampal-Temporopolar Connectivity Contributes to Episodic Simulation During Social Cognition</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-318303</dc:identifier>
   <dc:language>eng</dc:language>
   <dc:relation>10.1038/s41598-018-24557-y</dc:relation>
   <dc:relation>2045-2322</dc:relation>
   <dc:relation>10.1038/s41598-018-24557-y</dc:relation>
   <dc:relation>2045-2322</dc:relation>
   <dc:description>People are better able to empathize with others when they are given information concerning the context driving that person’s experiences. This suggests that people draw on prior memories when empathizing, but the mechanisms underlying this connection remain largely unexplored. The present study investigates how variations in episodic information shape the emotional response towards a movie character. Episodic information is either absent or provided by a written context preceding empathic film clips. It was shown that sad context information increases empathic concern for a movie character. This was tracked by neural activity in the temporal pole (TP) and anterior hippocampus (aHP). Dynamic causal modeling with Bayesian Model Selection has shown that context changes the effective connectivity from left aHP to the right TP. The same crossed-hemispheric coupling was found during rest, when people are left to their own thoughts. We conclude that (i) that the integration of episodic memory also supports the specific case of integrating context into empathic judgments, (ii) the right TP supports emotion processing by integrating episodic memory into empathic inferences, and (iii) lateral integration is a key process for episodic simulation during rest and during task. We propose that a disruption of the mechanism may underlie empathy deficits in clinical conditions, such as autism spectrum disorder.</dc:description>
   <dc:subject>emotional response, empathic film clips, hippocampus, dynamic causal modeling, Bayesian Model Selection, Technische Universität Dresden, Publishing Fund</dc:subject>
   <dc:subject>emotionale Reaktion, empathische Filmclips, Hippocampus, dynamische kausale Modellierung, Bayessche Modellauswahl, Technische Universität Dresden, Publikationsfond</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/520</dc:subject>
   <dc:creator>Pehrs,Corinna</dc:creator>
   <dc:creator>Zaki,Jamil</dc:creator>
   <dc:creator>Taruffi,Liila</dc:creator>
   <dc:creator>Kuchinke,Lars</dc:creator>
   <dc:creator>Koelsch,Stefan</dc:creator>
   <dc:publisher>Macmillan Publishers Limited, part of Springer Nature</dc:publisher>
   <dc:date>2018</dc:date>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2018-09-28</dc:date>
   <dc:source>Scientific Reports 8 (1), S. -. ISSN: 2045-2322</dc:source>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>article</dc:type>
</oai_dc:dc>', false, 'qucosa:31830');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (162, 22, '2019-01-29 12:08:49.746+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="eng" xsi:type="ddb:titleISO639-2">Hippocampal-Temporopolar Connectivity Contributes to Episodic Simulation During Social Cognition</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Corinna</pc:foreName>
            <pc:surName>Pehrs</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Jamil</pc:foreName>
            <pc:surName>Zaki</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Liila</pc:foreName>
            <pc:surName>Taruffi</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Lars</pc:foreName>
            <pc:surName>Kuchinke</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Stefan</pc:foreName>
            <pc:surName>Koelsch</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">emotional response</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">empathic film clips</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">hippocampus</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">dynamic causal modeling</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Bayesian Model Selection</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Technische Universität Dresden</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Publishing Fund</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">emotionale Reaktion</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">empathische Filmclips</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Hippocampus</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">dynamische kausale Modellierung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Bayessche Modellauswahl</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Technische Universität Dresden</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Publikationsfond</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">520</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">520</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">UA 1000</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">People are better able to empathize with others when they are given information concerning the context driving that person’s experiences. This suggests that people draw on prior memories when empathizing, but the mechanisms underlying this connection remain largely unexplored. The present study investigates how variations in episodic information shape the emotional response towards a movie character. Episodic information is either absent or provided by a written context preceding empathic film clips. It was shown that sad context information increases empathic concern for a movie character. This was tracked by neural activity in the temporal pole (TP) and anterior hippocampus (aHP). Dynamic causal modeling with Bayesian Model Selection has shown that context changes the effective connectivity from left aHP to the right TP. The same crossed-hemispheric coupling was found during rest, when people are left to their own thoughts. We conclude that (i) that the integration of episodic memory also supports the specific case of integrating context into empathic judgments, (ii) the right TP supports emotion processing by integrating episodic memory into empathic inferences, and (iii) lateral integration is a key process for episodic simulation during rest and during task. We propose that a disruption of the mechanism may underlie empathy deficits in clinical conditions, such as autism spectrum disorder.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Macmillan Publishers Limited, part of Springer Nature</cc:name>
         <cc:place>London</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-09-28</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2018</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-01-29</dcterms:modified>
   <dc:type xsi:type="dini:PublType">article</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-318303</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">eng</dc:language>
   <dcterms:isPartOf xsi:type="ddb:noScheme">Scientific Reports 8(2018)1, ISSN: 2045-2322</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="ddb:ISSN">2045-2322</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://tud.example.com/qucosa:31830/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-318303</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:31830');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (164, 17, '2019-01-31 09:04:03.09+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Journal of Vietnamese Environment</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-88705</dc:identifier>
   <dc:identifier>2193-6471</dc:identifier>
   <dc:language>eng</dc:language>
   <dc:language>vie</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa-88115</dc:relation>
   <dc:subject>Vietnam, Umwelt, Klimawandel, erneuerbare Energien, gesetzgeberischer Rahmen, sozioökonomischer Aspekt</dc:subject>
   <dc:subject>Vietnam, environment, climate change, renewable energies, legislative framework, socio-economic aspect</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/363</dc:subject>
   <dc:publisher>Technische Universität Dresden</dc:publisher>
   <dc:date>2012-08-06</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>PeriodicalPart</dc:type>
</oai_dc:dc>', false, 'qucosa:25014');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (165, 22, '2019-01-31 09:04:03.09+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="eng" xsi:type="ddb:titleISO639-2">Journal of Vietnamese Environment</dc:title>
   <dc:subject xsi:type="xMetaDiss:noScheme">Vietnam</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Umwelt</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Klimawandel</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">erneuerbare Energien</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">gesetzgeberischer Rahmen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">sozioökonomischer Aspekt</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Vietnam</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">environment</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">climate change</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">renewable energies</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">legislative framework</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">socio-economic aspect</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">363</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">363</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AR 10100</dc:subject>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Technische Universität Dresden</cc:name>
         <cc:place>Pirna</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2012-08-06</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-01-31</dcterms:modified>
   <dc:type xsi:type="dini:PublType">PeriodicalPart</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-88705</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">eng</dc:language>
   <dc:language xsi:type="dcterms:ISO639-2">vie</dc:language>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa-88115</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://tud.example.com/qucosa:25014/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-88705</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:25014');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (168, 17, '2019-01-31 09:52:58.695+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Medienwelten - Zeitschrift für Medienpädagogik:Gouvernementalität</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-195560</dc:identifier>
   <dc:identifier>2197-6481</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>10.13141/zfm.2013-1</dc:relation>
   <dc:relation>urn:nbn:de:bsz:14-qucosa-195462</dc:relation>
   <dc:subject>info:eu-repo/classification/ddc/570</dc:subject>
   <dc:subject>Gouvernementalität</dc:subject>
   <dc:subject>governmentality</dc:subject>
   <dc:publisher>Technische Universität Dresden</dc:publisher>
   <dc:date>2016-02-03</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>PeriodicalPart</dc:type>
</oai_dc:dc>', false, 'qucosa:29202');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (169, 22, '2019-01-31 09:52:58.695+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Medienwelten - Zeitschrift für Medienpädagogik</dc:title>
   <dcterms:alternative xml:lang="ger" xsi:type="ddb:talternativeISO639-2">Gouvernementalität</dcterms:alternative>
   <dc:subject xsi:type="dcterms:DDC">570</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">570</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">DW 4000</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Gouvernementalität</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">governmentality</dc:subject>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Technische Universität Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2016-02-03</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-01-31</dcterms:modified>
   <dc:type xsi:type="dini:PublType">PeriodicalPart</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-195560</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa-195462</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://tud.example.com/qucosa:29202/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-195560</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:29202');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (171, 17, '2019-01-31 12:38:33.351+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Charakterisierung pflanzlicher in vitro Kulturen am Beispiel Sonnenblume</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-216316</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>10.1007/s12268-014-0460-z</dc:relation>
   <dc:description>Wirkstoffgewinnung mittels chemischer Synthese führt oft zu Stereoisomeren, welche aufwendig getrennt werden müssen und manche Moleküle sind nur sehr kostenintensiv oder gar nicht darstellbar. Landwirtschaftliche Gewinnung bedeutet Nachteile wie Schadstoffeinsatz und großer Flächenbedarf. Der Einsatz von pflanzlichen Zell- und Gewebekulturen überwindet die genannten Hürden [1, 2]: mit Methoden der Pflanzenbiotechnologie ist es möglich, pflanzliche Inhaltsstoffe in ihrer natürlichen, bioaktiven Form das ganze Jahr über unabhängig von biotischen/abiotischen Umweltfaktoren bei gleichbleibender Qualität und Quantität zu produzieren [3, 4].
Suspensionskulturen und hairy roots gelten momentan als die in vitro-Kulturtypen mit dem größten biotechnologischen Potential. Erstere sind in Flüssigmedium kultivierte Kalluszellen. Bei Kallus handelt es sich um undifferenzierte Pflanzenzellen, welche tumorartig wachsen und durch Zugabe von Pflanzenhormonen an der Differenzierung gehindert werden. Hairy roots entstehen durch Infektion eines Pflanzenteils mit dem Bodenbakterium Agrobacterium rhizogenes. Die so erhaltene Haarwurzelkultur kann ohne Hormonzusatz vermehrt werden, ihre Morphologie erfordert aber häufig eine Anpassung bestehender Kultivierungsgefäße [2, 5].</dc:description>
   <dc:description>In advance of industrial applications of in vitro plant cell or tissue cultures e.g., as bioactive ingredients for pharmaceuticals, an intense characterization concerning growth and productivity has to be performed. Innovative respiration measurement techniques in shake flask scale were applied to investigate and compare heterotrophic, photomixotrophic and hairy root cultures of sunflower. Furthermore, the qualification of RAMOS for screening of plant in vitro cultures is discussed.</dc:description>
   <dc:subject>info:eu-repo/classification/ddc/570</dc:subject>
   <dc:subject>Sonnenblume, in vitro-Kulturen, Überwachung der Atmungsaktivität</dc:subject>
   <dc:subject>sunflower, in vitro cultures, respiration activity monitoring</dc:subject>
   <dc:creator>Geipel,Katja</dc:creator>
   <dc:creator>Bley,Thomas</dc:creator>
   <dc:creator>Steingroewer,Juliane</dc:creator>
   <dc:publisher>Spektrum</dc:publisher>
   <dc:date>2017-02-22</dc:date>
   <dc:date>2014</dc:date>
   <dc:source>BIOspektrum (2014), 20(4), S. 450-452. ISSN: 1868-6249. DOI: 10.1007/s12268-014-0460-z</dc:source>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>article</dc:type>
</oai_dc:dc>', false, 'qucosa:30072');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (172, 22, '2019-01-31 12:38:33.351+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Charakterisierung pflanzlicher in vitro Kulturen am Beispiel Sonnenblume</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Katja</pc:foreName>
            <pc:surName>Geipel</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Thomas</pc:foreName>
            <pc:surName>Bley</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Juliane</pc:foreName>
            <pc:surName>Steingroewer</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="dcterms:DDC">570</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">570</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">WA 150000</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Sonnenblume</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">in vitro-Kulturen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Überwachung der Atmungsaktivität</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">sunflower</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">in vitro cultures</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">respiration activity monitoring</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Wirkstoffgewinnung mittels chemischer Synthese führt oft zu Stereoisomeren, welche aufwendig getrennt werden müssen und manche Moleküle sind nur sehr kostenintensiv oder gar nicht darstellbar. Landwirtschaftliche Gewinnung bedeutet Nachteile wie Schadstoffeinsatz und großer Flächenbedarf. Der Einsatz von pflanzlichen Zell- und Gewebekulturen überwindet die genannten Hürden [1, 2]: mit Methoden der Pflanzenbiotechnologie ist es möglich, pflanzliche Inhaltsstoffe in ihrer natürlichen, bioaktiven Form das ganze Jahr über unabhängig von biotischen/abiotischen Umweltfaktoren bei gleichbleibender Qualität und Quantität zu produzieren [3, 4].
Suspensionskulturen und hairy roots gelten momentan als die in vitro-Kulturtypen mit dem größten biotechnologischen Potential. Erstere sind in Flüssigmedium kultivierte Kalluszellen. Bei Kallus handelt es sich um undifferenzierte Pflanzenzellen, welche tumorartig wachsen und durch Zugabe von Pflanzenhormonen an der Differenzierung gehindert werden. Hairy roots entstehen durch Infektion eines Pflanzenteils mit dem Bodenbakterium Agrobacterium rhizogenes. Die so erhaltene Haarwurzelkultur kann ohne Hormonzusatz vermehrt werden, ihre Morphologie erfordert aber häufig eine Anpassung bestehender Kultivierungsgefäße [2, 5].</dcterms:abstract>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">In advance of industrial applications of in vitro plant cell or tissue cultures e.g., as bioactive ingredients for pharmaceuticals, an intense characterization concerning growth and productivity has to be performed. Innovative respiration measurement techniques in shake flask scale were applied to investigate and compare heterotrophic, photomixotrophic and hairy root cultures of sunflower. Furthermore, the qualification of RAMOS for screening of plant in vitro cultures is discussed.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Spektrum</cc:name>
         <cc:place>Heidelberg</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2017-02-22</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2014</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-01-31</dcterms:modified>
   <dc:type xsi:type="dini:PublType">article</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-216316</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:source xsi:type="ddb:noScheme">BIOspektrum (2014), 20(4), S. 450-452. ISSN: 1868-6249. DOI: 10.1007/s12268-014-0460-z</dc:source>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://tud.example.com/qucosa:30072/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-216316</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:30072');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (174, 17, '2019-01-31 12:53:11.139+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>BIS - Das Magazin der Bibliotheken in Sachsen</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-169537</dc:identifier>
   <dc:identifier>1866-0665</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa2-704887</dc:relation>
   <dc:relation>1866-0665</dc:relation>
   <dc:relation>2418048-8</dc:relation>
   <dc:description>BIS : Das Magazin der Bibliotheken in Sachsen erscheint dreimal jährlich.</dc:description>
   <dc:subject>Sachsen, Bibliothek, Zeitschrift</dc:subject>
   <dc:subject>Saxony, library, periodical</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/020</dc:subject>
   <dc:subject>Sachsen; Bibliothek; Zeitschrift</dc:subject>
   <dc:publisher>SLUB Dresden</dc:publisher>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2018-01-04</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>PeriodicalPart</dc:type>
</oai_dc:dc>', false, 'qucosa:16953');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (175, 22, '2019-01-31 12:53:11.139+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">BIS - Das Magazin der Bibliotheken in Sachsen</dc:title>
   <dc:subject xsi:type="xMetaDiss:noScheme">Sachsen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Bibliothek</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Zeitschrift</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Saxony</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">library</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">periodical</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">020</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">020</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AN 80190</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Sachsen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Bibliothek</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Zeitschrift</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">BIS : Das Magazin der Bibliotheken in Sachsen erscheint dreimal jährlich.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>SLUB Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-01-04</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-01-31</dcterms:modified>
   <dc:type xsi:type="dini:PublType">PeriodicalPart</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-169537</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="ddb:ISSN">1866-0665</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa2-704887</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:16953/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-169537</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:16953');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (177, 17, '2019-01-31 13:50:28.884+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Strandkorb-Revue:News aus der gestreiften Welt</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-705015</dc:identifier>
   <dc:identifier>1234-5678</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa2-704985</dc:relation>
   <dc:relation>1234-5678</dc:relation>
   <dc:relation>2-12345678</dc:relation>
   <dc:subject>Zeitschrift, Meer, Wellen, Wind</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/000</dc:subject>
   <dc:publisher>Verlag Meeresrauschen</dc:publisher>
   <dc:date>2018</dc:date>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2019-01-17</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>PeriodicalPart</dc:type>
</oai_dc:dc>', false, 'qucosa:70501');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (178, 22, '2019-01-31 13:50:28.884+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Strandkorb-Revue</dc:title>
   <dcterms:alternative xml:lang="ger" xsi:type="ddb:talternativeISO639-2">News aus der gestreiften Welt</dcterms:alternative>
   <dc:subject xsi:type="xMetaDiss:noScheme">Zeitschrift</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Meer</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Wellen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Wind</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">000</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">000</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AA 1234</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">BB 1234</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">CC 1234</dc:subject>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Verlag Meeresrauschen</cc:name>
         <cc:place>Warnemünde</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2019-01-17</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2018</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-01-31</dcterms:modified>
   <dc:type xsi:type="dini:PublType">PeriodicalPart</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-705015</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="ddb:ISSN">1234-5678</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa2-704985</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:70501/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-705015</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70501');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (180, 17, '2019-02-07 09:12:38.382+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>„Thank you for not smoking“ - Zur Gouvernementalität des Rauchens</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-195594</dc:identifier>
   <dc:identifier>456392580</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>10.13141/zfm.2013-1.46</dc:relation>
   <dc:relation>urn:nbn:de:bsz:14-qucosa-195560</dc:relation>
   <dc:description>In der vorliegenden Arbeit wird der Versuch unternommen, den Foucaultschen Gouvernementalitätsansatz für Analytiken im Feld der  Medienpädagogik aufzuschließen. Im Fokus der Darstellung steht dabei der Bereich der Raucherprävention. In einem einführenden Teil wird das Konzept der Gouvernementalität innerhalb der Werkgeschichte Foucaults platziert und systematisch entfaltet. Hieran schließt sich eine Zuspitzung auf die gegenwärtigen neoliberalen Verhältnisse und  die im  Gesundheitsbereich etablierten Rationalitäten an. Dass die Geschichte des Rauchens eine genealogische Dimension der stetig voranschreitenden Problematisierung aufweist, wird entlang der Frage  nach repressiven Machtmechanismen um das Rauchen deutlich gemacht. Mit den so im Verlauf der Darstellung entwickelten Werkzeugen werden abschließend zwei an Jugendliche gerichtete Raucherpräventionsschriften der Bundeszentrale für gesundheitliche Aufklärung systematisch in den Blick genommen, um so die Macht der medialen Inszenierung als Melange von Wahrheit, Subjektivierung und Selbstführung aufzuzeigen.</dc:description>
   <dc:description>In the present work a try is made to make the governmentality of Foucault accessible for analytics in the area of media education. The centre of attention is the (juvenile) smoking prevention. In an introducing part the concept of governmentality is placed and systematically envolved in accordance with the history of Foucaults work. An aggravation of the contemporary neoliberal conditions and the rationalities established in the health-care sector follows. That the ''history of smoking'' features a genealogical dimension of the continously proceeding problematization reveals alongside the question of repressive mechanisms of power about smoking. With the tools developed during the process of this presentation it is conclusively expected to systematically take two smoking prevention documents of the Federal Centre for Health Education into account to illustrate the power of the medial staging as a melange of truth, subjectivization and self-leadership.</dc:description>
   <dc:subject>info:eu-repo/classification/ddc/570</dc:subject>
   <dc:subject>Gouvernementalität, Foucault, Gesundheit, Prävention, Macht, Selbstführung</dc:subject>
   <dc:subject>Gouvernementalität, Foucault, health, prevention, power, self Leadership</dc:subject>
   <dc:creator>Lang,Katarina</dc:creator>
   <dc:publisher>Technische Universität Dresden</dc:publisher>
   <dc:date>2016-02-03</dc:date>
   <dc:source>Medienwelten - Zeitschrift für Medienpädagogik, 2013, Nr. 1, S. 1-135, ISSN: 2197-6481</dc:source>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>article</dc:type>
</oai_dc:dc>', false, 'qucosa:29204');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (181, 22, '2019-02-07 09:12:38.382+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">„Thank you for not smoking“ - Zur Gouvernementalität des Rauchens</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Katarina</pc:foreName>
            <pc:surName>Lang</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="dcterms:DDC">570</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">570</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">DW 4000</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Gouvernementalität</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Foucault</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Gesundheit</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Prävention</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Macht</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Selbstführung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Gouvernementalität</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Foucault</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">health</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">prevention</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">power</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">self Leadership</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">In der vorliegenden Arbeit wird der Versuch unternommen, den Foucaultschen Gouvernementalitätsansatz für Analytiken im Feld der  Medienpädagogik aufzuschließen. Im Fokus der Darstellung steht dabei der Bereich der Raucherprävention. In einem einführenden Teil wird das Konzept der Gouvernementalität innerhalb der Werkgeschichte Foucaults platziert und systematisch entfaltet. Hieran schließt sich eine Zuspitzung auf die gegenwärtigen neoliberalen Verhältnisse und  die im  Gesundheitsbereich etablierten Rationalitäten an. Dass die Geschichte des Rauchens eine genealogische Dimension der stetig voranschreitenden Problematisierung aufweist, wird entlang der Frage  nach repressiven Machtmechanismen um das Rauchen deutlich gemacht. Mit den so im Verlauf der Darstellung entwickelten Werkzeugen werden abschließend zwei an Jugendliche gerichtete Raucherpräventionsschriften der Bundeszentrale für gesundheitliche Aufklärung systematisch in den Blick genommen, um so die Macht der medialen Inszenierung als Melange von Wahrheit, Subjektivierung und Selbstführung aufzuzeigen.</dcterms:abstract>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="eng" xsi:type="ddb:contentISO639-2">In the present work a try is made to make the governmentality of Foucault accessible for analytics in the area of media education. The centre of attention is the (juvenile) smoking prevention. In an introducing part the concept of governmentality is placed and systematically envolved in accordance with the history of Foucaults work. An aggravation of the contemporary neoliberal conditions and the rationalities established in the health-care sector follows. That the ''history of smoking'' features a genealogical dimension of the continously proceeding problematization reveals alongside the question of repressive mechanisms of power about smoking. With the tools developed during the process of this presentation it is conclusively expected to systematically take two smoking prevention documents of the Federal Centre for Health Education into account to illustrate the power of the medial staging as a melange of truth, subjectivization and self-leadership.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Technische Universität Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2016-02-03</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-02-07</dcterms:modified>
   <dc:type xsi:type="dini:PublType">article</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-195594</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:source xsi:type="ddb:noScheme">Medienwelten - Zeitschrift für Medienpädagogik, 2013, Nr. 1, S. 1-135, ISSN: 2197-6481</dc:source>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa-195560</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://tud.example.com/qucosa:29204/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-195594</ddb:identifier>
   <ddb:identifier ddb:type="Erstkat-ID">456392580</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:29204');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (183, 17, '2019-02-08 10:22:29.609+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Winzlinge im Buchregal</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-705021</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>1866-0665</dc:relation>
   <dc:relation>XYZ</dc:relation>
   <dc:description>Sie passen in fast jede Tasche und sind nicht viel größer als ein Smartphone, bergen aber literarische Schätze wie die „Großen“: Miniaturbücher sind „Die Kolibris der Bücherwelt“. Eine Ausstellung an der Hochschule für Technik und Wirtschaft Dresden zeigt die umfangreiche Sammlung des Dresdner Ehepaars Elke und Walter Staufenbiel und stellt die Vielfalt dieser besonderen Buchgattung vor.</dc:description>
   <dc:subject>Hochschule für Technik und Wirtschaft Dresden, Hochschulbibliothek, Ausstellung, Miniaturbuch, Präsentation</dc:subject>
   <dc:creator>Ackermann,Hilde</dc:creator>
   <dc:publisher>SLUB Dresden</dc:publisher>
   <dc:date>2019-02-08</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>article</dc:type>
</oai_dc:dc>', false, 'qucosa:70502');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (184, 22, '2019-02-08 10:22:29.609+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Winzlinge im Buchregal</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Hilde</pc:foreName>
            <pc:surName>Ackermann</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Hochschule für Technik und Wirtschaft Dresden</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Hochschulbibliothek</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Ausstellung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Miniaturbuch</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Präsentation</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Sie passen in fast jede Tasche und sind nicht viel größer als ein Smartphone, bergen aber literarische Schätze wie die „Großen“: Miniaturbücher sind „Die Kolibris der Bücherwelt“. Eine Ausstellung an der Hochschule für Technik und Wirtschaft Dresden zeigt die umfangreiche Sammlung des Dresdner Ehepaars Elke und Walter Staufenbiel und stellt die Vielfalt dieser besonderen Buchgattung vor.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>SLUB Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2019-02-08</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-02-08</dcterms:modified>
   <dc:type xsi:type="dini:PublType">article</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-705021</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="ddb:noScheme">BIS - Das Magazin der Bibliotheken in Sachsen</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="ddb:ISSN">1866-0665</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:70502/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-705021</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70502');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (186, 17, '2019-02-11 09:02:19.309+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Bibliotheksmagazin</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-705039</dc:identifier>
   <dc:identifier>1234-5678</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:subject>Bibliothek, Bücher, Lesen</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/111</dc:subject>
   <dc:publisher>Bibliothek Dresden</dc:publisher>
   <dc:date>2019-02-11</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>Periodical</dc:type>
</oai_dc:dc>', false, 'qucosa:70503');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (187, 22, '2019-02-11 09:02:19.309+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Bibliotheksmagazin</dc:title>
   <dc:subject xsi:type="xMetaDiss:noScheme">Bibliothek</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Bücher</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Lesen</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">111</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">111</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AA 11111</dc:subject>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Bibliothek Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2019-02-11</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-02-11</dcterms:modified>
   <dc:type xsi:type="dini:PublType">Periodical</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-705039</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>0</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:70503/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-705039</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70503');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (189, 17, '2019-02-11 09:54:56.553+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Artikel aus dem Bibliotheksmagazin</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-705048</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa2-705039</dc:relation>
   <dc:relation>1234-5678</dc:relation>
   <dc:relation>8765-4321</dc:relation>
   <dc:relation>2-1234567</dc:relation>
   <dc:relation>35</dc:relation>
   <dc:relation>10.25366/2018.1</dc:relation>
   <dc:subject>Bibliothek, Bücher, Lesen</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/111</dc:subject>
   <dc:creator>Wächter,Gisela</dc:creator>
   <dc:publisher>Bibliothek Dresden</dc:publisher>
   <dc:date>2017</dc:date>
   <dc:type>info:eu-repo/semantics/publishedVersion</dc:type>
   <dc:date>2019-02-11</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>article</dc:type>
</oai_dc:dc>', false, 'qucosa:70504');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (190, 22, '2019-02-11 09:54:56.553+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Artikel aus dem Bibliotheksmagazin</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Gisela</pc:foreName>
            <pc:surName>Wächter</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Bibliothek</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Bücher</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Lesen</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">111</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">111</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AA 11111</dc:subject>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Bibliothek Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2019-02-11</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2017</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-02-11</dcterms:modified>
   <dc:type xsi:type="dini:PublType">article</dc:type>
   <dini:version_driver>publishedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-705048</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="ddb:noScheme">Bibliotheksmagazin</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="ddb:ISSN">1234-5678</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa2-705039</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:70504/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-705048</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70504');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (192, 17, '2019-02-11 10:26:18.765+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Discipline-specific gender knowledge – starting point for organisational changes?</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-705057</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>2214-7829</dc:relation>
   <dc:relation>2764252-5</dc:relation>
   <dc:relation>001</dc:relation>
   <dc:relation>10.1016/j.invent.2017.11.001</dc:relation>
   <dc:description>In consequence of the economic restructuring of universities the issue of equality programme implementation to boost the standing of academic organisations in the competition for economic resources and young scientists is focused increasingly. Therefore the importance of expert gender knowledge as well as academic gender knowledge has intensified in the university context. Having this in mind, the following article asks which gender knowledge (cf. Wetterer, 2008; Wetterer, 2009) affects three different academic disciplines (mechanical engineering, disciplines of linguistic, literature and cultural studies and architecture) and to what extent can this become an impediment or an enabler on the shift towards gender-equitable universities.</dc:description>
   <dc:subject>Hochschulmanagement, New Public Management, Neoliberalismus, Geschlechterwissen, Genderexpertise, Organisationaler Wandel, Gender</dc:subject>
   <dc:subject>Higher Education management, New Public Management, Neoliberalism, Gender Knowledge, Gender expertise, Organizational shift, Gender</dc:subject>
   <dc:creator>Krzywinsky,Nora</dc:creator>
   <dc:publisher>Elsevier</dc:publisher>
   <dc:date>2016</dc:date>
   <dc:type>info:eu-repo/semantics/acceptedVersion</dc:type>
   <dc:date>2019-02-11</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>article</dc:type>
</oai_dc:dc>', false, 'qucosa:70505');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (193, 22, '2019-02-11 10:26:18.765+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="eng" xsi:type="ddb:titleISO639-2">Discipline-specific gender knowledge – starting point for organisational changes?</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Nora</pc:foreName>
            <pc:surName>Krzywinsky</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Hochschulmanagement</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">New Public Management</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Neoliberalismus</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Geschlechterwissen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Genderexpertise</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Organisationaler Wandel</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Gender</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Higher Education management</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">New Public Management</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Neoliberalism</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Gender Knowledge</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Gender expertise</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Organizational shift</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Gender</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">In consequence of the economic restructuring of universities the issue of equality programme implementation to boost the standing of academic organisations in the competition for economic resources and young scientists is focused increasingly. Therefore the importance of expert gender knowledge as well as academic gender knowledge has intensified in the university context. Having this in mind, the following article asks which gender knowledge (cf. Wetterer, 2008; Wetterer, 2009) affects three different academic disciplines (mechanical engineering, disciplines of linguistic, literature and cultural studies and architecture) and to what extent can this become an impediment or an enabler on the shift towards gender-equitable universities.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>Elsevier</cc:name>
         <cc:place>Amsterdam [u.a.]</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2019-02-11</dcterms:issued>
   <dcterms:created xsi:type="dcterms:W3CDTF">2016</dcterms:created>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-02-11</dcterms:modified>
   <dc:type xsi:type="dini:PublType">article</dc:type>
   <dini:version_driver>acceptedVersion</dini:version_driver>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-705057</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="ddb:noScheme">Internet Interventions</dcterms:isPartOf>
   <dcterms:isPartOf xsi:type="ddb:ISSN">2214-7829</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:70505/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-705057</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70505');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (195, 17, '2019-02-20 08:27:01.84+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>BIS - Zeitschriftenüberordnung</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-705068</dc:identifier>
   <dc:identifier>1234-1235</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:subject>Zeitschrift</dc:subject>
   <dc:date>2019-02-20</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>Periodical</dc:type>
</oai_dc:dc>', false, 'qucosa:70506');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (196, 22, '2019-02-20 08:27:01.84+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">BIS - Zeitschriftenüberordnung</dc:title>
   <dc:subject xsi:type="xMetaDiss:noScheme">Zeitschrift</dc:subject>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2019-02-20</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-02-20</dcterms:modified>
   <dc:type xsi:type="dini:PublType">Periodical</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-705068</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>0</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:70506/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-705068</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70506');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (198, 17, '2019-02-20 08:28:25.453+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>BIS - Das Magazin der Bibliotheken in Sachsen</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa-118975</dc:identifier>
   <dc:identifier>1866-0665</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa2-705068</dc:relation>
   <dc:description>BIS : Das Magazin der Bibliotheken in Sachsen erscheint dreimal jährlich.</dc:description>
   <dc:subject>Sachsen, Bibliothek, Zeitschrift</dc:subject>
   <dc:subject>Saxony, library, periodical</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/020</dc:subject>
   <dc:subject>Sachsen; Bibliothek; Zeitschrift</dc:subject>
   <dc:publisher>SLUB Dresden</dc:publisher>
   <dc:date>2013-07-22</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>PeriodicalPart</dc:type>
</oai_dc:dc>', false, 'qucosa:3441');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (199, 22, '2019-02-20 08:28:25.453+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">BIS - Das Magazin der Bibliotheken in Sachsen</dc:title>
   <dc:subject xsi:type="xMetaDiss:noScheme">Sachsen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Bibliothek</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Zeitschrift</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Saxony</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">library</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">periodical</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">020</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">020</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AN 80190</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Sachsen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Bibliothek</dc:subject>
   <dc:subject xsi:type="xMetaDiss:SWD">Zeitschrift</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">BIS : Das Magazin der Bibliotheken in Sachsen erscheint dreimal jährlich.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>SLUB Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2013-07-22</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-02-20</dcterms:modified>
   <dc:type xsi:type="dini:PublType">PeriodicalPart</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa-118975</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa2-705068</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>2</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:3441/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa-118975</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:3441');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (201, 17, '2019-02-21 13:44:28.039+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Schriftenreihe XMDP-Test</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-705071</dc:identifier>
   <dc:identifier>1234-4321</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:subject>Test</dc:subject>
   <dc:subject>DNB</dc:subject>
   <dc:date>2019-02-21</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>Periodical</dc:type>
</oai_dc:dc>', false, 'qucosa:70507');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (202, 22, '2019-02-21 13:44:28.039+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Schriftenreihe XMDP-Test</dc:title>
   <dc:subject xsi:type="xMetaDiss:noScheme">Test</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">DNB</dc:subject>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2019-02-21</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-02-21</dcterms:modified>
   <dc:type xsi:type="dini:PublType">Periodical</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-705071</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>0</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:70507/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-705071</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70507');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (204, 17, '2018-12-11 13:05:08.525+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>SLUB - Anzeiger:Das Magazin der SLUB</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-704896</dc:identifier>
   <dc:identifier>1234-2345</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa2-704887</dc:relation>
   <dc:relation>12341234-5</dc:relation>
   <dc:description>Nachrichtenorgan der SLUB</dc:description>
   <dc:subject>Zeitschrift</dc:subject>
   <dc:publisher>SLUB</dc:publisher>
   <dc:date>2018-10-16</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>PeriodicalPart</dc:type>
</oai_dc:dc>', false, 'qucosa:70489');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (205, 22, '2018-12-11 13:05:08.525+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">SLUB - Anzeiger</dc:title>
   <dcterms:alternative xml:lang="ger" xsi:type="ddb:talternativeISO639-2">Das Magazin der SLUB</dcterms:alternative>
   <dc:subject xsi:type="xMetaDiss:noScheme">Zeitschrift</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Nachrichtenorgan der SLUB</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>SLUB</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-10-16</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2018-12-11</dcterms:modified>
   <dc:type xsi:type="dini:PublType">PeriodicalPart</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-704896</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa2-704887</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:70489/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-704896</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70489');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (207, 17, '2019-02-21 13:46:42.535+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Monographie für XMDP-Test</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-705086</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa2-705071</dc:relation>
   <dc:subject>Testbuch</dc:subject>
   <dc:date>2019-02-21</dc:date>
   <dc:source/>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>book</dc:type>
</oai_dc:dc>', false, 'qucosa:70508');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (208, 22, '2019-02-21 13:46:42.535+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Monographie für XMDP-Test</dc:title>
   <dc:subject xsi:type="xMetaDiss:noScheme">Testbuch</dc:subject>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2019-02-21</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-02-21</dcterms:modified>
   <dc:type xsi:type="dini:PublType">book</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-705086</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa2-705071</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:70508/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-705086</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70508');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (210, 17, '2019-02-22 08:53:09.198+01', '<oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:dc" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:slub="http://slub-dresden.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title>Gemeinschaft macht stark</dc:title>
   <dc:identifier>urn:nbn:de:bsz:14-qucosa2-704878</dc:identifier>
   <dc:identifier>285361678</dc:identifier>
   <dc:language>ger</dc:language>
   <dc:relation>urn:nbn:de:bsz:14-qucosa2-169537</dc:relation>
   <dc:relation>1234589-8</dc:relation>
   <dc:relation>nbn:q89e42398tzg</dc:relation>
   <dc:relation>10.testquelledoi</dc:relation>
   <dc:relation>1234-1234</dc:relation>
   <dc:relation>10.testverweisdoi</dc:relation>
   <dc:relation>10.123567/34235</dc:relation>
   <dc:relation>10.1234567/FDDOI2</dc:relation>
   <dc:description>Ein Bibliotheksland mit großer Geschichte Sachsen ist eine der dichtesten und traditionsreichsten Bibliothekslandschaften in Deutschland. In der neuzeitlichen Geschichte bis 1933 gingen von hier zahlreiche Impulse aus.</dc:description>
   <dc:subject>Sachsen, Bibliotheken, Kooperation, Vernetzung</dc:subject>
   <dc:subject>Saxony, Libraries, Cooperation, Networking</dc:subject>
   <dc:subject>info:eu-repo/classification/ddc/020</dc:subject>
   <dc:creator>Bonte,Achim</dc:creator>
   <dc:publisher>SLUB Dresden</dc:publisher>
   <dc:date>2018-09-10</dc:date>
   <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>
   <dc:type>article</dc:type>
</oai_dc:dc>', false, 'qucosa:70487');
INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, deleted, id_record) VALUES (211, 22, '2019-02-22 08:53:09.198+01', '<xMetaDiss:xMetaDiss xmlns:cc="http://www.d-nb.de/standards/cc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ddb="http://www.d-nb.de/standards/ddb/" xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/" xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:myfunc="urn:de:qucosa:xmetadissplus" xmlns:pc="http://www.d-nb.de/standards/pc/" xmlns:slub="http://slub-dresden.de/" xmlns:subject="http://www.d.nb.de/standards/subject/" xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:urn="http://www.d-nb.de/standards/urn/" xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <dc:title xml:lang="ger" xsi:type="ddb:titleISO639-2">Gemeinschaft macht stark</dc:title>
   <dc:creator xsi:type="pc:MetaPers">
      <pc:person>
         <pc:name type="nameUsedByThePerson">
            <pc:foreName>Achim</pc:foreName>
            <pc:surName>Bonte</pc:surName>
         </pc:name>
      </pc:person>
   </dc:creator>
   <dc:subject xsi:type="xMetaDiss:noScheme">Sachsen</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Bibliotheken</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Kooperation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Vernetzung</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Saxony</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Libraries</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Cooperation</dc:subject>
   <dc:subject xsi:type="xMetaDiss:noScheme">Networking</dc:subject>
   <dc:subject xsi:type="dcterms:DDC">020</dc:subject>
   <dc:subject xsi:type="subject:DDC-SG">020</dc:subject>
   <dc:subject xsi:type="xMetaDiss:RVK">AN 80190</dc:subject>
   <dcterms:abstract ddb:type="subject:noScheme" xml:lang="ger" xsi:type="ddb:contentISO639-2">Ein Bibliotheksland mit großer Geschichte Sachsen ist eine der dichtesten und traditionsreichsten Bibliothekslandschaften in Deutschland. In der neuzeitlichen Geschichte bis 1933 gingen von hier zahlreiche Impulse aus.</dcterms:abstract>
   <dc:publisher xsi:type="cc:Publisher">
      <cc:universityOrInstitution>
         <cc:name>SLUB Dresden</cc:name>
         <cc:place>Dresden</cc:place>
      </cc:universityOrInstitution>
   </dc:publisher>
   <dcterms:issued xsi:type="dcterms:W3CDTF">2018-09-10</dcterms:issued>
   <dcterms:modified xsi:type="dcterms:W3CDTF">2019-02-22</dcterms:modified>
   <dc:type xsi:type="dini:PublType">article</dc:type>
   <dc:identifier xsi:type="urn:nbn">urn:nbn:de:bsz:14-qucosa2-704878</dc:identifier>
   <dcterms:medium/>
   <dc:language xsi:type="dcterms:ISO639-2">ger</dc:language>
   <dcterms:isPartOf xsi:type="dcterms:URI">http://nbn-resolving.de/urn:nbn:de:bsz:14-qucosa2-169537</dcterms:isPartOf>
   <dc:rights xsi:type="ddb:noScheme">info:eu-repo/semantics/openAccess</dc:rights>
   <ddb:fileNumber>1</ddb:fileNumber>
   <ddb:transfer ddb:type="dcterms:URI">http://slub.example.com/qucosa:70487/content.zip</ddb:transfer>
   <ddb:identifier ddb:type="URN">urn:nbn:de:bsz:14-qucosa2-704878</ddb:identifier>
   <ddb:identifier ddb:type="Erstkat-ID">285361678</ddb:identifier>
   <ddb:rights ddb:kind="free"/>
</xMetaDiss:xMetaDiss>', false, 'qucosa:70487');


--
-- TOC entry 2300 (class 0 OID 34501)
-- Dependencies: 273
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (1, '1', '<< Flyway Baseline >>', 'BASELINE', '<< Flyway Baseline >>', NULL, 'postgres', '2019-06-28 12:53:03.160507', 0, true);


--
-- TOC entry 2294 (class 0 OID 32041)
-- Dependencies: 265
-- Data for Name: formats; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO formats (id, mdprefix, schemaurl, namespace, deleted) VALUES (17, 'oai_dc', 'http://www.openarchives.org/OAI/2.0/oai_dc/', 'oai_dc', false);
INSERT INTO formats (id, mdprefix, schemaurl, namespace, deleted) VALUES (22, 'xmetadissplus', 'http://www.d-nb.de/standards/xmetadissplus/', 'xMetaDiss', false);


--
-- TOC entry 2309 (class 0 OID 0)
-- Dependencies: 263
-- Name: oaiprovider; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('oaiprovider', 211, true);


--
-- TOC entry 2295 (class 0 OID 32049)
-- Dependencies: 266
-- Data for Name: records; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (28, 'qucosa:24994', 'qucosa:24994', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (31, 'qucosa:30859', 'qucosa:30859', false, true);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (36, 'qucosa:30725', 'qucosa:30725', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (40, 'qucosa:30751', 'qucosa:30751', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (43, 'qucosa:30738', 'qucosa:30738', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (47, 'qucosa:31145', 'qucosa:31145', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (51, 'qucosa:31834', 'qucosa:31834', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (55, 'qucosa:30805', 'qucosa:30805', false, true);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (59, 'qucosa:31127', 'qucosa:31127', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (62, 'qucosa:31901', 'qucosa:31901', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (65, 'qucosa:30803', 'qucosa:30803', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (68, 'qucosa:31971', 'qucosa:31971', false, true);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (71, 'qucosa:31141', 'qucosa:31141', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (74, 'qucosa:30089', 'qucosa:30089', false, true);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (78, 'qucosa:30413', 'qucosa:30413', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (81, 'qucosa:31974', 'qucosa:31974', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (84, 'qucosa:31905', 'qucosa:31905', false, true);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (88, 'qucosa:70491', 'qucosa:70491', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (91, 'qucosa:70493', 'qucosa:70493', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (94, 'qucosa:70494', 'qucosa:70494', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (97, 'qucosa:70495', 'qucosa:70495', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (101, 'qucosa:70496', 'qucosa:70496', false, true);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (104, 'qucosa:70481', 'qucosa:70481', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (109, 'qucosa:26121', 'qucosa:26121', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (113, 'qucosa:30686', 'qucosa:30686', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (116, 'qucosa:31635', 'qucosa:31635', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (120, 'qucosa:70488', 'qucosa:70488', false, true);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (123, 'qucosa:31900', 'qucosa:31900', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (126, 'qucosa:31011', 'qucosa:31011', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (129, 'qucosa:70479', 'qucosa:70479', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (132, 'qucosa:70480', 'qucosa:70480', false, true);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (135, 'qucosa:70492', 'qucosa:70492', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (138, 'qucosa:7944', 'qucosa:7944', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (142, 'qucosa:3445', 'qucosa:3445', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (145, 'qucosa:16954', 'qucosa:16954', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (148, 'qucosa:70498', 'qucosa:70498', false, true);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (152, 'qucosa:70500', 'qucosa:70500', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (156, 'qucosa:70499', 'qucosa:70499', false, true);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (159, 'qucosa:31830', 'qucosa:31830', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (163, 'qucosa:25014', 'qucosa:25014', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (166, 'qucosa:29202', 'qucosa:29202', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (170, 'qucosa:30072', 'qucosa:30072', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (173, 'qucosa:16953', 'qucosa:16953', false, true);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (176, 'qucosa:70501', 'qucosa:70501', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (179, 'qucosa:29204', 'qucosa:29204', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (182, 'qucosa:70502', 'qucosa:70502', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (185, 'qucosa:70503', 'qucosa:70503', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (188, 'qucosa:70504', 'qucosa:70504', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (191, 'qucosa:70505', 'qucosa:70505', false, true);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (194, 'qucosa:70506', 'qucosa:70506', false, true);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (197, 'qucosa:3441', 'qucosa:3441', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (200, 'qucosa:70507', 'qucosa:70507', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (203, 'qucosa:70489', 'qucosa:70489', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (206, 'qucosa:70508', 'qucosa:70508', false, true);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (209, 'qucosa:70487', 'qucosa:70487', false, false);
INSERT INTO records (id, pid, oaiid, deleted, visible) VALUES (18, 'qucosa:32394', 'qucosa:32394', true, true);


--
-- TOC entry 2298 (class 0 OID 34196)
-- Dependencies: 269
-- Data for Name: resumption_tokens; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:45:14.926+02', 0, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/1', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:45:14.926+02', 9, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/2', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:45:14.926+02', 19, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/3', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:45:14.926+02', 29, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/4', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:45:14.926+02', 39, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/5', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:45:14.926+02', 49, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/6', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:45:39.249+02', 0, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/1', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:45:39.249+02', 9, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/2', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:45:39.249+02', 19, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/3', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:45:39.249+02', 29, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/4', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:45:39.249+02', 39, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/5', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:45:39.249+02', 49, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/6', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:46:09.958+02', 0, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/1', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:46:09.958+02', 9, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/2', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:46:09.958+02', 19, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/3', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:46:09.958+02', 29, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/4', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:46:09.958+02', 39, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/5', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:46:09.958+02', 49, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/6', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:49:51.1+02', 0, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/1', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:49:51.1+02', 9, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/2', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:49:51.1+02', 19, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/3', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:49:51.1+02', 29, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/4', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:49:51.1+02', 39, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/5', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:49:51.1+02', 49, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/6', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:51:32.983+02', 0, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/1', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:51:32.983+02', 9, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/2', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:51:32.983+02', 19, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/3', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:51:32.983+02', 29, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/4', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:51:32.983+02', 39, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/5', 17);
INSERT INTO resumption_tokens (expiration_date, cursor, token_id, format_id) VALUES ('2019-05-02 10:51:32.983+02', 49, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/6', 17);


--
-- TOC entry 2299 (class 0 OID 34201)
-- Dependencies: 270
-- Data for Name: rst_to_identifiers; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (28, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (31, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (36, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (40, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (43, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (47, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (51, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (55, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (59, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (62, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (65, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (68, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (71, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (74, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (78, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (81, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (84, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (88, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (91, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (94, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (97, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (101, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (104, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (109, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (113, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (116, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (120, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (123, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (126, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (129, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (132, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (135, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (138, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (142, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (145, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (148, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (152, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (156, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (159, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (163, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (166, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (170, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (173, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (176, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (179, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (182, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (185, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (188, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (191, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (194, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (197, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (200, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (203, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (206, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (209, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (18, '50f9274dd176d4eb27eb911bc9f073b553b0749a8d19b28b9310d200936a0c9f/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (28, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (31, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (36, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (40, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (43, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (47, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (51, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (55, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (59, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (62, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (65, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (68, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (71, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (74, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (78, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (81, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (84, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (88, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (91, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (94, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (97, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (101, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (104, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (109, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (113, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (116, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (120, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (123, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (126, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (129, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (132, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (135, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (138, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (142, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (145, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (148, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (152, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (156, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (159, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (163, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (166, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (170, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (173, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (176, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (179, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (182, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (185, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (188, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (191, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (194, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (197, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (200, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (203, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (206, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (209, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (18, '2705f1da8753b145e3b00f38a280960277a26f669afa1b5334af6d0ec06153c8/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (28, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (31, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (36, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (40, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (43, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (47, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (51, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (55, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (59, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (62, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (65, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (68, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (71, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (74, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (78, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (81, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (84, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (88, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (91, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (94, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (97, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (101, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (104, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (109, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (113, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (116, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (120, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (123, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (126, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (129, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (132, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (135, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (138, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (142, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (145, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (148, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (152, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (156, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (159, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (163, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (166, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (170, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (173, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (176, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (179, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (182, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (185, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (188, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (191, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (194, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (197, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (200, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (203, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (206, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (209, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (18, '245b66c11af24c095b6febe66b2f0c29788d3bfb0acaff6c9f6b00c2f6885f47/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (28, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (31, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (36, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (40, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (43, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (47, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (51, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (55, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (59, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (62, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (65, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (68, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (71, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (74, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (78, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (81, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (84, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (88, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (91, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (94, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (97, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (101, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (104, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (109, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (113, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (116, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (120, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (123, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (126, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (129, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (132, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (135, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (138, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (142, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (145, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (148, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (152, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (156, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (159, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (163, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (166, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (170, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (173, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (176, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (179, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (182, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (185, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (188, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (191, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (194, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (197, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (200, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (203, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (206, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (209, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (18, '8b28a1ad3352f6eb29570a61c8abbd2911c475577e17a7f4be43dc59c8724f87/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (28, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (31, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (36, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (40, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (43, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (47, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (51, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (55, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (59, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (62, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/1');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (65, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (68, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (71, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (74, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (78, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (81, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (84, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (88, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (91, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (94, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/2');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (97, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (101, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (104, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (109, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (113, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (116, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (120, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (123, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (126, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (129, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/3');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (132, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (135, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (138, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (142, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (145, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (148, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (152, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (156, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (159, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (163, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/4');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (166, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (170, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (173, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (176, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (179, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (182, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (185, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (188, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (191, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (194, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/5');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (197, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (200, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (203, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (206, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (209, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/6');
INSERT INTO rst_to_identifiers (record_id, rst_id) VALUES (18, 'b9937c0de038ad440c23dbd148af417404f88640124ea2c6bc586859add337cd/6');


--
-- TOC entry 2293 (class 0 OID 32030)
-- Dependencies: 264
-- Data for Name: sets; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (19, 'ddc:610', 'Medical sciences Medicine', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (20, 'status-type:publishedVersion', '', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (23, 'status-type:draftVersion', '', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (24, 'status-type:submittedVersion', '', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (25, 'status-type:acceptedVersion', '', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (26, 'status-type:updatedVersion', '', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (32, 'ddc:380', 'Commerce, communications, transport', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (33, 'doc-type:bachelorThesis', 'BachelorThesis', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (38, 'ddc:510', 'Mathematics', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (45, 'ddc:620', 'Engineering and allied operations', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (48, 'doc-type:report', 'Report', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (52, 'doc-type:StudyThesis', 'StudyThesis', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (56, 'doc-type:book', 'Book', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (75, 'doc-type:workingPaper', 'WorkingPaper', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (85, 'ddc:330', 'Economics', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (98, 'doc-type:lecture', 'Lecture', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (105, 'ddc:600', 'Technology (Applied sciences)', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (106, 'doc-type:bookPart', 'BookPart', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (110, 'doc-type:Periodical', 'Periodical', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (117, 'ddc:590', 'Zoological sciences', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (139, 'ddc:020', 'Library and information sciences', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (149, 'ddc:000', 'Generalities, Science', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (153, 'doc-type:PeriodicalPart', 'PeriodicalPart', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (160, 'ddc:520', 'Astronomy and allied sciences', '', false);
INSERT INTO sets (id, setspec, setname, setdescription, deleted) VALUES (167, 'ddc:570', 'Life sciences', '', false);


--
-- TOC entry 2297 (class 0 OID 32081)
-- Dependencies: 268
-- Data for Name: sets_to_records; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO sets_to_records (id_set, id_record) VALUES (19, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 31);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 31);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 36);
INSERT INTO sets_to_records (id_set, id_record) VALUES (38, 36);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 40);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 43);
INSERT INTO sets_to_records (id_set, id_record) VALUES (45, 43);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 47);
INSERT INTO sets_to_records (id_set, id_record) VALUES (48, 47);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 51);
INSERT INTO sets_to_records (id_set, id_record) VALUES (52, 51);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 55);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 55);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 59);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 62);
INSERT INTO sets_to_records (id_set, id_record) VALUES (52, 62);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 65);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 65);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 68);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 71);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 71);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 74);
INSERT INTO sets_to_records (id_set, id_record) VALUES (75, 74);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 78);
INSERT INTO sets_to_records (id_set, id_record) VALUES (45, 78);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 81);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 81);
INSERT INTO sets_to_records (id_set, id_record) VALUES (85, 84);
INSERT INTO sets_to_records (id_set, id_record) VALUES (38, 84);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (98, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (105, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (106, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (45, 109);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 109);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 113);
INSERT INTO sets_to_records (id_set, id_record) VALUES (117, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 120);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 123);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 126);
INSERT INTO sets_to_records (id_set, id_record) VALUES (75, 126);
INSERT INTO sets_to_records (id_set, id_record) VALUES (105, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (105, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 135);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 142);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 148);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 148);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (160, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 163);
INSERT INTO sets_to_records (id_set, id_record) VALUES (167, 166);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 166);
INSERT INTO sets_to_records (id_set, id_record) VALUES (167, 170);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (167, 179);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 185);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 194);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 197);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 197);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 200);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 203);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 206);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 209);
INSERT INTO sets_to_records (id_set, id_record) VALUES (19, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 31);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 31);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 36);
INSERT INTO sets_to_records (id_set, id_record) VALUES (38, 36);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 40);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 43);
INSERT INTO sets_to_records (id_set, id_record) VALUES (45, 43);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 47);
INSERT INTO sets_to_records (id_set, id_record) VALUES (48, 47);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 51);
INSERT INTO sets_to_records (id_set, id_record) VALUES (52, 51);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 55);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 55);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 59);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 62);
INSERT INTO sets_to_records (id_set, id_record) VALUES (52, 62);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 65);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 65);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 68);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 71);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 71);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 74);
INSERT INTO sets_to_records (id_set, id_record) VALUES (75, 74);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 78);
INSERT INTO sets_to_records (id_set, id_record) VALUES (45, 78);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 81);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 81);
INSERT INTO sets_to_records (id_set, id_record) VALUES (85, 84);
INSERT INTO sets_to_records (id_set, id_record) VALUES (38, 84);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (98, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (105, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (106, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (45, 109);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 109);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 113);
INSERT INTO sets_to_records (id_set, id_record) VALUES (117, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 120);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 123);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 126);
INSERT INTO sets_to_records (id_set, id_record) VALUES (75, 126);
INSERT INTO sets_to_records (id_set, id_record) VALUES (105, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (105, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 135);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 142);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 148);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 148);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (160, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 163);
INSERT INTO sets_to_records (id_set, id_record) VALUES (167, 166);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 166);
INSERT INTO sets_to_records (id_set, id_record) VALUES (167, 170);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (167, 179);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 185);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 194);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 197);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 197);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 200);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 203);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 206);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 209);
INSERT INTO sets_to_records (id_set, id_record) VALUES (19, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 31);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 31);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 36);
INSERT INTO sets_to_records (id_set, id_record) VALUES (38, 36);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 40);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 43);
INSERT INTO sets_to_records (id_set, id_record) VALUES (45, 43);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 47);
INSERT INTO sets_to_records (id_set, id_record) VALUES (48, 47);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 51);
INSERT INTO sets_to_records (id_set, id_record) VALUES (52, 51);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 55);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 55);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 59);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 62);
INSERT INTO sets_to_records (id_set, id_record) VALUES (52, 62);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 65);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 65);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 68);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 71);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 71);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 74);
INSERT INTO sets_to_records (id_set, id_record) VALUES (75, 74);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 78);
INSERT INTO sets_to_records (id_set, id_record) VALUES (45, 78);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 81);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 81);
INSERT INTO sets_to_records (id_set, id_record) VALUES (85, 84);
INSERT INTO sets_to_records (id_set, id_record) VALUES (38, 84);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (98, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (105, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (106, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (45, 109);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 109);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 113);
INSERT INTO sets_to_records (id_set, id_record) VALUES (117, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 120);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 123);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 126);
INSERT INTO sets_to_records (id_set, id_record) VALUES (75, 126);
INSERT INTO sets_to_records (id_set, id_record) VALUES (105, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (105, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 135);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 142);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 148);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 148);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (160, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 163);
INSERT INTO sets_to_records (id_set, id_record) VALUES (167, 166);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 166);
INSERT INTO sets_to_records (id_set, id_record) VALUES (167, 170);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (167, 179);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 185);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 194);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 197);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 197);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 200);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 203);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 206);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 209);
INSERT INTO sets_to_records (id_set, id_record) VALUES (19, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 18);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 31);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 31);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 36);
INSERT INTO sets_to_records (id_set, id_record) VALUES (38, 36);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 40);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 43);
INSERT INTO sets_to_records (id_set, id_record) VALUES (45, 43);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 47);
INSERT INTO sets_to_records (id_set, id_record) VALUES (48, 47);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 51);
INSERT INTO sets_to_records (id_set, id_record) VALUES (52, 51);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 55);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 55);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 59);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 62);
INSERT INTO sets_to_records (id_set, id_record) VALUES (52, 62);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 65);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 65);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 68);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 71);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 71);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 74);
INSERT INTO sets_to_records (id_set, id_record) VALUES (75, 74);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 78);
INSERT INTO sets_to_records (id_set, id_record) VALUES (45, 78);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 81);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 81);
INSERT INTO sets_to_records (id_set, id_record) VALUES (85, 84);
INSERT INTO sets_to_records (id_set, id_record) VALUES (38, 84);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 91);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 94);
INSERT INTO sets_to_records (id_set, id_record) VALUES (98, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 97);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 101);
INSERT INTO sets_to_records (id_set, id_record) VALUES (105, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (106, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 104);
INSERT INTO sets_to_records (id_set, id_record) VALUES (45, 109);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 109);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 113);
INSERT INTO sets_to_records (id_set, id_record) VALUES (117, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 116);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 120);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 123);
INSERT INTO sets_to_records (id_set, id_record) VALUES (32, 126);
INSERT INTO sets_to_records (id_set, id_record) VALUES (75, 126);
INSERT INTO sets_to_records (id_set, id_record) VALUES (105, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (33, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 129);
INSERT INTO sets_to_records (id_set, id_record) VALUES (105, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 132);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 135);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 138);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 142);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 145);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 148);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 148);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 152);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 156);
INSERT INTO sets_to_records (id_set, id_record) VALUES (160, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 159);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 163);
INSERT INTO sets_to_records (id_set, id_record) VALUES (167, 166);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 166);
INSERT INTO sets_to_records (id_set, id_record) VALUES (167, 170);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 173);
INSERT INTO sets_to_records (id_set, id_record) VALUES (149, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 176);
INSERT INTO sets_to_records (id_set, id_record) VALUES (167, 179);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 185);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 188);
INSERT INTO sets_to_records (id_set, id_record) VALUES (25, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (23, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (24, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (20, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (26, 191);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 194);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 197);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 197);
INSERT INTO sets_to_records (id_set, id_record) VALUES (110, 200);
INSERT INTO sets_to_records (id_set, id_record) VALUES (153, 203);
INSERT INTO sets_to_records (id_set, id_record) VALUES (56, 206);
INSERT INTO sets_to_records (id_set, id_record) VALUES (139, 209);


--
-- TOC entry 2163 (class 2606 OID 32070)
-- Name: dissemination_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY disseminations
    ADD CONSTRAINT dissemination_pkey PRIMARY KEY (id);


--
-- TOC entry 2167 (class 2606 OID 34509)
-- Name: flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- TOC entry 2153 (class 2606 OID 32046)
-- Name: formats_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY formats
    ADD CONSTRAINT formats_pkey PRIMARY KEY (id);


--
-- TOC entry 2155 (class 2606 OID 32048)
-- Name: mdprefix_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY formats
    ADD CONSTRAINT mdprefix_unique UNIQUE (mdprefix);


--
-- TOC entry 2165 (class 2606 OID 34299)
-- Name: pk_token_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY resumption_tokens
    ADD CONSTRAINT pk_token_id PRIMARY KEY (token_id);


--
-- TOC entry 2157 (class 2606 OID 32057)
-- Name: record_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY records
    ADD CONSTRAINT record_pkey PRIMARY KEY (id);


--
-- TOC entry 2159 (class 2606 OID 32061)
-- Name: record_oaiid_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY records
    ADD CONSTRAINT record_oaiid_unique UNIQUE (oaiid);


--
-- TOC entry 2161 (class 2606 OID 32059)
-- Name: record_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY records
    ADD CONSTRAINT record_unique UNIQUE (pid);


--
-- TOC entry 2149 (class 2606 OID 32038)
-- Name: sets_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sets
    ADD CONSTRAINT sets_pkey PRIMARY KEY (id);


--
-- TOC entry 2151 (class 2606 OID 32040)
-- Name: setspec_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sets
    ADD CONSTRAINT setspec_unique UNIQUE (setspec);


--
-- TOC entry 2168 (class 1259 OID 34510)
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX flyway_schema_history_s_idx ON flyway_schema_history USING btree (success);


--
-- TOC entry 2170 (class 2606 OID 34486)
-- Name: dissemination_format_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY disseminations
    ADD CONSTRAINT dissemination_format_fkey FOREIGN KEY (id_format) REFERENCES formats(id) ON DELETE CASCADE;


--
-- TOC entry 2169 (class 2606 OID 32076)
-- Name: dissemination_record_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY disseminations
    ADD CONSTRAINT dissemination_record_fkey FOREIGN KEY (id_record) REFERENCES records(oaiid) ON DELETE CASCADE;


--
-- TOC entry 2173 (class 2606 OID 34491)
-- Name: fk_id_format; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY resumption_tokens
    ADD CONSTRAINT fk_id_format FOREIGN KEY (format_id) REFERENCES formats(id) ON DELETE CASCADE;


--
-- TOC entry 2175 (class 2606 OID 34496)
-- Name: fk_record_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rst_to_identifiers
    ADD CONSTRAINT fk_record_id FOREIGN KEY (record_id) REFERENCES records(id) ON DELETE CASCADE;


--
-- TOC entry 2174 (class 2606 OID 34300)
-- Name: fk_rst_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY rst_to_identifiers
    ADD CONSTRAINT fk_rst_id FOREIGN KEY (rst_id) REFERENCES resumption_tokens(token_id) ON DELETE CASCADE;


--
-- TOC entry 2171 (class 2606 OID 32084)
-- Name: str_record_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sets_to_records
    ADD CONSTRAINT str_record_fkey FOREIGN KEY (id_record) REFERENCES records(id) ON DELETE CASCADE;


--
-- TOC entry 2172 (class 2606 OID 32089)
-- Name: str_set_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sets_to_records
    ADD CONSTRAINT str_set_fkey FOREIGN KEY (id_set) REFERENCES sets(id) ON DELETE CASCADE;


--
-- TOC entry 2307 (class 0 OID 0)
-- Dependencies: 89
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2019-07-01 11:04:31 CEST

--
-- PostgreSQL database dump complete
--

