package de.Strobl.Main;

import org.apache.logging.log4j.Logger;

import de.Strobl.Commands.Server.Emotes;
import de.Strobl.Commands.Server.Hinweis;
import de.Strobl.Commands.Server.UserInfo.Info;
import de.Strobl.Commands.Setup.Aktivität;
import de.Strobl.Instances.getMember;
import de.Strobl.Instances.isMod;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class BefehleAuswertung extends ListenerAdapter {

	public void onSlashCommand(SlashCommandEvent event) {
		Logger logger = Main.logger;
		try {
			event.deferReply(true).queue();
			InteractionHook EventHook = event.getHook();

// Only accept commands from guilds

			if (!event.isFromGuild()) {
				EventHook.editOriginal("Diese Befehle funktionieren nur auf dem Server.").queue();
				logger.info("Befehl in den Privatnachrichten erkannt. Abbruch");
				return;
			}
			logger.info("Befehl erkannt:");
			logger.info("Author: " + event.getMember());
			logger.info("Befehl: " + event.getName() + "   " + event.getOptions());

// Modrollen Abfragen
//-1 = Fehler		
//0 = User
//1 = Channelmod
//2 = Mod
//3 = Admin

			Integer Modrolle = isMod.check(event, EventHook);
			if (Modrolle == 0) {
				EventHook.editOriginal("Du hast nicht die notwendigen Rechte diesen Befehl auszuführen.").queue();
				return;
			} else if (Modrolle == -1) {
				EventHook.editOriginal(
						"Bei der Ausführung ist ein Fehler aufgetreten. Versuche es bitte erneut. Wenn das Problem dadurch nicht behoben wird, wende dich bitte an Twin.")
						.queue();
				return;
			}

//Auslesen der Befehle

//Channelmod
			if (Modrolle > 0) {
				switch (event.getName()) {
				case "hinweis":
					Member member = getMember.getmember(event, event.getOption("user").getAsString(), EventHook);
					String grundhinweis = event.getOption("grund").getAsString();
					if (member == null) {
						EventHook.editOriginal("User nicht erkannt").queue();
						return;
					}
					if (event.getJDA().getSelfUser() == member.getUser()) {
						EventHook.editOriginal(
								"Du kannst dem " + event.getJDA().getSelfUser().getName() + " keinen Hinweis schicken.")
								.queue();
						return;
					}
					;
					Hinweis.hinweis(event, member, grundhinweis, EventHook);
					return;

				case "hinweisn":
					String grundhinweisn = event.getOption("grund").getAsString();
					member = event.getOption("user").getAsMember();
					if (event.getJDA().getSelfUser() == member.getUser()) {
						EventHook.editOriginal(
								"Du kannst dem " + event.getJDA().getSelfUser().getName() + " keinen Hinweis schicken.")
								.queue();
						return;
					}
					;
					Hinweis.hinweis(event, member, grundhinweisn, EventHook);
					return;

				case "info":
					member = getMember.getmember(event, event.getOption("userid").getAsString(), EventHook);
					if (!(member == null)) {
						Info.info(event, member, EventHook);
					}
					return;

				case "infon":
					member = event.getOption("user").getAsMember();
					Info.info(event, member, EventHook);
					return;
				}
			}

//Moderator

			if (Modrolle > 1) {
				switch (event.getName()) {
				case "modrolle":
					return;
				case "namen":
					return;
				case "datei":
					return;
				case "emotes":
					Emotes.emotes(event);
					return;
				case "help":
					return;
				case "kick":
					return;
				case "ban":
					return;
				case "changeban":
					return;
				case "remove":
					
					
					
					
					return;
				}
			}

//Admin

			if (Modrolle > 2) {
				switch (event.getName()) {
				case "modrolle":
					return;
				case "onlinestatus":
					return;
				case "activity":
					Aktivität.aktivität(event);
					return;
				case "afkchannel":
					return;
				case "logchannel":
					return;
				}
			}
			EventHook.sendMessage("Befehl nicht gefunden. Bitte klär das mit Twin.").setEphemeral(false).queue();
			logger.error(event.getName());
		} catch (Exception e) {
			logger.error("Fehler beim auswerten des Befehls", e);
		}

	}
}