package de.qucosa.oai.provider.persitence.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordTransport implements Serializable {
    
}
