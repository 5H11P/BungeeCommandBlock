package github._5h11p.bungeecommandblock;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public final class CommandFilterListener implements net.md_5.bungee.api.plugin.Listener {

    private static final String STAFF_PERMISSION = "bungeecommandblock.staff";
    private static final String BYPASS_PERMISSION = "bungeecommandblock.bypass";
    private static final String PLUGIN_WILDCARD_PERMISSION = "bungeecommandblock.*";

    private final BungeeCommandBlockPlugin plugin;

    public CommandFilterListener(BungeeCommandBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(ChatEvent event) {
        if (!event.isCommand()) {
            return;
        }

        if (!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        if (event.isProxyCommand()) {
            return;
        }

        PluginConfig config = plugin.getPluginConfig();
        if (config == null) {
            return;
        }

        String commandRoot = PluginConfig.extractCommandRoot(event.getMessage());
        if (commandRoot == null) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String inputCommand = normalizePlaceholderCommand(event.getMessage());

        if (hasBypassPermission(player)) {
            return;
        }

        if (config.isBlacklisted(commandRoot)) {
            event.setCancelled(true);
            sendMessage(player, config.getBlacklistCommandMessage(), inputCommand);
            return;
        }

        if (config.isWhitelisted(commandRoot)) {
            return;
        }

        if (config.isStaffWhitelisted(commandRoot)) {
            if (player.hasPermission(STAFF_PERMISSION)) {
                return;
            }

            event.setCancelled(true);
            sendMessage(player, config.getUndefinedCommandMessage(), inputCommand);
            return;
        }

        event.setCancelled(true);
        sendMessage(player, config.getUndefinedCommandMessage(), inputCommand);
    }

    private boolean hasBypassPermission(ProxiedPlayer player) {
        return player.hasPermission(BYPASS_PERMISSION)
                || player.hasPermission(PLUGIN_WILDCARD_PERMISSION);
    }

    private void sendMessage(ProxiedPlayer player, String messageTemplate, String inputCommand) {
        if (messageTemplate == null || messageTemplate.isEmpty()) {
            return;
        }
        String message = messageTemplate.replace("%command%", inputCommand == null ? "" : inputCommand);
        player.sendMessage(TextComponent.fromLegacyText(message));
    }

    private String normalizePlaceholderCommand(String input) {
        if (input == null) {
            return "";
        }
        String trimmed = input.trim();
        if (trimmed.startsWith("/")) {
            return trimmed.substring(1);
        }
        return trimmed;
    }
}
