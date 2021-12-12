package de.Strobl.Commands.Server;

import java.awt.Color;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Help {
	private static final Logger logger = LogManager.getLogger(Help.class);
	public static void help (SlashCommandEvent event) {
		try {
			EmbedBuilder builder = Discord.standardEmbed(Color.ORANGE, "Hilf mir", event.getGuild().getSelfMember().getId(), event.getGuild().getSelfMember().getEffectiveAvatarUrl());
			builder.addField("Es gibt keine Hilfe. Die Hilfe ist verloren.", "Bei Fragen wende dich an Twin.", false);
			event.getHook().editOriginal("").setEmbeds(builder.build()).queue();
			
			
		} catch (Exception e) {
			logger.error("Fehler beim Anzeigen der Hilfe....dass das mal passiert 😥", e);
		}
	}
}
