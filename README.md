# TabooBackPack
_RPG_ 服适用的背包插件！

---------
###### 如何使用 _TabooLib_ 创建一个背包物品
```yaml
小背包:
  material: CHEST
  name: '&f小背包'
  lore:
  - ''
  - '&8&o放到背包第三行最后一格'
  - '&8&o右键打开'
  nbt:
    # 背包行数
    backpack-size: 1
 ```
插件会自动生成 "_backpack-uid_" 标签，当然你也可以添加自定义标签，例如 **“公共背包”**？
```yaml
公共背包:
  material: CHEST
  name: '&f公共云端背包'
  lore:
  - ''
  - '&8&o放到背包第三行最后一格'
  - '&8&o右键打开'
  nbt:
    # 背包行数
    backpack-size: 3
    # 背包序列号
    backpack-uid: "public-backpack"
```
