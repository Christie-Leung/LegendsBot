import Commands.ReactionRoles.AddReactionRoles;
import Commands.ReactionRoles.ReactionListener;
import Commands.ReactionRoles.ReactionRolesSql;
import Commands.ReactionRoles.RemoveReactionRoleListener;
import Commands.Utilities.DeleteLineCmd;
import Commands.RoleCmd;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;


public class LegendsBot extends ListenerAdapter {
    public static void main(String[] args) throws LoginException {
        EventWaiter waiter = new EventWaiter();
        ReactionRolesSql.getConn();
        ReactionListener reactionListener = new ReactionListener();
        RemoveReactionRoleListener removeReactionRoleListener = new RemoveReactionRoleListener();

        CommandClientBuilder client = new CommandClientBuilder();
        client.setPrefix("!")
                .setOwnerId(Private.ownerId)
                .useHelpBuilder(false)
                .setActivity(Activity.playing("Discord"))
                .addCommands(
                        new AddReactionRoles(waiter),
                        new RoleCmd(waiter),
                        new DeleteLineCmd()
                );

        new JDABuilder(Private.botToken)
                .addEventListeners(
                        client.build(),
                        reactionListener,
                        removeReactionRoleListener,
                        waiter)
                .build();
    }
}
