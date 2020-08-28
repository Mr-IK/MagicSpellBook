package jp.mkserver.magicspellbook.inv.multiver;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class UpdateTitle_1_15_2  implements UpdateTitle{
    @Override
    public void sendTitleChangePacket(Player p, String title, Inventory inv) {
        EntityPlayer ep = ((CraftPlayer)p).getHandle();
        Containers con = Containers.GENERIC_9X1;
        if(inv.getSize()==18){
            con = Containers.GENERIC_9X2;
        }else if(inv.getSize()==27){
            con = Containers.GENERIC_9X3;
        }else if(inv.getSize()==36){
            con = Containers.GENERIC_9X4;
        }else if(inv.getSize()==45){
            con = Containers.GENERIC_9X5;
        }else if(inv.getSize()==54){
            con = Containers.GENERIC_9X6;
        }

        PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(ep.activeContainer.windowId, con, new ChatMessage(title));
        ep.playerConnection.sendPacket(packet);
        ep.updateInventory(ep.activeContainer);
    }
}
