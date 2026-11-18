package com.provismet.cursedspawners.imixin;

import com.provismet.cursedspawners.action.ActionGroup;

public interface IMixinBaseSpawner {
    default ActionGroup cursed_spawners$getActionGroup () {
        return ActionGroup.DEFAULT;
    }

    default void cursed_spawners$setActionGroup (ActionGroup group) {

    }
}
