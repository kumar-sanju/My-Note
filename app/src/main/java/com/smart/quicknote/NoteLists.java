package com.smart.quicknote;
public class NoteLists {
    private String dataTitle = "";
    private String dataDesc = "";
    private String dataLang = "";
    private String dataImage = "";
    private String key = "";
    Boolean isSelected=false;

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getDataTitle() {
        return dataTitle;
    }
    public String getDataDesc() {
        return dataDesc;
    }
    public String getDataLang() {
        return dataLang;
    }
    public String getDataImage() {
        return dataImage;
    }

    public NoteLists(String dataTitle, String dataDesc, String dataLang, String dataImage) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
    }

    public NoteLists(String dataTitle, String dataDesc, String dataLang, String dataImage, String key) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
        this.key = key;
    }

    public NoteLists(){
    }
}