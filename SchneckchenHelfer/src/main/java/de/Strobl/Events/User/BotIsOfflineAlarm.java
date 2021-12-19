package de.Strobl.Events.User;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotIsOfflineAlarm extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(BotIsOfflineAlarm.class);
	public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event) {
		try {
			if (event.getMember().getUser().isBot() && !(event.getUser() == event.getJDA().getSelfUser())) {
				Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
				
				EmbedBuilder builder = Discord.standardEmbed(Color.RED, "Bot ändert Online-Status", event.getMember().getId(), event.getMember().getEffectiveAvatarUrl());
				
				builder.addField("Betroffener Bot:", event.getMember().getAsMention(), true);
				builder.addField("Online Status", event.getNewValue().toString(), true);
				
				event.getGuild().getTextChannelById(ini.get("Settings", "LogChannel")).sendMessage(event.getMember().getAsMention()).setEmbeds(builder.build()).queue();
				builder.clear();
			}
		} catch (IOException e) {
			logger.error("IO-Fehler Onlinestatus Überwachung", e);
		} catch (Exception e) {
			logger.error("Fehler Onlinestatus Überwachung", e);
		}
	}
}