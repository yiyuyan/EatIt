package cn.ksmcbrigade.ei.mixin;

import cn.ksmcbrigade.ei.EatIt;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.io.IOException;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Shadow @Final @Nullable private FoodProperties foodProperties;

    @Inject(method = "isEdible",at = @At("RETURN"),cancellable = true)
    public void edible(CallbackInfoReturnable<Boolean> cir) throws IOException {
        Item item = (Item) ((Object)this);
        if(!EatIt.config.excluded(item)){
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getFoodProperties",at = @At("RETURN"),cancellable = true)
    public void fakeProperties(CallbackInfoReturnable<FoodProperties> cir) throws IOException {
        Item item = (Item) ((Object)this);
        if(!EatIt.config.excluded(item) && (this.foodProperties==null || EatIt.config.data.has(ForgeRegistries.ITEMS.getKey(item).toString()))){
            FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(EatIt.config.food2(item)).saturationMod(EatIt.config.food(item));
            if(EatIt.config.always(item)) builder.alwaysEat();
            cir.setReturnValue(builder.build());
        }
    }

    @Redirect(method = "finishUsingItem",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"))
    public ItemStack eatAfter(LivingEntity instance, Level p_21067_, ItemStack p_21068_){
        if(instance instanceof ServerPlayer serverPlayer){
            CommandSourceStack stack = new CommandSourceStack(serverPlayer.server, serverPlayer.position(), Vec2.ZERO, serverPlayer.serverLevel(), 4, serverPlayer.getName().getString(), serverPlayer.getName(), serverPlayer.server, null);
            for (String command : EatIt.config.getCommands(p_21068_.getItem())) {
                serverPlayer.server.getCommands().performPrefixedCommand(stack,command);
            }
        }
        return instance.eat(p_21067_, p_21068_);
    }
}
