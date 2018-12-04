package com.sasha.osudiscord;

import com.github.francesco149.koohii.Koohii;
import com.oopsjpeg.osu4j.GameMod;
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

    private static int getColourCodeForRank(String rank) {
        switch (rank) {
            case "A":
                return 0x00ff11;
            case "B":
                return 0x9dff00;
            case "C":
                return 0xffe100;
            case "D":
                return 0xff6e00;
            case "F":
                return 0xff0000;
            default:
                return 0xff77f5;
        }
    }

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

        public static MessageEmbed makeOsuScoreEmbed(OsuScore score) throws OsuAPIException, MalformedURLException {
            EmbedBuilder builder = new EmbedBuilder();
            OsuUser user = score.getUser().get();
            OsuBeatmap map = score.getBeatmap().get();
            boolean comma = false;
            StringBuilder mods = new StringBuilder("**Mods**: ");
            for (GameMod enabledMod : score.getEnabledMods()) {
                if (!comma) {
                    mods.append(enabledMod.getName());
                    comma = true;
                    continue;
                }
                mods.append(", ").append(enabledMod.getName());
            }
            builder.setColor(getColourCodeForRank(score.getRank()));
            builder.setTitle(user.getUsername() + " - " + map.getTitle(), map.getURL().toString());
            builder.setDescription("**Difficulty** > " + map.getDifficulty() + "\n" +
                    "**Score** > " + score.getScore() + "\n" +
                    "**PP** > " + (score.getPp() == 0f ? "Not sure" : score.getPp()) + "\n" +
                    "**激's**: " + score.getGekis() + " | **喝's**: " + score.getKatus() + " | **300's**: " + score.getHit300() + " | **100's**: " + score.getHit100() + " | **50's**: " + score.getHit50() + " | **X's**: " + score.getMisses() + "\n" +
                    "**Rank for beatmap** > " + score.getRank() + "\n" +
                    "**Max Combo Ratio** > " + score.getMaxCombo() + ":" + map.getMaxCombo() + "\n" +
                    mods.toString());
            builder.setImage("https://assets.ppy.sh/beatmaps/{}/covers/cover.jpg".replace("{}", "" + map.getBeatmapSetID()));
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
            builder.setThumbnail("https://a.ppy.sh/" + usr.getID());
            return builder.build();
        }
        public static MessageEmbed makeGenericEmbed(String title, String desc) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(title);
            builder.setDescription(desc);
            return builder.build();
        }
    }

}
