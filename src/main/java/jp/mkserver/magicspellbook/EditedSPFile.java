package jp.mkserver.magicspellbook;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditedSPFile {

    public String fileName;
    public List<ItemStack> requiredItems = new ArrayList<>();
    public String eisyou;
    public boolean power = false;
    public int requiredExp = 0;
    public int takeExp = 0;
    public HashMap<ItemStack,Integer> resultItems = new HashMap<>();

    public SpellFile createSpell(){
        return new SpellFile(power,fileName,requiredItems,requiredExp,takeExp,resultItems);
    }
}
