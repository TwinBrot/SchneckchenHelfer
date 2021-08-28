package de.Strobl.Events.Nachrichten;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnMessageReactionRemoveEvent extends ListenerAdapter {
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		Logger logger = Main.logger;
		try {
//Abfrage
			if (!event.getChannel().getId().equals("143488875182948353")
					&& !event.getChannel().getId().equals("720439181696041122")) {
				EmbedBuilder ReactionRemoved = new EmbedBuilder();

				try {
					ReactionRemoved.setThumbnail(event.getReactionEmote().getEmote().getImageUrl());
				} catch (Exception e) {
				}
				ReactionRemoved.setColor(0x110acc);
				ReactionRemoved.setAuthor("Reaction removed:", event.getGuild().getIconUrl(),
						event.getGuild().getIconUrl());
				ReactionRemoved.addField("User:", event.getMember().getAsMention(), true);
				ReactionRemoved.addField("Emote:", event.getReactionEmote().getName(), true);
				ReactionRemoved.addField("Nachrichten Link:",
						"https://discordapp.com/channels" + "/" + event.getGuild().getId().toString() + "/"
								+ event.getTextChannel().getId() + "/" + event.getMessageId(),
						false);
				Wini ini;
				try {
					ini = new Wini(new File(Main.Pfad + "settings.ini"));
					String ID = ini.get("Settings", "Settings.LogChannel");
					event.getGuild().getTextChannelById(ID).sendMessageEmbeds(ReactionRemoved.build()).queue();
					ReactionRemoved.clear();
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			logger.error("IO-Fehler", e);
		}
	}
}
