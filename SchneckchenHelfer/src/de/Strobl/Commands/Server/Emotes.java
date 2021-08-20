package de.Strobl.Commands.Server;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.ListedEmote;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Emotes {
	public static Wini emotesini;
	public static int counter;
	public static EmbedBuilder EmbedEmotes = new EmbedBuilder();

	public static void emotes(SlashCommandEvent event) {
		try {
			List<ListedEmote> GuildEmotes = event.getGuild().retrieveEmotes().complete();
			MessageChannel channel = event.getChannel();
//Ini initialisieren
			try {
				emotesini = new Wini(new File(Main.Pfad + "Emotes.ini"));
			} catch (Exception e) {
				channel.sendMessage("Fehler ini").queue();
				e.printStackTrace();
				return;
			}
//emotes.ini in Liste packen
			Map<Emote, Integer> Emotelist = new HashMap<Emote, Integer>();
			GuildEmotes.forEach(ListedEmote -> {
				if ((emotesini.get("Emotes", ListedEmote.getId()) != null) && !ListedEmote.isManaged()) {
					Emotelist.put(ListedEmote, Integer.valueOf(emotesini.get("Emotes", ListedEmote.getId())));
				}
			});
//Emoteliste Sortieren und Feld im Embed hinzufügen
			Emotelist.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach((i) -> {
				counter++;
				EmbedEmotes.addField("", i.getKey().getAsMention() + emotesini.get("Emotes", i.getKey().getId()), true);
				int EmbedCount = Integer.parseInt(emotesini.get("Embed", "Count"));
//Embed abschicken und leeren, wenn durch 24 Teilbar oder Liste leer
				if (counter % EmbedCount == 0 || counter == Emotelist.size()) {
					EmbedEmotes.setAuthor(event.getJDA().getSelfUser().getName() + " Emote-Auswertung",
							event.getGuild().getIconUrl(), event.getGuild().getIconUrl());
					EmbedEmotes.setColor(0x00c42b);
					channel.sendMessageEmbeds(EmbedEmotes.build()).queue();
					EmbedEmotes.clear();
				}
			});
		} catch (Exception e) {
			event.getHook().editOriginal("Fehler beim Ausführen").queue();
			e.printStackTrace();
		}
	}
}