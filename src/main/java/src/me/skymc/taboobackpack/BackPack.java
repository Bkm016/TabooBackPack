package me.skymc.taboobackpack;

import java.io.File;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import me.skymc.taboobackpack.api.BackPackAPI;
import me.skymc.taboobackpack.command.MainCommand;
import me.skymc.taboobackpack.data.BackPackConfiguration;
import me.skymc.taboobackpack.inventory.BackPackHolder;
import me.skymc.taboobackpack.listener.ListenerInventory;
import me.skymc.taboobackpack.task.BackPackSaveTask;
import me.skymc.taboolib.fileutils.ConfigUtils;
import me.skymc.taboolib.message.MsgUtils;

/**
 * 插件主类
 * 
 * @author sky
 * @sine 2018-1-29
 */
public class BackPack extends JavaPlugin {
	
	@Getter
	private static BackPack inst;
	
	@Getter
	private static BackPackAPI backPackAPI;
	
	@Getter
	private static File backPackFolder;
	
	@Getter
	private static ConcurrentHashMap<String, BackPackConfiguration> backPacks = new ConcurrentHashMap<>();
	
	@Getter
	private static FileConfiguration conf;
	
	@Getter
	private static BackPackSaveTask backPackSaveTask;
	
	@Override
	public FileConfiguration getConfig() {
		return conf;
	}
 	
	@Override
	public void reloadConfig() {
		File file = new File(getDataFolder(), "config.yml");;
		if (!file.exists()) {
			saveResource("config.yml", true);
		}
		conf = ConfigUtils.load(this, file);
	}
	
	@Override
	public void onLoad() {
		inst = this;
		backPackAPI = new BackPackAPI();
		backPackSaveTask = new BackPackSaveTask();
	}
	
	@Override
	public void onEnable() {
		/**
		 * 载入配置文件
		 */
		reloadConfig();
		createFolders();
		
		/**
		 * 注册监听器
		 */
		Bukkit.getPluginManager().registerEvents(new ListenerInventory(), this);
		
		/**
		 * 注册指令
		 */
		Bukkit.getPluginCommand("taboocommand").setExecutor(new MainCommand());
		
		/**
		 * 启动保存任务
		 */
		backPackSaveTask.runTaskTimerAsynchronously(this, 0, conf.getInt("Settings.SaveDelay"));
		
		/**
		 * 小广告
		 */
		MsgUtils.send("插件已载入!", this);
		MsgUtils.send("作者: &f坏黑 &7(&fQQ: 449599702&7)", this);
	}
	
	@Override
	public void onDisable() {
		/**
		 * 关闭所有在线玩家的背包
		 */
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getOpenInventory().getTopInventory().getHolder() instanceof BackPackHolder) {
				player.closeInventory();
			}
		}
		
		/**
		 * 停止所有任务
		 */
		Bukkit.getScheduler().cancelTasks(this);
		
		/**
		 * 同步保存数据
		 */
		backPackSaveTask.run();
	}
	
	/**
	 * 创建背包数据文件夹
	 */
	private void createFolders() {
		backPackFolder = new File(getDataFolder(), "BackPacks");
		if (!backPackFolder.exists()) {
			backPackFolder.mkdir();
		}
	}
}
