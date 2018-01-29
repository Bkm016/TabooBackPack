package me.skymc.taboobackpack.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.skymc.taboobackpack.BackPack;

public class MainCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(" ");
			sender.sendMessage("��b��l----- TabooBackPack Commands -----");
			sender.sendMessage(" ");
			sender.sendMessage(" ��f/backpack reload ��8-- ��7���ز��");
			sender.sendMessage(" ");
		}
		else if (args[0].equalsIgnoreCase("reload")) {
			BackPack.getInst().reloadConfig();
			sender.sendMessage("reload ok!");
		}
		return false;
	}
}
