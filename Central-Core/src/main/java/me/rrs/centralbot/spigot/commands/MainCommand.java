package me.rrs.centralbot.spigot.commands;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.rrs.centralbot.spigot.CentralBot;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {

    private YamlDocument config;

    public MainCommand(YamlDocument config){
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (args.length == 0){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&m&l[&6&lCentralBot&r&m&l]&r CentralBot by RRS"));
            }else {
                switch (args[0]) {
                    case "reload-config":
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            if (player.hasPermission("centralbot.admin")) {
                                try {
                                    this.config.reload();
                                    player.sendMessage(ChatColor.GREEN + "[CentralBot] " + ChatColor.RESET + "Config reloaded successfully!");
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&m&l[&6&lCentralBot&r&m&l]&r You need to restart the server if you want to change database & bot stuffs "));
                                } catch (IOException e) {
                                    player.sendMessage(ChatColor.RED + "[CentralBot] " + ChatColor.RESET + "Error reloading config. Check Console for more details!");
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        break;

                    case "reload-commands":
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            if (player.hasPermission("centralbot.admin")) {
                                CentralBot.getJda().updateCommands().queue();
                                Bukkit.getScheduler().runTaskLater(CentralBot.getInstance(), () -> {
                                    CommandListUpdateAction updateAction = CentralBot.getJda().updateCommands();
                                    for (SlashCommandData command : CentralBot.getCommands()) {
                                        updateAction.addCommands(command);
                                        CentralBot.log("&m&l[&6&lCentralBot&r&m&l]&r '" + command.getName() + "' registered!");
                                    }
                                    updateAction.queue();
                                }, 20L);
                                player.sendMessage(ChatColor.GREEN + "[CentralBot] " + ChatColor.RESET + " All slash commands reloaded! (may take some times to came up)");
                            }
                        }
                        break;

                    case "reload-all":
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            if (player.hasPermission("centralbot.admin")) {
                                try {
                                    this.config.reload();
                                    player.sendMessage(ChatColor.GREEN + "[CentralBot] " + ChatColor.RESET + "Config reloaded successfully!");
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&m&l[&6&lCentralBot&r&m&l]&r You need to restart the server if you want to change database & bot stuffs "));
                                } catch (IOException e) {
                                    player.sendMessage(ChatColor.RED + "[CentralBot] " + ChatColor.RESET + "Error reloading config. Check Console for more details!");
                                    throw new RuntimeException(e);
                                }
                                CentralBot.getJda().updateCommands().queue();
                                Bukkit.getScheduler().runTaskLater(CentralBot.getInstance(), () -> {
                                    CommandListUpdateAction updateAction = CentralBot.getJda().updateCommands();
                                    for (SlashCommandData command : CentralBot.getCommands()) {
                                        updateAction.addCommands(command);
                                        CentralBot.log("&m&l[&6&lCentralBot&r&m&l]&r '" + command.getName() + "' registered!");
                                    }
                                    updateAction.queue();
                                }, 20L);
                                player.sendMessage(ChatColor.GREEN + "[CentralBot] " + ChatColor.RESET + " All slash commands reloaded! (may take some times to came up)");
                            }
                        }
                        break;
                }
            }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> list = new ArrayList<>();
        list.add("reload-config");
        list.add("reload-commands");
        list.add("reload-all");
        if (args.length == 1) {
            return list;
        }
        return Collections.emptyList();
    }
}
