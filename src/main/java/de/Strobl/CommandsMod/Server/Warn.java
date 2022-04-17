package de.Strobl.CommandsMod.Server;

import java.awt.Color;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Instances.Strafe;
import de.Strobl.Instances.StrafenTyp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Warn {
	private static final Logger logger = LogManager.getLogger(Warn.class);

	public static void onSlashCommand(SlashCommandInteractionEvent event, User user, String Text, InteractionHook EventHook) {
		try {
			if (event.getJDA().getSelfUser() == user) {
				EventHook.editOriginal("Du kannst dem " + event.getJDA().getSelfUser().getName() + " keine Verwarnung schicken.").queue();
				return;
			}
			;

			EmbedBuilder builderuser = Discord.standardEmbed(Color.RED, "Verwarnung vom Serverteam:", user.getId(), user.getEffectiveAvatarUrl());
			builderuser.setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl());
			Discord.SplitTexttoField(Text, "").forEach(field -> {
				builderuser.addField(field);
			});
			user.openPrivateChannel().queue(channel -> {
				channel.sendMessageEmbeds(builderuser.build()).queue(success -> {
					try {

						EmbedBuilder builderintern = Discord.standardEmbed(Color.GREEN, "User hat einen Warn erhalten", user.getId(), user.getEffectiveAvatarUrl());

						try {
							Strafe strafe = new Strafe(user.getId(), StrafenTyp.WARN, Text, event.getMember().getId()).save();
							String id = strafe.getId();
							builderintern.addField("Warn-ID: " + id, "**" + user.getName() + "'s Warn Nr. " + Strafe.getSQLSize(user.getId(), StrafenTyp.WARN) + "**", false);

						} catch (SQLException e) {

							builderintern.addField("Fehler beim Speichern der Verwarnung. Der User hat den Warn erhalten.", "", true);
							builderintern.setColor(Color.RED);
							logger.error("SQL-Fehler beim Speichern der Verwarnung", e);
						}

						Discord.SplitTexttoField(Text, "Warn-Grund:").forEach(field -> {
							builderintern.addField(field);
						});
						builderintern.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
						EventHook.editOriginal("User: <@" + user.getId() + ">").setEmbeds(builderintern.build()).queue();
						builderintern.clear();

					} catch (Exception e) {
						logger.error("Fehler bei Warn-Befehl ausführung", e);
						EventHook.editOriginal("Fehler bei Warn-Ausführung. Der User hat die Nachricht erhalten.").queue();
					}
				}, e -> {
					try {
						Strafe strafe = new Strafe(user.getId(), StrafenTyp.WARN, Text, event.getMember().getId());
						strafe.save();
						EmbedBuilder builderinternnodm = Discord.standardEmbed(Color.YELLOW, "User wurde verwarnt.", user.getId(), user.getEffectiveAvatarUrl());
						builderinternnodm.setDescription("Die Verwarnung konnte nicht zugestellt werden. (Privatnachrichten aus?)\nWarn wurde abgespeichert.");
						String id = strafe.getId();
						builderinternnodm.addField("Warn-ID: " + id, "**" + user.getName() + "'s Warn Nr. " + Strafe.getSQLSize(user.getId(), StrafenTyp.WARN) + "**", false);
						Discord.SplitTexttoField(Text, "Warn-Grund:").forEach(field -> {
							builderinternnodm.addField(field);
						});
						builderinternnodm.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
						EventHook.editOriginal("User: <@" + user.getId() + ">").setEmbeds(builderinternnodm.build()).queue();
						builderinternnodm.clear();
					} catch (Exception e1) {
						EventHook.editOriginal("Verwarnung konnte nicht zugestellt werden. Privatnachrichten aus? \nWarn konnte zudem nicht gespeichert werden!").queue();
					}
				});
				builderuser.clear();
			}, e -> {
				logger.error("Fehler beim öffnen des Privaten Channels", e);
				EventHook.editOriginal("Fehler beim senden der Verwarnung.").queue();
			});

		} catch (Exception e) {
			logger.error("Fehler bei Warn-Befehl ausführung", e);
			EventHook.editOriginal("Unbekannter Fehler. Twin Informieren").queue();
		}
	}
}
