package fr.leviathanstudio.engine.config;

import com.typesafe.config.*;
import fr.leviathanstudio.engine.GameEngine;
import fr.zeamateis.kubbi.KubbiClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ZeAmateis
 */
public class Configuration {

    private final Path configFilePath;
    private Config config;

    public Configuration(Path configFilePath) {
        this.configFilePath = configFilePath;
        if (!configFilePath.getParent().toFile().exists())
            try {
                Files.createDirectories(configFilePath.getParent());
            } catch (IOException ex) {
                GameEngine.LOGGER.error("Unable to create directory {}", configFilePath.getParent());
            }

        this.config = ConfigFactory.parseFile(configFilePath.toFile());
    }

    public Path getConfigFilePath() {
        return configFilePath;
    }

    public Config getConfig() {
        return config;
    }

    public Locale getLocale(String path) {
        AtomicReference<Locale> locale = new AtomicReference<>();
        List<String> stringList = getStringList(path);
        if (stringList.size() == 1) {
            locale.set(new Locale(stringList.get(0)));
        } else if (stringList.size() >= 1 && stringList.size() < 3) {
            locale.set(new Locale(stringList.get(0), stringList.get(1)));
        } else if (stringList.size() >= 3) {
            KubbiClient.LOGGER.debug("Too many arguments for language configuration, assuming second parameter is correct");
            locale.set(new Locale(stringList.get(0), stringList.get(1)));
        }
        return locale.get();
    }

    /**
     * Checks whether a value is set to null at the given path,
     * but throws an exception if the value is entirely
     * unset. This method will not throw if {@link
     * Config#hasPathOrNull(String)} returned true for the same path, so
     * to avoid any possible exception check
     * <code>hasPathOrNull()</code> first.  However, an exception
     * for unset paths will usually be the right thing (because a
     * <code>reference.conf</code> should exist that has the path
     * set, the path should never be unset unless something is
     * broken).
     *
     * <p>
     * Note that path expressions have a syntax and sometimes require quoting
     * (see {@link ConfigUtil#joinPath} and {@link ConfigUtil#splitPath}).
     *
     * @param path the path expression
     * @return true if the value exists and is null, false if it
     * exists and is not null
     * @throws ConfigException.BadPath if the path expression is invalid
     * @throws ConfigException.Missing if value is not set at all
     */
    public boolean getIsNull(String path) {
        return this.config.getIsNull(path);
    }

    /**
     * @param path path expression
     * @return the boolean value at the requested path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to boolean
     */
    public boolean getBoolean(String path) {
        return this.config.getBoolean(path);
    }

    /**
     * @param path path expression
     * @return the numeric value at the requested path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a number
     */
    public Number getNumber(String path) {
        return this.config.getNumber(path);
    }

    /**
     * Gets the integer at the given path. If the value at the
     * path has a fractional (floating point) component, it
     * will be discarded and only the integer part will be
     * returned (it works like a "narrowing primitive conversion"
     * in the Java language specification).
     *
     * @param path path expression
     * @return the 32-bit integer value at the requested path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to an int (for example it is out
     *                                   of range, or it's a boolean value)
     */
    public int getInt(String path) {
        return this.config.getInt(path);
    }

    /**
     * Gets the long integer at the given path.  If the value at
     * the path has a fractional (floating point) component, it
     * will be discarded and only the integer part will be
     * returned (it works like a "narrowing primitive conversion"
     * in the Java language specification).
     *
     * @param path path expression
     * @return the 64-bit long value at the requested path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a long
     */
    public long getLong(String path) {
        return this.config.getLong(path);
    }

    /**
     * @param path path expression
     * @return the floating-point value at the requested path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a double
     */
    public double getDouble(String path) {
        return this.config.getDouble(path);
    }

    /**
     * @param path path expression
     * @return the string value at the requested path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a string
     */
    public String getString(String path) {
        return this.config.getString(path);
    }

    /**
     * @param enumClass an enum class
     * @param <T>       a generic denoting a specific type of enum
     * @param path      path expression
     * @return the {@code Enum} value at the requested path
     * of the requested enum class
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to an Enum
     */
    public <T extends Enum<T>> T getEnum(Class<T> enumClass, String path) {
        return this.config.getEnum(enumClass, path);
    }

    /**
     * @param path path expression
     * @return the {@link ConfigObject} value at the requested path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to an object
     */
    public ConfigObject getObject(String path) {
        return this.config.getObject(path);
    }

    /**
     * @param path path expression
     * @return the nested {@code Config} value at the requested path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a Config
     */
    public Config getConfig(String path) {
        return this.config.getConfig(path);
    }

    /**
     * Gets the value at the path as an unwrapped Java boxed value (
     * {@link java.lang.Boolean Boolean}, {@link java.lang.Integer Integer}, and
     * so on - see {@link ConfigValue#unwrapped()}).
     *
     * @param path path expression
     * @return the unwrapped value at the requested path
     * @throws ConfigException.Missing if value is absent or null
     */
    public Object getAnyRef(String path) {
        return this.config.getAnyRef(path);
    }

    /**
     * Gets the value at the given path, unless the value is a
     * null value or missing, in which case it throws just like
     * the other getters. Use {@code get()} on the {@link
     * Config#root()} object (or other object in the tree) if you
     * want an unprocessed value.
     *
     * @param path path expression
     * @return the value at the requested path
     * @throws ConfigException.Missing if value is absent or null
     */
    public ConfigValue getValue(String path) {
        return this.config.getValue(path);
    }

    /**
     * Gets a value as a size in bytes (parses special strings like "128M"). If
     * the value is already a number, then it's left alone; if it's a string,
     * it's parsed understanding unit suffixes such as "128K", as documented in
     * the <a
     * href="https://github.com/lightbend/config/blob/master/HOCON.md">the
     * spec</a>.
     *
     * @param path path expression
     * @return the value at the requested path, in bytes
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to Long or String
     * @throws ConfigException.BadValue  if value cannot be parsed as a size in bytes
     */
    public Long getBytes(String path) {
        return this.config.getBytes(path);
    }

    /**
     * Gets a value as an amount of memory (parses special strings like "128M"). If
     * the value is already a number, then it's left alone; if it's a string,
     * it's parsed understanding unit suffixes such as "128K", as documented in
     * the <a
     * href="https://github.com/lightbend/config/blob/master/HOCON.md">the
     * spec</a>.
     *
     * @param path path expression
     * @return the value at the requested path, in bytes
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to Long or String
     * @throws ConfigException.BadValue  if value cannot be parsed as a size in bytes
     * @since 1.3.0
     */
    public ConfigMemorySize getMemorySize(String path) {
        return this.config.getMemorySize(path);
    }

    /**
     * Gets a value as a duration in a specified
     * {@link java.util.concurrent.TimeUnit TimeUnit}. If the value is already a
     * number, then it's taken as milliseconds and then converted to the
     * requested TimeUnit; if it's a string, it's parsed understanding units
     * suffixes like "10m" or "5ns" as documented in the <a
     * href="https://github.com/lightbend/config/blob/master/HOCON.md">the
     * spec</a>.
     *
     * @param path path expression
     * @param unit convert the return value to this time unit
     * @return the duration value at the requested path, in the given TimeUnit
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to Long or String
     * @throws ConfigException.BadValue  if value cannot be parsed as a number of the given TimeUnit
     * @since 1.2.0
     */
    public long getDuration(String path, TimeUnit unit) {
        return this.config.getDuration(path, unit);
    }

    /**
     * Gets a value as a java.time.Duration. If the value is
     * already a number, then it's taken as milliseconds; if it's
     * a string, it's parsed understanding units suffixes like
     * "10m" or "5ns" as documented in the <a
     * href="https://github.com/lightbend/config/blob/master/HOCON.md">the
     * spec</a>. This method never returns null.
     *
     * @param path path expression
     * @return the duration value at the requested path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to Long or String
     * @throws ConfigException.BadValue  if value cannot be parsed as a number of the given TimeUnit
     * @since 1.3.0
     */
    public Duration getDuration(String path) {
        return this.config.getDuration(path);
    }

    /**
     * Gets a value as a java.time.Period. If the value is
     * already a number, then it's taken as days; if it's
     * a string, it's parsed understanding units suffixes like
     * "10d" or "5w" as documented in the <a
     * href="https://github.com/lightbend/config/blob/master/HOCON.md">the
     * spec</a>. This method never returns null.
     *
     * @param path path expression
     * @return the period value at the requested path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to Long or String
     * @throws ConfigException.BadValue  if value cannot be parsed as a number of the given TimeUnit
     * @since 1.3.0
     */
    public Period getPeriod(String path) {
        return this.config.getPeriod(path);
    }

    /**
     * Gets a value as a java.time.temporal.TemporalAmount.
     * This method will first try get get the value as a java.time.Duration, and if unsuccessful,
     * then as a java.time.Period.
     * This means that values like "5m" will be parsed as 5 minutes rather than 5 months
     *
     * @param path path expression
     * @return the temporal value at the requested path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to Long or String
     * @throws ConfigException.BadValue  if value cannot be parsed as a TemporalAmount
     */
    public TemporalAmount getTemporal(String path) {
        return this.config.getTemporal(path);
    }

    /**
     * Gets a list value (with any element type) as a {@link ConfigList}, which
     * implements {@code java.util.List<ConfigValue>}. Throws if the path is
     * unset or null.
     *
     * @param path the path to the list value.
     * @return the {@link ConfigList} at the path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a ConfigList
     */
    public ConfigList getList(String path) {
        return this.config.getList(path);
    }

    /**
     * Gets a list value with boolean elements.  Throws if the
     * path is unset or null or not a list or contains values not
     * convertible to boolean.
     *
     * @param path the path to the list value.
     * @return the list at the path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a list of booleans
     */
    public List<Boolean> getBooleanList(String path) {
        return this.config.getBooleanList(path);
    }

    /**
     * Gets a list value with number elements.  Throws if the
     * path is unset or null or not a list or contains values not
     * convertible to number.
     *
     * @param path the path to the list value.
     * @return the list at the path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a list of numbers
     */
    public List<Number> getNumberList(String path) {
        return this.config.getNumberList(path);
    }

    /**
     * Gets a list value with int elements.  Throws if the
     * path is unset or null or not a list or contains values not
     * convertible to int.
     *
     * @param path the path to the list value.
     * @return the list at the path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a list of ints
     */
    public List<Integer> getIntList(String path) {
        return this.config.getIntList(path);
    }

    /**
     * Gets a list value with long elements.  Throws if the
     * path is unset or null or not a list or contains values not
     * convertible to long.
     *
     * @param path the path to the list value.
     * @return the list at the path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a list of longs
     */
    public List<Long> getLongList(String path) {
        return this.config.getLongList(path);
    }

    /**
     * Gets a list value with double elements.  Throws if the
     * path is unset or null or not a list or contains values not
     * convertible to double.
     *
     * @param path the path to the list value.
     * @return the list at the path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a list of doubles
     */
    public List<Double> getDoubleList(String path) {
        return this.config.getDoubleList(path);
    }

    /**
     * Gets a list value with string elements.  Throws if the
     * path is unset or null or not a list or contains values not
     * convertible to string.
     *
     * @param path the path to the list value.
     * @return the list at the path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a list of strings
     */
    public List<String> getStringList(String path) {
        return this.config.getStringList(path);
    }

    /**
     * Gets a list value with {@code Enum} elements.  Throws if the
     * path is unset or null or not a list or contains values not
     * convertible to {@code Enum}.
     *
     * @param enumClass the enum class
     * @param <T>       a generic denoting a specific type of enum
     * @param path      the path to the list value.
     * @return the list at the path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a list of {@code Enum}
     */
    public <T extends Enum<T>> List<T> getEnumList(Class<T> enumClass, String path) {
        return this.config.getEnumList(enumClass, path);
    }

    /**
     * Gets a list value with object elements.  Throws if the
     * path is unset or null or not a list or contains values not
     * convertible to <code>ConfigObject</code>.
     *
     * @param path the path to the list value.
     * @return the list at the path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a list of objects
     */
    public List<? extends ConfigObject> getObjectList(String path) {
        return this.config.getObjectList(path);
    }

    /**
     * Gets a list value with <code>Config</code> elements.
     * Throws if the path is unset or null or not a list or
     * contains values not convertible to <code>Config</code>.
     *
     * @param path the path to the list value.
     * @return the list at the path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a list of configs
     */
    public List<? extends Config> getConfigList(String path) {
        return this.config.getConfigList(path);
    }

    /**
     * Gets a list value with any kind of elements.  Throws if the
     * path is unset or null or not a list. Each element is
     * "unwrapped" (see {@link ConfigValue#unwrapped()}).
     *
     * @param path the path to the list value.
     * @return the list at the path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a list
     */
    public List<? extends Object> getAnyRefList(String path) {
        return this.config.getAnyRefList(path);
    }

    /**
     * Gets a list value with elements representing a size in
     * bytes.  Throws if the path is unset or null or not a list
     * or contains values not convertible to memory sizes.
     *
     * @param path the path to the list value.
     * @return the list at the path
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a list of memory sizes
     */
    public List<Long> getBytesList(String path) {
        return this.config.getBytesList(path);
    }

    /**
     * Gets a list, converting each value in the list to a memory size, using the
     * same rules as {@link #getMemorySize(String)}.
     *
     * @param path a path expression
     * @return list of memory sizes
     * @throws ConfigException.Missing   if value is absent or null
     * @throws ConfigException.WrongType if value is not convertible to a list of memory sizes
     * @since 1.3.0
     */
    public List<ConfigMemorySize> getMemorySizeList(String path) {
        return this.config.getMemorySizeList(path);
    }

    /**
     * Gets a list, converting each value in the list to a duration, using the
     * same rules as {@link #getDuration(String, TimeUnit)}.
     *
     * @param path a path expression
     * @param unit time units of the returned values
     * @return list of durations, in the requested units
     * @since 1.2.0
     */
    public List<Long> getDurationList(String path, TimeUnit unit) {
        return this.config.getDurationList(path, unit);
    }

    /**
     * Gets a list, converting each value in the list to a duration, using the
     * same rules as {@link #getDuration(String)}.
     *
     * @param path a path expression
     * @return list of durations
     * @since 1.3.0
     */
    public List<Duration> getDurationList(String path) {
        return this.config.getDurationList(path);
    }
}