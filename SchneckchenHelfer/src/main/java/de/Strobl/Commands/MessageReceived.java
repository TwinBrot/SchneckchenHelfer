package de.Strobl.Commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Commands.Server.Info;
import de.Strobl.Instances.Discord;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReceived extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(MessageReceived.class);

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		try {
			String args[] = event.getMessage().getContentRaw().split("\\s");
//Commanderkennung
			if (!args[0].equals("*warns")) {
				return;
			}
			if (!(args.length == 2)) {
				event.getMessage().reply("Ungültige Anzahl Argumente").queue();
				return;
			}

//Console output
			logger.info("Befehl erkannt: _______________________________________________________________________");
			logger.info("Author: " + event.getMember());
			logger.info("*warns erkannt!");

//Nur Befehle aus Servern erlauben
			if (!event.isFromGuild()) {
				logger.warn("Befehl in Privatnachrichten, breche Ausführung ab!");
				return;
			}

//Nötigen Rechte vorhanden?
			if (!event.getGuild().getSelfMember().hasPermission(event.getGuildChannel(), Permission.MESSAGE_SEND,
					Permission.VIEW_CHANNEL)) {
				logger.error("Fehlende Berechtigung um auf Befehl zu antworten");
				logger.error("GuildChannel: " + event.getGuildChannel());
				logger.error("Permissions: " + event.getGuild().getSelfMember().getPermissions());
				return;
			}

// Modrollen Abfragen
// -1 = Fehler
// 0 = User
// 1 = Channelmod
// 2 = Mod
// 3 = Admin

			Integer Modrolle = Discord.isMod(event.getMember());
			if (Modrolle == 0) {
				event.getMessage().reply("Du hast nicht die notwendigen Rechte diesen Befehl auszuführen.").queue();
				return;
			} else if (Modrolle == -1) {
				event.getMessage().reply("Ausführung fehlgeschlagen. Wende dich bitte an Twin.").queue();
				return;
			}

//Getmember und retrieveuser und info Command auslösen
			Member member = Discord.getmember(event.getGuild(), args[1]);

			event.getJDA().retrieveUserById(args[1]).queue(user -> {
				Info.messagereceivedevent(event, member, user);
			}, e -> {
				event.getMessage().reply("Befehl konnte nicht ausgeführt werden. UserID richtig?").queue();
				logger.error("Fehler beim User-Retrieve", e);
			});

		} catch (Exception e) {
			logger.error("Info Command Error", e);
			event.getChannel().sendMessage("Fehler beim Auswerten").queue();
		}
	}
}