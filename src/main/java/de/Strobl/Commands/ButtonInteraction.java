package de.Strobl.Commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Commands.Buttons.BanButton;
import de.Strobl.Commands.Funsies.TicTacToeCommand;
import de.Strobl.Commands.Funsies.WordleCommand;
import de.Strobl.Instances.Discord;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class ButtonInteraction extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(ButtonInteraction.class);

	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		try {
			InteractionHook hook = event.getHook();
// Console Output
			String componentID = event.getComponentId();
			logger.info("Button erkannt: _______________________________________________________________________");
			if (event.getMember() == null) {
				logger.info("Author: " + event.getUser());
			} else {
				logger.info("Author: " + event.getMember());
			}
			logger.info(componentID);

// Only from Guilds
			if (!event.isFromGuild()) {
				if (event.getButton().getId().startsWith("wordle")) {
					WordleCommand.wordlebuttonclick(event);
				}
				if (event.getButton().getId().startsWith("TicTacToe_")) {
					TicTacToeCommand.tictactoebutton(event, hook);
				}
				return;
			}

// Modrollen Abfragen
// -1 = Fehler
// 0 = User
// 1 = Channelmod
// 2 = Mod
// 3 = Admin
			Integer Modrolle = Discord.isMod(event.getMember());
			if (Modrolle == -1) {
				event.reply("Fehler beim Auslesen der Modrolle.").queue();
				return;
			}
			if (Modrolle < 0) {
				event.reply("Nicht die nötigen Rechte!").queue();
				logger.info("Befehl wurde abgebrochen, Fehlende Rechte.");
			}

// Block Commands in Channels i can't Write in.
			if (!event.getGuild().getSelfMember().hasPermission(event.getGuildChannel(), Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL)) {
				event.reply("Ich habe nicht die nötigen Rechte, diesen Befehl auszuführen.").setEphemeral(true).queue();
				logger.error("GuildChannel: " + event.getGuildChannel());
				logger.error("Permissions: " + event.getGuild().getSelfMember().getPermissions());
				return;
			}
			event.deferEdit().queue();

			String[] befehl = componentID.split("\\s");

// Channelmod
			if (Modrolle > 0) {
			}
//Mod
			if (Modrolle > 1) {
				switch (befehl[0]) {
				case "ban":
					BanButton.bancheck(event, hook, befehl[1]);
					return;
				case "finalban":
					BanButton.banchecked(event, hook, befehl[1]);
					return;
				}
			}
//Admin
			if (Modrolle > 2) {
			}

			event.reply("Du hast nicht die nötigen Rechte diesen Button zu drücken.").queue();
			logger.info("Nicht die nötigen Rechte um diesen Befehl auszuführen.");

		} catch (Exception e) {
			event.reply("Fehler beim Auswerten des Buttons.").queue();
			logger.error("Fehler Button Auswertung", e);
		}
	}
}
