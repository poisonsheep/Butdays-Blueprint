# Butday's Blueprint

*A data-driven blueprint recipe engine for modpack authors — inspired by Don't Starve.*

## Overview

Butday's Blueprint is first and foremost a **modpack author's tool**. It provides a complete framework for gating crafting recipes behind blueprint knowledge, with zero Java coding required. Everything — blueprints, their textures, descriptions, unlock conditions, and loot sources — is driven by JSON data.

Blueprints are scattered across the world in chests, dropped by mobs, or reeled in while fishing. Each blueprint a player **reads** (right-click) permanently unlocks one or more crafting recipes. If the blueprint hasn't been learned, the recipe simply won't work — no matter how many ingredients you have.

All blueprints share a single item type, distinguished by NBT data. Adding a new blueprint means dropping in a few JSON files and a texture — no new items to register, no code to touch.

## Getting Started

1. **Get the Anthology** — The first time you sleep, you'll receive the *Blueprint Anthology* and a dreamlike vision. If you ever lose it, sleep again while holding a **Book** to remake one.
2. **Find Blueprints** — Explore the world. Blueprints can be found in structure chests (dungeons, temples, bastions, strongholds, ancient cities, and more), dropped by certain mobs, or obtained from fishing and archaeology.
3. **Read & Learn** — Right-click a blueprint to learn it. The blueprint is consumed and you get a piece of paper back.
4. **Craft** — Learned recipes are now available at the crafting table.
5. **Review** — Open the Anthology to browse all learned blueprints: their descriptions, acquisition hints, and unlocked recipes. With JEI installed, locked recipes will show which blueprints are required.

## Features

- **Highly customizable** — Every blueprint and its textures, descriptions, and recipes are data-driven. Modpack authors can add, remove, or modify everything via JSON.
- **Flexible recipe locking** — One blueprint can unlock multiple recipes; one recipe can require multiple blueprints. Both shaped and shapeless crafting are supported.
- **Blueprint Anthology** — An in-game compendium that tracks learned blueprints and lets you browse their descriptions, unlock conditions, and associated recipes.
- **JEI integration** — Locked recipes show required blueprints directly in the recipe viewer.
- **Full network sync** — Blueprint progress persists across death, dimension change, and reconnect.

## For Modpack Authors: Creating Custom Blueprints

### Locking Recipes

There are two ways to lock a recipe behind blueprints — choose whichever fits your use case:

**Option A: Blueprint Recipe JSON** — Use `butdaysblueprint:blueprint` as the recipe type. Embed the target recipe directly. Supports **shaped** and **shapeless** crafting. Best for mod-added recipes or when you want to redefine a vanilla recipe.

Place under `/data/<your_namespace>/recipes/<name>.json`:

**Single blueprint:**
```json
{
  "_comment": "Example: reading drawing_ability unlocks name tag crafting",
  "type": "butdaysblueprint:blueprint",
  "blueprint": "butdaysblueprint:blueprint/drawing_ability",
  "recipe": {
    "type": "minecraft:crafting_shapeless",
    "ingredients": [
      { "item": "minecraft:paper" },
      { "item": "minecraft:string" },
      { "item": "minecraft:iron_nugget" }
    ],
    "result": { "item": "minecraft:name_tag" }
  }
}
```

**Multiple blueprints (all required):**
```json
{
  "_comment": "Example: atlantis + engineer_manual → trident crafting",
  "type": "butdaysblueprint:blueprint",
  "blueprints": [
    "butdaysblueprint:blueprint/residual_chapter_of_atlantis",
    "butdaysblueprint:blueprint/engineer_manual"
  ],
  "recipe": {
    "type": "minecraft:crafting_shaped",
    "pattern": [" PP", " SP", "S  "],
    "key": {
      "P": { "item": "minecraft:prismarine_shard" },
      "S": { "item": "minecraft:prismarine_crystals" }
    },
    "result": { "item": "minecraft:trident" }
  }
}
```

**Triple blueprint:**
```json
{
  "_comment": "Example: 3 blueprints required to craft wither skeleton skulls",
  "type": "butdaysblueprint:blueprint",
  "blueprints": [
    "butdaysblueprint:blueprint/contaminated_code",
    "butdaysblueprint:blueprint/fading_in_the_shadow",
    "butdaysblueprint:blueprint/burning_wish"
  ],
  "recipe": {
    "type": "minecraft:crafting_shaped",
    "pattern": ["BBB", "BSB", "BBB"],
    "key": {
      "B": { "item": "minecraft:bone" },
      "S": { "item": "minecraft:soul_sand" }
    },
    "result": { "item": "minecraft:wither_skeleton_skull", "count": 3 }
  }
}
```

**Option B: `blueprint_locks.json`** — Lock existing vanilla recipes by ID, without redefining them. Best when you only need to gate vanilla recipes and don't want to touch recipe JSONs.

Place under `/data/<your_namespace>/blueprint_locks.json`:

```json
{
  "locks": {
    "butdaysblueprint:blueprint/engineer_manuscript": [
      "minecraft:iron_pickaxe",
      "minecraft:iron_axe"
    ]
  },
  "multi_locks": [
    {
      "blueprints": [
        "butdaysblueprint:blueprint/contaminated_code",
        "butdaysblueprint:blueprint/fading_in_the_shadow"
      ],
      "recipes": [
        "minecraft:beacon"
      ]
    }
  ]
}
```

- `locks` — single blueprint → list of recipe IDs to lock
- `multi_locks` — multiple blueprints → all must be learned to unlock these recipes

### Blueprint Acquisition (Loot Modifier)

Add to `/data/forge/loot_modifiers/global_loot_modifiers.json`:
```json
{
  "replace": false,
  "entries": [
    "yourmod:my_blueprint_loot"
  ]
}
```

Create the loot modifier `/data/<your_namespace>/loot_modifiers/<name>.json`:
```json
{
  "type": "butdaysblueprint:blueprint_modifier",
  "conditions": [
    {
      "condition": "forge:loot_table_id",
      "loot_table_id": "minecraft:chests/village/village_toolsmith"
    },
    {
      "condition": "minecraft:random_chance",
      "chance": 0.50
    }
  ],
  "item": "butdaysblueprint:blueprint",
  "blueprint": "butdaysblueprint:blueprint/engineer_manuscript"
}
```

- `loot_table_id` — the loot table to inject into (supports mob drops, fishing, archaeology, and chests)
- `chance` — drop probability (0.0 to 1.0)

### Blueprint List & Assets

Register your blueprint in `/assets/<your_namespace>/blueprint/list.json`:
```json
{
  "blueprints": [
    "yourmod:blueprint/my_blueprint"
  ]
}
```

Add localization to `/assets/<your_namespace>/lang/en_us.json`:
```json
{
  "yourmod.blueprint/my_blueprint": "My Blueprint",
  "yourmod.blueprint/my_blueprint.description": "A mysterious blueprint",
  "yourmod.blueprint/my_blueprint.tooltip": "You learned its secrets",
  "yourmod.blueprint/my_blueprint.message": "Found in desert pyramids"
}
```

Add textures under `/assets/<your_namespace>/textures/item/blueprint/` and models under `/assets/<your_namespace>/models/blueprint/`. See the built-in blueprints as templates.
