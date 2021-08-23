package de.Strobl.Commands.Setup;

import java.io.File;

import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Aktivität {
	public static Wini ini;

	public static void aktivität(SlashCommandEvent event) {
		try {
			ini = new Wini(new File(Main.Pfad + "settings.ini"));
			
			String typ = event.getOption("activitytyp").getAsString();
			String text = event.getOption("activitytext").getAsString();
			String URL = ini.get("Settings", "Settings.StreamLink");
			
			ini.put("Settings", "Settings.AktivitätTyp", typ);
			ini.put("Settings", "Settings.AktivitätText", text);
			ini.store();
			
			EmbedBuilder Erfolg = new EmbedBuilder();
			Erfolg.setAuthor(event.getMember().getEffectiveName(), event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
			Erfolg.addField("Aktivität eingestellt: ", typ, true);
			Erfolg.addField("", text, true);
			Erfolg.setColor(0x00c42b);
			event.getChannel().sendMessageEmbeds(Erfolg.build()).queue();
			Erfolg.clear();

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

		} catch (Exception e) {
			e.printStackTrace();
			event.getHook().editOriginal("Irgendwas ist schief gegangen.").queue();
		}
	}
}