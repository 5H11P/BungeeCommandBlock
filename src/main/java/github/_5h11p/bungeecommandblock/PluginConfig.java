package github._5h11p.bungeecommandblock;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class PluginConfig {

    private final Set<String> whitelistCommands;
    private final Set<String> blacklistCommands;
    private final Set<String> staffWhitelistCommands;
    private final String undefinedCommandMessage;
    private final String blacklistCommandMessage;

    private PluginConfig(Set<String> whitelistCommands,
                         Set<String> blacklistCommands,
                         Set<String> staffWhitelistCommands,
                         String undefinedCommandMessage,
                         String blacklistCommandMessage) {
        this.whitelistCommands = whitelistCommands;
        this.blacklistCommands = blacklistCommands;
        this.staffWhitelistCommands = staffWhitelistCommands;
        this.undefinedCommandMessage = undefinedCommandMessage;
        this.blacklistCommandMessage = blacklistCommandMessage;
    }

    public static PluginConfig load(File file) throws IOException {
        Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

        Set<String> whitelist = normalizeCommandList(configuration.getStringList("whitelist-commands"));
        Set<String> blacklist = normalizeCommandList(configuration.getStringList("blacklist-commands"));
        Set<String> staffWhitelist = normalizeCommandList(configuration.getStringList("staff-whitelist-commands"));

        String undefinedMessage = colorize(configuration.getString("undefined-command-message",
                "Unknown command. Type \"/help\" for help."));
        String blacklistMessage = colorize(configuration.getString("blacklist-command-message",
                "You cannot use this command."));

        return new PluginConfig(whitelist, blacklist, staffWhitelist, undefinedMessage, blacklistMessage);
    }

    public boolean isWhitelisted(String commandRoot) {
        return whitelistCommands.contains(commandRoot);
    }

    public boolean isBlacklisted(String commandRoot) {
        return blacklistCommands.contains(commandRoot);
    }

    public boolean isStaffWhitelisted(String commandRoot) {
        return staffWhitelistCommands.contains(commandRoot);
    }

    public Set<String> getWhitelistCommands() {
        return whitelistCommands;
    }

    public Set<String> getBlacklistCommands() {
        return blacklistCommands;
    }

    public Set<String> getStaffWhitelistCommands() {
        return staffWhitelistCommands;
    }

    public String getUndefinedCommandMessage() {
        return undefinedCommandMessage;
    }

    public String getBlacklistCommandMessage() {
        return blacklistCommandMessage;
    }

    private static Set<String> normalizeCommandList(List<String> commands) {
        if (commands == null || commands.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> normalized = new HashSet<String>();
        for (String command : commands) {
            String value = normalizeCommand(command);
            if (value != null) {
                normalized.add(value);
            }
        }
        return Collections.unmodifiableSet(normalized);
    }

    public static String extractCommandRoot(String rawMessage) {
        if (rawMessage == null) {
            return null;
        }

        String trimmed = rawMessage.trim();
        if (trimmed.isEmpty() || trimmed.charAt(0) != '/') {
            return null;
        }

        int spaceIndex = trimmed.indexOf(' ');
        String root = spaceIndex >= 0 ? trimmed.substring(0, spaceIndex) : trimmed;
        return normalizeCommand(root);
    }

    private static String normalizeCommand(String command) {
        if (command == null) {
            return null;
        }

        String value = command.trim();
        if (value.isEmpty()) {
            return null;
        }

        if (value.charAt(0) != '/') {
            value = "/" + value;
        }

        return value.toLowerCase(Locale.ROOT);
    }

    private static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text);
    }
}
