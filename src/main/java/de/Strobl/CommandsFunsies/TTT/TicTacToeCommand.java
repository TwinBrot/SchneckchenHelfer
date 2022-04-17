package de.Strobl.CommandsFunsies.TTT;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class TicTacToeCommand {
	private static final Logger logger = LogManager.getLogger(TicTacToeCommand.class);

	public static void tictactoestart(SlashCommandInteractionEvent event, InteractionHook eventHook) {
		try {
			int dif;
			String kidif = event.getOption("kistärke").getAsString();
			if (kidif.equals("leicht")) {
				dif = 1;
			} else if (kidif.equals("mittel")) {
				dif = 2;
			} else {
				dif = 3;
			}
			event.getHook().retrieveOriginal().queue(msg -> {
				try {
					TicTacToe ttt = new TicTacToe(event.getUser().getId(), null, dif, msg.getId(), null);
					event.getHook().editOriginal("").setActionRows(ttt.createButtons(false)).queue();

				} catch (Exception e) {
					logger.error("Fehler ttt command", e);
				}
			});

		} catch (Exception e) {
			logger.error("Fehler TicTacToe start", e);
		}
	}

	public static void tictactoebutton(ButtonInteractionEvent event, InteractionHook hook) {
		try {
			String eventname = event.getButton().getId().replace("TicTacToe_", "");
			if (eventname.equals("hilfe")) {
				event.reply("Die Regeln:").queue();
				return;
			}
			TicTacToe ttt = new TicTacToe(event.getUser().getId());
			if (ttt.player1 == null) {
				hook.editOriginal("Derzeit läuft bei dir kein Tic-Tac-Toe Spiel! Da ist scheinbar etwas schiefgelaufen!").queue();
				return;
			}
			if (ttt.turn == false && ttt.player1.equals(event.getUser().getId())) {
			}
			if (ttt.player2 != null) {
				if (ttt.turn == true && ttt.player2.equals(event.getUser().getId())) {
					hook.editOriginal("Dein Gegner ist gerade dran! Bitte warte bis dein Gegner gespielt hat!").queue();
				}
			}

			if (eventname.equals("surr")) {
				ttt.winner = !event.getUser().getId().equals(ttt.player1);
				ttt.finished = true;
				ttt.updateMessages();
				return;
			}

			int y = Integer.parseInt(eventname.substring(0, 1));
			int x = Integer.parseInt(eventname.substring(1, 2));
			ttt.playerMove(event.getUser().getId(), y, x);
			ttt.checkWin();
			if (ttt.finished) {

			}

		} catch (Exception e) {
			logger.error("Fehler TicTacToe Button", e);
		}
	}
}
