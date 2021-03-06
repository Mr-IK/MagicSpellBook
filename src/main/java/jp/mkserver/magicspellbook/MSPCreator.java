package jp.mkserver.magicspellbook;

import jp.mkserver.magicspellbook.inv.InvListener;
import jp.mkserver.magicspellbook.inv.InventoryAPI;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static jp.mkserver.magicspellbook.MagicSpellBook.data;
import static jp.mkserver.magicspellbook.MagicSpellBook.prefix;

public class MSPCreator implements Listener, CommandExecutor {

    private final MagicSpellBook plugin;

    public MSPCreator(MagicSpellBook plugin){
        this.plugin = plugin;
    }

    private InventoryAPI get3_9Inv(Player p,InventoryAPI invs,String name){
        if(invs!=null&&invs.getSize()==27){
            invs.regenerateID();
            invs.allunregistRunnable();
            invs.updateTitle(p,name);
            invs.clear();
            invs.addOriginalListing(new InvListener(plugin, invs) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    e.setCancelled(true);
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            ItemStack wall = invs.createUnbitem(" ", new String[]{}, Material.BLACK_STAINED_GLASS_PANE, 0, false);
            invs.fillInv(wall);
            invs.setOpenType(true);
            return invs;
        }else {
            InventoryAPI inv = new InventoryAPI(plugin, name, 27);
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    e.setCancelled(true);
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            ItemStack wall = inv.createUnbitem(" ", new String[]{}, Material.BLACK_STAINED_GLASS_PANE, 0, false);
            inv.fillInv(wall);
            return inv;
        }
    }

    private InventoryAPI get6_9Inv(Player p,InventoryAPI invs,String name){
        if(invs!=null&&invs.getSize()==54){
            invs.regenerateID();
            invs.allunregistRunnable();
            invs.updateTitle(p,name);
            invs.clear();
            invs.addOriginalListing(new InvListener(plugin, invs) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    e.setCancelled(true);
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            invs.setOpenType(true);
            return invs;
        }else {
            InventoryAPI inv = new InventoryAPI(plugin, name, 54);
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    e.setCancelled(true);
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            return inv;
        }
    }

    private InventoryAPI getListupInv(Player p,InventoryAPI invs,String name){
        InventoryAPI inv = get6_9Inv(p,invs,name);
        ItemStack wall = inv.createUnbitem(" ",new String[]{}, Material.BLACK_STAINED_GLASS_PANE,0,false);
        ItemStack back = inv.createUnbitem("§f§l§o前のページへ",new String[]{}, Material.WHITE_STAINED_GLASS_PANE,0,false);
        ItemStack walk = inv.createUnbitem("§f§l§o次のページへ",new String[]{}, Material.WHITE_STAINED_GLASS_PANE,0,false);
        inv.setItems(new int[]{45,46},back);
        inv.setItems(new int[]{47,48,50,51},wall);
        inv.setItems(new int[]{52,53},walk);
        inv.setItem(49,inv.createUnbitem("§c§l戻る",new String[]{"§e前のページに戻ります。"},
                Material.DARK_OAK_DOOR,0,false));
        return inv;
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
            openGUI(p,null,"main",new String[]{});
            // openAdminGUIMain(p,null);
            return true;
        }else if(args.length==1){
            if(data.fileList.containsKey(args[0])){
                ItemStack item = p.getInventory().getItemInMainHand();
                if(item.getType()== Material.AIR){
                    p.sendMessage(prefix+"§cアイテムを手に持ってください。");
                    return true;
                }
                net.minecraft.server.v1_15_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
                NBTTagCompound nbttag = nmsItem.getTag();
                if(nbttag!=null){
                    nbttag.setString("MagicSpellBookData", "MSP:"+args[0]);
                }
                nmsItem.setTag(nbttag);
                ItemStack citem = CraftItemStack.asBukkitCopy(nmsItem);
                p.getInventory().setItemInMainHand(citem);
                p.sendMessage(prefix+"§aセットが完了しました。");
            }else{
                p.sendMessage(prefix+"§c存在しないidです。");
                return true;
            }
        }
        p.sendMessage(MagicSpellBook.prefix+"§e/msp");
        return true;
    }

    public void openGUI(Player p,InventoryAPI inv,String invname,String[] args) {

        if (invname.equalsIgnoreCase("main")) {
            inv = get3_9Inv(p, inv, MagicSpellBook.prefix + "§5§l管理画面 §9§l―メイン―");
            inv.setItem(11, inv.createUnbitem("§a§l新規作成", new String[]{"§e一から魔法パターンを作成します。"},
                    Material.WRITABLE_BOOK, 0, false));
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    if (e.getSlot() != 11) {
                        return;
                    }
                    p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
                    openCreateGUIStart(p);
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            inv.setItem(13, inv.createUnbitem("§b§l編集・削除", new String[]{"§e作成済みの魔法パターンを編集・削除します。"},
                    Material.WRITTEN_BOOK, 0, false));
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    if (e.getSlot() != 13) {
                        return;
                    }
                    p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
                    openGUI(p, inv, "editlist", new String[]{"" + 0});
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            inv.setItem(15, inv.createUnbitem("§6§l特殊書見台設定", new String[]{"§e特殊書見台の作成や編集を行います。"},
                    Material.LECTERN, 0, false));
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    if (e.getSlot() != 15) {
                        return;
                    }
                    p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            inv.openInv(p);
        } else if (invname.equalsIgnoreCase("editmain")) {
            String id = args[0];
            inv = get3_9Inv(p, inv, MagicSpellBook.prefix+"§5§l編集画面 §9§l―"+id+"―");
            if(!MagicSpellBook.data.fileList.containsKey(id)){
                return;
            }
            SpellFile sp = MagicSpellBook.data.fileList.get(id);
            if(sp.isPower()){
                inv.setItem(4,inv.createUnbitem("§a§l稼働設定: 現在ON",new String[]{"§2このシステムの稼働状態を切り替えます。","§eクリックで切り替えます。"},
                        Material.EMERALD_BLOCK,0,false));
            }else{
                inv.setItem(4,inv.createUnbitem("§4§l稼働設定: 現在OFF",new String[]{"§cこのシステムの稼働状態を切り替えます。","§eクリックで切り替えます。"},
                        Material.REDSTONE_BLOCK,0,false));
            }
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
                    if(!sp.isPower()){
                        inv.setItem(4,inv.createUnbitem("§a§l稼働設定: 現在ON",new String[]{"§2このシステムの稼働状態を切り替えます。","§eクリックで切り替えます。"},
                                Material.EMERALD_BLOCK,0,false));
                        sp.setPower(true);
                    }else{
                        inv.setItem(4,inv.createUnbitem("§4§l稼働設定: 現在OFF",new String[]{"§cこのシステムの稼働状態を切り替えます。","§eクリックで切り替えます。"},
                                Material.REDSTONE_BLOCK,0,false));
                        sp.setPower(false);
                    }
                }
                @EventHandler
                public void onClose(InventoryCloseEvent e){
                    super.closeCheck(e);
                }
            });
            inv.setItem(10,inv.createUnbitem("§6§l必要アイテム設定",new String[]{"§e起動に必要なアイテムの設定を行います。"},
                    Material.CHEST,0,false));
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
                    openGUI(p,inv,"editreq",new String[]{id,""+0});
                }
                @EventHandler
                public void onClose(InventoryCloseEvent e){
                    super.closeCheck(e);
                }
            });
            inv.setItem(11,inv.createUnbitem("§a§l必要・消費経験値設定",new String[]{"§e起動時に必要・消費する","§e経験値の設定を行います。"},
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
                    openGUI(p,inv,"editexp",new String[]{id});
                }
                @EventHandler
                public void onClose(InventoryCloseEvent e){
                    super.closeCheck(e);
                }
            });
            inv.setItem(12,inv.createUnbitem("§3§l詠唱設定",new String[]{"§e詠唱に関する設定を行います。"},
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
                }
                @EventHandler
                public void onClose(InventoryCloseEvent e){
                    super.closeCheck(e);
                }
            });
            inv.setItem(14,inv.createUnbitem("§4§l完成・失敗時本破壊設定",new String[]{"§e起動失敗・成功時に","§e魔術書を破壊する設定を行います。"},
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
                    openGUI(p, inv, "editbook", new String[]{id});
                }
                @EventHandler
                public void onClose(InventoryCloseEvent e){
                    super.closeCheck(e);
                }
            });
            inv.setItem(15,inv.createUnbitem("§b§l完成品設定",new String[]{"§e起動成功時に得られる","§eアイテムの設定を行います。"},
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
                    openGUI(p,inv,"editres",new String[]{id,""+0});
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
                }
                @EventHandler
                public void onClose(InventoryCloseEvent e){
                    super.closeCheck(e);
                }
            });
            inv.setItem(18,inv.createUnbitem("§c§l保存して戻る",new String[]{"§e編集を終えて、前のページに戻ります。"},
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
                    sp.saveYML();
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK,1.0f,1.2f);
                    openGUI(p,inv,"main",new String[]{});
                }
                @EventHandler
                public void onClose(InventoryCloseEvent e){
                    super.closeCheck(e);
                }
            });
            inv.setItem(26,inv.createUnbitem("§4§l削除",new String[]{"§cこの魔法パターンを削除します。"},
                    Material.BARRIER,0,false));
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
                    openGUI(p,inv,"confirm",new String[]{id,"delete"});
                }
                @EventHandler
                public void onClose(InventoryCloseEvent e){
                    super.closeCheck(e);
                }
            });
            inv.openInv(p);

        } else if (invname.equalsIgnoreCase("editlist")) {
            int page = Integer.parseInt(args[0]);

            inv = getListupInv(p, inv, MagicSpellBook.prefix + "§5§l管理画面 §9Page:" + page);
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    if (e.getSlot() != 49) {
                        return;
                    }
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
                    openGUI(p, inv, "main", new String[]{});
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });

            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    if (e.getSlot() == 45 || e.getSlot() == 46) {
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        if (page == 0) {
                            return;
                        }
                        openGUI(p, inv, "editlist", new String[]{"" + (page - 1)});
                    } else if (e.getSlot() == 52 || e.getSlot() == 53) {
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        openGUI(p, inv, "editlist", new String[]{"" + (page + 1)});
                    }
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            int i = page * 45;
            int ii = 0;
            for (String ssp : MagicSpellBook.data.fileList.keySet()) {
                if (i != 0) {
                    i--;
                    continue;
                }
                if (ii == 45) {
                    break;
                }
                SpellFile sp = MagicSpellBook.data.fileList.get(ssp);
                if (sp.isPower()) {
                    inv.setItem(ii, inv.createUnbitem("§a§l" + ssp, new String[]{"§eクリックで編集画面へ移行します！"},
                            Material.EMERALD, 0, false));
                } else {
                    inv.setItem(ii, inv.createUnbitem("§c§l" + ssp, new String[]{"§eクリックで編集画面へ移行します！"},
                            Material.REDSTONE, 0, false));
                }
                int finalIi = ii;
                inv.addOriginalListing(new InvListener(plugin, inv) {
                    @EventHandler
                    public void onClick(InventoryClickEvent e) {
                        if (!super.ClickCheck(e)) {
                            return;
                        }
                        if (e.getSlot() != finalIi) {
                            return;
                        }
                        openGUI(p, inv, "editmain", new String[]{ssp});
                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 0.9f);
                    }

                    @EventHandler
                    public void onClose(InventoryCloseEvent e) {
                        super.closeCheck(e);
                    }
                });
                ii++;
            }

            inv.openInv(p);
        } else if (invname.equalsIgnoreCase("editexp")) {
            String id = args[0];
            inv = get3_9Inv(p, inv, MagicSpellBook.prefix + "§5§l経験値設定画面");
            SpellFile sp = MagicSpellBook.data.fileList.get(id);
            inv.setItem(12, inv.createUnbitem("§2§l必要経験値レベル設定",
                    new String[]{"§c起動時に必要な経験値レベルです！", "§c※消費経験値レベルより下にはセットできません", "§e通常/シフトクリックで数値をセット",
                            "§e左: +1(シフトで+10) 右: -1(シフトで-10)", "§e§l現在: " + sp.getRequiredExp()}, Material.ENCHANTING_TABLE, 0, false));
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    if (e.getSlot() != 12) {
                        return;
                    }
                    if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                        if (e.getClick() == ClickType.SHIFT_LEFT) {
                            sp.setReqExp(sp.getRequiredExp() + 10);
                        } else {
                            sp.setReqExp(sp.getRequiredExp() + 1);
                        }
                        openGUI(p, inv, "editexp", new String[]{id});
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.9f);
                    } else if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                        if (e.getClick() == ClickType.SHIFT_RIGHT) {
                            if ((sp.getRequiredExp() - 10) < sp.getTakeExp()) {
                                sp.setReqExp(sp.getTakeExp());
                            } else if ((sp.getRequiredExp() - 10) < 0) {
                                sp.setReqExp(0);
                            } else {
                                sp.setReqExp(sp.getRequiredExp() - 10);
                            }
                        } else {
                            if ((sp.getRequiredExp() - 1) < sp.getTakeExp()) {
                                sp.setReqExp(sp.getTakeExp());
                            } else if ((sp.getRequiredExp() - 1) < 0) {
                                sp.setReqExp(0);
                            } else {
                                sp.setReqExp(sp.getRequiredExp() - 1);
                            }
                        }
                        openGUI(p, inv, "editexp", new String[]{id});
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMITE_HURT, 1.0f, 0.9f);
                    }
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            inv.setItem(14, inv.createUnbitem("§a§l消費経験値レベル設定",
                    new String[]{"§c起動時に消費する経験値レベルです！", "§c※必要経験値レベルより上にはセットできません", "§e通常/シフトクリックで数値をセット",
                            "§e左: +1(シフトで+10) 右: -1(シフトで-10)", "§e§l現在: " + sp.getTakeExp()}, Material.EXPERIENCE_BOTTLE, 0, false));
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    if (e.getSlot() != 14) {
                        return;
                    }
                    if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                        if (e.getClick() == ClickType.SHIFT_LEFT) {
                            if ((sp.getTakeExp() + 10) > sp.getRequiredExp()) {
                                sp.setTakeExp(sp.getRequiredExp());
                            } else {
                                sp.setTakeExp(sp.getTakeExp() + 10);
                            }
                        } else {
                            if ((sp.getTakeExp() + 1) > sp.getRequiredExp()) {
                                sp.setTakeExp(sp.getRequiredExp());
                            } else {
                                sp.setTakeExp(sp.getTakeExp() + 1);
                            }
                        }
                        openGUI(p, inv, "editexp", new String[]{id});
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.9f);
                    } else if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                        if (e.getClick() == ClickType.SHIFT_RIGHT) {
                            if ((sp.getTakeExp() - 10) < 0) {
                                sp.setTakeExp(0);
                            } else {
                                sp.setTakeExp(sp.getTakeExp() - 10);
                            }
                        } else {
                            if ((sp.getTakeExp() - 1) < 0) {
                                sp.setTakeExp(0);
                            } else {
                                sp.setTakeExp(sp.getTakeExp() - 1);
                            }
                        }
                        openGUI(p, inv, "editexp", new String[]{id});
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMITE_HURT, 1.0f, 0.9f);
                    }
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            inv.setItem(18, inv.createUnbitem("§c§l戻る", new String[]{"§e前のページに戻ります。"},
                    Material.DARK_OAK_DOOR, 0, false));
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    if (e.getSlot() != 18) {
                        return;
                    }
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
                    e.setCancelled(true);
                    super.inv.regenerateID();
                    super.unregister();
                    openGUI(p, inv, "editmain", new String[]{id});
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            inv.openInv(p);
        } else if (invname.equalsIgnoreCase("editbook")) {
            String id = args[0];
            inv = get3_9Inv(p, inv, MagicSpellBook.prefix + "§5§l本破壊設定");
            SpellFile sp = MagicSpellBook.data.fileList.get(id);
            inv.setItem(12, inv.createUnbitem("§2§l失敗時破壊確率設定",
                    new String[]{"§c起動失敗時に本が消滅する可能性です！", "§c※100分の〇にセットします。", "§e通常/シフトクリックで数値をセット",
                            "§e左: +1(シフトで+10) 右: -1(シフトで-10)", "§e§l現在: 100分の" + sp.getSbreakC()}, Material.TNT, 0, false));
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    if (e.getSlot() != 12) {
                        return;
                    }
                    if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                        if (e.getClick() == ClickType.SHIFT_LEFT) {
                            if ((sp.getSbreakC() + 10) > 100) {
                                sp.setSbreakC(100);
                            }else {
                                sp.setSbreakC(sp.getSbreakC() + 10);
                            }
                        } else {
                            if ((sp.getSbreakC() + 1) > 100) {
                                sp.setSbreakC(100);
                            }else {
                                sp.setSbreakC(sp.getSbreakC() + 1);
                            }
                        }
                        openGUI(p, inv, "editbook", new String[]{id});
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.9f);
                    } else if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                        if (e.getClick() == ClickType.SHIFT_RIGHT) {
                            if ((sp.getSbreakC() - 10) < 0) {
                                sp.setSbreakC(0);
                            } else {
                                sp.setSbreakC(sp.getSbreakC()- 10);
                            }
                        } else {
                            if ((sp.getSbreakC() - 1) < 0) {
                                sp.setSbreakC(0);
                            } else {
                                sp.setSbreakC(sp.getSbreakC()- 1);
                            }
                        }
                        openGUI(p, inv, "editbook", new String[]{id});
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMITE_HURT, 1.0f, 0.9f);
                    }
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            inv.setItem(14, inv.createUnbitem("§a§l成功時破壊確率設定",
                    new String[]{"§e起動成功時に本が消滅する可能性です！", "§c※100分の〇にセットします。", "§e通常/シフトクリックで数値をセット",
                            "§e左: +1(シフトで+10) 右: -1(シフトで-10)", "§e§l現在: 100分の" + sp.getBreakC()}, Material.ENDER_PEARL, 0, false));
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    if (e.getSlot() != 14) {
                        return;
                    }
                    if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                        if (e.getClick() == ClickType.SHIFT_LEFT) {
                            if ((sp.getBreakC() + 10) > 100) {
                                sp.setBreakC(100);
                            }else {
                                sp.setBreakC(sp.getBreakC() + 10);
                            }
                        } else {
                            if ((sp.getBreakC() + 1) > 100) {
                                sp.setBreakC(100);
                            }else {
                                sp.setBreakC(sp.getBreakC() + 1);
                            }
                        }
                        openGUI(p, inv, "editbook", new String[]{id});
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.9f);
                    } else if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                        if (e.getClick() == ClickType.SHIFT_RIGHT) {
                            if ((sp.getBreakC() - 10) < 0) {
                                sp.setBreakC(0);
                            } else {
                                sp.setBreakC(sp.getBreakC()- 10);
                            }
                        } else {
                            if ((sp.getBreakC() - 1) < 0) {
                                sp.setBreakC(0);
                            } else {
                                sp.setBreakC(sp.getBreakC()- 1);
                            }
                        }
                        openGUI(p, inv, "editbook", new String[]{id});
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMITE_HURT, 1.0f, 0.9f);
                    }
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            inv.setItem(18, inv.createUnbitem("§c§l戻る", new String[]{"§e前のページに戻ります。"},
                    Material.DARK_OAK_DOOR, 0, false));
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    if (e.getSlot() != 18) {
                        return;
                    }
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
                    e.setCancelled(true);
                    super.inv.regenerateID();
                    super.unregister();
                    openGUI(p, inv, "editmain", new String[]{id});
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            inv.openInv(p);
        } else if (invname.equalsIgnoreCase("editreq")) {
            String id = args[0];
            int page = Integer.parseInt(args[1]);
            inv = getListupInv(p, inv, MagicSpellBook.prefix + "§5§l必要アイテム管理 §9Page:" + page);
            if (!MagicSpellBook.data.fileList.containsKey(id)) {
                return;
            }
            SpellFile sp = MagicSpellBook.data.fileList.get(id);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.5f);
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (e.getClickedInventory() == null) {
                        return;
                    }
                    if (super.player == null || !e.getWhoClicked().getUniqueId().equals(super.player)) {
                        return;
                    }
                    if (!checkUnique()) {
                        return;
                    }
                    e.setCancelled(true);
                    if (e.getClickedInventory().equals(e.getWhoClicked().getInventory())) {
                        ItemStack item = p.getInventory().getItem(e.getSlot());
                        if (item == null || item.getType() == Material.AIR) {
                            return;
                        }
                        sp.addReqItem(item);
                        p.getInventory().setItem(e.getSlot(), null);
                        openGUI(p, inv, "editreq", new String[]{id, "" + page});
                    }
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            ItemStack wall = inv.createUnbitem(" ", new String[]{}, Material.BLACK_STAINED_GLASS_PANE, 0, false);
            ItemStack back = inv.createUnbitem("§f§l§o前のページへ", new String[]{}, Material.WHITE_STAINED_GLASS_PANE, 0, false);
            ItemStack walk = inv.createUnbitem("§f§l§o次のページへ", new String[]{}, Material.WHITE_STAINED_GLASS_PANE, 0, false);
            inv.setItems(new int[]{45, 46}, back);
            inv.setItems(new int[]{47, 48, 50, 51}, wall);
            inv.setItems(new int[]{52, 53}, walk);
            inv.setItem(49, inv.createUnbitem("§c§l戻る", new String[]{"§e前のページに戻ります。"},
                    Material.DARK_OAK_DOOR, 0, false));
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    if (e.getSlot() == 45 || e.getSlot() == 46) {
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        if (page == 0) {
                            return;
                        }
                        super.inv.regenerateID();
                        super.unregister();
                        openGUI(p, inv, "editreq", new String[]{id, "" + (page - 1)});
                    } else if (e.getSlot() == 52 || e.getSlot() == 53) {
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        super.inv.regenerateID();
                        super.unregister();
                        openGUI(p, inv, "editreq", new String[]{id, "" + (page + 1)});
                    } else if (e.getSlot() == 49) {
                        sp.saveYML();
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
                        super.inv.regenerateID();
                        super.unregister();
                        p.closeInventory();
                        openGUI(p, inv, "editmain", new String[]{id});
                    }
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            int i = page * 45;
            int ii = 0;
            for (ItemStack item : sp.getRequiredItems()) {
                if (i != 0) {
                    i--;
                    continue;
                }
                if (ii == 45) {
                    break;
                }
                ItemStack citem = item.clone();
                inv.setItem(ii, inv.overhaulItem(citem, null,
                        new String[]{"§cホイールクリックで削除します"}));
                int finalIi = ii;
                inv.addOriginalListing(new InvListener(plugin, inv) {
                    @EventHandler
                    public void onClick(InventoryClickEvent e) {
                        if (!super.ClickCheck(e)) {
                            return;
                        }
                        if (e.getSlot() != finalIi) {
                            return;
                        }
                        super.inv.regenerateID();
                        super.unregister();
                        if (e.getClick() == ClickType.MIDDLE) {
                            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 0.9f);
                            MagicSpellBook.safeGiveItem(p, item);
                            sp.removeReqItem(item);
                            openGUI(p, inv, "editreq", new String[]{id, "" + (page)});
                        }
                    }

                    @EventHandler
                    public void onClose(InventoryCloseEvent e) {
                        super.closeCheck(e);
                    }
                });
                ii++;
            }
            inv.openInv(p);
        } else if (invname.equalsIgnoreCase("editres")) {
            String id = args[0];
            int page = Integer.parseInt(args[1]);
            inv = getListupInv(p, inv, MagicSpellBook.prefix + "§5§l報酬アイテム管理 §9Page:" + page);
            if (!MagicSpellBook.data.fileList.containsKey(id)) {
                return;
            }
            SpellFile sp = MagicSpellBook.data.fileList.get(id);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.5f);
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (e.getClickedInventory() == null) {
                        return;
                    }
                    if (super.player == null || !e.getWhoClicked().getUniqueId().equals(super.player)) {
                        return;
                    }
                    if (!checkUnique()) {
                        return;
                    }
                    e.setCancelled(true);
                    if (e.getClickedInventory().equals(e.getWhoClicked().getInventory())) {
                        ItemStack item = p.getInventory().getItem(e.getSlot());
                        if (item == null || item.getType() == Material.AIR) {
                            return;
                        }
                        sp.putResultItem(item, 0);
                        p.getInventory().setItem(e.getSlot(), null);
                        openGUI(p, inv, "editres", new String[]{id, "" + page});
                    }
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            inv.addOriginalListing(new InvListener(plugin, inv) {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (!super.ClickCheck(e)) {
                        return;
                    }
                    if (e.getSlot() == 45 || e.getSlot() == 46) {
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        if (page == 0) {
                            return;
                        }
                        super.inv.regenerateID();
                        super.unregister();
                        openGUI(p, inv, "editres", new String[]{id, "" + (page - 1)});
                    } else if (e.getSlot() == 52 || e.getSlot() == 53) {
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        super.inv.regenerateID();
                        super.unregister();
                        openGUI(p, inv, "editres", new String[]{id, "" + (page + 1)});
                    } else if (e.getSlot() == 49) {
                        sp.saveYML();
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.2f);
                        super.inv.regenerateID();
                        super.unregister();
                        p.closeInventory();
                        openGUI(p, inv, "editmain", new String[]{id});
                    }
                }

                @EventHandler
                public void onClose(InventoryCloseEvent e) {
                    super.closeCheck(e);
                }
            });
            int i = page * 45;
            int ii = 0;
            for (ItemStack item : sp.getResultItems().keySet()) {
                int count = sp.getResultItems().get(item);
                if (i != 0) {
                    i--;
                    continue;
                }
                if (ii == 45) {
                    break;
                }
                ItemStack citem = item.clone();
                inv.setItem(ii, inv.overhaulItem(citem, null,
                        new String[]{"§cホイールクリックで削除", "§e通常/シフトクリックで確率をセット",
                                "§e左: +1(シフトで+10) 右: -1(シフトで-10)", "§e§l現在: " + count}));
                int finalIi = ii;
                inv.addOriginalListing(new InvListener(plugin, inv) {
                    @EventHandler
                    public void onClick(InventoryClickEvent e) {
                        if (!super.ClickCheck(e)) {
                            return;
                        }
                        if (e.getSlot() != finalIi) {
                            return;
                        }
                        super.inv.regenerateID();
                        super.unregister();
                        if (e.getClick() == ClickType.MIDDLE) {
                            MagicSpellBook.safeGiveItem(p, item);
                            sp.removeResultItem(item);
                            openGUI(p, inv, "editres", new String[]{id, "" + (page)});
                            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 1.0f, 0.9f);
                        } else if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.SHIFT_LEFT) {
                            if (e.getClick() == ClickType.SHIFT_LEFT) {
                                sp.putResultItem(item, count + 10);
                            } else {
                                sp.putResultItem(item, count + 1);
                            }
                            openGUI(p, inv, "editres", new String[]{id, "" + (page)});
                            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.9f);
                        } else if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                            if (e.getClick() == ClickType.SHIFT_RIGHT) {
                                if ((count - 10) < 0) {
                                    sp.putResultItem(item, 0);
                                } else {
                                    sp.putResultItem(item, count - 10);
                                }
                            } else {
                                if ((count - 1) < 0) {
                                    sp.putResultItem(item, 0);
                                } else {
                                    sp.putResultItem(item, count - 1);
                                }
                            }
                            openGUI(p, inv, "editres", new String[]{id, "" + (page)});
                            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMITE_HURT, 1.0f, 0.9f);
                        }
                    }

                    @EventHandler
                    public void onClose(InventoryCloseEvent e) {
                        super.closeCheck(e);
                    }
                });
                ii++;
            }
            inv.openInv(p);
        } else if (invname.equalsIgnoreCase("confirm")) {
            inv = get3_9Inv(p, inv, MagicSpellBook.prefix+"§5§l確認画面");
            String id = args[0];
            String type = args[1];
            if(type.equalsIgnoreCase("delete")){
                ItemStack wall = inv.createUnbitem(" ",new String[]{}, Material.BLACK_STAINED_GLASS_PANE,0,false);
                ItemStack ok = inv.createUnbitem("§4§l"+id+"§c§lを削除する",new String[]{}, Material.BARRIER,0,false);
                ItemStack cancel = inv.createUnbitem("§a§lキャンセル",new String[]{}, Material.GREEN_STAINED_GLASS_PANE,0,false);
                inv.setItems(new int[]{0,1,2,3,9,10,11,12,18,19,20,21},ok);
                inv.setItems(new int[]{4,13,22},wall);
                inv.setItems(new int[]{5,6,7,8,14,15,16,17,23,24,25,26},cancel);
                inv.addOriginalListing(new InvListener(plugin, inv){
                    @EventHandler
                    public void onClick(InventoryClickEvent e){
                        if(!super.ClickCheck(e)){
                            return;
                        }
                        if(!((e.getSlot()>=0&&e.getSlot()<=3)||(e.getSlot()>=9&&e.getSlot()<=12)||(e.getSlot()>=18&&e.getSlot()<=21))){
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK,1.0f,1.2f);
                        MagicSpellBook.data.removeMagicData(id);
                        super.inv.regenerateID();
                        super.unregister();
                        openGUI(p,inv,"main",new String[]{});
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
                        if(!((e.getSlot()>=5&&e.getSlot()<=8)||(e.getSlot()>=14&&e.getSlot()<=17)||(e.getSlot()>=23&&e.getSlot()<=26))){
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK,1.0f,1.2f);
                        super.inv.regenerateID();
                        super.unregister();
                        openGUI(p,inv,"editmain",new String[]{id});
                    }
                    @EventHandler
                    public void onClose(InventoryCloseEvent e){
                        super.closeCheck(e);
                    }
                });
                inv.openInv(p);
            }
        }
    }

    public void openCreateGUIStart(Player p){
        new AnvilGUI.Builder()
                .onComplete((player, text) -> {           //called when the inventory output slot is clicked
                    boolean exsist = MagicSpellBook.data.fileList.containsKey(text);
                    if(!exsist) {
                        EditedSPFile spFile = new EditedSPFile();
                        spFile.fileName = text;
                        data.fileList.put(text,spFile.createSpell());
                        openGUI(p,null,"editmain",new String[]{text});
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
}
