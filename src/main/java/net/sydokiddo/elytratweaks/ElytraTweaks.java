package net.sydokiddo.elytratweaks;

import net.fabricmc.api.ModInitializer;
import net.sydokiddo.elytratweaks.sound.ModSoundEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElytraTweaks implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");
	public static final String MOD_ID = "elytratweaks";
	public static boolean elytraThicknessFix = true;
	public static boolean isRocketing = false;
	public static double rollSmoothing = 0.85;
	public static double wingPower = 1.25;

	@Override
	public void onInitialize() {

		// Registry

		ModSoundEvents.registerSounds();

		LOGGER.info("Thank you for downloading Elytra Tweaks! :)");
	}
}