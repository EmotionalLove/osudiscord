package com.sasha.osudiscord;

import com.oopsjpeg.osu4j.OsuBeatmap;
import com.oopsjpeg.osu4j.OsuScore;
import com.oopsjpeg.osu4j.OsuUser;
import com.oopsjpeg.osu4j.exception.OsuAPIException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import javax.annotation.Nullable;
import java.awt.*;
import java.net.MalformedURLException;

public class DiscordEventHandler {

    public static Message lastMessage;

    @SubscribeEvent
    public void onGuildMsgRx(GuildMessageReceivedEvent e) {
        if (e.getMessage().getContentDisplay().startsWith("\\")) {
            e.getChannel().sendTyping().complete();
            lastMessage = e.getMessage();
            OsuDiscord.INSTANCE.COMMAND_PROCESSOR.processCommand(e.getMessage().getContentDisplay());
        }
    }

    public static class Util {

        public static MessageEmbed makeErrorEmbed(String title, String message, @Nullable String imgUrl) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Error - " + title);
            builder.setDescription(message);
            if (imgUrl != null) {
                builder.setThumbnail(imgUrl);
            }
            builder.setColor(Color.RED);
            return builder.build();
        }

        public static MessageEmbed makeOsuScoreEmbed(OsuScore score) throws OsuAPIException {
            EmbedBuilder builder = new EmbedBuilder();
            OsuUser user = score.getUser().get();
            OsuBeatmap map = score.getBeatmap().get();
            builder.setTitle(user.getUsername() + " - " + map.getTitle());
            builder.setDescription("**Difficulty** > " + map.getDifficulty() + "\n" +
                    "**PP** > " + score.getPp());
            return builder.build();
        }
        public static MessageEmbed makeOsuUserEmbed(OsuUser usr) throws MalformedURLException {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(usr.getUsername(), usr.getURL().toString());
            builder.setDescription("**Accuracy** > " + usr.getAccuracy() + "\n" +
                    "**PP** > " + usr.getPP() + "\n" +
                    "**Level** > " + usr.getLevel() + "\n" +
                    "**Rank** > " + usr.getRank() + "\n" +
                    "**Country** > " + usr.getCountry().getName() + " :flag_" + usr.getCountry().getAlpha2().toLowerCase() + ":");
            builder.setThumbnail("https://osu.ppy.sh/images/layout/avatar-guest.png");
            return builder.build();
        }

    }

}
