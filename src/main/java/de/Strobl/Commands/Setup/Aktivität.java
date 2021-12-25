package de.Strobl.Commands.Setup;

import java.awt.Color;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Aktivität {

	private static final Logger logger = LogManager.getLogger(Aktivität.class);

	public static void aktivität(SlashCommandEvent event) {
		try {

			String typ = Settings.AktivitätTyp;
			String text = Settings.AktivitätText;
			String URL = Settings.StreamLink;

			Settings.set("Settings", "AktivitätTyp", typ);
			Settings.set("Settings", "AktivitätTyp", text);

			if (typ.equals("playing")) {
				event.getJDA().getPresence().setActivity(Activity.playing(text));
			} else if (typ.equals("listening")) {
				event.getJDA().getPresence().setActivity(Activity.listening(text));
			} else if (typ.equals("watching")) {
				event.getJDA().getPresence().setActivity(Activity.watching(text));
			} else if (typ.equals("streaming")) {
				event.getJDA().getPresence().setActivity(Activity.streaming(text, URL));
			}
			Member self = event.getGuild().getSelfMember();
			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Aktivität eingestellt: " + typ, self.getId(), self.getEffectiveAvatarUrl());
			builder.setAuthor(event.getMember().getEffectiveName(), event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
			builder.addField("Text:", text, true);
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