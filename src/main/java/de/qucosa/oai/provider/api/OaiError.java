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

package de.qucosa.oai.provider.api;

import de.qucosa.oai.provider.api.utils.DateTimeConverter;
import de.qucosa.oai.provider.api.utils.DocumentXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

public class OaiError {

    private String errorCode;

    private String errorMsg;

    private String requestDate = DateTimeConverter.getOaiXmlDate();

    private String requestUrl = "http://localhost:8080/oai";

    public OaiError(String errorCode) {

        Map<String, String> oaiErrorConfig = new HashMap<String, String>() {
            {
                put("badArgument", "The request includes illegal arguments, is missing required arguments, includes a repeated argument, or values for arguments have an illegal syntax.");
                put("badResumptionToken", "The value of the resumptionToken argument is invalid or expired.");
                put("badVerb", "Value of the verb argument is not a legal OAI-PMH verb, the verb argument is missing, or the verb argument is repeated. ");
                put("cannotDisseminateFormat", "The metadata format identified by the value given for the metadataPrefix argument is not supported by the item or by the repository.");
                put("idDoesNotExist", "The value of the identifier argument is unknown or illegal in this repository.");
                put("noRecordsMatch", "The combination of the values of the from, until, set and metadataPrefix arguments results in an empty list.");
                put("noMetadataFormats", "There are no metadata formats available for the specified item.");
                put("noSetHierarchy", "The repository does not support sets.");
            }
        };
        if (!oaiErrorConfig.containsKey(errorCode)) {
            throw new RuntimeException("The " + errorCode + " is not a correct oai sepc error code.");
        }

        for(Map.Entry<String, String> entry : oaiErrorConfig.entrySet()) {

            if (entry.getKey().equals(errorCode)) {
                setErrorCode(errorCode);
                setErrorMsg(entry.getValue());
            }
        }
    }

    public String getErrorCode() {
        return  errorCode;
    }

    private void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    private void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public OaiError setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }

    public Document getOaiErrorXml() {
        Document error = DocumentXmlUtils.document(getClass().getResourceAsStream("/templates/oai_error.xml"), true);
        Node responseDateNode = error.getElementsByTagName("responseDate").item(0);
        responseDateNode.setTextContent(getRequestDate());

        Node requestNode = error.getElementsByTagName("request").item(0);
        requestNode.setTextContent(getRequestUrl());

        Node errorNode = error.getElementsByTagName("error").item(0);
        errorNode.getAttributes().getNamedItem("code").setTextContent(errorCode);
        errorNode.setTextContent(errorMsg);

        return error;
    }
}
