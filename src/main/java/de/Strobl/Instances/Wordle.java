package de.Strobl.Instances;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import de.Strobl.Main.Settings;

public class Wordle {
	public static String classname = SQL.classname;
	public static String connectionname = SQL.connectionname;
	private static final Logger logger = LogManager.getLogger(Wordle.class);

	public DateTime datum;
	public Integer streak;
	public String Wort1;
	public String Wort2;
	public String Wort3;
	public String Wort4;
	public String Wort5;
	public String Wort6;
	public Long UserID;
	public Boolean finished;

//From Database
	public Wordle(Long userID) throws SQLException {
		try {
			this.UserID = userID;
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("select * from wordle where UserID = '" + userID + "';");
			if (!rs.next()) {
				return;
			}
			;
			this.datum = DateTime.parse(rs.getString("Datum"));
			this.streak = Integer.parseInt(rs.getString("Streak"));
			this.Wort1 = rs.getString("Wort1");
			this.Wort2 = rs.getString("Wort2");
			this.Wort3 = rs.getString("Wort3");
			this.Wort4 = rs.getString("Wort4");
			this.Wort5 = rs.getString("Wort5");
			this.Wort6 = rs.getString("Wort6");
			if (this.Wort1.equals("null"))
				this.Wort1 = null;
			if (this.Wort2.equals("null"))
				this.Wort2 = null;
			if (this.Wort3.equals("null"))
				this.Wort3 = null;
			if (this.Wort4.equals("null"))
				this.Wort4 = null;
			if (this.Wort5.equals("null"))
				this.Wort5 = null;
			if (this.Wort6.equals("null"))
				this.Wort6 = null;
			if (rs.getString("finished").equals("true")) {
				this.finished = true;
			} else {
				this.finished = false;
			}
			rs.close();
			stat.close();
			conn.close();
		} catch (Exception e) {
			logger.fatal("Fehler beim Auslesen der Wordle-Datenbank:", e);
			throw new SQLException();
		}
	}

	// Creates new Wordle Word
	public static void newWord() {
		try {
			List<String> solutions = solutions();
			int size = solutions.size();
			Random rand = new Random();
			int random = rand.nextInt(size);
			Settings.set("Settings", "Wordle", solutions.get(random));
		} catch (IOException e) {
			logger.error("Fehler neues Wordle Wort", e);
		}
	}

	// Create New
	public void newWordle() throws Exception {
		this.streak = 0;
		this.datum = DateTime.now();
		this.Wort1 = null;
		this.Wort2 = null;
		this.Wort3 = null;
		this.Wort4 = null;
		this.Wort5 = null;
		this.Wort6 = null;
		this.finished = false;
		save();
	}

	// Update
	public void updateWordle() throws Exception {
		if (this.datum.plusDays(2).withTimeAtStartOfDay().isAfterNow()) {
			this.streak = 0;
		}
		this.datum = DateTime.now();
		this.Wort1 = null;
		this.Wort2 = null;
		this.Wort3 = null;
		this.Wort4 = null;
		this.Wort5 = null;
		this.Wort6 = null;
		this.finished = false;
		save();
	}

	public String createPattern() {
		String pattern = "";
		if (Wort1 != null) {
			pattern = pattern + patternline(Wort1) + "\n";
		}
		if (Wort2 != null) {
			pattern = pattern + patternline(Wort2) + "\n";
		}
		if (Wort3 != null) {
			pattern = pattern + patternline(Wort3) + "\n";
		}
		if (Wort4 != null) {
			pattern = pattern + patternline(Wort4) + "\n";
		}
		if (Wort5 != null) {
			pattern = pattern + patternline(Wort5) + "\n";
		}
		if (Wort6 != null) {
			pattern = pattern + patternline(Wort6) + "\n";
		}
		return pattern;
	}

	private static String patternline(String word) {
		String line = "";
		String sol = Settings.currentword;
		char solchar1 = sol.charAt(0);
		char ischar1 = word.charAt(0);
		line = linechar(sol, ischar1, solchar1, line);
		char solchar2 = sol.charAt(1);
		char ischar2 = word.charAt(1);
		line = linechar(sol, ischar2, solchar2, line);
		char solchar3 = sol.charAt(2);
		char ischar3 = word.charAt(2);
		line = linechar(sol, ischar3, solchar3, line);
		char solchar4 = sol.charAt(3);
		char ischar4 = word.charAt(3);
		line = linechar(sol, ischar4, solchar4, line);
		char solchar5 = sol.charAt(4);
		char ischar5 = word.charAt(4);
		line = linechar(sol, ischar5, solchar5, line);
		return line;
	}

	private static String linechar(String sol, char ischar, char solchar, String line) {
		HashMap<String, String> correct = new HashMap<>();
		correct.put("A", "🇦");
		correct.put("B", "🇧");
		correct.put("C", "🇨");
		correct.put("D", "🇩");
		correct.put("E", "🇪");
		correct.put("F", "🇫");
		correct.put("G", "🇬");
		correct.put("H", "🇭");
		correct.put("I", "🇮");
		correct.put("J", "🇯");
		correct.put("K", "🇰");
		correct.put("L", "🇱");
		correct.put("M", "🇲");
		correct.put("N", "🇳");
		correct.put("O", "🇴");
		correct.put("P", "🇵");
		correct.put("Q", "🇶");
		correct.put("R", "🇷");
		correct.put("S", "🇸");
		correct.put("T", "🇹");
		correct.put("U", "🇺");
		correct.put("V", "🇻");
		correct.put("W", "🇼");
		correct.put("X", "🇽");
		correct.put("Y", "🇾");
		correct.put("Z", "🇿");

		HashMap<String, String> wrongpos = new HashMap<>();
		wrongpos.put("A", "🄰");
		wrongpos.put("B", "🄱");
		wrongpos.put("C", "🄲");
		wrongpos.put("D", "🄳");
		wrongpos.put("E", "🄴");
		wrongpos.put("F", "🄵");
		wrongpos.put("G", "🄶");
		wrongpos.put("H", "🄷");
		wrongpos.put("I", "🄸");
		wrongpos.put("J", "🄹");
		wrongpos.put("K", "🄺");
		wrongpos.put("L", "🄻");
		wrongpos.put("M", "🄼");
		wrongpos.put("N", "🄽");
		wrongpos.put("O", "🄾");
		wrongpos.put("P", "🄿");
		wrongpos.put("Q", "🅀");
		wrongpos.put("R", "🅁");
		wrongpos.put("S", "🅂");
		wrongpos.put("T", "🅃");
		wrongpos.put("U", "🅄");
		wrongpos.put("V", "🅅");
		wrongpos.put("W", "🅆");
		wrongpos.put("X", "🅇");
		wrongpos.put("Y", "🅈");
		wrongpos.put("Z", "🅉");

		char[] solchars = sol.toCharArray();
		Boolean boolcor = (solchar == ischar);
		Boolean cont = (solchars[0] == ischar || solchars[1] == ischar || solchars[2] == ischar || solchars[3] == ischar || solchars[4] == ischar)&& !boolcor;
		Boolean wrong = !(boolcor || cont);

		if (boolcor) {
			line = line + " " + correct.get(ischar + "") + " ";
		}
		if (cont) {
			line = line + " " + wrongpos.get(ischar + "") + " ";
		}
		if (wrong) {
			line = line + "   " + ischar + "   ";
		}

		return line;
	}

	public Wordle save() throws Exception {
		Class.forName(classname);
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();
		stat.executeUpdate("delete from wordle where userid = '" + UserID.toString() + "';");
		stat.executeUpdate("INSERT INTO Wordle (UserID, finished, Streak, Datum, Wort1, Wort2, Wort3, Wort4, Wort5, Wort6) VALUES ('" + UserID + "', '" + finished + "','" + streak + "','" + datum
				+ "','" + Wort1 + "','" + Wort2 + "','" + Wort3 + "','" + Wort4 + "','" + Wort5 + "','" + Wort6 + "')");
		stat.close();
		conn.close();
		return this;
	}

	public static List<String> words() {
		List<String> list = Arrays.asList("AACHS", "AADEN", "AAKEN", "AAKES", "AALEN", "AALES", "AALLS", "AALST", "AAPEN", "AAPES", "AARAU", "AARES", "AARON", "AASEE", "AASEN", "AASER", "AASES",
				"ABACA", "ABADE", "ABAKA", "ABAKI", "ABART", "ABASA", "ABASI", "ABATA", "ABATE", "ABATI", "ABBAS", "ABBOS", "ABBTS", "ABDAS", "ABDIS", "ABDON", "ABECE", "ABEES", "ABELE", "ABELN",
				"ABELS", "ABENS", "ABERN", "ABERS", "ABIAM", "ABIAS", "ABILO", "ABLAD", "ABNER", "ABORT", "ABRAM", "ABRIN", "ABRIS", "ABTES", "ABUJA", "ABWEG", "ABZYM", "ACCRA", "ACERS", "ACHAT",
				"ACHAU", "ACHAZ", "ACHEL", "ACHER", "ACHIM", "ACHTE", "ACIDS", "ACKJA", "ACNEN", "ACRES", "ACTIO", "ACYLE", "ACYLS", "ADALA", "ADAMA", "ADAME", "ADAMS", "ADANA", "ADDAX", "ADDIS",
				"ADDOS", "ADELE", "ADELS", "ADENS", "ADEPT", "ADIEU", "ADIGE", "ADJES", "ADMIN", "ADNER", "ADNEX", "ADOBE", "ADOLF", "ADORF", "ADOUR", "ADRAM", "ADRAR", "ADRIA", "ADWIN", "AEDIL",
				"AETIT", "AFARS", "AFFIX", "AFRAS", "AFROS", "AFTER", "AGADE", "AGARS", "AGAVE", "AGENS", "AGGER", "AGGES", "AGGIS", "AGGOS", "AGHAS", "AGIEN", "AGIOS", "AGNAT", "AGNES", "AGONE",
				"AGONS", "AGORA", "AGRAM", "AGRAS", "AGRIA", "AGUTI", "AHAUS", "AHERN", "AHLEN", "AHLFF", "AHMAD", "AHNES", "AHNIN", "AIGEN", "AINUS", "AIOLI", "AISCH", "AITEL", "AJKAS", "AJVAR",
				"AKABA", "AKENS", "AKIAS", "AKITA", "AKKAD", "AKKUS", "AKNEN", "AKOLA", "AKREN", "AKTES", "AKTIN", "AKTOR", "AKUTE", "AKUTS", "AKYNE", "AKYNS", "ALAND", "ALANE", "ALANT", "ALAUN",
				"ALBAN", "ALBAS", "ALBEN", "ALBES", "ALBIT", "ALBUS", "ALENA", "ALERT", "ALETE", "ALETS", "ALEXA", "ALFAS", "ALFES", "ALGER", "ALGES", "ALGIS", "ALGOL", "ALIEN", "ALIJA", "ALINA",
				"ALITA", "ALIUD", "ALKAN", "ALKEN", "ALKER", "ALKES", "ALKIN", "ALKIS", "ALLAH", "ALLEN", "ALLER", "ALLOD", "ALMAR", "ALMAS", "ALMEN", "ALNOT", "ALOEN", "ALOIS", "ALPHA", "ALSEN",
				"ALTAI", "ALTAN", "ALTAS", "ALTEM", "ALTEN", "ALTES", "ALTIN", "ALTIS", "ALTOS", "ALUNE", "ALWIN", "ALWIS", "ALZEY", "AMARA", "AMBAI", "AMBEN", "AMBER", "AMBOS", "AMBRA", "AMENS",
				"AMERO", "AMIDE", "AMIDS", "AMIGA", "AMIGO", "AMINA", "AMINE", "AMINS", "AMISH", "AMMAN", "AMMEI", "AMMEN", "AMMER", "AMMON", "AMOKS", "AMORS", "AMPER", "AMREI", "AMRUM", "AMTES",
				"AMUNS", "AMURS", "ANBOT", "ANDAU", "ANDEL", "ANDEN", "ANDON", "ANGER", "ANHUI", "ANICH", "ANIME", "ANION", "ANISE", "ANKEN", "ANNAM", "ANNAS", "ANNEX", "ANODE", "ANTAU", "ANTON",
				"ANYON", "AOSTA", "APELS", "APHEL", "APIAN", "APIAS", "APNOE", "APOLL", "APPLE", "APSIS", "ARADS", "ARAKE", "ARAKS", "ARBAT", "ARCHE", "ARENE", "ARENS", "ARFST", "ARGEN", "ARGON",
				"ARGOT", "ARGUS", "ARIAN", "ARIEN", "ARIER", "ARLTS", "ARMAN", "ARMEM", "ARMEN", "ARMER", "ARMES", "ARMIN", "ARNDT", "ARNIS", "ARNOS", "AROSA", "ARPAD", "ARRAK", "ARRAY", "ARRHA",
				"ARSCH", "ARSEN", "ARTEL", "ARTUR", "ARUBA", "ARVEN", "ARZTE", "ARZTS", "ASANT", "ASCHS", "ASCOT", "ASCUS", "ASERN", "ASERS", "ASIEN", "ASKET", "ASKUS", "ASPIE", "ASPIK", "ASSAM",
				"ASSEN", "ASSER", "ASSES", "ASSIS", "ASTAT", "ASTER", "ASTES", "ATAIR", "ATEMS", "ATHEN", "ATHOS", "ATMAN", "ATOMS", "ATOUT", "ATZEN", "AUDIS", "AUDIT", "AUERS", "AUGES", "AUGIT",
				"AUGUR", "AULEN", "AULET", "AULOI", "AULOS", "AUMAS", "AURAR", "AUREN", "AUSSI", "AUTIE", "AUTUN", "AUXIN", "AVALE", "AVALS", "AVARE", "AVERS", "AVISE", "AVISO", "AWARD", "AWARE",
				"AXELN", "AXELS", "AXONE", "AXONS", "AYOLD", "AZIDE", "AZIDS", "AZINE", "AZINS", "AZIUS", "AZOLE", "AZOLS", "AZONE", "AZUBI", "AZURS", "AZYMA", "AOEDE", "BAACK", "BAADE", "BAAKS",
				"BAALS", "BAARS", "BAASE", "BABAS", "BABEL", "BABOS", "BABSI", "BABUS", "BABYS", "BACHS", "BACKS", "BACON", "BADER", "BADES", "BADIS", "BAGEL", "BAGNI", "BAGNO", "BAHAI", "BAHNE",
				"BAHOE", "BAIAO", "BAIEN", "BAITS", "BAKEL", "BAKEN", "BAKUS", "BALDA", "BALDO", "BALGE", "BALGS", "BALIS", "BALLE", "BALLS", "BALSA", "BALTE", "BALVE", "BALYK", "BAMBI", "BANAT",
				"BANDA", "BANDS", "BANEN", "BANJA", "BANJO", "BANNE", "BANNS", "BANSE", "BARBE", "BARBI", "BARBY", "BARDO", "BAREM", "BAREN", "BARES", "BARGS", "BARND", "BARRE", "BARTE", "BARTH",
				"BARTS", "BARYT", "BASEL", "BASEN", "BASIL", "BASKE", "BASRA", "BASSE", "BASTE", "BATIK", "BATON", "BAUDE", "BAUES", "BAULE", "BAUME", "BAUMS", "BAUTE", "BAYER", "BAZIS", "BEACH",
				"BEAMS", "BEATA", "BEATE", "BEATS", "BEAUS", "BEBBI", "BEBOP", "BEBRA", "BECKE", "BECKS", "BEDEN", "BEECK", "BEEPS", "BEETE", "BEETS", "BEGUM", "BEHUF", "BEIDL", "BEIEN", "BEILE",
				"BEILS", "BEINS", "BEISL", "BELLO", "BELTE", "BELTS", "BEMAS", "BEMBA", "BEMME", "BENIN", "BERGS", "BERIT", "BERME", "BERND", "BERNS", "BERNT", "BESAN", "BESCH", "BESTE", "BETAS",
				"BETEL", "BETER", "BETTE", "BETTS", "BEYEN", "BEYER", "BIAKS", "BIBAX", "BIDET", "BIENS", "BIERE", "BIERS", "BIESE", "BIGEN", "BIGOS", "BIHAR", "BIJOU", "BIKER", "BIKES", "BILCH",
				"BILDE", "BILDS", "BILGE", "BILLE", "BIMBO", "BIMSE", "BINGE", "BINGO", "BINOM", "BINSE", "BIOME", "BIOMS", "BIRKS", "BIRMA", "BIRTE", "BISEN", "BITKI", "BITOK", "BITZE", "BIWAS",
				"BLAGE", "BLAGS", "BLAHE", "BLAUS", "BLEIE", "BLEIS", "BLETZ", "BLIDE", "BLIES", "BLINI", "BLOCH", "BLOGS", "BLUES", "BLUFF", "BLUNT", "BLUTE", "BLUTS", "BLUEN", "BOBBY", "BOBOT",
				"BOCKE", "BOCKS", "BODYS", "BOGEY", "BOHEI", "BOHLE", "BOIER", "BOJAR", "BOJEN", "BOLAS", "BOLDO", "BOLID", "BOLLE", "BOMBO", "BONGO", "BONGS", "BONNS", "BONZE", "BOOMS", "BOOTS",
				"BORAS", "BORDE", "BORDS", "BORGE", "BORGS", "BORIS", "BORNA", "BORNE", "BORNS", "BOSNA", "BOSON", "BOSSE", "BOTIN", "BOTOX", "BOWLE", "BOWLS", "BOZEN", "BRAAI", "BRACK", "BRARE",
				"BRARS", "BRASS", "BRAST", "BRATS", "BRAVI", "BRAVO", "BREAK", "BREIE", "BREIS", "BREME", "BRENZ", "BREST", "BREVE", "BREZE", "BREZN", "BRIAN", "BRIES", "BRIGG", "BRIME", "BRINK",
				"BRITS", "BROMS", "BRONX", "BRONY", "BROTS", "BRUCK", "BRUMM", "BRUNO", "BRAET", "BUACH", "BUBIS", "BUCHS", "BUDEL", "BUDEN", "BUDOS", "BUFFI", "BUFFO", "BUFFS", "BUGEN", "BUGES",
				"BUGGY", "BUHEI", "BUHLE", "BUHNE", "BULGE", "BULIN", "BULLA", "BULWE", "BUNDA", "BUNDE", "BUNDS", "BUNGE", "BURAN", "BUREN", "BURGI", "BURIN", "BURKA", "BURKE", "BURMA", "BURRO",
				"BURSE", "BURST", "BUSAN", "BUSEN", "BUSES", "BUSSE", "BUSSI", "BUTAN", "BUTEN", "BUTIN", "BUTOR", "BUTTE", "BUTTS", "BUTYL", "BUTZE", "BUXEN", "BYTES", "BAERE", "BAERS", "BAETZ",
				"BOEEL", "BOEEN", "BOEGE", "BOEHM", "BOETE", "BUEGE", "BUEHL", "BUESI", "BUETT", "CABAN", "CACHE", "CAENS", "CAIPI", "CAJON", "CAJUS", "CALAU", "CALBE", "CALLS", "CALWS", "CAMEO",
				"CAMPS", "CAPAS", "CAPES", "CAPRI", "CARGO", "CARLS", "CARPS", "CASTS", "CASUS", "CAUSA", "CAVAN", "CAVAS", "CAVUM", "CEGOS", "CELLA", "CELLE", "CELLI", "CENTS", "CERCI", "CERES",
				"CEUTA", "CHANE", "CHANS", "CHARM", "CHARS", "CHART", "CHATS", "CHECK", "CHEDI", "CHEFS", "CHEMO", "CHEWA", "CHICK", "CHICS", "CHILE", "CHINA", "CHINO", "CHIPS", "CHOKE", "CHONZ",
				"CHORE", "CHORS", "CHOSE", "CHRIS", "CHUNK", "CHURS", "CIDRE", "CILLI", "CINDY", "CIRCE", "CITYS", "CIZEK", "CLAAS", "CLAIM", "CLANE", "CLANS", "CLARA", "CLARE", "CLASH", "CLAUS",
				"CLIPS", "CLOGS", "CLOUD", "CLOUS", "CLUBS", "COBLA", "COBOT", "COCAS", "COCOS", "CODAS", "CODES", "CODEX", "CODON", "COLAS", "COMBO", "COMIC", "COOKS", "CORAS", "CORDS", "CORKS",
				"CORPS", "COTTE", "COUNT", "COUPE", "COUPS", "COURT", "COVER", "COVID", "CRACK", "CRASH", "CREDO", "CREEK", "CREMA", "CREME", "CREWS", "CRIME", "CROON", "CROSS", "CRWTH", "CURIA",
				"CURIE", "CURRY", "CYANS", "CYRUS", "DACHE", "DACKE", "DADAS", "DADDY", "DAEGU", "DAESH", "DAGGA", "DAHLS", "DAHME", "DAHNS", "DAINA", "DAKAR", "DAKER", "DALLE", "DALLS", "DAMME",
				"DAMMS", "DAMNA", "DANDY", "DANKS", "DARME", "DARMS", "DARRE", "DARTS", "DARSS", "DATEN", "DATES", "DAUBE", "DAUEN", "DAUNS", "DAVID", "DAVIT", "DAVOS", "DAXEN", "DAXES", "DEALS",
				"DEBET", "DECAN", "DECKS", "DEERN", "DEGUS", "DEIST", "DEKAS", "DELIR", "DEMEN", "DEMOS", "DEMSE", "DENAR", "DENDI", "DENKE", "DEPPE", "DEPPS", "DERBY", "DETOX", "DEUCE", "DEVON",
				"DEZEN", "DEZES", "DEZIL", "DHAKA", "DIANA", "DIEBS", "DIEHL", "DIEME", "DIETA", "DIKTA", "DILDO", "DILIS", "DILLE", "DILLS", "DIMER", "DIMLI", "DINER", "DINGI", "DINGS", "DINOS",
				"DIODE", "DIPOL", "DIREX", "DIRKS", "DIRNE", "DISKO", "DIVAS", "DIVEN", "DIVIS", "DIWAN", "DIXIE", "DIXIS", "DOCKE", "DOCKS", "DODOS", "DOGEN", "DOGON", "DOHAS", "DOHNA", "DOJOS",
				"DOKUS", "DOLDE", "DOLEN", "DOLES", "DOLLE", "DOMEN", "DOMES", "DONAR", "DONAU", "DONEZ", "DONGA", "DONUT", "DOOFI", "DOPES", "DORER", "DORFE", "DORFS", "DORIS", "DORIT", "DORNE",
				"DORNS", "DORSA", "DOSHA", "DOSSE", "DOSTE", "DOSTS", "DOURO", "DOVER", "DOYEN", "DRAFT", "DRANN", "DREFS", "DREHE", "DREHS", "DRESS", "DRIFT", "DRINK", "DRISS", "DRIVE", "DROHN",
				"DROPS", "DRUDE", "DRUSE", "DUALE", "DUALS", "DUBAI", "DUCES", "DUCHT", "DUDEN", "DUERO", "DUFFS", "DUFTE", "DUFTS", "DUMAS", "DUMMI", "DUMMY", "DUMPS", "DUNGE", "DUNGS", "DUPLA",
				"DUQUE", "DURAL", "DURIT", "DURRA", "DUSEL", "DUSTE", "DUSTS", "DUTTE", "DUTTS", "DUVET", "DUZEN", "DWEIL", "DWOGS", "DYADE", "DYULA", "DAENE", "DOENS", "DOETZ", "DUENA", "EAGLE",
				"EARLS", "EBART", "EBBEN", "EBBES", "EBBYS", "EBELS", "EBERN", "EBERS", "EBERT", "EBNIT", "EBOLA", "EBROS", "ECHOS", "ECKER", "ECKES", "EDAMS", "EDDAS", "EDDES", "EDENS", "EDERS",
				"EDITH", "EDNAS", "EDUKT", "EFEUS", "EFFET", "EGART", "EGELN", "EGELS", "EGERS", "EGGEN", "EGGER", "EGONS", "EHLEN", "EHLIN", "EIBEN", "EIDAM", "EIDEN", "EIDER", "EIDES", "EIERN",
				"EIFEL", "EIGER", "EIGGS", "EISES", "EIZES", "EISSE", "EKELS", "ELAMS", "ELANS", "ELBAS", "ELBEN", "ELBES", "ELBIN", "ELCHE", "ELCHS", "ELEAS", "ELENA", "ELFEN", "ELFER", "ELFIN",
				"ELGER", "ELIAS", "ELIMS", "ELISE", "ELLEN", "ELOAH", "ELOGE", "ELPEL", "ELSPE", "ELTER", "ELZES", "EMAIL", "EMDEN", "EMDES", "EMIRE", "EMIRS", "EMMAS", "EMMER", "EMMYS", "EMOJI",
				"EMPLS", "EMSEN", "ENDES", "ENGEN", "ENGER", "ENNIS", "ENNUI", "ENZEN", "ENZIN", "EPHOR", "ERATO", "ERBES", "ERICA", "ERICH", "ERIKA", "ERLAA", "ERLAG", "ERLEN", "ERSJA", "ERSTI",
				"ERTAG", "ERWIN", "ERZEN", "ERZES", "ESELN", "ESELS", "ESENS", "ESNAS", "ESPAN", "ESPEN", "ESSAI", "ESSAY", "ESSIV", "ESTEN", "ESTER", "ESTES", "ESTIN", "ETATS", "ETHAN", "ETHEN",
				"ETHER", "ETHIN", "ETMAL", "ETSCH", "ETTER", "ETUIS", "ETYMA", "EUGEN", "EULAN", "EULER", "EUMEL", "EUPEN", "EUROS", "EUTIN", "EVENT", "EWENE", "EWERT", "EXILE", "EXILS", "EXONS",
				"EXPOS", "EYRIR", "EZZES", "FABER", "FABRI", "FACHE", "FACHS", "FAINA", "FAKES", "FAKTA", "FAKTS", "FALBE", "FALLS", "FALUN", "FALZE", "FANAL", "FANGE", "FANGO", "FANGS", "FANIN",
				"FANON", "FANTE", "FANTS", "FARAD", "FARNE", "FARNS", "FARRE", "FARSI", "FASEL", "FASEN", "FASSE", "FATUM", "FATWA", "FAULE", "FAUNE", "FAUNI", "FAUNS", "FAXEN", "FEBER", "FEDDO",
				"FEEDS", "FEGER", "FEHEN", "FEHES", "FEHLE", "FEHLS", "FEIME", "FEIMS", "FEIST", "FELDE", "FELDS", "FELIX", "FELLE", "FELLS", "FEMEL", "FEMEN", "FEMUR", "FENCE", "FENNE", "FENNS",
				"FERGE", "FERID", "FESEN", "FESES", "FESTS", "FETAS", "FETEN", "FETTE", "FETTS", "FETUS", "FETWA", "FEXEN", "FEXES", "FEZEN", "FEZES", "FIALE", "FIBEL", "FIBER", "FICKE", "FICKS",
				"FIDUZ", "FIETE", "FIGHT", "FILET", "FILMS", "FILOU", "FILZE", "FINAL", "FINCA", "FINKS", "FIONA", "FIRNE", "FIRNS", "FIRST", "FITIS", "FIXEN", "FIXER", "FIXES", "FIXUM", "FJELL",
				"FJORD", "FLAKS", "FLAME", "FLAPS", "FLARE", "FLATS", "FLAUM", "FLAXE", "FLETT", "FLICK", "FLIRT", "FLOHE", "FLOHS", "FLOPS", "FLORA", "FLORE", "FLORI", "FLORS", "FLOWS", "FLUGE",
				"FLUID", "FLUKE", "FLURE", "FLURS", "FLUSE", "FLYER", "FLAEZ", "FLOEZ", "FOLIA", "FOLIO", "FONDS", "FOODS", "FORKE", "FORKS", "FORTS", "FOSSA", "FOTEN", "FOTON", "FOTZE", "FOULS",
				"FOSSE", "FRAME", "FRANC", "FRANK", "FRANZ", "FRATZ", "FRASS", "FREAK", "FREIA", "FREIE", "FREIS", "FRERK", "FREYA", "FRIES", "FRISE", "FRITZ", "FRONE", "FRONS", "FRUED", "FUDER",
				"FUFFI", "FUFUS", "FUGES", "FUGUS", "FUHRE", "FUHSE", "FULDA", "FUNDI", "FUNDS", "FUNKS", "FURAN", "FURIE", "FUROR", "FURRY", "FURTH", "FURZE", "FUSEL", "FUTEN", "FUTON", "FUXEN",
				"FUXES", "FUZEL", "FUZZI", "FUSSE", "FYLKE", "FAEHE", "FOEHR", "FUERS", "FUEXE", "GABUN", "GADEN", "GAGEN", "GALAN", "GALAS", "GAMAY", "GAMET", "GAMMA", "GANDS", "GANGE", "GANGS",
				"GANJA", "GANSU", "GARBE", "GARNE", "GARNS", "GARTZ", "GASAS", "GASEL", "GASEN", "GASES", "GASSI", "GASTE", "GASTS", "GATEN", "GATES", "GAUBE", "GAUCH", "GAUCK", "GAUDI", "GAUEN",
				"GAUES", "GAULE", "GAULS", "GAURS", "GAYAL", "GAYOS", "GAZAS", "GAZEN", "GECKO", "GEEKS", "GEERT", "GEEST", "GEIEN", "GEISA", "GEIZE", "GEISS", "GELBE", "GELBS", "GELDE", "GELDS",
				"GELEE", "GELEN", "GELES", "GEMME", "GENEN", "GENFS", "GENRO", "GENTS", "GENUA", "GENUS", "GEODE", "GEORG", "GERAS", "GERDA", "GERDE", "GERDS", "GEREN", "GERES", "GERMS", "GESTS",
				"GETAS", "GETTI", "GETTO", "GETUE", "GEYER", "GHANA", "GHULE", "GHULS", "GIAUR", "GIERS", "GIFTE", "GIFTS", "GIGUE", "GILDE", "GILET", "GINST", "GIPSE", "GIRRE", "GLACE", "GLANS",
				"GLASE", "GLASS", "GLAST", "GLATZ", "GLEYE", "GLEYS", "GLIMA", "GLIOM", "GLOSA", "GLUFE", "GLUON", "GNEIS", "GNOME", "GNOMS", "GOALS", "GOCHS", "GODEN", "GODLN", "GOFEN", "GOGEL",
				"GOGEN", "GOJES", "GOJIM", "GOJTE", "GOLAN", "GOLDE", "GOLDS", "GOLEM", "GOLFE", "GOLFS", "GOLLE", "GOMEL", "GONEN", "GONES", "GONGS", "GOOFY", "GORES", "GOSEN", "GOSSE", "GOSUS",
				"GOTEN", "GOTHA", "GOTTE", "GOTTS", "GOUTS", "GOZOS", "GRABE", "GRABS", "GRADE", "GRADS", "GRAFS", "GRALS", "GRAME", "GRAMS", "GRANE", "GRANS", "GRANT", "GRAPH", "GRASE", "GRATE",
				"GRATS", "GRAUS", "GRAVE", "GRAYS", "GREDE", "GREEN", "GREIF", "GREIZ", "GREMM", "GRETE", "GRIEL", "GRIES", "GRIKO", "GRIND", "GRIOT", "GRIPS", "GRISS", "GRITE", "GRITS", "GROAT",
				"GROGS", "GROOM", "GROSZ", "GROWL", "GROSS", "GRUPP", "GRUSE", "GRUSS", "GUAMS", "GUANO", "GUAVE", "GUBEN", "GUCKE", "GUDOK", "GUFEL", "GUIDE", "GUIDO", "GULAG", "GULLY", "GUMMA",
				"GUMPE", "GUNNE", "GUPFE", "GUPFS", "GUPPY", "GURDE", "GURTS", "GURUS", "GUSLA", "GUSLE", "GUSLI", "GUSSE", "GUSTL", "GUTEM", "GUTEN", "GUTER", "GUTES", "GUYOT", "GYROS", "GYRUS",
				"GAEAS", "GOERE", "GOERS", "GOERZ", "GOETZ", "GUENS", "HAABS", "HAACK", "HAANS", "HAARS", "HABEL", "HABER", "HABIT", "HABUB", "HACKS", "HADAL", "HADER", "HADES", "HAFFE", "HAFFS",
				"HAFTE", "HAFTS", "HAGEN", "HAHNE", "HAHNS", "HAIEN", "HAIES", "HAIKU", "HAINE", "HAINS", "HAITI", "HAJEK", "HAKKA", "HALBE", "HALFA", "HALLS", "HALMA", "HALMS", "HALOS", "HALSE",
				"HALTE", "HALTS", "HAMAC", "HAMAM", "HAMAS", "HAMEN", "HAMMS", "HANAU", "HANDS", "HANEN", "HANFS", "HANGE", "HANGS", "HANJA", "HANNS", "HANOI", "HANSE", "HANSI", "HANZI", "HANZL",
				"HAORI", "HARAM", "HARDE", "HAREM", "HAREN", "HARKE", "HARME", "HARMS", "HARNE", "HARNS", "HARRE", "HARRY", "HARST", "HARTE", "HARTS", "HARTZ", "HARZE", "HASCH", "HASEL", "HASES",
				"HASPE", "HASSE", "HATER", "HAUER", "HAUES", "HAUFE", "HAUKE", "HAULK", "HAUNE", "HAUSA", "HAUSE", "HAVEL", "HAXEN", "HAXLN", "HAXLS", "HEBEI", "HEBER", "HEBES", "HECKS", "HEERE",
				"HEERS", "HEFEN", "HEFES", "HEFTS", "HEGEL", "HEGER", "HEHLS", "HEIAS", "HEIKE", "HEIKO", "HEILS", "HEIME", "HEIMS", "HEINE", "HEINI", "HEINZ", "HELBE", "HELDS", "HELDT", "HELGA",
				"HELGE", "HELIS", "HELIX", "HELLE", "HELME", "HELMS", "HELOT", "HEMAN", "HEMAU", "HEMDE", "HEMDS", "HEMEN", "HEMER", "HEMIS", "HENAN", "HENDL", "HENNA", "HENRY", "HERAS", "HERDS",
				"HERNE", "HEROA", "HEROE", "HERON", "HEROS", "HERRL", "HERRN", "HERRS", "HERSE", "HERTZ", "HESSE", "HETEN", "HEUES", "HEUKE", "HEUSS", "HEXAN", "HEXER", "HEXIN", "HIATE", "HIATS",
				"HIEBE", "HIEBS", "HIEFE", "HIFEL", "HINDE", "HINDI", "HINDU", "HIPPE", "HIPPO", "HIRMS", "HIRNE", "HIRNS", "HIWIS", "HLEZA", "HMONG", "HOBOS", "HOCHS", "HOCKS", "HODEN", "HOFER",
				"HOFES", "HOHNE", "HOHNS", "HOLES", "HOLIS", "HOLKE", "HOLKS", "HOLLY", "HOLME", "HOLMS", "HOLZE", "HOMAN", "HOMER", "HOMIE", "HOMOS", "HONKS", "HONZA", "HOODY", "HOOGE", "HOORN",
				"HOPPE", "HOPSE", "HORBS", "HOREN", "HORNE", "HORNS", "HORTE", "HORTS", "HOSEA", "HOSTS", "HOUSE", "HOYAS", "HOYMS", "HUBEI", "HUBEN", "HUBER", "HUBES", "HUCKE", "HUDEL", "HUFEN",
				"HUFES", "HUGOS", "HUHNE", "HUHNS", "HUKAS", "HUKEN", "HUKES", "HULDA", "HULKE", "HULKS", "HULLS", "HUMUS", "HUNAN", "HUNDS", "HUNNE", "HUNNI", "HUNTE", "HUNTS", "HUREN", "HURIS",
				"HUSAR", "HUSSE", "HUSUM", "HUTEN", "HUTES", "HUTZE", "HYADE", "HYDRA", "HYMEN", "HYPES", "HAEPE", "HAESS", "HOEFE", "HOERI", "HUEBE", "HUETE", "IBANS", "IBIZA", "ICONS", "IDAHO",
				"IDIOM", "IDIST", "IDOLS", "IDUNA", "IDYLL", "IFTAR", "IGBOS", "IGELN", "IGELS", "IGLAU", "IGLUS", "IHDES", "IHNEN", "IHNES", "IHREM", "IHREN", "IHRER", "IHRES", "IJJAR", "IKONS",
				"IKTEN", "IKTUS", "ILEEN", "ILEUS", "ILLER", "ILLIG", "ILSES", "IMAGE", "IMAGO", "IMAME", "IMAMS", "IMMEN", "IMMOS", "IMSTS", "INCHS", "INDER", "INDIK", "INDIO", "INDRA", "INDRI",
				"INDUS", "INFIX", "INFOS", "INGAS", "INGEN", "INGKE", "INGOS", "INKAS", "INROS", "INSTE", "INTIS", "INTRO", "INUIT", "INUKS", "IODES", "IONEN", "IOTAS", "IOWAS", "IPPEN", "IPPON",
				"IRADE", "IRAKI", "IRAKS", "IRANS", "IRENE", "IRENS", "IRMAS", "IRREM", "IRRER", "ISCHE", "ISLAM", "ISMEN", "ISMUS", "ISNYS", "ISSEL", "ITAKA", "ITEMS", "IVRIT", "IWANS", "IWRIT",
				"JABOS", "JABOT", "JACHT", "JACKY", "JADEN", "JADES", "JAFFA", "JAGST", "JAHNS", "JAHRS", "JAHVE", "JAHWE", "JAINA", "JAKOB", "JALTA", "JAMBE", "JANNA", "JANUS", "JAPAN", "JAPSE",
				"JARLE", "JARLS", "JASON", "JASSY", "JAUCH", "JAUKS", "JAUST", "JAVAS", "JECKE", "JECKS", "JEEPS", "JELEN", "JEMEN", "JENAS", "JENTE", "JEREZ", "JESUM", "JESUS", "JETTS", "JEVER",
				"JIEZE", "JIHAD", "JILKE", "JINAS", "JINYU", "JIVES", "JOCHE", "JOCHS", "JODAT", "JODES", "JOGIN", "JOGIS", "JOHNS", "JOINT", "JOJOS", "JOKES", "JOKUS", "JOLLE", "JONAH", "JONAS",
				"JONNY", "JOPPE", "JOSEF", "JOSUA", "JOTAS", "JOULE", "JUBAS", "JUDAS", "JUDEN", "JUDOS", "JUIST", "JULEI", "JULIA", "JULIE", "JULIS", "JUNGS", "JUNIS", "JUNOS", "JURAS", "JURIJ",
				"JURKE", "JURTE", "JURYS", "JUSTS", "JUTTA", "JUXEN", "JUXES", "JUXTA", "JUZES", "JAECK", "JAEHE", "JOERG", "JUETE", "KAABA", "KABIS", "KABOD", "KABUL", "KACKE", "KADIS", "KAFFE",
				"KAFFS", "KAFIR", "KAFKA", "KAHAL", "KAHBA", "KAHLA", "KAHME", "KAHMS", "KAHNE", "KAHNS", "KAIKS", "KAIRO", "KAJAK", "KAJAL", "KAJEN", "KAKAS", "KAKEN", "KAKES", "KAKIS", "KALBE",
				"KALBS", "KALEU", "KALIF", "KALIS", "KALKE", "KALKS", "KALLI", "KAMEE", "KAMEN", "KAMME", "KAMMS", "KAMPS", "KANGS", "KANIN", "MORBI", "MORDE", "MORDS", "MOREN", "MORPH", "MOSEL",
				"MOSER", "MOSTE", "MOSTS", "MOSUL", "MOTEL", "MOTTI", "MOUSE", "MOVIE", "MOXEN", "MUCKS", "MUFFE", "MUFFS", "MUFTI", "MUGEL", "MUGGE", "MUHME", "MULCH", "MULIS", "MULLA", "MULLE",
				"MULLS", "MULME", "MULMS", "MUMME", "MUMMS", "MUMPS", "MUMUS", "MUNDE", "MUNDS", "MUNGO", "MUREN", "MURGS", "MURKS", "MUSEN", "MUSES", "MUTES", "MUTEX", "MUTON", "MUTTI", "MUTZE",
				"MUTAE", "MUZAK", "MUZIN", "MUSSE", "MYLAU", "MYOME", "MYOMS", "MYONS", "MYOPE", "MYRTE", "MYSTE", "MYTHE", "MYXOM", "MYZEL", "MYZET", "MAERE", "MOEHN", "MOELL", "MOESE", "MUENZ",
				"MUERZ", "NABEN", "NABOB", "NACHO", "NADIR", "NADJA", "NAFRI", "NAGER", "NAHIE", "NAHLE", "NAHUM", "NAHUR", "NAILA", "NAIVE", "NAMAS", "NAMAZ", "NAMEN", "NAMIB", "NAMUR", "NANAS",
				"NANCY", "NANDU", "NANNY", "NAOMI", "NAPFE", "NAPFS", "NAPPA", "NAPPO", "NARCO", "NARDE", "NARES", "NAREW", "NARIS", "NARWA", "NASEN", "NASHI", "NASSE", "NATEL", "NAUEN", "NAUES",
				"NAUGH", "NAURU", "NAVIS", "NAZIS", "NEBRA", "NEGER", "NEGUS", "NEIDE", "NEIDS", "NEIGE", "NEINS", "NENZE", "NEONS", "NEPAL", "NEPPS", "NERDS", "NERIS", "NERVE", "NERVI", "NERVS",
				"NERZE", "NESTE", "NESTS", "NETTE", "NETZE", "NEUEM", "NEUEN", "NEUER", "NEUES", "NEUME", "NEUSS", "NEXUS", "NGONI", "NGUNI", "NIDDA", "NIDEL", "NIDLE", "NIEBY", "NIETE", "NIETS",
				"NIGER", "NIGLS", "NIKES", "NILLE", "NILLS", "NINJA", "NIOBE", "NIOBS", "NIQAB", "NISAN", "NIUES", "NIXEN", "NIXES", "NIZZA", "NOAHS", "NOCKE", "NOCKS", "NODUS", "NOEMI", "NOIRS",
				"NOMAE", "NOMAS", "NOMEN", "NONAN", "NONEN", "NONNS", "NOOBS", "NORAS", "NORDS", "NORNE", "NORNS", "NOVAE", "NOWAK", "NOXEN", "NUGAT", "NULLS", "NULPE", "NUMEN", "NURSE", "NUTEN",
				"NUTTE", "NUTZE", "NUUKS", "NYALA", "NAETA", "NAETO", "NAEVI", "NOETE", "OAHUS", "OASEN", "OBERN", "OBERS", "OBOEN", "OBOER", "OBSTE", "OBSTS", "OCKER", "OCTAN", "ODELS", "ODEMS",
				"ODERS", "ODILO", "ODINS", "ODIUM", "ODORS", "OEHME", "OELDE", "OESUM", "OFENS", "OGERN", "OGERS", "OGLIO", "OHEIM", "OHIOS", "OHMEN", "OHMES", "OHRES", "OIKOI", "OIKOS", "OKAPI",
				"OKTAV", "OKULI", "OLAND", "OLDIE", "OLEUM", "OLFEN", "OLMEN", "OLMES", "OLPES", "OLYMP", "OMAMA", "OMANI", "OMANS", "OMEGA", "OMENS", "OMINA", "OMMEN", "OMSKS", "OMULE", "OMULS",
				"ONERA", "ONYXE", "OOZYT", "OPALE", "OPALS", "OPAPA", "OPERA", "OPERN", "OPIAT", "OPSIN", "OPUWO", "ORCAS", "ORDAL", "ORDER", "ORELS", "ORIJA", "ORION", "ORIYA", "ORJOL", "ORLOG",
				"ORNAT", "ORNES", "OROMO", "ORTES", "OSCAR", "OSCHI", "OSERN", "OSKAR", "OSKEN", "OSKES", "OSLIP", "OSLOS", "OSSIS", "OTAKU", "OTTOS", "OUKIE", "OUTEN", "OUZOS", "OVALE", "OVALS",
				"OVARE", "OVARS", "OVULA", "OVUMS", "OWENS", "OWRAG", "OXERN", "OXERS", "OXIDE", "OXIDS", "OXYDE", "OXYDS", "OZONS", "PAARS", "PABLO", "PACKE", "PACKS", "PADUA", "PAGAT", "PAGEN",
				"PAIRS", "PAKTE", "PAKTS", "PALAS", "PALAU", "PALMA", "PALSE", "PAMAS", "PAMIR", "PAMPA", "PAMPE", "PANDA", "PAPAS", "PAPER", "PAPIS", "PAPSE", "PAPUA", "PARAS", "PAREO", "PARIA",
				"PARIS", "PARKA", "PARKE", "PARKS", "PARMA", "PARSE", "PARZE", "PASCH", "PASSA", "PASSE", "PASTE", "PATCH", "PATIN", "PATIO", "PATNA", "PATTE", "PATTS", "PAULA", "PAULS", "PAUSA",
				"PAVIA", "PAXEN", "PAXES", "PEAKS", "PECHE", "PECHS", "PEENE", "PEERS", "PEGAU", "PEINE", "PEITZ", "PELLE", "PELTE", "PELZE", "PENCE", "PENES", "PENIG", "PENIS", "PENNE", "PENNY",
				"PENSA", "PERLS", "PERMS", "PERSO", "PERTH", "PERUS", "PESEL", "PESEN", "PESOS", "PESTO", "PESTS", "PETER", "PETRA", "PETZE", "PFAAR", "PFADE", "PFADS", "PFAFF", "PFALZ", "PFAUE",
				"PFAUS", "PFIFF", "PFUHL", "PHAGE", "PHONE", "PHONS", "PHYLA", "PIANI", "PIANO", "PIANS", "PICKE", "PICKS", "PIEKS", "PIEPE", "PIEPS", "PIERE", "PIERS", "PIETA", "PIJIN", "PIKEN",
				"PIKES", "PIKTE", "PILAW", "PILES", "PIMPF", "PINAX", "PINGE", "PINGS", "PINKA", "PINKE", "PINNE", "PINTE", "PINTS", "PINZE", "PIPEN", "PIPIS", "PIPOX", "PIRNA", "PIROL", "PISAS",
				"PISSE", "PIVOT", "PLAID", "PLANS", "PLAST", "PLAUE", "PLAUS", "PLAYA", "PLAYE", "PLEBS", "PLENA", "PLENK", "PLOCK", "PLOTS", "PLUTO", "PLOEN", "PNEUS", "POCKE", "PODEX", "POELS",
				"POEME", "POEMS", "POGGE", "POHLS", "POLCH", "POLEN", "POLIN", "POLIS", "POLLE", "POLOS", "POLYP", "POMPS", "PONTE", "POOLS", "POOPS", "POPEL", "POPEN", "POPOS", "POPPE", "POREN",
				"PORNO", "PORST", "PORTE", "PORTI", "PORTS", "POSCH", "POSEN", "POSTS", "POTTE", "POTTS", "POWER", "PRAGS", "PRAHM", "PRAIA", "PRAUE", "PRIEL", "PRIEM", "PRIMI", "PRINT", "PRION",
				"PRIOR", "PROFS", "PROLL", "PROLO", "PROMI", "PROST", "PROTZ", "PROXY", "PRUTH", "PRUEM", "PSKOW", "PUCKS", "PUFFE", "PUFFS", "PUGIO", "PULKE", "PULKS", "PULLE", "PULLI", "PULPA",
				"PULSE", "PULTE", "PULTS", "PUMAS", "PUMPF", "PUMPS", "PUNAS", "PUNKS", "PUNZE", "PUPEN", "PUPES", "PUPSE", "PURIM", "PUSSY", "PUTEN", "PUTTE", "PUTTI", "PUTTO", "PUTTS", "PUTZE",
				"PYHRA", "PYHRN", "PYLON", "PYXIS", "PAENZ", "PUETT", "PUETZ", "QANAT", "QOPHS", "QOPPA", "QUAAS", "QUADE", "QUADS", "QUADT", "QUAIS", "QUALE", "QUANT", "QUARZ", "QUAST", "QUBIT",
				"QUECK", "QUEEN", "QUELL", "QUENT", "QUESE", "QUEST", "QUEUE", "QUINT", "QUITO", "RAABE", "RAABS", "RAAEN", "RABAT", "RABBI", "RABEA", "RABES", "RACKE", "RACKS", "RADEN", "RADES",
				"RADIS", "RADJA", "RADLN", "RADLS", "RADOM", "RADON", "RAHEN", "RAHMS", "RAIDS", "RAINE", "RAINS", "RAISE", "RAJAH", "RAJAS", "RAKEL", "RAKIS", "RALLE", "RAMEN", "RAMIN", "RAMME",
				"RANCH", "RANDE", "RANDS", "RANGE", "RANGS", "RANIS", "RANKE", "RANKS", "RAPHE", "RAPSE", "RASTE", "RATES", "RATIO", "RATZE", "RAUBE", "RAUBS", "RAUFE", "RAUKE", "RAUME", "RAUMS",
				"RAUPS", "RAVES", "RAYON", "REALO", "REALS", "REBBE", "REBEN", "REBUS", "RECKE", "RECKS", "REEDE", "REEMT", "REEPE", "REEPS", "REETS", "REFFE", "REFFS", "REGNA", "REGUR", "REHAS",
				"REHAU", "REHEN", "REHES", "REHNA", "REIBE", "REIFS", "REIHN", "REIKI", "REIMS", "REINE", "REIWA", "REIZE", "REKTA", "REMIS", "REMUS", "RENEN", "RENES", "RENKE", "RENOS", "RERIK",
				"RESCH", "RESTS", "REUSE", "REUSS", "REVAL", "REVUE", "REXEN", "RHEDE", "RHEIN", "RHEMA", "RHENS", "RHINS", "RHONE", "RIADE", "RIADS", "RIALS", "RICIN", "RICKE", "RIEDE", "RIEDS",
				"RIEFE", "RIENZ", "RIESA", "RIETZ", "RIFFE", "RIFFS", "RIGAS", "RIGEL", "RIGOR", "RINDS", "RINGO", "RINGS", "RIOJA", "RISTE", "RITEN", "RITTE", "RITTS", "RIYAL", "RIZIN", "ROBEN",
				"ROBOT", "ROCKE", "ROCKS", "RODEO", "RODER", "ROGEN", "ROHRS", "ROJER", "ROLLI", "ROLLO", "ROLOF", "ROLUF", "ROMNI", "RONDE", "RONDO", "RONIN", "RONNY", "ROSAS", "ROSEL", "ROSSE",
				"ROSTE", "ROTEN", "ROTHE", "ROTHS", "ROTTE", "ROUEN", "ROUGE", "ROUTS", "ROVER", "ROWDY", "ROYAL", "RUASA", "RUBEL", "RUBRA", "RUCHS", "RUCKE", "RUCKS", "RUFES", "RUGBY", "RUHLA",
				"RUHME", "RUHMS", "RUINS", "RUMBA", "RUMEN", "RUMOR", "RUNDS", "RUNEN", "RUNGE", "RUPIA", "RUPIE", "RUSCH", "RUSSE", "RUSTS", "RUTEN", "RAEFE", "RAEFS", "RAETE", "RAETS", "ROERD",
				"ROETE", "ROETZ", "SAALE", "SAALS", "SAAMI", "SABOR", "SACHA", "SACHS", "SACKE", "SACKS", "SAFED", "SAFES", "SAFTE", "SAFTS", "SAGAS", "SAGER", "SAGOS", "SAHEL", "SAHIB", "SAHRA",
				"SAHTI", "SALAM", "SALDI", "SALME", "SALMI", "SALMS", "SALPE", "SALTI", "SALUT", "SALZE", "SAMIN", "SAMOA", "SAMTE", "SAMTS", "SANAA", "SANDE", "SANDS", "SANGE", "SANGO", "SANGS",
				"SANKA", "SANKT", "SANNE", "SANYA", "SAPPE", "SARAH", "SARDE", "SARGE", "SARGS", "SARIN", "SARIS", "SASAK", "SASSE", "SATIN", "SATTE", "SATYR", "SATZE", "SAUCE", "SAUDI", "SAUEN",
				"SAUME", "SAUMS", "SAUSE", "SAXEN", "SAYDA", "SAZEN", "SCANS", "SCATS", "SCHAH", "SCHAN", "SCHEM", "SCHIA", "SCHIS", "SCHMA", "SCHMU", "SCHOT", "SCHUR", "SCHWA", "SCIFI", "SCOOP",
				"SCOTS", "SCOUT", "SCRIP", "SCUDI", "SCUDO", "SECHE", "SEDUM", "SEGGE", "SEIDL", "SEIGE", "SEIKE", "SEILS", "SEIME", "SEIMS", "SEINE", "SEINS", "SEKEL", "SEKTS", "SELBS", "SELEN",
				"SELMS", "SEMEM", "SEMEN", "SEMIT", "SENFE", "SENFS", "SENGE", "SENJS", "SENNE", "SENNS", "SEOUL", "SEPIA", "SEPOY", "SEPPS", "SEPTA", "SERBE", "SEREN", "SERER", "SERGE", "SERIN",
				"SERNF", "SESLE", "SEXES", "SEXTA", "SEXTE", "SEXUS", "SHAWL", "SHAWS", "SHIRE", "SHOAH", "SHONA", "SHOPS", "SHOWS", "SHUNT", "SIALS", "SIAMS", "SICKE", "SICKS", "SIEBE", "SIEBS",
				"SIEGE", "SIEGS", "SIEKE", "SIELE", "SIFFS", "SIGEL", "SIGLE", "SIGMA", "SIKHS", "SILAS", "SILAU", "SILEN", "SILGE", "SILKS", "SILOS", "SILTE", "SILTS", "SILUR", "SIMON", "SIMRI",
				"SIMSE", "SINAH", "SINJE", "SINNS", "SINTI", "SINTO", "SINUS", "SIOUX", "SIRES", "SISAL", "SITAR", "SITAS", "SITUS", "SITZE", "SIVAN", "SIWAN", "SIXTA", "SKAIS", "SKALE", "SKATE",
				"SKATS", "SKENE", "SKIER", "SKIFF", "SKINS", "SKUAS", "SKUFF", "SKULD", "SKULL", "SKUNK", "SKYES", "SKYRS", "SKUES", "SLANG", "SLASH", "SLAWE", "SLICE", "SLICK", "SLIPS", "SLOOP",
				"SLUMS", "SLUPS", "SMASH", "SMOGS", "SMOKS", "SNACK", "SNAPS", "SNIFF", "SNOBS", "SNUFF", "SOAPS", "SOAVE", "SODAS", "SODEN", "SODES", "SODOM", "SOEST", "SOFAS", "SOFER", "SOFIA",
				"SOGEN", "SOGES", "SOHNE", "SOHNS", "SOHOS", "SOJAS", "SOJEN", "SOKOS", "SOLDE", "SOLDI", "SOLDO", "SOLDS", "SOLEN", "SOLIS", "SOLLS", "SOLMS", "SOLOS", "SOMAL", "SOMAS", "SONEN",
				"SONES", "SONGS", "SONJA", "SOPOR", "SORAU", "SORBE", "SOREN", "SOTER", "SOTTS", "SOUKS", "SOULS", "SOUND", "SOYKA", "SOZEN", "SOZIA", "SOZII", "SOZIO", "SOZIS", "SOSSE", "SPACE",
				"SPAMS", "SPANE", "SPANN", "SPANS", "SPANT", "SPARE", "SPATE", "SPATS", "SPAYS", "SPEED", "SPEIL", "SPEIS", "SPELT", "SPELZ", "SPEZI", "SPEZL", "SPIND", "SPINS", "SPINT", "SPLIT",
				"SPORE", "SPOTS", "SPRAY", "SPREE", "SPRIT", "SPUKE", "SPUKS", "SPUND", "SPURT", "SPUTA", "SQUAT", "SQUAW", "STABE", "STABS", "STACK", "STADE", "STAGE", "STAGS", "STAKE", "STANZ",
				"STARE", "STARS", "STASI", "STAUE", "STAUF", "STAUS", "STEAK", "STEGE", "STEGS", "STELE", "STENO", "STENT", "STENZ", "STERS", "STERT", "STERZ", "STETL", "STEYR", "STICK", "STILE",
				"STILS", "STINO", "STOEN", "STOMA", "STOOB", "STORE", "STORY", "STOSS", "STREM", "STREU", "STRIA", "STRIP", "STUDI", "STUHR", "STUKA", "STUPA", "STUPS", "STURA", "STUSS", "STUTZ",
				"STYLI", "SUADA", "SUCRE", "SUDAN", "SUDEN", "SUDES", "SUDOR", "SUFET", "SUFFS", "SUFIS", "SUGOS", "SUHLE", "SUHLS", "SUJET", "SUKKA", "SULCI", "SULKY", "SUMER", "SUNDE", "SUNDS",
				"SUNKS", "SUREN", "SURIS", "SUSAS", "SUSEN", "SUSHI", "SUVAS", "SVAMI", "SVANA", "SVANE", "SVEAR", "SWAGS", "SWAMI", "SWANA", "SWANE", "SWASI", "SWATI", "SWING", "SWIST", "SYKES",
				"SYLTS", "SYPHS", "SYRAH", "SYRER", "SYRTE", "SYSOP", "SZECH", "SAELE", "SAEUE", "SUESS", "TABEN", "TABES", "TABOR", "TABUS", "TACHO", "TACKE", "TACKS", "TACOS", "TADES", "TADIG",
				"TAELS", "TAFTE", "TAFTS", "TAGES", "TAHRS", "TAIGA", "TAJOS", "TAKTE", "TAKTS", "TALAR", "TALES", "TALGE", "TALGS", "TALIB", "TALJE", "TALKE", "TALKS", "TALON", "TAMEN", "TAMIL",
				"TAMPA", "TANDS", "TANGA", "TANGE", "TANGS", "TANJA", "TANKA", "TANKE", "TANKS", "TANNA", "TANNS", "TANSE", "TANZE", "TAPAS", "TAPES", "TAPET", "TAPIR", "TARAN", "TAREN", "TARGI",
				"TAROS", "TARTE", "TATAR", "TATES", "TATIN", "TATRA", "TATTS", "TAUEN", "TAUES", "TAUON", "TAXEN", "TAXIE", "TAXIS", "TAXON", "TAYEN", "TEAMS", "TEBET", "TEDDY", "TEEEI", "TEENS",
				"TEERE", "TEERS", "TEIFI", "TEIGE", "TEIGS", "TEINS", "TEINT", "TELEN", "TELES", "TELEX", "TEMPI", "TENDO", "TENNE", "TENNO", "TERME", "TERMS", "TERNE", "TERNO", "TESJE", "TESLA",
				"TESTE", "TESTS", "TETUM", "TEUFE", "TEURO", "TEWET", "TEXAS", "TEXEL", "TEXTS", "THAIS", "THALE", "THANE", "THANS", "THARR", "THARS", "THAYA", "THEIA", "THEIN", "THEOS", "THERR",
				"THETA", "THIEL", "THILO", "THING", "THIOL", "THONS", "THORA", "THORN", "THORS", "THUJA", "THUMS", "THUNS", "THURE", "TIARA", "TIBER", "TIBET", "TICKS", "TIDEN", "TIEFS", "TIERS",
				"TIFFY", "TIGON", "TILDE", "TILLE", "TILLI", "TILLS", "TILLY", "TIMMS", "TIMOR", "TIMOS", "TINAS", "TINEN", "TINGE", "TINGS", "TIPIS", "TIPPS", "TIRET", "TIROL", "TISAS", "TITER",
				"TITTE", "TITUS", "TIXOS", "TJARK", "TOBAJ", "TOBAK", "TOBEL", "TOBOL", "TODDY", "TODEN", "TODES", "TOFUS", "TOGEN", "TOGOS", "TOKEN", "TOKIO", "TOKUS", "TOKYO", "TOLAS", "TOLLE",
				"TOMMY", "TOMSK", "TONDI", "TONDO", "TONEN", "TONER", "TONES", "TONGA", "TONIS", "TONUS", "TOOLS", "TOPAS", "TOPFE", "TOPFS", "TOPOI", "TOPOS", "TOPPE", "TOPPS", "TOQUE", "TORAH",
				"TOREN", "TORES", "TORFE", "TORFS", "TORII", "TORIS", "TORSI", "TORSO", "TORTS", "TORYS", "TOSKE", "TOTEN", "TOTER", "TOTUM", "TOUCH", "TOURI", "TOWER", "TRABI", "TRABS", "TRAIN",
				"TRAME", "TRAMP", "TRAMS", "TRANE", "TRANS", "TRAPO", "TRASH", "TRASS", "TRAUF", "TRAUN", "TRAVE", "TRECK", "TREFF", "TREMA", "TRIAL", "TRIAS", "TRIEL", "TRIER", "TRIFT", "TRIOL",
				"TRIOS", "TRIPS", "TROAS", "TROGE", "TROGS", "TROIA", "TROJA", "TROPE", "TROPF", "TRUCK", "TRUDE", "TRUGS", "TRUHE", "TRUMM", "TRUNK", "TRUTE", "TRUTZ", "TSUBA", "TUBEL", "TUBEN",
				"TUBER", "TUBUS", "TUCHE", "TUCHS", "TUCKE", "TUCKS", "TUDOR", "TUFFE", "TUFFS", "TUKAN", "TULAS", "TULLN", "TUNEN", "TUNER", "TUNES", "TUNIS", "TUNKE", "TUNTE", "TUPEL", "TUPFE",
				"TUPFS", "TURAN", "TURAS", "TURKO", "TURME", "TURMS", "TUSCH", "TUSSE", "TUSSI", "TUTEL", "TUTOR", "TUTTE", "TUTUS", "TUWAS", "TWEDT", "TWEED", "TWEEF", "TWEET", "TWENS", "TWERS",
				"TWETE", "TYPES", "TYPUS", "TOEFF", "TOELE", "TOENE", "TOERN", "TUELL", "TUERE", "UBIER", "UBOOT", "UETZE", "UFERN", "UFERS", "UKASE", "ULEMA", "ULKEN", "ULKES", "ULMEN", "ULMER",
				"ULTRA", "UMBRA", "UMLAD", "UNGAR", "UNIKA", "UNIXE", "UNKAS", "UNKEL", "UNKEN", "UNNAS", "UNRUH", "UNTAT", "UNZEN", "UPPER", "URAHN", "URALS", "URANS", "URINE", "URINS", "URNEN",
				"UROMA", "UROPA", "URSON", "URUKS", "USANZ", "USERN", "USERS", "USLAR", "UTAHS", "UTERI", "UTRUM", "UVALA", "UVITE", "UVITS", "UVULA", "VACHA", "VACKE", "VADUZ", "VAKAT", "VAKUA",
				"VALET", "VALIN", "VAMPS", "VAREL", "VARIA", "VARIX", "VASEN", "VATIS", "VEDAS", "VEDEN", "VELAR", "VELDE", "VELDS", "VELMS", "VELOS", "VELUM", "VENAE", "VENDA", "VENEN", "VERBA",
				"VERBE", "VERBS", "VERLS", "VERSE", "VERVE", "VESPA", "VESTA", "VESTE", "VESUV", "VETOS", "VIALS", "VIECH", "VIEHE", "VIEHS", "VIELS", "VIGIL", "VIKES", "VINAS", "VINKO", "VIPER",
				"VIREN", "VIRIA", "VIROM", "VISEN", "VITAE", "VITEN", "VITIS", "VITUS", "VIURA", "VIZES", "VIZIN", "VIOEL", "VLOGS", "VOCES", "VOGTE", "VOGTS", "VOGUE", "VOIGT", "VOLKE", "VOLKS",
				"VOLTE", "VOLTS", "VOLUM", "VOPOS", "VOTEN", "VOTIV", "VOTZE", "VOUTE", "VOXEL", "VRBAS", "VULVA", "VUOTO", "WAADT", "WAALE", "WAALS", "WABEN", "WACKE", "WADES", "WADIS", "WADLN",
				"WADLS", "WAFER", "WAHNE", "WAHNS", "WAIDE", "WAIDS", "WAKEN", "WALDE", "WALDS", "WALEN", "WALES", "WALKE", "WALLE", "WALLS", "WALME", "WALMS", "WAMME", "WAMPE", "WAMSE", "WANDA",
				"WANEN", "WANJA", "WANST", "WANTS", "WARAY", "WAREN", "WARFT", "WARIN", "WARPE", "WARPS", "WARTS", "WARVE", "WARWE", "WASEN", "WATTS", "WAUEN", "WECKE", "WECKS", "WEDAS", "WEDEL",
				"WEDEN", "WEDGE", "WEDRO", "WEEDS", "WEFTE", "WEFTS", "WEGES", "WEHES", "WEHLE", "WEHRE", "WEHRS", "WEIBE", "WEIBS", "WEIDA", "WEILS", "WEINE", "WEINS", "WEISS", "WELFE", "WELIS",
				"WELLI", "WELSE", "WELTS", "WENNS", "WEPSE", "WERDE", "WERDS", "WERGS", "WERKS", "WERLS", "WERNE", "WERRA", "WERST", "WERTH", "WERTS", "WESEL", "WESER", "WESIR", "WESSI", "WHIGS",
				"WHIPS", "WHIST", "WICHS", "WICKE", "WIEDE", "WIEHE", "WIEHL", "WIEKE", "WIEKS", "WIENS", "WIESN", "WIFIS", "WIFOS", "WIKEN", "WIKES", "WIKIS", "WILDS", "WILLI", "WILMS", "WILNA",
				"WINAS", "WINDS", "WINKE", "WINKS", "WIRTS", "WISCH", "WITIB", "WIYOT", "WOBER", "WODAN", "WODKA", "WOHLE", "WOLFE", "WOLFF", "WOLFS", "WOLGA", "WOLOF", "WOODS", "WORMS", "WORTS",
				"WOSSI", "WOTAN", "WOTEN", "WOTIN", "WRAPS", "WRUKE", "WUHAN", "WUHNE", "WULFF", "WULKA", "WUMME", "WUMMS", "WUNEN", "WURFE", "WURFS", "WURME", "WURMS", "WUSTS", "WUZEL", "WAEHE",
				"XAVER", "XENIA", "XENIE", "XENON", "XENOS", "XERES", "XETRA", "XHOSA", "XIANG", "XOANA", "XYLAN", "XYLEM", "XYLIT", "XYLOL", "XYLON", "XYSTI", "YAKIS", "YAMEN", "YANGS", "YAPOK",
				"YARDS", "YAREN", "YAWLE", "YAWLS", "YEAST", "YENTL", "YERBA", "YETIS", "YLIDE", "YLIDS", "YOGAS", "YOGIN", "YOGIS", "YOMUD", "YORCK", "YPERN", "YSOPE", "YSOPS", "YSPER", "YTONG",
				"YUANS", "YUCCA", "YUKOS", "YUROK", "YVONN", "YAERB", "ZABEL", "ZACHE", "ZACHS", "ZAFUS", "ZAGEL", "ZAGEN", "ZAHNA", "ZAHNE", "ZAHNS", "ZAINE", "ZAINS", "ZAKAT", "ZAMAK", "ZAMBA",
				"ZAMBO", "ZAMIA", "ZAMIE", "ZANES", "ZANIS", "ZANKE", "ZANNE", "ZANNI", "ZARAH", "ZAREN", "ZARGE", "ZARIN", "ZARMA", "ZAUME", "ZAUMS", "ZAUNE", "ZAUNS", "ZAUPE", "ZEBUS", "ZECKS",
				"ZEDER", "ZEESE", "ZEHES", "ZEHNT", "ZEILS", "ZEITS", "ZEITZ", "ZELGE", "ZELLS", "ZELOT", "ZELTE", "ZELTS", "ZENTA", "ZENZI", "ZESEL", "ZESTE", "ZETAS", "ZETTS", "ZEUGS", "ZEVEN",
				"ZIBBE", "ZICKE", "ZIELS", "ZIEST", "ZIMTE", "ZIMTS", "ZINKS", "ZINNS", "ZIONS", "ZIPPS", "ZISKA", "ZITAS", "ZITZE", "ZIVIS", "ZLOTY", "ZNAIM", "ZOFEN", "ZOFFS", "ZOLLE", "ZOLLS",
				"ZONEN", "ZOOMS", "ZOPEN", "ZOPFE", "ZOPFS", "ZORES", "ZORNE", "ZORNS", "ZOSSE", "ZOTEN", "ZUAVE", "ZUBER", "ZUGES", "ZULPE", "ZULPS", "ZULUS", "ZUMBA", "ZUREK", "ZWANE", "ZYANE",
				"ZYANS", "ZYMNY", "ZYRUS", "ZAEKA", "ZOEKA", "ZUEGE", "AALET", "AALTE", "AASET", "AASIG", "AASTE", "ABASS", "ABBAT", "ABBOG", "ABBOT", "ABBUK", "ABERE", "ABERT", "ABGAB", "ABHER",
				"ABHIN", "ABHOB", "ABHUB", "ABKAM", "ABLAS", "ABLUD", "ABSAH", "ABSOG", "ABTAT", "ABTUE", "ABTUT", "ABWOG", "ABZOG", "ACHLE", "ACKRE", "ADDEN", "ADDET", "ADDIO", "ADELN", "ADELT",
				"ADLET", "ADLIG", "ADULT", "AEROB", "AFFIG", "AFFIN", "AGIER", "AGILE", "AHMEN", "AHNDE", "AHNET", "AHNST", "AHNTE", "AKRAL", "ALAAF", "ALBER", "ALBRE", "ALLDA", "ALLEL", "ALLEM",
				"ALLES", "ALLZU", "ALMET", "ALMST", "ALMTE", "ALTEL", "ALTLE", "ALTRE", "AMBIG", "ANALE", "ANASS", "ANBEI", "ANBUK", "ANDIN", "ANGAB", "ANGLE", "ANHAT", "ANHOB", "ANHUB", "ANKAM",
				"ANKRE", "ANLAG", "ANLAS", "ANLOG", "ANSAH", "ANSOG", "ANTAT", "ANTUE", "ANTUT", "ANZOG", "APERE", "APERN", "APERT", "APRER", "ARGEM", "ARGER", "ARGES", "ARIDE", "ARKAN", "ARTET",
				"ASCHT", "ASSAI", "ASTEN", "ASTET", "ATMET", "ATZET", "ATZTE", "AUTEL", "AUTLE", "AUWEH", "AUWEI", "AZURN", "BACKT", "BADET", "BAFFE", "BAHNT", "BALGT", "BALLT", "BALZE", "BALZT",
				"BANGT", "BANNT", "BAPPE", "BAPPT", "BARER", "BARGT", "BARME", "BARMT", "BARST", "BASAL", "BASTA", "BATEN", "BATET", "BATST", "BAUET", "BAUMT", "BAUST", "BEAME", "BEAMT", "BEBAK",
				"BEBAU", "BEBET", "BEBST", "BEBTE", "BEEHR", "BEEID", "BEEIL", "BEEND", "BEENG", "BEERB", "BEERD", "BEFUG", "BEGAB", "BEGAS", "BEGEH", "BEGIB", "BEHAG", "BEHAU", "BEHEB", "BEHEX",
				"BEHOB", "BEHOS", "BEHUB", "BEIGT", "BEIRR", "BEIZT", "BEIZU", "BEISS", "BEJAG", "BEJAH", "BEKAM", "BEKAU", "BEKOT", "BELAD", "BELEB", "BELEH", "BELLE", "BELLT", "BELOB", "BELOG",
				"BELUD", "BEMAL", "BENAG", "BENOT", "BERAT", "BERED", "BEREU", "BERGT", "BESAH", "BESAM", "BESAE", "BETAN", "BETAT", "BETET", "BETUE", "BETUN", "BETUT", "BEUGT", "BEULT", "BEWEG",
				"BEWOG", "BEZOG", "BEOEL", "BIEGE", "BIEGT", "BIENN", "BIETE", "BIKEN", "BIKET", "BIKST", "BIKTE", "BIMST", "BINGT", "BIPED", "BIRGT", "BIRST", "BISST", "BISTE", "BLACH", "BLADE",
				"BLAFF", "BLAKE", "BLAKT", "BLAND", "BLAST", "BLAUE", "BLAUT", "BLECK", "BLEIB", "BLEIT", "BLICH", "BLIEB", "BLINK", "BLOGG", "BLOSS", "BLUBB", "BLUTT", "BLAEH", "BLAEU", "BLOEK",
				"BLUEH", "BOCKT", "BODIG", "BOGIG", "BOGST", "BOHNT", "BOHRE", "BOHRT", "BOING", "BOLZE", "BOLZT", "BOMBT", "BONGE", "BONGT", "BOOME", "BOOMT", "BORGT", "BOSEL", "BOSLE", "BOTET",
				"BOTST", "BOWLT", "BOXET", "BOXTE", "BRATE", "BRAUS", "BRAVE", "BREMS", "BRENN", "BRICH", "BRIET", "BRING", "BROCK", "BROWS", "BRUNZ", "BRAEM", "BRUEH", "BRUET", "BUBET", "BUBST",
				"BUBTE", "BUDER", "BUDRE", "BUHEN", "BUHET", "BUHLT", "BUHST", "BUHTE", "BUKEN", "BUKST", "BUMSE", "BUMST", "BUNTE", "BUSIG", "BUTCH", "BAEHE", "BAEHT", "BAETE", "BAEUM", "BOEGT",
				"BOELK", "BOERN", "BUECK", "BUEKE", "BUEKT", "BUERD", "BUERG", "BUESS", "CACHT", "CAMPE", "CAMPT", "CASTE", "CHASS", "CHATT", "CHICE", "CHILL", "CHIPP", "CIRCA", "CLEAN", "CLICK",
				"CODEN", "CODET", "COOLE", "COVRE", "CRAWL", "CREMT", "CRUIS", "CUTTE", "DAHAT", "DALAG", "DALKE", "DALKT", "DALLI", "DAMMT", "DANGT", "DANKT", "DARBE", "DARBT", "DAREM", "DAROB",
				"DARRT", "DASIG", "DATET", "DAURE", "DAUSS", "DAWAI", "DEALE", "DEALT", "DEBIL", "DECKT", "DEHNE", "DEHNT", "DEINE", "DEINS", "DELLT", "DENEN", "DENKT", "DERBE", "DEREN", "DERER",
				"DESTO", "DETTO", "DEUTE", "DEVOT", "DICKT", "DIENE", "DIENT", "DIESE", "DIMME", "DIMMT", "DINGT", "DIPPE", "DIPPT", "DISSE", "DISST", "DOCKT", "DOLOS", "DOOFE", "DOPEN", "DOPET",
				"DOPST", "DOPTE", "DORRE", "DORRT", "DOXEN", "DOXET", "DOXTE", "DRAUS", "DREHT", "DREIE", "DREIN", "DRING", "DRITT", "DROHE", "DROHT", "DRAEU", "DUCKE", "DUCKT", "DUDEL", "DUDLE",
				"DULDE", "DUMME", "DUNEM", "DUNEN", "DUNER", "DUNES", "DUSCH", "DUSLE", "DUTZE", "DUTZT", "DUZET", "DUZTE", "DAEMM", "DOERR", "DOESE", "DOEST", "DUENG", "DUENK", "DUEST", "EBBET",
				"EBBST", "EBBTE", "EBNET", "ECHOE", "ECHOT", "ECHTE", "EDELN", "EDIER", "EDLEM", "EDLEN", "EDLER", "EDLES", "EGALE", "EGGET", "EGGST", "EGGTE", "EHERN", "EHRET", "EHRST", "EHRTE",
				"EICHT", "EIDET", "EIDIG", "EIERE", "EIERT", "EIFRE", "EIGNE", "EILET", "EILST", "EILTE", "EINEM", "EINES", "EINET", "EINTE", "EIRET", "EISET", "EISTE", "EITLE", "EITRE", "EKELE",
				"EKELT", "EKLEM", "EKLEN", "EKLER", "EKLES", "EKLET", "EKLIG", "EKRUE", "ELFTE", "ELJEN", "EMDET", "ENDEL", "ENDET", "ENDIG", "ENDLE", "ENGEM", "ENGES", "ENGET", "ENGST", "ENGTE",
				"ENNET", "ENTER", "ENTRE", "ERAHN", "ERARM", "ERBAT", "ERBAU", "ERBEB", "ERBET", "ERBOS", "ERBOT", "ERBST", "ERBTE", "ERBUB", "ERDET", "ERDIG", "EREIL", "ERERB", "ERGAB", "ERGEH",
				"ERGIB", "ERHEB", "ERHOB", "ERHOL", "ERHUB", "ERJAG", "ERKOR", "ERLAB", "ERLAS", "ERLEB", "ERLEG", "ERLOG", "ERLOS", "ERLOT", "ERNEU", "ERRAT", "ERREG", "ERSAH", "ERWEB", "ERWOB",
				"ERWOG", "ERZET", "ERZOG", "ERZTE", "ESSET", "ETWER", "EUERE", "EUMLE", "EURES", "EWIGE", "EXEND", "EXEST", "EXTEN", "EXTET", "FABLE", "FACHT", "FADEM", "FADER", "FADES", "FAHEN",
				"FAHLE", "FAHND", "FAHRE", "FAIRE", "FAKEN", "FAKET", "FAKST", "FAKTE", "FALLT", "FALZT", "FANGT", "FASCH", "FASET", "FASLE", "FASRE", "FASST", "FAUCH", "FAULT", "FAXET", "FAXTE",
				"FECHS", "FEDRE", "FEGET", "FEGTE", "FEHLT", "FEIEN", "FEIET", "FEILT", "FEINE", "FEINT", "FEIRE", "FEITE", "FEIXE", "FEIXT", "FENZE", "FENZT", "FERNT", "FETAL", "FETZE", "FETZT",
				"FEURE", "FICHT", "FICKT", "FIELE", "FIELT", "FIEPE", "FIEPS", "FIEPT", "FIERE", "FIERT", "FIESE", "FILMT", "FILZT", "FINDE", "FINGE", "FINGT", "FINIT", "FIRME", "FIRMT", "FIRNT",
				"FISTE", "FITTE", "FITZE", "FITZT", "FIXEM", "FIXET", "FIXTE", "FLAGG", "FLAMM", "FLANK", "FLASH", "FLAUE", "FLAXT", "FLEHE", "FLEHM", "FLEHT", "FLENN", "FLENS", "FLENZ", "FLEXE",
				"FLEXT", "FLIEG", "FLIEH", "FLIES", "FLIPP", "FLIRR", "FLITZ", "FLOCK", "FLOGT", "FLOHT", "FLOPP", "FLOSS", "FLUTE", "FLOEH", "FLOET", "FOCHT", "FODER", "FODRE", "FOHLE", "FOHLT",
				"FOKAL", "FOLGT", "FOPPE", "FOPPT", "FORME", "FORMT", "FORTE", "FOTZT", "FOULE", "FOULT", "FRAGT", "FRAMT", "FREIT", "FRETT", "FREUE", "FREUT", "FRIED", "FRIER", "FROHE", "FRORT",
				"FRUGT", "FRAES", "FROEN", "FUGET", "FUGST", "FUGTE", "FUNKT", "FUNZE", "FUNZT", "FURCH", "FURZT", "FUSST", "FAELL", "FAERB", "FUEGE", "FUEGT", "FUEHL", "FUEHR", "FUELL", "FUERN",
				"GABLE", "GABST", "GACKS", "GAFFE", "GAFFT", "GALLT", "GAMEN", "GAMST", "GAMTE", "GAREM", "GARER", "GARES", "GARET", "GARNT", "GARST", "GARTE", "GASET", "GASIG", "GAUME", "GAUMT",
				"GEBAR", "GEDER", "GEEXT", "GEGNE", "GEHLE", "GEHRE", "GEHRT", "GEHST", "GEHTS", "GEIET", "GEIGT", "GEILE", "GEILT", "GEIRE", "GEITE", "GEIZT", "GELEM", "GELER", "GELLT", "GELOB",
				"GELTE", "GEMUT", "GENAS", "GERAT", "GERBE", "GERBT", "GEREU", "GERUH", "GETAN", "GEUDE", "GEUZT", "GHOST", "GIBST", "GICKS", "GIERE", "GIERT", "GIESS", "GIKSE", "GIKST", "GILBE",
				"GILBT", "GINGE", "GIPST", "GIRRT", "GLARE", "GLART", "GLAUB", "GLAUK", "GLEIT", "GLICH", "GLIMM", "GLOMM", "GLOTZ", "GLUCK", "GLUEH", "GNATZ", "GOLFT", "GOREN", "GORST", "GOSST",
				"GRABT", "GRAST", "GRAUE", "GRAUL", "GRAUT", "GREIN", "GRENZ", "GRETZ", "GRIEN", "GRINS", "GROBE", "GROOV", "GRUBB", "GRUBT", "GRUNZ", "GRAEM", "GRAET", "GROEL", "GUCKT", "GURKT",
				"GURRE", "GURRT", "GAEBE", "GAEBT", "GAEHN", "GAERE", "GAERT", "GOENN", "GOERT", "GUELL", "GUERT", "HAART", "HABET", "HABIL", "HABRE", "HACKT", "HADRE", "HAGET", "HAGLE", "HAGST",
				"HAGTE", "HAKEL", "HAKET", "HAKLE", "HAKST", "HAKTE", "HALAL", "HALFT", "HALIN", "HALLT", "HALST", "HAPPY", "HAPRE", "HARFT", "HARKT", "HARNT", "HARRT", "HARZT", "HASST", "HASTE",
				"HATTE", "HAUBT", "HAUET", "HAUST", "HAUTE", "HAUSS", "HEBET", "HEBLE", "HEBST", "HECKT", "HEERT", "HEFIG", "HEGET", "HEGST", "HEGTE", "HEHLE", "HEHLT", "HEHRE", "HEIDI", "HEIER",
				"HEILE", "HEILT", "HEINT", "HEISA", "HEIZE", "HEIZT", "HEISS", "HELAU", "HELFE", "HELFT", "HELLT", "HELMT", "HEMME", "HEMMT", "HENKE", "HENKT", "HERBE", "HERZE", "HERZT", "HERZU",
				"HETZT", "HEUEN", "HEUET", "HEULE", "HEULT", "HEURE", "HEUST", "HEXET", "HEXTE", "HICKS", "HIEBT", "HIELT", "HIEVE", "HIEVT", "HIEZU", "HIESS", "HILFT", "HINAN", "HINGE", "HINGT",
				"HINKE", "HINKT", "HINUM", "HISSE", "HISST", "HITZT", "HOBEN", "HOBLE", "HOBST", "HOCKT", "HOFFE", "HOFFT", "HOHEM", "HOHEN", "HOHER", "HOHES", "HOHLE", "HOLDE", "HOLET", "HOLLA",
				"HOLST", "HOLTE", "HOLZT", "HONEN", "HONET", "HONST", "HONTE", "HOOKE", "HOOKT", "HOPFE", "HOPFT", "HOPST", "HORCH", "HORNT", "HOSET", "HOSSA", "HOSTE", "HOWDY", "HUBST", "HUDER",
				"HUDLE", "HUDRE", "HUFET", "HUFST", "HUFTE", "HUJUS", "HUMID", "HUMIL", "HUMOS", "HUMSE", "HUMST", "HUNZE", "HUNZT", "HUPET", "HUPFE", "HUPFT", "HUPST", "HUPTE", "HURET", "HURST",
				"HURTE", "HUSCH", "HUSSA", "HUSST", "HUSTE", "HYPEN", "HYPET", "HYPST", "HYPTE", "HAELT", "HAEMT", "HAENG", "HAERE", "HAERM", "HAERT", "HAEUF", "HAEUT", "HOEBE", "HOEBT", "HOEHL",
				"HOEHN", "HOEHT", "HOEKE", "HOEKT", "HOERE", "HOERN", "HOERT", "HUEBT", "HUELL", "HUELS", "HUEPF", "IAHEN", "IAHET", "IAHST", "IAHTE", "IDENT", "IGELE", "IGELT", "IGITT", "IGLET",
				"IHRZE", "IHRZT", "ILLRE", "IMKRE", "IMPFE", "IMPFT", "INDES", "INERT", "INNER", "INTUS", "IRRES", "IRRET", "IRRTE", "ISSES", "JAGET", "JAGTE", "JAMME", "JAMMT", "JAPST", "JASSE",
				"JASST", "JAULE", "JAULT", "JAZZE", "JAZZT", "JEDEM", "JEDEN", "JEDER", "JEDES", "JENEM", "JENEN", "JENER", "JENES", "JETTE", "JEUEN", "JEUET", "JEUST", "JEUTE", "JOBBE", "JOBBT",
				"JOCHT", "JODEL", "JODLE", "JOGGE", "JOGGT", "JOHLE", "JOHLT", "JUBLE", "JUCHE", "JUCHT", "JUCKE", "JUCKT", "JUMPE", "JUMPT", "JUXET", "JUXTE", "JAEHR", "JAETE", "JUENG", "KABLE",
				"KACKT", "KADUK", "KAHLE", "KAJOL", "KAKEL", "KAKLE", "KALBT", "KALKT", "KALTE", "KAMST", "KAPPT", "KAPRE", "KARDE", "KARGE", "KARGT", "KARRT", "KARST", "KASCH", "KATTE", "KAUER",
				"KAUET", "KAUFE", "KAUFT", "KAURE", "KAUST", "KAUTE", "KECKE", "KEGLE", "KEHLT", "KEHRT", "KEIFE", "KEIFT", "KEILE", "KEILT", "KEIME", "KEIMT", "KEINE", "KEINS", "KENNE", "KENNT",
				"KERBT", "KERNE", "KERNT", "KESSE", "KEUCH", "KEULT", "KICKE", "KICKT", "KIEKE", "KIEKT", "KIESE", "KIEST", "KIFFE", "KIFFT", "KILLE", "KILLT", "KIPPT", "KIRRE", "KIRRT", "KITEN",
				"KITET", "KITTE", "KLACK", "KLAFF", "KLAGT", "KLAPP", "KLAPS", "KLARE", "KLASS", "KLAUB", "KLAUT", "KLEBE", "KLEBT", "KLECK", "KLEHE", "KLEHT", "KLEMM", "KLENG", "KLIEB", "KLIER",
				"KLIMM", "KLING", "KLINK", "KLIPP", "KLIRR", "KLOBT", "KLOMM", "KLONE", "KLONT", "KLOPF", "KLOPP", "KLUGE", "KLAER", "KLOEN", "KNACK", "KNAPS", "KNARR", "KNARZ", "KNAUB", "KNEIF",
				"KNEIS", "KNETE", "KNIET", "KNIPS", "KNITZ", "KNORR", "KNOSP", "KNOTE", "KNUFF", "KNURR", "KNUSE", "KNUST", "KOBER", "KOBRE", "KOCHE", "KOCHT", "KOHLT", "KOKEL", "KOKEN", "KOKET",
				"KOKLE", "KOKSE", "KOKST", "KOKTE", "KOMME", "KOMMT", "KOPFE", "KOPFT", "KOPPE", "KOPPT", "KORAM", "KOREN", "KORKE", "KORKT", "KORST", "KOSEN", "KOSET", "KOSTE", "KOTEN", "KOTET",
				"KOTIG", "KOTZE", "KOTZT", "KRABB", "KRALL", "KRAME", "KRAMT", "KRATZ", "KRAUE", "KRAUL", "KREID", "KRELL", "KRIPP", "KROCH", "KROSS", "KRUDE", "KRAEH", "KROEN", "KUCKE", "KUCKT",
				"KUDER", "KUDRE", "KUGLE", "KUMME", "KUREN", "KURET", "KURST", "KURTE", "KURVT", "KURZE", "KUSCH", "KAELK", "KAELT", "KAEME", "KAEMM", "KAEMT", "KAEST", "KAETZ", "KAEUE", "KAEUT",
				"KOEPF", "KOERE", "KOERN", "KOERT", "KUEND", "KUERE", "KUERT", "KUERZ", "KUESS", "LABEL", "LABER", "LABET", "LABLE", "LABRE", "LABST", "LABTE", "LACHT", "LACKE", "LACKT", "LADET",
				"LAFFE", "LAGEN", "LAGRE", "LAGST", "LAHME", "LAHMT", "LAICH", "LALLE", "LALLT", "LAMME", "LAMMT", "LANDE", "LANGT", "LAPPE", "LAPPT", "LASCH", "LASEN", "LASSE", "LASST", "LASTE",
				"LATZE", "LATZT", "LAUBT", "LAUEM", "LAUEN", "LAUER", "LAUES", "LAUFE", "LAUFT", "LAUGT", "LAURE", "LAUSE", "LAUST", "LAXEM", "LAXEN", "LAXER", "LAXES", "LEAKE", "LEAKT", "LEASE",
				"LEAST", "LEBET", "LEBST", "LEBTE", "LECKE", "LECKT", "LEDRE", "LEERT", "LEGER", "LEGET", "LEGTE", "LEHNT", "LEHRT", "LEIBT", "LEIDE", "LEIHE", "LEIHT", "LEIME", "LEIMT", "LEINT",
				"LEIRE", "LEIST", "LEITE", "LENKE", "LENKT", "LENZE", "LENZT", "LERNE", "LERNT", "LESET", "LETAL", "LETZE", "LETZT", "LIEBT", "LIEFE", "LIEFT", "LIEGT", "LIEHE", "LIEHT", "LIEST",
				"LIESS", "LIFTE", "LIIER", "LIKEN", "LIKET", "LIKST", "LIKTE", "LINKT", "LINST", "LISCH", "LISME", "LITTE", "LIVID", "LOBET", "LOBST", "LOBTE", "LOCHE", "LOCHT", "LOCKT", "LODEN",
				"LODER", "LODRE", "LOGEN", "LOGGE", "LOGGT", "LOGST", "LOHEN", "LOHET", "LOHNE", "LOHNT", "LOHST", "LOHTE", "LOOPE", "LOOPT", "LOOTE", "LOSCH", "LOSEM", "LOSER", "LOSES", "LOSET",
				"LOSTE", "LOTEN", "LOTET", "LOTST", "LUDEN", "LUDER", "LUDET", "LUDRE", "LUDST", "LUFTE", "LUGEN", "LUGET", "LUGST", "LUGTE", "LULLE", "LULLT", "LUNAR", "LUNZE", "LUNZT", "LUPFE",
				"LUPFT", "LUZID", "LAEDT", "LAEGE", "LAEGT", "LAEHM", "LAENG", "LAEPP", "LAESE", "LAEUT", "LOECK", "LOEGE", "LOEGT", "LOEHN", "LOESE", "LOEST", "LOETE", "LUEDE", "LUEFT", "LUEGT",
				"LUEPF", "LUEST", "LUETT", "MACHE", "MADIG", "MAGRE", "MAGST", "MAHLE", "MAHLT", "MAHNE", "MAHNT", "MAILE", "MAILT", "MAKLE", "MALAD", "MALLE", "MALLT", "MALME", "MALMT", "MALRE",
				"MAMPF", "MANAG", "MANCH", "MANNE", "MANNT", "MAPPT", "MARIN", "MAROD", "MASTE", "MATER", "MATRE", "MAUEM", "MAUEN", "MAUES", "MAULE", "MAULT", "MAUNZ", "MAURE", "MAUSE", "MAUST",
				"MAUVE", "MASST", "MEHLE", "MEHLT", "MEHRE", "MEHRT", "MEIDE", "MEIER", "MEINE", "MEINS", "MEINT", "MEIRE", "MELDE", "MELKE", "MELKT", "MENNO", "MERKE", "MERKT", "MESCH", "MESST",
				"METZG", "MIAUE", "MIAUT", "MIEDE", "MIEFE", "MIEFT", "MIESE", "MILBT", "MILKT", "MIMEN", "MIMET", "MIMST", "MIMTE", "MINEN", "MINET", "MINIM", "MINST", "MINTE", "MISCH", "MISSE",
				"MISST", "MISTE", "MIXET", "MIXTE", "MOBBE", "MOBBT", "MODEL", "MODLE", "MODRE", "MOGEL", "MOGLE", "MOLAR", "MOLKT", "MOOSE", "MOOST", "MOPPE", "MOPPT", "MOPSE", "MOPST", "MOROS",
				"MORSE", "MORST", "MOSRE", "MOTZE", "MOTZT", "MOXET", "MOXTE", "MUCKT", "MUFFT", "MUHEN", "MUHET", "MUHST", "MUHTE", "MUHTS", "MUMMT", "MURET", "MURRE", "MURRT", "MURST", "MURTE",
				"MUSET", "MUSST", "MUSTE", "MUTEN", "MUTET", "MAEHE", "MAEHL", "MAEHT", "MAEST", "MOEGE", "MOEGT", "MUEHT", "MUEND", "NABLE", "NADLE", "NAGET", "NAGLE", "NAGST", "NAGTE", "NAHEM",
				"NAHEN", "NAHER", "NAHES", "NAHET", "NAHMT", "NAHST", "NAHTE", "NARBT", "NARRE", "NARRT", "NASCH", "NATIV", "NEBLE", "NEBST", "NECKE", "NECKT", "NEHME", "NEHMT", "NEIGT", "NEINE",
				"NEINT", "NENNE", "NENNT", "NEPPE", "NEPPT", "NERVT", "NETZT", "NEUNE", "NEUNT", "NEURE", "NICKE", "NICKT", "NIESE", "NIEST", "NIMMT", "NIPPE", "NIPPT", "NISTE", "NIVAL", "NOBLE",
				"NOCTU", "NORME", "NORMT", "NUDLE", "NULLE", "NULLT", "NUTET", "NUTZT", "NAEHR", "NAEHT", "NAESS", "NOELE", "NOELT", "NOERE", "NOERT", "NUETZ", "OBIGE", "OBLAG", "OBSEN", "OBSET",
				"OCHST", "ODELE", "ODELN", "ODELT", "ODIOS", "ODLET", "OKTAL", "OLLEM", "OLLEN", "OLLER", "OLLES", "OPAKE", "OPFRE", "ORALE", "ORDNE", "ORDRE", "OREAL", "ORGLE", "ORTET", "OSTET",
				"OUTET", "OVOID", "OWNEN", "OWNET", "PAART", "PACKT", "PAFFE", "PAFFT", "PAGAN", "PALEN", "PALET", "PALST", "PALTE", "PAPAL", "PAPPT", "PARKT", "PARST", "PASST", "PATZE", "PATZT",
				"PAUKT", "PAUST", "PECKE", "PECKT", "PEDDE", "PEILE", "PEILT", "PEKIG", "PELLT", "PENIL", "PENNT", "PERDU", "PERLT", "PESET", "PESTE", "PETTE", "PETZT", "PFARR", "PFEIF", "PFETZ",
				"PFLEG", "PFLOG", "PICHE", "PICHT", "PICKT", "PIEKE", "PIEKT", "PIEPT", "PIERC", "PIKET", "PIKSE", "PIKST", "PINGT", "PINNT", "PISST", "PIXLE", "PLACK", "PLAGT", "PLANT", "PLINS",
				"PLOPP", "POCHE", "POCHT", "POFEN", "POFET", "POFST", "POFTE", "POGEN", "POGET", "POGST", "POGTE", "POKRE", "POLET", "POLKE", "POLKT", "POLST", "POLTE", "POPLE", "POPPT", "POSET",
				"POSTE", "POWRE", "PRACK", "PRAHL", "PRANG", "PRANZ", "PRASS", "PREIE", "PREIT", "PRELL", "PRESS", "PRICK", "PRIES", "PRIME", "PROBT", "PRUST", "PRAEG", "PRUEF", "PUCKE", "PUCKT",
				"PUDLE", "PUDRE", "PUFFT", "PULEN", "PULET", "PULLT", "PULST", "PUMPT", "PUNCH", "PUNZT", "PUPET", "PUPPT", "PUPST", "PUPTE", "PUREM", "PUREN", "PURER", "PURES", "PURRE", "PURRT",
				"PUSCH", "PUSHE", "PUSHT", "PUTZT", "POEHL", "POELZ", "QUAKE", "QUAKT", "QUASE", "QUEER", "QUERT", "QUICK", "QUIEK", "QUILL", "QUITT", "QUIZZ", "QUOLL", "QUORR", "QUAEK", "QUAEL",
				"RADEL", "RADLE", "RAFFE", "RAFFT", "RAGEN", "RAGET", "RAGST", "RAGTE", "RAHME", "RAHMT", "RAHNE", "RALLT", "RAMMT", "RANGT", "RANKT", "RANNT", "RAPID", "RAPPT", "RAREM", "RAREN",
				"RARER", "RARES", "RASET", "RATET", "RATZT", "RAUBT", "RAUEM", "RAUEN", "RAUER", "RAUES", "RAUET", "RAUFT", "RAUNE", "RAUNT", "RAUNZ", "RAUST", "RAVEN", "RAVET", "RAVST", "RAVTE",
				"REALE", "REBEL", "REBLE", "RECHE", "RECKT", "REDET", "REESE", "REEST", "REFFT", "REGEM", "REGER", "REGES", "REGET", "REGNE", "REGST", "REGTE", "REIBT", "REIFT", "REIHT", "REIMT",
				"REIST", "REITE", "REIZT", "REISS", "REKEL", "REKLE", "RELAX", "RENAL", "RENKT", "RENNE", "RENNT", "RETTE", "REUEN", "REUTE", "RICHT", "RIEBE", "RIEBT", "RIECH", "RIEFT", "RIEHE",
				"RIEHT", "RIETE", "RIGGE", "RIGGT", "RIGID", "RIGOL", "RILLT", "RINGT", "RINNT", "RIPPT", "RISST", "RITZT", "ROBBT", "ROCHT", "ROCKT", "RODET", "RODLE", "ROHEM", "ROHEN", "ROHER",
				"ROHES", "ROHRT", "ROLLT", "ROSAE", "ROTEM", "ROTER", "ROTES", "ROTZE", "ROTZT", "RUCKT", "RUDRE", "RUFET", "RUFST", "RUHET", "RUHST", "RUHTE", "RUMSE", "RUMST", "RUPFE", "RUPFT",
				"RURAL", "RUSST", "RAECH", "RAEUM", "RAESS", "ROEHR", "ROEST", "RUECK", "RUEGT", "RUEHM", "RUEHR", "RUEST", "SACKT", "SAGET", "SAGST", "SAGTE", "SAHEN", "SAHNT", "SAHST", "SALBT",
				"SALZT", "SALUE", "SANGT", "SANNT", "SAUET", "SAUFE", "SAUFT", "SAUGE", "SAUGT", "SAUNE", "SAUNT", "SAURE", "SAUST", "SAUTE", "SAVEN", "SAVET", "SAVST", "SAVTE", "SASST", "SCANN",
				"SCHAB", "SCHER", "SCHES", "SCHOB", "SCHOR", "SCHUF", "SCHUL", "SEELT", "SEGLE", "SEGNE", "SEHET", "SEHNT", "SEICH", "SEIEN", "SEIER", "SEIFT", "SEIHE", "SEIHT", "SEILT", "SEIRE",
				"SEIST", "SELBE", "SELCH", "SENDE", "SENGT", "SENKT", "SENNT", "SENST", "SETZE", "SETZT", "SEUCH", "SEUFZ", "SEXEN", "SEXET", "SEXYE", "SHISH", "SHOPP", "SHORT", "SICKT", "SIEBT",
				"SIECH", "SIEDE", "SIEGT", "SIEHE", "SIEHT", "SIELT", "SIEZE", "SIEZT", "SIMST", "SINGE", "SINGT", "SINKE", "SINKT", "SINNT", "SIPPT", "SIRRE", "SIRRT", "SITZT", "SKYPE", "SKYPT",
				"SLIME", "SLIMT", "SLIPP", "SMALL", "SMART", "SNOEB", "SOCKT", "SOFFT", "SOGST", "SOHIN", "SOHLT", "SOLAR", "SOLCH", "SOLID", "SOLLE", "SOLLT", "SONNT", "SONOR", "SOOFT", "SORGT",
				"SORRY", "SOWAS", "SPACK", "SPART", "SPAWN", "SPEIB", "SPEIE", "SPEIT", "SPELL", "SPEND", "SPERR", "SPICK", "SPIEB", "SPIEN", "SPIET", "SPINN", "SPUCK", "SPUKT", "SPULT", "SPURE",
				"SPUTE", "SPAEH", "SPAEN", "SPUEL", "SPUER", "STABT", "STACH", "STAKS", "STAKT", "STALK", "STANK", "STAPF", "STARB", "STAUN", "STAUT", "STECK", "STEHE", "STEHN", "STEHT", "STELL",
				"STELZ", "STEMM", "STEPP", "STETE", "STIEB", "STIEF", "STIEG", "STIMM", "STINK", "STIPP", "STIRB", "STOBT", "STOCH", "STOPF", "STRAF", "STREB", "STUFT", "STUKE", "STUKT", "STUND",
				"STUPF", "STURE", "STYLE", "STYLT", "SUDEL", "SUDER", "SUDLE", "SUDRE", "SUHLT", "SULZE", "SULZT", "SUMMT", "SUMSE", "SUMST", "SUPPT", "SURET", "SURFE", "SURFT", "SURRE", "SURRT",
				"SURST", "SURTE", "SUTJE", "SAECK", "SAEET", "SAEGT", "SAEHE", "SAEHT", "SAEST", "SAETE", "SAEUG", "SAEUM", "SOEGE", "SOEGT", "SOEHN", "SUEHN", "SUELZ", "TACKT", "TADLE", "TAFFE",
				"TAFLE", "TAGET", "TAGGE", "TAGGT", "TAGST", "TAGTE", "TAKEL", "TAKEM", "TAKEN", "TAKER", "TAKES", "TAKLE", "TALKT", "TANKT", "TANZT", "TAPEN", "TAPER", "TAPPE", "TAPPT", "TAPRE",
				"TAPSE", "TAPST", "TAPTE", "TARNE", "TARNT", "TATEN", "TATET", "TATST", "TAUCH", "TAUET", "TAUFT", "TAUGE", "TAUGT", "TAUPE", "TAUST", "TAUTE", "TAXET", "TAXTE", "TEERT", "TEILE",
				"TEILT", "TEUFT", "TEURE", "TEXTE", "TICKE", "TICKT", "TIGRE", "TILGE", "TILGT", "TILLT", "TILTE", "TIMEN", "TIMET", "TIMID", "TIMST", "TIMTE", "TIPPE", "TIPPT", "TITLE", "TOBET",
				"TOBST", "TOBTE", "TOFFE", "TOLLT", "TONAL", "TONET", "TONNT", "TONST", "TONTE", "TOPFT", "TOPPT", "TOSET", "TOSTE", "TOTES", "TOURE", "TOURT", "TRABE", "TRABT", "TRACK", "TRAFT",
				"TRAGT", "TRANK", "TRAUE", "TRAUT", "TREIB", "TREIF", "TREKK", "TRENN", "TRENZ", "TRETE", "TRIEF", "TRIEZ", "TRIFF", "TRIMM", "TRINK", "TRIPP", "TROFF", "TROGT", "TROTT", "TRUGT",
				"TRAEF", "TRAEG", "TRAEN", "TROEL", "TROET", "TRUEG", "TUCHT", "TUEND", "TUEST", "TUMBE", "TUNET", "TUNKT", "TUNST", "TUPFT", "TUPPE", "TUPPT", "TURNE", "TURNT", "TUTEN", "TUTET",
				"TYPET", "TYPST", "TYPTE", "TAETE", "TAEUB", "TOELT", "TOENT", "TOERE", "TOERT", "TOETE", "TUERK", "TUERM", "ULKET", "ULKST", "ULKTE", "UMAMI", "UMARM", "UMBOG", "UMGAB", "UMGEH",
				"UMGIB", "UMHAT", "UMHEG", "UMHIN", "UMKAM", "UMLOG", "UMLUD", "UMMAL", "UMRAS", "UMSAH", "UMTOB", "UMTOS", "UMTUN", "UMWEH", "UMZOG", "UMZON", "UNBAR", "UNIER", "UNKET", "UNKTE",
				"UNTIG", "URASS", "URIGE", "URSTE", "UZEND", "UZEST", "UZTEN", "UZTET", "VACAT", "VAGEM", "VAGEN", "VAGER", "VAGES", "VAGIL", "VALID", "VEGAN", "VERUZ", "VIELE", "VIERE", "VIERT",
				"VIFEM", "VIFEN", "VIFER", "VIFES", "VIRIL", "VOIPE", "VOIPT", "VOLAR", "VOLLE", "VORIG", "VOTET", "VRECK", "VULGO", "WABER", "WABRE", "WACHT", "WAGET", "WAGST", "WAGTE", "WAHRE",
				"WAHRT", "WALEI", "WALKT", "WALLT", "WALTE", "WALZT", "WANKE", "WANKT", "WANZT", "WARBT", "WARME", "WARNE", "WARNT", "WARST", "WASCH", "WASER", "WATET", "WEBET", "WEBST", "WEBTE",
				"WECKT", "WEDLE", "WEHEM", "WEHER", "WEHET", "WEHRT", "WEHST", "WEHTE", "WEIBT", "WEIFE", "WEIFT", "WEIHT", "WEILT", "WEINT", "WEIST", "WELKE", "WELKT", "WELLT", "WERBE", "WERBT",
				"WERFE", "WERKE", "WERKT", "WERTE", "WETZE", "WETZT", "WICHE", "WIDME", "WIDRE", "WIEGT", "WIEST", "WIMME", "WIMMT", "WINKT", "WIPPT", "WIRBT", "WIRFT", "WIRKE", "WIRKT", "WIRRE",
				"WIRRT", "WIRTE", "WISSE", "WISST", "WOBEN", "WOBST", "WOGET", "WOGST", "WOGTE", "WOHNE", "WOHNT", "WOKEM", "WOKEN", "WOKER", "WOKES", "WOLLT", "WRANG", "WRIGG", "WRING", "WUCHS",
				"WUMPE", "WUPPE", "WUPPT", "WURDE", "WURMT", "WURZE", "WURZT", "WUSCH", "WUSEL", "WUSLE", "WUZLE", "WAEGE", "WAEGT", "WAEHL", "WAEHN", "WAEHR", "WAELL", "WAELZ", "WAERE", "WAERM",
				"WAERT", "WOEBE", "WOEBT", "WOEGE", "WOEGT", "WOEHN", "WOELB", "WOELF", "WOELL", "WUEHL", "WUEMM", "WUERG", "WUERZ", "WUETE", "WUZUL", "ZACKT", "ZAGET", "ZAGTE", "ZAHLE", "ZAHLT",
				"ZAHME", "ZAHNT", "ZANKT", "ZAPFE", "ZAPFT", "ZAPPE", "ZAPPT", "ZARTE", "ZAUSE", "ZAUST", "ZECHT", "ZECKT", "ZEHNE", "ZEHRE", "ZEHRT", "ZEIGE", "ZEIGT", "ZEIHE", "ZEIHT", "ZERFE",
				"ZERFT", "ZERGE", "ZERGT", "ZERRE", "ZERRT", "ZETER", "ZETRE", "ZEUGT", "ZICKT", "ZIEHE", "ZIEHT", "ZIELE", "ZIELT", "ZIEME", "ZIEMT", "ZIEPE", "ZIEPT", "ZIERE", "ZIERT", "ZINKT",
				"ZINSE", "ZINST", "ZIPPE", "ZIPPT", "ZIRPE", "ZIRPT", "ZISCH", "ZOCKE", "ZOCKT", "ZOFFE", "ZOFFT", "ZOGEN", "ZOGST", "ZOLLT", "ZONAL", "ZOOME", "ZOOMT", "ZOPFT", "ZOTIG", "ZUCKE",
				"ZUCKT", "ZUGAB", "ZUGIG", "ZUHAT", "ZUKAM", "ZULUD", "ZUPFE", "ZUPFT", "ZURRE", "ZURRT", "ZUSAG", "ZUSAH", "ZUTUN", "ZUZEL", "ZUZLE", "ZUZOG", "ZWACK", "ZWEIE", "ZWEIT", "ZWICK",
				"ZWING", "ZWOTE", "ZWAEG", "ZAEHE", "ZAEHL", "ZAEHM", "ZAEHN", "ZAEUM", "ZAEUN", "ZOEGE", "ZOEGT", "ZUECK", "ZUEND", "ZUERN", "AEBIS", "AEBTE", "AELTE", "AEONS", "AERAR", "AEREN",
				"AESER", "AESOP", "AESTE", "AETNA", "AEXTE", "OEDEM", "OEDEN", "OEFEN", "OEFFI", "OEHIS", "OEHMD", "OEHRE", "OEHRN", "OEHRS", "OEKOS", "OELES", "OERES", "OESCH", "OESEL", "OESEN",
				"OESIS", "OETZI", "AEBER", "AECHT", "AEFFE", "AEFFT", "AESET", "AETZE", "AETZT", "AEUGE", "AEUGT", "AESSE", "OEDER", "OEDES", "OELEN", "OELET", "OELST", "OELTE", "OESET", "OESTE",
				"UEBET", "UEBLE", "UEBST", "UEBTE", "KANJI", "KANTS", "KANUN", "KANUS", "KAONS", "KAPOS", "KAPPA", "KAPUT", "KARAS", "KARAT", "KAREN", "KARES", "KARMA", "KAROK", "KARON", "KAROS",
				"KASAN", "KASBA", "KASEL", "KASKO", "KASUS", "KATAR", "KATAS", "KATEN", "KATHI", "KATJA", "KAUBS", "KAUFS", "KAURI", "KAUZE", "KAWIS", "KAZOO", "KEBAB", "KEBAP", "KEBSE", "KEDER",
				"KEFIR", "KEHLS", "KEIKE", "KEILS", "KEIMS", "KELIM", "KELLY", "KELPS", "KELTE", "KEMPO", "KENDO", "KENEM", "KENIA", "KERFE", "KERFS", "KERLE", "KERLS", "KERNS", "KERRY", "KERUB",
				"KERVE", "KERWE", "KETEL", "KETEN", "KETIN", "KETON", "KEVIN", "KHAKI", "KHANE", "KHANS", "KHMER", "KIBAS", "KICKS", "KIDDY", "KIELE", "KIELS", "KIEME", "KIENS", "KIEPE", "KIETZ",
				"KIEWS", "KIEZE", "KIGAS", "KIKIS", "KILBE", "KILBI", "KILOS", "KILTS", "KIMME", "KINDE", "KINDS", "KINGS", "KINNE", "KINNS", "KINOS", "KIOTO", "KIPFL", "KIPPA", "KIPPS", "KIRKE",
				"KIRNE", "KIRNS", "KIROW", "KITAS", "KITTS", "KITZE", "KIWIS", "KJELL", "KLAAS", "KLANE", "KLANS", "KLAUS", "KLEES", "KLEIE", "KLEIS", "KLEVE", "KLIFF", "KLINE", "KLIOS", "KLITS",
				"KLONS", "KLOPS", "KLOSE", "KLUBS", "KLUMP", "KNANS", "KNAUF", "KNIES", "KNOCK", "KNUDS", "KNUTE", "KNUTT", "KOALA", "KOANS", "KOBAN", "KOBEL", "KOBEN", "KOBOT", "KOBRA", "KOCHS",
				"KODAS", "KODES", "KOGGE", "KOGOS", "KOHLS", "KOHTE", "KOINE", "KOJEN", "KOKAS", "KOKKE", "KOKON", "KOKOS", "KOLAS", "KOLIK", "KOLJA", "KOLLI", "KOLLO", "KOLON", "KOMAS", "KOMBI",
				"KOMIS", "KONAK", "KONDO", "KONGO", "KONTI", "KOPFS", "KOPPA", "KOPPS", "KOPRA", "KORAN", "KORBE", "KORBS", "KOREA", "KORFU", "KORKS", "KORNE", "KORNS", "KORPS", "KORSE", "KORSO",
				"KOSAK", "KOTAU", "KOTES", "KOTOS", "KOTTI", "KRADS", "KRAIN", "KRAKE", "KRALE", "KRALS", "KRAMS", "KRANE", "KRANS", "KRAPP", "KRAXE", "KREDO", "KREML", "KREMS", "KRENS", "KREPP",
				"KRETA", "KRIDA", "KRIEK", "KRIPO", "KRUGE", "KRUGS", "KRUKE", "KRUME", "KRUPP", "KRUSE", "KSABI", "KUBAN", "KUBAS", "KUBEN", "KUBUS", "KUDOS", "KUHLE", "KUHNS", "KUJON", "KULIS",
				"KULMS", "KULTE", "KULTS", "KUMPF", "KUNFT", "KUNZE", "KUPON", "KURAT", "KURDE", "KURIN", "KURSK", "KURTS", "KUSEL", "KUSSE", "KWASS", "KYMAS", "KYOTO", "KYUDO", "KAETE", "KOEGE",
				"KOELN", "KUEHE", "LAABS", "LAACH", "LAAGE", "LABAN", "LABEN", "LABES", "LABOE", "LACKL", "LACKS", "LADER", "LADES", "LADYS", "LAGGS", "LAHAR", "LAHRS", "LAIBE", "LAIBS", "LAIEN",
				"LAINZ", "LAKAI", "LAKEN", "LAKIN", "LALAS", "LAMAS", "LAMMS", "LANDS", "LANGS", "LAOTE", "LARAS", "LAREN", "LASIN", "LASSI", "LATEN", "LATES", "LAUFS", "LAURA", "LAUTA", "LEBUS",
				"LECHS", "LECKS", "LEDAS", "LEEDS", "LEERS", "LEEZE", "LEGAT", "LEHLE", "LEHLS", "LEHME", "LEHMS", "LEIBE", "LEIBS", "LEICH", "LEIDS", "LEIEN", "LEIMS", "LEINS", "LEKTE", "LEKTS",
				"LEMGO", "LEMMA", "LEMUR", "LENAS", "LENDS", "LENNE", "LEONS", "LEPAS", "LEPRA", "LESBE", "LESTE", "LETTE", "LEUEN", "LEUNA", "LEUNS", "LEUTS", "LEVEL", "LEVKE", "LEXEM", "LEXIK",
				"LIANE", "LIBAN", "LIBAU", "LICHS", "LIDER", "LIDES", "LIEDE", "LIEDS", "LIEKS", "LIENZ", "LIESE", "LIFTS", "LIGEN", "LIGER", "LIKES", "LILAS", "LILLI", "LIMAS", "LIMBI", "LIMES",
				"LIMOS", "LINUS", "LINUX", "LIPID", "LIPOM", "LIPSI", "LISPS", "LISTS", "LITHO", "LITZE", "LIVEN", "LIVIN", "LOBAU", "LOBBY", "LOBES", "LOBUS", "LOCHS", "LOCRO", "LODGE", "LODIS",
				"LOFTS", "LOGIS", "LOGOS", "LOHNS", "LOHRS", "LOIRE", "LOITS", "LOITZ", "LOKUS", "LOLCH", "LOLLI", "LOOKS", "LOOPS", "LORCH", "LORDS", "LOREN", "LORIS", "LORKE", "LOSTS", "LOTES",
				"LOTOS", "LOTTE", "LOTTO", "LOTUS", "LOVER", "LUCCA", "LUCHE", "LUCIA", "LUCKA", "LUFTS", "LUGAU", "LUGES", "LUKAS", "LUKEN", "LUKES", "LULUS", "LUMEN", "LUMME", "LUNAS", "LUNCH",
				"LUNDE", "LUNDS", "LUPEN", "LUSER", "LUTTE", "LUXOR", "LUZIA", "LUSSE", "LYDER", "LYDIA", "LYNNS", "LYONS", "LYREN", "LYSIN", "LYSSA", "LOELI", "LOESS", "LUEBZ", "MAARE", "MAARS",
				"MAATE", "MAATS", "MACAO", "MACIS", "MADLN", "MADLS", "MAFIA", "MAGDA", "MAGGI", "MAGNA", "MAGOT", "MAHDI", "MAHDS", "MAHLS", "MAHRE", "MAHRS", "MAHUT", "MAIEN", "MAIER", "MAIKE",
				"MAILS", "MAINE", "MAINS", "MAINZ", "MAIRS", "MAISE", "MAIUM", "MAISS", "MAJOS", "MAKAK", "MAKRA", "MAKRO", "MALAS", "MALES", "MALIK", "MALIS", "MALLS", "MALMS", "MALTA", "MALTE",
				"MALUM", "MALUS", "MALVE", "MALZE", "MAMAS", "MAMIS", "MAMMA", "MAMME", "MANDA", "MANDL", "MANDY", "MANGA", "MANGE", "MANNA", "MANNS", "MANSE", "MANTA", "MAORI", "MARAS", "MARCH",
				"MARCO", "MARCS", "MAREI", "MAREK", "MAREN", "MARES", "MARGE", "MARIA", "MARIE", "MARIO", "MARIS", "MARKS", "MARLS", "MARNE", "MARSE", "MASEL", "MASEN", "MASER", "MASTS", "MATCH",
				"MATEN", "MATHE", "MATJE", "MATTA", "MATTI", "MATTS", "MATUR", "MATZE", "MAUIS", "MAULS", "MAXIM", "MAYAS", "MAYEN", "MAYER", "MAYIM", "MAYOS", "MAYRS", "MAZIS", "MAZZE", "MEDEA",
				"MEDIA", "MEERS", "MEHLS", "MEHRS", "MEIKE", "MEIKO", "MEKKA", "MELKS", "MELLE", "MEMEL", "MEMEN", "MEMES", "MEMME", "MEMOS", "MENKE", "MENUS", "MERAN", "MEROE", "MESON", "MESSI",
				"METAL", "METES", "METRA", "METTS", "METZE", "MEYER", "MEYRS", "MEZIE", "MIAMI", "MIAUS", "MICHA", "MICHI", "MIDDE", "MIEFS", "MIERE", "MIEZE", "MIESS", "MIGDA", "MIKRO", "MIKWE",
				"MILAN", "MILFS", "MILJE", "MIMIN", "MINIS", "MINNA", "MINNE", "MINOS", "MINSK", "MINZE", "MIROW", "MISOS", "MISSA", "MISTS", "MITAU", "MIXED", "MIXES", "MIZOS", "MNEME", "MOCCA",
				"MODEN", "MODES", "MOERS", "MOFAS", "MOGUL", "MOHEL", "MOHNE", "MOHNS", "MOHRE", "MOHRS", "MOILD", "MOKKA", "MOLAS", "MOLEN", "MOLKE", "MOLLE", "MOLLI", "MOLLS", "MOLLY", "MOLOS",
				"MOMME", "MONDE", "MONDS", "MONEM", "MONGO", "MONOM", "MOORE", "MOORS", "MOPPS", "LONGE");
		List<String> full = new ArrayList<String>();
		full.addAll(list);
		full.addAll(solutions());
		return full;
	}

	public static List<String> solutions() {
		List<String> list = Arrays.asList("FISCH", "TROTZ", "BIRKE", "RUFEN", "NOBEL", "FOKUS", "KREUZ", "ZANGE", "WILLE", "TRICK", "IDOLE", "DROGE", "REGEL", "DOHLE", "HARFE", "HOEHE", "ZWANG",
				"EULEN", "HAGEL", "SALAT", "ALTER", "WONNE", "WANNE", "HASEN", "PUDEL", "LEHRE", "RADIO", "WEIHE", "DRAHT", "WOVON", "RIPPE", "GASSE", "RODEN", "CLOWN", "EISEN", "ORTEN", "SACHE",
				"ERKER", "SEIFE", "SAEGE", "KUFEN", "LINKS", "WIEGE", "SIRUP", "STOER", "SENAT", "MUSIK", "ZILLE", "LIMIT", "NOTIZ", "INFAM", "WUNDE", "TAUFE", "WULST", "FLUOR", "TEXTE", "HOBBY",
				"MUMIE", "LEUTE", "ASSEL", "ELITE", "VIDEO", "ABTEI", "PFUND", "GUMMI", "BOHNE", "TITEL", "PROSA", "MADEN", "TRIEB", "SPEER", "ELEVE", "GABEN", "ZUTUN", "KASSE", "GROLL", "BIRNE",
				"LUNTE", "MEILE", "ORGEL", "HACKE", "SKALA", "GRAMM", "DELLE", "STICH", "EUTER", "GRUBE", "URALT", "SINNE", "TOTAL", "JAGEN", "VENUS", "TADEL", "BREIT", "ERPEL", "LUNGE", "HOSEN",
				"SUPPE", "ARTIG", "SORTE", "HINZU", "WORAN", "ERBSE", "REIHE", "BRACH", "GENIE", "FEIGE", "MARKE", "SELIG", "START", "MESSE", "BONUS", "APRIL", "SAKKO", "WORUM", "CHAOS", "CHROM",
				"FAUST", "SILBE", "GRILL", "ALTAR", "LOKAL", "ESSIG", "DRECK", "WIPPE", "BAUCH", "KRISE", "UMBAU", "GUNST", "HYMNE", "BEIZE", "RAUPE", "ZUTAT", "LOGIK", "MILDE", "LYRIK", "TEMPO",
				"SOMIT", "WANGE", "WENIG", "DAHIN", "UEBEN", "HEGEN", "ABZUG", "FATAL", "HINAB", "REGIE", "DIAET", "STATT", "UNTER", "KOMMA", "BLICK", "SAHNE", "GREIS", "BINDE", "JUROR", "ZELLE",
				"MANIE", "HUMOR", "PUSTE", "STROM", "QUALM", "TANNE", "WARUM", "AUTOR", "SEITE", "MARKT", "GRIMM", "PFERD", "SPITZ", "RAPPE", "STEIG", "HAARE", "KELLE", "RISPE", "HECKE", "AHNEN",
				"APART", "COUCH", "ILTIS", "BISON", "FASER", "HERAN", "KLEID", "GAREN", "LITER", "REELL", "QUIRL", "KNABE", "FAUNA", "BERUF", "BAUER", "HAUBE", "STAAT", "FESCH", "KLIMA", "ZEHEN",
				"RINDE", "HALLE", "VOGEL", "DUENN", "EKLAT", "DISCO", "VILLA", "SZENE", "FOEHN", "NARBE", "FELGE", "RUMPF", "LINDE", "MINUS", "RECHT", "DUERR", "ORDEN", "OPFER", "NEBEN", "DAVON",
				"ERSTE", "FARBE", "ZWIRN", "ERNTE", "MENGE", "WALZE", "STETS", "KNAST", "AUTOS", "PAARE", "EBENE", "ESSEN", "STOCK", "BRETT", "KOMIK", "NYLON", "AGENT", "IRDEN", "HEBEN", "WOMIT",
				"SIPPE", "KARTE", "TRIST", "DRAMA", "BARDE", "GRIFF", "DEKOR", "MUTIG", "LARVE", "THESE", "MALER", "FAMOS", "PRIMA", "LAUTE", "ASIAT", "WOLLE", "HAUCH", "ANRUF", "MAERZ", "AUGEN",
				"TOSEN", "MAGER", "ALBUM", "GENAU", "EILIG", "LAUBE", "BARKE", "MONAT", "ROSIG", "LOIPE", "LACHE", "LEINE", "BOGEN", "RUHIG", "SEKTE", "WOGEN", "DOGMA", "INSEL", "LIEGE", "JOKER",
				"DRALL", "LEGAL", "GEBEN", "KUGEL", "HEBEL", "BEIGE", "RAMPE", "AXIAL", "KARRE", "ZWEIG", "LEHEN", "KRUMM", "RACHE", "NICHT", "OELIG", "JUWEL", "BAHRE", "LEFZE", "THEMA", "KEGEL",
				"RIESE", "IMMER", "QUERE", "BOTEN", "NONNE", "UNSER", "PILZE", "FEUER", "NISSE", "KREBS", "NUDEL", "KLANG", "NETTO", "FROST", "KOPIE", "WRACK", "LINKE", "BETON", "NATUR", "NELKE",
				"GLEIS", "WANZE", "KEHLE", "GALLE", "SECHS", "DATEI", "WERTE", "RENTE", "ANGEL", "BRAUE", "MITTE", "STARR", "TREND", "FOTOS", "SPAET", "ARMUT", "LEHNE", "STIEL", "ANGST", "MORAL",
				"DEKAN", "FABEL", "BEINE", "ZUNFT", "FRUST", "ETHIK", "GLATT", "WEICH", "STAMM", "OCHSE", "MOLCH", "MODER", "BIBER", "FRAGE", "KRANK", "LIPPE", "MAUER", "FEIND", "HERAB", "JETZT",
				"KNALL", "TITAN", "BRISE", "BOMBE", "STIFT", "SONNE", "GABEL", "EIFER", "EBNEN", "VIRUS", "BEUTE", "TREUE", "GEHEN", "TROLL", "GROSS", "FERNE", "TONNE", "STEIN", "ZIEGE", "HITZE",
				"MINEN", "KASSA", "HORDE", "AREAL", "NAGEN", "OSTEN", "HILFE", "ROETE", "SAGEN", "ERNST", "SCHAM", "BLUME", "TORTE", "DUNST", "HECHT", "AORTA", "FEGEN", "BOXEN", "SAITE", "PUPPE",
				"PISTE", "MANKO", "LUCHS", "FORUM", "STURM", "GARDE", "BLOED", "FRIST", "STERN", "GNADE", "STUMM", "ERDEN", "RASCH", "JUBEL", "ZUDEM", "ENDEN", "ENKEL", "SPASS", "FLOTT", "PANIK",
				"HORST", "INDEX", "RODEL", "SACHT", "LOEWE", "BRIEF", "GEIGE", "INNEN", "HAGER", "KOHLE", "PFEIL", "RASUR", "LENDE", "FRONT", "PAKET", "DOLCH", "RUEBE", "HENNE", "KAMPF", "ANMUT",
				"LOBEN", "DABEI", "HEIDE", "WORTE", "PEGEL", "STUFE", "PERLE", "STEIF", "RUEDE", "TRUPP", "GLIED", "REISE", "DARUM", "PIRAT", "WOCHE", "GAMBE", "BLOND", "WEBER", "FOREN", "ROSEN",
				"MENSA", "WIESO", "ACRYL", "ELEND", "UNRAT", "HEUTE", "WUCHT", "OHREN", "SCHEU", "EMPOR", "PAUSE", "RIEGE", "PATER", "KERBE", "VIRAL", "ABBAU", "FAKIR", "STIER", "JAHRE", "SOHLE",
				"EICHE", "SAUNA", "MAGMA", "SONDE", "KRIMI", "ATLAS", "KANTE", "SPATZ", "KAPER", "FALKE", "GLANZ", "LODEN", "LAMPE", "URBAN", "FUNDE", "HETZE", "RASER", "SPOTT", "UMHER", "SALBE",
				"EITER", "AXIOM", "BUCHT", "DEGEN", "DAMIT", "KROPF", "LADEN", "DARIN", "TORUS", "VOKAL", "MODUS", "FROMM", "HIRSE", "SEIDE", "AESEN", "HEXEN", "BELEG", "GENOM", "KURVE", "WITWE",
				"AFFEN", "MUEDE", "MUERB", "HOTEL", "TOBEN", "BLASS", "EIGEN", "DAHER", "SEHNE", "LILIE", "KOMET", "RUEGE", "PARTY", "KERZE", "MALEN", "KUNDE", "QUARK", "RASEN", "SUPER", "LOYAL",
				"REUIG", "ALPIN", "SCHAU", "FAZIT", "YACHT", "PROFI", "WERKE", "WAAGE", "ORKAN", "SCHAR", "KANNE", "TEICH", "IRRIG", "ZIVIL", "GUETE", "HEUER", "TAUEN", "PARAT", "SERIE", "VITAL",
				"ZINKE", "BOESE", "FEDER", "FLUGS", "OLIVE", "BANAL", "WESPE", "RITZE", "FASAN", "INDIZ", "KAMIN", "ZWERG", "RUSSE", "NADEL", "HOBEL", "PFOTE", "LISTE", "SPECK", "TANGO", "GANZE",
				"SICHT", "ATOLL", "KIPPE", "LESEN", "WINDE", "APFEL", "FADEN", "SALON", "KLEIN", "AKTEN", "BLUSE", "DINGE", "SALVE", "HERUM", "DRAUF", "WUCHS", "LABEN", "MILIZ", "WEITE", "ECHSE",
				"MIXEN", "MODAL", "BERGE", "MENUE", "FINTE", "SEHEN", "BLASE", "BEULE", "KUNST", "WAGEN", "DAUER", "RATEN", "RADAU", "BEVOR", "ETWAS", "CHLOR", "ULKIG", "EXTRA", "RUBIN", "MIETE",
				"BASIS", "CHILI", "DOSIS", "KODEX", "TRUEB", "PARTE", "WENDE", "BRAUN", "ZUZUG", "EINST", "MEERE", "STOLA", "ZOTTE", "KATER", "GRUFT", "FALLE", "EITEL", "FUENF", "PATEN", "HOLEN",
				"JACKE", "RUDER", "GEBET", "SPREU", "JUNGE", "QUOTE", "ABRUF", "LACHS", "OPTIK", "PFAND", "VIKAR", "MOBIL", "HAUPT", "BLIND", "MILCH", "LAGER", "TRAGE", "WIESE", "FLINK", "BRAUT",
				"DAMPF", "LAERM", "KRACH", "BANDE", "ZIRKA", "ZYSTE", "BADEN", "FALTE", "TUMOR", "PILOT", "LEBER", "ZUVOR", "HUENE", "ESCHE", "ERBIN", "HEFTE", "BLATT", "MOEWE", "FEIER", "HOCKE",
				"PLATZ", "TATEN", "FUNKE", "SORGE", "ROUTE", "LICHT", "PUNKT", "BIEST", "GURTE", "INTIM", "WESTE", "SALTO", "ARTEN", "SCHUH", "MAPPE", "ZUNGE", "AKTIV", "PFAHL", "FERSE", "SAEEN",
				"KNIFF", "GENUG", "STOLZ", "REIFE", "UNTEN", "SUMME", "NEBEL", "PRALL", "TABAK", "THRON", "MAJOR", "ORGIE", "RESTE", "SEGEL", "REIME", "PORTO", "WOHIN", "UMZUG", "LUXUS", "DURST",
				"WITZE", "ZEILE", "BESEN", "VATER", "ACHSE", "LOCKE", "KLUFT", "UMWEG", "MOPED", "ANKER", "OEDEM", "ANZUG", "TENOR", "WICHT", "TINTE", "POSSE", "OBERE", "EINIG", "SPIEL", "PAPST",
				"TIGER", "BAUEN", "GICHT", "SUCHE", "MAGIE", "EILEN", "KUEHL", "KAKAO", "PFLUG", "KNIEN", "WEISE", "MOTTO", "PALME", "WORIN", "TULPE", "BORTE", "ATOME", "KELCH", "DANKE", "SKALP",
				"LANZE", "WEBEN", "EXAKT", "GEGEN", "IMMUN", "STEIL", "STOFF", "SAMEN", "ONKEL", "OELEN", "LINIE", "ATMEN", "NABEL", "RABEN", "MIXER", "PAUKE", "GENRE", "KISTE", "FOLIE", "TOTEM",
				"ZEBRA", "STARK", "ERBEN", "DICKE", "ZUMAL", "WEIDE", "BEUGE", "WARTE", "ANTUN", "OBHUT", "WELPE", "KUPPE", "VISUM", "ADERN", "EINEN", "ZEUGE", "TIEFE", "PRISE", "BULLE", "BUCHE",
				"SITTE", "BEIDE", "WOBEI", "ROMAN", "PRINZ", "AROMA", "RINGE", "LIEBE", "FORST", "STUCK", "KEULE", "ETAGE", "LEDIG", "LUEGE", "MASSE", "SUMPF", "WEHEN", "LINSE", "NASAL", "ZENIT",
				"ORGAN", "MEIST", "SCHUB", "TAGEN", "AGAPE", "LOTSE", "TRITT", "ABEND", "KLOTZ", "RITUS", "GURKE", "POLAR", "LEBEN", "ROTOR", "DAVOR", "REGEN", "MOTOR", "TATZE", "RILLE", "MEUTE",
				"KURSE", "PINIE", "KLICK", "WURST", "WILDE", "STURZ", "GERTE", "ALGEN", "TEUER", "LESER", "MIMIK", "FINNE", "DUESE", "STAND", "GERNE", "ADLER", "AHORN", "ROBBE", "RADAR", "AMPEL",
				"NACHT", "DOGGE", "POLKA", "BEERE", "SANFT", "BISSE", "HAFER", "MILBE", "DUELL", "KASTE", "SOWIE", "IRREN", "LASSO", "MODEM", "PLATT", "FUGEN", "DACHS", "KEHRE", "WARAN", "EDIKT",
				"WEILE", "NOTAR", "KONTO", "COACH", "UNGUT", "BEBEN", "DIELE", "NAEHE", "DICHT", "HANDY", "IMKER", "SATAN", "INDEM", "SPORT", "MEISE", "JAUSE", "BETEN", "STAUB", "NOTEN", "BASAR",
				"CHAOT", "REGAL", "PANNE", "PROBE", "MULDE", "WATTE", "EIMER", "FLAIR", "HAFEN", "SCHAF", "EKZEM", "UHREN", "TUETE", "FRUEH", "HALDE", "KEKSE", "GUSTO", "DECKE", "STALL", "POKAL",
				"KNICK", "SAMBA", "TAUBE", "HUNDE", "KIOSK", "UNFUG", "KRAFT", "ZINNE", "PIXEL", "FUTUR", "ROHRE", "ALARM", "DRILL", "KLAGE", "EISIG", "WIDER", "SENKE", "LEISE", "DRUCK", "BRUCH",
				"BIWAK", "MATTE", "MAGEN", "HUPEN", "ALIAS", "VORNE", "KNAPP", "SPALT", "NEFFE", "ABGAS", "TROTT", "HALLO", "POKER", "FIRMA", "RAUTE", "ZACKE", "LATTE", "FAHNE", "UNART", "ALIBI",
				"VLIES", "TRAKT", "LEIER", "DAUNE", "SPORN", "TAFEL", "HAKEN", "FRECH", "REICH", "WETTE", "LABIL", "WARZE", "BIBEL", "BROTE", "TROSS", "PACHT", "VIOLA", "BACHE", "STAHL", "DRANG",
				"ZWECK", "SONST", "REDEN", "FOYER", "FLACH", "LASER", "MAKEL", "KERNE", "ZECHE", "RUDEL", "TIERE", "WESEN", "RINNE", "GEBOT", "FEILE", "WEGEN", "DEICH", "BORKE", "BLECH", "KAUEN",
				"FLUCH", "QUASI", "KEIME", "ABTUN", "ZIELE", "BACKE", "WACHS", "ARMEE", "LAUCH", "PAPPE", "RASSE", "BOOTE", "DUETT", "ZOBEL", "UEBEL", "FLOSS", "NAGEL", "FLUSS", "PLAGE", "KAMEL",
				"WADEN", "KNOPF", "SPULE", "GATTE", "TASSE", "GESTE", "CELLO", "TEILE", "ORBIT", "METTE", "TISCH", "HAEME", "LAUGE", "SEGEN", "KAESE", "TRAUM", "TARIF", "LOSEN", "BUBEN", "STUTE",
				"WAFFE", "STOPP", "LANGE", "SERUM", "FOLGE", "ENZYM", "FREMD", "ECKEN", "LEERE", "BITTE", "FLECK", "BLANK", "DATIV", "PLANE", "BUERO", "KREIS", "KLAUE", "VOTUM", "STADT", "KADER",
				"PILLE", "MASKE", "AKTIE", "GRUEN", "IDIOT", "MACKE", "MUELL", "MACHT", "FILME", "TROST", "EMSIG", "EHREN", "KANAL", "WATEN", "OPIUM", "SENIL", "SOGAR", "RUHEN", "BLITZ", "ENORM",
				"HURRA", "ARENA", "HABEN", "MOTTE", "URBAR", "FIDEL", "WIRTE", "ENTEN", "FIGUR", "BIENE", "SENSE", "SPION", "DARAN", "PIZZA", "DEPOT", "KLAMM", "FESTE", "AEHRE", "BRAND", "STROH",
				"GEIER", "PEDAL", "MOTIV", "SEILE", "DUMPF", "NIERE", "LAUNE", "ULKEN", "SOCKE", "GRUND", "METER", "BANGE", "ROLLE", "TYPEN", "WOHER", "ENGEL", "KRASS", "JEHER", "MANGO", "PASTA",
				"SAUER", "DELTA", "WUEST", "MIENE", "BEZUG", "TANTE", "BRUST", "WAISE", "STIRN", "IKONE", "WACHE", "OTTER", "IDEEN", "AMSEL", "THEKE", "HIRTE", "KATZE", "SALDO", "ZECKE", "ZWIST",
				"BARON", "EINER", "SCHAL", "IDEAL", "UNMUT", "KRANZ", "ACKER", "FEHDE", "WEDER", "UEBER", "KRAUS", "NOVUM", "KLOSS", "ASCHE", "FARCE", "NACKT", "RUINE", "KAPPE", "HUMAN", "SEELE",
				"DOCHT", "KRONE", "PUDER", "MACHO", "FAHRT", "TALER", "WERFT", "BOXER", "SUITE", "STUHL", "DUENE", "KANON", "LEDER", "MUEHE", "LURCH", "PREIS", "PLUMP", "BRITE", "OFFEN", "TOAST",
				"KUEHN", "HERDE", "RAUCH", "GOTIK", "TRANK", "ALPEN", "KABEL", "KETTE", "DURCH", "SESAM", "MODUL", "ZITAT", "LABOR", "KUTTE", "TASTE", "GEIST", "GRELL", "ANBAU", "HAUEN", "BLOCK",
				"VORAN", "RATTE", "GELSE", "INNIG", "HALME", "DOSEN", "STILL", "BELAG", "SEHER", "WELLE", "EKELN", "FUCHS", "SCHON", "OZEAN", "SUCHT", "FRACK", "PSALM", "KRAUT", "ALLEE", "TEILS",
				"ECKIG", "BODEN", "ZUCHT", "ANTIK", "DEMUT", "KURIE", "DAMEN", "LEGEN", "PHASE", "PONYS", "HONIG", "WOLKE", "UNION", "PRUNK", "STUBE", "VORAB", "PUMPE", "BUSCH", "DATUM", "JEANS",
				"RUNDE", "RISSE", "KRIEG");
		return list;
	}

}