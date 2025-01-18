package me.rejomy.randomrush.manager;

import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.arena.Arena;
import me.rejomy.randomrush.match.Match;
import me.rejomy.randomrush.util.io.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

public class WorldManager {
    final String SYMBOL_SLASH = "/";
    final String SYMBOL_UNDERLINING = "_";

    public void load(Match match) {
        String worldName = match.getArena().worldName;
        int lastSlashIndex = worldName.lastIndexOf(SYMBOL_SLASH);
        // I add one because at first part we need to get slash too, because
        // we need to add underlining to world name, but not world folder.
        String newWorldName = worldName.substring(0, lastSlashIndex + 1) + SYMBOL_UNDERLINING +
                worldName.substring(lastSlashIndex + 1) +
                ThreadLocalRandom.current().nextInt();
        match.setWorld(createWorld(match.getArena().worldName, newWorldName));
    }

    public void unload(Match match) {
        Bukkit.getServer().unloadWorld(match.getWorld(), true);
        FileUtil.deleteFile(new File(match.getWorld().getName()));
    }

    private World createWorld(String source, String name){
        World template = Bukkit.getWorld(source);

        File sourceDir = template.getWorldFolder();
        File target = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/" + name);

        FileUtil.copyWorld(sourceDir, target);

        return new WorldCreator(name).createWorld();
    }

    public void cleanupOldMaps() {
        for (Arena arena : RandomRushAPI.INSTANCE.getArenaManager().arenas) {
            // If arena put in some directory, same with random_rush/arenas/ we move to this directory and search files
            // But if arenas not in directory, lastIndexOf return -1 and its can cause error in substring.
            int slashIndex = arena.worldName.lastIndexOf(SYMBOL_SLASH);
            File path = slashIndex != -1?
                    new File(arena.worldName.substring(0, arena.worldName.lastIndexOf(SYMBOL_SLASH))) :
                    Bukkit.getServer().getWorldContainer();

            for (File file : path.listFiles()) {
               String name = file.getName();

               int lastIndexOfUnderlining = name.lastIndexOf(SYMBOL_UNDERLINING);
               if (lastIndexOfUnderlining > 0 &&
                       name.startsWith(SYMBOL_UNDERLINING) && name.substring(1, lastIndexOfUnderlining).equals(arena.worldName)) {
                   RandomRushAPI.INSTANCE.getPlugin().getLogger()
                           .warning("Find not deleted map " + name + ". Starting to deleting this automatically...");
                   FileUtil.deleteFile(file);
               }
            }
        }
    }
}
