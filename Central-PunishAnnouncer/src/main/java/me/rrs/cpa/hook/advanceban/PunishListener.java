package me.rrs.cpa.hook.advanceban;

import me.leoko.advancedban.bukkit.event.PunishmentEvent;
import me.leoko.advancedban.utils.Punishment;
import me.rrs.centralbot.spigot.CentralBotAPI;
import me.rrs.cpa.CentralPunishAnnouncer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class PunishListener implements Listener {

    final CentralBotAPI api = new CentralBotAPI(CentralPunishAnnouncer.getInstance());

    @EventHandler
    public void onPunishment(PunishmentEvent event) {
        Punishment punish = event.getPunishment();
        EmbedBuilder builder = new EmbedBuilder();

        String title = "";
        switch (punish.getType().getName().toUpperCase()) {
            case "BAN":
            case "TEMPBAN":
            case "BAN-IP":
            case "BANIP":
            case "IPBAN":
            case "TEMPIPBAN":
            case "TIPBAN":
                title = punish.getType().isIpOrientated() ? "IP Ban" : "Ban";
                builder.addField("Operator", punish.getOperator(), false);
                builder.addField("Reason", punish.getReason(), false);
                builder.addField("Duration", punish.getDuration(true), false);
                break;
            case "KICK":
                title = "Kick";
                builder.addField("Operator", punish.getOperator(), false);
                builder.addField("Reason", punish.getReason(), false);
                break;
            case "MUTE":
            case "TEMPMUTE":
                title = "Mute";
                builder.addField("Operator", punish.getOperator(), false);
                builder.addField("Reason", punish.getReason(), false);
                builder.addField("Duration", punish.getDuration(true), false);
                break;
            case "WARN":
            case "TEMPWARN":
                title = "Warn";
                builder.addField("Operator", punish.getOperator(), false);
                builder.addField("Reason", punish.getReason(), false);
                builder.addField("Duration", punish.getDuration(true), false);
                break;
            default:
                break;
        }

        if (!title.isEmpty()) {
            builder.setTitle(title);
            builder.addField("Player", punish.getName(), false);

            TextChannel channel = api.getJda().getTextChannelById("1063109339608526868");
            channel.sendMessageEmbeds(builder.build()).queue();
        }
    }


}
