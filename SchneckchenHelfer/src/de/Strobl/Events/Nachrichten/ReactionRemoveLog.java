package de.Strobl.Events.Nachrichten;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Instances.Discord;
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
			if (!event.getChannel().getId().equals("143488875182948353")
					&& !event.getChannel().getId().equals("720439181696041122")) {

				Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
				String ID = ini.get("Settings", "Settings.LogChannel");

				// Nachrichtenlink Erzeugung:
				String GuildID = event.getGuild().getId().toString();
				String ChannelID = event.getTextChannel().getId();
				String MessageID = event.getMessageId();
				String Nachrichtenlink = "https://discordapp.com/channels/" + GuildID + "/" + ChannelID + "/"
						+ MessageID;

				EmbedBuilder builder = Discord.standardEmbed(Color.CYAN, "Reaction Removed", event.getMember().getId(),
						event.getMember().getEffectiveAvatarUrl());
				try {
					builder.setThumbnail(event.getReactionEmote().getEmote().getImageUrl());
				} catch (Exception e) {
				}
				builder.addField("Emote:", event.getReactionEmote().getName(), true);
				builder.addField("Nachrichten Link:", "[Drück mich](" + Nachrichtenlink + ")", true);
				event.getGuild().getTextChannelById(ID).sendMessage("User: " + event.getMember().getAsMention())
						.setEmbeds(builder.build()).allowedMentions(Collections.emptyList()).queue();
				builder.clear();
			}
		} catch (IOException e) {
			logger.error("IO-Fehler", e);
		} catch (Exception e) {
			logger.error("Fehler", e);
		}
	}
}
