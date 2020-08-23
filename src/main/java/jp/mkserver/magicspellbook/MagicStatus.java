package jp.mkserver.magicspellbook;

import com.sun.java.swing.action.AlignRightAction;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jp.mkserver.magicspellbook.MagicSpellBook.safeGiveItem;

public class MagicStatus {

    private SpellFile spell;
    private UUID uuid;
    private List<ItemStack> inputedItem;
    private int phase = 0;

    public MagicStatus(SpellFile spell,UUID uuid){
        this.spell = spell;
        this.uuid = uuid;
        inputedItem = new ArrayList<>();
    }

    public UUID getUUID(){
        return uuid;
    }

    public void inputItem(ItemStack item){
        inputedItem.add(item);
    }

    public void releaseItem(){
        safeGiveItem(Bukkit.getPlayer(uuid),inputedItem);
        inputedItem.clear();
    }

    public SpellFile getSpell() {
        return spell;
    }

    public List<ItemStack> getInputedItem() {
        return inputedItem;
    }

    public boolean spellCharge(){
        boolean complete = true;
        List<ItemStack> req = spell.getRequiredItems();
        List<ItemStack> inp = new ArrayList<>(inputedItem);
        for(ItemStack re : req){
            int amore = re.getAmount();
            for(ItemStack ii : inp){
                if(!equalItem(re,ii))continue;
                inp.remove(ii);
                int imor = ii.getAmount();
                if(amore>imor){
                    amore = amore - imor;
                }else if(amore==imor){
                    amore = 0;
                    break;
                }else {
                    ii.setAmount(imor-amore);
                    amore = 0;
                    inp.add(ii);
                    break;
                }
            }
            if(amore!=0){
                complete = false;
                break;
            }
        }

        if(!complete){
            //ここで失敗時処理
            return false;
        }
        inputedItem = inp;
        return true;
    }

    public void pushPhase(){
        phase++;
    }

    public int getPhase() {
        return phase;
    }

    //trueで同一、falseで違う
    public boolean equalItem(ItemStack itema,ItemStack itemb){
        ItemStack a = itema.clone();
        a.setAmount(1);
        ItemStack b = itemb.clone();
        b.setAmount(1);

        return a.toString().equals(b.toString());

    }
}
