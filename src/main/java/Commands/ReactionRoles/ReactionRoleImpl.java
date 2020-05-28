package Commands.ReactionRoles;

public class ReactionRoleImpl implements ReactionRole {
    private long msgID;
    private String emoji;
    private long roleID;

    public ReactionRoleImpl() {
    }


    @Override
    public long getMsgID() {
        return msgID;
    }

    @Override
    public String getEmoji() {
        return emoji;
    }

    @Override
    public long getRoleID() {
        return roleID;
    }

    @Override
    public void setRoleID(long roleID) {
        this.roleID = roleID;
    }

    @Override
    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    @Override
    public void setMsgID(long msgID) {
        this.msgID = msgID;
    }
}
