package com.offlineprogrammer.KidzTokenz.kid.editKid;

public class Monster {

    private String monsterName;
    private String monsterImageResourceName;


    public Monster(String monsterName, String monsterImageResourceName) {
        this.monsterName = monsterName;
        this.monsterImageResourceName = monsterImageResourceName;
    }


    public String getMonsterName() {
        return monsterName;
    }

    public void setMonsterName(String monsterName) {
        this.monsterName = monsterName;
    }

    public String getMonsterImageResourceName() {
        return monsterImageResourceName;
    }

    public void setMonsterImageResourceName(String monsterImageResourceName) {
        this.monsterImageResourceName = monsterImageResourceName;
    }
}
