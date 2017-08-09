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
"pageSize",
"page"
})
public class Paging {

@JsonProperty("pageSize")
private String pageSize;
@JsonProperty("page")
private String page;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("pageSize")
public String getPageSize() {
return pageSize;
}

@JsonProperty("pageSize")
public void setPageSize(String pageSize) {
this.pageSize = pageSize;
}

@JsonProperty("page")
public String getPage() {
return page;
}

@JsonProperty("page")
public void setPage(String page) {
this.page = page;
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