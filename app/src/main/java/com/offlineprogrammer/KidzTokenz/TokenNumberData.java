package com.offlineprogrammer.KidzTokenz;

public class TokenNumberData {

    private String tokenNumberName;
    private int tokenNumberImage;
    private String tokenNumberImageResourceName;
    private int tokenNumber;

    public TokenNumberData(String tokenNumberName, int tokenNumberImage, String tokenNumberImageResourceName, int tokenNumber) {
        this.tokenNumberName = tokenNumberName;
        this.tokenNumberImage = tokenNumberImage;
        this.tokenNumberImageResourceName = tokenNumberImageResourceName;
        this.tokenNumber = tokenNumber;
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

    public int getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(int tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public String getTokenNumberImageResourceName() {
        return tokenNumberImageResourceName;
    }

    public void setTokenNumberImageResourceName(String tokenNumberImageResourceName) {
        this.tokenNumberImageResourceName = tokenNumberImageResourceName;
    }
}
