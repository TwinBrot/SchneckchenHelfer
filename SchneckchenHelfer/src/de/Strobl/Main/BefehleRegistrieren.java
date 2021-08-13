package de.Strobl.Main;

import static net.dv8tion.jda.api.interactions.commands.OptionType.CHANNEL;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.ROLE;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class BefehleRegistrieren  {

    public static void register(JDA jda) {
    	System.out.println("");
    	System.out.println("Befehle werden registriert");
        CommandListUpdateAction commands = jda.updateCommands();
        
//Setup
        commands.addCommands(new CommandData("onlinestatus", "Ändert den Onlinestatus des Bots")
      			.addOptions(new OptionData(STRING,"onlinestatus", "Legt den Onlinestatus fest.")
      					.addChoice("online", "Ändert den Onlinestatus des Bots auf Online")
      					.addChoice("nichtstören", "Ändert den Onlinestatus des Bots auf bitte nicht stören.")
      					.addChoice("abwesend", "Ändert den Onlinestatus des Bots auf Abwesend.")
      					.setRequired(true)));

      commands.addCommands(new CommandData("activity", "Konfiguriert die Aktivität des Bots")
  			.addOptions(new OptionData(STRING,"activitytyp", "Typ der Activity auswählen.")
  					.addChoice("playing", "Ändere Aktivität auf: 'Spielt...'")
  					.addChoice("watching", "Ändere Aktivität auf: 'Schaut...zu'")
  					.addChoice("listening", "Ändere Aktivität auf: 'Hört ... zu'")
  					.addChoice("streaming", "Ändere Aktivität auf: 'Streamt...'")
  					.setRequired(true))
  			.addOptions(new OptionData(STRING, "activitytext", "Konfiguriert den Text der Activity")
  					.setRequired(true)));
        
        commands.addCommands(new CommandData("namen", "Konfiguriert die Namensüberwachung")
        				.addSubcommands(new SubcommandData("activate","Namensüberwachung aktivieren"))
        				.addSubcommands(new SubcommandData("deactivate","Namensüberwachung deaktivieren"))
        				.addSubcommands(new SubcommandData("add","Verbotene Namen hinzufügen"))
        				.addSubcommands(new SubcommandData("remove","Verbotene Namen entfernen"))
        				.addSubcommands(new SubcommandData("list","Liste der Verbotenen Namen")));
        
        commands.addCommands(new CommandData("datei", "Konfiguriert die Dateiüberwachung")
        				.addSubcommands(new SubcommandData("activate","Dateiüberwachung aktivieren"))
        				.addSubcommands(new SubcommandData("deactivate","Dateiüberwachung deaktivieren"))
        				.addSubcommands(new SubcommandData("add","Erlaubte Dateiendungen hinzufügen"))
        				.addSubcommands(new SubcommandData("remove","Erlaubte Dateiendungen entfernen"))
        				.addSubcommands(new SubcommandData("list","Liste der Erlaubte Dateiendungen")));

        commands.addCommands(new CommandData("afkchannel", "User, die in den AFKChannel gemoved werden, werden automatisch aus dem Voice gekickt.")
      		  		.addOptions(new OptionData(CHANNEL, "voicechannel", "Wähle den AFK-Voice Channel aus.")
      		  			.setRequired(true)));

        commands.addCommands(new CommandData("logchannel", "Legt den Kanal fest, in dem die Alarme gepostet werden.")
      		  		.addOptions(new OptionData(CHANNEL, "textchannel", "Wähle den Textchannel aus.")
      		  			.setRequired(true)));
        
        commands.addCommands(new CommandData("modrolle", "Konfiguriert die Modrollen")
        				.addSubcommands(new SubcommandData("add","Modrolle hinzufügen")
        						.addOptions(new OptionData(ROLE, "rolle","Hier die gewünschte Rolle angeben."))
        						.addOptions(new OptionData(STRING, "zugriffsstufe", "Welche Stufe soll die Rolle haben?")
        			  					.addChoice("Admin", "Administratoren haben Zugriff auf ALLE Befehle.")
        			  					.addChoice("Mod", "Moderatoren haben Zugriff auf Befehle zum Moderieren.")
        			  					.addChoice("ChannelMod", "Channelmoderatoren haben Zugriff auf den Hinweis Befehl.")))
        				.addSubcommands(new SubcommandData("remove","Verbotene Dateiendungen entfernen")
        						.addOptions(new OptionData(ROLE, "rolle","Hier die gewünschte Rolle angeben.")))
        				.addSubcommands(new SubcommandData("list","Listet alle Modrollen auf.")));
        
//Allgemein 
        
        commands.addCommands(new CommandData("emotes", "Startet die Emoteauswertung"));

//        commands.addCommands(new CommandData("help", "Zeigt die Hilfe an")
//    				.addSubcommands(new SubcommandData("setup","Zeigt die Befehle zum einrichten des Bots.")
//    						.addOptions(new OptionData(STRING,"hilfebereich","Wobei benötigst du Hilfe?")
//    								.addChoice("activity","Konfigurieren der Bot Aktivität.")
//    								.addChoice("onlinestatus","Einstellen des Onlinestatus des Bots.")
//    								.addChoice("afkchannel","Konfigurieren desx AFK-Channels.")
//    								.addChoice("logchannel","Konfigurieren des LogChannels.")
//    								.addChoice("modrolle","Einstellen der Modrollen.")
//    								.addChoice("namen","Konfigurieren der Namensüberwachung.")
//    								.addChoice("datei","Konfigurieren der Dateiüberwachung.")))
//    				.addSubcommands(new SubcommandData("userinfo","Befehle die Infos über den User anzeigen.")
//    						.addOptions(new OptionData(STRING,"hilfebereich","Wobei benötigst du Hilfe?")
//    								.addChoice("info","Zeigt allgemeine Userinformationen an.")
//    								.addChoice("hinweise","Einstellen des Onlinestatus des Bots")
////    								.addChoice("warns","Zeigt die Hinweise eines Users an.")
//    								.addChoice("mutes","Zeigt die Mutes eines Users an.")
//    								.addChoice("kicks","Zeigt die kicks eines Users an.")
//    								.addChoice("bans","Zeigt die Bans eines Users an.")))
//    				.addSubcommands(new SubcommandData("moderieren","Befehle die zur Moderation benötigt werden.")
//    						.addOptions(new OptionData(STRING,"hilfebereich","Wobei benötigst du Hilfe?")
//    								.addChoice("hinweis","Schickt einem User einen Hinweis.")
////    								.addChoice("warn","Verwarnt einen User.")
//    								.addChoice("tempmute","Mutet einen User temporär.")
//    								.addChoice("mute","Mutet einen User.")
//    								.addChoice("kick","Kickt einen User.")
//    								.addChoice("tempban","Bannt einen User temporär.")
//    								.addChoice("ban","Bannt einen User.")))
//    				.addSubcommands(new SubcommandData("moderation_ändern","Befehle die Strafen nachträglich ändern.")
//    						.addOptions(new OptionData(STRING,"hilfebereich","Wobei benötigst du Hilfe?")
//    								.addChoice("hinweis_remove","Löscht einen Hinweis aus dem Speicher.")
////    								.addChoice("warn_remove","Löscht einen Warn aus dem Speicher.")
//    								.addChoice("mute_remove","Löscht einen Mute aus dem Speicher.")
//    								.addChoice("kick_remove","Löscht einen Kick aus dem Speicher.")
//    								.addChoice("ban_remove","Löscht einen Ban aus dem Speicher. KEIN ENTBANN! NUR DOKU!")
//    								.addChoice("changeban","Ändert die Dauer eines Temporären Bans.")
//    								.addChoice("changemute","Ändert die Dauer eines Temporären Mutes.")
//    								.addChoice("unban","Entbannt einen Spieler. NUR ENTBANN! DOKU BLEIBT BESTEHEN!")
//    								.addChoice("unmute","Entmutet einen Spieler. NUR UNMUTE! DOKU BLEIBT BESTEHEN!")))
//    						.addOptions(new OptionData(STRING,"sonstiges","Alle sonstigen Befehle")
//    								.addChoice("emotes","Startet die Emoteauswertung")
//    								.addChoice("help","Öffnet die Hilfe.")));
//Moderation  
        
        commands.addCommands(new CommandData("info", "Ruft Informationen über einen User ab.")
    			.addOptions(new OptionData(STRING,"userid", "Wähle hier den User aus.")
    					.setRequired(true)));        
        
        commands.addCommands(new CommandData("infon", "Ruft Informationen über einen User ab.")
    			.addOptions(new OptionData(USER,"user", "Wähle hier den User aus.")
    					.setRequired(true)));
        
        commands.addCommands(new CommandData("hinweisn", "Schickt einem User einen Hinweis")
    			.addOptions(new OptionData(USER,"user", "Wähle hier den User aus.")
    					.setRequired(true))
    			.addOptions(new OptionData(STRING,"grund", "Gib hier den Hinweis-Text an.")
    					.setRequired(true)));
        
        commands.addCommands(new CommandData("hinweis", "Schickt einem User einen Hinweis")
    			.addOptions(new OptionData(STRING,"user", "Gib hier die User-ID an.")
    					.setRequired(true))
    			.addOptions(new OptionData(STRING,"grund", "Gib hier den Hinweis-Text an.")
    					.setRequired(true)));
        
        commands.addCommands(new CommandData("kick", "Kickt den ausgewählten User")
    			.addOptions(new OptionData(USER,"user", "Wähle hier den User aus.")
    					.setRequired(true))
    			.addOptions(new OptionData(STRING,"grund", "Gib hier den Grund des Kicks an.")));
        
        commands.addCommands(new CommandData("ban", "Bannt den ausgewählten User")
        			.addOptions(new OptionData(USER,"user", "Wähle den zu bannenden User aus.")
        					.setRequired(true))
        			.addOptions(new OptionData(STRING,"grund", "Gib hier den Grund des Bans an.")));
        
        commands.addCommands(new CommandData("changeban", "Ändert die Dauer eines Tempbans.")
        			.addOptions(new OptionData(STRING,"user", "Gib die ID des Users an")
        					.setRequired(true))
        			.addOptions(new OptionData(STRING,"dauer", "Gib hier die neue Dauer des TempBans an")
        					.setRequired(true)));
        
        commands.addCommands(new CommandData("removeban", "Entfernt einen Ban aus dem Speicher. KEIN UNBAN!")
    				.addOptions(new OptionData(STRING,"banid", "Wähle den Ban aus! (/bans USER)")
    						.setRequired(true)));

//        commands.addCommands(new CommandData("activity", "Ändert die Activity des Bots")
//    			.addOptions(new OptionData(STRING,"test", "test").addChoice("test1", "Test12").addChoice("test2", "Test22").addChoice("test3", "Test33").setRequired(true)
//    					)
//    			);

        commands.complete();
    	System.out.println("Befehle wurden registriert");
    	System.out.println("");
    }
}