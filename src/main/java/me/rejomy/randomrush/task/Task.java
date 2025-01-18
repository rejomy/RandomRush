package me.rejomy.randomrush.task;

import lombok.Getter;
import lombok.Setter;
import me.rejomy.randomrush.RandomRushAPI;
import me.rejomy.randomrush.match.Match;
import org.bukkit.Bukkit;

public abstract class Task {
    @Getter
    private int taskId;
    public Match match;

    public Task(Match match) {
        this.match = match;
    }

    public void run() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(RandomRushAPI.INSTANCE.getPlugin(), getBukkitRunnable(), 20, 20);
    }

    public void runAsync() {
        taskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(RandomRushAPI.INSTANCE.getPlugin(), getBukkitRunnable(), 20, 20);
    }

    @Setter
    @Getter
    private Runnable bukkitRunnable = null;

    protected void cancelTask() {
        Bukkit.getScheduler().cancelTask(getTaskId());
    }
}
