package com.sasha.osudiscord.command;

import com.sasha.osudiscord.DiscordEventHandler;
import com.sasha.osudiscord.LinkManager;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;
import net.dv8tion.jda.core.EmbedBuilder;

import java.io.IOException;

@SimpleCommandInfo(description = "Link your osu! account to the bot", syntax = {"<username>"})
public class LinkCommand extends SimpleCommand {

    public LinkCommand() {
        super("link");
    }

    @Override public void onCommand() {
        if (this.getArguments() == null || this.getArguments().length != 1) {
            DiscordEventHandler.lastMessage.getChannel().sendMessage(
                    DiscordEventHandler.Util.makeErrorEmbed("Invalid args", "Too few or too many arguments", null)
            ).submit();
            return;
        }
        try {
            LinkManager.link(DiscordEventHandler.lastMessage.getAuthor().getId(), this.getArguments()[0]);
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Linked!");
            builder.setDescription("Your osu! account has been linked.");
            builder.setColor(0xff7cc0);
            DiscordEventHandler.lastMessage.getChannel().sendMessage(builder.build()).submit();
        } catch (IOException e) {
            DiscordEventHandler.lastMessage.getChannel().sendMessage(
                    DiscordEventHandler.Util.makeErrorEmbed("Internal server error", "An internal error occurred whilst handling your request. Please try again later.", null)
            ).submit();
            e.printStackTrace();
        }
    }
}
