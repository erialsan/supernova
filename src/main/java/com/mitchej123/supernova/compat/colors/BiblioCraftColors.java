package com.mitchej123.supernova.compat.colors;

import com.mitchej123.supernova.Supernova;
import com.mitchej123.supernova.api.LightColors;

/**
 * BiblioCraft -- lamps, lanterns, and writing desks (including wood addon variants).
 */
public final class BiblioCraftColors {

    public static void register() {
        int count = 0;

        // BiblioCraft core
        count += ColorRegistrationHelper.registerBlock("BiblioCraft", "BiblioLamp", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock("BiblioCraft", "BiblioIronLamp", LightColors.DYE_WHITE);
        count += ColorRegistrationHelper.registerBlock("BiblioCraft", "BiblioLantern", 15, 14, 13);
        count += ColorRegistrationHelper.registerBlock("BiblioCraft", "BiblioIronLantern", 15, 14, 13);
        count += ColorRegistrationHelper.registerBlock("BiblioCraft", "BiblioDesk", 7, 7, 5);

        // BiblioWoodsBoP
        count += ColorRegistrationHelper.registerBlock("BiblioWoodsBoP", "BiblioWooddesk", 7, 7, 5);

        // BiblioWoodsForestry
        count += ColorRegistrationHelper.registerBlock("BiblioWoodsForestry", "BiblioWoodFstdesk", 7, 7, 5);
        count += ColorRegistrationHelper.registerBlock("BiblioWoodsForestry", "BiblioWoodFstdesk2", 7, 7, 5);

        // BiblioWoodsNatura
        count += ColorRegistrationHelper.registerBlock("BiblioWoodsNatura", "BiblioWooddesk", 7, 7, 5);

        if (count > 0) {
            Supernova.LOG.info("Registered {} BiblioCraft light colors", count);
        }
    }

    private BiblioCraftColors() {}
}
