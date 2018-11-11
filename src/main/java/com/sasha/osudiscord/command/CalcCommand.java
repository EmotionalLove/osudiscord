package com.sasha.osudiscord.command;

import com.github.francesco149.koohii.Koohii;
import com.sasha.osudiscord.DiscordEventHandler;
import com.sasha.simplecmdsys.SimpleCommand;

public class CalcCommand extends SimpleCommand {

    public CalcCommand() {
        super("calc");
    }

    @Override public void onCommand() {
        if (DiscordEventHandler.lastMessage.getAttachments() == null ||
        DiscordEventHandler.lastMessage.getAttachments().isEmpty()) {
            DiscordEventHandler.lastMessage.getChannel().sendMessage(
                    DiscordEventHandler.Util.makeErrorEmbed("No beatmap", "You need to upload a beatmap", null)
            ).submit();
            return;
        }
        DiscordEventHandler.lastMessage.getChannel().sendMessage("This is WIP...").submit();
        //todo
        //new Koohii.Parser().map()
    }
}
