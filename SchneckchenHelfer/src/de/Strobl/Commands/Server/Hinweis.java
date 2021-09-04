package de.Strobl.Commands.Server;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;

import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Hinweis {
	public static void hinweis (SlashCommandEvent event, Member member, String Text, InteractionHook EventHook) {
		Logger logger = Main.logger;
		try {	
			try { @SuppressWarnings("unused")
				Wini WiniID = new Wini(new File(Main.Pfad + "ID.ini"));
			} catch (Exception e) {
			    File newFile = new File(Main.Pfad + "ID.ini");
			    newFile.createNewFile();
				Wini WiniID = new Wini (new File(Main.Pfad + "ID.ini"));
				WiniID.put("Counter", "Counter", "0");
	        	WiniID.store();
			}
			try { @SuppressWarnings("unused")
				Wini WiniUser = new Wini(new File(Main.Userpfad + member.getId() +".ini"));
			} catch (Exception e) {
			    File newFile = new File(Main.Userpfad + member.getId() +".ini");
			    newFile.createNewFile();
				Wini WiniUser = new Wini (new File(Main.Userpfad + member.getId() +".ini"));
				WiniUser.store();
			}
			
			try {
			
				EmbedBuilder Nachricht = new EmbedBuilder();
	        	Nachricht.setColor(0xd41406);
	        	Nachricht.setAuthor(event.getGuild().getName(), event.getGuild().getIconUrl(), event.getGuild().getIconUrl());
	        	Nachricht.addField("Hinweis des Serverteams:", Text, true);
				member.getUser().openPrivateChannel().complete().sendMessageEmbeds(Nachricht.build()).complete();
				Nachricht.clear();
		
			} catch (ErrorResponseException e2) {
				EventHook.editOriginal("Nachricht konnte nicht zugestellt werden! (Privatnachrichten aus?)").queue();
				return;
				
			}
			
			Wini WiniID;
			Wini WiniUser;
			Integer ID;
			EmbedBuilder Info = new EmbedBuilder();
			Info.setColor(0x00b806);
			try {
				WiniID = new Wini(new File(Main.Pfad + "ID.ini"));
				ID = Integer.parseInt(WiniID.get("Counter", "Counter"))+1;
				WiniUser = new Wini(new File(Main.Userpfad + member.getId() +".ini"));
				WiniID.put("Hinweise", ID.toString(), member.getId());
				WiniID.put("Counter", "Counter", ID);
				WiniID.store();
				WiniUser.put("Hinweise", ID.toString(), Text);
				WiniUser.store();
				Info.addField("User hat einen Hinweis erhalten", "\n**Hinweis-ID:** " + ID, false);
				Info.addField(member.getEffectiveName() + "'s Hinweis Nr. " + WiniUser.get("Hinweise").size(), "**Grund:** " + Text, true);
			} catch (Exception e1) {
				e1.printStackTrace();
				Info.setColor(0xd41406);
				Info.addField("User hat einen Hinweis erhalten", "", false);
				Info.addField("Fehler beim Speichern des Hinweises. Der User hat den Hinweis erhalten.", "**Grund:** " + Text, true);
			}
			Info.setAuthor(member.getEffectiveName() + "     UserID: " + member.getId(), member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl());
			Info.setFooter("Abgeschickt von: " + event.getMember().getEffectiveName());
			Info.setTimestamp(ZonedDateTime.now().toInstant());
			EventHook.editOriginal("Erledigt.").queue();
			event.getChannel().sendMessageEmbeds(Info.build()).queue();
			Info.clear();
			
		} catch (IOException e) {
			logger.error("IO-Fehler bei Hinweis-Befehl ausführung", e);
			EventHook.editOriginal("IO-Fehler beim Ausführen. Twin Informieren").queue();
		} catch (Exception e) {
			logger.error("Fehler bei Hinweis-Befehl ausführung", e);
			EventHook.editOriginal("Unbekannter Fehler. Twin Informieren").queue();
		}
	}
}
