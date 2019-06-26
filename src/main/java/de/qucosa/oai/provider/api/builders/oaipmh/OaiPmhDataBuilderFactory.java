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

package de.qucosa.oai.provider.api.builders.oaipmh;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.persistence.model.ResumptionToken;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhList;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhListByToken;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class OaiPmhDataBuilderFactory {
    private Document oaiPmhTemplate;

    private String verb;

    private String mdprefix;

    private String identifier;

    private int recordsProPage;

    private Collection<Record> records;

    private Collection<Set> sets;

    private Collection<Format> formats;

    private Format format;

    private Collection<OaiPmhListByToken> oaiPmhListByToken;

    private Collection<OaiPmhList> oaiPmhList;

    private Collection<Dissemination> disseminations;

    private ResumptionToken resumptionToken;

    private ObjectMapper om = new ObjectMapper();

    private OaiPmhDataBuilder oaiPmhDataBuilder;

    private Environment environment;

    public OaiPmhDataBuilderFactory(Document oaiPmhTemplate) {
        this.oaiPmhTemplate = oaiPmhTemplate;
    }

    public Document oaiPmhData() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        Node responseDate = oaiPmhTemplate.getElementsByTagName("responseDate").item(0);
        responseDate.setTextContent(sdf.format(new Date()));

        String request = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toString();
        String uri = request.substring(0, (request.indexOf(verb) - 1));

        Node requestNode = oaiPmhTemplate.getElementsByTagName("request").item(0);
        requestNode.setTextContent(uri);
        requestNode.getAttributes().getNamedItem("verb").setTextContent(verb);

        if (getFormat() != null) {
            requestNode.getAttributes().getNamedItem("metadataPrefix").setTextContent(getFormat().getMdprefix());
        } else {
            Element requestNodeElem = (Element) requestNode;
            requestNodeElem.removeAttribute("metadataPrefix");
        }

        Element verbElem = oaiPmhTemplate.createElement(verb);
        oaiPmhTemplate.getDocumentElement().appendChild(verbElem);

        try {
            oaiPmhDataBuilder = (OaiPmhDataBuilder) this.getClass().getDeclaredMethod("get" + verb).invoke(this);
            oaiPmhDataBuilder.setVerb(verb);
        } catch (NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return oaiPmhDataBuilder.oaiXmlData();
    }

    private OaiPmhDataBuilder getListIdentifiers() {
        ListIdentifiers listIdentifiers = new ListIdentifiers();
        listIdentifiers.setOaiPmhTpl(oaiPmhTemplate);
        listIdentifiers.setFormat(getFormat());
        listIdentifiers.setOaiPmhListByToken(oaiPmhListByToken);
        listIdentifiers.setOaiPmhList(oaiPmhList);
        listIdentifiers.setDataSize(getRecords().size());
        listIdentifiers.setResumptionToken(resumptionToken);
        listIdentifiers.setRecordsProPage(recordsProPage);
        return listIdentifiers;
    }

    private OaiPmhDataBuilder getListRecords() {
        ListRecords listRecords = new ListRecords();
        listRecords.setOaiPmhTpl(oaiPmhTemplate);
        listRecords.setFormat(getFormat());
        listRecords.setOaiPmhListByToken(oaiPmhListByToken);
        listRecords.setOaiPmhList(oaiPmhList);
        listRecords.setDataSize(getRecords().size());
        listRecords.setResumptionToken(resumptionToken);
        listRecords.setRecordsProPage(recordsProPage);
        return listRecords;
    }

    private OaiPmhDataBuilder getGetRecord() {
        GetRecord getRecord = new GetRecord();
        getRecord.setOaiPmhTpl(oaiPmhTemplate);
        getRecord.setFormat(getFormat());
        getRecord.setOaiPmhList(oaiPmhList);
        getRecord.setIdentifier(identifier);
        return getRecord;
    }

    private OaiPmhDataBuilder getListSets() {
        ListSets listSets = new ListSets();
        listSets.setOaiPmhTpl(oaiPmhTemplate);
        listSets.setVerb(verb);
        listSets.setSets(sets);
        return listSets;
    }

    private OaiPmhDataBuilder getListMetadataFormats() {
        ListMetadataFormats listMetadataFormats = new ListMetadataFormats();
        listMetadataFormats.setOaiPmhTpl(oaiPmhTemplate);
        listMetadataFormats.setVerb(verb);
        listMetadataFormats.setFormats(formats);
        return listMetadataFormats;
    }

    private OaiPmhDataBuilder getIdentify() {
        Identify identify = new Identify();
        identify.setOaiPmhTpl(oaiPmhTemplate);
        identify.setVerb(verb);
        identify.setEnvironment(environment);
        identify.setDisseminations(disseminations);
        return identify;
    }

    public OaiPmhDataBuilderFactory setVerb(String verb) {
        this.verb = verb;
        return this;
    }

    public String getMdprefix() {
        return mdprefix;
    }

    public OaiPmhDataBuilderFactory setMdprefix(String mdprefix) {
        this.mdprefix = mdprefix;
        return this;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public OaiPmhDataBuilderFactory setRecordsProPage(int recordsProPage) {
        this.recordsProPage = recordsProPage;
        return this;
    }

    public void setRecords(Collection<Record> records) {
        this.records = records;
    }

    public Collection<Record> getRecords() {
        return records;
    }

    public Collection<Set> getSets() {
        return sets;
    }

    public void setSets(Collection<Set> sets) {
        this.sets = sets;
    }

    public Collection<Format> getFormats() {
        return formats;
    }

    public void setFormats(Collection<Format> formats) {
        this.formats = formats;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public Collection<OaiPmhListByToken> getOaiPmhListByToken() {
        return oaiPmhListByToken;
    }

    public void setOaiPmhListByToken(Collection<OaiPmhListByToken> oaiPmhListByToken) {
        this.oaiPmhListByToken = oaiPmhListByToken;
    }

    public Collection<OaiPmhList> getOaiPmhList() {
        return oaiPmhList;
    }

    public void setOaiPmhList(Collection<OaiPmhList> oaiPmhList) {
        this.oaiPmhList = oaiPmhList;
    }

    public Collection<Dissemination> getDisseminations() {
        return disseminations;
    }

    public void setDisseminations(Collection<Dissemination> disseminations) {
        this.disseminations = disseminations;
    }

    public ResumptionToken getResumptionToken() {
        return resumptionToken;
    }

    public void setResumptionToken(ResumptionToken resumptionToken) {
        this.resumptionToken = resumptionToken;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
