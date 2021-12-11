package de.Strobl.Events.User;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;
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
				EmbedBuilder BotOffline = new EmbedBuilder();
				BotOffline.setAuthor("Bot ändert Online-Status", event.getGuild().getIconUrl(), event.getGuild().getIconUrl());
				BotOffline.addField("Betroffener Bot:", event.getMember().getAsMention(), true);
				BotOffline.addField("Online Status", event.getNewValue().toString(), true);
				BotOffline.setColor(0x110acc);
				event.getGuild().getTextChannelById(ini.get("Settings", "Settings.LogChannel")).sendMessageEmbeds(BotOffline.build()).queue();
				BotOffline.clear();
			}
		} catch (IOException e) {
			logger.error("IO-Fehler Onlinestatus Überwachung", e);
		} catch (Exception e) {
			logger.error("Fehler Onlinestatus Überwachung", e);
		}
	}
}