package jp.mkserver.magicspellbook;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class MSPData implements Listener {

    HashMap<String,SpellFile> fileList;
    MagicSpellBook plugin;

    public MSPData(){
        fileList = new HashMap<>();

        for(String name : SpellBookFileManager.getFolderInFileList()){
            SpellFile sp = new SpellFile(name);
            fileList.put(name,sp);
        }

        plugin = MagicSpellBook.plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    

}
