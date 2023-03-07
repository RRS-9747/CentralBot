package me.rrs.essentials.bungee.commands.discord;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.rrs.essentials.bungee.CentralEssentials;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerList extends ListenerAdapter {

    private final YamlDocument config = CentralEssentials.getConfiguration();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals(config.getString("Bot.Command"))) {
            return;
        }

        Map<String, List<String>> playersByServer = getOnlinePlayersByServer();
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Players (" + ProxyServer.getInstance().getPlayers().size() + ")")
                .setColor(Color.GREEN);

        for (Map.Entry<String, List<String>> entry : playersByServer.entrySet()) {
            String serverName = entry.getKey();
            List<String> playerNames = entry.getValue();
            String playerList = String.join(", ", playerNames);
            String description = playerList.isEmpty() ? "No players online." : "```" + playerList + "```";
            MessageEmbed.Field field = new MessageEmbed.Field(serverName, description, false);
            builder.addField(field);
        }

        event.replyEmbeds(builder.build()).queue();
    }

    private Map<String, List<String>> getOnlinePlayersByServer() {
        Map<String, List<String>> playersByServer = new HashMap<>();
        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();

        for (Map.Entry<String, ServerInfo> entry : servers.entrySet()) {
            ServerInfo serverInfo = entry.getValue();
            List<String> players = serverInfo.getPlayers().stream()
                    .map(ProxiedPlayer::getName)
                    .collect(Collectors.toList());

            if (!players.isEmpty()) {
                playersByServer.put(entry.getKey(), players);
            }
        }

        if (ProxyServer.getInstance().getPlayers().isEmpty()) {
            playersByServer.clear();
        }

        return playersByServer;
    }
}


