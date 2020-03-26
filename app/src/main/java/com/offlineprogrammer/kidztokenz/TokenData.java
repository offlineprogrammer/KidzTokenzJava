package com.offlineprogrammer.kidztokenz;

public class TokenData {

    private String tokenName;
    private int tokenImage;

    public TokenData(String tokenName, int tokenImage) {
        this.tokenName = tokenName;
        this.tokenImage = tokenImage;
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
}
