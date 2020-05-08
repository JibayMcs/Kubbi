package fr.leviathanstudio.engine.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.leviathanstudio.engine.GameEngine;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ZeAmateis
 */
public class Translation {
    private static List<TranslationEntry> translations = new ArrayList<>();
    private final AssetManager assetManager;
    private final Gson gson;
    private final Type translationType;
    private Map<Asset, Locale> translationsAssets = new HashMap<>();

    private Locale selectedLocale;

    public Translation(AssetManager assetManager, Locale selectedLocale) {
        this.assetManager = assetManager;
        this.selectedLocale = selectedLocale;

        TypeToken<?> typeToken = new TypeToken<TranslationEntry>() {
        };
        this.translationType = typeToken.getType();
        this.gson = new GsonBuilder().registerTypeAdapter(translationType, new TranslationTypeAdapter()).create();
    }

    public void registerTranslations(Locale localeIn) {
        this.translationsAssets.put(
                new Asset(
                        String.format("%s_%s", localeIn.getLanguage(), localeIn.getCountry().toLowerCase()),
                        String.format("lang/%s_%s.json", localeIn.getLanguage(), localeIn.getCountry().toLowerCase())
                ),
                localeIn
        );

        this.translationsAssets.forEach((asset, locale) -> {
            this.assetManager.registerAsset(asset);
        });

        this.parseTranslation(localeIn);
    }

    private void parseTranslation(Locale localeIn) {
        this.translationsAssets.forEach((asset, locale) -> {
            if (locale.equals(localeIn))
                try (Reader reader = new FileReader(this.assetManager.getAssetPath(asset).toFile())) {
                    System.out.println(this.assetManager.getAssetPath(asset).toFile().getAbsolutePath());
                    TranslationEntry translation = this.gson.fromJson(reader, this.translationType);
                    this.translations.add(translation);
                } catch (IOException ex) {
                    GameEngine.LOGGER.throwing(ex);
                }
        });
    }

    public Locale getSelectedLocale() {
        return selectedLocale;
    }

    public void setSelectedLocale(Locale selectedLocale) {
        this.selectedLocale = selectedLocale;
    }

    /**
     * Formatting
     */

    public static String format(String key, Object... objects) {
        AtomicReference<String> value = new AtomicReference<>();
        translations.forEach(translationEntry -> {
            if (translationEntry.getKey().equals(key)) {
                value.set(String.format(translationEntry.getValue(), objects));
            }
        });
        return value.get();
    }

    public static String format(String key) {
        AtomicReference<String> value = new AtomicReference<>();
        translations.forEach(translationEntry -> {
            if (translationEntry.getKey().equals(key))
                value.set(translationEntry.getValue());
        });
        return value.get();
    }

}
