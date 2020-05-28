package Commands.ReactionRoles;

import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RemoveReactionRoleListener extends ListenerAdapter {

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
        if(parseThruReactionRoles(e.getMessageIdLong()).size() > 0) {
            String emojiName = e.getReactionEmote().getEmoji();
            emojiName = EmojiUtils.getEmoji(emojiName).getDecimalHtml();
            ReactionRole rr = ReactionRolesSql.getFromEmoji(EmojiUtils.getEmoji(emojiName).getDecimalHtml(), e.getMessageIdLong());
            if (rr.getEmoji() != null) {
                if(rr.getEmoji().contains(emojiName) || rr.getEmoji().equalsIgnoreCase(emojiName)) {
                    for (Role r : e.getGuild().getRoles()) {
                        if(rr.getRoleID() == r.getIdLong()) {
                            e.getGuild().removeRoleFromMember(e.getUserId(), r).queue();
                        }
                    }
                }
            }
        }

    }

    List<RolesAndEmojis> parseThruReactionRoles(long msgID) {
        return getRolesAndEmojis(msgID);
    }

    @NotNull
        static List<RolesAndEmojis> getRolesAndEmojis(long msgID) {
            List<RolesAndEmojis> reactionRoles = new ArrayList<>();

            List<ReactionRole> rr = ReactionRolesSql.reactionRolesInMsg(msgID);

        for (ReactionRole tempRR : rr) {
            if(tempRR.getMsgID() == msgID) {
                String emoji = tempRR.getEmoji();
                long roleID = tempRR.getRoleID();
                long tempRRMsgID = tempRR.getMsgID();

                RolesAndEmojis r = new RolesAndEmojis(tempRRMsgID, emoji, roleID);
                reactionRoles.add(r);
            }
        }
        return reactionRoles;
    }
}
