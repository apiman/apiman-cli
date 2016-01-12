package io.apiman.cli.core.declarative.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.apiman.cli.core.plugin.model.Plugin;

import java.util.List;

/**
 * @author pete
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DeclarativeSystem {
    @JsonProperty
    private List<DeclarativeGateway> gateways;

    @JsonProperty
    private List<Plugin> plugins;

    public List<DeclarativeGateway> getGateways() {
        return gateways;
    }

    public void setGateways(List<DeclarativeGateway> gateways) {
        this.gateways = gateways;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<Plugin> plugins) {
        this.plugins = plugins;
    }
}
