package me.rejomy.randomrush.util.expansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.data.StatisticalData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Expansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "randomrush";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Rejomy";
    }

    @Override
    public @NotNull String getVersion() {
        return RandomRushAPI.INSTANCE.getPlugin().getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        StatisticalData data = RandomRushAPI.INSTANCE.getDataManager().get(player).getStatisticalData();

        return switch (params.toLowerCase()) {
            case "kills" -> String.valueOf(data.kills);
            case "deaths" -> String.valueOf(data.deaths);
            case "games" -> String.valueOf(data.games);
            case "wins" -> String.valueOf(data.wins);
            case "online" -> String.valueOf(RandomRushAPI.INSTANCE.getMatchManager().getTotalOnline());
            default -> "-1";
        };
    }
}
