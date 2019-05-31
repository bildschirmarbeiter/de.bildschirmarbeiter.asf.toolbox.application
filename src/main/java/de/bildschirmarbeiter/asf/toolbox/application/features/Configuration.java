package de.bildschirmarbeiter.asf.toolbox.application.features;

import java.util.Map;
import java.util.TreeMap;

public class Configuration {

    final String pid;

    final Map<String, String> entries = new TreeMap<>();

    final boolean isFactory;

    private static final String DASH = "-";

    public Configuration(final String name) {
        isFactory = name.contains(DASH);
        if (isFactory) {
            pid = name.substring(0, name.indexOf(DASH));
        } else {
            if (name.endsWith(".cfg")) {
                pid = name.substring(0, name.indexOf(".cfg"));
            } else if (name.endsWith(".config")) {
                pid = name.substring(0, name.indexOf(".config"));
            } else {
                pid = name;
            }
        }
    }

    public String getPid() {
        return pid;
    }

    public void addEntry(final String key, final String value) {
        entries.put(key, value);
    }

    public Map<String, String> getEntries() {
        return entries;
    }

    public boolean isFactory() {
        return isFactory;
    }

}
