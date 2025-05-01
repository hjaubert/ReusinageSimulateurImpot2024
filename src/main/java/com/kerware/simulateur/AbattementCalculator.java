package com.kerware.simulateur;

public class AbattementCalculator {

    /**
     * Constantes pour l'abattement.
     */
    private static final int ABATTEMENT_MAX = 14171;
    private static final int ABATTEMENT_MIN = 495;
    private static final double TAUX_ABATTEMENT = 0.1;

    public static long calculer(int revenu1, int revenu2, SituationFamiliale sitFam) {
        long abt1 = Math.round(revenu1 * TAUX_ABATTEMENT);
        abt1 = Math.min(Math.max(abt1, ABATTEMENT_MIN), ABATTEMENT_MAX);

        long abt2 = 0;
        if (sitFam == SituationFamiliale.MARIE || sitFam == SituationFamiliale.PACSE) {
            abt2 = Math.round(revenu2 * TAUX_ABATTEMENT);
            abt2 = Math.min(Math.max(abt2, ABATTEMENT_MIN), ABATTEMENT_MAX);
        }

        return abt1 + abt2;
    }
}
