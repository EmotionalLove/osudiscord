package com.sasha.osudiscord.command;

import com.sasha.osudiscord.DiscordEventHandler;
import com.sasha.osudiscord.PpCompute;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

import java.io.IOException;
@SimpleCommandInfo(description = "Calculate PP for a beatmap",
        syntax = {"<beatmap id> ['acc' <value>] ['spd' <value>]"})
public class CalcCommand extends SimpleCommand {

    public CalcCommand() {
        super("calc");
    }

    @Override public void onCommand() {
        double acc = -1.0;
        double spd = -1.0;
        if (this.getArguments() == null || this.getArguments().length != 1) {
            if (this.getArguments() == null) {
                return;
            }
            if (this.getArguments().length == 3) {
                int indexAcc = -1;
                int indexSpd = -1;
                for (int i = 0; i < this.getArguments().length; i++) {
                    if (this.getArguments()[i].equalsIgnoreCase("acc")) {
                        indexAcc = i;
                    }
                    if (this.getArguments()[i].equalsIgnoreCase("spd")) {
                        indexSpd = i;
                    }
                }
                if (indexAcc != -1) acc = Double.parseDouble(this.getArguments()[indexAcc + 1]) / 100;
                if (indexSpd != -1) spd = Double.parseDouble(this.getArguments()[indexSpd + 1]) / 100;
            }
        }
        double finalAcc = acc;
        double finalSpd = spd;
        DiscordEventHandler.lastMessage.getChannel().sendMessage(DiscordEventHandler.Util.makeGenericEmbed("Please wait...", "Downloading beatmap...")).queue(q -> {
            try {
                PpCompute compute = new PpCompute(this.getArguments()[0], finalAcc, finalSpd);
                double ppValue = compute.calc();
                q.editMessage(DiscordEventHandler.Util.makeGenericEmbed("PP on " + compute.getMap().title, ppValue + "pp" + (finalAcc == -1.0 ? " @ FC/SS" : " @ " + finalAcc * 100 + "% accuracy"))).submit();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException x) {
                q.editMessage(DiscordEventHandler.Util.makeErrorEmbed("Invalid beatmap download",
                        "Invalid beatmap download. You can only calculate PP for beatmaps hosted on the official osu website.", null)).submit();
            }
        });
    }
}