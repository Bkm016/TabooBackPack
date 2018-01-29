package me.skymc.taboobackpack.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import me.skymc.taboobackpack.BackPack;
import me.skymc.taboobackpack.api.BackPackAPI;
import me.skymc.taboobackpack.inventory.BackPackHolder;
import me.skymc.taboolib.inventory.ItemUtils;
import me.skymc.taboolib.itemnbtapi.NBTItem;

/**
 * 背包监听器
 * 
 * @author sky
 * @sine 2018-1-29
 */
public class ListenerInventory implements Listener {
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onClickItem(InventoryClickEvent e) {
		if (e.isCancelled() || !(e.getInventory().getHolder() instanceof BackPackHolder) || e.getWhoClicked().isOp()) {
			return;
		}
		
		ItemStack clickItem = e.getCurrentItem();
		if (e.getClick().isKeyboardClick()) {
			clickItem = e.getWhoClicked().getInventory().getItem(e.getHotbarButton());
		}
		
		/**
		 * 如果点击物品是背包，则取消事件，如果不是则判断条件表
		 */
		if (BackPack.getBackPackAPI().isBackPack(clickItem)) {
			((Player) e.getWhoClicked()).sendMessage(BackPack.getConf().getString("Messages.item-deny").replace("&", "§"));
			e.setCancelled(true);
		} else if (e.getRawSlot() >= 0) {
			for (String condition : BackPack.getConf().getStringList("DenyItems")) {
				boolean deny = false;
				if (condition.startsWith("name@") && ItemUtils.isNameAs(e.getCurrentItem(), condition.split("@")[1])) {
					deny = true;
				}
				else if (condition.startsWith("lore@") && ItemUtils.hasLore(e.getCurrentItem(), condition.split("@")[1])) {
					deny = true;
				}
				else if (condition.startsWith("type@") && e.getCurrentItem().getType().toString().equals(condition.split("@")[1])) {
					deny = true;
				}
				if (deny) {
					((Player) e.getWhoClicked()).sendMessage(BackPack.getConf().getString("Messages.item-deny").replace("&", "§"));
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onClickBackPack(InventoryClickEvent e) {
		/**
		 * 检查是否符合打开背包的动作
		 * 
		 * - 没有被取消
		 * - 右键点击
		 * - 位置正确
		 * - 物品正确
		 */
		if (e.isCancelled() || !BackPack.getConf().getIntegerList("Settings.EffectiveSlots").contains(e.getRawSlot()) || !e.getClick().isRightClick() || !BackPack.getBackPackAPI().isBackPack(e.getCurrentItem())) {
			return;
		} else {
			e.setCancelled(true);
		}
		
		/**
		 * 检查是否符合打开背包的条件
		 * 
		 * - 是否在其他界面
		 * - 是否在禁用世界
		 */
		if ((!BackPack.getConf().getBoolean("Settings.OpenOnOther") && !e.getWhoClicked().getInventory().getTitle().equals("container.inventory")) || BackPack.getConf().getStringList("DenyWorlds").contains(e.getWhoClicked().getWorld().getName())) {
			((Player) e.getWhoClicked()).sendMessage(BackPack.getConf().getString("Messages.open-fall").replace("&", "§"));
			return;
		}
		
		/**
		 * 检查是否正在打开背包
		 */
		if (e.getWhoClicked().hasMetadata("backpack-opening")) {
			((Player) e.getWhoClicked()).sendMessage(BackPack.getConf().getString("Messages.open-already").replace("&", "§"));
			return;
		}
		
		/**
		 * 检查这个背包是否被注册, 没有则检查是否被其他人打开
		 */
		NBTItem nbt = new NBTItem(e.getCurrentItem());
		if (!nbt.hasKey("backpack-uid")) {
			nbt.setString("backpack-uid", UUID.randomUUID().toString());
			nbt.setString("backpack-ownder", e.getWhoClicked().getName());
			e.setCurrentItem(nbt.getItem());
		} else {
			for (Player player : Bukkit.getOnlinePlayers()) {
				BackPackHolder holder = getBackPackHolder(player);
				if (holder != null && holder.getBackPackUID().equals(nbt.getString("backpack-uid"))) {
					((Player) e.getWhoClicked()).sendMessage(BackPack.getConf().getString("Messages.open-other").replace("&", "§"));
					return;
				}
			}
		}
		
		/**
		 * 打开背包任务
		 */
		((Player) e.getWhoClicked()).sendMessage(BackPack.getConf().getString("Messages.open-pre").replace("&", "§"));
		((Player) e.getWhoClicked()).setMetadata("backpack-opening", new FixedMetadataValue(BackPack.getInst(), true));
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				FileConfiguration conf = BackPack.getBackPackAPI().getBackPackConfiguration(nbt.getString("backpack-uid"));
				Inventory inventory = Bukkit.createInventory(new BackPackHolder(nbt.getString("backpack-uid")), nbt.getInteger("backpack-size") * 9, ItemUtils.getCustomName(e.getCurrentItem()));
				
				for (Integer i = 0; i < inventory.getSize() ; i++) {
					inventory.setItem(i, conf.getItemStack(i.toString()));
				}
				
				new BukkitRunnable() {
					
					@Override
					public void run() {
						((Player) e.getWhoClicked()).openInventory(inventory);
						((Player) e.getWhoClicked()).removeMetadata("backpack-opening", BackPack.getInst());
					}
				}.runTask(BackPack.getInst());
			}
		}.runTaskAsynchronously(BackPack.getInst());
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		BackPack.getBackPackAPI().saveBackPack((Player) e.getPlayer(), false);
	}
	
	private BackPackHolder getBackPackHolder(Player player) {
		if (player.getOpenInventory().getTopInventory().getHolder() instanceof BackPackHolder) {
			return (BackPackHolder) player.getOpenInventory().getTopInventory().getHolder();
		} else {
			return null;
		}
	}
}
