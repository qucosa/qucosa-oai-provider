/*
 * *
 *     ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *     ~
 *     ~ Licensed under the Apache License, Version 2.0 (the "License");
 *     ~ you may not use this file except in compliance with the License.
 *     ~ You may obtain a copy of the License at
 *     ~
 *     ~     http://www.apache.org/licenses/LICENSE-2.0
 *     ~
 *     ~ Unless required by applicable law or agreed to in writing, software
 *     ~ distributed under the License is distributed on an "AS IS" BASIS,
 *     ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     ~ See the License for the specific language governing permissions and
 *     ~ limitations under the License.
 *
 */

package de.qucosa.oai.provider.controller;

import de.qucosa.oai.provider.ErrorDetails;
import de.qucosa.oai.provider.api.builders.oaipmh.OaiPmhDataBuilderFactory;
import de.qucosa.oai.provider.api.utils.DocumentXmlUtils;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.persistence.model.ResumptionToken;
import de.qucosa.oai.provider.persistence.model.RstToIdentifiers;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.services.ResumptionTokenService;
import de.qucosa.oai.provider.services.RstToIdentifiersService;
import de.qucosa.oai.provider.services.views.OaiPmhListByTokenService;
import de.qucosa.oai.provider.services.views.OaiPmhListService;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
@RequestMapping("/oai")
@RestController
public class OaiPmhController {
    private Logger logger = LoggerFactory.getLogger(OaiPmhController.class);

    @Autowired
    private Environment environment;

    @Value("${records.pro.page}")
    private int recordsProPage;

    @Value("${expiries.hours}")
    private int expiriesHours;

    @Value(("${app.url}"))
    private String appUrl;

    @Value("${server.port}")
    private int serverPort;

    @Value("#{'${oai.pmh.verbs}'.split(',')}")
    private List<String> verbs;

    private int cursor = 0;

    private Format format;

    private ResumptionTokenService resumptionTokenService;

    private RstToIdentifiersService rstToIdentifiersService;

    private OaiPmhListByTokenService oaiPmhListByTokenService;

    private OaiPmhListService oaiPmhListService;

    private RestTemplate restTemplate;

    @Autowired
    public OaiPmhController(RestTemplate restTemplate,
                            ResumptionTokenService resumptionTokenService,
                            RstToIdentifiersService rstToIdentifiersService,
                            OaiPmhListByTokenService oaiPmhListByTokenService,
                            OaiPmhListService oaiPmhListService) {
        this.restTemplate = restTemplate;
        this.resumptionTokenService = resumptionTokenService;
        this.rstToIdentifiersService = rstToIdentifiersService;
        this.oaiPmhListByTokenService = oaiPmhListByTokenService;
        this.oaiPmhListService = oaiPmhListService;
    }

    @SuppressWarnings("ConstantConditions")
    @GetMapping(value = {"{verb}", "{verb}/{metadataPrefix}",
            "{verb}/{metadataPrefix}/{from}",
            "{verb}/{metadataPrefix}/{from}/{until}"},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity findAll(@PathVariable String verb,
                                  @PathVariable(value = "metadataPrefix", required = false) String metadataPrefix,
                                  @PathVariable(value = "from", required = false) String from,
                                  @PathVariable(value = "until", required = false) String until,
                                  @RequestParam(value = "identyfier", required = false) String identyfier,
                                  @RequestParam(value = "resumptionToken", required = false) String resumptionToken) throws IOException {

        if (!verbs.contains(verb)) {
            return new ErrorDetails(this.getClass().getName(), "findAll", "GET:findAll",
                    HttpStatus.BAD_REQUEST, "The verb (" + verb + ") is does not exists in OAI protocol.", null)
                    .response();
        }

        ResponseEntity output = null;
        ResumptionToken resumptionTokenObj = null;

        OaiPmhDataBuilderFactory oaiPmhDataBuilderFactory = new OaiPmhDataBuilderFactory(
                DocumentXmlUtils.document(getClass().getResourceAsStream("/templates/oai_pmh.xml"), false)
        ).setVerb(verb).setMdprefix(metadataPrefix).setRecordsProPage(recordsProPage);

        if (verb.equals("ListIdentifiers") || verb.equals("ListRecords")) {
            setBuilderFactoryValues(oaiPmhDataBuilderFactory, resumptionToken, metadataPrefix, from, until);

            try {

                if (oaiPmhDataBuilderFactory.getRecords().size() > recordsProPage) {
                    output = getOaiPmhListByToken(oaiPmhDataBuilderFactory, resumptionToken);
                } else {
                    output = getOaiPmhList(oaiPmhDataBuilderFactory, metadataPrefix, from, until);
                }
            } catch (Exception e) {
                return errorDetails(e, "findAll", "GET:findAll", HttpStatus.NOT_FOUND);
            }
        }

        if (verb.equals("ListSets")) {
            try {
                output = getListSets(oaiPmhDataBuilderFactory);
            } catch (Exception e) {
                return errorDetails(e, "findAll", "GET:findAll", HttpStatus.NOT_FOUND);
            }
        }

        if (verb.equals("ListMetadataFormats")) {
            try {
                output = getListMetadataFormats(oaiPmhDataBuilderFactory);
            } catch (Exception e) {
                return errorDetails(e, "findAll", "GET:findAll", HttpStatus.NOT_FOUND);
            }
        }

        if (verb.equals("GetRecord")) {
            try {
                output = getRecord(metadataPrefix, identyfier, oaiPmhDataBuilderFactory);
            } catch (Exception e) {
                return errorDetails(e, "findAll", "GET:findAll", HttpStatus.NOT_FOUND);
            }
        }

        if (verb.equals("Identify")) {
            try {
                output = identify(oaiPmhDataBuilderFactory);
            } catch (Exception e) {
                return errorDetails(e, "findAll", "GET:findAll", HttpStatus.NOT_FOUND);
            }
        }

        return output;
    }

    private void setBuilderFactoryValues(OaiPmhDataBuilderFactory oaiPmhDataBuilderFactory, String resumptionToken, String metadataPrefix, String from, String until) {

        if (resumptionToken == null || resumptionToken.isEmpty()) {
            format = restTemplate.getForObject(
                    UriComponentsBuilder.fromUriString(appUrl + ":" + serverPort + "/formats/format")
                            .queryParam("mdprefix", metadataPrefix)
                            .toUriString(),
                    Format.class);
        }

        if (metadataPrefix != null && from != null && until != null) {
            oaiPmhDataBuilderFactory.setRecords(restTemplate.exchange(
                    UriComponentsBuilder
                            .fromUriString(appUrl + ":" + serverPort + "/records")
                            .queryParam("metadataPrefix", format.getFormatId())
                            .queryParam("from", from)
                            .queryParam("until", until)
                            .toUriString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Collection<Record>>(){}).getBody());
        } else if (metadataPrefix != null && from != null && until == null) {
            oaiPmhDataBuilderFactory.setRecords(restTemplate.exchange(
                    UriComponentsBuilder
                            .fromUriString(appUrl + ":" + serverPort + "/records")
                            .queryParam("metadataPrefix", format.getFormatId())
                            .queryParam("from", from)
                            .toUriString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Collection<Record>>(){}).getBody());
        } else {
            oaiPmhDataBuilderFactory.setRecords(restTemplate.exchange(appUrl + ":" + serverPort + "/records",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Collection<Record>>(){}).getBody());
        }

        oaiPmhDataBuilderFactory.setSets(
                restTemplate.exchange(appUrl + ":" + serverPort + "/sets",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Collection<Set>>(){}).getBody());

        oaiPmhDataBuilderFactory.setFormats(
                restTemplate.exchange(appUrl + ":" + serverPort + "/formats",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Collection<Format>>() {}).getBody());
    }

    private ResponseEntity getOaiPmhListByToken(OaiPmhDataBuilderFactory oaiPmhDataBuilderFactory,
                                                String resumptionToken) throws Exception {
        ResumptionToken resumptionTokenObj;
        try {
            resumptionTokenObj = (resumptionToken == null || resumptionToken.isEmpty())
                    ? saveResumptionTokenAndPidsPersistent(createResumptionToken(), oaiPmhDataBuilderFactory.getRecords())
                    : resumptionTokenService.findById(resumptionToken);

            if (resumptionToken != null && !resumptionToken.isEmpty()) {
                format = restTemplate.getForObject(
                        UriComponentsBuilder.fromUriString(appUrl + ":" + serverPort + "/formats/format")
                                .queryParam("formatId", resumptionTokenObj.getFormatId())
                                .toUriString(),
                        Format.class);
            }

            oaiPmhDataBuilderFactory.setResumptionToken(resumptionTokenObj);
            oaiPmhDataBuilderFactory.setFormat(format);
        } catch (SaveFailed saveFailed) {
            return new ErrorDetails(this.getClass().getName(), "findAll", "GET:findAll",
                    HttpStatus.NOT_ACCEPTABLE, saveFailed.getMessage(), saveFailed)
                    .response();
        } catch (NotFound | NoSuchAlgorithmException | UnsupportedEncodingException notFound) {
            return new ErrorDetails(this.getClass().getName(), "findAll", "GET:findAll",
                    HttpStatus.NOT_FOUND, notFound.getMessage(), notFound)
                    .response();
        }

        try {
            oaiPmhDataBuilderFactory.setOaiPmhListByToken(
                    oaiPmhListByTokenService.findRowsByMultipleValues(
                            "rst_id = %s AND format = %s", resumptionTokenObj.getTokenId(),
                            String.valueOf(resumptionTokenObj.getFormatId()))
            );
        } catch (NotFound notFound) {
            return new ErrorDetails(this.getClass().getName(), "findAll", "GET:findAll",
                    HttpStatus.NOT_FOUND, notFound.getMessage(), notFound)
                    .response();
        }

        return new ResponseEntity<>(DocumentXmlUtils.resultXml(oaiPmhDataBuilderFactory.oaiPmhData()), HttpStatus.OK);
    }

    private ResponseEntity getOaiPmhList(OaiPmhDataBuilderFactory oaiPmhDataBuilderFactory, String metadataPrefix,
                                         String from, String until) throws Exception {
        try {

            if (format == null) {
                format = restTemplate.getForObject(
                        UriComponentsBuilder.fromUriString(appUrl + ":" + serverPort + "/formats/format")
                                .queryParam("mdprefix", metadataPrefix)
                                .toUriString(),
                        Format.class);
            }

            oaiPmhDataBuilderFactory.setFormat(format);

            if (metadataPrefix != null && from != null && until != null) {
                oaiPmhDataBuilderFactory.setOaiPmhList(
                        oaiPmhListService.findByMultipleValues("", String.valueOf(format.getFormatId()), from, until)
                );
            } else if (metadataPrefix != null && from != null && until == null) {
                oaiPmhDataBuilderFactory.setOaiPmhList(
                        oaiPmhListService.findByMultipleValues("BETWEEN ? AND NOW()", String.valueOf(format.getFormatId()), from)
                );
            } else {
                oaiPmhDataBuilderFactory.setOaiPmhList(
                        oaiPmhListService.findByPropertyAndValue("format_id", String.valueOf(format.getFormatId()))
                );
            }
        } catch (NotFound notFound) {
            return new ErrorDetails(this.getClass().getName(), "findAll", "GET:findAll",
                    HttpStatus.NOT_FOUND, notFound.getMessage(), notFound)
                    .response();
        }

        return new ResponseEntity<>(DocumentXmlUtils.resultXml(oaiPmhDataBuilderFactory.oaiPmhData()), HttpStatus.OK);
    }

    private ResponseEntity errorDetails(Exception e, String method, String requestMethodAndApth, HttpStatus status) {
        return new ErrorDetails(this.getClass().getName(), method, requestMethodAndApth, status, e.getMessage(), e)
                .response();
    }

    private ResponseEntity getListSets(OaiPmhDataBuilderFactory oaiPmhDataBuilderFactory) throws Exception {
        Collection<Set> sets = restTemplate.exchange(appUrl + ":" + serverPort + "/sets",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Set>>() {}).getBody();
        oaiPmhDataBuilderFactory.setSets(sets);

        return new ResponseEntity<>(DocumentXmlUtils.resultXml(oaiPmhDataBuilderFactory.oaiPmhData()), HttpStatus.OK);
    }

    private ResponseEntity getListMetadataFormats(OaiPmhDataBuilderFactory oaiPmhDataBuilderFactory) throws Exception {
        oaiPmhDataBuilderFactory.setFormats(
                restTemplate.exchange(appUrl + ":" + serverPort + "/formats",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Collection<Format>>() {}).getBody());
        return new ResponseEntity<>(DocumentXmlUtils.resultXml(oaiPmhDataBuilderFactory.oaiPmhData()), HttpStatus.OK);
    }

    private ResponseEntity identify(OaiPmhDataBuilderFactory oaiPmhDataBuilderFactory) throws Exception {
        oaiPmhDataBuilderFactory.setEnvironment(environment);
        oaiPmhDataBuilderFactory.setDisseminations(
                restTemplate.exchange(appUrl + ":" + serverPort + "/disseminations/earliest", HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Collection<Dissemination>>() {}).getBody());
        return new ResponseEntity<>(DocumentXmlUtils.resultXml(oaiPmhDataBuilderFactory.oaiPmhData()), HttpStatus.OK);
    }

    private ResponseEntity getRecord(String metadataPrefix, String identyfier, OaiPmhDataBuilderFactory oaiPmhDataBuilderFactory) throws Exception {

        if (identyfier == null) {
            return new ErrorDetails(this.getClass().getName(), "findAll", "GET:findAll",
                    HttpStatus.NOT_FOUND, "Identyfier parameter failed.", null)
                    .response();
        }

        oaiPmhDataBuilderFactory.setIdentifier(identyfier);

        try {
            format = restTemplate.getForObject(
                    UriComponentsBuilder.fromUriString(appUrl + ":" + serverPort + "/formats/format")
                            .queryParam("mdprefix", metadataPrefix)
                            .toUriString(),
                    Format.class);

            oaiPmhDataBuilderFactory.setFormat(format);
            oaiPmhDataBuilderFactory.setOaiPmhList(
                    oaiPmhListService.findByPropertyAndValue("format_id", String.valueOf(format.getFormatId()))
            );
        } catch (NotFound notFound) {
            return new ErrorDetails(this.getClass().getName(), "findAll", "GET:findAll",
                    HttpStatus.NOT_FOUND, notFound.getMessage(), notFound)
                    .response();
        }

        return new ResponseEntity<>(DocumentXmlUtils.resultXml(oaiPmhDataBuilderFactory.oaiPmhData()), HttpStatus.OK);
    }

    private String createResumptionToken() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        return HexUtils.toHexString(digest.digest());
    }

    private ResumptionToken saveResumptionTokenAndPidsPersistent(String token, Collection<Record> records) throws SaveFailed, NotFound {
        List<Record> recordList = new ArrayList<>(records);
        Collection<RstToIdentifiers> rstToIdentifiersCollection = new ArrayList<>();

        if (recordList.size() > recordsProPage) {
            int pageSum = (int) Math.ceil(((double)recordList.size() / (double)recordsProPage));
            int rcCnt = 0;
            Timestamp timestamp = new Timestamp(new Date().getTime());
            int cursor = 0;

            for (int i = 1; i <= pageSum; i++) {
                ResumptionToken resumptionToken = new ResumptionToken();
                resumptionToken.setTokenId(token + "/" + (rcCnt + 1));
                resumptionToken.setExpirationDate(timestamp);
                resumptionToken.setFormatId(format.getFormatId());

                if (rcCnt > 0) {
                    cursor = ((rcCnt * recordsProPage) - 1);
                }

                resumptionToken.setCursor((long) cursor);
                rcCnt++;
                resumptionToken = resumptionTokenService.saveAndSetIdentifier(resumptionToken);
                int end;

                if (cursor == 0) {
                    end = recordsProPage;
                } else {
                    cursor++;
                    end = (cursor + recordsProPage);
                }

                for (int j = cursor; j < end; j++) {

                    if (j < recordList.size()) {
                        RstToIdentifiers rstToIdentifiers = new RstToIdentifiers();
                        rstToIdentifiers.setRecordId(recordList.get(j).getRecordId());
                        rstToIdentifiers.setRstId(resumptionToken.getTokenId());
                        rstToIdentifiersCollection.add(rstToIdentifiers);
                    }
                }
            }

            rstToIdentifiersService.saveAndSetIdentifier(rstToIdentifiersCollection);

        }

        return resumptionTokenService.findById(token + "/1");
    }
}
