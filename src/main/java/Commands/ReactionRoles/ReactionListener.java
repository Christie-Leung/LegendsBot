package Commands.ReactionRoles;

import emoji4j.EmojiUtils;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.Objects;

public class ReactionListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
         if (!Objects.requireNonNull(e.getMember()).getUser().isBot()) {
             String emojiName = e.getReactionEmote().getEmoji();
             emojiName = EmojiUtils.getEmoji(emojiName).getDecimalHtml();

             if(parseThruReactionRoles(e.getMessageIdLong()) != null) {
                 ReactionRole rr = ReactionRolesSql.getFromEmoji(EmojiUtils.getEmoji(emojiName).getDecimalHtml(), e.getMessageIdLong());
                 if (rr.getEmoji() != null) {
                     if(rr.getEmoji().contains(emojiName) || rr.getEmoji().equalsIgnoreCase(emojiName)) {
                         for (Role r : e.getGuild().getRoles()) {
                             if(rr.getRoleID() == r.getIdLong()) {
                                 e.getGuild().addRoleToMember(e.getUserId(), r).queue();
                             }
                         }
                     }
                 }
             }
         }
    }

     List<RolesAndEmojis> parseThruReactionRoles(long msgID) {
         return RemoveReactionRoleListener.getRolesAndEmojis(msgID);
     }
}
