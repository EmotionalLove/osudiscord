package com.sasha.osudiscord;

import com.github.francesco149.koohii.Koohii;
import com.oopsjpeg.osu4j.*;
import com.oopsjpeg.osu4j.backend.EndpointBeatmapSet;
import com.oopsjpeg.osu4j.exception.OsuAPIException;
import lt.ekgame.beatmap_analyzer.beatmap.Beatmap;
import lt.ekgame.beatmap_analyzer.parser.BeatmapException;
import lt.ekgame.beatmap_analyzer.parser.BeatmapParser;
import lt.ekgame.beatmap_analyzer.performance.Performance;
import lt.ekgame.beatmap_analyzer.performance.scores.Score;
import lt.ekgame.beatmap_analyzer.utils.Mods;
import lt.ekgame.beatmap_analyzer.utils.ScoreVersion;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

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
        for (String s : e.getMessage().getContentDisplay().split(" ")) {
            if (s.matches("https://osu\\.ppy\\.sh/s/.*")) {
                int id = Integer.parseInt(s.replace("https://osu.ppy.sh/s/", ""));
                System.out.println(id);
                EndpointBeatmapSet.Arguments args = new EndpointBeatmapSet.Arguments(id);
                try {
                    OsuBeatmapSet set = OsuDiscord.INSTANCE.osuApi.beatmapSets.query(args);
                    if (set == null) return;
                    e.getChannel().sendMessage(Util.makeOsuBeatmapSetEmbed(set)).submit();
                } catch (OsuAPIException | MalformedURLException e1) {
                    e1.printStackTrace();
                }
            }
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

        public static float getMapCompletionPercent(int numObjectsTotalCurrent, int totalObjectsTotal) {
            System.out.println(numObjectsTotalCurrent + " / " + totalObjectsTotal);
            return (((float) numObjectsTotalCurrent / (float) totalObjectsTotal) * 100f);
        }

        public static MessageEmbed makeOsuBeatmapSetEmbed(OsuBeatmapSet set) throws MalformedURLException {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(set.getTitle(), set.getURL().toString());
            builder.setImage("https://assets.ppy.sh/beatmaps/{}/covers/cover.jpg".replace("{}", "" + set.getBeatmapSetID()));
            builder.addField("Artist", set.getArtist(), true);
            builder.addField("Mapper", set.getCreatorName(), true);
            builder.addField("Download beatmap!", "(Download .osz)[http://osu.ppy.sh/d/" + set.getBeatmapSetID() + "] \\((No video?)[http://osu.ppy.sh/d/" + set.getBeatmapSetID() + "n])", true);
            return builder.build();
        }

        @Deprecated
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
                    "**Mark for beatmap** > " + score.getRank() + "\n" +
                    "**Max Combo Ratio** > " + score.getMaxCombo() + ":" + map.getMaxCombo() + "\n" +
                    (mods.toString().equals("**Mods**: ") ? "No-mod" : mods.toString()));
            builder.setImage("https://assets.ppy.sh/beatmaps/{}/covers/cover.jpg".replace("{}", "" + map.getBeatmapSetID()));
            return builder.build();
        }

        public static MessageEmbed makeOsuScoreRecentEmbed(OsuScore score) throws IOException, BeatmapException {
            EmbedBuilder builder = new EmbedBuilder();
            OsuUser user = score.getUser().get();
            OsuBeatmap map = score.getBeatmap().get();
            Beatmap smap = getPhysicalBeatmap(map);
            Performance performance = calc(score, smap);
            Performance fcperformance = calcFc(score, smap);
            float progress = getMapCompletionPercent(score.getTotalHits(), smap.getObjectCount());
            boolean comma = false;
            StringBuilder mods = new StringBuilder();
            for (GameMod enabledMod : score.getEnabledMods()) {
                if (!comma) {
                    mods.append(enabledMod.getName());
                    comma = true;
                    continue;
                }
                mods.append(", ").append(enabledMod.getName());
            }
            //
            builder.setColor(getColourCodeForRank(score.getRank()));
            builder.setImage("https://assets.ppy.sh/beatmaps/{}/covers/cover.jpg".replace("{}", "" + map.getBeatmapSetID()));
            builder.setTitle(user.getUsername() + " - " + map.getTitle(), map.getURL().toString());
            builder.addField("Difficulty", map.getDifficulty() + " stars", true);
            builder.addField("Score", score.getScore() + "", true);
            builder.addField("Accuracy", Math.dround(performance.getAccuracy() * 100, 3) + "%", true);
            builder.addField("PP", (score.getPp() == 0f ? Math.dround(performance.getPerformance(), 3) : Math.dround(score.getPp(), 3)) + " pp (" + Math.dround(fcperformance.getPerformance(), 3) + " pp for FC)", true);
            builder.addField("Grade", score.getRank(), true);
            builder.addField("Combo", score.getMaxCombo() + "x (" + map.getMaxCombo() + "x for FC)", true);
            builder.setDescription("**激's**: " + score.getGekis() + " | **喝's**: " + score.getKatus() + " | **300's**: " + score.getHit300() + " | **100's**: " + score.getHit100() + " | **50's**: " + score.getHit50() + " | **X's**: " + score.getMisses());
            if (progress <= 90f && score.getRank().equalsIgnoreCase("F")) builder.addField("Completion", progress + "%", true);
            if (!mods.toString().equals("")) builder.addField("Mods", mods.toString(), true);
            //
            return builder.build();
        }

        public static MessageEmbed makeOsuUserEmbed(OsuUser usr) throws MalformedURLException {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(usr.getUsername(), usr.getURL().toString());
            builder.addField("Accuracy", usr.getAccuracy() + "%", true);
            builder.addField("Level", usr.getPP() + " pp", true);
            builder.addField("Rank", usr.getRank() + "", true);
            builder.addField("Country", usr.getCountry().getName() + " :flag_" + usr.getCountry().getAlpha2().toLowerCase() + ":", true);
            builder.setThumbnail("https://a.ppy.sh/" + usr.getID());
            return builder.build();
        }

        public static MessageEmbed makeGenericEmbed(String title, String desc) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(title);
            builder.setDescription(desc);
            return builder.build();
        }

        public static Beatmap getPhysicalBeatmap(OsuBeatmap map) throws IOException, BeatmapException {
            File f = new File(map.getTitle() + ".osz");
            FileUtils.copyURLToFile(new URL("https://osu.ppy.sh/osu/" + map.getID()), f);
            return new BeatmapParser().parse(f);
        }

        public static Koohii.Map getPhysicalBeatmapKoohii(OsuBeatmap map) throws IOException {
            File f = new File(map.getTitle() + ".osz");
            FileUtils.copyURLToFile(new URL("https://osu.ppy.sh/osu/" + map.getID()), f);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            return new Koohii.Parser().map(reader);
        }


        public static Performance calc(OsuScore score, OsuBeatmap map) throws IOException, BeatmapException {
            int mods = 0;
            for (GameMod enabledMod : score.getEnabledMods()) {
                mods |= ((int) enabledMod.getBit());
            }
            File f = new File(map.getTitle() + ".osz");
            FileUtils.copyURLToFile(new URL("https://osu.ppy.sh/osu/" + map.getID()), f);
            Beatmap physicalMap = new BeatmapParser().parse(f);
            Score s = Score.of(physicalMap)
                    .combo(score.getMaxCombo())
                    .score(score.getScore())
                    .osuAccuracy(score.getHit100(), score.getHit50(), score.getMisses())
                    .version(ScoreVersion.V1)
                    .build();
            return physicalMap.getDifficulty(Mods.parse(mods)).getPerformance(s);
        }

        public static Performance calc(OsuScore score, Beatmap map) {
            int mods = 0;
            for (GameMod enabledMod : score.getEnabledMods()) {
                mods |= ((int) enabledMod.getBit());
            }
            Score s = Score.of(map)
                    .combo(score.getMaxCombo())
                    .score(score.getScore())
                    .osuAccuracy(score.getHit100(), score.getHit50(), score.getMisses())
                    .version(ScoreVersion.V1)
                    .build();
            return map.getDifficulty(Mods.parse(mods)).getPerformance(s);
        }

        public static Performance calcFc(OsuScore score, Beatmap map) {
            int mods = 0;
            for (GameMod enabledMod : score.getEnabledMods()) {
                mods |= ((int) enabledMod.getBit());
            }
            Score s = Score.of(map)
                    .combo(map.getMaxCombo())
                    .score(score.getScore())
                    .version(ScoreVersion.V1)
                    .build();
            return map.getDifficulty(Mods.parse(mods)).getPerformance(s);
        }

    }

}