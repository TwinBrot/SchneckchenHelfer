package de.Strobl.Main;

import org.apache.logging.log4j.Logger;

import de.Strobl.Commands.Server.UserInfo.Info;
import de.Strobl.Exceptions.MissingPermException;
import de.Strobl.Instances.getMember;
import de.Strobl.Instances.isMod;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReceivedAuswertung extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Logger logger = Main.logger;
		if (event.getMessage().getContentRaw().startsWith("*warns")) {
			try {

				logger.info("Befehl erkannt:");
				logger.info("Author: " + event.getMember());
				logger.info("Befehl: " + event.getMessage().getContentRaw());

// Block Commands in Channels i cant Write in.

				if (!event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_WRITE, Permission.MESSAGE_READ)) {
					logger.error("TextChannel: " + event.getChannel());
					logger.error("Permissions: " + event.getGuild().getSelfMember().getPermissions());
					throw new MissingPermException("MISSING_PERM_EXCEPTION", "Kann keine Nachricht in " + event.getChannel().getName() + " schreiben.");
				}

// Modrollen Abfragen
//-1 = Fehler		
//0 = User
//1 = Channelmod
//2 = Mod
//3 = Admin

				Integer Modrolle = isMod.check(event.getMember());
				if (Modrolle == 0) {
					event.getChannel().sendMessage("Du hast nicht die notwendigen Rechte diesen Befehl auszuführen.").queue();
				} else if (Modrolle == -1) {
					event.getChannel().sendMessage("Bei der Ausführung ist ein Fehler aufgetreten. Wende dich bitte an Twin.").queue();
				} else if (Modrolle > 0) {
					String[] args = event.getMessage().getContentRaw().split("\\s+");
					Member member = getMember.getmember(event.getGuild(), args[1]);
					Info.messagereceivedevent(event, member);
				}
			} catch (MissingPermException e) {
				logger.error("Fehlende Berechtigungen:", e);
			} catch (Exception e) {
				logger.error("Fehler beim auswerten des Befehls", e);
			}
		}
	}
}