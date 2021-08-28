package de.Strobl.Events.Nachrichten;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Filechecker extends ListenerAdapter {
	public String[] Endungen;
	public String Endung;
	public ArrayList<String> DateiEndungen = new ArrayList<String>();
	public String ID;

	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Logger logger = Main.logger;
		try {
//Datei lesen
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));

			Boolean active = ini.get("Dateiüberwachung", "Active").equals("");
			
			
			
			
			
			
		} catch (IOException e) {
			logger.error("IO-Fehler Dateiüberwachung", e);
		} catch (Exception e) {
			logger.error("Fehler Dateiüberwachung:", e);
		}


		
		
////Datei erkennung & Aktiv
//			if (!event.getMessage().getAttachments().isEmpty() && active.equals("true") && !event.getAuthor().isBot()) {
//				ID = ini.get("Settings", "Settings.LogChannel");
////Erlaubte Endungen auslesen
//				String[] Endungen = ini.get("Dateiüberwachung", "Allowed").replaceAll("]", "").replaceAll("\\s", "")
//						.split(",");
//				for (int i = 0; i < Endungen.length; i++) {
//					DateiEndungen.add(i, Endungen[i]);
//				}
//				DateiEndungen.set(0, "0");
//				Endung = event.getMessage().getAttachments().get(0).getFileExtension();
////Endung verboten
//				try {
//					if (!DateiEndungen.contains(Endung.toLowerCase())) {
//						try {
//							EmbedBuilder Dateigelöscht = new EmbedBuilder();
//							Dateigelöscht.setColor(0xd41406);
//							Dateigelöscht.setAuthor("Nachricht mit Datei gelöscht", event.getGuild().getIconUrl(),
//									event.getGuild().getIconUrl());
//							Dateigelöscht.addField("User:",
//									event.getAuthor().getAsMention() + "             " + event.getAuthor().getName(),
//									false);
//							Dateigelöscht.addField("Channel:", event.getChannel().getAsMention(), true);
//							Dateigelöscht.addField("DateiName:",
//									event.getMessage().getAttachments().get(0).getFileName(), true);
//							event.getGuild().getTextChannelById(ID).sendMessageEmbeds(Dateigelöscht.build()).complete();
//							event.getGuild().getTextChannelById(ID).sendMessage(event.getJDA().getGuilds().get(0)
//									.getMemberById("227131380058947584").getAsMention()).complete();
//							Dateigelöscht.clear();
//							event.getMessage().delete().queue();
//						} catch (Exception e) {
//							logger.error("Kein Logchannel eingerichtet", e);
//						}
//					}
//				} catch (NullPointerException e) {
//					try {
//						EmbedBuilder Dateigelöscht = new EmbedBuilder();
//						Dateigelöscht.setColor(0xd41406);
//						Dateigelöscht.setAuthor("Nachricht mit Datei gelöscht", event.getGuild().getIconUrl(),
//								event.getGuild().getIconUrl());
//						Dateigelöscht.addField("User:",
//								event.getAuthor().getAsMention() + "             " + event.getAuthor().getName(),
//								false);
//						Dateigelöscht.addField("Channel:", event.getChannel().getAsMention(), true);
//						Dateigelöscht.addField("DateiName:", event.getMessage().getAttachments().get(0).getFileName(),
//								true);
//						event.getGuild().getTextChannelById(ID).sendMessageEmbeds(Dateigelöscht.build()).complete();
//						event.getGuild().getTextChannelById(ID).sendMessage(
//								event.getJDA().getGuilds().get(0).getMemberById("227131380058947584").getAsMention())
//								.complete();
//						Dateigelöscht.clear();
//						event.getMessage().delete().queue();
//					} catch (Exception e1) {
//						logger.error("Kein Logchannel eingerichtet", e);
//					}
//				}
//			}
//		} catch (Exception e) {
//			logger.error("Fehler", e);
//		}
	}
}