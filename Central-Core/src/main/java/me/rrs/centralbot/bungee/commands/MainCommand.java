package me.rrs.centralbot.bungee.commands;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.rrs.centralbot.bungee.CentralBot;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainCommand extends Command implements TabExecutor {

    private YamlDocument config;

    public MainCommand(YamlDocument config){
        super("centralbot");
        this.config = config;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage();
        if (args.length == 0){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&m&l[&6&lCentralBot&r&m&l]&r CentralBot by RRS"));
        }else {
            switch (args[0]) {
                case "reload-config":
                    if (sender instanceof ProxiedPlayer) {
                        ProxiedPlayer player = (ProxiedPlayer) sender;
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
                    if (sender instanceof ProxiedPlayer) {
                        ProxiedPlayer player = (ProxiedPlayer) sender;
                        if (player.hasPermission("centralbot.admin")) {
                            CentralBot.getJda().updateCommands().queue();
                            CentralBot.getInstance().getProxy().getScheduler().schedule(CentralBot.getInstance(), () ->{
                                CommandListUpdateAction updateAction = CentralBot.getJda().updateCommands();
                                for (SlashCommandData command : CentralBot.getCommands()) {
                                    updateAction.addCommands(command);
                                    CentralBot.getInstance().getProxy().getConsole().sendMessage("&m&l[&6&lCentralBot&r&m&l]&r '" + command.getName() + "' registered!");
                                }
                                updateAction.queue();
                            }, 2, TimeUnit.SECONDS);
                            player.sendMessage(ChatColor.GREEN + "[CentralBot] " + ChatColor.RESET + " All slash commands reloaded! (may take some times to came up)");
                        }
                    }
                    break;

                case "reload-all":
                    if (sender instanceof ProxiedPlayer) {
                        ProxiedPlayer player = (ProxiedPlayer) sender;
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
                            CentralBot.getInstance().getProxy().getScheduler().schedule(CentralBot.getInstance(), () ->{
                                CommandListUpdateAction updateAction = CentralBot.getJda().updateCommands();
                                for (SlashCommandData command : CentralBot.getCommands()) {
                                    updateAction.addCommands(command);
                                    CentralBot.getInstance().getProxy().getConsole().sendMessage("&m&l[&6&lCentralBot&r&m&l]&r '" + command.getName() + "' registered!");
                                }
                                updateAction.queue();
                            }, 2, TimeUnit.SECONDS);
                            player.sendMessage(ChatColor.GREEN + "[CentralBot] " + ChatColor.RESET + " All slash commands reloaded! (may take some times to came up)");
                        }
                    }
                    break;
            }
        }

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
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
