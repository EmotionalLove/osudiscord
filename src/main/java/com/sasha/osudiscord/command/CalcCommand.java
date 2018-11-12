package com.sasha.osudiscord.command;

import com.sasha.osudiscord.DiscordEventHandler;
import com.sasha.osudiscord.PpCompute;
import com.sasha.simplecmdsys.SimpleCommand;

import java.io.IOException;

public class CalcCommand extends SimpleCommand {

    public CalcCommand() {
        super("calc");
    }

    @Override public void onCommand() {
        if (this.getArguments() == null || this.getArguments().length != 1) {
            return;
        }
        DiscordEventHandler.lastMessage.getChannel().sendMessage(DiscordEventHandler.Util.makeGenericEmbed("Please wait...", "Downloading beatmap...")).queue(q -> {
            try {
                PpCompute compute = new PpCompute(this.getArguments()[0]);
                double ppValue = compute.calc();
                q.editMessage(DiscordEventHandler.Util.makeGenericEmbed("PP for FC on " + compute.getMap().title, ppValue + "pp")).submit();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException x) {
                q.editMessage(DiscordEventHandler.Util.makeErrorEmbed("Invalid beatmap download",
                        "Invalid beatmap download. You can only calculate PP for beatmaps hosted on the official osu website.", null)).submit();
            }
        });
    }
}