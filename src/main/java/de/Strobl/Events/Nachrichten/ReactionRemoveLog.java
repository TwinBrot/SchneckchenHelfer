package de.Strobl.Events.Nachrichten;

import java.awt.Color;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionRemoveLog extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(ReactionRemoveLog.class);

	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		if (!event.isFromGuild()) {
			return;
		}
		
		try {
//Abfrage
			if (!event.getChannel().getId().equals("143488875182948353") && !event.getChannel().getId().equals("720439181696041122")) {

				// Nachrichtenlink Erzeugung:
				String GuildID = event.getGuild().getId().toString();
				String ChannelID = event.getChannel().getId();
				String MessageID = event.getMessageId();
				String Nachrichtenlink = "https://discordapp.com/channels/" + GuildID + "/" + ChannelID + "/" + MessageID;

				EmbedBuilder builder = Discord.standardEmbed(Color.CYAN, "Reaction Removed", event.getMember().getId(), event.getMember().getEffectiveAvatarUrl());
				try {
					builder.setThumbnail(event.getReactionEmote().getEmote().getImageUrl());
				} catch (Exception e) {
				}
				builder.addField("Emote:", event.getReactionEmote().getName(), true);
				builder.addField("Nachrichten Link:", "[Drück mich](" + Nachrichtenlink + ")", true);
				event.getGuild().getTextChannelById(Settings.LogChannel).sendMessage("User: " + event.getMember().getAsMention()).setEmbeds(builder.build()).allowedMentions(Collections.emptyList())
						.queue();
				builder.clear();
			}
		} catch (Exception e) {
			logger.error("Fehler", e);
		}
	}
}
