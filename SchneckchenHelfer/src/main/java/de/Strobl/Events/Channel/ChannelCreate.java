package de.Strobl.Events.Channel;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChannelCreate extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(ChannelCreate.class);
	@Override
	public void onChannelCreate(ChannelCreateEvent event) {
		try {
		if (!event.isFromGuild()) {
			return;
		}
		if (!event.isFromType(ChannelType.TEXT)) {
			return;
		}
		Thread.sleep(1000);
		String id = event.getChannel().getId();
		TextChannel channel = event.getGuild().getTextChannelById(id);
		String topic = channel.getTopic();
		if (topic.startsWith("User ID:")) {
			topic = topic.replaceFirst("User ID: ", "");
			channel.sendMessage("<@" + topic + ">").queueAfter(2, TimeUnit.SECONDS);
		} else {
			return;
		}
		} catch (Exception e) {
			logger.error("Fehler Auto-Ping", e);
		}
	}
}
