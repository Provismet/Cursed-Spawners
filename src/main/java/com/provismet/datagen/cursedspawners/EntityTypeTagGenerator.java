package com.provismet.datagen.cursedspawners;

import com.provismet.cursedspawners.registries.CSEntityTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import java.util.concurrent.CompletableFuture;

public class EntityTypeTagGenerator extends FabricTagProvider.EntityTypeTagProvider {
    public EntityTypeTagGenerator (FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags (HolderLookup.Provider wrapperLookup) {
        this.valueLookupBuilder(TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("extra-damage-enchantments", "arcane")))
            .add(CSEntityTypes.SPAWNER_MIMIC);

        this.valueLookupBuilder(EntityTypeTags.NO_ANGER_FROM_WIND_CHARGE)
            .add(CSEntityTypes.SPAWNER_MIMIC);

        this.valueLookupBuilder(EntityTypeTags.WITHER_FRIENDS)
            .add(CSEntityTypes.SPAWNER_MIMIC);

        this.valueLookupBuilder(EntityTypeTags.ILLAGER_FRIENDS)
            .add(CSEntityTypes.SPAWNER_MIMIC);
    }
}
