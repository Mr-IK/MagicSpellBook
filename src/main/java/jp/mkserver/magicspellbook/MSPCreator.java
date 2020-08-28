package jp.mkserver.magicspellbook;

import jp.mkserver.magicspellbook.inv.InvListener;
import jp.mkserver.magicspellbook.inv.InventoryAPI;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class MSPCreator implements Listener, CommandExecutor {

    HashMap<UUID, EditedSPFile> noEdited = new HashMap<>();
    private MagicSpellBook plugin;

    public MSPCreator(MagicSpellBook plugin){
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player p = (Player) sender;

        if(!p.hasPermission("magicspell.admin")){
            p.sendMessage("Unknown command. Type \"/help\" for help.");
            return true;
        }

        if(args.length==0){
            openAdminGUIMain(p,null);
            return true;
        }
        p.sendMessage(MagicSpellBook.prefix+"§e/msp");
        return true;
    }


    public void openAdminGUIMain(Player p,InventoryAPI inv){
        boolean update = true;
        if(inv==null){
            inv = new InventoryAPI(MagicSpellBook.plugin,MagicSpellBook.prefix+"§5§l管理画面 §9§l―メイン―",27);
            update = false;
        }else{
            inv.allunregistRunnable();
            inv.updateTitle(p,MagicSpellBook.prefix+"§5§l管理画面 §9§l―メイン―");
            inv.clear();
        }
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL,1.0f,1.5f);
        inv.addOriginalListing(new InvListener(plugin, inv){
            @EventHandler
            public void onClick(InventoryClickEvent e){
                if(!super.ClickCheck(e)){
                    return;
                }
                e.setCancelled(true);
            }
            @EventHandler
            public void onClose(InventoryCloseEvent e){
                super.closeCheck(e);
            }
        });
        ItemStack wall = inv.createUnbitem(" ",new String[]{}, Material.BLACK_STAINED_GLASS_PANE,0,false);
        inv.fillInv(wall);
        inv.setItem(11,inv.createUnbitem("§a§l新規作成",new String[]{"§e一から魔法パターンを作成します。"},
                Material.WRITABLE_BOOK,0,false));
        inv.addOriginalListing(new InvListener(plugin, inv){
            @EventHandler
            public void onClick(InventoryClickEvent e){
                if(!super.ClickCheck(e)){
                    return;
                }
                if(e.getSlot()!=11){
                    return;
                }
                p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN,1.0f,1.0f);
                e.setCancelled(true);
                super.inv.regenerateID();
                super.unregister();
                openCreateGUIStart(p);
            }
            @EventHandler
            public void onClose(InventoryCloseEvent e){
                super.closeCheck(e);
            }
        });
        inv.setItem(13,inv.createUnbitem("§b§l編集・削除",new String[]{"§e作成済みの魔法パターンを編集・削除します。"},
                Material.WRITTEN_BOOK,0,false));
        inv.addOriginalListing(new InvListener(plugin, inv){
            @EventHandler
            public void onClick(InventoryClickEvent e){
                if(!super.ClickCheck(e)){
                    return;
                }
                if(e.getSlot()!=13){
                    return;
                }
                p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE,1.0f,1.0f);
                e.setCancelled(true);
                super.inv.regenerateID();
                super.unregister();
                p.closeInventory();
            }
            @EventHandler
            public void onClose(InventoryCloseEvent e){
                super.closeCheck(e);
            }
        });
        inv.setItem(15,inv.createUnbitem("§6§l特殊書見台設定",new String[]{"§e特殊書見台の作成や編集を行います。"},
                Material.LECTERN,0,false));
        inv.addOriginalListing(new InvListener(plugin, inv){
            @EventHandler
            public void onClick(InventoryClickEvent e){
                if(!super.ClickCheck(e)){
                    return;
                }
                if(e.getSlot()!=15){
                    return;
                }
                p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN,1.0f,1.0f);
                e.setCancelled(true);
                super.inv.regenerateID();
                super.unregister();
                p.closeInventory();
            }
            @EventHandler
            public void onClose(InventoryCloseEvent e){
                super.closeCheck(e);
            }
        });
        if(update){
            inv.refresh(p);
        }else{
            inv.openInv(p);
        }
    }

    public void openCreateGUIStart(Player p){
        new AnvilGUI.Builder()
                .onComplete((player, text) -> {           //called when the inventory output slot is clicked
                    boolean exsist = MagicSpellBook.data.fileList.containsKey(text);
                    if(!exsist) {
                        EditedSPFile spFile = new EditedSPFile();
                        spFile.fileName = text;
                        spFile.createSpell();
                        openEditGUIMain(p,null,text);
                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE,1.0f,0.9f);
                        return AnvilGUI.Response.text("none");
                    } else {
                        return AnvilGUI.Response.text("そのidは既に存在しています!");
                    }
                })
                .text("ここにidを入力してください!")     //sets the text the GUI should start with
                .item(new ItemStack(Material.NAME_TAG)) //use a custom item for the first slot
                .title(MagicSpellBook.prefix+"§2§l作成画面")   //set the title of the GUI (only works in 1.14+)
                .plugin(plugin)                 //set the plugin instance
                .open(p);                          //opens the GUI for the player provided
    }

    public void openEditGUIMain(Player p,InventoryAPI inv, String id){
        boolean update = true;
        if(inv==null){
            p.closeInventory();
            inv = new InventoryAPI(MagicSpellBook.plugin,MagicSpellBook.prefix+"§5§l編集画面 §9§l―"+id+"―",27);
            update = false;
        }else{
            inv.allunregistRunnable();
            inv.updateTitle(p,MagicSpellBook.prefix+"§5§l編集画面 §9§l―"+id+"―");
            inv.clear();
        }
        inv.addOriginalListing(new InvListener(plugin, inv){
            @EventHandler
            public void onClick(InventoryClickEvent e){
                if(!super.ClickCheck(e)){
                    return;
                }
                e.setCancelled(true);
            }
            @EventHandler
            public void onClose(InventoryCloseEvent e){
                super.closeCheck(e);
            }
        });
        ItemStack wall = inv.createUnbitem(" ",new String[]{}, Material.BLACK_STAINED_GLASS_PANE,0,false);
        inv.fillInv(wall);
        inv.setItem(10,inv.createUnbitem("§6§l必要アイテム設定",new String[]{"§e起動に必要なアイテムの設定を行います。"},
                Material.CHEST,0,false));
        inv.setItem(4,inv.createUnbitem("§4§l稼働設定",new String[]{"§cこのシステムの稼働状態を切り替えます。","§eクリックで切り替えます。"},
                Material.REDSTONE_BLOCK,0,false));
        inv.addOriginalListing(new InvListener(plugin, inv){
            @EventHandler
            public void onClick(InventoryClickEvent e){
                if(!super.ClickCheck(e)){
                    return;
                }
                if(e.getSlot()!=4){
                    return;
                }
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL,1.0f,0.7f);
                e.setCancelled(true);
                super.inv.regenerateID();
                super.unregister();
                p.closeInventory();
            }
            @EventHandler
            public void onClose(InventoryCloseEvent e){
                super.closeCheck(e);
            }
        });
        inv.addOriginalListing(new InvListener(plugin, inv){
            @EventHandler
            public void onClick(InventoryClickEvent e){
                if(!super.ClickCheck(e)){
                    return;
                }
                if(e.getSlot()!=10){
                    return;
                }
                p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN,1.0f,1.0f);
                e.setCancelled(true);
                super.inv.regenerateID();
                super.unregister();
                p.closeInventory();
            }
            @EventHandler
            public void onClose(InventoryCloseEvent e){
                super.closeCheck(e);
            }
        });
        inv.setItem(11,inv.createUnbitem("§a§l必要・消費経験値設定",new String[]{"§e起動時に必要・消費する経験値の設定を行います。"},
                Material.EXPERIENCE_BOTTLE,0,false));
        inv.addOriginalListing(new InvListener(plugin, inv){
            @EventHandler
            public void onClick(InventoryClickEvent e){
                if(!super.ClickCheck(e)){
                    return;
                }
                if(e.getSlot()!=11){
                    return;
                }
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1.0f,1.0f);
                e.setCancelled(true);
                super.inv.regenerateID();
                super.unregister();
                p.closeInventory();
            }
            @EventHandler
            public void onClose(InventoryCloseEvent e){
                super.closeCheck(e);
            }
        });
        inv.setItem(12,inv.createUnbitem("§3§l詠唱設定",new String[]{"§e起動時の詠唱に関する設定を行います。"},
                Material.PAPER,0,true));
        inv.addOriginalListing(new InvListener(plugin, inv){
            @EventHandler
            public void onClick(InventoryClickEvent e){
                if(!super.ClickCheck(e)){
                    return;
                }
                if(e.getSlot()!=12){
                    return;
                }
                p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN,1.0f,1.0f);
                e.setCancelled(true);
                super.inv.regenerateID();
                super.unregister();
                p.closeInventory();
            }
            @EventHandler
            public void onClose(InventoryCloseEvent e){
                super.closeCheck(e);
            }
        });
        inv.setItem(13,inv.createUnbitem("§d§l演出・音設定",new String[]{"§e起動中の演出や鳴る音の設定を行います。"},
                Material.NOTE_BLOCK,0,false));
        inv.addOriginalListing(new InvListener(plugin, inv){
            @EventHandler
            public void onClick(InventoryClickEvent e){
                if(!super.ClickCheck(e)){
                    return;
                }
                if(e.getSlot()!=13){
                    return;
                }
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP,1.0f,1.0f);
                e.setCancelled(true);
                super.inv.regenerateID();
                super.unregister();
                p.closeInventory();
            }
            @EventHandler
            public void onClose(InventoryCloseEvent e){
                super.closeCheck(e);
            }
        });
        inv.setItem(14,inv.createUnbitem("§4§l完成・失敗時本破壊設定",new String[]{"§e起動失敗・成功時に魔術書を破壊する設定を行います。"},
                Material.BOOK,0,false));
        inv.addOriginalListing(new InvListener(plugin, inv){
            @EventHandler
            public void onClick(InventoryClickEvent e){
                if(!super.ClickCheck(e)){
                    return;
                }
                if(e.getSlot()!=14){
                    return;
                }
                p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN,1.0f,1.0f);
                e.setCancelled(true);
                super.inv.regenerateID();
                super.unregister();
                p.closeInventory();
            }
            @EventHandler
            public void onClose(InventoryCloseEvent e){
                super.closeCheck(e);
            }
        });
        inv.setItem(15,inv.createUnbitem("§b§l完成品設定",new String[]{"§e起動成功時に得られるアイテムの設定を行います。"},
                Material.LIGHT_BLUE_SHULKER_BOX,0,false));
        inv.addOriginalListing(new InvListener(plugin, inv){
            @EventHandler
            public void onClick(InventoryClickEvent e){
                if(!super.ClickCheck(e)){
                    return;
                }
                if(e.getSlot()!=15){
                    return;
                }
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL,1.0f,1.0f);
                e.setCancelled(true);
                super.inv.regenerateID();
                super.unregister();
                p.closeInventory();
            }
            @EventHandler
            public void onClose(InventoryCloseEvent e){
                super.closeCheck(e);
            }
        });
        inv.setItem(16,inv.createUnbitem("§f§lその他設定",new String[]{"§e細かい様々な設定を行います。"},
                Material.COMPARATOR,0,false));
        inv.addOriginalListing(new InvListener(plugin, inv){
            @EventHandler
            public void onClick(InventoryClickEvent e){
                if(!super.ClickCheck(e)){
                    return;
                }
                if(e.getSlot()!=16){
                    return;
                }
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK,1.0f,1.0f);
                e.setCancelled(true);
                super.inv.regenerateID();
                super.unregister();
                p.closeInventory();
            }
            @EventHandler
            public void onClose(InventoryCloseEvent e){
                super.closeCheck(e);
            }
        });
        inv.setItem(18,inv.createUnbitem("§c§l戻る",new String[]{"§e編集を終えて、前のページに戻ります。"},
                Material.DARK_OAK_DOOR,0,false));
        inv.addOriginalListing(new InvListener(plugin, inv){
            @EventHandler
            public void onClick(InventoryClickEvent e){
                if(!super.ClickCheck(e)){
                    return;
                }
                if(e.getSlot()!=18){
                    return;
                }
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK,1.0f,1.2f);
                e.setCancelled(true);
                super.inv.regenerateID();
                super.unregister();
                openAdminGUIMain(p,inv);
            }
            @EventHandler
            public void onClose(InventoryCloseEvent e){
                super.closeCheck(e);
            }
        });
        inv.setItem(26,inv.createUnbitem("§4§l削除",new String[]{"§cこの魔法パターンを削除します。"},
                Material.REDSTONE_BLOCK,0,false));
        inv.addOriginalListing(new InvListener(plugin, inv){
            @EventHandler
            public void onClick(InventoryClickEvent e){
                if(!super.ClickCheck(e)){
                    return;
                }
                if(e.getSlot()!=26){
                    return;
                }
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL,1.0f,1.0f);
                e.setCancelled(true);
                super.inv.regenerateID();
                super.unregister();
                p.closeInventory();
            }
            @EventHandler
            public void onClose(InventoryCloseEvent e){
                super.closeCheck(e);
            }
        });
        if(update){
            inv.refresh(p);
        }else{
            inv.openInv(p);
        }
    }
}
