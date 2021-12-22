package de.Strobl.Events.Channel;

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
			Thread.sleep(1000);
			String id = event.getChannel().getId();
			TextChannel channel = event.getGuild().getTextChannelById(id);
			String topic = event.getNewValue();
			if (topic == null) {
				return;
			}
			if (topic.startsWith("User ID:")) {
				topic = topic.replaceFirst("User ID: ", "");
				channel.sendMessage("<@" + topic + ">").queue();
			} else {
				return;
			}
		} catch (Exception e) {
			logger.error("Fehler Auto-Ping", e);
		}
	}
}
