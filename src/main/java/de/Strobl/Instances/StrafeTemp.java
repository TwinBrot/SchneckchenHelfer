package de.Strobl.Instances;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

public class StrafeTemp {
	private static final Logger logger = LogManager.getLogger(StrafeTemp.class);

	String id;
	String userid;
	StrafenTyp typ;
	DateTime unbantime;
	Boolean saved = false;

	public StrafeTemp(Strafe strafe, DateTime unbantime) {
		this.id = strafe.getId();
		this.typ = strafe.getTyp();
		this.userid = strafe.getUserId();
		this.unbantime = unbantime;
	}

	private StrafeTemp(String id, String userid, StrafenTyp typ, DateTime unbantime) {
		this.id = id;
		this.userid = userid;
		this.typ = typ;
		this.unbantime = unbantime;
		this.saved = true;
	}

	public static List<StrafeTemp> getAll() throws SQLException {
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery("select * from temp;");
		List<StrafeTemp> temp = new ArrayList<StrafeTemp>();
		while (rs.next()) {
			temp.add(new StrafeTemp(rs.getString("id"), rs.getString("userid"), getEnum(rs.getString("typ")), DateTime.parse(rs.getString("time"))));
		}
		rs.close();
		conn.close();
		return temp;
	}

	public static List<StrafeTemp> getByUserId(String userid) throws SQLException {
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery("select * from temp where userid = '" + userid + "';");
		List<StrafeTemp> temp = new ArrayList<StrafeTemp>();
		while (rs.next()) {
			temp.add(new StrafeTemp(rs.getString("id"), rs.getString("userid"), getEnum(rs.getString("typ")), DateTime.parse(rs.getString("time"))));
		}
		rs.close();
		conn.close();
		return temp;
	}

	public StrafeTemp updateTime(DateTime time) throws SQLException {
		this.unbantime = time;
		this.save();
		return this;
	}
	
	public DateTime getDateTime() {
		return this.unbantime;
	}

	public StrafenTyp getStrafenTyp() {
		return this.typ;
	}

	public String getUserID() {
		return this.userid;
	}

	public String getID() {
		return this.id;
	}

	public void delete() throws SQLException {
		logger.info("Entferne Temp-Strafe aus der Datenbank:");
		logger.info("ID = " + id);
		remove();
		this.id = null;
		this.typ = null;
		this.unbantime = null;
		this.userid = null;
	}

	private void remove() throws SQLException {
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();
		stat.executeUpdate("delete from temp where id = '" + id + "';");
		this.saved = false;
		conn.close();
	}

	public StrafeTemp save() throws SQLException {
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();
		if (saved) {
			remove();
		}
		List<StrafeTemp> list = getByUserId(this.userid);
		list.forEach(temp -> {
			try {
				if (temp.getStrafenTyp() == this.typ) {
					temp.delete();
				}
			} catch (SQLException e) {
				logger.error("Fehler beim löschen alter TEMP-Strafe", e);
			}
		});
		stat.executeUpdate("INSERT INTO temp (id, userid, typ, time) VALUES ('" + id + "', '" + userid + "','" + typ.toString() + "','" + unbantime + "')");
		conn.close();
		stat.close();
		this.saved = true;
		return this;
	}

	public static DateTime fromString(String ZeitRaw) {
		DateTime unbantime = DateTime.now();
		if (ZeitRaw == "") {
			return null;
		}
		for (int i = 0; i < ZeitRaw.length(); i++) {
			i--;
			Integer merker = 0;
			Integer length = ZeitRaw.length();
			for (int i1 = 0; i1 < length; i1++) {
				try {
					merker = Integer.parseInt(merker + "" + Integer.parseInt("" + ZeitRaw.charAt(0)));
					ZeitRaw = ZeitRaw.replaceFirst(ZeitRaw.charAt(0) + "", "");
				} catch (Exception e) {
					if (i1 == 0) {
						return null;
					}
					i1 = length;
				}
			}
			if (ZeitRaw.startsWith("y")) {
				unbantime = unbantime.plusYears(merker);
				ZeitRaw = ZeitRaw.replaceFirst("y", "");
			} else if (ZeitRaw.startsWith("mon")) {
				unbantime = unbantime.plusMonths(merker);
				ZeitRaw = ZeitRaw.replaceFirst("mon", "");
			} else if (ZeitRaw.startsWith("w")) {
				unbantime = unbantime.plusWeeks(merker);
				ZeitRaw = ZeitRaw.replaceFirst("w", "");
			} else if (ZeitRaw.startsWith("d")) {
				unbantime = unbantime.plusDays(merker);
				ZeitRaw = ZeitRaw.replaceFirst("d", "");
			} else if (ZeitRaw.startsWith("h")) {
				unbantime = unbantime.plusHours(merker);
				ZeitRaw = ZeitRaw.replaceFirst("h", "");
			} else if (ZeitRaw.startsWith("min")) {
				unbantime = unbantime.plusMinutes(merker);
				ZeitRaw = ZeitRaw.replaceFirst("min", "");
			} else {
				return null;
			}
		}
		return unbantime;
	}

	private static StrafenTyp getEnum(String typ) {
		switch (typ) {
		case "Ban":
			return StrafenTyp.BAN;
		case "Tempan":
			return StrafenTyp.TEMPBAN;
		case "Mute":
			return StrafenTyp.MUTE;
		case "Kick":
			return StrafenTyp.KICK;
		case "Warn":
			return StrafenTyp.WARN;
		case "Hinweis":
			return StrafenTyp.HINWEIS;
		}
		return null;
	}
}
