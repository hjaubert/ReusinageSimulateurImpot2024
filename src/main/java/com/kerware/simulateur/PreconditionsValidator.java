package com.kerware.simulateur;

public class PreconditionsValidator {

    private static final int NB_MAX_ENFANTS = 7;

    public static void verifierPreconditions(int revNetDecl1, int revNetDecl2,
                                             SituationFamiliale sitFam,
                                             int nbEnfants, int nbEnfantsHandicapes,
                                             boolean parentIsol) {
        if (revNetDecl1 < 0 || revNetDecl2 < 0) {
            throw new IllegalArgumentException(
                    "Le revenu net ne peut pas être négatif"
            );
        }
        if (nbEnfants < 0) {
            throw new IllegalArgumentException(
                    "Le nombre d'enfants ne peut pas être négatif"
            );
        }
        if (nbEnfantsHandicapes < 0) {
            throw new IllegalArgumentException(
                    "Le nombre d'enfants handicapés ne peut pas être négatif"
            );
        }
        if (sitFam == null) {
            throw new IllegalArgumentException(
                    "La situation familiale ne peut pas être null"
            );
        }
        if (nbEnfantsHandicapes > nbEnfants) {
            throw new IllegalArgumentException(
                    "Le nombre d'enfants handicapés ne peut pas être supérieur au nombre d'enfants"
            );
        }
        if (nbEnfants > NB_MAX_ENFANTS) {
            throw new IllegalArgumentException(
                    "Le nombre d'enfants ne peut pas être supérieur à " + NB_MAX_ENFANTS
            );
        }
        if (parentIsol && (sitFam == SituationFamiliale.MARIE
                            || sitFam == SituationFamiliale.PACSE)) {
            throw new IllegalArgumentException(
                    "Un parent isolé ne peut pas être marié ou pacsé"
            );
        }
        boolean estSeul = sitFam == SituationFamiliale.CELIBATAIRE
                            || sitFam == SituationFamiliale.DIVORCE
                            || sitFam == SituationFamiliale.VEUF;
        if (estSeul && revNetDecl2 > 0) {
            throw new IllegalArgumentException(
                    "Un célibataire, un divorcé ou un veuf ne peut pas avoir de revenu déclarant 2"
            );
        }
    }
}

