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

import java.util.*;

public final class Mobteams extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("The plugin is running !");
        getServer().getPluginManager().registerEvents(this, this);
    }


    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event){

        Creature creature = (Creature) event.getEntity();
        String teamName = isOnTeam(creature);
        if(creature instanceof Monster && (teamName.length() > 0)) {
            //creature is on team
        }
    }

    private Map<String, List<LivingEntity>> GetLivingEntityNotOnTeam() {
        Map<String, List<LivingEntity>> teams = new HashMap<>();
        for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            Set<String> entries = team.getEntries();
            String teamName = team.getName();
            UUID uid;
                teams.put(teamName, new ArrayList<>());
            List<LivingEntity> livingEntities = teams.get(teamName);
            for (String entry : entries) {
                try {
                        LivingEntity mob = (LivingEntity) Bukkit.getEntity(UUID.fromString(entry));
                        livingEntities.add(mob);
                } catch (IllegalArgumentException e){
                    //is a player
                        LivingEntity player = (LivingEntity) Bukkit.getPlayer(entry);
                        livingEntities.add(player);
                }
            }

        }
            return teams;
    }

    private void setTargetForAllMonstersOfType(Monster monster, EntityType type) {
        for (Entity entity : monster.getWorld().getEntities()) {

            if (entity instanceof Monster && entity != monster && entity.getType() == type) {
                ((Monster) entity).setTarget(monster); // Set the target as the original monster
                monster.setTarget((Monster) entity);
            }
        }
    }
    //check if an entity is on a team
    private String isOnTeam(Entity entity) {
        for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            Set<String> entries = team.getEntries();
            UUID uid = entity.getUniqueId();

            for (String entry : entries) {
                try {
                    if (uid.equals(UUID.fromString(entry))) {
                        return team.getName();
                    }
                } catch (IllegalArgumentException e) {
                    //means that a player is an entry
                }
            }
        }
        return null;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
