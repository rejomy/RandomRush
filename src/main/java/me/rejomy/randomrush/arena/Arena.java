package me.rejomy.randomrush.arena;

import lombok.Getter;
import lombok.Setter;
import me.rejomy.randomrush.util.world.Position;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Arena {
    public String name;
    public String worldName;
    public int minPlayers = 2, maxPlayers = 2;

    @Setter
    @Getter
    Position centerPosition;

    @Getter
    List<Position> spawnPositions = new ArrayList<>();

    @Getter
    List<Integer> playerPerTeam = new ArrayList<>();

    public void copy(Arena arena) {
        for (Field field : arena.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                field.set(this, field.get(arena));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
