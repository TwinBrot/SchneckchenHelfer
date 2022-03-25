package de.Strobl.Commands.Funsies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class SlashCommandFunsies extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(SlashCommandFunsies.class);

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		InteractionHook EventHook = event.getHook();
		try {

// Only accept commands from DMs

			if (event.isFromGuild()) {
				return;
			}
			event.deferReply(false).queue();

// Console Output
			String CommandData = "Befehl: " + event.getName();
			if (event.getSubcommandGroup() != null) {
				CommandData = CommandData + "  SubCommandGroup: " + event.getSubcommandGroup();
			}
			if (event.getSubcommandName() != null) {
				CommandData = CommandData + "  SubCommandName: " + event.getSubcommandName();
			}
			CommandData = CommandData + "  Options: " + event.getOptions();

			logger.info("Befehl erkannt: _______________________________________________________________________");
			logger.info("Author: " + event.getUser());
			logger.info(CommandData);

// Auslesen der Befehle
// Funsies

			if (event.getName().equals("wordle")) {
				WordleCommand.wordlestart(event, EventHook);
				return;
			}

			if (event.getName().equals("ssp")) {
				SSPCommand.sspstart(event, EventHook);
				return;
			}

			if (event.getName().equals("tictactoe")) {
				TicTacToeCommand.tictactoestart(event, EventHook);
				return;
			}

		} catch (Exception e) {
			EventHook.editOriginal("Es ist etwas schiefgelaufen. Bitte wende dich an Twin.").queue();
			logger.error("Fehler beim auswerten des Befehls", e);
		}

	}
}