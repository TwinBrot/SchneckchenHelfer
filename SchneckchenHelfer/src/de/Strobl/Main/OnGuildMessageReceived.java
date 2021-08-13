package de.Strobl.Main;
import org.ini4j.Wini;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
public class OnGuildMessageReceived extends ListenerAdapter {
	public static Wini ini;
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		
		
		
		
		
		
		
//		try {
//			ini = new Wini (new File(Main.Pfad + "settings.ini"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
////Command erkennung
//		if (!event.getMessage().getContentRaw().startsWith(ini.get("Settings", "Settings.Prefix"))) {
//			return;
//		}
//		String[] args = event.getMessage().getContentRaw().split("\\s+");
//		String Prefix = ini.get("Settings", "Settings.Prefix");
////ModRollen auslesen
//		Boolean Owner = false;
//		ArrayList <String> ModRollen = new ArrayList<String>();
//		ArrayList <String> UserRollen = new ArrayList<String>();
//		String[] Rollen = ini.get("ModRollen", "Modrollen").replaceAll("]", "").replaceAll("\\s", "").split(",");
//		for (int i = 0; i < Rollen.length; i++ ){
//			ModRollen.add(Rollen[i]);
//		}
//		ModRollen.set(0, "0");	
////Modrollen vergleichen  HIER WIEDERHOLT FEHLER; DAHER TRY CATCH
//		try {
//			for (int i=0; i < event.getMember().getRoles().size(); i++) {
//				UserRollen.add(event.getMember().getRoles().get(i).getId().toString());
//			}
//		} catch (Exception e) {
//			System.out.println(event.getMember().getUser().getName());
//			System.out.println(event.getMember().getUser().getId());
//			e.printStackTrace();
//		};
//		UserRollen.removeAll(ModRollen);
//		if (!(UserRollen.size() == event.getMember().getRoles().size()) || event.getGuild().getOwnerId().equals(event.getMember().getId())) {
//			Owner = true;
//		}
//		if (Owner) {
//			if (args[0].equalsIgnoreCase(Prefix + "AFKChannel")) {
//				AFKChannel.afkchannel(event, ini);
//			} else if (args[0].equalsIgnoreCase(Prefix + "activity")) {
//				Aktivität.aktivität(event, ini);
//			} else if (args[0].equalsIgnoreCase(Prefix + "Datei")) {
//				Datei.datei(event, ini);
//
//			
//		}
	}
}