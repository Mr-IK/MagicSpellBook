package jp.mkserver.magicspellbook;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static jp.mkserver.magicspellbook.MagicSpellBook.*;

public class MSPCommand implements CommandExecutor {

    HashMap<UUID, EditedSPFile> nowCreated = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player p = (Player) sender;

        if(args.length==0){
            p.sendMessage(prefix+"§a/msp takeexp [数字] : 消費経験値レベル(消費あり)を設定します reqがこれ以下の場合は強制で同一に変わります");
            p.sendMessage(prefix+"§a/msp reqexp [数字] : 必要経験値レベル(消費なし)を設定します takeと同じかそれ以上限定です。");
            p.sendMessage(prefix+"§a/msp addresult [数字] : 錬成結果に入るアイテムを手にもって実行すると結果に入ります。数字を大きくすると確率が上がります");
            p.sendMessage(prefix+"§a/msp reqItem : 素材とするアイテムを手にもって実行すると必要素材に入ります。");
            p.sendMessage(prefix+"§a/msp create [name] : 作成を確定し、データを保存します。失敗時はクリアされます。");
            p.sendMessage(prefix+"§c/msp cancel : 作成モードを離れます。データは破棄されます。");
            return true;
        }else if(args.length==1){
            String a = args[0];
            if(a.equals("cancel")){
                if(!nowCreated.containsKey(p.getUniqueId())){
                    p.sendMessage(prefix+"§c現在作成中ではありません。");
                    return true;
                }
                p.sendMessage(prefix+"§a作成をキャンセルしました。");
                nowCreated.remove(p.getUniqueId());
                return true;
            }else if(a.equals("reqitem")){
                if(!nowCreated.containsKey(p.getUniqueId())){
                    p.sendMessage(prefix+"§c現在作成中ではありません。");
                    return true;
                }
                EditedSPFile sp = nowCreated.get(p.getUniqueId());
                sp.requiredItems.add(p.getInventory().getItemInMainHand());
                p.sendMessage(prefix+"§a素材アイテムを追加しました。");
                return true;
            }
            if(data.fileList.containsKey(a)){
                ItemStack item = p.getInventory().getItemInMainHand();
                if(item.getType()== Material.AIR){
                    p.sendMessage(prefix+"§cアイテムを手に持ってください。");
                    return true;
                }
                ItemMeta meta = item.getItemMeta();
                List<String> lore = meta.getLore();
                if(lore==null){
                    lore = new ArrayList<>();
                }
                lore.add("§kMSP:"+a);
                meta.setLore(lore);
                item.setItemMeta(meta);
                p.getInventory().setItemInMainHand(item);
                p.sendMessage(prefix+"§aloreのセットが完了しました。");
            }else{
                p.sendMessage(prefix+"§c存在しないidです。");
                return true;
            }
        }else if(args.length==2){
            String a = args[0];
            if(a.equalsIgnoreCase("create")){
                if(nowCreated.containsKey(p.getUniqueId())){
                    SpellFile sp = nowCreated.get(p.getUniqueId()).createSpell();
                    if(sp==null){
                        p.sendMessage(prefix+"§c作成に失敗しました。");
                        nowCreated.remove(p.getUniqueId());
                        return true;
                    }
                    data.fileList.put(sp.getFileName(),sp);
                    p.sendMessage(prefix+"§a作成を確定しました。");
                    nowCreated.remove(p.getUniqueId());
                    return true;
                }
                if(data.fileList.containsKey(args[1])){
                    p.sendMessage(prefix+"§c存在するidです。");
                    return true;
                }
                EditedSPFile spFile = new EditedSPFile();
                spFile.fileName = args[1];
                nowCreated.put(p.getUniqueId(),spFile);
                p.sendMessage(prefix+"§a魔術作成モードに移行しました。");
                p.sendMessage(prefix+"§a作成時のヘルプは /msp を参照してください。");
                return true;
            }else if(a.equalsIgnoreCase("takeexp")){
                if(!nowCreated.containsKey(p.getUniqueId())){
                    p.sendMessage(prefix+"§c現在作成中ではありません。");
                    return true;
                }
                int req = Integer.parseInt(args[1]);
                EditedSPFile sp = nowCreated.get(p.getUniqueId());
                sp.takeExp = req;
                if(sp.requiredExp<=req){
                    sp.requiredExp = req;
                    p.sendMessage(prefix+"§aReqExpを"+req+"に設定しました");
                }
                p.sendMessage(prefix+"§aTakeExpを"+req+"に設定しました");
                return true;
            }else if(a.equalsIgnoreCase("reqexp")){
                if(!nowCreated.containsKey(p.getUniqueId())){
                    p.sendMessage(prefix+"§c現在作成中ではありません。");
                    return true;
                }
                int req = Integer.parseInt(args[1]);
                EditedSPFile sp = nowCreated.get(p.getUniqueId());
                if(sp.takeExp>req){
                    p.sendMessage(prefix+"§cReqExpはTakeExpと同一かそれ以上の数値が必要です!");
                    return true;
                }
                sp.requiredExp = req;
                p.sendMessage(prefix+"§aReqExpを"+req+"に設定しました");
                return true;
            }else if(a.equalsIgnoreCase("addresult")){
                if(!nowCreated.containsKey(p.getUniqueId())){
                    p.sendMessage(prefix+"§c現在作成中ではありません。");
                    return true;
                }
                int req = Integer.parseInt(args[1]);
                EditedSPFile sp = nowCreated.get(p.getUniqueId());
                sp.resultItems.put(p.getInventory().getItemInMainHand(),req);
                p.sendMessage(prefix+"§aリザルトアイテムを追加しました。");
                return true;
            }
        }
        return true;
    }
}
