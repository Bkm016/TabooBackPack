package me.skymc.taboobackpack.api;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.skymc.taboobackpack.BackPack;
import me.skymc.taboobackpack.data.BackPackConfiguration;
import me.skymc.taboobackpack.inventory.BackPackHolder;
import me.skymc.taboolib.fileutils.FileUtils;
import me.skymc.taboolib.itemnbtapi.NBTItem;

/**
 * 背包 API
 * 
 * @author sky
 * @sine 2018-1-29
 */
public class BackPackAPI {
	
	/**
	 * 检查物品是否为背包
	 * 
	 * @param item 物品
	 * @return {@link Boolean}
	 */
	public boolean isBackPack(ItemStack item) {
		if (item == null || item.getType().equals(Material.AIR)) {
			return false;
		} else {
			NBTItem nbt = new NBTItem(item);
			return nbt.hasKey("backpack-size");
		}
	}
	
	/**
	 * 获取背包配置文件
	 * 
	 * @param backPackUID 背包序列号
	 * @return {@link FileConfiguration}
	 */
	public FileConfiguration getBackPackConfiguration(String backPackUID) {
		if (BackPack.getBackPacks().contains(backPackUID)) {
			BackPackConfiguration backpackConf = BackPack.getBackPacks().get(backPackUID);
			backpackConf.setLastOpened(System.currentTimeMillis());
			return backpackConf.getConf();
		} else {
			FileConfiguration conf = YamlConfiguration.loadConfiguration(FileUtils.file(BackPack.getBackPackFolder(), backPackUID));
			BackPack.getBackPacks().put(backPackUID, new BackPackConfiguration(conf));
			return conf;
		}
	}
	
	/**
	 * 保存玩家正在打开的背包
	 * 
	 * @param player 玩家
	 * @param closeInventory 是否关闭
	 * @return (@link Boolean)
	 */
	public boolean saveBackPack(Player player, boolean closeInventory) {
		if (player.getOpenInventory().getTopInventory().getHolder() instanceof BackPackHolder) {
			FileConfiguration conf = BackPack.getBackPackAPI().getBackPackConfiguration(((BackPackHolder) player.getOpenInventory().getTopInventory().getHolder()).getBackPackUID());
			for (Integer i = 0; i < player.getOpenInventory().getTopInventory().getSize() ; i++) {
				conf.set(i.toString(), player.getOpenInventory().getTopInventory().getItem(i));
			}
			if (closeInventory) {
				player.closeInventory();
			}
			return true;
		} else {
			return false;
		}
	}
}
