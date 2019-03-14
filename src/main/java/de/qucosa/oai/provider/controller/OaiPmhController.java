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
import de.qucosa.oai.provider.api.builders.oaipmh.OaiPmhFactory;
import de.qucosa.oai.provider.api.utils.DocumentXmlUtils;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.persistence.model.ResumptionToken;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.FormatService;
import de.qucosa.oai.provider.services.RecordService;
import de.qucosa.oai.provider.services.ResumptionTokenService;
import de.qucosa.oai.provider.services.SetService;
import de.qucosa.oai.provider.services.SetsToRecordService;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequestMapping("/oai")
@RestController
@Scope("session")
public class OaiPmhController {
    private Logger logger = LoggerFactory.getLogger(OaiPmhController.class);

    @Value("${records.pro.page}")
    private int recordsProPage;

    @Value("${expiries.hours}")
    private int expiriesHours;

    private int cursor = 0;

    private RecordService recordService;

    private FormatService formatService;

    private SetService setService;

    private DisseminationService disseminationService;

    private SetsToRecordService setsToRecordService;

    private ResumptionTokenService<ResumptionToken> resumptionTokenService;

    @Autowired
    public OaiPmhController(RecordService recordService, FormatService formatService, SetService setService,
                            DisseminationService disseminationService, SetsToRecordService setsToRecordService,
                            ResumptionTokenService<ResumptionToken> resumptionTokenService) {
        this.recordService = recordService;
        this.formatService = formatService;
        this.setService = setService;
        this.disseminationService = disseminationService;
        this.setsToRecordService = setsToRecordService;
        this.resumptionTokenService = resumptionTokenService;
    }

    @GetMapping(value = {"{verb}", "{verb}/{metadataPrefix}", "{verb}/{metadataPrefix}/{from}",
            "{verb}/{metadataPrefix}/{from}/{until}", "{verb}/{metadataPrefix}/{resumptionToken}",
            "{verb}/{metadataPrefix}/{from}/{resumptionToken}",
            "{verb}/{metadataPrefix}/{from}/{until}/{resumptionToken}"},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity findAll(HttpSession session, @PathVariable String verb,
                                  @PathVariable(value = "metadataPrefix", required = false) String metadataPrefix,
                                  @PathVariable(value = "from", required = false) String from,
                                  @PathVariable(value = "until", required = false) String until,
                                  @PathParam(value = "resumptionToken") String resumptionToken) throws IOException {
        Format format = findFormat(metadataPrefix);

        if (format == null) {
            return new ErrorDetails(this.getClass().getName(), "find", "GET:find",
                    HttpStatus.NOT_FOUND, "Cannot found format " + metadataPrefix + ".", null)
                    .response();
        }

        Collection<Record> records = null;

        try {
            records = recordService.findAll();

            if (session.getAttribute("resumptionToken") == null || session.getAttribute("resumptionToken").toString().isEmpty()) {
                createResumptionToken(session);

                try {
                    saveResumptionTokenAndPidsPersistent(session, records);
                } catch (SaveFailed saveFailed) {
                    saveFailed.printStackTrace();
                }
            }
        } catch (NotFound notFound) {
            return new ErrorDetails(this.getClass().getName(), "findAll", "GET:findAll",
                    HttpStatus.NOT_FOUND, "Cannot found records.", notFound)
                    .response();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        OaiPmhFactory oaiPmhFactory = new OaiPmhFactory(getClass().getResourceAsStream("/templates/oai_pmh.xml"));
        Document oaiPmhList = null;

        try {
            oaiPmhList = oaiPmhFactory.createList(verb, format, records, disseminationService, setService, setsToRecordService);
        } catch (IOException | NotFound e) {
            e.printStackTrace();
        }

        return new ResponseEntity(DocumentXmlUtils.resultXml(oaiPmhList), HttpStatus.OK);
    }

    private Format findFormat(String metadataPrefix) {
        Collection<Format> formats;
        Format format = null;

        try {
            formats = formatService.find("mdprefix", metadataPrefix);

            if (!formats.isEmpty()) {
                format = formats.iterator().next();
            }
        } catch (NotFound notFound) {
            logger.error("Format " + metadataPrefix + " not found!");
        }

        return format;
    }

    private void createResumptionToken(HttpSession session) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(UUID.randomUUID().toString().getBytes("UTF-8"));
        session.setAttribute("resumptionToken", HexUtils.toHexString(digest.digest()));
    }

    private void saveResumptionTokenAndPidsPersistent(HttpSession session, Collection<Record> records) throws SaveFailed {
        List<Record> recordList = new ArrayList<>();
        recordList.addAll(records);

        if (recordList.size() > recordsProPage) {
            int pageSum = (int) Math.ceil(((double)recordList.size() / (double)recordsProPage));
            int rcCnt = 0;
            Timestamp timestamp = new Timestamp(new Date().getTime());
            int cursor = 0;

            for (int i = 1; i <= pageSum; i++) {
                ResumptionToken resumptionToken = new ResumptionToken();
                resumptionToken.setTokenId(session.getAttribute("resumptionToken") + "/" + (rcCnt + 1));
                resumptionToken.setExpirationDate(timestamp);

                if (rcCnt > 0) {
                    cursor = ((rcCnt * recordsProPage) - 1);
                }

                resumptionToken.setCursor(new Long(cursor));
                rcCnt++;
                resumptionToken = resumptionTokenService.saveAndSetIdentifier(resumptionToken);
                int end = 0;

                if (cursor == 0) {
                    end = recordsProPage;
                } else {
                    cursor++;
                    end = (cursor + recordsProPage);
                }

                for (int j = cursor; j < end; j++) {

                    if (j < recordList.size()) {
                        Record record = recordList.get(j);
                        System.out.println(resumptionToken.getTokenId() + " - " + j + " / " + record.getUid());
                    }
                }
            }
        }
    }
}
