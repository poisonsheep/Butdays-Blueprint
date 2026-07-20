# Butday's Blueprint

*Unlock the recipe with blueprints — like the game Don't Starve.*

## Overview

Blueprints are scattered across the world in chests and loot tables. Each blueprint you **read** (right-click) permanently unlocks a recipe. The twist: the recipe simply won't work until the blueprint is learned — no matter how many ingredients you have.

18 themed blueprints are hidden in different structures, from the Toolsmith's house to the End City, each unlocking vanilla or custom recipes. Collect them all in your **Blueprint Anthology**.

## Getting Started

1. **Get the Anthology** — Sleep with a **Book** in your hand. You'll receive the *Blueprint Anthology* and the *Dream Again* advancement.
2. **Find Blueprints** — Explore the world. Blueprints are hidden in chests: dungeons, temples, bastions, strongholds, ancient cities, and more.
3. **Read & Learn** — Right-click a blueprint to learn it. The blueprint is consumed and you get a piece of paper back.
4. **Craft** — Learned recipes are now available at the crafting table.
5. **Review** — Open the Anthology to browse all learned blueprints and their associated recipes. Use `JEI` to see which blueprints a locked recipe requires.

> **Tip**: Hold `Shift` while viewing the Anthology to see only learned blueprints.

## Features

- **18 unique blueprints**, each with lore-rich flavor text (English + Chinese)
- **Recipe locking** — Crafting recipes are gated behind blueprint knowledge
- **Multi-blueprint recipes** — Some recipes require learning 2+ blueprints
- **Blueprint Anthology** — In-game compendium to browse learned blueprints and recipes
- **JEI integration** — Locked recipes show required blueprints in the recipe viewer
- **Full network sync** — Blueprint progress persists across death, dimension change, and reconnect
- **Data-driven** — Everything is configurable via JSON — perfect for modpack authors

## For Modpack Authors: Creating Custom Blueprints

### Step 1: Blueprint Recipe (JSON)

Create a blueprint recipe under `/data/<your_namespace>/recipes/<name>.json`:

**Single blueprint unlock:**
```json
{
  "type": "butdaysblueprint:blueprint",
  "blueprint": "butdaysblueprint:blueprint/engineer_manuscript",
  "recipe": {
    "type": "minecraft:crafting_shaped",
    "pattern": ["III", "I I", "III"],
    "key": { "I": { "item": "minecraft:iron_ingot" } },
    "result": { "item": "minecraft:anvil" }
  }
}
```

**Multi-blueprint unlock (all must be learned):**
```json
{
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

> The inner `"recipe"` can use any vanilla or modded recipe type (`crafting_shaped`, `crafting_shapeless`, `smithing`, etc.).

### Step 2: Lock Vanilla Recipes (Optional)

For vanilla recipes you don't want to redefine, use `blueprint_locks.json`:

```json
{
  "locks": {
    "butdaysblueprint:blueprint/engineer_manuscript": [
      "minecraft:iron_pickaxe",
      "minecraft:iron_axe"
    ],
    "butdaysblueprint:blueprint/contaminated_code": [
      "minecraft:ender_eye"
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
- `multi_locks` — array of blueprint sets → list of recipes that require ALL specified blueprints

### Step 3: Blueprint Acquisition (Loot Modifier)

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
    { "condition": "forge:loot_table_id", "loot_table_id": "minecraft:chests/village/village_toolsmith" },
    { "condition": "minecraft:random_chance", "chance": 0.50 }
  ],
  "item": "butdaysblueprint:blueprint",
  "blueprint": "yourmod:blueprint/my_blueprint"
}
```

- `loot_table_id` — the vanilla/modded loot table to inject into
- `chance` — drop probability (0.0 to 1.0)
- `blueprint` — the blueprint ID to spawn

### Step 4: Blueprint List

Register your blueprint in `/assets/<your_namespace>/blueprint/list.json`:
```json
{
  "blueprints": [
    "yourmod:blueprint/my_blueprint"
  ]
}
```

> The base mod's list is at `assets/butdaysblueprint/blueprint/list.json`. Modpack authors can create their own — all loaded lists are merged at runtime.

### Step 5: Localization

Add to `/assets/<your_namespace>/lang/en_us.json`:
```json
{
  "yourmod.blueprint/my_blueprint": "My Blueprint",
  "yourmod.blueprint/my_blueprint.description": "A mysterious blueprint",
  "yourmod.blueprint/my_blueprint.tooltip": "You learned its secrets",
  "yourmod.blueprint/my_blueprint.message": "Found in desert pyramids"
}
```

### Step 6: Assets

Add textures and models for your blueprint item. See the existing blueprints under `assets/butdaysblueprint/textures/item/blueprint/` and `assets/butdaysblueprint/models/blueprint/` as templates.

---

## Built-in Blueprints

| Blueprint | Location | Unlocks |
|-----------|----------|---------|
| Engineer Manuscript | Village Toolsmith | Iron tools & armor |
| Engineer Manual | Stronghold Library | Diamond tools & armor |
| NASA Tech Blueprint | Stronghold / End City | Redstone components |
| Contaminated Manuscript | Dungeon / Mansion / Evoker | Enchanting & brewing |
| Twisted Codex | Crafted (3× Manuscript + Leather) | Eye of Ender |
| Fading in the Shadow | Wither | Soul items + Beacon |
| The Lost Chapter of Atlantis | Elder Guardian / Fishing / Archaeology | Conduit & ocean items |
| Burning Wish | Blaze / Bastion | Fire charge & magma cream |
| Drawing Ability | Shipwreck | Painting & decorations |
| Music Producer | Ancient City | Jukebox & note block |
| Sweet Blueprint | Crafted (Blueprint + Honey) | Cake & sweets |
| Key to the City | Village Church / Raid | Villager workstations |
| Bio Program | Jungle Temple | Lead & hay bale |
| The Sands of Time | Desert Pyramid / Archaeology | Clock |
| Wormholes on Paper | Ender Dragon | Ender Chest |
| Kitty Letter | (Modpack-defined) | — |
| Fake Imperial Edict | (Modpack-defined) | — |
| Unknown Blueprint | — | Clears learned blueprints |

---

## Events

For KubeJS / CraftTweaker integration, the following Forge events are exposed:

- `ReadEvent` — Fired when a player reads a blueprint. Cancel to prevent learning.
- `LearnEvent` — Fired after a blueprint is successfully learned.
- `BlueprintEvent` — General blueprint lifecycle event.

## Credits

- Developed by **butday**
- Inspired by Don't Starve's blueprint system
- Built on Minecraft Forge 1.20.1

## License

MIT
