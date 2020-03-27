package com.offlineprogrammer.kidztokenz;

public class TokenNumberData {

    private String tokenNumberName;
    private int tokenNumberImage;

    public TokenNumberData(String tokenNumberName, int tokenNumberImage) {
        this.tokenNumberName = tokenNumberName;
        this.tokenNumberImage = tokenNumberImage;
    }


    public String getTokenNumberName() {
        return tokenNumberName;
    }

    public void setTokenNumberName(String tokenNumberName) {
        this.tokenNumberName = tokenNumberName;
    }

    public int getTokenNumberImage() {
        return tokenNumberImage;
    }

    public void setTokenNumberImage(int tokenNumberImage) {
        this.tokenNumberImage = tokenNumberImage;
    }
}
