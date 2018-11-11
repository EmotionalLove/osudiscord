package com.sasha.osudiscord.command;

import com.sasha.osudiscord.DiscordEventHandler;
import com.sasha.simplecmdsys.SimpleCommand;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

public class MimicCommand extends SimpleCommand {

    public MimicCommand() {
        super("mimic");
    }

    @Override public void onCommand() {
        if (this.getArguments() == null || this.getArguments().length != 2) {
            DiscordEventHandler.lastMessage.getChannel().sendMessage(
                    DiscordEventHandler.Util.makeErrorEmbed("Invalid args", "Too few or too many arguments", null)
            ).submit();
            return;
        }
        try {
            Member member = DiscordEventHandler.lastMessage.getGuild().getMembersByName(this.getArguments()[0], true).get(0);
            DiscordEventHandler.lastMessage.getTextChannel().getWebhooks().queue(hooks -> {
                Webhook hook = DiscordEventHandler.lastMessage.getTextChannel().createWebhook("Mimi").complete();
                WebhookMessageBuilder b = new WebhookMessageBuilder();
                b.setUsername(member.getEffectiveName());
                b.setAvatarUrl(member.getUser().getAvatarUrl());
                b.setContent(this.getArguments()[1]);
                WebhookClient cli = hook.newClient().build();
                cli.send(b.build());
                cli.close();
            }, fail -> DiscordEventHandler.lastMessage.getChannel().sendMessage(
                    DiscordEventHandler.Util.makeErrorEmbed("No permission", "I can't mimic anyone because I don't have the \"Manage Webhook\" permission!", null)
            ).submit());
        } catch (InsufficientPermissionException e) {
            DiscordEventHandler.lastMessage.getChannel().sendMessage(
                    DiscordEventHandler.Util.makeErrorEmbed("No permission", "I can't mimic anyone because I don't have the \"Manage Webhook\" permission!", null)
            ).submit();
        } catch (Exception e) {
            e.printStackTrace();
            DiscordEventHandler.lastMessage.getChannel().sendMessage(
                    DiscordEventHandler.Util.makeErrorEmbed("Invalid user", "I can't mimic people that aren't in the server!", null)).submit();
        }
    }
}
