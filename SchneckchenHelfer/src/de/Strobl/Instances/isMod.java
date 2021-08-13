package de.Strobl.Instances;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class isMod {
	public static Wini ini;
	public static Integer check (SlashCommandEvent event, InteractionHook EventHook) {
		
//-1 = Fehler		
//0 = User ist kein Mod
//1 = User ist Channelmod
//2 = User ist Mod
//3 = User ist Admin
		
		try {
		
//Owner
			if (event.getMember().isOwner()) {
				return 3;
			}
			
//Wini ini
			
			try {
				ini = new Wini (new File(Main.Pfad + "settings.ini"));
			} catch (IOException e1) {
				EventHook.editOriginal("Bei der Ausführung ist ein Fehler aufgetreten. Versuche es bitte erneut. Wenn das Problem dadurch nicht behoben wird, wende dich bitte an Twin.").queue();
				e1.printStackTrace();
				return -1;
			}
			
//Userrollen auslesen  HIER WIEDERHOLT FEHLER, DAHER TRY CATCH
			
			ArrayList <String> UserRollen = new ArrayList<String>();

			for (int i=0; i < event.getMember().getRoles().size(); i++) {
				UserRollen.add(event.getMember().getRoles().get(i).getId().toString());
			}
			
//Administrator
			
			if (!(ini.get("ModRollen", "Admin") == null)) {
				ArrayList <String> Admins = new ArrayList<String>();
				String[] Rollen = ini.get("ModRollen", "Admin").replaceAll("]", "").replaceAll("\\s", "").split(",");
				
				for (int i = 0; i < Rollen.length; i++ ){
					Admins.add(Rollen[i]);
				}
				
				Admins.set(0, "0");	
				UserRollen.removeAll(Admins);
				if (!(UserRollen.size() == event.getMember().getRoles().size())) {
					return 3;
				}
			}
			
			
//Moderator
			
			if (!(ini.get("ModRollen", "Mod") == null)) {
				ArrayList <String> Mods = new ArrayList<String>();
				String[] Rollen = ini.get("ModRollen", "Mod").replaceAll("]", "").replaceAll("\\s", "").split(",");
				
				for (int i = 0; i < Rollen.length; i++ ){
					Mods.add(Rollen[i]);
				}
				
				Mods.set(0, "0");	
				UserRollen.removeAll(Mods);
				if (!(UserRollen.size() == event.getMember().getRoles().size())) {
					return 2;
				}
			}
			
//Channelmod
			
			if (!(ini.get("ModRollen", "Channelmod") == null)) {
				ArrayList <String> Channelmods = new ArrayList<String>();
				String[] Rollen = ini.get("ModRollen", "Channelmod").replaceAll("]", "").replaceAll("\\s", "").split(",");
				
				for (int i = 0; i < Rollen.length; i++ ){
					Channelmods.add(Rollen[i]);
				}
				
				Channelmods.set(0, "0");	
				UserRollen.removeAll(Channelmods);
				if (!(UserRollen.size() == event.getMember().getRoles().size())) {
					return 1;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			EventHook.editOriginal("Bei der Ausführung ist ein Fehler aufgetreten. Versuche es bitte erneut. Wenn das Problem dadurch nicht behoben wird, wende dich bitte an Twin.").queue();
			return -1;
		}
		return 0;
	}
}
