package com.kerware.simulateur;

/**
 * Adaptateur pour la classe SimulateurRefactorise implémentant l'interface ICalculateurImpot.
 * Cette classe fait le pont entre l'ancien code et le nouveau système.
 *
 * @author [Votre Nom]
 * @version 1.0
 */
public final class AdaptateurSimulateur implements ICalculateurImpot {

    /**
     * Instance du simulateur refactorisé.
     */
    private final Simulateur simulateur;

    /**
     * Revenu net du premier déclarant.
     */
    private int revenusNetDecl1;

    /**
     * Revenu net du deuxième déclarant.
     */
    private int revenusNetDecl2;

    /**
     * Situation familiale du contribuable.
     */
    private SituationFamiliale situationFamiliale;

    /**
     * Nombre d'enfants à charge.
     */
    private int nbEnfantsACharge;

    /**
     * Nombre d'enfants en situation de handicap.
     */
    private int nbEnfantsSituationHandicap;

    /**
     * Indique si le contribuable est un parent isolé.
     */
    private boolean parentIsole;

    /**
     * Résultat du calcul d'impôt.
     */
    private int impotCalcule;

    /**
     * Constructeur par défaut.
     */
    public AdaptateurSimulateur() {
        this.simulateur = new Simulateur();
        reinitialiser();
    }

    /**
     * Réinitialise les attributs de l'adaptateur.
     */
    private void reinitialiser() {
        this.revenusNetDecl1 = 0;
        this.revenusNetDecl2 = 0;
        this.situationFamiliale = null;
        this.nbEnfantsACharge = 0;
        this.nbEnfantsSituationHandicap = 0;
        this.parentIsole = false;
        this.impotCalcule = 0;
    }

    @Override
    public void setRevenusNetDeclarant1(int rn) {
        this.revenusNetDecl1 = rn;
    }

    @Override
    public void setRevenusNetDeclarant2(int rn) {
        this.revenusNetDecl2 = rn;
    }

    @Override
    public void setSituationFamiliale(SituationFamiliale sf) {
        this.situationFamiliale = sf;
    }

    @Override
    public void setNbEnfantsACharge(int nbe) {
        this.nbEnfantsACharge = nbe;
    }

    @Override
    public void setNbEnfantsSituationHandicap(int nbesh) {
        this.nbEnfantsSituationHandicap = nbesh;
    }

    @Override
    public void setParentIsole(boolean pi) {
        this.parentIsole = pi;
    }

    @Override
    public void calculImpotSurRevenuNet() {
        if (this.situationFamiliale == null) {
            throw new IllegalArgumentException("La situation familiale doit être définie");
        }

        this.impotCalcule = simulateur.calculImpot(
                revenusNetDecl1,
                revenusNetDecl2,
                situationFamiliale,
                nbEnfantsACharge,
                nbEnfantsSituationHandicap,
                parentIsole
        );
    }

    @Override
    public int getRevenuNetDeclatant1() {
        return revenusNetDecl1;
    }

    @Override
    public int getRevenuNetDeclatant2() {
        return revenusNetDecl2;
    }

    @Override
    public double getContribExceptionnelle() {
        return simulateur.getCalculFiscale().getContributionExceptionnelle();
    }

    @Override
    public int getRevenuFiscalReference() {
        return (int) simulateur.getCalculFiscale().getRevenuFiscalReference();
    }

    @Override
    public int getAbattement() {
        return (int) simulateur.getCalculFiscale().getAbattement();
    }

    @Override
    public double getNbPartsFoyerFiscal() {
        return simulateur.getCalculFiscale().getNombrePartsFoyer();
    }

    @Override
    public int getImpotAvantDecote() {
        return (int) simulateur.getCalculFiscale().getImpotAvantDecote();
    }

    @Override
    public int getDecote() {
        return (int) simulateur.getCalculFiscale().getDecote();
    }

    @Override
    public int getImpotSurRevenuNet() {
        return (int) simulateur.getCalculFiscale().getImpotNet();
    }
}