package club.rainbowkitty.kalic0re.common.text;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Translatable text for a server-side mod, each piece carrying its English as fallback.
 *
 * <p>Translation happens on the client, and a vanilla client never holds a server-side mod's lang
 * file, so a plain {@link Component#translatable} shows it the raw key. This reads the mod's own
 * {@code assets/<modId>/lang/en_us.json} once and hands each entry along as the component's
 * fallback: a client with the pack (Polymer resource pack, or the mod installed) localizes, every
 * other client reads the English. English lives only in that file; code names keys.
 *
 * <p>A key the file lacks is logged once as an error and shown as the key itself, which is what
 * the client would have shown anyway.
 */
public final class FallbackText {
    private static final Logger LOGGER = LoggerFactory.getLogger("kalic0re/text");

    private final String modId;
    private final Map<String, String> english;
    private final Set<String> reportedMissing = ConcurrentHashMap.newKeySet();

    private FallbackText(String modId, Map<String, String> english) {
        this.modId = modId;
        this.english = english;
    }

    /**
     * Loads {@code assets/<modId>/lang/en_us.json} from that mod's own jar; hold one per mod.
     *
     * @throws IllegalStateException when the mod is not loaded or the file is missing or
     *         unreadable, since text without its English is a packaging mistake worth stopping
     *         startup for
     */
    public static FallbackText load(String modId) {
        String file = "assets/" + modId + "/lang/en_us.json";
        ModContainer mod = FabricLoader.getInstance().getModContainer(modId)
                .orElseThrow(() -> new IllegalStateException("No mod '" + modId + "' loaded"));
        Path path = mod.findPath(file).filter(Files::isRegularFile)
                .orElseThrow(() -> new IllegalStateException(modId + " has no " + file));
        Map<String, String> english = new HashMap<>();
        try (Reader reader = Files.newBufferedReader(path)) {
            for (Map.Entry<String, JsonElement> entry
                    : JsonParser.parseReader(reader).getAsJsonObject().entrySet()) {
                english.put(entry.getKey(), entry.getValue().getAsString());
            }
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Cannot read " + modId + "'s " + file, e);
        }
        return new FallbackText(modId, Map.copyOf(english));
    }

    /**
     * A translatable component for {@code key}, with the file's English as fallback and
     * {@code args} formatted into whichever of the two the client ends up showing.
     */
    public MutableComponent of(String key, Object... args) {
        return Component.translatableWithFallback(key, fallback(key), args);
    }

    /** Whether the lang file has {@code key}. */
    public boolean has(String key) {
        return english.containsKey(key);
    }

    // The English for a key; a missing key is reported once and shows as itself.
    private String fallback(String key) {
        String text = english.get(key);
        if (text == null && reportedMissing.add(key)) {
            LOGGER.error("{} has no English for '{}' in its lang/en_us.json", modId, key);
        }
        return text != null ? text : key;
    }
}
