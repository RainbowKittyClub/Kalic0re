package club.rainbowkitty.kalic0re.common.config;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

// One kind of setting value: how it is checked, parsed from a command or a forcing meta value,
// carried as contract JSON, saved in the player attachment, and shown in a message.
sealed interface SettingKind<T> {
    // The kind's name in a declaration.
    public String name();

    // The value, or an error naming what was expected when it is out of bounds.
    public DataResult<T> check(T value);

    // Whether value is within bounds, without allocating; reads ask this every time.
    public default boolean accepts(T value) {
        return true;
    }

    // Parses the text a player typed or a meta value carries.
    public DataResult<T> parse(String text);

    // The contract's JSON form of a value.
    public JsonElement toJson(T value);

    // A contract JSON value read back, bounds checked.
    public DataResult<T> fromJson(JsonElement json);

    // The value as a command reply shows it, as the hub does: its contract JSON, except a
    // choice's bare option id.
    public default String show(T value) {
        return toJson(value).toString();
    }

    // What a command suggests for the value.
    public List<String> suggestions();

    // The attachment codec, bounds checked on decode.
    public default Codec<T> codec() {
        return baseCodec().validate(this::check);
    }

    // The unchecked codec under codec().
    public Codec<T> baseCodec();

    // A true/false switch.
    record Toggle() implements SettingKind<Boolean> {
        @Override
        public String name() {
            return "toggle";
        }

        @Override
        public DataResult<Boolean> check(Boolean value) {
            return DataResult.success(value);
        }

        @Override
        public DataResult<Boolean> parse(String text) {
            return switch (text) {
                case "true" -> DataResult.success(true);
                case "false" -> DataResult.success(false);
                default -> DataResult.error(() -> "expected true or false");
            };
        }

        @Override
        public JsonElement toJson(Boolean value) {
            return new JsonPrimitive(value);
        }

        @Override
        public DataResult<Boolean> fromJson(JsonElement json) {
            return json.isJsonPrimitive() && json.getAsJsonPrimitive().isBoolean()
                    ? DataResult.success(json.getAsBoolean())
                    : DataResult.error(() -> "expected true or false");
        }

        @Override
        public List<String> suggestions() {
            return List.of("true", "false");
        }

        @Override
        public Codec<Boolean> baseCodec() {
            return Codec.BOOL;
        }
    }

    // A whole number in [min, max].
    record Range(int min, int max) implements SettingKind<Integer> {
        @Override
        public String name() {
            return "range";
        }

        @Override
        public DataResult<Integer> check(Integer value) {
            return accepts(value) ? DataResult.success(value)
                    : DataResult.error(() -> expected());
        }

        @Override
        public boolean accepts(Integer value) {
            return value >= min && value <= max;
        }

        @Override
        public DataResult<Integer> parse(String text) {
            try {
                return check(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                return DataResult.error(() -> expected());
            }
        }

        @Override
        public JsonElement toJson(Integer value) {
            return new JsonPrimitive(value);
        }

        @Override
        public DataResult<Integer> fromJson(JsonElement json) {
            if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isNumber()) {
                return DataResult.error(() -> expected());
            }
            double number = json.getAsDouble();
            return number == Math.rint(number) && number >= min && number <= max
                    ? DataResult.success((int) number) : DataResult.error(() -> expected());
        }

        @Override
        public List<String> suggestions() {
            return List.of(Integer.toString(min), Integer.toString(max));
        }

        @Override
        public Codec<Integer> baseCodec() {
            return Codec.INT;
        }

        // The refusal text for a value out of range or not a number.
        private String expected() {
            return "expected a whole number from " + min + " to " + max;
        }
    }

    // One constant of an enum, named by its lower-cased constant name.
    record Choice<E extends Enum<E>>(Class<E> type) implements SettingKind<E> {
        // The option id of a constant.
        static String id(Enum<?> constant) {
            return constant.name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String name() {
            return "choice";
        }

        @Override
        public DataResult<E> check(E value) {
            return DataResult.success(value);
        }

        @Override
        public DataResult<E> parse(String text) {
            String typed = text.toLowerCase(Locale.ROOT);
            Optional<E> match = Arrays.stream(type.getEnumConstants())
                    .filter(constant -> id(constant).equals(typed)).findFirst();
            return match.map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "expected one of "
                            + String.join(", ", ids())));
        }

        @Override
        public JsonElement toJson(E value) {
            return new JsonPrimitive(id(value));
        }

        @Override
        public DataResult<E> fromJson(JsonElement json) {
            return json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()
                    ? parse(json.getAsString())
                    : DataResult.error(() -> "expected an option id");
        }

        @Override
        public String show(E value) {
            return id(value);
        }

        @Override
        public List<String> suggestions() {
            return ids();
        }

        // Every option id, in constant order.
        public List<String> ids() {
            return Arrays.stream(type.getEnumConstants()).map(Choice::id).toList();
        }

        @Override
        public Codec<E> baseCodec() {
            return Codec.STRING.comapFlatMap(this::parse, Choice::id);
        }
    }

    // A string of at most maxLength characters.
    record Text(int maxLength) implements SettingKind<String> {
        @Override
        public String name() {
            return "text";
        }

        @Override
        public DataResult<String> check(String value) {
            return accepts(value) ? DataResult.success(value)
                    : DataResult.error(() -> "expected at most " + maxLength + " characters");
        }

        @Override
        public boolean accepts(String value) {
            return value.length() <= maxLength;
        }

        @Override
        public DataResult<String> parse(String text) {
            return check(text);
        }

        @Override
        public JsonElement toJson(String value) {
            return new JsonPrimitive(value);
        }

        @Override
        public DataResult<String> fromJson(JsonElement json) {
            return json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()
                    ? check(json.getAsString())
                    : DataResult.error(() -> "expected a string");
        }

        @Override
        public List<String> suggestions() {
            return List.of("\"\"");
        }

        @Override
        public Codec<String> baseCodec() {
            return Codec.STRING;
        }
    }
}
