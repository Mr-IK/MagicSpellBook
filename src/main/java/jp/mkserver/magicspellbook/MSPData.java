package jp.mkserver.magicspellbook;

import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Lectern;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static jp.mkserver.magicspellbook.MagicSpellBook.*;

public class MSPData implements Listener {

    HashMap<String,SpellFile> fileList;

    HashMap<Location,MagicStatus> mgStats; //ブロック内のデータ
    HashMap<UUID,Location> mgsPlayers; //何らかの処理をしているか、その操作先は

    MagicSpellBook plugin;

    final ItemStack close = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
    final ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
    final ItemStack start = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

    public MSPData(){
        fileList = new HashMap<>();
        mgStats = new HashMap<>();
        mgsPlayers = new HashMap<>();

        for(String name : SpellBookFileManager.getFolderInFileList()){
            SpellFile sp = new SpellFile(name);
            fileList.put(name,sp);
        }

        plugin = MagicSpellBook.plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);

        ItemMeta clme = close.getItemMeta();
        clme.setDisplayName("§e§lインベントリを閉じる");
        List<String> clmelo = new ArrayList<>();
        clmelo.add("§aインベントリを閉じます。");
        clmelo.add("§a台をクリックすると再度インベントリが開けます！");
        clme.setLore(clmelo);
        close.setItemMeta(clme);

        ItemMeta came = cancel.getItemMeta();
        came.setDisplayName("§e§l作成をキャンセルする");
        List<String> camelo = new ArrayList<>();
        camelo.add("§c作成をキャンセルします。※やり直しはできません！");
        came.setLore(camelo);
        cancel.setItemMeta(came);

        ItemMeta stme = start.getItemMeta();
        stme.setDisplayName("§e§l合成を始める");
        List<String> stmelo = new ArrayList<>();
        stmelo.add("§cインベントリを閉じます。※二度と開けません！");
        stmelo.add("§a台をクリックすると合成が開始されます！");
        stme.setLore(stmelo);
        start.setItemMeta(stme);
    }

    @EventHandler
    public void onINVClick(InventoryClickEvent e){
        Player p = (Player)e.getWhoClicked();

        if (!mgsPlayers.containsKey(p.getUniqueId()))return;
        if(e.getClickedInventory()==p.getInventory())return;
        if(!e.getView().getTitle().equals(prefix))return;

        if(e.getSlot()>=27&&e.getSlot()<=35){
            e.setCancelled(true);
            if(e.getSlot()>=27&&e.getSlot()<=29) {
                p.closeInventory();
            }else if(e.getSlot()>=30&&e.getSlot()<=32){
                MagicStatus mgs = mgStats.get(mgsPlayers.get(p.getUniqueId()));
                p.closeInventory();
                mgs.releaseItem();
                removeMagic(mgsPlayers.get(p.getUniqueId()),p);
                p.sendActionBar("§c§l§o術式がキャンセルされました。アイテムを返却します――");
            }else if(e.getSlot()>=33&&e.getSlot()<=35){
                MagicStatus stats = mgStats.get(mgsPlayers.get(p.getUniqueId()));
                stats.pushPhase();
                p.closeInventory();
            }
        }
    }

    @EventHandler
    public void onINVClose(InventoryCloseEvent e){
        Player p = (Player)e.getPlayer();

        if (!mgsPlayers.containsKey(p.getUniqueId()))return;
        if(e.getInventory()==p.getInventory())return;
        if(!e.getView().getTitle().equals(prefix))return;

        MagicStatus stats = mgStats.get(mgsPlayers.get(p.getUniqueId()));
        for(int i = 0;i<27;i++){
            ItemStack item =e.getInventory().getItem(i);
            if(item==null)continue;
            stats.inputItem(item);
        }

        if(stats.getPhase()==1){
            for(Player as : Bukkit.getOnlinePlayers()){
                as.playSound(mgsPlayers.get(p.getUniqueId()), Sound.BLOCK_ENCHANTMENT_TABLE_USE,2.0f,0.5f);
            }
            mgsPlayers.get(p.getUniqueId()).getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE,mgsPlayers.get(p.getUniqueId()),50);
            p.sendActionBar("§a§l§o準備完了。右クリックして魔法を起動してください――");
            stats.pushPhase();
        }
    }


    @EventHandler
    public void onItemInput(PlayerInteractEvent e){

        Player p = e.getPlayer();

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)return;
        if (e.getHand() != EquipmentSlot.HAND)return;

        Block block = e.getClickedBlock();
        if(block==null)return;

        if(block.getType()!= Material.LECTERN)return;

        Lectern lec = (Lectern) block.getState();

        ItemStack book = lec.getInventory().getItem(0);
        if(book==null)return;

        net.minecraft.server.v1_15_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(book);
        NBTTagCompound nbttag = nmsItem.getTag();
        if(nbttag==null)return;

        String dataid = nbttag.getString("MagicSpellBookData").replaceFirst("§kMSP:","");

        if(!fileList.containsKey(dataid)){
            if(mgStats.containsKey(block.getLocation())) {
                MagicStatus mgs = mgStats.get(block.getLocation());
                if (mgs.getUUID() != p.getUniqueId()) {
                    return;
                }
                e.setCancelled(true);
                mgs.releaseItem();
                removeMagic(mgsPlayers.get(p.getUniqueId()),p);
                p.sendActionBar("§c§l§o術式がキャンセルされました。アイテムを返却します――");
            }
            return;
        }
        SpellFile file = fileList.get(dataid);

        if(!file.isPower()){
            if(mgStats.containsKey(block.getLocation())) {
                MagicStatus mgs = mgStats.get(block.getLocation());
                if (mgs.getUUID() != p.getUniqueId()) {
                    return;
                }
                e.setCancelled(true);
                mgs.releaseItem();
                removeMagic(mgsPlayers.get(p.getUniqueId()),p);
                p.sendActionBar("§c§l§o術式がキャンセルされました。アイテムを返却します――");
            }
            return;
        }

        //マジックステータス内にこのブロックは何らかの処理が走っていると確認された(続きを行う)
        if(mgStats.containsKey(block.getLocation())){
            e.setCancelled(true);
            MagicStatus mgs = mgStats.get(block.getLocation());
            if (mgs.getUUID() != p.getUniqueId()) {
                p.sendActionBar("§c§l§o他の魔法への干渉は許されません――");
                return;
            }

            //フェーズ0: アイテム投入
            if(mgs.getPhase()==0){
                for(Player as : Bukkit.getOnlinePlayers()){
                    as.playSound(block.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN,2.0f,0.5f);
                }
                block.getWorld().spawnParticle(Particle.PORTAL,block.getLocation(),80);
                openMagicInv(p,mgs);
                return;
            //フェーズ2: 実行
            }else if(mgs.getPhase()==2) {
                if(!mgs.chargeExp(false)){
                    for(Player as : Bukkit.getOnlinePlayers()){
                        as.playSound(block.getLocation(), Sound.ENTITY_GENERIC_EXPLODE,2.0f,0.5f);
                    }
                    block.getWorld().spawnParticle(Particle.SPELL_WITCH,block.getLocation(),20);
                    p.sendActionBar("§c§l§oこの術式を起動するには経験値不足です。アイテムを返却します――");
                    mgs.releaseItem();
                    removeMagic(block.getLocation(),p);
                    return;
                }

                if(!mgs.spellCharge()){
                    for(Player as : Bukkit.getOnlinePlayers()){
                        as.playSound(block.getLocation(), Sound.ENTITY_GENERIC_EXPLODE,2.0f,0.5f);
                    }
                    block.getWorld().spawnParticle(Particle.SMOKE_LARGE,block.getLocation(),20);
                    p.sendActionBar("§c§l§oこの術式を起動するのには素材不足です。アイテムを返却します――");
                    mgs.releaseItem();
                    removeMagic(block.getLocation(),p);
                    return;
                }
                mgs.chargeExp(true);
                safeGiveItem(p,mgs.getSpell().resultGacha());
                mgs.releaseItem();
                removeMagic(block.getLocation(),p);
                for(Player as : Bukkit.getOnlinePlayers()){
                    as.playSound(block.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE,2.0f,1.5f);
                }
                block.getWorld().spawnParticle(Particle.END_ROD,block.getLocation(),50);
                p.sendActionBar("§a§l§o術式の起動に成功。");
                return;
            }

        //マジックステータス内にブロックの処理は確認できなかった(新しく開始する)
        }else {
            //かつ、他の魔法を行使中ではないなら
            if (mgsPlayers.containsKey(p.getUniqueId())) {
                p.sendActionBar("§c§o魔法は並行処理することができない…");
                e.setCancelled(true);
                return;
            }

            //かつ、プレイヤーがスニーク状態じゃない場合
            if (p.isSneaking())return;

            e.setCancelled(true);

            //マジックステータス及び魔法チェックを作成
            MagicStatus mgs = new MagicStatus(file,p.getUniqueId());
            mgStats.put(block.getLocation(),mgs);
            mgsPlayers.put(p.getUniqueId(),block.getLocation());

            for(Player as : Bukkit.getOnlinePlayers()){
                as.playSound(block.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN,2.0f,0.5f);
            }
            block.getWorld().spawnParticle(Particle.PORTAL,block.getLocation(),80);
            openMagicInv(p,mgs);
        }
    }

    public void openMagicInv(Player p,MagicStatus mgs){
        Inventory inv = Bukkit.createInventory(null,36,prefix);
        inv.setItem(27,close);
        inv.setItem(28,close);
        inv.setItem(29,close);
        inv.setItem(30,cancel);
        inv.setItem(31,cancel);
        inv.setItem(32,cancel);
        inv.setItem(33,start);
        inv.setItem(34,start);
        inv.setItem(35,start);

        for(ItemStack item:mgs.getInputedItem()){
            inv.addItem(item);
        }

        mgs.resetInputItem();

        p.openInventory(inv);
    }

    public void removeMagic(Location loc,Player p){
        mgStats.remove(loc);
        mgsPlayers.remove(p.getUniqueId());
    }

    public void removeMagicData(String id){
        if(!data.fileList.containsKey(id)){
            return;
        }
        SpellFile spf = data.fileList.get(id);
        if(spf.isPower()){
            return;
        }
        data.fileList.remove(id);
        SpellBookFileManager.removeYMLFile(id);
    }
}
