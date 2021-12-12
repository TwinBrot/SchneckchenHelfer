package de.Strobl.Events.Nachrichten;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ScamDetectionLink extends ListenerAdapter {
	public Wini ini;
	public Wini Link;
	public Section Links;
	private static final Logger logger = LogManager.getLogger(ScamDetectionLink.class);

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!event.isFromGuild()) {
			return;
		}
		try {
			ini = new Wini(new File(Main.Pfad + "settings.ini"));
			Link = new Wini(new File(Main.Pfad + "Link.ini"));
			Links = Link.get("Links");
			String message = event.getMessage().getContentRaw();
			for (int i = 1; i <= Links.size(); i++) {
				if (message.toLowerCase().contains(Links.get(i + "").toLowerCase())) {
					event.getMessage().delete().queue(success -> {
						String LogChannel = ini.get("Settings", "Settings.LogChannel");
						Guild guild = event.getGuild();
						EmbedBuilder builder = Discord.standardEmbed(Color.red,
								"Nachricht gelöscht! Unerlaubter Link erkannt!", event.getMember().getId(),
								event.getMember().getEffectiveAvatarUrl());
						builder.addField("Nachrichten Inhalt:", message, false);
						guild.getTextChannelById(LogChannel)
								.sendMessage("User: " + event.getMember().getAsMention()
										+ " Notification: <@227131380058947584> <@140206875596685312>")
								.setEmbeds(builder.build()).queue();
						builder.clear();
					}, e -> {
						logger.error("ScamDetection fehler", e);
					});
				}
			}
		} catch (

		IOException e) {
			logger.error("IO-Fehler", e);
		} catch (Exception e) {
			logger.error("Fehler", e);
		}
	}
}