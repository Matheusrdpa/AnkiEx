package com.ankiEx.project.entities.morph;

public class TokenData {
    private String written;
    private String dictionary;
    private String type;
    private String subType;

    public TokenData(String written, String dictionary, String type, String subType) {
        this.written = written;
        this.dictionary = dictionary;
        this.type = type;
        this.subType = subType;
    }

    public String getWritten() {
        return written;
    }

    public void setWritten(String written) {
        this.written = written;
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    @Override
    public String toString() {
        return String.format("[%s] (Dic: %s) -> Type: %s / %s",
                written, dictionary, type, subType);
    }
}
