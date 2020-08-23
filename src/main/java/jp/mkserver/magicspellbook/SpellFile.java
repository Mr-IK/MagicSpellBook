package jp.mkserver.magicspellbook;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SpellFile {

    private final String fileName;
    private List<ItemStack> requiredItems;
    private int requiredExp;
    private int takeExp;
    private HashMap<ItemStack,Integer> resultItems;

    public SpellFile(String fileName) {
        //ファイル名から読み込むタイプ
        //※すでに作成が終わっておりプラグイン起動時などに読み込むための初期化メソッド
        this.fileName = fileName;

        FileConfiguration yml = SpellBookFileManager.loadYMLFile(fileName);

        if(yml==null){
            return;
        }

        requiredExp = yml.getInt("needExp",0);

        takeExp = yml.getInt("takeExp",0);

        requiredItems = (List<ItemStack>)yml.get("needItems");

        resultItems = new HashMap<>();

        for(String key : yml.getConfigurationSection("resultItems").getKeys(false)){
            ItemStack result = yml.getItemStack("resultItems."+key+".i");
            int count = yml.getInt("resultItems."+key+".c",1);
            resultItems.put(result,count);
        }
    }

    public SpellFile(String fileName,List<ItemStack> requiredItems,int requiredExp,int takeExp,HashMap<ItemStack,Integer> resultItems) {
        //全データをインプットしファイルを作成するタイプ
        //※ゲーム内から新規にデータを作り、ファイルを作るための初期化メソッド
        this.fileName = fileName;
        if(requiredExp<takeExp){
            return;
        }
        this.requiredExp = requiredExp;
        this.takeExp = takeExp;
        this.resultItems = resultItems;
        this.requiredItems = requiredItems;

        FileConfiguration yml = SpellBookFileManager.createYMLFile(fileName);

        if(yml == null){
            return;
        }

        yml.set("needExp",requiredExp);
        yml.set("takeExp",takeExp);
        yml.set("needItems",requiredItems);

        int co = 0;
        for(ItemStack item : resultItems.keySet()){
            int count = resultItems.get(item);
            yml.set("resultItems."+co+".i",item);
            yml.set("resultItems."+co+".c",count);
            co++;
        }

        SpellBookFileManager.saveYMLFile(fileName,yml);
    }


    public String getFileName() {
        return fileName;
    }

    public List<ItemStack> getRequiredItems() {
        return requiredItems;
    }

    public int getRequiredExp() {
        return requiredExp;
    }

    public ItemStack resultGacha(){
        List<ItemStack> resultRole = new ArrayList<>();
        for(ItemStack item : resultItems.keySet()){
            int count = resultItems.get(item);
            for(int i = 0;i<count;i++){
                resultRole.add(item);
            }
        }
        Collections.shuffle(resultRole);
        return resultRole.get(0);
    }

    public int getTakeExp() {
        return takeExp;
    }
}
