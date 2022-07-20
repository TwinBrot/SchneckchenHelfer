package de.Strobl.CommandsMod;

import static net.dv8tion.jda.api.interactions.commands.OptionType.CHANNEL;
import static net.dv8tion.jda.api.interactions.commands.OptionType.ROLE;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import de.Strobl.CommandsFunsies.Wordle.WordleCommand;
import de.Strobl.CommandsMod.Server.Ban;
import de.Strobl.CommandsMod.Server.ChangeTemp;
import de.Strobl.CommandsMod.Server.Emotes;
import de.Strobl.CommandsMod.Server.Hinweis;
import de.Strobl.CommandsMod.Server.Info;
import de.Strobl.CommandsMod.Server.Kick;
import de.Strobl.CommandsMod.Server.Mute;
import de.Strobl.CommandsMod.Server.Remove;
import de.Strobl.CommandsMod.Server.Warn;
import de.Strobl.CommandsMod.Setup.Aktivität;
import de.Strobl.CommandsMod.Setup.AnnounceChannel;
import de.Strobl.CommandsMod.Setup.Dateiüberwachung;
import de.Strobl.CommandsMod.Setup.LogChannel;
import de.Strobl.CommandsMod.Setup.ModRolle;
import de.Strobl.CommandsMod.Setup.Namensüberwachung;
import de.Strobl.CommandsMod.Setup.Onlinestatus;
import de.Strobl.Instances.Discord;
import de.Strobl.Instances.StrafeTemp;
import de.Strobl.Instances.StrafenTyp;
import de.Strobl.Main.Settings;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class SlashCommand extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(SlashCommand.class);

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		// Only accept commands from Guilds
		if (!event.isFromGuild()) {
			return;
		}
		event.deferReply(false).queue();
		InteractionHook hook = event.getHook();
		try {

			// Console Output
			String CommandData = "Mod-Befehl: " + event.getName();
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

			// Block Commands in Channels i can't Write in.

			if (!event.getGuild().getSelfMember().hasPermission(event.getGuildChannel(), Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL)) {
				hook.editOriginal("Ich habe nicht die nötigen Rechte, diesen Befehl auszuführen.").queue();
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
				hook.editOriginal("Du hast nicht die notwendigen Rechte diesen Befehl auszuführen.").queue();
				return;
			} else if (Modrolle == -1) {
				hook.editOriginal("Bei der Ausführung ist ein Fehler aufgetreten. Wende dich bitte an Twin.").queue();
				return;
			}

			// Auslesen der Befehle
			// Channelmod
			if (Modrolle > 0) {
				if (event.getName().equals("hinweis")) {
					User user = event.getOption("user").getAsUser();
					String grundhinweis = event.getOption("grund").getAsString();
					Hinweis.SlashCommandInteraction(event, user, grundhinweis, hook);
					return;
				} else if (event.getName().equals("info")) {
					User user = event.getOption("user").getAsUser();
					Info.slashcommandevent(event, user, hook);
					return;
				}
			}

			// Modrolle: Moderator
			if (Modrolle > 1) {
				// Setup-Commands

				if (event.getName().equals("namen")) {
					String SubCommandName = event.getSubcommandName();
					if (SubCommandName.equals("activate") || SubCommandName.equals("deactivate")) {
						Namensüberwachung.onoff(event, hook);
					} else if (SubCommandName.equals("add")) {
						Namensüberwachung.add(event, hook);
					} else if (SubCommandName.equals("remove")) {
						Namensüberwachung.remove(event, hook);
					} else if (SubCommandName.equals("list")) {
						Namensüberwachung.list(event, hook);
					}
					return;
				} else if (event.getName().equals("datei")) {
					String SubCommandName = event.getSubcommandName();
					if (SubCommandName.equals("activate") || SubCommandName.equals("deactivate")) {
						Dateiüberwachung.onoff(event, hook);
					} else if (SubCommandName.equals("add")) {
						Dateiüberwachung.add(event, hook);
					} else if (SubCommandName.equals("remove")) {
						Dateiüberwachung.remove(event, hook);
					} else if (SubCommandName.equals("list")) {
						Dateiüberwachung.list(event, hook);
					}
					return;
					// Moderations-Commands
				} else if (event.getName().equals("emotes")) {
					Emotes.emotes(event, hook);
					return;
				} else if (event.getName().equals("remove")) {
					Remove.remove(event, hook);
					return;
				} else if (event.getName().equals("warn")) {
					User user = event.getOption("user").getAsUser();
					String grundhinweis = event.getOption("grund").getAsString();
					Warn.onSlashCommand(event, user, grundhinweis, hook);
					return;
				} else if (event.getName().equals("kick")) {
					Member member = event.getOption("user").getAsMember();
					String text;
					try {
						text = event.getOption("grund").getAsString();
					} catch (NullPointerException e) {
						text = "";
					}
					if (member == null) {
						hook.editOriginal("Konnte den User nicht finden").queue();
						return;
					}
					Kick.onSlashCommand(event, member, text, hook);
					return;

				} else if (event.getName().equals("ban")) {
					DateTime unbantime = DateTime.now().plusYears(4);
					User user = event.getOption("user").getAsUser();
					event.getGuild().retrieveMember(user).queue(member -> {
						String text;
						try {
							text = event.getOption("grund").getAsString();
						} catch (NullPointerException e) {
							text = "";
						}
						Ban.onSlashCommand(event, user, member, text, hook, unbantime);
					}, e -> {
						String text;
						try {
							text = event.getOption("grund").getAsString();
						} catch (NullPointerException e1) {
							text = "";
						}
						Ban.onSlashCommand(event, user, null, text, hook, unbantime);
					});
					return;
				} else if (event.getName().equals("permaban")) {
					User user = event.getOption("user").getAsUser();
					event.getGuild().retrieveMember(user).queue(member -> {
						String text;
						try {
							text = event.getOption("grund").getAsString();
						} catch (NullPointerException e) {
							text = "";
						}
						Ban.onSlashCommand(event, user, member, text, hook, null);
					}, e -> {
						String text;
						try {
							text = event.getOption("grund").getAsString();
						} catch (NullPointerException e1) {
							text = "";
						}
						Ban.onSlashCommand(event, user, null, text, hook, null);
					});
					return;
				} else if (event.getName().equals("tempban")) {
					User user = event.getOption("user").getAsUser();
					DateTime unbantime = StrafeTemp.fromString(event.getOption("dauer").getAsString());
					if (unbantime == null) {
						hook.editOriginal("Das Fomat der Dauer ist nicht korrekt! Richtiges Format: 1y1mon1w1d1h1min").queue();
						return;
					}
					event.getGuild().retrieveMember(user).queue(member -> {
						String text;
						try {
							text = event.getOption("grund").getAsString();
						} catch (NullPointerException e) {
							text = "";
						}
						Ban.onSlashCommand(event, user, member, text, hook, unbantime);
					}, e -> {
						String text;
						try {
							text = event.getOption("grund").getAsString();
						} catch (NullPointerException e1) {
							text = "";
						}
						Ban.onSlashCommand(event, user, null, text, hook, unbantime);
					});
					return;
				} else if (event.getName().equals("changeban")) {
					User user = event.getOption("user").getAsUser();
					DateTime unbantime = StrafeTemp.fromString(event.getOption("dauer").getAsString());
					if (unbantime == null) {
						hook.editOriginal("Das Fomat der Dauer ist nicht korrekt! Richtiges Format: 1y1mon1w1d1h1min").queue();
						return;
					}
					ChangeTemp.onSlashCommand(event, user, hook, unbantime, StrafenTyp.BAN);
					return;
				} else if (event.getName().equals("changemute")) {
					User user = event.getOption("user").getAsUser();
					DateTime unbantime = StrafeTemp.fromString(event.getOption("dauer").getAsString());
					if (unbantime == null) {
						hook.editOriginal("Das Fomat der Dauer ist nicht korrekt! Richtiges Format: 1y1mon1w1d1h1min").queue();
						return;
					}
					ChangeTemp.onSlashCommand(event, user, hook, unbantime, StrafenTyp.MUTE);
					return;

				} else if (event.getName().equals("mute")) {
					Member member = event.getOption("user").getAsMember();
					String text;
					try {
						text = event.getOption("grund").getAsString();
					} catch (NullPointerException e) {
						text = "";
					}
					if (member == null) {
						hook.editOriginal("Konnte den User nicht finden").queue();
						return;
					}
					Mute.onSlashCommand(event, member, text, hook);
					return;

				}
			}

			// Admin
			if (Modrolle > 2) {
				switch (event.getName()) {
				case "modrolle":
					if (event.getSubcommandName().equals("add")) {
						ModRolle.add(event, hook);
					} else if (event.getSubcommandName().equals("remove")) {
						ModRolle.remove(event, hook);
					} else if (event.getSubcommandName().equals("list")) {
						ModRolle.list(event, hook);
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
				case "announcechannel":
					AnnounceChannel.setup(event);
					return;
				case "wordle":
					WordleCommand.anleitung(event, hook);
					return;
				}
			}
			event.getHook().editOriginal("Du hast nicht die notwendigen Rechte diesen Befehl auszuführen!").queue();
			logger.warn("Befehl wurde nicht ausgeführt. Hatte der User die notwendigen Rechte: " + event.getName() + " " + event.getMember().getEffectiveName());
		} catch (

		Exception e) {
			hook.editOriginal("Es ist etwas schiefgelaufen. Bitte wende dich an Twin.").queue();
			logger.error("Fehler beim auswerten des Befehls", e);
		}

	}

	public static void startupcheck(JDA jda, String versionbot) {
		try {
			if (Settings.Version.equals(versionbot)) {
				return;
			}
			ArrayList<CommandData> newlistguild = new ArrayList<CommandData>();

			newlistguild.add(onlinestatus());
			newlistguild.add(activity());
			newlistguild.add(name());
			newlistguild.add(datei());
			newlistguild.add(logchannel());
			newlistguild.add(announcechannel());
			newlistguild.add(modrolle());
			newlistguild.add(emotes());
			newlistguild.add(info());
			newlistguild.add(hinweis());
			newlistguild.add(warn());
			newlistguild.add(kick());
			newlistguild.add(ban());
			newlistguild.add(tempban());
			newlistguild.add(permaban());
			newlistguild.add(changeban());
			newlistguild.add(remove());
			newlistguild.add(mute());

			ArrayList<CommandData> newlistjda = new ArrayList<CommandData>();

			newlistjda.add(wordle());
			newlistjda.add(tictactoe());
			newlistjda.add(ssp());

			register(newlistguild, newlistjda, jda);
			Settings.set("Setup", "Version", versionbot);
		} catch (Exception e) {
			logger.fatal("Fehler beim Aktuallisieren der Befehle:", e);
		}
	}

	public static void register(ArrayList<CommandData> listserver, ArrayList<CommandData> listjda, JDA jda) {
		try {
			CommandListUpdateAction commandsguild = jda.getGuilds().get(0).updateCommands();
			for (CommandData command : listserver) {
				commandsguild.addCommands(command);
			}
			commandsguild.queue(success -> logger.info("Befehle wurden Guild registriert: " + success),
					failure -> logger.fatal("Fehler beim Registrieren der Befehle:", failure));

			CommandListUpdateAction commandsjda = jda.updateCommands();
			for (CommandData command : listjda) {
				commandsjda.addCommands(command);
			}
			commandsjda.queue(success -> logger.info("Befehle wurden JDA registriert: " + success),
					failure -> logger.fatal("Fehler beim Registrieren der Befehle:", failure));

		} catch (Exception e) {
			logger.fatal("Fehler beim Registrieren der Befehle:", e);
		}
	}

	private static SlashCommandData onlinestatus() {
		return Commands.slash("onlinestatus", "Ändert den Onlinestatus des Bots")
				.addOptions(new OptionData(STRING, "onlinestatus", "Legt den Onlinestatus fest.").addChoice("online", "ONLINE")
						.addChoice("nichtstören", "DO_NOT_DISTURB").addChoice("abwesend", "IDLE").addChoice("unsichtbar", "INVISIBLE").setRequired(true));
	}

	private static SlashCommandData activity() {
		return Commands.slash("activity", "Konfiguriert die Aktivität des Bots")
				.addOptions(new OptionData(STRING, "activitytyp", "Typ der Activity auswählen.").addChoice("playing", "playing").addChoice("watching", "watching")
						.addChoice("listening", "listening").addChoice("streaming", "streaming").setRequired(true))
				.addOptions(new OptionData(STRING, "activitytext", "Konfiguriert den Text der Activity").setRequired(true));
	}

	private static SlashCommandData name() {
		return Commands.slash("namen", "Konfiguriert die Namensüberwachung").addSubcommands(new SubcommandData("activate", "Namensüberwachung aktivieren"))
				.addSubcommands(new SubcommandData("deactivate", "Namensüberwachung deaktivieren"))
				.addSubcommands(new SubcommandData("add", "Verbotene Namen hinzufügen")
						.addOptions(new OptionData(STRING, "name", "Hier verbotenen Namen angeben.").setRequired(true)))
				.addSubcommands(new SubcommandData("remove", "Verbotene Namen entfernen")
						.addOptions(new OptionData(STRING, "name", "Hier Namen angeben.").setRequired(true)))
				.addSubcommands(new SubcommandData("list", "Liste der Verbotenen Namen"));
	}

	private static SlashCommandData datei() {
		return Commands.slash("datei", "Konfiguriert die Dateiüberwachung").addSubcommands(new SubcommandData("activate", "Namensüberwachung aktivieren"))
				.addSubcommands(new SubcommandData("deactivate", "Namensüberwachung deaktivieren"))
				.addSubcommands(new SubcommandData("add", "Verbotene Namen hinzufügen")
						.addOptions(new OptionData(STRING, "name", "Hier verbotenen Namen angeben.").setRequired(true)))
				.addSubcommands(new SubcommandData("remove", "Verbotene Namen entfernen")
						.addOptions(new OptionData(STRING, "name", "Hier Namen angeben.").setRequired(true)))
				.addSubcommands(new SubcommandData("list", "Liste der Verbotenen Namen"));
	}

	private static SlashCommandData logchannel() {
		return Commands.slash("logchannel", "Legt den Kanal fest, in dem die Alarme gepostet werden.")
				.addOptions(new OptionData(CHANNEL, "textchannel", "Wähle den Textchannel aus.").setRequired(true));
	}

	private static SlashCommandData announcechannel() {
		return Commands.slash("announcechannel", "Legt den Kanal fest, in dem Ankündigungen gepostet werden.")
				.addOptions(new OptionData(CHANNEL, "textchannel", "Wähle den Textchannel aus.").setRequired(true));
	}

	private static SlashCommandData modrolle() {
		return Commands.slash("modrolle", "Konfiguriert die Modrollen")
				.addSubcommands(new SubcommandData("add", "Modrolle hinzufügen")
						.addOptions(new OptionData(ROLE, "rolle", "Hier die gewünschte Rolle angeben.").setRequired(true))
						.addOptions(new OptionData(STRING, "zugriffsstufe", "Welche Stufe soll die Rolle haben?").setRequired(true).addChoice("Admin", "Admin")
								.addChoice("Mod", "Mod").addChoice("ChannelMod", "Channelmod")))
				.addSubcommands(new SubcommandData("remove", "Verbotene Dateiendungen entfernen")
						.addOptions(new OptionData(STRING, "rolle", "Hier die gewünschte Rolle angeben.").setRequired(true)))
				.addSubcommands(new SubcommandData("list", "Listet alle Modrollen auf."));
	}

	private static SlashCommandData emotes() {
		return Commands.slash("emotes", "Startet die Emoteauswertung");
	}

	private static SlashCommandData info() {
		return Commands.slash("info", "Ruft Informationen über einen User ab.")
				.addOptions(new OptionData(USER, "user", "Wähle hier den User aus.").setRequired(true));
	}

	private static SlashCommandData hinweis() {
		return Commands.slash("hinweis", "Schickt einem User einen Hinweis").addOptions(new OptionData(USER, "user", "Wähle hier den User aus.").setRequired(true))
				.addOptions(new OptionData(STRING, "grund", "Gib hier den Hinweis-Text an.").setRequired(true));
	}

	private static SlashCommandData warn() {
		return Commands.slash("warn", "Schickt einem User eine Verwarnung").addOptions(new OptionData(USER, "user", "Wähle hier den User aus.").setRequired(true))
				.addOptions(new OptionData(STRING, "grund", "Gib hier den Grund an.").setRequired(true));
	}

	private static SlashCommandData kick() {
		return Commands.slash("kick", "Kickt den ausgewählten User").addOptions(new OptionData(USER, "user", "Wähle hier den User aus.").setRequired(true))
				.addOptions(new OptionData(STRING, "grund", "Gib hier den Grund des Kicks an.").setRequired(true));
	}

	private static SlashCommandData ban() {
		return Commands.slash("ban", "Bannt den ausgewählten User").addOptions(new OptionData(USER, "user", "Wähle den zu bannenden User aus.").setRequired(true))
				.addOptions(new OptionData(STRING, "grund", "Gib hier den Grund des Bans an.").setRequired(true));
	}

	private static SlashCommandData mute() {
		return Commands.slash("mute", "Timeoutet den ausgewählten User für X Tage")
				.addOptions(new OptionData(USER, "user", "Wähle den User aus, der getimeoutet werden soll.").setRequired(true))
				.addOptions(new OptionData(STRING, "grund", "Gib hier den Grund des TimeOuts an.").setRequired(true))
				.addOptions(new OptionData(STRING, "days", "Gib hier die Anzahl der Tage TimeOut an.").setRequired(false))
				.addOptions(new OptionData(STRING, "hours", "Gib hier die Anzahl der Stunden TimeOut an.").setRequired(false))
				.addOptions(new OptionData(STRING, "minutes", "Gib hier die Anzahl der Minuten TimeOut an.").setRequired(false));
	}

	private static SlashCommandData tempban() {
		return Commands.slash("tempban", "Bannt den ausgewählten User temporär")
				.addOptions(new OptionData(USER, "user", "Wähle den zu bannenden User aus.").setRequired(true))
				.addOptions(new OptionData(STRING, "dauer", "Format: 1y1mon1w1d1h1min").setRequired(true))
				.addOptions(new OptionData(STRING, "grund", "Gib hier den Grund des Bans an.").setRequired(true));
	}

	private static SlashCommandData permaban() {
		return Commands.slash("permaban", "Bannt den ausgewählten User Permanent")
				.addOptions(new OptionData(USER, "user", "Wähle den zu bannenden User aus.").setRequired(true))
				.addOptions(new OptionData(STRING, "grund", "Gib hier den Grund des Bans an.").setRequired(true));
	}

	private static SlashCommandData changeban() {
		return Commands.slash("changeban", "Ändert die Dauer eines Tempbans.").addOptions(new OptionData(USER, "user", "Gib die ID des Users an").setRequired(true))
				.addOptions(new OptionData(STRING, "dauer", "Gib hier die neue Dauer des TempBans an").setRequired(true));
	}

	private static SlashCommandData remove() {
		return Commands.slash("remove", "Entfernt Daten aus der Datenbank! Macht Keine Strafen rückgängig!.")
				.addOptions(new OptionData(STRING, "id", "Eindeutige ID der Strafe").setRequired(true));
	}

	// Funsies

	private static SlashCommandData wordle() {
		return Commands.slash("wordle", "Startet eine neue World-Session");
	}

	private static SlashCommandData tictactoe() {
		return Commands.slash("tictactoe", "Startet eine neue TicTacToe-Session").addOptions(new OptionData(STRING, "kistärke", "Schwierigkeit der KI")
				.addChoice("Leicht", "leicht").addChoice("Mittel", "mittel").addChoice("Schwer", "schwer").setRequired(true));
	}

	private static SlashCommandData ssp() {
		return Commands.slash("ssp", "Schere - Stein - Papier").addOptions(new OptionData(STRING, "auswahl", "Schere Stein oder Papier?")
				.addChoice("Schere", "schere").addChoice("Stein", "stein").addChoice("Papier", "papier").setRequired(true));
	}
}