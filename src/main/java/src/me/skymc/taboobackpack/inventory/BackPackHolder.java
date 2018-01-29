package me.skymc.taboobackpack.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import lombok.Getter;

/**
 * ����������ʵ��
 * 
 * @author sky
 * @sine 2018-1-29
 */ 
public class BackPackHolder implements InventoryHolder {
	
	@Getter
	private String backPackUID;
	
	public BackPackHolder(String backPackUID) {
		this.backPackUID = backPackUID;
	}

	@Override
	public Inventory getInventory() {
		return null;
	}
}
