package org.lvrd.mobteams;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
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



        Entity newEntity = event.getEntity();

                if(newEntity instanceof LivingEntity) {

                 //   FindEnemyTarget((LivingEntity) newEntity);
                  Map<String, List<LivingEntity>> MapOfTeams = GetTeams();
                  FindEnemyTarget(MapOfTeams);
                }




        }
        @EventHandler
public void onCreatureDie(EntityDeathEvent event) {
        Entity newEntity = event.getEntity();
        if(newEntity instanceof LivingEntity) {
          //  FindEnemyTarget((LivingEntity) newEntity);
             Map<String, List<LivingEntity>> MapOfTeams = GetTeams();
           FindEnemyTarget(MapOfTeams);
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
                    if(!(livingEntity instanceof Mob)) {
                            continue;
                    }
                    String current_team = mp.getKey();
                    double minDistance = Double.POSITIVE_INFINITY;
                    LivingEntity minLivingEntity = null;
                    List<Entity> AllEntities = livingEntity.getWorld().getEntities();
                    for (Entity target : AllEntities) {
                        if(target instanceof LivingEntity) {
                            if(target instanceof Player) {
                                if(((Player) target).getGameMode() == GameMode.CREATIVE) {
                                    continue;
                            }
                        }
                        Team target_team = null;
                           if(target instanceof Player) {
                               target_team = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam((Player) target);
                            } else {
                               target_team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(String.valueOf(target.getUniqueId()));
                           }
                        String target_teamname = null;
                        if(target_team != null) {
                            target_teamname = target_team.getName();
                        }

                           if(!(current_team.equals(target_teamname))) {

                               double DistanceToEntity = livingEntity.getLocation().distance(target.getLocation());
                                if(DistanceToEntity < minDistance) {
                                   minDistance = DistanceToEntity;
                                   minLivingEntity = (LivingEntity) target;
                                }
                            }
                        }
                    }
                    if(minDistance <= 20) {
                        Mob mob = (Mob) livingEntity;
                        mob.setAI(true);
                        System.out.println("TARGET SET " + minLivingEntity + "is targeted by " + mob);
                        mob.setTarget(minLivingEntity);
                    }

            }
        }
    }

    private void FindEnemyTarget(LivingEntity livingentity) {
        for (Entity enemy : livingentity.getWorld().getEntities()) {
            if (!(enemy instanceof Mob)) {
                continue;
            }
            Mob pot_enemy = (Mob) enemy;
            String current_teamname = "";
            Team current_team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(String.valueOf(pot_enemy.getUniqueId()));
            if (current_team != null) {
                current_teamname = current_team.getName();
            }
            double minDistance = Double.POSITIVE_INFINITY;
            LivingEntity minLivingEntity = null;

            for (Entity target : livingentity.getWorld().getEntities()) {
                if(target.getUniqueId().equals(enemy.getUniqueId())) {
                    continue;
                }
                Team target_team = null;
                if (target instanceof LivingEntity) {
                    if (target instanceof Player) {
                        if (((Player) target).getGameMode() == GameMode.CREATIVE) {
                            continue;
                        }
                        target_team = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam((Player) target);
                    } else {
                        target_team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(String.valueOf(target.getUniqueId()));
                    }
                    String target_teamname = null;
                    if (target_team != null) {
                        target_teamname = target_team.getName();
                    }
                    double DistanceToEnemy = -1;

                    if (!(current_teamname.equals(target_teamname)) && !current_teamname.equals(""))  {
                        DistanceToEnemy = enemy.getLocation().distance(target.getLocation());
                    }
                    if(current_teamname.equals("") && target_team != null) {
                        DistanceToEnemy = enemy.getLocation().distance(target.getLocation());
                    }
                    if ((DistanceToEnemy != -1) && DistanceToEnemy < minDistance) {
                        minDistance = DistanceToEnemy;
                        minLivingEntity = (LivingEntity) target;
                    }
                }
            }
            if(minDistance <= 20 && minLivingEntity != null) {
                Mob mob = (Mob) enemy;
                mob.setAI(true);
                System.out.println("TARGET SET " + minLivingEntity + " is targeted by " + mob);
                mob.setTarget(minLivingEntity);
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
