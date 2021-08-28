package de.Strobl.Loops;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.JDA;

public class TempBan implements Runnable {
	@Override
	public void run() {
//		Logger logger = Main.logger;
		JDA jda = Main.jda;
		jda.getGuilds();
	}
}
