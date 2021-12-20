package de.Strobl.Commands.Server;

import java.awt.Color;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Instances.Strafe;
import de.Strobl.Instances.StrafenTyp;
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
			EmbedBuilder builderuser = Discord.standardEmbed(Color.RED, "Kick vom Server", member.getId(), member.getEffectiveAvatarUrl());
			builderuser.setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl());
			Discord.SplitTexttoField(Text, "Grund:").forEach(field -> {
				builderuser.addField(field);
			});
//DMs öffnen & abschicken
			member.getUser().openPrivateChannel().queue(userchannel -> {
				userchannel.sendMessageEmbeds(builderuser.build()).queue(success -> {
					kick(event.getMember(), member, Text, EventHook, true);
				}, e -> {
					kick(event.getMember(), member, Text, EventHook, false);
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

	private static void kick(Member modmember, Member banmember, String text, InteractionHook EventHook, Boolean dm) {
//User Kicken
		banmember.kick(Discord.trim(text)).queue(success -> {
			try {
				EmbedBuilder builderintern = Discord.standardEmbed(Color.GREEN, "User wurde vom Server gekickt", banmember.getId(), banmember.getEffectiveAvatarUrl());
				builderintern.setAuthor(modmember.getEffectiveName(), null, modmember.getEffectiveAvatarUrl());
				String sql = "";
				try {
					Integer size = Strafe.getSQLSize(banmember.getId(), StrafenTyp.KICK);
					Strafe strafe = new Strafe(banmember.getId(), StrafenTyp.KICK, text, modmember.getId()).save();
					String id = strafe.getId();

					sql = "Kick-ID: " + id + "\n" + banmember.getEffectiveName() + " 's Kick Nr." + size;

				} catch (SQLException e) {
					builderintern.setColor(Color.RED);
					sql = "SQL Fehler beim Speichern des Kicks!";
					logger.error("Fehler beim Speichern des Kicks", e);
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
				EventHook.editOriginal("User wurde gekickt. Fehler bei der Rückmeldung!").queue();
				logger.error("User wurde gekickt. Fehler bei der Rückmeldung!", e);
			}
		}, e -> {
			EventHook.editOriginal("Fehler beim kick. User wurde nicht gekickt.").queue();
			logger.error("Fehler beim Kicken von " + banmember.getEffectiveName(), e);
		});
	}
}
