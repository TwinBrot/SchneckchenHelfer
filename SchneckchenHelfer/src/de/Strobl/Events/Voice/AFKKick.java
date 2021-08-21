package de.Strobl.Events.Voice;

import java.io.File;

import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AFKKick extends ListenerAdapter {
	public Wini ini;
	public String VoiceID;
	public String LogChannel;
	public void onGuildVoiceMove (GuildVoiceMoveEvent event) {
		try {
			ini = new Wini (new File(Main.Pfad + "settings.ini"));
			VoiceID = ini.get("Settings","Settings.AFKVoice");
			LogChannel = ini.get("Settings", "Settings.LogChannel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (event.getNewValue().getId().equals(VoiceID)) {
			try {
				event.getGuild().kickVoiceMember(event.getMember()).queue();
				if (!LogChannel.equals("")) {
		    		EmbedBuilder voicekick = new EmbedBuilder();
		    		voicekick.setColor(0x110acc);
					voicekick.setAuthor("AFK Kick", event.getGuild().getIconUrl(), event.getGuild().getIconUrl());
		    		voicekick.addField("User aus Voice gekickt", event.getMember().getEffectiveName() + " wurde in den AFK Channel verschoben und deswegen von mir aus dem Voice gekickt!", false);
		    		event.getGuild().getTextChannelById(LogChannel).sendMessageEmbeds(voicekick.build()).queue();
		    		event.getGuild().getTextChannelById(LogChannel).sendMessage("<@227131380058947584>").queue();
		    		voicekick.clear(); 
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}