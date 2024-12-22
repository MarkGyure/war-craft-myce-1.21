package net.myce.warcraft;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Factions
{
    public static boolean checkIfFactionExists(String faction, StateSaverAndLoader serverState)
    {
        for(int i = 0; i < serverState.factionList.size(); i++)
        {
            if(serverState.factionList.get(i).equals(faction))
                return true;
        }
        return false;
    }

    public static boolean checkIfFactionNameAvailable(String name, StateSaverAndLoader serverState)
    {
        List<String> takenNames = serverState.factionList;

        for(String takenName : takenNames)
        {
            if(takenName.equals(name))
                return false;
        }

        return true;
    }

    public static boolean checkIfAlreadyInAFaction(PlayerData player)
    {
        return player.factionName == null;
    }

    public static UUID getFactionLeader(String faction, StateSaverAndLoader serverState)
    {
        AtomicReference<UUID> leader = new AtomicReference<>();

        serverState.players.forEach((uuid, playerData) ->
        {
            if(playerData.factionName.equals(faction) && playerData.factionLeader)
                leader.set(uuid);
        });

        return leader.get();
    }
}
