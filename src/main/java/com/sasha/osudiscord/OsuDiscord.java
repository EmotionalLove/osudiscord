package com.sasha.osudiscord;

import com.oopsjpeg.osu4j.backend.EndpointBeatmaps;
import com.oopsjpeg.osu4j.backend.Osu;
import com.sasha.osudiscord.command.*;
import com.sasha.simplecmdsys.SimpleCommandProcessor;
import com.sasha.simplesettings.SettingHandler;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;

public class OsuDiscord {

    public static OsuDiscord INSTANCE;

    public JDA jdaDiscord;
    public Osu osuApi;

    public Configuration config = new Configuration();
    public SettingHandler SETTING_HANDLER = new SettingHandler("OsuDiscordConfig");
    public SimpleCommandProcessor COMMAND_PROCESSOR = new SimpleCommandProcessor("\\");

    public static void main(String[] args) throws LoginException, InterruptedException, IllegalAccessException, InstantiationException {
        INSTANCE = new OsuDiscord();
        INSTANCE.SETTING_HANDLER.read(INSTANCE.config);
        INSTANCE.start();
    }

    public void start() throws LoginException, InterruptedException, InstantiationException, IllegalAccessException {
        System.out.println("Connecting to Discord...");
        COMMAND_PROCESSOR.register(UserCommand.class);
        COMMAND_PROCESSOR.register(RecentCommand.class);
        COMMAND_PROCESSOR.register(CalcCommand.class);
        COMMAND_PROCESSOR.register(TopCommand.class);
        COMMAND_PROCESSOR.register(MimicCommand.class);
        COMMAND_PROCESSOR.register(LinkCommand.class);
        jdaDiscord = new JDABuilder(config.discordBotToken).buildBlocking();
        jdaDiscord.setEventManager(new AnnotatedEventManager());
        jdaDiscord.addEventListener(new DiscordEventHandler()); // register events
        System.out.println("Connecting to osu!...");
        osuApi = Osu.getAPI(config.osuApplicationToken);
        System.out.println("Done!");
    }

}
