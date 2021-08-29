package de.Strobl.Instances;

import org.apache.logging.log4j.Logger;

import de.Strobl.Main.Main;

public class PingPauseReset extends Thread {
	public void run() {
		Logger logger = Main.logger;
		try {
			Thread.sleep(300000);
			Main.PingPause = false;
		} catch (InterruptedException e) {
			logger.error("Pingpause Reset Fehler:", e);
		}
	}
}
