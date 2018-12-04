package com.sasha.osudiscord.command;

import com.sasha.osudiscord.DiscordEventHandler;
import com.sasha.osudiscord.OsuDiscord;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

import static com.sasha.osudiscord.OsuDiscord.INSTANCE;

@SimpleCommandInfo(description = "Show this message", syntax = {""})
public class HelpCommand extends SimpleCommand {

    public HelpCommand() {
        super("help");
    }

    @Override
    public void onCommand() {
        EmbedBuilder b = new EmbedBuilder();
        b.setColor(Color.LIGHT_GRAY);
        b.setTitle("Help");
        StringBuilder sb = new StringBuilder();
        INSTANCE.COMMAND_PROCESSOR.getCommandRegistry().forEach((clazz, cmdObj) -> {
            SimpleCommand cmd = (SimpleCommand) cmdObj;
            sb.append("\n**\\" + cmd.getCommandName() + "** - " + INSTANCE.COMMAND_PROCESSOR.getDescription(clazz) + "\n(syntax:");
            for (String argument : INSTANCE.COMMAND_PROCESSOR.getSyntax(clazz)) {
                sb.append(" `\\" + cmd.getCommandName() + " " + argument + "`");
            }
            sb.append(")");
        });

        b.setDescription(sb);
        DiscordEventHandler.lastMessage.getChannel().sendMessage(b.build()).submit();
    }
}
