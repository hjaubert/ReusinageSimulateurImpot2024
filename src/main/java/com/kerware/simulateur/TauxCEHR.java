package com.kerware.simulateur;

public enum TauxCEHR {
    TAUX_0(0.0, 0.0),
    TAUX_1(0.03, 0.0),
    TAUX_2(0.04, 0.03),
    TAUX_3(0.04, 0.04);

    private final double tauxCelibataire;
    private final double tauxCouple;

    TauxCEHR(double tauxCelibataire, double tauxCouple) {
        this.tauxCelibataire = tauxCelibataire;
        this.tauxCouple = tauxCouple;
    }

    public double getTauxCelibataire() {
        return tauxCelibataire;
    }

    public double getTauxCouple() {
        return tauxCouple;
    }
}

