package de.Strobl.Commands.Setup;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Onlinestatus {
	private static final Logger logger = LogManager.getLogger(Onlinestatus.class);

	public static void change(SlashCommandEvent event) {
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));

			String status = event.getOption("onlinestatus").getAsString();

			ini.put("Settings", "Settings.Status", status);
			ini.store();

			if (status.equals("ONLINE")) {
				event.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
			} else if (status.equals("DO_NOT_DISTURB")) {
				event.getJDA().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
			} else if (status.equals("IDLE")) {
				event.getJDA().getPresence().setStatus(OnlineStatus.IDLE);
			} else if (status.equals("INVISIBLE")) {
				event.getJDA().getPresence().setStatus(OnlineStatus.INVISIBLE);
			}

			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Onlinestatus eingestellt: " + status,
					event.getGuild().getSelfMember().getId(), event.getGuild().getSelfMember().getEffectiveAvatarUrl());
			builder.setAuthor(event.getMember().getEffectiveName(), event.getUser().getAvatarUrl(),
					event.getUser().getAvatarUrl());
			event.getHook().editOriginal("").setEmbeds(builder.build()).queue();
			builder.clear();

		} catch (IOException e) {
			logger.error("IOFehler beim Ändern des Onlinestatus:", e);
			event.getHook().editOriginal("IOFehler beim Ändern des Onlinestatus.").queue();
		} catch (Exception e) {
			logger.error("Fehler beim Ändern des Onlinestatus:", e);
			event.getHook().editOriginal("Fehler beim Ändern des Onlinestatus.").queue();
		}
	}
}