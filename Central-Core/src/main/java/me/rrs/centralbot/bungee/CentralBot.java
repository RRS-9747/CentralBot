package me.rrs.centralbot.bungee;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.Pattern;
import dev.dejvokep.boostedyaml.dvs.segment.Segment;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import me.rrs.centralbot.bungee.commands.MainCommand;
import me.rrs.centralbot.bungee.utils.Metrics;
import me.rrs.centralbot.bungee.utils.UpdateAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CentralBot extends Plugin {


    private static JDA jda;
    private static YamlDocument config;
    private static CentralBot instance;
    private static CommandListUpdateAction commandList;
    private static Database database;
    private final String PREFIX = "&m&l[&6&lCentralBot&r&m&l]&r ";
    private static final Map<String, Map<String, Integer>> map = new HashMap<>();
    private static final List<SlashCommandData> commands = new ArrayList<>();
    private static String guildID;
    private static List<String> playerRole, adminRole;


    public static List<SlashCommandData> getCommands() {
        return commands;
    }
    public static Map<String, Map<String, Integer>> getMap() {
        return map;
    }
    public static JDA getJda() {
        return jda;
    }
    public static CommandListUpdateAction getCommandList() {
        return commandList;
    }
    public static CentralBot getInstance() {
        return instance;
    }
    public static Database getDatabase(){
        return database;
    }
    public static String getGuildID() {
        return guildID;
    }
    public static List<String> getPlayerRoles(){
        return playerRole;
    }
    public static List<String> getAdminRoles() {
        return adminRole;
    }


    @Override
    public void onLoad(){
        instance = this;
        Metrics metrics = new Metrics(this, 17872);
        File dir = new File(getDataFolder().getPath(), "addons");
        if (!dir.exists()){
            dir.mkdir();
        }
        metrics.addCustomChart(new Metrics.DrilldownPie("addon_usage", () -> map));
    }

    @Override
    public void onEnable () {
        checker();
        log(PREFIX + "Loading configuration file...");
        try {
            config = YamlDocument.create(new File(getDataFolder(), "config.yml"), getResourceAsStream("config.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setAutoSave(true).setVersioning(new Pattern(Segment.range(1, Integer.MAX_VALUE),
                            Segment.literal("."), Segment.range(0, 10)), "Version").build());
            log(PREFIX + "✔ Configuration file loaded successfully!");

            guildID = config.getString("Server.ID");

        } catch (IOException e) {
            log(PREFIX + "✖ Failed to load configuration file!");
            throw new RuntimeException(e);
        }

        getProxy().getPluginManager().registerCommand(this, new MainCommand(config));

        log(PREFIX + "Setting up database connection...");
        database = new Database(config);
        database.setupDataSource();

        log(PREFIX + "Loading bot...");
        loadBot();

        playerRole = config.getStringList("Server.Player-Role");
        adminRole = config.getStringList("Server.Admin-Role");

        commandList = jda.updateCommands();
        getProxy().getScheduler().schedule(this, () -> {
            commandList.queue();
            log(PREFIX + "Slash Commands Registered!");
        }, 2, TimeUnit.SECONDS);

        updateChecker();

    }

    @Override
    public void onDisable () {
        if (jda != null) {
            jda.updateCommands().queue();
            jda.shutdown();
        }
    }

    private void log(String log) {
        getProxy().getConsole().sendMessage(ChatColor.translateAlternateColorCodes('&', log));
    }


    public void updateChecker(){
        UpdateAPI updateAPI = new UpdateAPI();
        getProxy().getScheduler().schedule(this, () -> {
            if (updateAPI.hasSpigotUpdate("107799")) {
                String newVersion = updateAPI.getSpigotVersion("107799");
                for (ProxiedPlayer p : getProxy().getPlayers()) {
                    if (p.hasPermission("centralbot.notify")) {
                        p.sendMessage(TextComponent.fromLegacyText("--------------------------------"));
                        p.sendMessage(TextComponent.fromLegacyText("You are using CentralBot " + getDescription().getVersion()));
                        p.sendMessage(TextComponent.fromLegacyText("However version " + newVersion + " is available."));
                        p.sendMessage(TextComponent.fromLegacyText("You can download it from: https://www.spigotmc.org/resources/107799/"));
                        p.sendMessage(TextComponent.fromLegacyText("--------------------------------"));
                    }
                }
            }
        }, 0L, 1, TimeUnit.HOURS);


    }


    private void checker() {
        String pluginName = getDescription().getName();
        String pluginAuthor = getDescription().getAuthor();
        if (!pluginName.equals("CentralBot") || !pluginAuthor.equals("RRS")) {
            getLogger().severe("This plugin is modified!");
        }
    }

    private void loadBot() {
        jda = JDABuilder.createDefault(config.getString("Bot.Token"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setAutoReconnect(true)
                .setActivity(Activity.of(Activity.ActivityType.valueOf(config.getString("Bot.Activity.Type")), config.getString("Bot.Activity.Name")))
                .setStatus(OnlineStatus.valueOf(config.getString("Bot.Status")))
                .build();
        try {
            jda.awaitReady();
            log(PREFIX + "Logged as &b" + jda.getSelfUser().getName() + "#" + jda.getSelfUser().getDiscriminator());
        } catch (InterruptedException ignored) {
        }
    }
}
