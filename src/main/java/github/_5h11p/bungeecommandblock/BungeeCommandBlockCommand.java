package github._5h11p.bungeecommandblock;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;

public final class BungeeCommandBlockCommand extends Command {

    private final BungeeCommandBlockPlugin plugin;

    public BungeeCommandBlockCommand(BungeeCommandBlockPlugin plugin) {
        super("bcb", null, "bcbreload");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            PluginConfig config = plugin.getPluginConfig();
            String message = config != null
                    ? config.getUndefinedCommandMessage()
                    : "Unknown command. Type \"/help\" for help.";
            sender.sendMessage(TextComponent.fromLegacyText(
                    message.replace("%command%", buildCommandInput(args))
            ));
            return;
        }

        if (args.length != 1 || !"reload".equalsIgnoreCase(args[0])) {
            sender.sendMessage(TextComponent.fromLegacyText("Usage: /bcb reload"));
            return;
        }

        try {
            plugin.reloadPluginConfig();
            sender.sendMessage(TextComponent.fromLegacyText("[BungeeCommandBlock] Config reloaded successfully."));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to reload config.yml: " + e.getMessage());
            sender.sendMessage(TextComponent.fromLegacyText("[BungeeCommandBlock] Failed to reload config.yml. Check console logs."));
        }
    }

    private String buildCommandInput(String[] args) {
        StringBuilder builder = new StringBuilder(getName());
        for (String arg : args) {
            builder.append(' ').append(arg);
        }
        return builder.toString();
    }
}
