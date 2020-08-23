package jp.mkserver.magicspellbook;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Lectern;
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

import static jp.mkserver.magicspellbook.MagicSpellBook.prefix;
import static jp.mkserver.magicspellbook.MagicSpellBook.safeGiveItem;

public class MSPData implements Listener {

    HashMap<String,SpellFile> fileList;

    HashMap<Location,MagicStatus> mgStats; //ブロック内のデータ
    HashMap<UUID,Location> mgsPlayers; //何らかの処理をしているか、その操作先は

    MagicSpellBook plugin;

    final ItemStack close = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
    final ItemStack wall = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
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
            if(e.getSlot()>=27&&e.getSlot()<=30){
                p.closeInventory();
            }else if(e.getSlot()>=32&&e.getSlot()<=35){
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
            p.sendMessage(prefix + "§a準備完了。右クリックして魔法を起動してください。");
            stats.pushPhase();
        }else if(stats.getPhase()==0){
            p.sendMessage(prefix + "§a再度クリックすると投入するアイテムを変更できます！");
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

        Lectern lec = (Lectern) block;

        ItemStack book = lec.getInventory().getItem(0);
        if(book==null)return;

        ItemMeta meta = book.getItemMeta();
        if(meta==null)return;
        if(meta.getLore()==null)return;

        if(meta.getLore().get(meta.getLore().size()-1).startsWith("§kMSP:"))return;

        String dataid = meta.getLore().get(meta.getLore().size()-1).replace("§kMSP:","");
        if(fileList.containsKey(dataid))return;
        SpellFile file = fileList.get(dataid);
        //マジックステータス内にこのブロックは何らかの処理が走っていると確認された(続きを行う)
        if(mgStats.containsKey(block.getLocation())){
            e.setCancelled(true);
            MagicStatus mgs = mgStats.get(block.getLocation());
            if (mgs.getUUID() != p.getUniqueId()) {
                p.sendMessage(prefix + "§c他人の魔法への干渉はできません！");
                return;
            }

            //フェーズ0: アイテム投入
            if(mgs.getPhase()==0){
                openMagicInv(p,mgs);
                return;
            //フェーズ2: 実行
            }else if(mgs.getPhase()==2) {
                if(!mgs.chargeExp()){
                    p.sendMessage(prefix + "§c発動に必要な経験値が足りないようです。アイテムを返却します。");
                    mgs.releaseItem();
                    removeMagic(block.getLocation(),p);
                    return;
                }

                if(!mgs.spellCharge()){
                    p.sendMessage(prefix + "§c発動に必要な材料が足りないようです。アイテムを返却します。");
                    mgs.releaseItem();
                    removeMagic(block.getLocation(),p);
                    return;
                }

                safeGiveItem(p,mgs.getSpell().resultGacha());
                mgs.releaseItem();
                removeMagic(block.getLocation(),p);
                p.sendMessage(prefix + "§c発動に成功しました。術式を停止します。");
                return;
            }

        //マジックステータス内にブロックの処理は確認できなかった(新しく開始する)
        }else {
            //かつ、他の魔法を行使中ではないなら
            if (mgsPlayers.containsKey(p.getUniqueId())) {
                p.sendMessage(prefix + "§c魔法の行使中は他の魔法を開始できません！");
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

            openMagicInv(p,mgs);
        }
    }

    public void openMagicInv(Player p,MagicStatus mgs){
        Inventory inv = Bukkit.createInventory(null,36,prefix);
        inv.setItem(27,close);
        inv.setItem(28,close);
        inv.setItem(29,close);
        inv.setItem(30,close);
        inv.setItem(31,wall);
        inv.setItem(32,start);
        inv.setItem(33,start);
        inv.setItem(34,start);
        inv.setItem(35,start);

        for(ItemStack item:mgs.getInputedItem()){
            inv.addItem(item);
        }

        p.openInventory(inv);
    }

    public void removeMagic(Location loc,Player p){
        mgStats.remove(loc);
        mgsPlayers.remove(p.getUniqueId());
    }
}
