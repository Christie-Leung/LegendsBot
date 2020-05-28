package Commands.ReactionRoles;

public interface ReactionRole {

    long getMsgID();

    String getEmoji();

    long getRoleID();

    void setMsgID(long msgID);

    void setEmoji(String emoji);

    void setRoleID(long roleID);

}
