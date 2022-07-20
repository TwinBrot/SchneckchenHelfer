package de.Strobl.Events.Channel;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateTopicEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChannelCreate extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(ChannelCreate.class);

	@Override
	public void onChannelUpdateTopic(ChannelUpdateTopicEvent event) {
		try {
			if (!event.isFromGuild()) {
				return;
			}
			TextChannel channel = event.getChannel().asTextChannel();
			String topic = event.getNewValue();
			if (topic == null) {
				return;
			}
			if (topic.startsWith("User ID:")) {
				topic = topic.replaceFirst("User ID: ", "");
				channel.sendMessage("<@" + topic + ">").queueAfter(1000, TimeUnit.MILLISECONDS);
			} else {
				return;
			}
		} catch (Exception e) {
			logger.error("Fehler Auto-Ping", e);
		}
	}
}
