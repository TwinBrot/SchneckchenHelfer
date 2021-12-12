package de.Strobl.Commands.Server;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Instances.SQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.ListedEmote;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Emotes {
	public static int counter;
	public static EmbedBuilder EmbedEmotes = new EmbedBuilder();
	private static final Logger logger = LogManager.getLogger(Emotes.class);

	public static void emotes(SlashCommandEvent event, InteractionHook EventHook) {
		try {
			counter = 0;
			MessageChannel channel = event.getChannel();
//Emotes der Guild
			List<ListedEmote> GuildEmotes = event.getGuild().retrieveEmotes().complete();
//Emotes der Datenbank
			Map<String, Integer> Emoteslist = SQL.emotesget();


//Emotes in SQL UND Guild
			Map<Emote, Integer> Emotelist = new HashMap<Emote, Integer>();
			GuildEmotes.forEach(ListedEmote -> {
				if (Emoteslist.containsKey(ListedEmote.getId()) && !ListedEmote.isManaged()) {
					Emotelist.put(ListedEmote, Integer.valueOf(Emoteslist.get(ListedEmote.getId())));
				}
			});
			EmbedEmotes = Discord.standardEmbed(Color.GREEN, "Emote-Auswertung", event.getGuild().getSelfMember().getId(), event.getGuild().getSelfMember().getEffectiveAvatarUrl());
			EmbedEmotes.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
// Emoteliste Sortieren und Feld im Embed hinzufügen
			Emotelist.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach((i) -> {
				counter++;
				EmbedEmotes.addField("", i.getKey().getAsMention() + Emoteslist.get(i.getKey().getId()), true);
// Embed abschicken und leeren, wenn durch 24 Teilbar oder Liste leer
				if (counter % 24 == 0 || counter == Emotelist.size()) {
					
					channel.sendMessageEmbeds(EmbedEmotes.build()).queue();
					EmbedEmotes.clearFields();
				}
			});
			EventHook.deleteOriginal().queue();
			
		} catch (Exception e) {
			event.getHook().editOriginal("Fehler beim Ausführen").queue();
			logger.error("Fehler beim Auswerten der Emotenutzung:", e);
		}
	}
}