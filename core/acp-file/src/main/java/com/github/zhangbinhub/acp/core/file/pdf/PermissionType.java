package com.github.zhangbinhub.acp.core.file.pdf;

import com.itextpdf.text.pdf.PdfWriter;
import com.github.zhangbinhub.acp.core.exceptions.EnumValueUndefinedException;

import java.util.HashMap;
import java.util.Map;

public enum PermissionType {

    ALLOW_ASSEMBLY("ALLOW_ASSEMBLY", PdfWriter.ALLOW_ASSEMBLY),

    ALLOW_COPY("ALLOW_COPY", PdfWriter.ALLOW_COPY),

    ALLOW_DEGRADED_PRINTING("ALLOW_DEGRADED_PRINTING", PdfWriter.ALLOW_DEGRADED_PRINTING),

    ALLOW_FILL_IN("ALLOW_FILL_IN", PdfWriter.ALLOW_FILL_IN),

    ALLOW_MODIFY_ANNOTATIONS("ALLOW_MODIFY_ANNOTATIONS", PdfWriter.ALLOW_MODIFY_ANNOTATIONS),

    ALLOW_MODIFY_CONTENTS("ALLOW_MODIFY_CONTENTS", PdfWriter.ALLOW_MODIFY_CONTENTS),

    ALLOW_PRINTING("ALLOW_PRINTING", PdfWriter.ALLOW_PRINTING),

    ALLOW_SCREENREADERS("ALLOW_SCREENREADERS", PdfWriter.ALLOW_SCREENREADERS);

    private final String name;

    private final Integer value;

    private static final Map<Integer, PermissionType> map;

    static {
        map = new HashMap<>();
        for (PermissionType type : values()) {
            map.put(type.getValue(), type);
        }
    }

    PermissionType(String name, Integer value) {
        this.name = name;
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

    public static PermissionType getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        throw new EnumValueUndefinedException(PermissionType.class, value + "");
    }
}
