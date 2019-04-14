package com.example.attendance;

public class Take_att_data_node {
    private String roll;
    private String pofa;
    private int checkValue;
    public Take_att_data_node(String roll, String pofa, int chk) {
        this.roll = roll;
        this.pofa = pofa;
        this.checkValue = chk;
    }

    public String getRoll() {
        return roll;
    }

    public String getPofa() {
        return pofa;
    }

    public void setCheckValue(int checkValue) {
        this.checkValue = checkValue;
    }

    public boolean getCheckValue() {
        if (checkValue==1)
            return true;
        return false;
    }
    public int getIntegerCheckValue(){
        return this.checkValue;
    }
}
