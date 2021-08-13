package de.Strobl.Loops;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.JDA;

public class LoopMain extends Thread {
	public void run() {
		try {
			System.out.println("LoopThread startet");
			JDA jda = Main.jda;

			System.out.println("LoopThread wurde gestartet");
			System.out.println("");
			System.out.println("----------------------------------\n");
			System.out.println("Der Bot ist vollständig gestartet\n");
			System.out.println("----------------------------------");
			System.out.println("----------------------------------\n");

			while (true) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {

					System.err.println("");
					System.err.println("Fehler in Thread.sleep() der LoopMain!");
					System.err.println("");
					e.printStackTrace();
				}
				TempBan.run(jda);
				TempMute.run(jda);
			}

		} catch (Exception e) {
			System.err.println("");
			System.err.println("Allgemeiner Fehler in der LoopMain!");
			System.err.println("");
			e.printStackTrace();
		}
	}
}