package me.vadim.penis;

import org.bukkit.plugin.java.JavaPlugin;

public class PenisPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("penis").setExecutor(new PenisCommand(this));
    }
}
