package com.kerware.simulateur;

/**
 * Simulateur d'impôt sur le revenu en France pour l'année 2024 sur les revenus de l'année 2023.
 * Cette classe permet de calculer l'impôt pour des contribuables
 * avec différentes situations familiales.
 */
public class Simulateur {

    /**
     * Constantes pour la contribution exceptionnelle sur les hauts revenus.
     */
    private static final int LIMITE_CEHR_0 = 0;
    private static final int LIMITE_CEHR_1 = 250000;
    private static final int LIMITE_CEHR_2 = 500000;
    private static final int LIMITE_CEHR_3 = 1000000;
    private static final int LIMITE_CEHR_4 = Integer.MAX_VALUE;

    /**
     * Taux CEHR pour les célibataires.
     */
    private static final double TAUX_CEHR_CELIBATAIRE_0 = 0.0;
    private static final double TAUX_CEHR_CELIBATAIRE_1 = 0.03;
    private static final double TAUX_CEHR_CELIBATAIRE_2 = 0.04;
    private static final double TAUX_CEHR_CELIBATAIRE_3 = 0.04;

    /**
     * Taux CEHR pour les couples.
     */
    private static final double TAUX_CEHR_COUPLE_0 = 0.0;
    private static final double TAUX_CEHR_COUPLE_1 = 0.0;
    private static final double TAUX_CEHR_COUPLE_2 = 0.03;
    private static final double TAUX_CEHR_COUPLE_3 = 0.04;

    /**
     * Constantes pour l'abattement.
     */
    private static final int ABATTEMENT_MAX = 14171;
    private static final int ABATTEMENT_MIN = 495;
    private static final double TAUX_ABATTEMENT = 0.1;

    /**
     * Constantes pour le plafonnement et la décote.
     */
    private static final double PLAFOND_DEMI_PART = 1759;
    private static final double SEUIL_DECOTE_SEUL = 1929;
    private static final double SEUIL_DECOTE_COUPLE = 3191;
    private static final double DECOTE_MAX_SEUL = 873;
    private static final double DECOTE_MAX_COUPLE = 1444;
    private static final double TAUX_DECOTE = 0.4525;

    /**
     * Constantes pour le calcul des parts fiscales.
     */
    private static final double PART_ENFANT = 0.5;
    private static final double PART_PARENT_ISOLE = 0.5;
    private static final int NB_MAX_ENFANTS = 7;
    private static final int NB_ENFANTS_DEMI_PART = 2;

    // Données du calcul
    private int revenuNetDeclarant1;
    private int revenuNetDeclarant2;
    private int nombreEnfants;
    private int nombreEnfantsHandicap;
    private boolean parentIsole;
    private double revenuFiscalReference;
    private double abattement;
    private double nombrePartsDeclarants;
    private double nombrePartsFoyer;
    private double decote;
    private double impotDeclarants;
    private double impotFoyer;
    private double impotAvantDecote;
    private double contributionExceptionnelle;

    /**
     * Tableau des limites de tranches d'imposition.
     */
    private final int[] limitesTranches = {
            TrancheImposition.LIMITE_T0.getLimite(), TrancheImposition.LIMITE_T1.getLimite(), TrancheImposition.LIMITE_T2.getLimite(), TrancheImposition.LIMITE_T3.getLimite(), TrancheImposition.LIMITE_T4.getLimite(), TrancheImposition.LIMITE_T5.getLimite()
    };

    /**
     * Tableau des taux d'imposition par tranche.
     */
    private final double[] tauxTranches = {
            TrancheImposition.LIMITE_T0.getTaux(),
            TrancheImposition.LIMITE_T1.getTaux(),
            TrancheImposition.LIMITE_T2.getTaux(),
            TrancheImposition.LIMITE_T3.getTaux(),
            TrancheImposition.LIMITE_T4.getTaux()
    };

    /**
     * Tableau des limites pour la contribution exceptionnelle.
     */
    private final int[] limitesCEHR = {
            LIMITE_CEHR_0, LIMITE_CEHR_1, LIMITE_CEHR_2, LIMITE_CEHR_3, LIMITE_CEHR_4
    };

    /**
     * Tableau des taux CEHR pour les célibataires.
     */
    private final double[] tauxCEHRCelibataire = {
            TAUX_CEHR_CELIBATAIRE_0, TAUX_CEHR_CELIBATAIRE_1,
            TAUX_CEHR_CELIBATAIRE_2, TAUX_CEHR_CELIBATAIRE_3
    };

    /**
     * Tableau des taux CEHR pour les couples.
     */
    private final double[] tauxCEHRCouple = {
            TAUX_CEHR_COUPLE_0, TAUX_CEHR_COUPLE_1, TAUX_CEHR_COUPLE_2, TAUX_CEHR_COUPLE_3
    };

    /**
     * Constructeur par défaut.
     */
    public Simulateur() {
        // Initialisation des attributs avec des valeurs par défaut
        reinitialiser();
    }

    /**
     * Réinitialise tous les attributs du simulateur.
     */
    private void reinitialiser() {
        this.revenuNetDeclarant1 = 0;
        this.revenuNetDeclarant2 = 0;
        this.nombreEnfants = 0;
        this.nombreEnfantsHandicap = 0;
        this.parentIsole = false;
        this.revenuFiscalReference = 0;
        this.abattement = 0;
        this.nombrePartsDeclarants = 0;
        this.nombrePartsFoyer = 0;
        this.decote = 0;
        this.impotDeclarants = 0;
        this.impotFoyer = 0;
        this.impotAvantDecote = 0;
        this.contributionExceptionnelle = 0;
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
        verifierPreconditions(revNetDecl1, revNetDecl2, sitFam,
                nbEnfants, nbEnfantsHandicapes, parentIsol);

        // Initialisation des données
        initialiserDonnees(revNetDecl1, revNetDecl2, nbEnfants, nbEnfantsHandicapes, parentIsol);

        // Calcul de l'abattement
        calculerAbattement(sitFam);

        // Calcul du revenu fiscal de référence
        calculerRevenuFiscalReference();

        // Calcul du nombre de parts
        calculerNombreParts(sitFam);

        // Calcul de la contribution exceptionnelle
        calculerContributionExceptionnelle();

        // Calcul de l'impôt des déclarants
        calculerImpotDeclarants();

        // Calcul de l'impôt du foyer fiscal
        calculerImpotFoyer();

        // Vérification du plafonnement
        appliquerPlafonnement();

        // Calcul de la décote
        calculerDecote();

        // Calcul de l'impôt final
        return calculerImpotFinal();
    }

    /**
     * Vérifie les préconditions des paramètres.
     */
    private void verifierPreconditions(int revNetDecl1, int revNetDecl2, SituationFamiliale sitFam,
                                       int nbEnfants, int nbEnfantsHandicapes, boolean parentIsol) {
        if (revNetDecl1 < 0 || revNetDecl2 < 0) {
            throw new IllegalArgumentException("Le revenu net ne peut pas être négatif");
        }

        if (nbEnfants < 0) {
            throw new IllegalArgumentException("Le nombre d'enfants ne peut pas être négatif");
        }

        if (nbEnfantsHandicapes < 0) {
            throw new IllegalArgumentException(
                    "Le nombre d'enfants handicapés ne peut pas être négatif"
            );
        }

        if (sitFam == null) {
            throw new IllegalArgumentException("La situation familiale ne peut pas être null");
        }

        if (nbEnfantsHandicapes > nbEnfants) {
            throw new IllegalArgumentException(
                    "Le nombre d'enfants handicapés ne peut pas être supérieur au nombre d'enfants"
            );
        }

        if (nbEnfants > NB_MAX_ENFANTS) {
            throw new IllegalArgumentException(
                    "Le nombre d'enfants ne peut pas être supérieur à " + NB_MAX_ENFANTS);
        }

        if (parentIsol && (sitFam == SituationFamiliale.MARIE ||
                sitFam == SituationFamiliale.PACSE)) {
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

    /**
     * Initialise les données pour le calcul.
     */
    private void initialiserDonnees(int revNetDecl1, int revNetDecl2, int nbEnfants,
                                    int nbEnfantsHandicapes, boolean parentIsol) {
        this.revenuNetDeclarant1 = revNetDecl1;
        this.revenuNetDeclarant2 = revNetDecl2;
        this.nombreEnfants = nbEnfants;
        this.nombreEnfantsHandicap = nbEnfantsHandicapes;
        this.parentIsole = parentIsol;
    }

    /**
     * Calcule l'abattement à appliquer.
     * EXG_IMPOT_02
     */
    private void calculerAbattement(SituationFamiliale sitFam) {
        long abt1 = Math.round(revenuNetDeclarant1 * TAUX_ABATTEMENT);
        long abt2 = Math.round(revenuNetDeclarant2 * TAUX_ABATTEMENT);

        abt1 = Math.min(Math.max(abt1, ABATTEMENT_MIN), ABATTEMENT_MAX);

        if (sitFam == SituationFamiliale.MARIE || sitFam == SituationFamiliale.PACSE) {
            abt2 = Math.min(Math.max(abt2, ABATTEMENT_MIN), ABATTEMENT_MAX);
        } else {
            abt2 = 0;
        }

        this.abattement = abt1 + abt2;
    }

    /**
     * Calcule le revenu fiscal de référence.
     */
    private void calculerRevenuFiscalReference() {
        this.revenuFiscalReference = revenuNetDeclarant1 + revenuNetDeclarant2 - abattement;
        if (this.revenuFiscalReference < 0) {
            this.revenuFiscalReference = 0;
        }
    }

    /**
     * Calcule le nombre de parts du foyer fiscal.
     * EXG_IMPOT_03
     */
    private void calculerNombreParts(SituationFamiliale sitFam) {
        // Calcul des parts pour les déclarants
        switch (sitFam) {
            case CELIBATAIRE:
            case DIVORCE:
            case VEUF:
                this.nombrePartsDeclarants = 1;
                break;
            case MARIE:
            case PACSE:
                this.nombrePartsDeclarants = 2;
                break;
            default:
                this.nombrePartsDeclarants = 0;
                break;
        }

        // Calcul des parts pour les enfants à charge
        this.nombrePartsFoyer = this.nombrePartsDeclarants;

        if (nombreEnfants <= NB_ENFANTS_DEMI_PART) {
            this.nombrePartsFoyer += nombreEnfants * PART_ENFANT;
        } else {
            this.nombrePartsFoyer += 1.0 + (nombreEnfants - NB_ENFANTS_DEMI_PART);
        }

        // Ajout pour parent isolé
        if (parentIsole && nombreEnfants > 0) {
            this.nombrePartsFoyer += PART_PARENT_ISOLE;
        }

        // Cas spécial pour veuf avec enfant
        if (sitFam == SituationFamiliale.VEUF && nombreEnfants > 0) {
            this.nombrePartsFoyer += 1;
        }

        // Ajout pour enfants en situation de handicap
        this.nombrePartsFoyer += nombreEnfantsHandicap * PART_ENFANT;
    }

    /**
     * Calcule la contribution exceptionnelle sur les hauts revenus.
     * EXG_IMPOT_07
     */
    private void calculerContributionExceptionnelle() {
        this.contributionExceptionnelle = 0;
        int i = 0;

        do {
            double[] tauxCEHR = (nombrePartsDeclarants == 1)
                    ? tauxCEHRCelibataire : tauxCEHRCouple;

            if (revenuFiscalReference >= limitesCEHR[i]
                    && revenuFiscalReference < limitesCEHR[i + 1]
            ) {
                contributionExceptionnelle +=
                        (revenuFiscalReference - limitesCEHR[i]) * tauxCEHR[i];
                break;
            } else {
                contributionExceptionnelle += (limitesCEHR[i + 1] - limitesCEHR[i]) * tauxCEHR[i];
            }
            i++;
        } while (i < tauxCEHRCelibataire.length);

        this.contributionExceptionnelle = Math.round(this.contributionExceptionnelle);
    }

    /**
     * Calcule l'impôt brut des déclarants.
     * EXG_IMPOT_04
     */
    private void calculerImpotDeclarants() {
        double revenuImposable = revenuFiscalReference / nombrePartsDeclarants;
        this.impotDeclarants = calculerImpotParTranches(revenuImposable) * nombrePartsDeclarants;
        this.impotDeclarants = Math.round(this.impotDeclarants);
    }

    /**
     * Calcule l'impôt brut du foyer fiscal.
     * EXG_IMPOT_04
     */
    private void calculerImpotFoyer() {
        double revenuImposable = revenuFiscalReference / nombrePartsFoyer;
        this.impotFoyer = calculerImpotParTranches(revenuImposable) * nombrePartsFoyer;
        this.impotFoyer = Math.round(this.impotFoyer);
    }

    /**
     * Calcule l'impôt pour un revenu donné selon les tranches d'imposition.
     */
    private double calculerImpotParTranches(double revenuImposable) {
        return TrancheImposition.calculerImpot(revenuImposable);
    }

    /**
     * Applique le plafonnement des effets du quotient familial.
     * EXG_IMPOT_05
     */
    private void appliquerPlafonnement() {
        double baisseImpot = impotDeclarants - impotFoyer;
        double ecartParts = nombrePartsFoyer - nombrePartsDeclarants;
        double plafond = (ecartParts / PART_ENFANT) * PLAFOND_DEMI_PART;

        if (baisseImpot >= plafond) {
            this.impotFoyer = impotDeclarants - plafond;
        }

        this.impotAvantDecote = this.impotFoyer;
    }

    /**
     * Calcule la décote.
     * EXG_IMPOT_06
     */
    private void calculerDecote() {
        this.decote = 0;

        if (nombrePartsDeclarants == 1) {
            if (impotFoyer < SEUIL_DECOTE_SEUL) {
                this.decote = DECOTE_MAX_SEUL - (impotFoyer * TAUX_DECOTE);
            }
        } else if (nombrePartsDeclarants == 2) {
            if (impotFoyer < SEUIL_DECOTE_COUPLE) {
                this.decote = DECOTE_MAX_COUPLE - (impotFoyer * TAUX_DECOTE);
            }
        }

        this.decote = Math.round(this.decote);

        if (impotFoyer <= this.decote) {
            this.decote = impotFoyer;
        }
    }

    /**
     * Calcule l'impôt final.
     */
    private int calculerImpotFinal() {
        double impotFinal = impotFoyer - decote + contributionExceptionnelle;
        return (int) Math.round(impotFinal);
    }

    /**
     * Retourne le revenu fiscal de référence.
     *
     * @return Revenu fiscal de référence
     */
    public double getRevenuReference() {
        return revenuFiscalReference;
    }

    /**
     * Retourne la décote calculée.
     *
     * @return Décote
     */
    public double getDecote() {
        return decote;
    }

    /**
     * Retourne l'abattement appliqué.
     *
     * @return Abattement
     */
    public double getAbattement() {
        return abattement;
    }

    /**
     * Retourne le nombre de parts du foyer fiscal.
     *
     * @return Nombre de parts
     */
    public double getNbParts() {
        return nombrePartsFoyer;
    }

    /**
     * Retourne l'impôt avant décote.
     *
     * @return Impôt avant décote
     */
    public double getImpotAvantDecote() {
        return impotAvantDecote;
    }

    /**
     * Retourne l'impôt net final.
     *
     * @return Impôt net final
     */
    public double getImpotNet() {
        return impotFoyer - decote + contributionExceptionnelle;
    }

    /**
     * Retourne le revenu net du premier déclarant.
     *
     * @return Revenu net du premier déclarant
     */
    public int getRevenuNetDeclatant1() {
        return revenuNetDeclarant1;
    }

    /**
     * Retourne le revenu net du deuxième déclarant.
     *
     * @return Revenu net du deuxième déclarant
     */
    public int getRevenuNetDeclatant2() {
        return revenuNetDeclarant2;
    }

    /**
     * Retourne la contribution exceptionnelle sur les hauts revenus.
     *
     * @return Contribution exceptionnelle
     */
    public double getContribExceptionnelle() {
        return contributionExceptionnelle;
    }
}