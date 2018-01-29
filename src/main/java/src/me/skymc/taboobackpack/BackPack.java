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
 * �������
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
		 * ���������ļ�
		 */
		reloadConfig();
		createFolders();
		
		/**
		 * ע�������
		 */
		Bukkit.getPluginManager().registerEvents(new ListenerInventory(), this);
		
		/**
		 * ע��ָ��
		 */
		Bukkit.getPluginCommand("taboocommand").setExecutor(new MainCommand());
		
		/**
		 * ������������
		 */
		backPackSaveTask.runTaskTimerAsynchronously(this, 0, conf.getInt("Settings.SaveDelay"));
		
		/**
		 * С���
		 */
		MsgUtils.send("���������!", this);
		MsgUtils.send("����: &f���� &7(&fQQ: 449599702&7)", this);
	}
	
	@Override
	public void onDisable() {
		/**
		 * �ر�����������ҵı���
		 */
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getOpenInventory().getTopInventory().getHolder() instanceof BackPackHolder) {
				player.closeInventory();
			}
		}
		
		/**
		 * ֹͣ��������
		 */
		Bukkit.getScheduler().cancelTasks(this);
		
		/**
		 * ͬ����������
		 */
		backPackSaveTask.run();
	}
	
	/**
	 * �������������ļ���
	 */
	private void createFolders() {
		backPackFolder = new File(getDataFolder(), "BackPacks");
		if (!backPackFolder.exists()) {
			backPackFolder.mkdir();
		}
	}
}
