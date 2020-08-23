package jp.mkserver.magicspellbook;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreatedSPFile {

    public String fileName;
    public List<ItemStack> requiredItems = new ArrayList<>();
    public int requiredExp = 0;
    public int takeExp = 0;
    public HashMap<ItemStack,Integer> resultItems = new HashMap<>();

    public SpellFile createSpell(){
        return new SpellFile(fileName,requiredItems,requiredExp,takeExp,resultItems);
    }
}
