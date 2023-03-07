package me.rrs.centralbot.spigot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class CentralBotAPI {
    private static final Set<Plugin> usedAddons = new HashSet<>();


    public CentralBotAPI() {
    }

    public CentralBotAPI(Plugin plugin) {
        if (!usedAddons.contains(plugin)) {
            String addonName = plugin.getName();
            Map<String, Integer> addonData = CentralBot.getAddonsList().getOrDefault(addonName, new HashMap<>());
            int usageCount = addonData.getOrDefault(addonName, 0);
            addonData.put(addonName, usageCount + 1);
            CentralBot.getAddonsList().put(addonName, addonData);
            usedAddons.add(plugin);
        }
    }

    public JDA getJda() {
        return CentralBot.getJda();
    }

    public File getAddonFolder(Plugin plugin) {
        return new File(CentralBot.getInstance().getDataFolder().getPath(), "addons" + File.separator + plugin.getDescription().getName());
    }

    public void registerSlashCommand(Plugin plugin, List<SlashCommandData> commands) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (SlashCommandData command : commands){
                CentralBot.getCommands().add(command);
                CentralBot.getUpdateAction().addCommands(command);
                CentralBot.log("&m&l[&6&lCentralBot&r&m&l]&r Command '" + command.getName() + "' listed from " + plugin.getName());
            }
        }, 20);
    }

    public Connection getConnection() throws SQLException {
        return CentralBot.getDatabase().getDataSource().getConnection();
    }

    public List<Role> getPlayerRoles() {
        List<String> roleIds = CentralBot.getPlayerRoles();
        Guild guild = CentralBot.getJda().getGuildById(CentralBot.getGuildID());
        List<Role> roles = new ArrayList<>();
        for (String roleId : roleIds) {
            Role role = guild.getRoleById(roleId);
            if (role != null) {
                roles.add(role);
            }
        }
        return roles;
    }


    public List<Role> getAdminRoles() {
        List<String> roleIds = CentralBot.getAdminRoles();
        Guild guild = CentralBot.getJda().getGuildById(CentralBot.getGuildID());

        List<Role> roles = new ArrayList<>();
        for (String roleId : roleIds) {
            Role role = guild.getRoleById(roleId);
            if (role != null) {
                roles.add(role);
            }
        }

        return roles;
    }


}