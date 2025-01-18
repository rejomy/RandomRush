package me.rejomy.randomrush;

import org.bukkit.plugin.java.JavaPlugin;

public class RandomRush extends JavaPlugin {
    @Override
    public void onLoad() {
        RandomRushAPI.INSTANCE.load(this);
    }

    @Override
    public void onDisable() {
        RandomRushAPI.INSTANCE.stop(this);
    }

    @Override
    public void onEnable() {
        RandomRushAPI.INSTANCE.start(this);
    }
}
