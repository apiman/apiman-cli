package io.apiman.cli.core.org.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"beans",
"totalSize"
})
public class OrgSearchOut {

@JsonProperty("beans")
private List<Bean> beans = new ArrayList<Bean>();
@JsonProperty("totalSize")
private String totalSize;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("beans")
public List<Bean> getBeans() {
return beans;
}

@JsonProperty("beans")
public void setBeans(List<Bean> beans) {
this.beans = beans;
}

@JsonProperty("totalSize")
public String getTotalSize() {
return totalSize;
}

@JsonProperty("totalSize")
public void setTotalSize(String totalSize) {
this.totalSize = totalSize;
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

