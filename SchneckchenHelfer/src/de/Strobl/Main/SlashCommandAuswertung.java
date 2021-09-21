package de.Strobl.Main;

import org.apache.logging.log4j.Logger;

import de.Strobl.Commands.Server.Emotes;
import de.Strobl.Commands.Server.Hinweis;
import de.Strobl.Commands.Server.Remove;
import de.Strobl.Commands.Server.UserInfo.Info;
import de.Strobl.Commands.Setup.Aktivität;
import de.Strobl.Commands.Setup.Dateiüberwachung;
import de.Strobl.Commands.Setup.LogChannel;
import de.Strobl.Commands.Setup.ModRolle;
import de.Strobl.Commands.Setup.Namensüberwachung;
import de.Strobl.Commands.Setup.Onlinestatus;
import de.Strobl.Exceptions.MissingPermException;
import de.Strobl.Instances.isMod;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class SlashCommandAuswertung extends ListenerAdapter {

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

//Console Output

			String CommandData = "Befehl: " + event.getName();
			if (event.getSubcommandGroup() != null) {
				CommandData = CommandData + "  SubCommandGroup: " + event.getSubcommandGroup();
			}
			if (event.getSubcommandName() != null) {
				CommandData = CommandData + "  SubCommandName: " + event.getSubcommandName();
			}
			CommandData = CommandData + "  Options: " + event.getOptions();

			logger.info("Befehl erkannt: _______________________________________________________________________");
			logger.info("Author: " + event.getMember());
			logger.info(CommandData);

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
					User user = event.getOption("user").getAsUser();
					String grundhinweis = event.getOption("grund").getAsString();
					Hinweis.hinweis(event, user, grundhinweis, EventHook);
					return;
				case "info":
					Member member = event.getOption("user").getAsMember();
					Info.slashcommandevent(event, member, EventHook);
					return;
				}
			}

//Moderator
			String SubCommandName;
			if (Modrolle > 1) {
				switch (event.getName()) {
				case "namen":
					SubCommandName = event.getSubcommandName();
					if (SubCommandName.equals("activate") || SubCommandName.equals("deactivate")) {
						Namensüberwachung.onoff(event, EventHook);
					} else if (SubCommandName.equals("add")) {
						Namensüberwachung.add(event, EventHook);
					} else if (SubCommandName.equals("remove")) {
						Namensüberwachung.remove(event, EventHook);
					} else if (SubCommandName.equals("list")) {
						Namensüberwachung.list(event, EventHook);
					}
					return;
				case "datei":
					SubCommandName = event.getSubcommandName();
					if (SubCommandName.equals("activate") || SubCommandName.equals("deactivate")) {
						Dateiüberwachung.onoff(event, EventHook);
					} else if (SubCommandName.equals("add")) {
						Dateiüberwachung.add(event, EventHook);
					} else if (SubCommandName.equals("remove")) {
						Dateiüberwachung.remove(event, EventHook);
					} else if (SubCommandName.equals("list")) {
						Dateiüberwachung.list(event, EventHook);
					}
					return;
				case "emotes":
					Emotes.emotes(event, EventHook);
					return;
				case "remove":
					Remove.remove(event, EventHook);
					return;
				case "help":
					return;
				case "kick":
					return;
				case "ban":
					return;
				case "changeban":
					return;
				}
			}

//Admin
			if (Modrolle > 2) {
				switch (event.getName()) {
				case "modrolle":
					if (event.getSubcommandName().equals("add")) {
						ModRolle.add(event, EventHook);
					} else if (event.getSubcommandName().equals("remove")) {
						ModRolle.remove(event, EventHook);
					} else if (event.getSubcommandName().equals("list")) {
						ModRolle.list(event, EventHook);
					}
					return;
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