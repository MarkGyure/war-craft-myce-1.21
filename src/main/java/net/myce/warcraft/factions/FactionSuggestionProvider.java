package net.myce.warcraft.factions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.myce.warcraft.StateSaverAndLoader;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FactionSuggestionProvider implements SuggestionProvider<ServerCommandSource>
{
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException
    {
        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(context.getSource().getServer());
        List<String> factionList = serverState.factionList;

        for (String faction : factionList)
        {
            builder.suggest(faction);
        }

        return builder.buildFuture();
    }
}
