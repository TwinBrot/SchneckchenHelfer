package de.Strobl.Events.User;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnUserUpdateOnlineStatusEvent extends ListenerAdapter {
	public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event) {
		Logger logger = Main.logger;
		try {
			if (event.getMember().getUser().isBot() && !(event.getUser() == event.getJDA().getSelfUser())) {
				Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
				EmbedBuilder BotOffline = new EmbedBuilder();
				BotOffline.setAuthor("Bot ändert Online-Status", event.getGuild().getIconUrl(),
						event.getGuild().getIconUrl());
				BotOffline.addField("Betroffener Bot:", event.getMember().getAsMention(), true);
				BotOffline.addField("Online Status", event.getNewValue().toString(), true);
				BotOffline.setColor(0x110acc);
				event.getGuild().getTextChannelById(ini.get("Settings", "Settings.LogChannel"))
						.sendMessage("<@227131380058947584>").queue();
				event.getGuild().getTextChannelById(ini.get("Settings", "Settings.LogChannel"))
						.sendMessageEmbeds(BotOffline.build()).queue();
				BotOffline.clear();
			}
		} catch (Exception e) {
			logger.error("Fehler", e);
		}
	}
}