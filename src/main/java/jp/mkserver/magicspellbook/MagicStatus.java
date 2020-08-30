package jp.mkserver.magicspellbook;

import jp.mkserver.magicspellbook.util.ExpManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static jp.mkserver.magicspellbook.MagicSpellBook.plugin;
import static jp.mkserver.magicspellbook.MagicSpellBook.safeGiveItem;

public class MagicStatus {

    private SpellFile spell;
    private UUID uuid;
    private List<ItemStack> inputedItem;
    private int phase = 0;
    private boolean getExp = false;

    public MagicStatus(SpellFile spell,UUID uuid){
        this.spell = spell;
        this.uuid = uuid;
        inputedItem = new ArrayList<>();
    }

    public UUID getUUID(){
        return uuid;
    }

    public void inputItem(ItemStack item) {
        inputedItem.add(item);
    }

    public void resetInputItem(){
        inputedItem.clear();
    }

    public void releaseItem(){
        if(Bukkit.getPlayer(uuid)!=null){
            safeGiveItem(Bukkit.getPlayer(uuid),inputedItem);
            inputedItem.clear();
        }
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
            List<ItemStack> inps = new ArrayList<>(inp);
            for(ItemStack ii : inps){
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

        if(complete){
            //ここで成功時処理
            inputedItem = inp;
            return true;
        }
        return false;
    }

    public boolean chargeExp(boolean execute) {
        Player p = Bukkit.getPlayer(uuid);
        if(p!=null) {
            int reqExp = spell.getRequiredExp();
            int nowLv = p.getLevel();
            if(nowLv<reqExp){
                return false;
            }
            if(execute){
                int takexp = calcTakeExp(p,spell.getTakeExp());
                int ta10 = takexp/20;
                int tt = takexp%20;
                new BukkitRunnable() {
                    int i = 0;
                    @Override
                    public void run() {
                        if(i>=20){
                            cancel();
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,0.5f,0.8f);
                        if(i==0){
                            ExpManager.setTotalExperience(p,ExpManager.getTotalExperience(p) - (ta10+tt));
                        }else {
                            ExpManager.setTotalExperience(p, ExpManager.getTotalExperience(p) - ta10);
                        }
                        i++;
                    }
                }.runTaskTimerAsynchronously(plugin,0,2);
            }
            return true;
        }else{
            return false;
        }
    }

    private int calcTakeExp(Player p,int takeLv){
        int nowLv = p.getLevel();
        int goLv = nowLv-takeLv;
        int takeExp = 0;
        for(int i = 0;i<takeLv;i++){
            int r = ExpManager.getExpAtLevel(goLv+i);
            takeExp = (takeExp+r);
        }
        return takeExp;
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
