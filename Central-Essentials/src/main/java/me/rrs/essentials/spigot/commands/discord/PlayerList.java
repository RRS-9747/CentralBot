package me.rrs.essentials.spigot.commands.discord;

import com.earth2me.essentials.Essentials;
import dev.dejvokep.boostedyaml.YamlDocument;
import me.rrs.essentials.spigot.CentralEssentials;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerList extends ListenerAdapter {

    final YamlDocument config = CentralEssentials.getConfiguration();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(config.getString("Bot.Command-List"))){

            EmbedBuilder builder = new EmbedBuilder();

            // Get the list of online players
            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

            // Filter out vanished players using the EssentialsX plugin
            if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (Essentials.getPlugin(Essentials.class).getUser(player).isVanished()) {
                        onlinePlayers.remove(player);
                    }
                }
            }

            // Filter out vanished players using the SuperVanish plugin
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasMetadata("vanished") && player.getMetadata("vanished").get(0).asBoolean()) {
                    onlinePlayers.remove(player);
                }
            }

            // Format the list of online players as a comma-separated string
            String onlinePlayersNames = onlinePlayers.stream()
                    .map(Player::getName)
                    .collect(Collectors.joining(", "));


            builder.setTitle("Players (" + onlinePlayers.size() + ")");
            if (onlinePlayers.size() == 0) {
                builder.setDescription("No players online");
            } else {
                builder.setDescription("```" + onlinePlayersNames + "```");
            }
            builder.setColor(Color.GREEN);
            event.replyEmbeds(builder.build()).queue();

        }
    }


}
