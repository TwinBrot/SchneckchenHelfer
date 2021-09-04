package de.Strobl.Commands.Server;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Remove extends ListenerAdapter {

	public void onSlashCommand(SlashCommandEvent event) {
		Logger logger = Main.logger;
		try {
			Wini ID = new Wini(new File(Main.Pfad + "ID.ini"));

			System.out.println(event.getOption("").getAsString());
			System.out.println(ID.get(event.getOption("").getAsString()));
			
			
			
			
			
			
			
			
			
			
			
			
		} catch (IOException e) {
			logger.error("Fehler beim entfernen der Strafe:", e);
		} catch (Exception e) {
			logger.error("Fehler beim entfernen der Strafe:", e);
		}

	}
}