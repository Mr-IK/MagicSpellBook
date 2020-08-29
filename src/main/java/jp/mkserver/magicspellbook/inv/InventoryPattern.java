package jp.mkserver.magicspellbook.inv;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;

public class InventoryPattern {

    private HashMap<String,InventoryAPI> invs;
    private JavaPlugin plugin;

    public InventoryPattern(JavaPlugin plugin){
        this.plugin = plugin;
        invs = new HashMap<>();
    }

    public void putInv(String id,InventoryAPI inv){
        invs.put(id,inv);
    }

    public void removeInv(String id){
        invs.remove(id);
    }

    public void clearInvs(){
        invs.clear();
    }

    public boolean containInv(String id){
        return invs.containsKey(id);
    }

    private InventoryAPI getInv(String id) {
        return invs.get(id);
    }

    public InventoryAPI copyInv(String id){
        if(!containInv(id)){
            return null;
        }
        return new InventoryAPI(plugin,getInv(id));
    }

    public InventoryAPI copyInv(String id,String newName){
        if(!containInv(id)){
            return null;
        }
        return new InventoryAPI(plugin,getInv(id),newName);
    }

    public InventoryAPI overWriteInv(Player p, InventoryAPI inv, String newTitle, String type) {
        Bukkit.getLogger().info(type+": "+ Arrays.toString(getInv(type).inv.getContents()));
        if (type == null) {
            return null;
        } else if (inv == null) {
            p.closeInventory();
            return null;
        } else if (inv.getSize() != getInv(type).getSize()) {
            inv.regenerateID();
            inv.allunregistRunnable();
            p.closeInventory();
            return null;
        } else {
            inv.updateTitle(p,newTitle);
            inv.copyFromOtherInvAPI(getInv(type));
            return inv;
        }
    }

    public InventoryAPI overWriteInv(Player p, InventoryAPI inv, String newTitle, int size){
        if(inv==null||inv.getSize()!=size){
            p.closeInventory();
            return new InventoryAPI(plugin,newTitle,size);
        }else{
            inv.updateTitle(p,newTitle);
            inv.regenerateID();
            inv.allunregistRunnable();
            inv.clear();
            return inv;
        }
    }
}
