package github._5h11p.bungeecommandblock;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class BungeeCommandBlockPlugin extends Plugin {

    private volatile PluginConfig pluginConfig;

    @Override
    public void onEnable() {
        try {
            ensureDefaultConfig();
            reloadPluginConfig();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.yml", e);
        }

        Listener listener = new CommandFilterListener(this);
        getProxy().getPluginManager().registerListener(this, listener);
        getProxy().getPluginManager().registerCommand(this, new BungeeCommandBlockCommand(this));
        getLogger().info("BungeeCommandBlock enabled.");
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public void reloadPluginConfig() throws IOException {
        this.pluginConfig = PluginConfig.load(new File(getDataFolder(), "config.yml"));
        getLogger().info("Loaded config: whitelist=" + pluginConfig.getWhitelistCommands().size()
                + ", staffWhitelist=" + pluginConfig.getStaffWhitelistCommands().size()
                + ", blacklist=" + pluginConfig.getBlacklistCommands().size());
    }

    private void ensureDefaultConfig() throws IOException {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            throw new IOException("Could not create plugin data folder: " + getDataFolder());
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (configFile.exists()) {
            return;
        }

        try (InputStream input = getResourceAsStream("config.yml")) {
            if (input == null) {
                throw new IOException("Bundled config.yml not found in plugin resources");
            }
            Files.copy(input, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
