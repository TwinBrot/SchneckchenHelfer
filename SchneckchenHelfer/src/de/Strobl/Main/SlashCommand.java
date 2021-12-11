package de.Strobl.Main;

import static net.dv8tion.jda.api.interactions.commands.OptionType.CHANNEL;
import static net.dv8tion.jda.api.interactions.commands.OptionType.ROLE;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.Strobl.Commands.Server.Emotes;
import de.Strobl.Commands.Server.Hinweis;
import de.Strobl.Commands.Server.Kick;
import de.Strobl.Commands.Server.Remove;
import de.Strobl.Commands.Server.Ban.Ban;
import de.Strobl.Commands.Server.UserInfo.Info;
import de.Strobl.Commands.Setup.Aktivität;
import de.Strobl.Commands.Setup.Dateiüberwachung;
import de.Strobl.Commands.Setup.LogChannel;
import de.Strobl.Commands.Setup.ModRolle;
import de.Strobl.Commands.Setup.Namensüberwachung;
import de.Strobl.Commands.Setup.Onlinestatus;
import de.Strobl.Instances.getMember;
import de.Strobl.Instances.isMod;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class SlashCommand extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(SlashCommand.class);

	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		InteractionHook EventHook = event.getHook();
		try {

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
			logger.info("Author: " + event.getMember());
			logger.info(CommandData);

// Only accept commands from Guilds

			if (!event.isFromGuild()) {
				event.reply("Diese Befehle funktionieren nur auf dem Server.").setEphemeral(true).queue();
				logger.info("Befehl in den Privatnachrichten erkannt. Abbruch");
				return;
			}
			
// Block Commands in Channels i can't Write in.

			if (!event.getGuild().getSelfMember().hasPermission(event.getGuildChannel(), Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL)) {
				event.reply("Ich habe nicht die nötigen Rechte, diesen Befehl auszuführen.").setEphemeral(true).queue();
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

			Integer Modrolle = isMod.check(event.getMember());
			if (Modrolle == 0) {
				event.reply("Du hast nicht die notwendigen Rechte diesen Befehl auszuführen.").setEphemeral(true).queue();
				return;
			} else if (Modrolle == -1) {
				event.reply("Bei der Ausführung ist ein Fehler aufgetreten. Wende dich bitte an Twin.").setEphemeral(true).queue();
				return;
			}

			

// Auslesen der Befehle
			event.deferReply(true).queue();
			Member member;
			String text;
// Channelmod
			if (Modrolle > 0) {
				switch (event.getName()) {
				case "hinweis":
					User user = event.getOption("user").getAsUser();
					String grundhinweis = event.getOption("grund").getAsString();
					Hinweis.hinweis(event, user, grundhinweis, EventHook);
					return;
				case "info":
					member = event.getOption("user").getAsMember();
					Info.slashcommandevent(event, member, EventHook);
					return;
				}
			}

// Moderator
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
				case "kick":
					member = getMember.getmember(event.getGuild(), event.getOption("user").getAsUser());
					try {
						text = event.getOption("grund").getAsString();
					} catch (NullPointerException e) {
						text = "";
					}
					if (member == null) {
						EventHook.editOriginal("Konnte den User nicht finden").queue();
						return;
					}
					Kick.onSlashCommand(event, member, text, EventHook);
					return;
				case "ban":
					member = getMember.getmember(event.getGuild(), event.getOption("user").getAsUser());
					try {
						text = event.getOption("grund").getAsString();
					} catch (NullPointerException e) {
						text = "";
					}
					if (member == null) {
						EventHook.editOriginal("Konnte den User nicht finden").queue();
						return;
					}
// TODO: Ban = Kick
					Ban.onSlashCommand(event, member, text, EventHook);
					return;
				case "help":
					return;
				case "changeban":
					return;
				}
			}

// Admin
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
			event.reply("Du hast nicht die notwendigen Rechte diesen Befehl auszuführen.").setEphemeral(false).queue();
			logger.error(event.getName());
		} catch (Exception e) {
			logger.error("Fehler beim auswerten des Befehls", e);
		}

	}

	public static void startupcheck(JDA jda) {
		try {
			ArrayList<CommandData> newlist = new ArrayList<CommandData>();

			newlist.add(onlinestatus());
			newlist.add(activity());
			newlist.add(name());
			newlist.add(datei());
			newlist.add(logchannel());
			newlist.add(modrolle());
			newlist.add(emotes());
			newlist.add(info());
			newlist.add(hinweis());
			newlist.add(kick());
			newlist.add(ban());
			newlist.add(changeban());
			newlist.add(remove());

			register(newlist, jda);

		} catch (Exception e) {
			logger.fatal("Fehler beim Aufnehmen der Befehle:", e);
		}
	}

	public static void register(ArrayList<CommandData> list, JDA jda) {
		try {
			CommandListUpdateAction commands = jda.updateCommands();

			for (CommandData command : list) {
				commands.addCommands(command);
			}
			commands.queue(success -> logger.info("Befehle wurden registriert: " + success),
					failure -> logger.fatal("Fehler beim Registrieren der Befehle:", failure));
		} catch (Exception e) {
			logger.fatal("Fehler beim Registrieren der Befehle:", e);
		}
	}
	
	private static CommandData onlinestatus() {
		return new CommandData("onlinestatus", "Ändert den Onlinestatus des Bots")
				.addOptions(new OptionData(STRING, "onlinestatus", "Legt den Onlinestatus fest.").addChoice("online", "ONLINE")
						.addChoice("nichtstören", "DO_NOT_DISTURB").addChoice("abwesend", "IDLE").addChoice("unsichtbar", "INVISIBLE")
						.setRequired(true));
	}
	private static CommandData activity() {
		return new CommandData("activity", "Konfiguriert die Aktivität des Bots")
				.addOptions(new OptionData(STRING, "activitytyp", "Typ der Activity auswählen.").addChoice("playing", "playing")
						.addChoice("watching", "watching").addChoice("listening", "listening").addChoice("streaming", "streaming").setRequired(true))
				.addOptions(new OptionData(STRING, "activitytext", "Konfiguriert den Text der Activity").setRequired(true));
	}
	private static CommandData name() {
		return new CommandData("namen", "Konfiguriert die Namensüberwachung")
				.addSubcommands(new SubcommandData("activate", "Namensüberwachung aktivieren"))
				.addSubcommands(new SubcommandData("deactivate", "Namensüberwachung deaktivieren"))
				.addSubcommands(new SubcommandData("add", "Verbotene Namen hinzufügen")
						.addOptions(new OptionData(STRING, "name", "Hier verbotenen Namen angeben.").setRequired(true)))
				.addSubcommands(new SubcommandData("remove", "Verbotene Namen entfernen")
						.addOptions(new OptionData(STRING, "name", "Hier Namen angeben.").setRequired(true)))
				.addSubcommands(new SubcommandData("list", "Liste der Verbotenen Namen"));
	}
	private static CommandData datei() {
		return new CommandData("datei", "Konfiguriert die Dateiüberwachung")
				.addSubcommands(new SubcommandData("activate", "Namensüberwachung aktivieren"))
				.addSubcommands(new SubcommandData("deactivate", "Namensüberwachung deaktivieren"))
				.addSubcommands(new SubcommandData("add", "Verbotene Namen hinzufügen")
						.addOptions(new OptionData(STRING, "name", "Hier verbotenen Namen angeben.").setRequired(true)))
				.addSubcommands(new SubcommandData("remove", "Verbotene Namen entfernen")
						.addOptions(new OptionData(STRING, "name", "Hier Namen angeben.").setRequired(true)))
				.addSubcommands(new SubcommandData("list", "Liste der Verbotenen Namen"));
	}
	private static CommandData logchannel() {
		return new CommandData("logchannel", "Legt den Kanal fest, in dem die Alarme gepostet werden.")
				.addOptions(new OptionData(CHANNEL, "textchannel", "Wähle den Textchannel aus.").setRequired(true));
	}
	private static CommandData modrolle() {
		return new CommandData("modrolle", "Konfiguriert die Modrollen")
				.addSubcommands(new SubcommandData("add", "Modrolle hinzufügen")
						.addOptions(new OptionData(ROLE, "rolle", "Hier die gewünschte Rolle angeben.").setRequired(true))
						.addOptions(new OptionData(STRING, "zugriffsstufe", "Welche Stufe soll die Rolle haben?").setRequired(true)
								.addChoice("Admin", "Admin").addChoice("Mod", "Mod").addChoice("ChannelMod", "Channelmod")))
				.addSubcommands(new SubcommandData("remove", "Verbotene Dateiendungen entfernen")
						.addOptions(new OptionData(ROLE, "rolle", "Hier die gewünschte Rolle angeben.").setRequired(true)))
				.addSubcommands(new SubcommandData("list", "Listet alle Modrollen auf."));
	}
	private static CommandData emotes() {
		return new CommandData("emotes", "Startet die Emoteauswertung");
	}
	private static CommandData info() {
		return new CommandData("info", "Ruft Informationen über einen User ab.")
				.addOptions(new OptionData(USER, "user", "Wähle hier den User aus.").setRequired(true));
	}
	private static CommandData hinweis() {
		return new CommandData("hinweis", "Schickt einem User einen Hinweis")
				.addOptions(new OptionData(USER, "user", "Wähle hier den User aus.").setRequired(true))
				.addOptions(new OptionData(STRING, "grund", "Gib hier den Hinweis-Text an.").setRequired(true));
	}
	private static CommandData kick() {
		return new CommandData("kick", "Kickt den ausgewählten User")
				.addOptions(new OptionData(USER, "user", "Wähle hier den User aus.").setRequired(true))
				.addOptions(new OptionData(STRING, "grund", "Gib hier den Grund des Kicks an.").setRequired(true));
	}
	private static CommandData ban() {
		return new CommandData("ban", "Bannt den ausgewählten User")
				.addOptions(new OptionData(USER, "user", "Wähle den zu bannenden User aus.").setRequired(true))
				.addOptions(new OptionData(STRING, "grund", "Gib hier den Grund des Bans an.").setRequired(true));
	}
	private static CommandData changeban() {
		return new CommandData("changeban", "Ändert die Dauer eines Tempbans.")
				.addOptions(new OptionData(STRING, "user", "Gib die ID des Users an").setRequired(true))
				.addOptions(new OptionData(STRING, "dauer", "Gib hier die neue Dauer des TempBans an").setRequired(true));
	}
	private static CommandData remove() {
		return new CommandData("remove", "Entfernt gespeicherte Strafen aus dem Log.")
				.addOptions(new OptionData(STRING, "id", "Eindeutige ID der Strafe"));
	}
}