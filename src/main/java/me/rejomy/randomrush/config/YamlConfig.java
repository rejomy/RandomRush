package me.rejomy.randomrush.config;

import me.rejomy.randomrush.interfaces.Loadable;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Objects;

public abstract class YamlConfig implements Loadable {
    protected final FileConfiguration config;

    public YamlConfig (FileConfiguration config) {
        this.config = config;
    }

    protected int getIntElse(String path, int any) {
        return (int) Objects.requireNonNullElse(value(path), any);
    }

    protected double getDoubleElse(String path, double any) {
        return (double) Objects.requireNonNullElse(value(path), any);
    }

    protected float getFloatElse(String path, float any) {
        return (float) Objects.requireNonNullElse(value(path), any);
    }

    protected boolean getBooleanElse(String path, boolean any) {
        return (boolean) Objects.requireNonNullElse(value(path), any);
    }

    protected String getStringElse(String path, String any) {
        return (String) Objects.requireNonNullElse(value(path), any);
    }

    protected List<String> getStringListElse(String path, List<String> any) {
        return (List<String>) Objects.requireNonNullElse(value(path), any);
    }

    private Object value(String path) {
        return config.get(path);
    }
}
