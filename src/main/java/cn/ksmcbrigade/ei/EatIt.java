package cn.ksmcbrigade.ei;

import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EatIt.MODID)
public class EatIt {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "ei";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static Config config;

    static {
        try {
            config = new Config(new File("config/ei-config.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public EatIt() {
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Eat It mod loaded.");
    }

    @SubscribeEvent
    public void command(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal("ei-reload").executes(commandContext -> {
            try {
                EatIt.config.load();
                commandContext.getSource().sendSystemMessage(CommonComponents.GUI_DONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }));
    }
}
