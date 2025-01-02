package de.srendi.advancedperipherals.common.addons;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

@EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class APAddons {

    public static final String AE2_MODID = "ae2";
    public static final String CURIOS_MODID = "curios";
    public static final String REFINEDSTORAGE_MODID = "refinedstorage";
    public static final String APP_MEKANISTICS_MODID = "appmek";
    public static final String PATCHOULI_MODID = "patchouli";

    public static boolean ae2Loaded;
    public static boolean curiosLoaded;
    public static boolean refinedStorageLoaded;
    public static boolean appMekLoaded;
    public static boolean patchouliLoaded;

    private APAddons() {
    }

    public static void commonSetup() {
        ModList modList = ModList.get();
        ae2Loaded = modList.isLoaded(AE2_MODID);
        curiosLoaded = modList.isLoaded(CURIOS_MODID);
        refinedStorageLoaded = modList.isLoaded(REFINEDSTORAGE_MODID);
        appMekLoaded = modList.isLoaded(APP_MEKANISTICS_MODID);
        patchouliLoaded = modList.isLoaded(PATCHOULI_MODID);

        if (refinedStorageLoaded)
            RefinedStorage.instance = new RefinedStorage();

    }

    @SubscribeEvent
    public static void interModComms(InterModEnqueueEvent event) {
        /*
        if (!curiosLoaded) {
        }

        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("glasses").size(1).build());
        */
    }
}
