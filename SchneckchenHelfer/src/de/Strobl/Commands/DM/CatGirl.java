package de.Strobl.Commands.DM;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CatGirl extends ListenerAdapter {
	public static Wini ini;
	public void onPrivateMessageReceived (PrivateMessageReceivedEvent event) {
		
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		
		try {
			ini = new Wini (new File(Main.Pfad + "catgirl.ini"));
		} catch (Exception e) {
			File newFile = new File(Main.Pfad + "catgirl.ini");
			try {
				newFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			ini = null;
			try {
				ini = new Wini (new File(Main.Pfad + "catgirl.ini"));
			} catch (InvalidFileFormatException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			ini.add("Images", "0", "http://www.imagine-waggons.de/cHZIccHiRLFIRYpE");
			try {
				ini.store();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		if (args[0].equalsIgnoreCase("!cat") && (event.getAuthor().getId().equals("227131380058947584"))) {
			
//ADD
			if (args[1].equalsIgnoreCase("add")) {
				
				int zahl;
				zahl = ini.get("Images").size();
				
				ini.add("Images", zahl + "", args[2]);
				try {
					ini.store();
				} catch (IOException e) {
					e.printStackTrace();
				}
				EmbedBuilder add = new EmbedBuilder();
				add.addField("Neues Bild hinzugef�gt:", "**Link:** " + args[2] + "\n**ID:** " + zahl, false);
				event.getChannel().sendMessageEmbeds(add.build()).queue();
				
//ENTFERNEN
			} else if (args[1].equalsIgnoreCase("remove")) {
				
				try {
					EmbedBuilder add = new EmbedBuilder();
					add.addField("Bild entfernt:", "**Link:** " + ini.get("Images", args[2]) + "\n**ID:** " + args[2], false);
					
					ini.remove("Images", args[2]);
					try {
						ini.store();
					} catch (IOException e) {
						e.printStackTrace();
					}
				
					event.getChannel().sendMessageEmbeds(add.build()).queue();
				} catch (Exception e) {
					event.getChannel().sendMessage("Fehler").queue();
				}
				
//LISTE
			} else if (args[1].equalsIgnoreCase("list")) {
				for (int p=0; p<= ((ini.get("Images").size()/24));p++) {
					EmbedBuilder list = new EmbedBuilder();
					list.setAuthor("Catgirl Image List");
					for (int i=(p*24); i < ((p*24)+24) && i < ini.get("Images").size() ; i++) {
						list.addField(i + "", ini.get("Images", i + ""), false);
					}
					if (list.getFields().size() > 0) {
						event.getChannel().sendMessageEmbeds(list.build()).queue();
					};
					list.clear();
				}
			}
		}
		
//BEFEHL
		if (event.getMessage().getContentRaw().equalsIgnoreCase("!catgirl")) {
			
			Random random = new Random();
			
			event.getChannel().sendMessage(ini.get("Images", random.nextInt(ini.get("Images").size()) + "")).queue();
			
		}
		
	}

}