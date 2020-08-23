package jp.mkserver.magicspellbook;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpellBookFileManager {

    private static final String folderName = File.separator + "spellFiles";
    private static final File pluginFolder = MagicSpellBook.plugin.getDataFolder();

    //フォルダの存在を確認しない場合は作成
    public static void checkFolderExist() {
        File ff = new File(pluginFolder,"");
        if (!ff.exists()) {
            ff.mkdir();
        }
        File f = new File(pluginFolder, folderName);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    public static String removeYML(String filename){
        if(filename.substring(0,1).equalsIgnoreCase(".")){
            return filename;
        }

        int point = filename.lastIndexOf(".");
        if (point != -1) {
            filename =  filename.substring(0, point);
        }
        return filename;
    }

    public static List<String> getFolderInFileList() {

        List<String> list = new ArrayList<>();

        File folder = new File(pluginFolder, folderName);

        if(!folder.exists()){
            folder.mkdir();
        }

        File[] files = folder.listFiles();  // (a)
        if (files != null) {
            for (File f : files) {
                if (f.isFile()){  // (c)
                    String filename = f.getName();
                    list.add(removeYML(filename));
                }
            }
        }

        return list;
    }

    public static FileConfiguration loadYMLFile(String name){
        checkFolderExist();

        File f = new File(pluginFolder, folderName+ File.separator + name + ".yml");
        if (f.exists()) {
            return YamlConfiguration.loadConfiguration(f);
        }else{
            return null;
        }
    }

    public static FileConfiguration createYMLFile(String name){
        checkFolderExist();

        File f = new File(pluginFolder, folderName+ File.separator + name + ".yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
                return YamlConfiguration.loadConfiguration(f);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

    public static int saveYMLFile(String name, FileConfiguration data){
        checkFolderExist();

        File f = new File(pluginFolder, folderName+ File.separator + name + ".yml");

        f.delete();
        if (!f.exists()) {
            try {
                data.save(f);
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return -2;
            }
        }else{
            return -1;
        }
    }
}
