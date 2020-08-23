package jp.mkserver.magicspellbook;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static jp.mkserver.magicspellbook.SpellBookFileManager.checkFolderExist;

public final class MagicSpellBook extends JavaPlugin {

    public static String prefix = "§e§l[§b§lMSBook§e§l]§r";
    public static MagicSpellBook plugin;
    public static MSPData data;
    public static MSPCommand command;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        checkFolderExist();
        data = new MSPData();
        command = new MSPCommand();
        getCommand("msp").setExecutor(command);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void safeGiveItem(Player p, ItemStack item){
        if(p.getInventory().firstEmpty()==-1){
            p.getWorld().dropItemNaturally(p.getLocation(),item);
            p.sendMessage(prefix+"§eインベントリが一杯のため付近にアイテムがドロップしました");
        }else{
            p.getInventory().addItem(item);
        }
    }

    public static void safeGiveItem(Player p, List<ItemStack> items){
        boolean fulldrop = false;
        for(ItemStack item:items){
            if(p.getInventory().firstEmpty()==-1){
                p.getWorld().dropItemNaturally(p.getLocation(),item);
                fulldrop = true;
            }else{
                p.getInventory().addItem(item);
            }
        }
        if(fulldrop){
            p.sendMessage(prefix+"§eインベントリが一杯のため付近にアイテムがドロップしました");
        }
    }
}
