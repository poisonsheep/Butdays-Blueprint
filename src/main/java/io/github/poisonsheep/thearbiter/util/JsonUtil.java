package io.github.poisonsheep.thearbiter.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.poisonsheep.thearbiter.util.serializer.IngredientSerializer;
import io.github.poisonsheep.thearbiter.util.serializer.ItemStackSerializer;
import io.github.poisonsheep.thearbiter.util.serializer.NonNullListSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

//踏马的给我复杂吐了，这都是踏马的什么，为什么作者说得那么简单
public enum JsonUtil {
    INSTANCE;
    public final Gson normal;
    public final Gson pretty;
    public final Gson noExpose;
    JsonUtil() {
        GsonBuilder builder = new GsonBuilder()
                // 关闭 html 转义
                .disableHtmlEscaping()
                // 开启复杂 Map 的序列化
                .enableComplexMapKeySerialization()
                // 注册自定义类型的序列化/反序列化器
                .registerTypeAdapter(Ingredient.class, new IngredientSerializer())
                .registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
                .registerTypeAdapter(NonNullList.class, new NonNullListSerializer());

        // 无视 @Expose 注解的 Gson 实例
        noExpose = builder.create();

        // 要求 *全部*字段都有 @Expose 注解的 Gson 实例
        builder.excludeFieldsWithoutExposeAnnotation();
        normal = builder.create();

        // 输出的字符串漂亮一点的 Gson 实例 -> 输出到 json 文件（例如合成表）的，好看
        builder.setPrettyPrinting();
        pretty = builder.create();
    }
}
