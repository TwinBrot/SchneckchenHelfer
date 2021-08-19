package de.Strobl.Loops;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.JDA;

public class TempMute implements Runnable {
	@Override
	public void run() {
		JDA jda = Main.jda;
		jda.getGuilds();
	}
}