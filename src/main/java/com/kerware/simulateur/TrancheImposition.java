package com.kerware.simulateur;

public enum TrancheImposition {
    LIMITE_T0(0, 0.0),
    LIMITE_T1(11294, 0.11),
    LIMITE_T2(28797, 0.30),
    LIMITE_T3(82341, 0.41),
    LIMITE_T4(177106, 0.45),
    LIMITE_T5(Integer.MAX_VALUE, 0.45);

    private final int limite;
    private final double taux;

    TrancheImposition(int limite, double taux) {
        this.limite = limite;
        this.taux = taux;
    }

    public int getLimite() {
        return limite;
    }

    public double getTaux() {
        return taux;
    }

    public static double calculerImpot(double revenuImposable) {
        double impot = 0;
        int i = 0;

        do {
            if (revenuImposable >= values()[i].getLimite() && revenuImposable < values()[i+1].getLimite()) {
                impot += (revenuImposable - values()[i].getLimite()) * values()[i].getTaux();
                break;
            } else {
                impot += (values()[i+1].getLimite() - values()[i].getLimite()) * values()[i].getTaux();
            }
            i++;
        } while (i < values().length);


        return impot;
    }
}
