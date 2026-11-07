package top.ribs.scguns.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.ribs.scguns.Config;
import top.ribs.scguns.Reference;
import top.ribs.scguns.entity.projectile.ProjectileEntity;
import top.ribs.scguns.init.ModDamageTypes;

/**
 * Optional armor scaling for bullet damage (gameplay.armorDamageScaling, disabled by default).
 * Applied once per hit at hurt time, so it covers every projectile type and beam weapons alike,
 * independent of how many times a projectile runs its own armor calculation.
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ArmorDamageScalingHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        double scaling = Config.COMMON.gameplay.armorDamageScaling.get();
        if (scaling <= 0) return;

        DamageSource source = event.getSource();
        if (!source.is(ModDamageTypes.BULLET)) return;

        LivingEntity target = event.getEntity();
        float armor = target.getArmorValue();
        if (source.getDirectEntity() instanceof ProjectileEntity projectile) {
            // Same bypass rule as ProjectileEntity#calculateArmorBypassDamage: every point bypasses 10% of armor.
            armor *= Math.max(0.0F, 1.0F - projectile.getArmorBypassAmount() / 10.0F);
        }
        if (armor <= 0) return;

        event.setAmount((float) (event.getAmount() / (1.0 + scaling * armor)));
    }
}
