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
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Aktivität {

	private static final Logger logger = LogManager.getLogger(Aktivität.class);

	public static void aktivität(SlashCommandEvent event) {
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));

			String typ = event.getOption("activitytyp").getAsString();
			String text = event.getOption("activitytext").getAsString();
			String URL = ini.get("Settings", "StreamLink");

			ini.put("Settings", "AktivitätTyp", typ);
			ini.put("Settings", "AktivitätText", text);
			ini.store();

			if (typ.equals("playing")) {
				event.getJDA().getPresence().setActivity(Activity.playing(text));
			} else if (typ.equals("listening")) {
				event.getJDA().getPresence().setActivity(Activity.listening(text));
			} else if (typ.equals("watching")) {
				event.getJDA().getPresence().setActivity(Activity.watching(text));
			} else if (typ.equals("streaming")) {
				event.getJDA().getPresence().setActivity(Activity.streaming(text, URL));
			}

			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Aktivität eingestellt: " + typ, event.getGuild().getSelfMember().getId(), event.getGuild().getSelfMember().getEffectiveAvatarUrl());
			builder.setAuthor(event.getMember().getEffectiveName(), event.getUser().getAvatarUrl(),	event.getUser().getAvatarUrl());
			event.getHook().editOriginal("").setEmbeds(builder.build()).queue();
			builder.clear();
		} catch (IOException e) {
			logger.error("IOFehler beim Ändern der Aktivity:", e);
			event.getHook().editOriginal("IOFehler beim Ändern der Aktivität.").queue();
		} catch (Exception e) {
			logger.error("Fehler beim Ändern der Aktivity:", e);
			event.getHook().editOriginal("Fehler beim Ändern der Aktivität.").queue();
		}
	}
}