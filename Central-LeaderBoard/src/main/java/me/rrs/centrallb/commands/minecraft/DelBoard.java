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

public class DelBoard implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (!player.hasPermission("centralbot.admin")) return true;
            if (args.length == 0){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&m&l[&6&lCentralBot&r&m&l]&r Give me a board name to delete!"));
                return true;
            }

            if (CentralLeaderBoard.getDatabase().getBoardList().contains(args[0])){
                CentralLeaderBoard.getDatabase().removeBoard(args[0]);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&m&l[&6&lCentralBot&r&m&l]&r Board " + args[0] + " removed successfully"));
            }else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&m&l[&6&lCentralBot&r&m&l]&r Board " + args[0] + " don't exist!"));
            }

        }
        return true;
    }
}