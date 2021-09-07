package de.Strobl.Commands.Server;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;

import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Instances.getMember;
import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Remove extends ListenerAdapter {
	public static void remove (SlashCommandEvent event, InteractionHook Hook) {
		Logger logger = Main.logger;
		try {
			Wini ID = new Wini(new File(Main.Pfad + "ID.ini"));
			String StrafenID = event.getOption("id").getAsString();
			String UserID = "0";
			String Typ = "0";
			if (!(ID.get("Hinweise", StrafenID) == null)) {
				UserID = ID.get("Hinweise", StrafenID);
				Typ = "Hinweise";
			} else if (!(ID.get("Warns", StrafenID) == null)) {
				UserID = ID.get("Warns", StrafenID);
				Typ = "Warns";
			} else if (!(ID.get("Bans", StrafenID) == null)) {
				UserID = ID.get("Bans", StrafenID);
				Typ = "Bans";
			} else if (!(ID.get("Mutes", StrafenID) == null)) {
				UserID = ID.get("Mutes", StrafenID);
				Typ = "Mutes";
			} else {
				Hook.editOriginal("ID " + StrafenID + " nicht gefunden.").queue();
				logger.debug(StrafenID + UserID + Typ);
				return;
			}
			
			Wini User = new Wini(new File(Main.Userpfad + UserID + ".ini"));
			String Grund = User.get(Typ, StrafenID);
			User.remove(Typ, StrafenID);
			ID.remove(Typ, StrafenID);
			User.store();
			ID.store();
			Member member = getMember.getmember(event, UserID);
			EmbedBuilder Erfolg = new EmbedBuilder();
			Erfolg.setAuthor("Hinweis " + StrafenID + " gelöscht!");
			if (member==null) {
				Erfolg.addField("UserID:   " + UserID, "Grund:   " + Grund, true);
			} else {
				Erfolg.addField("UserID:   " + UserID + "\nUser:   " + member.getEffectiveName(), "Grund:   " + Grund, true);
			}
			Erfolg.setFooter("Gelöscht von: " + event.getMember().getEffectiveName());
			Erfolg.setTimestamp(ZonedDateTime.now().toInstant());
			event.getChannel().sendMessageEmbeds(Erfolg.build()).queue();
			Erfolg.clear();
			Hook.editOriginal("Erledigt").queue();
			
		} catch (IOException e) {
			Hook.editOriginal("IO Fehler beim entfernen").queue();
			logger.error("IOFehler beim entfernen der Strafe:", e);
		} catch (Exception e) {
			Hook.editOriginal("Fehler beim entfernen").queue();
			logger.error("Fehler beim entfernen der Strafe:", e);
		}

	}
}