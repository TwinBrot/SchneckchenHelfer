package de.Strobl.Commands.Funsies;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class SSPCommand {
	private static final Logger logger = LogManager.getLogger(SSPCommand.class);

	public static void sspstart(SlashCommandInteractionEvent event, InteractionHook eventHook) {
		try {
			String choice = event.getOption("auswahl").getAsString();
			int rand = new Random().nextInt(3);
			String random = "";
			if (rand == 0) {
				random = "Schere";
			}
			if (rand == 1) {
				random = "Stein";
			}
			if (rand == 2) {
				random = "Papier";
			}

			if (choice.equalsIgnoreCase(random)) {
				eventHook.editOriginal(random + "\n\n\nUnentschieden!!! Revanche?").queue();

			} else if ((choice.equals("schere") && random.equals("Papier")) || choice.equals("papier") && random.equals("Stein") || choice.equals("stein") && random.equals("Schere")) {
				eventHook.editOriginal(random + "\n\n\nDu hast gewonnen! Ich will eine Ravanche!").queue();

			} else {
				eventHook.editOriginal(random + "\n\n\nDu hast verloren! Na, willst du eine Ravanche?").queue();
			}

		} catch (Exception e) {
			logger.error("Fehler SSP", e);
		}
	}
}
