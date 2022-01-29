package de.Strobl.Commands.Server;

import java.awt.Color;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Instances.Strafe;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Remove extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(Remove.class);
	public static void remove(SlashCommandInteractionEvent event, InteractionHook Hook) {
		try {
			String strafenID = event.getOption("id").getAsString();
			Strafe strafe;
			if ((strafe = Strafe.get(strafenID)) == null) {
				Hook.editOriginal("Konnte den Datensatz mit der ID " + strafenID + " nicht finden!").queue();
				return;
			};
			String typ = strafe.getTyp().toString();
			String userID = strafe.getUserId();
			String grund = strafe.getText();
			strafe.delete();
			event.getJDA().retrieveUserById(userID).queue(user -> {

				EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, typ + " " + strafenID + " gelöscht!", userID, user.getEffectiveAvatarUrl());
				builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
				builder.addField(typ + " Grund:", grund, true);
				
				Hook.editOriginal("User: " + user.getAsMention()).setEmbeds(builder.build()).queue();
				builder.clear();
				
			}, e -> {
				EmbedBuilder builder = Discord.standardEmbed(Color.RED, typ + " " + strafenID + " gelöscht!", userID, null);
				builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
				builder.setDescription("User konnte nicht erkannt werden. Datensatz wurde trotzdem gelöscht.");
				builder.addField("Grund", grund, true);
				Hook.editOriginal("").setEmbeds(builder.build()).queue();
				builder.clear();
				
			});
		} catch (Exception e) {
			Hook.editOriginal("Fehler beim entfernen").queue();
			logger.error("Fehler beim entfernen der Strafe:", e);
		}
	}
}