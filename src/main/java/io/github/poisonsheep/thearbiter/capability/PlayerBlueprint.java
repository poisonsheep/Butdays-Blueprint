package io.github.poisonsheep.thearbiter.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.ArrayList;
import java.util.List;

public class PlayerBlueprint {
    private List<String> blueprints = new ArrayList<>();
    public List<String> getBlueprints() {
        return blueprints;
    }
    public void setBlueprints(List<String> blueprints) {
        this.blueprints = blueprints;
    }
    public void addBluePrints(String blueprint){
        if (!this.blueprints.contains(blueprint)) {
            // 如果没有包含，那么才添加到列表中
            this.blueprints.add(blueprint);
        }
    }
    public void saveNBTData(CompoundTag tag){
        ListTag listTag = new ListTag();
        for (String blueprint : blueprints) {
            listTag.add(StringTag.valueOf(blueprint));
        }
        // 把ListTag对象放到nbt标签中，使用"blueprints"作为键名
        tag.put("blueprints", listTag);
    }
    public void loadNBTData(CompoundTag tag) {
        // 创建一个空的字符串列表，用来存储玩家的蓝图列表
        List<String> blueprints = new ArrayList<>();
        // 从nbt标签中获取ListTag对象，使用"blueprints"作为键名
        ListTag listTag = tag.getList("blueprints", 8); // 8表示字符串类型的nbt标签
        // 遍历ListTag对象，把每个StringTag对象转换成字符串，并添加到字符串列表中
        for (int i = 0; i < listTag.size(); i++) {
            blueprints.add(listTag.getString(i));
        }
        // 把字符串列表设置为玩家的蓝图列表
        setBlueprints(blueprints);
    }
    public void copyFrom(PlayerBlueprint oldStore) {
        this.blueprints = oldStore.blueprints;
    }
}
