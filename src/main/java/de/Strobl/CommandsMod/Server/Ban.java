package de.Strobl.CommandsMod.Server;

import java.awt.Color;
import java.sql.SQLException;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import de.Strobl.Instances.Discord;
import de.Strobl.Instances.Strafe;
import de.Strobl.Instances.StrafeTemp;
import de.Strobl.Instances.StrafenTyp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Ban {
	private static final Logger logger = LogManager.getLogger(Ban.class);
	public static void onSlashCommand(SlashCommandInteractionEvent event, User user, Member member, String Text, InteractionHook hook, @Nullable DateTime unbantime) {
		try {
			if (!(member == null)) {
				if (event.getJDA().getSelfUser() == member.getUser()) {
					hook.editOriginal("Wolltest du wirklich mich bannen? 🙄  Vergiss das mal gleich wieder.").queue();
					return;
				}
				if (!event.getMember().canInteract(member)) {
					hook.editOriginal("Deine Rechte reichen nicht aus, um diesen User zu bannen.").queue();
					return;
				}
				if (!event.getGuild().getSelfMember().canInteract(member)) {
					hook.editOriginal("Meine Rechte reichen nicht aus, um diesen User zu bannen.").queue();
					return;
				}
			}
			if (!event.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
				hook.editOriginal("Meine Rechte reichen nicht aus, um User zu bannen.").queue();
				return;
			}
//Nachricht an User vorbereiten
			EmbedBuilder builderuser = Discord.standardEmbed(Color.RED, "Ban vom Server", user.getId(), user.getEffectiveAvatarUrl());
			builderuser.setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl());
			Discord.SplitTexttoField(Text, "Grund:").forEach(field -> {
				builderuser.addField(field);
			});
			if (!(unbantime == null)) {
				builderuser.setDescription("Entbann: " + unbantime.toString("dd.MM.yyyy kk:mm"));
			}
//DMs öffnen & abschicken
			user.openPrivateChannel().queue(userchannel -> {
				userchannel.sendMessageEmbeds(builderuser.build()).queue(success -> {
					ban(event.getMember(), event.getGuild(), member, user, Text, hook, true, unbantime);
				}, e -> {
					ban(event.getMember(), event.getGuild(), member, user, Text, hook, false, unbantime);
				});
			}, e -> {
				hook.editOriginal("Fehler beim ban. User wurde nicht gebannt.").queue();
				logger.error("Fehler beim öffnen des Privaten Channels mit " + user.getName(), e);
			});
		} catch (Exception e) {
			hook.editOriginal("Fehler beim ban. User wurde nicht gebannt.").queue();
			logger.error("Fehler beim bannen von " + user.getName(), e);
		}
	}
	
	private static void ban(Member modmember, Guild guild, Member banmember, User banuser, String text, InteractionHook hook, Boolean dm, DateTime unbantime) {
		//User Kicken
		String reasontrim = Discord.trim(text);
		guild.ban(banuser.getId(), 7, reasontrim).queue(success -> {
					try {
						EmbedBuilder builderintern = Discord.standardEmbed(Color.GREEN, "User wurde vom Server gebannt", banuser.getId(), banuser.getEffectiveAvatarUrl());
						builderintern.setAuthor(modmember.getEffectiveName(), null, modmember.getEffectiveAvatarUrl());
						String sql = "";
						try {
							Strafe strafe = new Strafe(banuser.getId(), StrafenTyp.BAN, text, modmember.getId()).save();
							Integer size = Strafe.getSQLSize(banuser.getId(), StrafenTyp.BAN);
							String id = strafe.getId();
							if (!(unbantime == null)) {
								new StrafeTemp(strafe, unbantime).save();
								builderintern.setDescription("Entbann: " + unbantime.toString("dd.MM.yyyy kk:mm"));
							}
							sql = "Ban-ID: " + id + "\n" + banuser.getName() + " 's Ban Nr. " + size;

						} catch (SQLException e) {
							builderintern.setColor(Color.RED);
							sql = "SQL Fehler beim Speichern des Bans!";
							logger.error("Fehler beim Speichern des Bans", e);
						}
						
						String pm = "";
						if (dm) {
							pm = "";
						} else {
							builderintern.setColor(Color.YELLOW);
							if (banmember == null) {
								pm = "Da der User nicht auf dem Server ist, konnte er nicht vorher Informiert werden!";
							} else {
							pm = "User konnte NICHT informiert werden! Privatnachrichten aus?";
							}
						}
						builderintern.addField(sql, pm, false);
						
						Discord.SplitTexttoField(text, "Grund:").forEach(field -> {
							builderintern.addField(field);
						});
						hook.editOriginal("User: <@" + banuser.getId() + ">").setEmbeds(builderintern.build()).queue();
						builderintern.clear();
					} catch (Exception e) {
						hook.editOriginal("User wurde gebannt. Fehler bei der Rückmeldung!").queue();
						logger.error("User wurde gebannt. Fehler bei der Rückmeldung!", e);
					}
				}, e -> {
					hook.editOriginal("Fehler beim bannen. User wurde nicht gebannt. (Aber Informiert!)").queue();
					logger.error("Fehler beim Bannen von " + banuser.getName(), e);
				});
			}
}
