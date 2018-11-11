package com.sasha.osudiscord.command;

import com.oopsjpeg.osu4j.OsuScore;
import com.oopsjpeg.osu4j.OsuUser;
import com.oopsjpeg.osu4j.backend.EndpointUserRecents;
import com.oopsjpeg.osu4j.backend.EndpointUsers;
import com.sasha.osudiscord.DiscordEventHandler;
import com.sasha.osudiscord.LinkManager;
import com.sasha.osudiscord.OsuDiscord;
import com.sasha.simplecmdsys.SimpleCommand;

import java.util.List;

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
            EndpointUserRecents.Arguments args = new EndpointUserRecents.ArgumentsBuilder(user).setUserName(user).setLimit(2).build();
            OsuScore osus = OsuDiscord.INSTANCE.osuApi.userRecents.query(args).get(0);
            DiscordEventHandler.lastMessage.getChannel().sendMessage(DiscordEventHandler.Util.makeOsuScoreEmbed(osus)).submit();
        } catch (Exception e) {
            e.printStackTrace();
            DiscordEventHandler.lastMessage
                    .getChannel()
                    .sendMessage(DiscordEventHandler.Util
                            .makeErrorEmbed("Invalid User?", "An exception occurred whilst trying to retrieve this user... Is the username valid? Or maybe they've never played osu?", null)).submit();
        }
    }
}
