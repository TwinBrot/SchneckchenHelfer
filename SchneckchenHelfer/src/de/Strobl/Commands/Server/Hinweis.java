package de.Strobl.Commands.Server;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.Strobl.Instances.SQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Hinweis {
	private static final Logger logger = LogManager.getLogger(Hinweis.class);
	public static void hinweis(SlashCommandEvent event, User user, String Text, InteractionHook EventHook) {
		try {
			if (event.getJDA().getSelfUser() == user) {
				EventHook.editOriginal("Du kannst dem " + event.getJDA().getSelfUser().getName() + " keinen Hinweis schicken.").queue();
				return;
			};
			EmbedBuilder Nachricht = new EmbedBuilder();
			Nachricht.setColor(0xd41406);
			Nachricht.setAuthor(event.getGuild().getName(), event.getGuild().getIconUrl(), event.getGuild().getIconUrl());
			Nachricht.addField("Hinweis des Serverteams:", Text, true);
			user.openPrivateChannel().queue(channel -> {
				channel.sendMessageEmbeds(Nachricht.build()).queue(success -> {
					try {
						EmbedBuilder Info = new EmbedBuilder();
						Info.setColor(0x00b806);
						Info.setAuthor(user.getName() + "     UserID: " + user.getId(), user.getAvatarUrl(), user.getAvatarUrl());
						Info.setFooter("Abgeschickt von: " + event.getMember().getEffectiveName());
						Info.setTimestamp(ZonedDateTime.now().toInstant());
						
						try {
							Integer ID = SQL.strafengetcounter();
							SQL.strafenadd(ID, user.getId(), "Hinweis", Text);
							SQL.strafencounterup();
							Info.addField("User hat einen Hinweis erhalten", "\n**Hinweis-ID:** " + ID, false);
							Info.addField(user.getName() + "'s Hinweis Nr. "+ SQL.strafengetusersize(user, "Hinweis"), "**Grund:** " + Text, true);
						} catch (SQLException e) {
							logger.error("SQL-Fehler beim Speichern des Hinweises", e);
							Info.setColor(0xd41406);
							Info.addField("User hat einen Hinweis erhalten", "", false);
							Info.addField("Fehler beim Speichern des Hinweises. Der User hat den Hinweis erhalten.", "**Grund:** " + Text, true);
						}
						
						EventHook.editOriginal("Erledigt.").queue();
						event.getChannel().sendMessageEmbeds(Info.build()).queue();
						Info.clear();

					} catch (Exception e) {
						logger.error("Fehler bei Hinweis-Befehl ausführung", e);
						EventHook.editOriginal("Fehler bei Hinweis-Ausführung. Der User hat die Nachricht erhalten.").queue();
					}
				}, e -> {
					EventHook.editOriginal("Fehler beim zustellen der Nachricht. Privatnachrichten aus? Hinweis wurde nicht gespeichert!").queue();
				});
				Nachricht.clear();
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
