package net.myce.warcraft.factions;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.myce.warcraft.PlayerData;
import net.myce.warcraft.StateSaverAndLoader;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Factions {
    public static boolean checkIfFactionExists(StateSaverAndLoader serverState, String faction)
    {
        for (int i = 0; i < serverState.factionList.size(); i++)
        {
            if (serverState.factionList.get(i).equals(faction))
                return true;
        }
        return false;
    }

    public static boolean checkIfFactionNameAvailable(StateSaverAndLoader serverState, String name)
    {
        List<String> takenNames = serverState.factionList;

        for (String takenName : takenNames)
        {
            if (takenName.equals(name))
                return false;
        }

        return true;
    }

    public static boolean checkIfAlreadyInAFaction(PlayerData player)
    {
        return player.factionName.isEmpty();
    }

    public static UUID getFactionLeader(StateSaverAndLoader serverState, String faction)
    {
        UUID leader = null;

        for (Map.Entry<UUID, PlayerData> entry : serverState.players.entrySet())
        {
            UUID uuid = entry.getKey();
            PlayerData player = entry.getValue();

            System.out.println(uuid);
            if (player.factionName != null)
            {
                System.out.println(player.factionName);
            }
            System.out.println(player.factionLeader);

            if (player.factionName != null && player.factionName.equals(faction) && player.factionLeader)
            {
                leader = uuid;
            }
        }

        return leader;
    }

    public static ServerPlayerEntity checkIfPlayerOnline(ServerWorld world, UUID playerUuid)
    {
        List<ServerPlayerEntity> listOfPlayers = world.getPlayers();

        for (ServerPlayerEntity player : listOfPlayers)
        {
            if (player.getUuid() == playerUuid) {
                return player;
            }
        }

        return null;
    }

    public static boolean acceptRequestToJoin(StateSaverAndLoader serverState, UUID leaderUuid, UUID joinerUuid)
    {
        PlayerData leaderData = serverState.players.get(leaderUuid);
        PlayerData joinerData = serverState.players.get(joinerUuid);

        if(joinerData.factionName.isEmpty())
        {
            joinerData.factionName = leaderData.factionName;

            System.out.println(joinerUuid);
            System.out.println(leaderData.incomingRequestOne);

            //clearing the inbox slot that the joinerUuid's request is taking up
            if (leaderData.incomingRequestOne == joinerUuid)
            {
                System.out.println("Hi");
                leaderData.incomingRequestOne = UUID.fromString("00000000-0000-0000-0000-000000000000");
            }
            else if (leaderData.incomingRequestTwo == joinerUuid)
                leaderData.incomingRequestTwo = UUID.fromString("00000000-0000-0000-0000-000000000000");
            else
                leaderData.incomingRequestThree = UUID.fromString("00000000-0000-0000-0000-000000000000");

            return true;
        }

        return false;
    }

    public static boolean declineRequestToJoin(StateSaverAndLoader serverstate, UUID leaderUuid, UUID joinerUuid)
    {
        PlayerData leaderData = serverstate.players.get(leaderUuid);
        PlayerData joinerData = serverstate.players.get(joinerUuid);

        if (joinerData.factionName.isEmpty())
        {
            //clearing the inbox slot that the joinerUuid's request is taking up
            if (leaderData.incomingRequestOne == joinerUuid)
                leaderData.incomingRequestOne = UUID.fromString("00000000-0000-0000-0000-000000000000");
            else if (leaderData.incomingRequestTwo == joinerUuid)
                leaderData.incomingRequestTwo = UUID.fromString("00000000-0000-0000-0000-000000000000");
            else
                leaderData.incomingRequestThree = UUID.fromString("00000000-0000-0000-0000-000000000000");

            return true;
        }

        return false;
    }

    public static int checkForAvailableSlot(StateSaverAndLoader serverState, UUID leaderUuid, UUID joinerUuid) {
        PlayerData leaderData = serverState.players.get(leaderUuid);

        if (!leaderData.incomingRequestOne.toString().equals("00000000-0000-0000-0000-000000000000")) {
            if (leaderData.incomingRequestOne == joinerUuid) {
                return -1;
            } else if (!leaderData.incomingRequestTwo.toString().equals("00000000-0000-0000-0000-000000000000")) {
                if (leaderData.incomingRequestTwo == joinerUuid) {
                    return -1;
                } else if (!leaderData.incomingRequestThree.toString().equals("00000000-0000-0000-0000-000000000000")) {
                    if (leaderData.incomingRequestThree == joinerUuid) {
                        return -1;
                    }

                    return 0;
                } else
                    return 3;
            } else {
                return 2;
            }
        } else {
            return 1;
        }
    }

    public static void sendToLeaderInbox(StateSaverAndLoader serverState, UUID leaderUuid, UUID joinerUuid, int inboxSlot) {
        PlayerData leaderData = serverState.players.get(leaderUuid);

        if (inboxSlot == 1)
            leaderData.incomingRequestOne = joinerUuid;
        else if (inboxSlot == 2)
            leaderData.incomingRequestTwo = joinerUuid;
        else
            leaderData.incomingRequestThree = joinerUuid;
    }

    public static boolean getLeaderInboxSlot(PlayerData leaderData, int inboxSlot) {
        if (inboxSlot == 1)
            return leaderData.incomingRequestOne.toString().equals("00000000-0000-0000-0000-000000000000");
        else if (inboxSlot == 2)
            return leaderData.incomingRequestTwo.toString().equals("00000000-0000-0000-0000-000000000000");
        else
            return leaderData.incomingRequestThree.toString().equals("00000000-0000-0000-0000-000000000000");
    }

    public static String getUsername(StateSaverAndLoader serverState, UUID uuid)
    {
        PlayerData playerData = serverState.players.get(uuid);

        return playerData.username;
    }
}
