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
"filters",
"orderBy",
"pageSize",
"page",
"paging"
})
public class OrgSearchIn {

@JsonProperty("filters")
private List<Filter> filters = new ArrayList<Filter>();
@JsonProperty("orderBy")
private OrderBy orderBy;
@JsonProperty("pageSize")
private String pageSize;
@JsonProperty("page")
private String page;
@JsonProperty("paging")
private Paging paging;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("filters")
public List<Filter> getFilters() {
return filters;
}

@JsonProperty("filters")
public void setFilters(List<Filter> filters) {
this.filters = filters;
}

@JsonProperty("orderBy")
public OrderBy getOrderBy() {
return orderBy;
}

@JsonProperty("orderBy")
public void setOrderBy(OrderBy orderBy) {
this.orderBy = orderBy;
}

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

@JsonProperty("paging")
public Paging getPaging() {
return paging;
}

@JsonProperty("paging")
public void setPaging(Paging paging) {
this.paging = paging;
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