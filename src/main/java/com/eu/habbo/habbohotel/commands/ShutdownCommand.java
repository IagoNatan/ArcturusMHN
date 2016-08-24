package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.HotelWillCloseInMinutesComposer;
import com.eu.habbo.threading.runnables.ShutdownEmulator;

public class ShutdownCommand extends Command
{
    public ShutdownCommand()
    {
        super("cmd_shutdown", Emulator.getTexts().getValue("commands.keys.cmd_shutdown").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        String reason = "-";
        int minutes = 0;
        if(params.length > 2)
        {
            reason = "";
            for(int i = 1; i < params.length; i++)
            {
                reason += params[i] + " ";
            }
        }
        else
        {
            try
            {
                minutes = Integer.valueOf(params[1]);
            }
            catch (Exception e)
            {
                reason = params[1];
            }
        }

        ServerMessage message = null;
        if (!reason.equals("-"))
        {
            message = new GenericAlertComposer("<b>" + Emulator.getTexts().getValue("generic.warning") + "</b> \r\n" +
                    Emulator.getTexts().getValue("generic.shutdown").replace("%minutes%", minutes + "") + "\r\n" +
                    Emulator.getTexts().getValue("generic.reason.specified") + ": <b>" + reason + "</b>\r" +
                    "\r" +
                    "- " + gameClient.getHabbo().getHabboInfo().getUsername()).compose();
        }
        else
        {
            message = new HotelWillCloseInMinutesComposer(minutes).compose();
        }
        ShutdownEmulator.timestamp = Emulator.getIntUnixTimestamp() + (60 * minutes);
        Emulator.getThreading().run(new ShutdownEmulator(message), minutes * 60 * 1000);
        return true;
    }
}
