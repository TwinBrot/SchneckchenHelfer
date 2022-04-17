package de.Strobl.CommandsMod.Server;

import java.awt.Color;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import de.Strobl.Instances.Discord;
import de.Strobl.Instances.Strafe;
import de.Strobl.Instances.StrafenTyp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Mute {
	private static final Logger logger = LogManager.getLogger(Mute.class);

	public static void onSlashCommand(SlashCommandInteractionEvent event, Member member, String text, InteractionHook eventHook) {
		try {

//Permission Checks
			if (event.getJDA().getSelfUser() == member.getUser()) {
				eventHook.editOriginal("Wolltest du wirklich mich Timeouten? 🙄  Vergiss das mal gleich wieder.").queue();
				return;
			}
			if (!event.getGuild().getSelfMember().hasPermission(Permission.MODERATE_MEMBERS)) {
				eventHook.editOriginal("Meine Rechte reichen nicht aus, um User in Timeout zu schicken.").queue();
				return;
			}
			if (!event.getMember().canInteract(member)) {
				eventHook.editOriginal("Deine Rechte reichen nicht aus, um diesen User in Timeout zu schicken.").queue();
				return;
			}
			if (!event.getGuild().getSelfMember().canInteract(member)) {
				eventHook.editOriginal("Meine Rechte reichen nicht aus, um diesen User in Timeout zu schicken.").queue();
				return;
			}

//Auslesen & Umwandeln der Optionen
			int days;
			int hours;
			int minutes;

			try {
				try {
					String optiondays = event.getOption("days").getAsString();
					days = Integer.parseInt(optiondays);
				} catch (NullPointerException e) {
					days = 0;
				}

				try {
					String optionhours = event.getOption("hours").getAsString();
					hours = Integer.parseInt(optionhours);
				} catch (NullPointerException e) {
					hours = 0;
				}

				try {
					String optionminutes = event.getOption("minutes").getAsString();
					minutes = Integer.parseInt(optionminutes);
				} catch (NullPointerException e) {
					minutes = 0;
				}
				
//Dauer falsch angegeben
				
			} catch (NumberFormatException e) {
				eventHook.editOriginal("Dauer falsch angegeben! Bitte gib nur Zahlen ein!").queue();
				return;
			}
			
//Dauer angegeben?
			
			if (days == 0 && hours == 0 && minutes == 0) {
				eventHook.editOriginal("Keine Dauer angegeben! User wurde nicht getimeoutet!").queue();
				return;
			}
			
//Dauer kleiner 28 Tage?

			int timedif = days*60*24 + hours * 60 + minutes;
			DateTime unmute = DateTime.now().plusDays(days).plusHours(hours).plusMinutes(minutes);
			
			if (timedif > Member.MAX_TIME_OUT_LENGTH * 24 * 60) {
				eventHook.editOriginal("Die Dauer darf nicht länger als 28 Tage sein!").queue();
				return;
			}
			
			String grundshort = Discord.trim(text);

// Nachricht an User vorbereiten
			
			EmbedBuilder builderuser = Discord.standardEmbed(Color.RED, "TimeOut auf Server", member.getId(), member.getEffectiveAvatarUrl());
			builderuser.setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl());
			String description = "Ende TimeOut: " + unmute.toString("dd.MM.yyyy kk:mm");
			builderuser.setDescription(description);
			Discord.SplitTexttoField(text, "Grund:").forEach(field -> {
				builderuser.addField(field);
			});

// TimeOuten
			
			member.timeoutFor(timedif, TimeUnit.MINUTES).reason(grundshort).queue(success -> {

// DMs öffnen & abschicken
				member.getUser().openPrivateChannel().queue(userchannel -> {
					userchannel.sendMessageEmbeds(builderuser.build()).queue(msg -> {
						instance(event.getMember(), member, text, eventHook, description, true);
					}, e -> {
						instance(event.getMember(), member, text, eventHook, description, false);
					});
				}, e -> {
					eventHook.editOriginal("Fehler beim Ausführen! User wurde getimeoutet!").queue();
					logger.error("Fehler beim öffnen des Privaten Channels mit " + member.getEffectiveName(), e);
				});

			}, e -> {
				logger.error("Fehler beim Timeouten eines Users", e);
				eventHook.editOriginal("").queue();
			});

		} catch (Exception e) {
			eventHook.editOriginal("Fehler beim mute. User wurde nicht gemutet.").queue();
			logger.error("Fehler beim Muten von " + member.getEffectiveName(), e);
		}
	}

	private static void instance(Member modmember, Member banmember, String text, InteractionHook EventHook, String description, Boolean dm) {
//User Muten
		try {
			EmbedBuilder builderintern = Discord.standardEmbed(Color.GREEN, "User wurde auf Server gemutet", banmember.getId(), banmember.getEffectiveAvatarUrl());
			builderintern.setAuthor(modmember.getEffectiveName(), null, modmember.getEffectiveAvatarUrl());
			builderintern.setDescription(description);
			String sql = "";
			try {
				Strafe strafe = new Strafe(banmember.getId(), StrafenTyp.MUTE, text, modmember.getId()).save();
				Integer size = Strafe.getSQLSize(banmember.getId(), StrafenTyp.MUTE);
				String id = strafe.getId();

				sql = "Mute-ID: " + id + "\n" + banmember.getEffectiveName() + " 's Mute Nr. " + size;

			} catch (SQLException e) {
				builderintern.setColor(Color.RED);
				sql = "SQL Fehler beim Speichern des Mutes! User wurde gemutet!";
				logger.error("Fehler beim Speichern des Mutes", e);
			}

			String pm = "";
			if (dm) {
				pm = "";
			} else {
				builderintern.setColor(Color.YELLOW);
				pm = "User konnte NICHT informiert werden! Privatnachrichten aus?";
			}
			builderintern.addField(sql, pm, false);

			Discord.SplitTexttoField(text, "Grund:").forEach(field -> {
				builderintern.addField(field);
			});
			EventHook.editOriginal("User: <@" + banmember.getId() + ">").setEmbeds(builderintern.build()).queue();
			builderintern.clear();
		} catch (Exception e) {
			EventHook.editOriginal("User wurde gemutet. Fehler bei der Rückmeldung!").queue();
			logger.error("User wurde gemutet. Fehler bei der Rückmeldung!", e);
		}
	}
}