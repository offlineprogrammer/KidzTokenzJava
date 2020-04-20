package com.offlineprogrammer.KidzTokenz;

public class TokenData {

    private String tokenName;
    private int tokenImage;
    private int badtokenImage;
    private String tokenImageImageResourceName;
    private String badtokenImageImageResourceName;

    public TokenData(String tokenName, int tokenImage, int badtokenImage, String tokenImageImageResourceName, String badtokenImageImageResourceName) {
        this.tokenName = tokenName;
        this.tokenImage = tokenImage;
        this.badtokenImage = badtokenImage;
        this.tokenImageImageResourceName = tokenImageImageResourceName;
        this.badtokenImageImageResourceName = badtokenImageImageResourceName;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public int getTokenImage() {
        return tokenImage;
    }

    public void setTokenImage(int tokenImage) {
        this.tokenImage = tokenImage;
    }

    public String getTokenImageImageResourceName() {
        return tokenImageImageResourceName;
    }

    public void setTokenImageImageResourceName(String tokenImageImageResourceName) {
        this.tokenImageImageResourceName = tokenImageImageResourceName;
    }

    public String getBadtokenImageImageResourceName() {
        return badtokenImageImageResourceName;
    }

    public void setBadtokenImageImageResourceName(String badtokenImageImageResourceName) {
        this.badtokenImageImageResourceName = badtokenImageImageResourceName;
    }

    public int getBadtokenImage() {
        return badtokenImage;
    }

    public void setBadtokenImage(int badtokenImage) {
        this.badtokenImage = badtokenImage;
    }
}
