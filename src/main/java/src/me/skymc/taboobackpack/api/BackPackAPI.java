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
 * ���� API
 * 
 * @author sky
 * @sine 2018-1-29
 */
public class BackPackAPI {
	
	/**
	 * �����Ʒ�Ƿ�Ϊ����
	 * 
	 * @param item ��Ʒ
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
	 * ��ȡ���������ļ�
	 * 
	 * @param backPackUID �������к�
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
	 * ����������ڴ򿪵ı���
	 * 
	 * @param player ���
	 * @param closeInventory �Ƿ�ر�
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
