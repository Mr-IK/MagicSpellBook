package jp.mkserver.magicspellbook;

import org.bukkit.plugin.java.JavaPlugin;

import static jp.mkserver.magicspellbook.SpellBookFileManager.checkFolderExist;

public final class MagicSpellBook extends JavaPlugin {

    public static MagicSpellBook plugin;
    public static MSPData data;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        checkFolderExist();
        data = new MSPData();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
