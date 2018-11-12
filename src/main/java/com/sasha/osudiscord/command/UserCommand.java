package com.sasha.osudiscord.command;

import com.oopsjpeg.osu4j.OsuUser;
import com.oopsjpeg.osu4j.backend.EndpointUsers;
import com.sasha.osudiscord.DiscordEventHandler;
import com.sasha.osudiscord.OsuDiscord;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

@SimpleCommandInfo(description = "Show someone's osu! profile",
syntax = {"<username>"})
public class UserCommand extends SimpleCommand {

    public UserCommand() {
        super("user");
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
            OsuUser osuUser = OsuDiscord.INSTANCE.osuApi.users.query(args);
            DiscordEventHandler.lastMessage.getChannel().sendMessage(DiscordEventHandler.Util.makeOsuUserEmbed(osuUser)).submit();
        } catch (Exception e) {
            DiscordEventHandler.lastMessage
                    .getChannel()
                    .sendMessage(DiscordEventHandler.Util
                            .makeErrorEmbed("Invalid User?", "An exception occurred whilst trying to retrieve this user... Is the username valid?", null)).submit();
        }
    }
}
