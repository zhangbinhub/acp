package pers.acp.file.word;

import pers.acp.core.exceptions.EnumValueUndefinedException;

import java.util.HashMap;
import java.util.Map;

public enum WordType {

    WORD_TYPE_DOC(".doc", 0),

    WORD_TYPE_DOCX(".docx", 1);

    private final String name;

    private final Integer value;

    private static final Map<Integer, WordType> map;

    static {
        map = new HashMap<>();
        for (WordType type : values()) {
            map.put(type.getValue(), type);
        }
    }

    WordType(String name, Integer value) {
        this.name = name.toLowerCase();
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public Boolean equals(Integer value) {
        return this.value.equals(value);
    }

    public static WordType getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        throw new EnumValueUndefinedException(WordType.class, value + "");
    }

}
