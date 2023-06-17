package su.nightexpress.sunlight.hook.impl;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.event.Listener;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.hook.HookId;

public class DiscordSrvHook implements Listener {

    private static boolean discordSrvEnabled = false;
    JDA jda;

    public DiscordSrvHook(SunLight plugin) {
        if (!plugin.getServer().getPluginManager().isPluginEnabled(HookId.DISCORD_SRV)) {
            return;
        }

        discordSrvEnabled = true;
        plugin.info("Discord srv hook enabled.");

        jda = DiscordSRV.getPlugin().getJda();
    }

    public static void sendMessageToMain(String message) {
        if (!discordSrvEnabled) {
            return;
        }

        TextChannel textChannel = DiscordSRV.getPlugin().getMainTextChannel();
        textChannel.sendMessage(message).queue();
    }
}

