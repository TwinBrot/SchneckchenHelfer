package de.Strobl.Instances;

public enum StrafenTyp {

	HINWEIS("Hinweis"), WARN("Warn"), KICK("Kick"), MUTE("Mute"), TEMPBAN("Tempban"), BAN("Ban");

	String string;

	StrafenTyp(String temp) {
		string = temp;
	}

	public String toString() {
		return string;
	}
}
