package Commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public abstract class UtilitiesCmd extends Command {

    public UtilitiesCmd() {
        this.category = new Category("Utilities");
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent e) {
        doCommand(e);
    }


    public abstract void doCommand(CommandEvent e);
}

