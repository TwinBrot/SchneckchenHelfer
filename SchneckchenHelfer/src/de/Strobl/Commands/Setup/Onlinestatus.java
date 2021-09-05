package de.Strobl.Commands.Setup;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Onlinestatus {
	public static void change(SlashCommandEvent event) {
		Logger logger = Main.logger;
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
			
			EmbedBuilder Erfolg = new EmbedBuilder();
			Erfolg.setAuthor(event.getMember().getEffectiveName(), event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
			Erfolg.addField("Onlinestatus eingestellt: ", status, true);
			Erfolg.setColor(0x00c42b);
			event.getChannel().sendMessageEmbeds(Erfolg.build()).queue();
			Erfolg.clear();
			event.getHook().editOriginal("Erledigt").queue();
			
		} catch (IOException e) {
			logger.error("IOFehler beim Ändern des Onlinestatus:", e);
			event.getHook().editOriginal("IOFehler beim Ändern des Onlinestatus.").queue();
		} catch (Exception e) {
			logger.error("Fehler beim Ändern des Onlinestatus:", e);
			event.getHook().editOriginal("Fehler beim Ändern des Onlinestatus.").queue();
		}
	}
}