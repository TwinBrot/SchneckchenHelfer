package de.Strobl.CommandsMod.Server;

import java.awt.Color;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Instances.Strafe;
import de.Strobl.Instances.StrafenTyp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Hinweis {
	private static final Logger logger = LogManager.getLogger(Hinweis.class);

	public static void SlashCommandInteraction(SlashCommandInteractionEvent event, User user, Member member, String Text, InteractionHook EventHook) {
		try {
			if (event.getJDA().getSelfUser() == user) {
				EventHook.editOriginal("Du kannst dem " + event.getJDA().getSelfUser().getName() + " keinen Hinweis schicken.").queue();
				return;
			}
			;
			if (!(member == null)) {
				if (!event.getMember().canInteract(member)) {
					return;
				}
			}

			EmbedBuilder builderuser = Discord.standardEmbed(Color.YELLOW, "Hinweis des Serverteams:", user.getId(), user.getEffectiveAvatarUrl());
			builderuser.setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl());
			Discord.SplitTexttoField(Text, "").forEach(field -> {
				builderuser.addField(field);
			});
			user.openPrivateChannel().queue(channel -> {
				channel.sendMessageEmbeds(builderuser.build()).queue(success -> {
					try {

						EmbedBuilder builderintern = Discord.standardEmbed(Color.GREEN, "User hat einen Hinweis erhalten", user.getId(),
								user.getEffectiveAvatarUrl());

						try {
							Strafe strafe = new Strafe(user.getId(), StrafenTyp.HINWEIS, Text, event.getMember().getId()).save();
							String id = strafe.getId();
							builderintern.addField("Hinweis-ID: " + id,
									"**" + user.getName() + "'s Hinweis Nr. " + Strafe.getSQLSize(user.getId(), StrafenTyp.HINWEIS) + "**", false);

						} catch (SQLException e) {

							builderintern.addField("Fehler beim Speichern des Hinweises. Der User hat den Hinweis erhalten.", "", true);
							builderintern.setColor(Color.RED);
							logger.error("SQL-Fehler beim Speichern des Hinweises", e);
						}

						Discord.SplitTexttoField(Text, "Hinweistext:").forEach(field -> {
							builderintern.addField(field);
						});
						builderintern.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
						EventHook.editOriginal("User: <@" + user.getId() + ">").setEmbeds(builderintern.build()).queue();
						builderintern.clear();

					} catch (Exception e) {
						logger.error("Fehler bei Hinweis-Befehl ausführung", e);
						EventHook.editOriginal("Fehler bei Hinweis-Ausführung. Der User hat die Nachricht erhalten.").queue();
					}
				}, e -> {
					EventHook.editOriginal("Hinweis konnte nicht zugestellt werden. Privatnachrichten aus? (Hinweis wurde nicht gespeichert!)").queue();
				});
				builderuser.clear();
			}, e -> {
				logger.error("Fehler beim öffnen des Privaten Channels", e);
				EventHook.editOriginal("Fehler beim senden des Hinweises.").queue();
			});

		} catch (Exception e) {
			logger.error("Fehler bei Hinweis-Befehl ausführung", e);
			EventHook.editOriginal("Unbekannter Fehler. Twin Informieren").queue();
		}
	}
}
