package com.kerware.simulateur;

/**
 * Simulateur d'impôt sur le revenu en France pour l'année 2024 sur les revenus de l'année 2023.
 * Cette classe permet de calculer l'impôt pour des contribuables
 * avec différentes situations familiales.
 */
public class Simulateur {

    // Données du calcul
    private CalculFiscale calculFiscale;

    /**
     * Constructeur par défaut.
     */
    public Simulateur() {
        // Initialisation des attributs avec des valeurs par défaut
        calculFiscale = new CalculFiscale();
    }

    /**
     * Calcule l'impôt sur le revenu net.
     *
     * @param revNetDecl1 Revenu net du premier déclarant
     * @param revNetDecl2 Revenu net du deuxième déclarant
     * @param sitFam Situation familiale
     * @param nbEnfants Nombre d'enfants
     * @param nbEnfantsHandicapes Nombre d'enfants en situation de handicap
     * @param parentIsol Si le parent est isolé
     * @return Montant de l'impôt sur le revenu net
     * @throws IllegalArgumentException Si les paramètres sont invalides
     */
    public int calculImpot(int revNetDecl1, int revNetDecl2, SituationFamiliale sitFam,
                           int nbEnfants, int nbEnfantsHandicapes, boolean parentIsol) {

        // Vérification des préconditions
        PreconditionsValidator.verifierPreconditions(revNetDecl1, revNetDecl2, sitFam,
                nbEnfants, nbEnfantsHandicapes, parentIsol);

        // Initialisation des données
        calculFiscale.initialiserDonnees(revNetDecl1, revNetDecl2, nbEnfants,
                                        nbEnfantsHandicapes, parentIsol);

        // Calcul de l'abattement
        calculFiscale.calculerAbattement(sitFam);

        // Calcul du revenu fiscal de référence
        calculFiscale.calculerRevenuFiscalReference();

        // Calcul du nombre de parts
        calculFiscale.calculerNombreParts(sitFam);

        // Calcul de la contribution exceptionnelle
        calculFiscale.calculerContributionExceptionnelle();

        // Calcul de l'impôt des déclarants
        calculFiscale.calculerImpotDeclarants();

        // Calcul de l'impôt du foyer fiscal
        calculFiscale.calculerImpotFoyer();

        // Vérification du plafonnement
        calculFiscale.appliquerPlafonnement();

        // Calcul de la décote
        calculFiscale.calculerDecote();

        // Calcul de l'impôt final
        return calculFiscale.calculerImpotFinal();
    }

    /**
     *
     * @return
     */
    public CalculFiscale getCalculFiscale() {
        return calculFiscale;
    }

    /**
     *
     * @param newCalculFiscale
     */
    public void setCalculFiscale(CalculFiscale newCalculFiscale) {
        this.calculFiscale = newCalculFiscale;
    }
}