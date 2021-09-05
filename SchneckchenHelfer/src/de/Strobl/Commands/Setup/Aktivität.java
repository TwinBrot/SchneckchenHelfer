package de.Strobl.Commands.Setup;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Aktivität {

	public static void aktivität(SlashCommandEvent event) {
		Logger logger = Main.logger;
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			
			String typ = event.getOption("activitytyp").getAsString();
			String text = event.getOption("activitytext").getAsString();
			String URL = ini.get("Settings", "Settings.StreamLink");
			
			ini.put("Settings", "Settings.AktivitätTyp", typ);
			ini.put("Settings", "Settings.AktivitätText", text);
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
			event.getHook().editOriginal("Erledigt").queue();
			
			EmbedBuilder Erfolg = new EmbedBuilder();
			Erfolg.setAuthor(event.getMember().getEffectiveName(), event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
			Erfolg.addField("Aktivität eingestellt: ", typ, true);
			Erfolg.addField("", text, true);
			Erfolg.setColor(0x00c42b);
			event.getChannel().sendMessageEmbeds(Erfolg.build()).queue();
			Erfolg.clear();

		} catch (IOException e) {
			logger.error("IOFehler beim Ändern der Aktivity:", e);
			event.getHook().editOriginal("IOFehler beim Ändern der Aktivität.").queue();
		} catch (Exception e) {
			logger.error("Fehler beim Ändern der Aktivity:", e);
			event.getHook().editOriginal("Fehler beim Ändern der Aktivität.").queue();
		}
	}
}