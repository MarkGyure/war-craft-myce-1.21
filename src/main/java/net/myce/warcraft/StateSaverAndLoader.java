package net.myce.warcraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

//Bryce Harbison

public class StateSaverAndLoader extends PersistentState
{
    public List<String> factionList = new ArrayList<>();
    public HashMap<UUID, PlayerData> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        //this method creates the NbtCompounds that stores the data which is to be remembered, aka our list of factions and who is in what faction
        NbtCompound nbtFactionList = new NbtCompound();
        NbtCompound playersNbt = new NbtCompound();

        //goes through each index in factionlist to get the name of each faction
        for(int i = 0; i < factionList.size(); i++)
        {
            String factionName = factionList.get(i);

            //makes sure the string is not null for whatever reason
            if(factionName != null)
            {
                //makes a NEW compound specific for this faction
                NbtCompound factionCompound = new NbtCompound();

                //puts the faction's name into this compound
                factionCompound.putString("faction", factionName);

                //adds the specific compound into the larger compound housing EVERY faction
                nbtFactionList.put(Integer.toString(i), factionCompound);
            }
        }

        //goes through each "player" in the players hashmap
        players.forEach((uuid, playerData) ->
        {
            //creates a compound specific for this player
            NbtCompound playerCompound = new NbtCompound();

            //puts the name of the faction this player is in into the compound
            playerCompound.putString("factionName", playerData.factionName);

            //puts the specific compound into the larger compound that houses EVERY player's data
            playersNbt.put(uuid.toString(), playerCompound);
        });

        //adds both of the larger compounds into the final compound to be returned
        nbt.put("factionList", nbtFactionList);
        nbt.put("players", playersNbt);

        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup)
    {
        //creates a new instance of our state saver and loader
        StateSaverAndLoader state = new StateSaverAndLoader();

        //gets our compounds that house the player's data and factions
        NbtCompound nbtFactionList = tag.getCompound("factionList");
        NbtCompound playersNbt = tag.getCompound("players");

        //goes through each faction of this compound
        nbtFactionList.getKeys().forEach(key ->
        {
            //gets the specific faction's compound, and then gets the string (faction name) stored in that specific compound
            String s = nbtFactionList.getCompound(key).getString("faction");

            //adds this specific compound to our current state's list of factions
            state.factionList.add(s);
        });

        //goes through each player of the compound
        playersNbt.getKeys().forEach(key ->
        {
            //makes a new playerData instance
            PlayerData playerData = new PlayerData();

            //sets the player's faction by getting it from its specific compound
            playerData.factionName = playersNbt.getCompound(key).getString("factionName");

            //gets the players uuid (universally unique identifier)
            UUID uuid = UUID.fromString(key);

            //puts the player's data into the hashmap using their uuid as the key
            state.players.put(uuid, playerData);
        });

        return state;
    }

    public static PlayerData getPlayerState(LivingEntity player) {
        StateSaverAndLoader serverState = getServerState(player.getWorld().getServer());

        //either get the player by the uuid, or we don't have data for him yet, make a new player state
        PlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());

        return playerState;
    }

    private static Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::new, // If there's no 'StateSaverAndLoader' yet create one
            StateSaverAndLoader::createFromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server)
    {
        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, WarCraft.MOD_ID);

        // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
        // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
        // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
        // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
        // there were no actual change to any of the mods state (INCREDIBLY RARE).
        state.markDirty();

        return state;
    }
}
