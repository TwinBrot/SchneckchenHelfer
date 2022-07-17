package de.Strobl.CommandsMod.Setup;

import java.awt.Color;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class LogChannel {
	private static final Logger logger = LogManager.getLogger(LogChannel.class);

	public static void setup(SlashCommandInteractionEvent event) {
		try {
			GuildChannel Channel = event.getOption("textchannel").getAsChannel();
			if (Channel.getType() == ChannelType.TEXT) {
				String ChannelID = Channel.getId();
				TextChannel textchannel = event.getGuild().getTextChannelById(ChannelID);
				textchannel.sendMessage(event.getMember().getAsMention()).queue(sucess -> {
					try {
						Settings.set("Settings", "LogCHannel", ChannelID);

						EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "LogChannel eingestellt: " + "`" + textchannel.getName() + "`", event.getGuild().getSelfMember().getId(),
								event.getGuild().getSelfMember().getEffectiveAvatarUrl());
						builder.setAuthor(event.getMember().getEffectiveName(), event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
						event.getHook().editOriginal("").setEmbeds(builder.build()).queue();
						builder.clear();

					} catch (IOException e) {
						logger.error("IOFehler beim Ändern des LogChannel:", e);
						event.getHook().editOriginal("IOFehler beim Ändern des LogChannel.").queue();
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