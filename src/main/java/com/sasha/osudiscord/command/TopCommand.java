package com.sasha.osudiscord.command;

import com.oopsjpeg.osu4j.OsuScore;
import com.oopsjpeg.osu4j.OsuUser;
import com.oopsjpeg.osu4j.backend.EndpointUserRecents;
import com.oopsjpeg.osu4j.backend.EndpointUsers;
import com.sasha.osudiscord.DiscordEventHandler;
import com.sasha.osudiscord.OsuDiscord;
import com.sasha.simplecmdsys.SimpleCommand;

public class TopCommand extends SimpleCommand {

    public TopCommand() {
        super("top");
    }

    @Override public void onCommand() {
        if (this.getArguments() == null || this.getArguments().length != 1) {
            DiscordEventHandler.lastMessage.getChannel()
                    .sendMessage(DiscordEventHandler.Util.makeErrorEmbed("Invalid Args", "Too few or too many arguments", null))
                    .submit();
            return;
        }
        String user = this.getArguments()[0];
        try {
            EndpointUsers.Arguments args = new EndpointUsers.ArgumentsBuilder(user).build();
            OsuUser osus = OsuDiscord.INSTANCE.osuApi.users.query(args);
            DiscordEventHandler.lastMessage.getChannel().sendMessage(DiscordEventHandler.Util.makeOsuScoreEmbed(osus.getTopScores(1).get().get(0))).submit();
        } catch (Exception e) {
            e.printStackTrace();
            DiscordEventHandler.lastMessage
                    .getChannel()
                    .sendMessage(DiscordEventHandler.Util
                            .makeErrorEmbed("Invalid User?", "An exception occurred whilst trying to retrieve this user... Is the username valid? Or maybe they've never played osu?", null)).submit();
        }
    }
}
