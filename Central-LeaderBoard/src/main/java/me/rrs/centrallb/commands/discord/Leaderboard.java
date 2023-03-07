package me.rrs.centrallb.commands.discord;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.clip.placeholderapi.PlaceholderAPI;
import me.rrs.centrallb.CentralLeaderBoard;
import me.rrs.centrallb.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import us.ajg0702.leaderboards.LeaderboardPlugin;

import java.awt.*;
import java.util.List;

public class Leaderboard extends ListenerAdapter {

    private final YamlDocument config = CentralLeaderBoard.getConfiguration();
    private final LeaderboardPlugin ajlb = (LeaderboardPlugin) Bukkit.getPluginManager().getPlugin("ajLeaderboards");

    String board, rawBoard, time;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        List<String> boards = ajlb.getTopManager().getBoards();

        if (event.getName().equals(config.getString("Config.Command"))){
            event.deferReply();

            if (event.getOption("board") == null) return;
            board = event.getOption("board").getAsString();

            if (event.getOption("time") == null) {
                time = "alltime";
            }else {
                time = event.getOption("time").getAsString();
                if (!(time.equals("alltime") || time.equals("hourly") || time.equals("daily")
                        || time.equals("weekly") || time.equals("monthly") || time.equals("yearly"))){
                    event.reply("Choose a valid Time!").setEphemeral(true).queue();
                    return;
                }
            }

            rawBoard = CentralLeaderBoard.getDatabase().getRawBoard(board);

            if (boards.isEmpty() || !boards.contains(rawBoard)){
                event.reply("This board doesn't exist!").setEphemeral(true).queue();
                return;
            }

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(config.getString("Embed.Title")
                    .replace("{BOARD}", board)
                        .replace("{TIME}", time));
            builder.setColor(Color.decode("#00ff80"));
            String author = event.getUser().getName() + "#" + event.getUser().getDiscriminator();
            builder.setFooter(config.getString("Embed.Footer.Text").replace("{AUTHOR}", author));
            int top = config.getInt("Leaderboard.Top") + 1;
            for (int i = 1 ; i != top ; i++){
                String name = PlaceholderAPI.setPlaceholders(null, "%ajlb_lb_" + rawBoard + "_" + i + "_" + time + "_name%");
                String value = PlaceholderAPI.setPlaceholders(null, "%ajlb_lb_" + rawBoard + "_" + i + "_" + time + "_value%");
                String description = config.getString("Leaderboard.Format")
                        .replace("{NAME}", name)
                                .replace("{VALUE}", value)
                                        .replace("{BOARD}", board);
                builder.appendDescription(i + ". "+ description + "\n");

            }

            event.replyEmbeds(builder.build()).queue();
        }
    }

}
