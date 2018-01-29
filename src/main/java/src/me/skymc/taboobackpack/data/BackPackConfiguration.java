package me.skymc.taboobackpack.data;

import org.bukkit.configuration.file.FileConfiguration;

import lombok.Getter;
import lombok.Setter;

/**
 * 背包临时数据
 * 
 * @author sky
 * @sine 2018-1-29
 */
public class BackPackConfiguration {
	
	@Setter
	@Getter
	private Long lastOpened;
	
	@Setter
	@Getter
	private FileConfiguration conf;
	
	public BackPackConfiguration(FileConfiguration conf) {
		this.conf = conf;
		this.lastOpened = System.currentTimeMillis();
	}
}
