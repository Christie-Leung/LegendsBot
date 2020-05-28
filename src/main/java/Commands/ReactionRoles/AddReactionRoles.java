package Commands.ReactionRoles;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.vdurmont.emoji.EmojiManager;
import emoji4j.Emoji;
import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AddReactionRoles extends Command {

    public final EventWaiter waiter;

    public AddReactionRoles(EventWaiter waiter) {
        this.name = "rr";
        this.waiter = waiter;
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent e) {
        String[] s = e.getMessage().getContentRaw().split("\\s+");
        if(s.length == 3) {
            if(s[1].equalsIgnoreCase("list")) {
                List<ReactionRole> rrList = ReactionRolesSql.reactionRolesInMsg(Long.parseLong(s[2]));
                if (rrList.size() > 0) {
                    EmbedBuilder eb = new EmbedBuilder().setColor(Color.CYAN)
                            .appendDescription("All Roles in Msg");
                    for (ReactionRole rr : rrList) {
                        Role r = getRole(rr.getRoleID(), e);
                        String roleId = r.getName();
                        eb.addField(roleId, EmojiUtils.getEmoji(rr.getEmoji()).getEmoji(), true);
                    }
                    e.reply(eb.build());
                } else {
                    e.replySuccess("**There are no reaction roles in this msg!**");
                }
            } else if (s[1].equalsIgnoreCase("delete")) {
                ReactionRolesSql.delete(Long.parseLong(s[2]));
            } else {
                e.replyError("You bad!");
            }
        } else {
            e.reply("Please reply with the message channel and message id");
            waiter.waitForEvent(MessageReceivedEvent.class,
                    event -> event.getChannel().equals(e.getChannel())
                            && event.getAuthor().equals(e.getAuthor())
                            && !event.getMessage().equals(e.getMessage()),
                    event -> {
                        String[] items = event.getMessage().getContentRaw().split("\\s+");
                        TextChannel messageChannel = e.getGuild().getTextChannelById(items[0].substring(2, items[0].length() - 1));
                        String messageId = items[1];
                        assert messageChannel != null;
                        String msg = messageChannel.retrieveMessageById(messageId).complete().getContentRaw();
                        System.out.println(msg);

                        if(ReactionRolesSql.reactionRolesInMsg(Long.parseLong(items[1])).size() == 0) {
                            if(EmojiManager.containsEmoji(msg) || (msg.contains("<:") && msg.contains(">"))) {
                                String[] emojisAndRoles = msg.split("\\s+");
                                ArrayList<String> emojiList = new ArrayList<>();
                                ArrayList<String> roleList = new ArrayList<>();
                                boolean roleName = false;
                                StringBuilder stringBuilder = new StringBuilder();

                                for (String emojisAndRole : emojisAndRoles) {
                                    Emoji emoji = EmojiUtils.getEmoji(emojisAndRole);
                                    if(emoji != null) {
                                        emojiList.add(emoji.getEmoji());
                                        if(roleName) {
                                            String role = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
                                            roleList.add(role);
                                            stringBuilder.delete(0, stringBuilder.length());
                                        }
                                        roleName = false;
                                    } else if(emojisAndRole.startsWith("<:")) {
                                        emojiList.add(emojisAndRole.substring(2, emojisAndRole.length() - 1));
                                        if(roleName) {
                                            String role = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
                                            roleList.add(role);
                                            stringBuilder.delete(0, stringBuilder.length());
                                        }
                                        roleName = false;
                                    } else if(roleName && !emojisAndRole.equals("-")) {
                                        stringBuilder.append(emojisAndRole).append(" ");
                                    } else if(emojisAndRole.equals("-")) {
                                        roleName = true;
                                    }
                                }
                                if(!stringBuilder.toString().isEmpty()) {
                                    String role = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
                                    roleList.add(role);
                                }
                                if(roleList.size() > 0) {
                                    for (String role : roleList) {
                                        if(!role.isEmpty() && !checkRoleExist(role, e)) {
                                            e.getGuild().createRole().setName(role).queue();
                                        }
                                    }
                                }
                                if(emojiList.size() > 0) {
                                    for (String unicode : emojiList) {
                                        messageChannel.retrieveMessageById(messageId).queue(message ->
                                                message.addReaction(unicode).queue()
                                        );
                                    }
                                } else {
                                    e.replyError("Didnt werk you stoopid");
                                }
                                try {
                                    TimeUnit.SECONDS.sleep(5);
                                } catch(InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                                for (int x = 0; x < roleList.size(); x++) {
                                    Role r = getRole(roleList.get(x), e);
                                    if(r != null) {
                                        ReactionRole reactionRole = new ReactionRoleImpl();
                                        reactionRole.setMsgID(Long.parseLong(items[1]));
                                        reactionRole.setEmoji(EmojiUtils.getEmoji(emojiList.get(x)).getDecimalHtml());
                                        reactionRole.setRoleID(r.getIdLong());
                                        ReactionRolesSql.add(reactionRole);
                                        e.replySuccess("Added " + roleList.get(x));
                                    }

                                }
                            } else {
                                e.replyError("no bad");
                            }
                        } else {
                            e.replyError("This msg already have reaction roles!");
                        }
                    },
                    // if the user takes more than three minute, time out
                    3, TimeUnit.MINUTES, () -> e.reply("Sorry, you took too long."));

        }
    }

    Role getRole(String roleName, CommandEvent e) {
        for (Role r : Objects.requireNonNull(e.getJDA().getGuildById(e.getGuild().getId())).getRoles()) {
            if (r.getName().equalsIgnoreCase(roleName)) {
                return r;
            }
        }
        return null;
    }

    Role getRole(long roleID, CommandEvent e) {
        for (Role r : e.getGuild().getRoles()) {
            if (r.getIdLong() == roleID) {
                return r;
            }
        }
        return null;
    }

    boolean checkRoleExist(String rolename, CommandEvent e) {
        for (Role r: e.getGuild().getRoles()) {
            if (r.getName().equalsIgnoreCase(rolename)) {
                return true;
            }
        }
        return false;
    }
}
