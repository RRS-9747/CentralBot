package me.rrs.centrallb.commands.minecraft;

import me.rrs.centrallb.CentralLeaderBoard;
import me.rrs.centrallb.Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.ajg0702.leaderboards.LeaderboardPlugin;

import java.io.IOException;

public class SetBoard implements CommandExecutor {

    final LeaderboardPlugin ajlb = (LeaderboardPlugin) Bukkit.getPluginManager().getPlugin("ajLeaderboards");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player){
            Player player = (Player) sender;

            if (!player.hasPermission("centralbot.admin")) return true;
            if (args.length < 2){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&m&l[&6&lCentralBot&r&m&l]&r Give me Board name & Placeholder"));
                return true;
            }
            String board = args[0];
            String rawBoard = args[1];
            if (!rawBoard.startsWith("%") && !rawBoard.endsWith("%")){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&m&l[&6&lCentralBot&r&m&l]&r Not a valid Placeholder!"));
                return true;
            }
            rawBoard = rawBoard.replaceAll("%", "");

            if (!ajlb.getCache().boardExists(rawBoard)){
                ajlb.getCache().createBoard(rawBoard);
            }


            CentralLeaderBoard.getDatabase().addBoard(board, rawBoard);

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&m&l[&6&lCentralBot&r&m&l]&r Board added successfully"));

        }

        return true;
    }
}