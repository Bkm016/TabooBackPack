package me.skymc.taboobackpack.task;

import java.io.File;
import java.util.Map.Entry;

import org.bukkit.scheduler.BukkitRunnable;

import me.skymc.taboobackpack.BackPack;
import me.skymc.taboobackpack.data.BackPackConfiguration;
import me.skymc.taboolib.fileutils.FileUtils;
import me.skymc.taboolib.message.MsgUtils;

/**
 * �������ݱ�������
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
				MsgUtils.warn("���� " + backPackEntry.getKey() + " ����ʧ��");
				MsgUtils.warn("ԭ�� " + e.getMessage());
			}
			
			/**
			 * ������������һ��ʱ����û�б��ٴδ�
			 * ����ӻ������Ƴ�
			 */
			if (System.currentTimeMillis() - backPackEntry.getValue().getLastOpened() >= BackPack.getConf().getInt("Settings.ClearTimeOut") * 1000) {
				BackPack.getBackPacks().remove(backPackEntry.getKey());
				size_remove++;
			}
		}
		
		/**
		 * �Ƿ����õ���ģʽ
		 */
		if (BackPack.getConf().getBoolean("Settings.Debug")) {
			MsgUtils.send("�������ݱ���ɹ�, ��ʱ: &f" + (System.currentTimeMillis() - time) + "ms");
			MsgUtils.send("��������: &f" + size_all);
			MsgUtils.send("��������: &f" + size_remove);
		}
	}
}
