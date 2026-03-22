package com.mitchej123.supernova.core;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.mitchej123.supernova.compat.angelica.AngelicaCompat;
import com.mitchej123.supernova.config.SupernovaConfig;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

@IFMLLoadingPlugin.MCVersion("1.7.10")
public class SupernovaCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    private static final Logger LOG = LogManager.getLogger("supernova");

    static {
        try {
            ConfigurationManager.registerConfig(SupernovaConfig.class);
        } catch (ConfigException e) {
            throw new RuntimeException("Failed to register Supernova config", e);
        }

        if (SupernovaConfig.isScalarMode()) {
            LOG.info("Supernova running in SCALAR mode (no RGB color)");
        } else if (SupernovaCore.class.getClassLoader().getResource("com/gtnewhorizons/angelica/api/BlockLightProvider.class") != null) {
            AngelicaCompat.enableColoredLight();
            LOG.info("Enabled Angelica colored light support");
        }
    }

    @Override
    public String getMixinConfig() {
        return "mixins.supernova.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return IMixins.getEarlyMixins(Mixins.class, loadedCoreMods);
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return "com.mitchej123.supernova.compat.ecl.EasyColoredLightsDummyContainer";
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
