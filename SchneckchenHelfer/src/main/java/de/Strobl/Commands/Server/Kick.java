package de.Strobl.Commands.Server;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.Strobl.Instances.SQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Kick {
	private static final Logger logger = LogManager.getLogger(Kick.class);
	public static void onSlashCommand(SlashCommandEvent event, Member member, String Text, InteractionHook EventHook) {
		try {
			if (event.getJDA().getSelfUser() == member.getUser()) {
				EventHook.editOriginal("Wolltest du wirklich mich kicken? 🙄  Vergiss das mal gleich wieder.").queue();
				return;
			}
			if (!event.getGuild().getSelfMember().hasPermission(Permission.KICK_MEMBERS)) {
				EventHook.editOriginal("Meine Rechte reichen nicht aus, um User zu kicken.").queue();
				return;
			}
			if (!event.getMember().canInteract(member)) {
				EventHook.editOriginal("Deine Rechte reichen nicht aus, um diesen User zu kicken.").queue();
				return;
			}
			if (!event.getGuild().getSelfMember().canInteract(member)) {
				EventHook.editOriginal("Meine Rechte reichen nicht aus, um diesen User zu kicken.").queue();
				return;
			}
//Nachricht an User vorbereiten
			EmbedBuilder Nachricht = new EmbedBuilder();
			Nachricht.setColor(0xd41406);
			Nachricht.setAuthor(event.getGuild().getName(), event.getGuild().getIconUrl(), event.getGuild().getIconUrl());
			Nachricht.addField("Kick vom Server:", Text, true);
			Nachricht.setTimestamp(ZonedDateTime.now().toInstant());
//DMs öffnen & abschicken
			member.getUser().openPrivateChannel().queue(test -> {
				test.sendMessageEmbeds(Nachricht.build()).queue(success -> {
					kick(event, member, Text, EventHook, true);
				}, e -> {
					kick(event, member, Text, EventHook, false);
				});
			}, e -> {
				EventHook.editOriginal("Fehler beim kick. User wurde nicht gekickt.").queue();
				logger.error("Fehler beim öffnen des Privaten Channels mit " + member.getEffectiveName(), e);
			});
		} catch (Exception e) {
			EventHook.editOriginal("Fehler beim kick. User wurde nicht gekickt.").queue();
			logger.error("Fehler beim Kicken von " + member.getEffectiveName(), e);
		}
	}

	public static void kick(SlashCommandEvent event, Member member, String text, InteractionHook EventHook, Boolean dm) {
		try {
//User Kicken
			member.kick(text).queue(success -> {
				try {
				EmbedBuilder info = new EmbedBuilder();
				info.setColor(0x00b806);
				info.setAuthor(member.getEffectiveName(), member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl());
				info.setFooter("Gekickt von " + event.getMember().getEffectiveName());
				info.setTimestamp(ZonedDateTime.now().toInstant());
				if (dm) {
					info.addField("User wurde vom Server gekickt", "", false);
				} else {
					info.addField("User wurde vom Server gekickt", "User konnte NICHT informiert werden! Privatnachrichten aus?", false);
				}
				try {
					Integer size = SQL.strafengetusersize(member.getId(), "Kick")+1;
					Integer ID = SQL.strafengetcounter();
					SQL.strafenadd(ID, member.getId(), "Kick", text);
					SQL.strafencounterup();
					info.addField("Kick-ID: " + ID, member.getEffectiveName() + " 's Kick Nr." + size, false);
				} catch (SQLException e1) {
					info.addField("SQL Fehler beim Speichern des Kicks!", "User wurde erfolgreich gekickt!", false);
				}
				info.addField("Grund:", text, false);
				event.getChannel().sendMessageEmbeds(info.build()).queue();
				EventHook.editOriginal("Erfolg.").queue();
				} catch (Exception e) {
					event.getChannel().sendMessage("User wurde gekickt. Fehler bei der Rückmeldung!").queue();
				}
			}, e -> {
				EventHook.editOriginal("Fehler beim kick. User wurde nicht gekickt.").queue();
				logger.error("Fehler beim Kicken von " + member.getEffectiveName(), e);
			});
		} catch (Exception e) {
			EventHook.editOriginal("Fehler beim kick. User wurde nicht gekickt.").queue();
			logger.error("Fehler beim Kicken von " + member.getEffectiveName(), e);
		}
	}
}
