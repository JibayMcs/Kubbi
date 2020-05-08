package fr.leviathanstudio.engine.resources;

/**
 * @author ZeAmateis
 */
public class TranslationEntry {
    private String key, value;

    public TranslationEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Language{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
