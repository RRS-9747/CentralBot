package me.rrs.cpa;

import me.rrs.cpa.hook.advanceban.PunishListener;
import me.rrs.cpa.hook.advanceban.RevokePunishment;
import me.rrs.cpa.utils.UpdateAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class CentralPunishAnnouncer extends JavaPlugin {

    private static CentralPunishAnnouncer instance;

    public static CentralPunishAnnouncer getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new PunishListener(), this);
        getServer().getPluginManager().registerEvents(new RevokePunishment(), this);
        updateChecker();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    private void updateChecker(){
        UpdateAPI updateAPI = new UpdateAPI();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () ->{
            if (updateAPI.hasSpigotUpdate("107799")) {
                String newVersion = updateAPI.getSpigotVersion("107799");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("centralbot.notify")) {
                        p.sendMessage("--------------------------------");
                        p.sendMessage("You are using CentralBot " + getDescription().getVersion());
                        p.sendMessage("However version " + newVersion + " is available.");
                        p.sendMessage("You can download it from: " + "https://www.spigotmc.org/resources/107799/");
                        p.sendMessage("--------------------------------");
                    }
                }
            }
        }, 0L, 20L * 60 * 60 * 2);
    }
}
