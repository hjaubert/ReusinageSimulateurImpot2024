package com.kerware.simulateur;

public class CalculFiscale {

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

    public CalculFiscale() {
        reinitialiser();
    }

    /**
     *
     * @return le revenu fiscale de référence
     */
    public double getRevenuFiscalReference() {
        return revenuFiscalReference;
    }

    /**
     *
     * @return l'abattement
     */
    public double getAbattement() {
        return abattement;
    }

    /**
     *
     * @return le nombre de parts du foyer
     */
    public double getNombrePartsFoyer() {
        return nombrePartsFoyer;
    }

    /**
     *
     * @return la decote
     */
    public double getDecote() {
        return decote;
    }

    /**
     *
     * @return l'impot avant la decote
     */
    public double getImpotAvantDecote() {
        return impotAvantDecote;
    }

    /**
     *
     * @return la valeur de la contribution exceptionnelle
     */
    public double getContributionExceptionnelle() {
        return contributionExceptionnelle;
    }

    /**
     * Reinitialise toutes les variables
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
     *
     * @param revNetDecl1
     * @param revNetDecl2
     * @param nbEnfants
     * @param nbEnfantsHandicapes
     * @param parentIsol
     */
    public void initialiserDonnees(int revNetDecl1, int revNetDecl2, int nbEnfants,
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
    public void calculerAbattement(SituationFamiliale sitFam) {
        this.abattement = AbattementCalculator.calculer(
                revenuNetDeclarant1, revenuNetDeclarant2, sitFam
        );
    }

    /**
     * Calcule le revenu fiscal de référence.
     */
    public void calculerRevenuFiscalReference() {
        this.revenuFiscalReference = revenuNetDeclarant1 + revenuNetDeclarant2 - abattement;
        if (this.revenuFiscalReference < 0) {
            this.revenuFiscalReference = 0;
        }
    }

    /**
     * Calcule le nombre de parts du foyer fiscal.
     * EXG_IMPOT_03
     */
    public void calculerNombreParts(SituationFamiliale sitFam) {
        // Calcul des parts pour les déclarants
        this.nombrePartsDeclarants = sitFam.getNombrePartsDeclarants();

        // Calcul des parts pour les enfants à charge
        this.nombrePartsFoyer = this.nombrePartsDeclarants;

        if (nombreEnfants <= ConstantesFiscales.NB_ENFANTS_DEMI_PART ) {
            this.nombrePartsFoyer += nombreEnfants * ConstantesFiscales.PART_ENFANT;
        } else {
            this.nombrePartsFoyer += 1.0 + (
                    nombreEnfants - ConstantesFiscales.NB_ENFANTS_DEMI_PART
            );
        }

        // Ajout pour parent isolé
        if (parentIsole && nombreEnfants > 0) {
            this.nombrePartsFoyer += ConstantesFiscales.PART_PARENT_ISOLE;
        }

        // Cas spécial pour veuf avec enfant
        if (sitFam == SituationFamiliale.VEUF && nombreEnfants > 0) {
            this.nombrePartsFoyer += 1;
        }

        // Ajout pour enfants en situation de handicap
        this.nombrePartsFoyer += nombreEnfantsHandicap * ConstantesFiscales.PART_ENFANT;
    }

    /**
     * Calcule la contribution exceptionnelle sur les hauts revenus.
     * EXG_IMPOT_07
     */
    public void calculerContributionExceptionnelle() {
        this.contributionExceptionnelle = 0;

        LimiteCEHR[] limites = LimiteCEHR.values();
        TauxCEHR[] taux = TauxCEHR.values();

        for (int i = 0; i < taux.length; i++) {
            int borneInf = limites[i].getLimite();
            int borneSup = limites[i + 1].getLimite();

            if (revenuFiscalReference > borneInf) {
                double montantTranche = Math.min(revenuFiscalReference, borneSup) - borneInf;
                double tauxApplicable;
                if (nombrePartsDeclarants == 1) {
                    tauxApplicable = taux[i].getTauxCelibataire();
                } else {
                    tauxApplicable = taux[i].getTauxCouple();
                }
                this.contributionExceptionnelle += montantTranche * tauxApplicable;
            }
        }

        this.contributionExceptionnelle = Math.round(this.contributionExceptionnelle);
    }


    /**
     * Calcule l'impôt brut des déclarants.
     * EXG_IMPOT_04
     */
    public void calculerImpotDeclarants() {
        double revenuImposable = revenuFiscalReference / nombrePartsDeclarants;
        this.impotDeclarants = calculerImpotParTranches(revenuImposable) * nombrePartsDeclarants;
        this.impotDeclarants = Math.round(this.impotDeclarants);
    }

    /**
     * Calcule l'impôt brut du foyer fiscal.
     * EXG_IMPOT_04
     */
    public void calculerImpotFoyer() {
        double revenuImposable = revenuFiscalReference / nombrePartsFoyer;
        this.impotFoyer = calculerImpotParTranches(revenuImposable) * nombrePartsFoyer;
        this.impotFoyer = Math.round(this.impotFoyer);
    }

    /**
     * Calcule l'impôt pour un revenu donné selon les tranches d'imposition.
     */
    public double calculerImpotParTranches(double revenuImposable) {
        return TrancheImposition.calculerImpot(revenuImposable);
    }

    /**
     * Applique le plafonnement des effets du quotient familial.
     * EXG_IMPOT_05
     */
    public void appliquerPlafonnement() {
        double baisseImpot = impotDeclarants - impotFoyer;
        double ecartParts = nombrePartsFoyer - nombrePartsDeclarants;
        double plafond = (ecartParts / ConstantesFiscales.PART_ENFANT)
                * PlafonnementDecote.PLAFOND_DEMI_PART;

        if (baisseImpot >= plafond) {
            this.impotFoyer = impotDeclarants - plafond;
        }

        this.impotAvantDecote = this.impotFoyer;
    }

    /**
     * Calcule la décote.
     * EXG_IMPOT_06
     */
    public void calculerDecote() {
        this.decote = 0;

        if (nombrePartsDeclarants == 1) {
            if (impotFoyer < PlafonnementDecote.SEUIL_DECOTE_SEUL) {
                this.decote = PlafonnementDecote.DECOTE_MAX_SEUL
                        - (impotFoyer * PlafonnementDecote.TAUX_DECOTE);
            }
        } else if (nombrePartsDeclarants == 2) {
            if (impotFoyer < PlafonnementDecote.SEUIL_DECOTE_COUPLE) {
                this.decote = PlafonnementDecote.DECOTE_MAX_COUPLE
                        - (impotFoyer * PlafonnementDecote.TAUX_DECOTE);
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
    public int calculerImpotFinal() {
        double impotFinal = impotFoyer - decote + contributionExceptionnelle;
        return (int) Math.round(impotFinal);
    }

    /**
     * Retourne l'impôt net final.
     *
     * @return Impôt net final
     */
    public double getImpotNet() {
        return impotFoyer - decote + contributionExceptionnelle;
    }
}
