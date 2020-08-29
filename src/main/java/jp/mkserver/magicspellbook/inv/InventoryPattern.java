package jp.mkserver.magicspellbook.inv;

import jp.mkserver.magicspellbook.MagicSpellBook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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

    public InventoryAPI getInv(String id) {
        return invs.get(id);
    }

    public InventoryAPI copyInv(String id){
        if(!containInv(id)){
            return null;
        }
        return new InventoryAPI(plugin,invs.get(id));
    }

    public InventoryAPI copyInv(String id,String newName){
        if(!containInv(id)){
            return null;
        }
        return new InventoryAPI(plugin,invs.get(id),newName);
    }

    public InventoryAPI overWriteInv(Player p, InventoryAPI inv, String newTitle, String type){
        if(type==null) {
            return null;
        }else if(inv==null||inv.getSize()!=invs.get(type).getSize()){
            p.closeInventory();
            return copyInv(type,newTitle);
        }else{
            inv.copyFromOtherInvAPI(invs.get(type));
            inv.updateTitle(p,newTitle);
            return inv;
        }
    }


    public InventoryAPI overWriteInv(Player p, InventoryAPI inv, String newTitle, int size){
        if(inv==null||inv.getSize()!=size){
            p.closeInventory();
            return new InventoryAPI(plugin,newTitle,size);
        }else{
            inv.regenerateID();
            inv.allunregistRunnable();
            inv.updateTitle(p,newTitle);
            inv.clear();
            return inv;
        }
    }
}
