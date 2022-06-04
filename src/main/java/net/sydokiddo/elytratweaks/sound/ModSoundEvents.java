package net.sydokiddo.elytratweaks.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.sydokiddo.elytratweaks.ElytraTweaks;

public class ModSoundEvents {

// Sound Registry:

    public static final SoundEvent ENTITY_PLAYER_ELYTRA_CLOSE = registerSoundEvent();

    private static SoundEvent registerSoundEvent() {
        Identifier id = new Identifier(ElytraTweaks.MOD_ID, "player.elytra.close");
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }

    public static void registerSounds() {
        System.out.println("Registering Sounds for " + ElytraTweaks.MOD_ID);
    }
}