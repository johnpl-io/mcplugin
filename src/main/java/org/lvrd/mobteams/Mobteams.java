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
    public void onCreatureSpawn(EntitySpawnEvent event){



        Entity newEntity = event.getEntity();


            String teamName = isOnTeam(newEntity);
            if (teamName != null) {
               Map<String, List<LivingEntity>> MapOfTeams = GetTeams();
                    FindEnemyTarget(MapOfTeams);

            }
        }
        @EventHandler
public void onCreatureDie(EntityDeathEvent e) {
        if(e.getEntity() instanceof Player) {
            System.out.println("A loser player died!");
        }
}
    private Map<String, List<LivingEntity>> GetTeams() {
        Map<String, List<LivingEntity>> teams = new HashMap<>();
        for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            Set<String> entries = team.getEntries();
            String teamName = team.getName();
            UUID uid;
                teams.put(teamName, new ArrayList<>());
            List<LivingEntity> livingEntities = teams.get(teamName);
            for (String entry : entries) {
                try {
                    Entity mob = Bukkit.getEntity(UUID.fromString(entry));
                        if(mob instanceof LivingEntity) {
                            livingEntities.add((LivingEntity) mob);
                        }
                } catch (IllegalArgumentException e){
                    //is a player
                        LivingEntity player = (LivingEntity) Bukkit.getPlayer(entry);
                        livingEntities.add(player);
                }
            }

        }
            return teams;
    }
    //function that sets target for monsters on a team to the closest enemy
    private void FindEnemyTarget(Map<String, List<LivingEntity>> MapOfTeams) {
        for(Map.Entry<String, List<LivingEntity>> mp: MapOfTeams.entrySet()) {
            for(LivingEntity livingEntity : mp.getValue()) {
                    if(!(livingEntity instanceof Monster)) {
                            continue;
                    }
                    double minDistance = Double.POSITIVE_INFINITY;
                    LivingEntity minLivingEntity = null;
                for(Map.Entry<String, List<LivingEntity>> teams: MapOfTeams.entrySet()) {
                    if(teams.getKey().equals(mp.getKey())) {
                        continue; //skip entities on the same team
                    }
                    for(LivingEntity target : teams.getValue()) {
                        double DistanceToEntity = livingEntity.getLocation().distance(target.getLocation());
                        if(DistanceToEntity < minDistance) {
                            minDistance = minDistance;
                            minLivingEntity = target;
                        }
                    }
                }
                Monster monster = (Monster)livingEntity;
                monster.setAI(true);
                System.out.println("TARGET SET " + minLivingEntity + "is targeted by " + monster);
                monster.setTarget(minLivingEntity);
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
