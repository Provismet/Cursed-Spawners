<div align="center">

# Cursed Spawners
[![](https://img.shields.io/jitpack/version/com.github.Provismet/Cursed-Spawners?style=flat-square&logo=jitpack&color=F6F6F6)](https://jitpack.io/#Provismet/Cursed-Spawners)
[![](https://img.shields.io/modrinth/dt/kpbD49Kv?style=flat-square&logo=modrinth&color=F6F6F6)](https://modrinth.com/mod/cursed-spawners)
[![](https://img.shields.io/curseforge/dt/1083491?style=flat-square&logo=curseforge&color=F6F6F6)](https://www.curseforge.com/minecraft/mc-mods/cursed-spawners)

</div>

This is a mod made specifically to make mob spawners (not trial spawners) significantly harder to deal with via multiple avenues of pain.

Spawners will randomly select up to 7 actions that they can perform. These include periodic effects such as knocking away players,
and active responses, such as performing actions when broken.

Additionally, spawners have the chance to become mimics when broken. The Spawner Mimic is a fast and heavily armoured mob that will continue
to spawn other mobs from the original spawner nbt.

Lastly, the mining speed of spawners has been modified.

All three major features can be controlled by gamerules and NBT data. Allowing builders and datapack makers to intentionally produce dungeons
that exploit these features. For spawners with no NBT from this mod, they will generate it themselves on the next tick. 

### Action Intervals
Spawners can chance to perform actions on an interval.
- Knock away nearby players.
- Heal nearby hostile mobs.
- Grant movement speed to nearby hostile mobs.

These actions will generate with a random effect radius, interval, and efficacy. All three are indicated by a shrinking floor particle.

### Breaking Actions
There are two types of effect that can trigger when a spawner is broken.
- Reforge
  - Prevents the spawner from being broken. It is consumed when called and acts like an extra life for the spawner. 
- Break
  - Triggers when the spawner is properly broken. 

Reforge and break actions pull from the same pool of possibilities:
- Summon vexes.
- Summon silverfish.
- Blind and poison the player.

## Gamerules
- `spawnerActionChance`
  - The chance for spawners to be granted an ability.
  - Rolls the chance up to 7 times.
- `spawnerMimicChance`
  - The chance for a spawner to become a mimic when broken.
  - This is overridden by the NBT data on the spawner if set to a value above 0.
  - This rate applies to spawners with the default chance of -1.
- `spawnerMiningSpeedModifier`
  - Modifies the mining speed for breaking a spawner.
  - Lower values make spawners harder to break.
