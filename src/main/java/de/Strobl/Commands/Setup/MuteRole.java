package de.Strobl.Commands.Setup;

import java.awt.Color;
import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class MuteRole {
	private static final Logger logger = LogManager.getLogger(MuteRole.class);

	public static void onSlashCommand(SlashCommandEvent event, InteractionHook Hook) {
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			EmbedBuilder builder = Discord.standardEmbed(Color.CYAN, "Mute-Rolle:", event.getGuild().getSelfMember().getId(), event.getGuild().getSelfMember().getEffectiveAvatarUrl());
			
			
			try {
				Role NewRole = event.getOption("muterolle").getAsRole();
				builder.addField("MuteRolle eingestellt:", NewRole.getAsMention(), true);
				ini.put("Settings", "Muterolle", NewRole.getId());
				ini.store();
			} catch (NullPointerException e) {
				builder.addField("Aktuelle MuteRolle:", "ID: " + ini.get("Settings", "Muterolle"), true);
			}
			builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
			Hook.editOriginal("").setEmbeds(builder.build()).queue();
		} catch (Exception e) {
			Hook.editOriginal("Fehler beim ändern der Muterolle").queue();
			logger.error("Fehler beim ändern der Muterolle:", e);
		}
	}
}