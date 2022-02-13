package de.Strobl.Commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReceivedFunsies extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(MessageReceivedFunsies.class);

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		try {
//Server
			if (event.isFromGuild()) {
				if (!event.getMember().getId().equals("227131380058947584")) {
					return;
				}
				if ((event.getMessage().getContentRaw().startsWith("<@729067250954403890>") || event.getMessage().getContentRaw().startsWith("<@!729067250954403890>"))) {
					String m = event.getMessage().getContentRaw();
					m = m.replaceAll("<@729067250954403890> ", "").replaceAll("<@!729067250954403890> ", "");
					
					if (m.equalsIgnoreCase("danke")) {
						event.getMessage().reply(event.getMember().getAsMention() + " gerngeschehen").queue();
					} else if (m.equalsIgnoreCase("was denkst du von yeehaw?")) {
						event.getMessage().reply("Leider pingt sie mich die ganze Zeit. Das nervt mich komplett.....Aber sie hört einfach nicht auf mich <@714767360069861417> 😡").queue();
					}
				}
				
//DM
			} else {
				if (event.getAuthor().getId().equals("464487654122061824")) {
					event.getMessage().reply("Hai Susi 😍😘🥰🤩☺️😚").queue();
				} else if (event.getAuthor().getId().equals("714767360069861417")) {
					event.getMessage().reply("Lass mich doch bitte endlich in ruhe 😞😞😞😞😞😞😞😞😞😞  Ich bin doch mit Susi zusammen").queue();
				}
			}
		} catch (Exception e) {
			logger.error("Funsies Error", e);
		}
	}
}