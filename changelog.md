Major internal update to put the mod on 26.x versions.

## Additions
- Added new loot tables for spawner loot.
  - These copy the vanilla jungle temple, simple dungeon, and woodland mansion tables that were used previously.
  - These are here for modpack authors to edit.
- Added gamerule to control the maximum number of actions a spawner can stack.
- Spawner actions now use custom registries instead of being hard-coded. Addons can now easily add custom spawner actions.

## Changes
- Spawners can now stack the same passive action.
- All NBT reading has been changed, spawners now store their custom data within a `cursed-spawners` object in memory.