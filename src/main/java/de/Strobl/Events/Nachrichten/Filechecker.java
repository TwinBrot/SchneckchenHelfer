package de.Strobl.Events.Nachrichten;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Filechecker extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(Filechecker.class);

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!event.isFromGuild()) {
			return;
		}

		try {
			Boolean inactive = !Settings.DateiActive;
			Boolean Bot = event.getAuthor().isBot();
			Boolean NoFile = event.getMessage().getAttachments().isEmpty();
			if (inactive) {
				return;
			} else if (Bot) {
				return;
			} else if (NoFile) {
				return;
			}

			String[] Endungen = Settings.Datei;
			ArrayList<String> DateiEndungen = new ArrayList<String>();
			for (int i = 0; i < Endungen.length; i++) {
				DateiEndungen.add(i, Endungen[i]);
			}

			List<Attachment> Dateien = event.getMessage().getAttachments();
//Alle Dateiendungen kontrollieren
			for (int i = 0; i < Dateien.size(); i++) {

//Kontrolle ob Endung erlaubt / Endung vorhanden
				boolean delete = false;
				if (Dateien.get(i).getFileExtension() == null) {
					delete = true;
				} else if (!DateiEndungen.contains(Dateien.get(i).getFileExtension().toLowerCase())) {
					delete = true;
				}

//Delete Message, if User is not Mod
				if (delete) {
					Member member = event.getMember();
					if (Discord.isMod(member) > 0) {
						logger.info("Nicht erlaubte Dateiendung erkannt. Author ist Mod, daher nicht gelöscht.");
						return;
					}
					event.getMessage().delete().queue(success -> {
						member.getUser().openPrivateChannel().queue(pc -> {
							EmbedBuilder userinfo = Discord.standardEmbed(Color.RED, "Deine Nachrichten wurde automatisch gelöscht!", member.getId(), member.getEffectiveAvatarUrl());
							userinfo.addField("Grund:", "Unerlaubte Dateiendung erkannt \n Versuch es am besten mit einer anderen Dateiendung.", true);
							pc.sendMessageEmbeds(userinfo.build()).queue(null, e -> {
							});
							userinfo.clear();
						});
						EmbedBuilder builder = Discord.standardEmbed(Color.RED, "Unerlaubte Dateiendung erkannt. Nachricht gelöscht!", member.getId(), member.getEffectiveAvatarUrl());
						builder.addField("Channel:", event.getChannel().getAsMention(), true);
						builder.addField("DateiName:", event.getMessage().getAttachments().get(0).getFileName(), true);
						event.getGuild().getTextChannelById(Settings.LogChannel).sendMessage("User: " + event.getMember().getAsMention() + " Notification: <@227131380058947584>")
								.setEmbeds(builder.build()).queue();
						builder.clear();
					}, e -> {
						if (!e.getClass().getName().equals("net.dv8tion.jda.api.exceptions.ErrorResponseException")) {
							logger.error("Fehler bei der Dateiüberwachung", e);
						}
					});
					return;
				}
			}
		} catch (Exception e) {
			logger.error("Fehler Dateiüberwachung:", e);
		}
	}
}