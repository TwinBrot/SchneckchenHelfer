package de.Strobl.Commands.Server;

import java.sql.SQLException;
import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Exceptions.SQLDataNotFound;
import de.Strobl.Instances.Discord;
import de.Strobl.Instances.SQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Remove extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(Remove.class);
	public static void remove(SlashCommandEvent event, InteractionHook Hook) {
		String StrafenID = event.getOption("id").getAsString();
		try {
			String Strafe = SQL.strafengetid(StrafenID);
			SQL.strafenremove(StrafenID);
			String[] result = Strafe.split(",", 4);
			String Typ = result[1];
			String UserID = result[2];
			String Grund = result[3];
			Member member = Discord.getmember(event.getGuild(), UserID);
			EmbedBuilder Erfolg = new EmbedBuilder();
			Erfolg.setAuthor(Typ + ": " + StrafenID + " gelöscht!");
			if (member == null) {
				Erfolg.addField("UserID:   " + UserID, "Grund:   " + Grund, true);
			} else {
				Erfolg.addField("UserID:   " + UserID + "\nUser:   " + member.getEffectiveName(), "Grund:   " + Grund, true);
			}
			Erfolg.setFooter("Gelöscht von: " + event.getMember().getEffectiveName());
			Erfolg.setTimestamp(ZonedDateTime.now().toInstant());
			event.getChannel().sendMessageEmbeds(Erfolg.build()).queue();
			Erfolg.clear();
			Hook.editOriginal("Erledigt").queue();
		} catch (ArrayIndexOutOfBoundsException | SQLDataNotFound e) {
			Hook.editOriginal("Konnte den Datensatz mit der ID " + StrafenID + " nicht finden!").queue();
		} catch (SQLException e) {
			Hook.editOriginal("SQL-Fehler beim entfernen").queue();
			logger.error("SQL-Fehler beim entfernen der Strafe:", e);
		} catch (Exception e) {
			Hook.editOriginal("Fehler beim entfernen").queue();
			logger.error("Fehler beim entfernen der Strafe:", e);
		}
	}
}