package de.Strobl.Commands.Server.Ban;

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

public class Ban {
	private static final Logger logger = LogManager.getLogger(Ban.class);
	public static void onSlashCommand(SlashCommandEvent event, Member member, String Text, InteractionHook EventHook) {
		try {
			if (event.getJDA().getSelfUser() == member.getUser()) {
				EventHook.editOriginal("Wolltest du wirklich mich bannen? 🙄  Vergiss das mal gleich wieder.").queue();
				return;
			}
			if (!event.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
				EventHook.editOriginal("Meine Rechte reichen nicht aus, um User zu kicken.").queue();
				return;
			}
			if (!event.getMember().canInteract(member)) {
				EventHook.editOriginal("Deine Rechte reichen nicht aus, um diesen User zu bannen.").queue();
				return;
			}
			if (!event.getGuild().getSelfMember().canInteract(member)) {
				EventHook.editOriginal("Meine Rechte reichen nicht aus, um diesen User zu bannen.").queue();
				return;
			}
//Nachricht an User vorbereiten
			EmbedBuilder Nachricht = new EmbedBuilder();
			Nachricht.setColor(0xd41406);
			Nachricht.setAuthor(event.getGuild().getName(), event.getGuild().getIconUrl(), event.getGuild().getIconUrl());
			Nachricht.addField("Ban vom Server:", Text, true);
			Nachricht.setTimestamp(ZonedDateTime.now().toInstant());
//DMs öffnen & abschicken
			member.getUser().openPrivateChannel().queue(privatechannel -> {
				privatechannel.sendMessageEmbeds(Nachricht.build()).queue(success -> {
					baninstance(event, member, Text, EventHook, true);
				}, e -> {
					baninstance(event, member, Text, EventHook, false);
				});
			}, e -> {
				EventHook.editOriginal("Fehler beim ban. User wurde nicht gebannt.").queue();
				logger.error("Fehler beim öffnen des Privaten Channels mit " + member.getEffectiveName(), e);
			});
		} catch (Exception e) {
			EventHook.editOriginal("Fehler beim ban. User wurde nicht gebannt.").queue();
			logger.error("Fehler beim bannen von " + member.getEffectiveName(), e);
		}
	}

	public static void baninstance(SlashCommandEvent event, Member member, String text, InteractionHook EventHook, Boolean dm) {
		try {
			String membername = member.getEffectiveName();
			String useravatarurl = member.getUser().getAvatarUrl();
			String memberid = member.getId();
//User Kicken
			member.ban(7, text).queue(success -> {
				try {
				EmbedBuilder info = new EmbedBuilder();
				info.setColor(0x00b806);
				info.setAuthor(membername, useravatarurl, useravatarurl);
				info.setFooter("Gebannt von " + event.getMember().getEffectiveName());
				info.setTimestamp(ZonedDateTime.now().toInstant());
				if (dm) {
					info.addField("User wurde vom Server gebannt", "", false);
				} else {
					info.addField("User wurde vom Server gebannt", "User konnte NICHT informiert werden! Privatnachrichten aus?", false);
				}
				try {
					Integer size = SQL.strafengetusersize(memberid, "Ban")+1;
					Integer ID = SQL.strafengetcounter();
					SQL.strafenadd(ID, memberid, "Ban", text);
					SQL.strafencounterup();
					info.addField("Ban-ID: " + ID, membername + " 's Ban Nr." + size, false);
				} catch (SQLException e1) {
					info.addField("SQL Fehler beim Speichern des Bans!", "User wurde erfolgreich gebannt!", false);
				}
				info.addField("Grund:", text, false);
				event.getChannel().sendMessageEmbeds(info.build()).queue();
				EventHook.editOriginal("Erfolg.").queue();
				} catch (Exception e) {
					event.getChannel().sendMessage("User wurde gebannt. Fehler bei der Rückmeldung!").queue();
				}
			}, e -> {
				EventHook.editOriginal("Fehler beim ban. User wurde nicht gebannt.").queue();
				logger.error("Fehler beim Bannen von " + membername, e);
			});
		} catch (Exception e) {
			EventHook.editOriginal("Fehler beim ban. User wurde nicht gebannt.").queue();
			logger.error("Fehler beim Bannen von " + member.getEffectiveName(), e);
		}
	}
}
