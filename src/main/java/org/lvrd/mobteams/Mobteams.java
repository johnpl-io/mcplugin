package org.lvrd.mobteams;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Set;
import java.util.UUID;
public final class Mobteams extends JavaPlugin implements Listener {
    List<Entity> entities;

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("The plugin is running !");
        getServer().getPluginManager().registerEvents(this, this);
    }


    @EventHandler
    public void entity_spawn_spy(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
                Set<String> entries = team.getEntries();
                for (String entry : entries) {

                    Entity entity =  Bukkit.getEntity(UUID.fromString(entry));
                    if(entity instanceof Monster) {
                        System.out.println(entity);
                    }
                }

                // }
                Creature creature = (Creature) event.getEntity();
                if (creature instanceof Monster) {
                    Monster monster = (Monster) creature;
                    monster.setAI(true); // Enable AI for the monster //line may not be necessary
                    setTargetForAllMonstersOfType(monster, monster.getType()); // Set the target as the closest monster
                    System.out.println("Monster creature has spawned");
                } else {
                    System.out.println("creature spawned");
                }
            }

        }
    }
    private void setTargetForAllMonstersOfType(Monster monster, EntityType type) {
        for (Entity entity : monster.getWorld().getEntities()) {

            if (entity instanceof Monster && entity != monster && entity.getType() == type) {
                ((Monster) entity).setTarget(monster); // Set the target as the original monster
                monster.setTarget((LivingEntity) entity);
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

