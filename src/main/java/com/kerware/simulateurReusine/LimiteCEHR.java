package com.kerware.simulateurReusine;

public enum LimiteCEHR {
    LIMITE_CEHR_0(0),
    LIMITE_CEHR_1(250000),
    LIMITE_CEHR_2(500000),
    LIMITE_CEHR_3(1000000),
    LIMITE_CEHR_4(Integer.MAX_VALUE);

    private final int limite;

    LimiteCEHR(int newLimite) {
        this.limite = newLimite;
    }

    public int getLimite() {
        return limite;
    }
}

