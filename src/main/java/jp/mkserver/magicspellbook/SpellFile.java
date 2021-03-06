package jp.mkserver.magicspellbook;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SpellFile {

    private boolean power;
    private final String fileName;
    private List<ItemStack> requiredItems;
    private int requiredExp;
    private int takeExp;
    private int breakC;
    private int sbreakC;
    private HashMap<ItemStack,Integer> resultItems;

    public SpellFile(String fileName) {
        //ファイル名から読み込むタイプ
        //※すでに作成が終わっておりプラグイン起動時などに読み込むための初期化メソッド
        this.fileName = fileName;

        FileConfiguration yml = SpellBookFileManager.loadYMLFile(fileName);

        if(yml==null){
            return;
        }

        power = yml.getBoolean("power",false);

        requiredExp = yml.getInt("needExp",0);

        takeExp = yml.getInt("takeExp",0);

        breakC = yml.getInt("breakc",0);

        sbreakC = yml.getInt("sbreakc",0);

        requiredItems = (List<ItemStack>)yml.get("needItems");

        resultItems = new HashMap<>();

        if(yml.contains("resultItems")) {
            for (String key : yml.getConfigurationSection("resultItems").getKeys(false)) {
                ItemStack result = yml.getItemStack("resultItems." + key + ".i");
                int count = yml.getInt("resultItems." + key + ".c", 1);
                resultItems.put(result, count);
            }
        }
    }

    public SpellFile(boolean power,String fileName,List<ItemStack> requiredItems,int requiredExp,int takeExp,HashMap<ItemStack,Integer> resultItems,int breakc,int sbreak) {
        //全データをインプットしファイルを作成するタイプ
        //※ゲーム内から新規にデータを作り、ファイルを作るための初期化メソッド
        this.fileName = fileName;
        if(requiredExp<takeExp){
            return;
        }
        this.power = power;
        this.requiredExp = requiredExp;
        this.takeExp = takeExp;
        this.resultItems = resultItems;
        this.requiredItems = requiredItems;

        FileConfiguration yml = SpellBookFileManager.createYMLFile(fileName);

        if(yml == null){
            return;
        }
        yml.set("power",power);
        yml.set("needExp",requiredExp);
        yml.set("takeExp",takeExp);
        yml.set("needItems",requiredItems);
        yml.set("breakc",breakc);
        yml.set("sbreakc",sbreak);

        int co = 0;
        for(ItemStack item : resultItems.keySet()){
            int count = resultItems.get(item);
            yml.set("resultItems."+co+".i",item);
            yml.set("resultItems."+co+".c",count);
            co++;
        }

        SpellBookFileManager.saveYMLFile(fileName,yml);
    }

    public void saveYML(){
        FileConfiguration yml = SpellBookFileManager.loadYMLFile(fileName);
        if(yml == null){
            return;
        }
        yml.set("power",power);
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

    public void setTakeExp(int exp){
        this.takeExp = exp;
    }

    public void setReqExp(int exp){
        this.requiredExp = exp;
    }

    public boolean isPower() {
        return power;
    }

    public void setPower(boolean power){
        this.power = power;
    }

    public HashMap<ItemStack,Integer> getResultItems(){
        return resultItems;
    }

    public void removeReqItem(ItemStack item){
        requiredItems.remove(item);
    }

    public void addReqItem(ItemStack item){
        removeReqItem(item);
        requiredItems.add(item);
    }

    public void removeResultItem(ItemStack item){
        resultItems.remove(item);
    }

    public void putResultItem(ItemStack item){
        removeResultItem(item);
        resultItems.put(item,0);
    }

    public void putResultItem(ItemStack item, int i){
        removeResultItem(item);
        resultItems.put(item,i);
    }

    public int getBreakC() {
        return breakC;
    }

    public void setBreakC(int breakC) {
        this.breakC = breakC;
    }

    public int getSbreakC() {
        return sbreakC;
    }

    public void setSbreakC(int sbreakC) {
        this.sbreakC = sbreakC;
    }
}
