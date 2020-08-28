package jp.mkserver.magicspellbook;

import jp.mkserver.magicspellbook.inv.multiver.UpdateTitle;
import jp.mkserver.magicspellbook.inv.multiver.UpdateTitle_1_15_2;
import jp.mkserver.magicspellbook.inv.multiver.UpdateTitle_1_16_1;
import jp.mkserver.magicspellbook.inv.multiver.UpdateTitle_1_16_2;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static jp.mkserver.magicspellbook.SpellBookFileManager.checkFolderExist;

public final class MagicSpellBook extends JavaPlugin {

    public static String prefix = "§e§l[§b§lMSBook§e§l]§r";
    public static MagicSpellBook plugin;
    public static MSPData data;
    // public static MSPCommand command;
    public static MSPCreator creator;
    public static UpdateTitle updateTitle;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        checkToUTitle();
        checkFolderExist();
        data = new MSPData();
        // command = new MSPCommand();
        creator = new MSPCreator(this);
        getCommand("msp").setExecutor(creator);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void checkToUTitle(){
        String version;
        try {

            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            return;
        }

        switch (version) {
            case "v1_15_R1":
                //server is running 1.8-1.8.1 so we need to use the 1.8 R1 NMS class
                updateTitle = new UpdateTitle_1_15_2();

                break;
            case "v1_16_R1":
                //server is running 1.8.3 so we need to use the 1.8 R2 NMS class
                updateTitle = new UpdateTitle_1_16_1();
                break;
            case "v1_16_R2":
                //server is running 1.8.3 so we need to use the 1.8 R2 NMS class
                updateTitle = new UpdateTitle_1_16_2();
                break;
        }
        // This will return true if the server version was compatible with one of our NMS classes
        // because if it is, our actionbar would not be null
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
