package io.xygeni.plugins.jenkins.model;

import io.xygeni.plugins.jenkins.configuration.XygeniConfiguration;
import io.xygeni.plugins.jenkins.util.UserUtil;
import java.util.HashMap;
import java.util.Map;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

public abstract class XygeniEvent {

    private final Map<String, String> properties = new HashMap<>();

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("jenkinsUrl", getJenkinsUrl());
        json.put("userId", getCurrentUserId());
        json.put("eventType", getType());
        json.put("action", getAction());
        json.put("properties", properties);
        json.put("plugin-version", getXygeniVersion());
        return json;
    }

    private String getXygeniVersion() {
        return XygeniConfiguration.get().getVersion();
    }

    protected abstract String getType();

    protected abstract String getAction();

    private Object getCurrentUserId() {
        return UserUtil.getUserId(); // current authenticated userId or anonymous
    }

    private Object getJenkinsUrl() {
        Jenkins instance = Jenkins.get(); // throws illegalStateException if instance is starting or shutdown
        return instance.getRootUrl();
    }

    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    @Override
    public String toString() {
        return "XygeniEvent{"
                + ", jenkinsUrl=" + getJenkinsUrl()
                + ", userId=" + getCurrentUserId()
                + ", type=" + getType()
                + ", action=" + getAction()
                + ", properties=" + properties + '}';
    }
}
