package org.cosmos.minecosmos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;

public class Cosmos implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("cosmos");

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

    private static final Gson gson = new GsonBuilder().create();

    private static int getItemInHand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        ItemStack itemStack = player.getMainHandStack();

        //请求参数
        JsonObject apiParams = new JsonObject();
        //数量
        apiParams.addProperty("Count", itemStack.getCount());
        var typeName = Registry.ITEM.getKey(itemStack.getItem()).get().getValue().toString();
        //注册名
        apiParams.addProperty("TypeName", typeName);
        NbtCompound nbt = itemStack.getNbt();
        apiParams.addProperty("NbtData", nbt != null ? nbt.asString() : "");

        //模拟还原，这里寄了
//        Identifier itemId = new Identifier("itemName");
//        RegistryEntry<Item> itemEntry = Registry.ITEM.get(itemId);
//        ItemStack itemStack = new ItemStack(Items.DIAMOND_SWORD);


        LOGGER.info("push item, api params:" + apiParams);
        context.getSource().sendMessage(Text.literal("调用 /pushitem"));

        // TODO 改为多线程
        String s = Nets.postRequest("http://cosmos.api.xintianyuehui.cn/farbric-item", apiParams.toString());

        JsonObject object = gson.fromJson(s, JsonObject.class);
        String nbtData = object.get("data").getAsJsonObject().get("nbtData").getAsString();
        NbtCompound data = StringNbtReader.parse(nbtData);
        // TODO 处理请求

        // TODO 删除物品
        return 1;
    }

    private static int asItem(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO 发送请求
        JsonObject apiParams = new JsonObject();
        Item item = Registry.ITEM.get(new Identifier(apiParams.get("TypeName").getAsString()));
        ItemStack stack = new ItemStack(item, apiParams.get("Count").getAsInt());
        stack.setNbt(StringNbtReader.parse(apiParams.get("NbtData").getAsString()));

        // TODO 将物品给玩家
        return 1;
    }
}
