package me.Josvth.RandomSpawn;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.Josvth.RandomSpawn.Listeners.*;

public class RandomSpawn extends JavaPlugin{
	
	Logger logger;
	
	private RandomSpawnYamlHandler yamlHandler;
	RandomSpawnCommandHandler commandHandler;
	
	RandomSpawnRespawnListener respawnListener;
	RandomSpawnJoinListener joinListener;
	RandomSpawnSignListener signListener;
	RandomSpawnDebugListener debugListener;
	
	public HashMap<Player,RandomSpawnSpawner> tasks = new HashMap<Player,RandomSpawnSpawner>();
		
	@Override
	public void onDisable() {
		for(RandomSpawnSpawner spawner : tasks.values()){
			spawner.forcestop(true);
		}
	}

	@Override
	public void onEnable() {
		
		logger = Logger.getLogger("Minecraft");
		
		//setup handlers
		yamlHandler = new RandomSpawnYamlHandler(this);
		logDebug("Yamls loaded!");
		
		commandHandler = new RandomSpawnCommandHandler(this);
		logDebug("Commands registered!");
		
		//setup listeners
		respawnListener = new RandomSpawnRespawnListener(this);
		joinListener = new RandomSpawnJoinListener(this);
		signListener = new RandomSpawnSignListener(this);
		
		if(getYamlHandler().config.getBoolean("debug",false)){
			debugListener = new RandomSpawnDebugListener(this);
		}
	}
	    
	public void logInfo(String message){
        logger.info("[Random Spawn] " + message);
    }
    
    public void logDebug(String message){
       if (yamlHandler.config.getBoolean("debug",false)) { logger.info("[Random Spawn] (DEBUG) " + message); }
    }
    
    public void playerInfo(Player player, String message){
    	player.sendMessage(ChatColor.AQUA + "[RandomSpawn] " + ChatColor.RESET + message);
    }
    
    public RandomSpawnYamlHandler getYamlHandler(){
		return yamlHandler;   	
    }
    
    public boolean hasValidEnviroment(World world){
    	return world.getEnvironment() != Environment.NETHER;
    }
    
	public void randomSpawnPlayer(Player player, World spawnWorld) {		
		RandomSpawnSpawner spawner = new RandomSpawnSpawner(this, player, spawnWorld);		
		spawner.setTaskId(getServer().getScheduler().scheduleSyncRepeatingTask(this, spawner, 0, yamlHandler.config.getInt("generator.interval",4)));
		player.setMaximumNoDamageTicks(12000);
		player.setNoDamageTicks(12000);
		player.sendMessage(yamlHandler.config.getString("messages.pleasewait", "Please wait while your spawn is being loaded."));
	}
}