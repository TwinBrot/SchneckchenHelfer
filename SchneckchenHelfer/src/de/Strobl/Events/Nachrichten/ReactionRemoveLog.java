package de.Strobl.Events.Nachrichten;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;
import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionRemoveLog extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(ReactionRemoveLog.class);
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		try {
//Abfrage
			if (!event.getChannel().getId().equals("143488875182948353") && !event.getChannel().getId().equals("720439181696041122")) {
				EmbedBuilder ReactionRemoved = new EmbedBuilder();
				try {
					ReactionRemoved.setThumbnail(event.getReactionEmote().getEmote().getImageUrl());
				} catch (Exception e) {
				}
				ReactionRemoved.setColor(0x110acc);
				ReactionRemoved.setAuthor("Reaction removed:", event.getGuild().getIconUrl(), event.getGuild().getIconUrl());
				ReactionRemoved.addField("User:", event.getMember().getAsMention(), true);
				ReactionRemoved.addField("Emote:", event.getReactionEmote().getName(), true);
//Nachrichtenlink Erzeugung:
				String GuildID = event.getGuild().getId().toString();
				String ChannelID = event.getTextChannel().getId();
				String MessageID = event.getMessageId();
				String Nachrichtenlink = "https://discordapp.com/channels/" + GuildID + "/" + ChannelID + "/" + MessageID;
				ReactionRemoved.addField("Nachrichten Link:", "[Drück mich]("+Nachrichtenlink+")", true);
				Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
				String ID = ini.get("Settings", "Settings.LogChannel");
				event.getGuild().getTextChannelById(ID).sendMessageEmbeds(ReactionRemoved.build()).queue();
				ReactionRemoved.clear();
			}
		} catch (IOException e) {
			logger.error("IO-Fehler", e);
		} catch (Exception e) {
			logger.error("Fehler", e);
		}
	}
}
