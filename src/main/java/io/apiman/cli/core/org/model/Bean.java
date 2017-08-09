package io.apiman.cli.core.org.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"name",
"id",
"description",
"numClients",
"numMembers",
"numApis"
})
public class Bean {

@JsonProperty("name")
private String name;
@JsonProperty("id")
private String id;
@JsonProperty("description")
private String description;
@JsonProperty("numClients")
private String numClients;
@JsonProperty("numMembers")
private String numMembers;
@JsonProperty("numApis")
private String numApis;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("name")
public String getName() {
return name;
}

@JsonProperty("name")
public void setName(String name) {
this.name = name;
}

@JsonProperty("id")
public String getId() {
return id;
}

@JsonProperty("id")
public void setId(String id) {
this.id = id;
}

@JsonProperty("description")
public String getDescription() {
return description;
}

@JsonProperty("description")
public void setDescription(String description) {
this.description = description;
}

@JsonProperty("numClients")
public String getNumClients() {
return numClients;
}

@JsonProperty("numClients")
public void setNumClients(String numClients) {
this.numClients = numClients;
}

@JsonProperty("numMembers")
public String getNumMembers() {
return numMembers;
}

@JsonProperty("numMembers")
public void setNumMembers(String numMembers) {
this.numMembers = numMembers;
}

@JsonProperty("numApis")
public String getNumApis() {
return numApis;
}

@JsonProperty("numApis")
public void setNumApis(String numApis) {
this.numApis = numApis;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}