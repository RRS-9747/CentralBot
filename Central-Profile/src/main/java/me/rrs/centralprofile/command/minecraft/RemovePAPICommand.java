package me.rrs.centralprofile.command.minecraft;

import me.rrs.centralprofile.CentralProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class RemovePAPICommand implements CommandExecutor, TabCompleter {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("centralbot.admin")){
                player.sendMessage(ChatColor.RED + "[CentralBot Profile] " + ChatColor.RESET + "You don't have permission to use this command!");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "[CentralBot Profile] " + ChatColor.RESET + "Please provide a Title name to remove.");
                return false;
            }

            if (CentralProfile.getDatabase().removePAPITitle(args[0])){
                sender.sendMessage(ChatColor.GREEN + "[CentralBot Profile] " + ChatColor.RESET + args[0] +"Removed Successfully");
            }else sender.sendMessage(ChatColor.RED + "[CentralBot Profile] " + ChatColor.RESET + "Error removing" + args[0]);
        }



        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return CentralProfile.getDatabase().getPAPITitles();
        }
        return Collections.emptyList();
    }
}
