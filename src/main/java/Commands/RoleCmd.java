package Commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.util.List;

public class RoleCmd extends Command {

    private final Paginator.Builder pBuilder;

    public RoleCmd(EventWaiter waiter) {
        this.name = "role";
        this.help = "deals with roles";
        this.arguments = "create | add | list | remove | edit";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MANAGE_ROLES};
        this.pBuilder = new Paginator.Builder()
                .waitOnSinglePage(true)
                .setFinalAction(m -> m.clearReactions().queue(v -> {}, v -> {}))
                .setItemsPerPage(20)
                .setEventWaiter(waiter);
    }

    @Override
    protected void execute(CommandEvent event)  {
        Guild guild = event.getGuild();

        String[] items = event.getArgs().split("\\s+");

        if(items.length > 0) {
            if(items[0].contains("create")) {
                String roleName = event.getArgs().substring(7);
                guild.createRole().setName(roleName).queue();
                event.getChannel().sendMessage("Successfully created " + roleName + " role!").queue();
            } else if(items[0].contains("perm")) {
                String roleName = event.getArgs().substring(4);
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Role Perms: ").setColor(Color.green);
                for (Role r : guild.getRoles()) {
                    if(r.getName().equalsIgnoreCase(roleName) || r.getName().contains(roleName)) {
                        for (Permission perm : Permission.values()) {
                            if(r.hasPermission(perm)) {
                                eb.appendDescription("**" + perm.getName() + ": true**\n");
                            } else {
                                eb.appendDescription(perm.getName() + ": false\n");
                            }
                        }
                    }
                }
                event.getChannel().sendMessage(eb.build()).queue();
            } else if(items[0].contains("add")) {
                String roleName = event.getArgs().substring(3);
                if(items.length >= 3) {
                    for (Role r : guild.getRoles()) {
                        if(r.getName().equalsIgnoreCase(roleName) || r.getName().contains(roleName)) {
                            List<Member> memberList = event.getMessage().getMentionedMembers();
                            for (Member member : memberList) {
                                boolean check = checkPlayersRole(member, r.getName());
                                if(!check) {
                                    event.getGuild().addRoleToMember(member, r).queue();
                                    event.getChannel().sendMessage("Successfully added " + member.getEffectiveName() + " to " + r.getName()).queue();
                                } else {
                                    event.getChannel().sendMessage(member.getEffectiveName() + " already has this role! ").queue();
                                }
                                break;
                            }

                        }
                    }
                } else {
                    event.getChannel().sendMessage("Do !role add [roleName] [user]").queue();
                }
            } else if (items[0].contains("delete")) {
                String roleName = items[1];
                for (Role r : guild.getRoles()) {
                    if(r.getName().equalsIgnoreCase(roleName) || r.getName().contains(roleName)) {
                        r.delete().queue();
                        event.replySuccess("Deleted!");
                    }
                }
            } else if(items[0].contains("remove")) {
                String roleName = event.getArgs().substring(6);
                if(items.length >= 3) {
                    for (Role r : guild.getRoles()) {
                        if(r.getName().equalsIgnoreCase(roleName) || r.getName().contains(roleName)) {
                            List<Member> memberList = event.getMessage().getMentionedMembers();
                            for (Member member : memberList) {
                                boolean check = checkPlayersRole(member, r.getName());
                                if(check) {
                                    event.getGuild().removeRoleFromMember(member, r).queue();
                                    event.getChannel().sendMessage("Successfully removed " + member.getEffectiveName() + " from " + r.getName()).queue();
                                } else {
                                    event.getChannel().sendMessage(member.getEffectiveName() + " does not have this role! ").queue();
                                }
                            }
                            break;
                        }
                    }
                } else {
                    event.getChannel().sendMessage("Do !role remove [roleName] [user]").queue();
                }
            } else if(items[0].contains("list")) {
                if(items.length == 1) {
                    event.reply("**List of Roles:**", message -> {
                        pBuilder.clearItems();
                        pBuilder.setColor(event.getMember().getColor());
                        for (Role role : guild.getRoles()) {
                            if(!role.getName().contains("everyone")) {
                                int userWithRole = 0;
                                for (Member member : guild.getMembers()) {
                                    if(member.getRoles().contains(role)) {
                                        userWithRole += 1;
                                    }
                                }
                                pBuilder.showPageNumbers(true);
                                pBuilder.addItems(role.getName() + ": " + userWithRole + " users");
                                pBuilder.setText("**List of Roles:**");
                            }
                        }
                        pBuilder.build().display(message);
                    });
                } else if(items.length == 2) {
                    EmbedBuilder eb = new EmbedBuilder();
                    Member m = event.getMessage().getMentionedMembers().get(0);
                    eb.setTitle(m.getEffectiveName() + "'s Roles").setColor(Color.green);
                    for (Role role : m.getRoles()) {
                        eb.appendDescription(role.getName() + "\n");
                    }
                    event.getChannel().sendMessage(eb.build()).queue();
                } else {
                    event.getChannel().sendMessage("Do create | edit | delete then say the role name of which you want to change").queue();
                }
            }
        }
    }

    boolean checkPlayersRole(Member m, String role) {
        boolean check = false;
        List<Role> roleList = m.getRoles();
        for (Role r : roleList) {
            if(role.contains(r.getName())) {
                check = true;
            }
        }
        return check;
    }
}

