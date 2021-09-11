package de.Strobl.Main;

import org.apache.logging.log4j.Logger;

import de.Strobl.Commands.Server.Emotes;
import de.Strobl.Commands.Server.Hinweis;
import de.Strobl.Commands.Server.Remove;
import de.Strobl.Commands.Server.UserInfo.Info;
import de.Strobl.Commands.Setup.Aktivität;
import de.Strobl.Commands.Setup.LogChannel;
import de.Strobl.Commands.Setup.ModRolle;
import de.Strobl.Commands.Setup.Onlinestatus;
import de.Strobl.Exceptions.MissingPermException;
import de.Strobl.Instances.getMember;
import de.Strobl.Instances.isMod;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class BefehleAuswertung extends ListenerAdapter {

	public void onSlashCommand(SlashCommandEvent event) {
		Logger logger = Main.logger;
		InteractionHook EventHook = event.getHook();
		try {
			event.deferReply(true).queue();

// Only accept commands from guilds

			if (!event.isFromGuild()) {
				EventHook.editOriginal("Diese Befehle funktionieren nur auf dem Server.").queue();
				logger.info("Befehl in den Privatnachrichten erkannt. Abbruch");
				return;
			}
			logger.info("Befehl erkannt:");
			logger.info("Author: " + event.getMember());
			logger.info("Befehl: " + event.getName() + "   " + event.getOptions());

// Block Commands in Channels i cant Write in.

			if (!event.getGuild().getSelfMember().hasPermission(event.getGuildChannel(), Permission.MESSAGE_WRITE, Permission.MESSAGE_READ)) {
				EventHook.editOriginal("Ich habe nicht die nötigen Rechte, diesen Befehl auszuführen.").queue();
				logger.error("GuildChannel: " + event.getGuildChannel());
				logger.error("Permissions: " + event.getGuild().getSelfMember().getPermissions());
				throw new MissingPermException("MISSING_PERM_EXCEPTION", "Kann keine Nachricht in " + event.getChannel().getName() + " schreiben.");
			}


// Modrollen Abfragen
//-1 = Fehler		
//0 = User
//1 = Channelmod
//2 = Mod
//3 = Admin

			Integer Modrolle = isMod.check(event.getMember());
			if (Modrolle == 0) {
				EventHook.editOriginal("Du hast nicht die notwendigen Rechte diesen Befehl auszuführen.").queue();
				return;
			} else if (Modrolle == -1) {
				EventHook.editOriginal("Bei der Ausführung ist ein Fehler aufgetreten. Wende dich bitte an Twin.").queue();
				return;
			}


//Auslesen der Befehle

//Channelmod
			if (Modrolle > 0) {
				switch (event.getName()) {
				case "hinweis":
					Member member = getMember.getmember(event.getGuild(), event.getOption("user").getAsString());
					EventHook.editOriginal("User nicht erkannt").queue();
					String grundhinweis = event.getOption("grund").getAsString();
					if (member == null) {
						EventHook.editOriginal("User nicht erkannt").queue();
						return;
					}
					if (event.getJDA().getSelfUser() == member.getUser()) {
						EventHook.editOriginal("Du kannst dem " + event.getJDA().getSelfUser().getName() + " keinen Hinweis schicken.").queue();
						return;
					}
					;
					Hinweis.hinweis(event, member, grundhinweis, EventHook);
					return;

				case "info":
					member = event.getOption("user").getAsMember();
					Info.info(event, member, EventHook);
					return;
				}
			}

//Moderator

			if (Modrolle > 1) {
				switch (event.getName()) {
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
					Remove.remove(event, event.getHook());
					return;
				}
			}

//Admin
			if (Modrolle > 2) {
				switch (event.getName()) {
				case "modrolle":

					switch (event.getSubcommandName()) {
					case "add":
						ModRolle.add(event, event.getHook());
						return;
					case "remove":
						ModRolle.remove(event, event.getHook());
						return;
					case "list":
						ModRolle.list(event, event.getHook());
						return;
					default:
						return;
					}
				case "onlinestatus":
					Onlinestatus.change(event);
					return;
				case "activity":
					Aktivität.aktivität(event);
					return;
				case "logchannel":
					LogChannel.setup(event);
					return;
				}
			}
			EventHook.sendMessage("Befehl nicht gefunden. Bitte klär das mit Twin.").setEphemeral(false).queue();
			logger.error(event.getName());
		} catch (MissingPermException e) {
			logger.error("Fehlende Berechtigungen:", e);
		} catch (Exception e) {
			logger.error("Fehler beim auswerten des Befehls", e);
		}

	}
}