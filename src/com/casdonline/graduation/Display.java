package com.casdonline.graduation;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.ItemBookWritten;
import cn.nukkit.utils.TextFormat;

public class Display implements CommandExecutor {
	private Main instance;
	
	public Display(Main main) {
		instance = main;
	}
	
	public void display(MinecraftUser user) {
		for(Player player : instance.getServer().getOnlinePlayers().values()) {
			player.sendTitle(TextFormat.GOLD + "" + TextFormat.BOLD + user.getPlayerName(), user.getFullName());
			player.sendMessage(TextFormat.GREEN + "" + TextFormat.BOLD + user.getPlayerName() + " (" + user.getFullName() + ")");
		}
	}
	
	public ItemBookWritten createItem(MinecraftUser user) {
		ItemBookWritten book = new ItemBookWritten();
		String[] pages = {
				TextFormat.DARK_BLUE + "" + TextFormat.BOLD + "Chambersburg Area \n Senior High School\n\n"
						+ TextFormat.RESET + "   This certifies that\n\n"
						+ TextFormat.GOLD + "" + TextFormat.BOLD + "" + TextFormat.UNDERLINE + user.getFullName() + "\n\n"
						+ TextFormat.RESET + "   has satisfactorily\n"
								+ "completed the course of study as perscribed by the "
						+ "Chambersburg Area School District, and is therefore granted this diploma"
		};
		book.writeBook("CASHS", "Diploma", pages);
		return book;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		display(UserManager.userMap.get(args[0]));
		((Player) sender).getInventory().addItem(createItem(UserManager.userMap.get(args[0])));
		return true;
	}
	
	
}
