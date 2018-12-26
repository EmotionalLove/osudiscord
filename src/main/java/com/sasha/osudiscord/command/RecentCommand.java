package com.sasha.osudiscord.command;

import com.oopsjpeg.osu4j.OsuBeatmap;
import com.oopsjpeg.osu4j.OsuScore;
import com.oopsjpeg.osu4j.OsuUser;
import com.oopsjpeg.osu4j.backend.EndpointBeatmaps;
import com.oopsjpeg.osu4j.backend.EndpointScores;
import com.oopsjpeg.osu4j.backend.EndpointUserRecents;
import com.oopsjpeg.osu4j.backend.EndpointUsers;
import com.sasha.osudiscord.DiscordEventHandler;
import com.sasha.osudiscord.LinkManager;
import com.sasha.osudiscord.OsuDiscord;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

import java.util.List;

@SimpleCommandInfo(description = "Show the most recent beatmap you've played",
syntax = {"[username]"})
public class RecentCommand extends SimpleCommand {

    public RecentCommand() {
        super("recent");
    }

    @Override public void onCommand() {
        boolean flag = false;
        if (this.getArguments() == null || this.getArguments().length != 1) {
            if (LinkManager.hasLinkedAccount(DiscordEventHandler.lastMessage.getAuthor().getId())) {
                flag = true;
            }
            else {
                DiscordEventHandler.lastMessage.getChannel()
                        .sendMessage(DiscordEventHandler.Util.makeErrorEmbed("Invalid Args", "Too few or too many arguments", null))
                        .submit();
                return;
            }
        }
        String user = flag ? LinkManager.read(DiscordEventHandler.lastMessage.getAuthor().getId()) : this.getArguments()[0];
        try {
            EndpointUserRecents.ArgumentsBuilder recent = new EndpointUserRecents.ArgumentsBuilder(user);
            List<OsuScore> scores = OsuDiscord.INSTANCE.osuApi.userRecents.query(recent.build());
            if (scores.isEmpty()) {
                DiscordEventHandler.lastMessage.getChannel().sendMessage(DiscordEventHandler.Util.makeErrorEmbed("No Recents for " + getArguments()[0], "Nuthin here :/", null)).submit();
                return;
            }
            OsuScore score = scores.get(0);
            DiscordEventHandler.lastMessage.getChannel().sendMessage(DiscordEventHandler.Util.makeOsuScoreRecentEmbed(score)).submit();
        } catch (Exception e) {
            e.printStackTrace();
            DiscordEventHandler.lastMessage
                    .getChannel()
                    .sendMessage(DiscordEventHandler.Util
                            .makeErrorEmbed("Invalid User?", "An exception occurred whilst trying to retrieve this user... Is the username valid? Or maybe they've never played osu?", null)).submit();
        }
    }
}
