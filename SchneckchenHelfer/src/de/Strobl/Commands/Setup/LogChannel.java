package de.Strobl.Commands.Setup;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;

import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class LogChannel {
	public static void setup(SlashCommandEvent event) {
		Logger logger = Main.logger;
		try {
			GuildChannel Channel = event.getOption("textchannel").getAsGuildChannel();
			String ChannelID = Channel.getId();
			if (Channel.getType() == ChannelType.TEXT) {
				TextChannel textchannel = event.getGuild().getTextChannelById(ChannelID);
				textchannel.sendMessage(event.getMember().getAsMention()).queue(sucess -> {
					try {
						Wini ini;
						ini = new Wini(new File(Main.Pfad + "settings.ini"));
						ini.put("Settings", "Settings.LogChannel", ChannelID);
						ini.store();
						EmbedBuilder Erfolg = new EmbedBuilder();
						Erfolg.setAuthor(event.getMember().getEffectiveName(), event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
						Erfolg.addField("LogChannel eingestellt: ", textchannel.getAsMention(), true);
						Erfolg.setColor(0x00c42b);
						Erfolg.setFooter("Eingestellt von: " + event.getMember().getEffectiveName());
						Erfolg.setTimestamp(ZonedDateTime.now().toInstant());
						textchannel.sendMessageEmbeds(Erfolg.build()).queue();
						Erfolg.clear();
						event.getHook().editOriginal("Erledigt.").queue();
					} catch (IOException e) {
						logger.error("IOFehler beim Ändern des Onlinestatus:", e);
						event.getHook().editOriginal("IOFehler beim Ändern des Onlinestatus.").queue();
					}
				}, failure -> {
					event.getHook().editOriginal("Fehler beim erstellen des LogChannels. Hat der Bot Berechtigungen in dem Channel zu schreiben?").queue();
					logger.error("Fehler beim erstellen des LogChannels:", failure);
				});
			} else {
				event.getHook().editOriginal("Der Ausgewählte Channel ist kein TextChannel, sondern ein: " + Channel.getType()).queue();
			}
		} catch (Exception e) {
			logger.error("Fehler beim Ändern des Onlinestatus:", e);
			event.getHook().editOriginal("Fehler beim Ändern des Onlinestatus.").queue();
		}
	}
}