package Commands.ReactionRoles;

public class RolesAndEmojis{
    long msgID;
    String emoji;
    long roleID;

    public RolesAndEmojis(long msgID, String emoji, long roleID) {
        this.msgID = msgID;
        this.emoji = emoji;
        this.roleID = roleID;
    }

    public long getRoleID() {
        return roleID;
    }

    public String getEmoji() {
        return emoji;
    }

    public long getMsgID() {
        return msgID;
    }
}