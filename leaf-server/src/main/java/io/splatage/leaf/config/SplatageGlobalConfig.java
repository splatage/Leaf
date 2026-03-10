package io.splatage.leaf.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;

import java.io.File;
import java.util.List;
import java.util.Map;

public final class SplatageGlobalConfig {

    private static final String CURRENT_VERSION = "1.0";

    private static ConfigFile configFile;

    public SplatageGlobalConfig(final boolean init) throws Exception {
        configFile = ConfigFile.loadConfig(new File(SplatageConfig.I_CONFIG_FOLDER, SplatageConfig.I_GLOBAL_CONFIG_FILE));

        configFile.set("config-version", CURRENT_VERSION);
        configFile.addComments("config-version", """
            Splatage Config

            Fork-local configuration for Splatage features.
            """);
    }

    public void saveConfig() throws Exception {
        configFile.save();
    }

    public void createTitledSection(final String title, final String path) {
        configFile.addSection(title);
        configFile.addDefault(path, null);
    }

    public boolean getBoolean(final String path, final boolean def, final String comment) {
        configFile.addDefault(path, def, comment);
        return configFile.getBoolean(path, def);
    }

    public boolean getBoolean(final String path, final boolean def) {
        configFile.addDefault(path, def);
        return configFile.getBoolean(path, def);
    }

    public String getString(final String path, final String def, final String comment) {
        configFile.addDefault(path, def, comment);
        return configFile.getString(path, def);
    }

    public String getString(final String path, final String def) {
        configFile.addDefault(path, def);
        return configFile.getString(path, def);
    }

    public double getDouble(final String path, final double def, final String comment) {
        configFile.addDefault(path, def, comment);
        return configFile.getDouble(path, def);
    }

    public double getDouble(final String path, final double def) {
        configFile.addDefault(path, def);
        return configFile.getDouble(path, def);
    }

    public int getInt(final String path, final int def, final String comment) {
        configFile.addDefault(path, def, comment);
        return configFile.getInteger(path, def);
    }

    public int getInt(final String path, final int def) {
        configFile.addDefault(path, def);
        return configFile.getInteger(path, def);
    }

    public long getLong(final String path, final long def, final String comment) {
        configFile.addDefault(path, def, comment);
        return configFile.getLong(path, def);
    }

    public long getLong(final String path, final long def) {
        configFile.addDefault(path, def);
        return configFile.getLong(path, def);
    }

    public List<String> getList(final String path, final List<String> def, final String comment) {
        configFile.addDefault(path, def, comment);
        return configFile.getStringList(path);
    }

    public List<String> getList(final String path, final List<String> def) {
        configFile.addDefault(path, def);
        return configFile.getStringList(path);
    }

    public ConfigSection getConfigSection(final String path, final Map<String, Object> defaultKeyValue, final String comment) {
        configFile.addDefault(path, null, comment);
        configFile.makeSectionLenient(path);
        defaultKeyValue.forEach((string, object) -> configFile.addExample(path + "." + string, object));
        return configFile.getConfigSection(path);
    }

    public ConfigSection getConfigSection(final String path, final Map<String, Object> defaultKeyValue) {
        configFile.addDefault(path, null);
        configFile.makeSectionLenient(path);
        defaultKeyValue.forEach((string, object) -> configFile.addExample(path + "." + string, object));
        return configFile.getConfigSection(path);
    }

    public Boolean getBoolean(final String path) {
        String value = configFile.getString(path, null);
        return value == null ? null : Boolean.parseBoolean(value);
    }

    public String getString(final String path) {
        return configFile.getString(path, null);
    }

    public Double getDouble(final String path) {
        String value = configFile.getString(path, null);
        if (value == null) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            SplatageConfig.LOGGER.warn("{} is not a valid number, skipped! Please check your configuration.", path, e);
            return null;
        }
    }

    public Integer getInt(final String path) {
        String value = configFile.getString(path, null);
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            SplatageConfig.LOGGER.warn("{} is not a valid number, skipped! Please check your configuration.", path, e);
            return null;
        }
    }

    public Long getLong(final String path) {
        String value = configFile.getString(path, null);
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            SplatageConfig.LOGGER.warn("{} is not a valid number, skipped! Please check your configuration.", path, e);
            return null;
        }
    }

    public List<String> getList(final String path) {
        return configFile.getList(path, null);
    }

    public ConfigSection getConfigSection(final String path) {
        configFile.addDefault(path, null);
        configFile.makeSectionLenient(path);
        return configFile.getConfigSection(path);
    }

    public void addComment(final String path, final String comment) {
        configFile.addComment(path, comment);
    }
}
