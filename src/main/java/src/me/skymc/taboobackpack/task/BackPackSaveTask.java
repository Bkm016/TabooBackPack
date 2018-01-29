package me.skymc.taboobackpack.task;

import java.io.File;
import java.util.Map.Entry;

import org.bukkit.scheduler.BukkitRunnable;

import me.skymc.taboobackpack.BackPack;
import me.skymc.taboobackpack.data.BackPackConfiguration;
import me.skymc.taboolib.fileutils.FileUtils;
import me.skymc.taboolib.message.MsgUtils;

/**
 * 背包数据保存任务
 * 
 * @author sky
 * @sine 2018-1-29
 */
public class BackPackSaveTask extends BukkitRunnable {

	@Override
	public void run() {
		long time = System.currentTimeMillis();
		int size_all = BackPack.getBackPacks().size();
		int size_remove = 0;
		
		for (Entry<String, BackPackConfiguration> backPackEntry : BackPack.getBackPacks().entrySet()) {
			File backPackFile = FileUtils.file(BackPack.getBackPackFolder(), backPackEntry.getKey());
			try {
				backPackEntry.getValue().getConf().save(backPackFile);
			} catch (Exception e) {
				MsgUtils.warn("背包 " + backPackEntry.getKey() + " 保存失败");
				MsgUtils.warn("原因 " + e.getMessage());
			}
			
			/**
			 * 如果这个背包在一段时间内没有被再次打开
			 * 将会从缓存中移除
			 */
			if (System.currentTimeMillis() - backPackEntry.getValue().getLastOpened() >= BackPack.getConf().getInt("Settings.ClearTimeOut") * 1000) {
				BackPack.getBackPacks().remove(backPackEntry.getKey());
				size_remove++;
			}
		}
		
		/**
		 * 是否启用调试模式
		 */
		if (BackPack.getConf().getBoolean("Settings.Debug")) {
			MsgUtils.send("背包数据保存成功, 耗时: &f" + (System.currentTimeMillis() - time) + "ms");
			MsgUtils.send("保存总数: &f" + size_all);
			MsgUtils.send("清理总数: &f" + size_remove);
		}
	}
}
