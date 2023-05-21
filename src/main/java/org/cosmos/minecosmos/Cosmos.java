package org.cosmos.minecosmos;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.*;

import static net.minecraft.server.command.CommandManager.*;

public class Cosmos implements ModInitializer {
    public  static final Logger LOGGER = LoggerFactory.getLogger("cosmos");
    @Override
    public void onInitialize() {
        //加入游戏
        ServerPlayConnectionEvents.JOIN.register((player, networkHandler, sender) -> {
            // 获取玩家的名字
            String playerName = player.getPlayer().getGameProfile().getName();
            // 创建欢迎消息
            Text welcomeMessage = Text.of("欢迎, " + playerName + "加入游戏，cosmos已成功载入");
            // 发送欢迎消息给玩家
            sender.sendMessage(welcomeMessage);
        });
        // 注册命令
//        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated,environment) -> {
//            dispatcher.register(CommandManager.literal("pushitemtest")
//                    //.requires(source -> source.hasPermissionLevel(2))
//                    .executes(Cosmos::getItemInHand));
//        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("pushitem")
                .executes(Cosmos::getItemInHand)));

    }
    private static int getItemInHand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        ItemStack itemStack = player.getMainHandStack();

        //请求参数
        Dictionary<String,Object> apiParams =  new Hashtable<>();
        //数量
        apiParams.put("Count",itemStack.getCount());
        var typeName = Registry.ITEM.getKey(itemStack.getItem()).toString();
        //注册名
        apiParams.put("TypeName",typeName);
        NbtCompound nbt = itemStack.getNbt();
        if(nbt!=null){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] bytes;
            try {
                NbtIo.writeCompressed(nbt,outputStream);
                bytes = outputStream.toByteArray();
                //NBT数据
                apiParams.put("NbtData",bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //模拟还原，这里寄了
//        Identifier itemId = new Identifier("itemName");
//        RegistryEntry<Item> itemEntry = Registry.ITEM.get(itemId);
//        ItemStack itemStack = new ItemStack(Items.DIAMOND_SWORD);



//        LOGGER.info(str);
//        context.getSource().sendMessage(Text.literal("调用 /pushitem，不带参数。"));
//        context.getSource().sendMessage(Text.literal(str));


        //int count = itemStack.getCount();
        // TODO: 在这里实现您要执行的代码
        // 获取 ItemStack 的 NBT 标签
        //NbtCompound nbt = itemStack.getNbt();
        return 1;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap();
        result.put("type", this.getType().name());
        if (this.getAmount() != 1) {
            result.put("amount", this.getAmount());
        }

        ItemMeta meta = this.getItemMeta();
        if (!Bukkit.getItemFactory().equals(meta, (ItemMeta)null)) {
            result.put("meta", meta);
        }

        return result;
    }
}
