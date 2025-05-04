package com.kerware.simulateurReusine;

public enum SituationFamiliale {
    CELIBATAIRE(1),
    PACSE(2),
    MARIE(2),
    DIVORCE(1),
    VEUF(1);

    private final int nombrePartsDeclarants;

    // Constructeur pour l'énumération
    SituationFamiliale(int newNombrePartsDeclarants) {
        this.nombrePartsDeclarants = newNombrePartsDeclarants;
    }

    // Méthode pour récupérer le nombre de parts
    public int getNombrePartsDeclarants() {
        return nombrePartsDeclarants;
    }
}
