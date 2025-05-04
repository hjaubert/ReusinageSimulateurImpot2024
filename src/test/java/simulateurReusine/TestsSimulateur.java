package simulateurReusine;

import com.kerware.simulateurReusine.AdaptateurSimulateur;
import com.kerware.simulateurReusine.ICalculateurImpot;
import com.kerware.simulateurReusine.SituationFamiliale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestsSimulateur {

    private static ICalculateurImpot simulateur;

    @BeforeAll
    public static void setUp() {
        simulateur = new AdaptateurSimulateur();
    }

    public static Stream<Arguments> donneesPartsFoyerFiscal() {
        return Stream.of(
                Arguments.of(24000, "CELIBATAIRE", 0, 0, false, 1),
                Arguments.of(24000, "CELIBATAIRE", 1, 0, false, 1.5),
                Arguments.of(24000, "CELIBATAIRE", 2, 0, false, 2),
                Arguments.of(24000, "CELIBATAIRE", 3, 0, false, 3),
                Arguments.of(24000, "MARIE", 0, 0, false, 2),
                Arguments.of(24000, "PACSE", 0, 0, false, 2),
                Arguments.of(24000, "MARIE", 3, 1, false, 4.5),
                Arguments.of(24000, "DIVORCE", 2, 0, true, 2.5),
                Arguments.of(24000, "VEUF", 3, 0, true, 4.5)
        );
    }

    // COUVERTURE EXIGENCE : EXG_IMPOT_03
    @DisplayName("Tests du calcul des parts pour différents foyers fiscaux")
    @ParameterizedTest
    @MethodSource("donneesPartsFoyerFiscal")
    public void testNombreDeParts(int revenuNetDeclarant1, String situationFamiliale, int nbEnfantsACharge,
                                  int nbEnfantsSituationHandicap, boolean parentIsole, double nbPartsAttendu) {

        // Arrange
        simulateur.setRevenusNetDeclarant1(revenuNetDeclarant1);
        simulateur.setRevenusNetDeclarant2(0);
        simulateur.setSituationFamiliale(SituationFamiliale.valueOf(situationFamiliale));
        simulateur.setNbEnfantsACharge(nbEnfantsACharge);
        simulateur.setNbEnfantsSituationHandicap(nbEnfantsSituationHandicap);
        simulateur.setParentIsole(parentIsole);

        // Act
        simulateur.calculImpotSurRevenuNet();

        // Assert
        assertEquals(nbPartsAttendu, simulateur.getNbPartsFoyerFiscal());
    }

    public static Stream<Arguments> donneesAbattementFoyerFiscal() {
        return Stream.of(
                Arguments.of(4900, "CELIBATAIRE", 0, 0, false, 495), // < 495 => 495
                Arguments.of(12000, "CELIBATAIRE", 0, 0, false, 1200), // 10 %
                Arguments.of(200000, "CELIBATAIRE", 0, 0, false, 14171) // > 14171 => 14171
        );
    }

    // COUVERTURE EXIGENCE : EXG_IMPOT_03
    @DisplayName("Tests des abattements pour les foyers fiscaux")
    @ParameterizedTest
    @MethodSource("donneesAbattementFoyerFiscal")
    public void testAbattement(int revenuNetDeclarant1, String situationFamiliale, int nbEnfantsACharge,
                               int nbEnfantsSituationHandicap, boolean parentIsole, int abattementAttendu) {

        // Arrange
        simulateur.setRevenusNetDeclarant1(revenuNetDeclarant1);
        simulateur.setRevenusNetDeclarant2(0);
        simulateur.setSituationFamiliale(SituationFamiliale.valueOf(situationFamiliale));
        simulateur.setNbEnfantsACharge(nbEnfantsACharge);
        simulateur.setNbEnfantsSituationHandicap(nbEnfantsSituationHandicap);
        simulateur.setParentIsole(parentIsole);

        // Act
        simulateur.calculImpotSurRevenuNet();

        // Assert
        assertEquals(abattementAttendu, simulateur.getAbattement());
    }

    public static Stream<Arguments> donneesRevenusFoyerFiscal() {
        return Stream.of(
                Arguments.of(12000, "CELIBATAIRE", 0, 0, false, 0), // 0%
                Arguments.of(20000, "CELIBATAIRE", 0, 0, false, 199), // 11%
                Arguments.of(35000, "CELIBATAIRE", 0, 0, false, 2736), // 30%
                Arguments.of(95000, "CELIBATAIRE", 0, 0, false, 19284), // 41%
                Arguments.of(200000, "CELIBATAIRE", 0, 0, false, 60768) // 45%
        );
    }

    // COUVERTURE EXIGENCE : EXG_IMPOT_04
    @DisplayName("Tests des différents taux marginaux d'imposition")
    @ParameterizedTest
    @MethodSource("donneesRevenusFoyerFiscal")
    public void testTrancheImposition(int revenuNet, String situationFamiliale, int nbEnfantsACharge,
                                      int nbEnfantsSituationHandicap, boolean parentIsole, int impotAttendu) {

        // Arrange
        simulateur.setRevenusNetDeclarant1(revenuNet);
        simulateur.setRevenusNetDeclarant2(0);
        simulateur.setSituationFamiliale(SituationFamiliale.valueOf(situationFamiliale));
        simulateur.setNbEnfantsACharge(nbEnfantsACharge);
        simulateur.setNbEnfantsSituationHandicap(nbEnfantsSituationHandicap);
        simulateur.setParentIsole(parentIsole);

        // Act
        simulateur.calculImpotSurRevenuNet();

        // Assert
        assertEquals(impotAttendu, simulateur.getImpotSurRevenuNet());
    }

    // Tests individuels pour la classe PreconditionsValidator
    // Ces tests permettent de vérifier chaque branche indépendamment

    @Test
    @DisplayName("Test revenu net déclarant 1 négatif")
    public void testRevenuNetDeclarant1Negatif() {
        // Arrange
        simulateur.setRevenusNetDeclarant1(-1);
        simulateur.setRevenusNetDeclarant2(0);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> simulateur.calculImpotSurRevenuNet());
        assertEquals("Le revenu net ne peut pas être négatif", exception.getMessage());
    }

    @Test
    @DisplayName("Test revenu net déclarant 2 négatif")
    public void testRevenuNetDeclarant2Negatif() {
        // Arrange
        simulateur.setRevenusNetDeclarant1(30000);
        simulateur.setRevenusNetDeclarant2(-1);
        simulateur.setSituationFamiliale(SituationFamiliale.MARIE);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> simulateur.calculImpotSurRevenuNet());
        assertEquals("Le revenu net ne peut pas être négatif", exception.getMessage());
    }

    @Test
    @DisplayName("Test nombre d'enfants négatif")
    public void testNombreEnfantsNegatif() {
        // Arrange
        simulateur.setRevenusNetDeclarant1(30000);
        simulateur.setRevenusNetDeclarant2(0);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(-1);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> simulateur.calculImpotSurRevenuNet());
        assertEquals("Le nombre d'enfants ne peut pas être négatif", exception.getMessage());
    }

    @Test
    @DisplayName("Test nombre d'enfants handicapés négatif")
    public void testNombreEnfantsHandicapesNegatif() {
        // Arrange
        simulateur.setRevenusNetDeclarant1(30000);
        simulateur.setRevenusNetDeclarant2(0);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(2);
        simulateur.setNbEnfantsSituationHandicap(-1);
        simulateur.setParentIsole(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> simulateur.calculImpotSurRevenuNet());
        assertEquals("Le nombre d'enfants handicapés ne peut pas être négatif", exception.getMessage());
    }

    @Test
    @DisplayName("Test situation familiale null")
    public void testSituationFamilialeNull() {
        // Arrange
        simulateur.setRevenusNetDeclarant1(30000);
        simulateur.setRevenusNetDeclarant2(0);
        simulateur.setSituationFamiliale(null);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> simulateur.calculImpotSurRevenuNet());
        assertEquals("La situation familiale doit être définie", exception.getMessage());
    }

    @Test
    @DisplayName("Test plus d'enfants handicapés que d'enfants total")
    public void testPlusEnfantsHandicapesQueTotal() {
        // Arrange
        simulateur.setRevenusNetDeclarant1(30000);
        simulateur.setRevenusNetDeclarant2(0);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(2);
        simulateur.setNbEnfantsSituationHandicap(3);
        simulateur.setParentIsole(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> simulateur.calculImpotSurRevenuNet());
        assertEquals("Le nombre d'enfants handicapés ne peut pas être supérieur au nombre d'enfants", exception.getMessage());
    }

    @Test
    @DisplayName("Test trop d'enfants")
    public void testTropEnfants() {
        // Arrange
        simulateur.setRevenusNetDeclarant1(30000);
        simulateur.setRevenusNetDeclarant2(0);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(8);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> simulateur.calculImpotSurRevenuNet());
        assertEquals("Le nombre d'enfants ne peut pas être supérieur à 7", exception.getMessage());
    }

    @Test
    @DisplayName("Test parent isolé marié")
    public void testParentIsoleMarié() {
        // Arrange
        simulateur.setRevenusNetDeclarant1(30000);
        simulateur.setRevenusNetDeclarant2(0);
        simulateur.setSituationFamiliale(SituationFamiliale.MARIE);
        simulateur.setNbEnfantsACharge(2);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> simulateur.calculImpotSurRevenuNet());
        assertEquals("Un parent isolé ne peut pas être marié ou pacsé", exception.getMessage());
    }

    @Test
    @DisplayName("Test parent isolé pacsé")
    public void testParentIsolePacsé() {
        // Arrange
        simulateur.setRevenusNetDeclarant1(30000);
        simulateur.setRevenusNetDeclarant2(0);
        simulateur.setSituationFamiliale(SituationFamiliale.PACSE);
        simulateur.setNbEnfantsACharge(2);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> simulateur.calculImpotSurRevenuNet());
        assertEquals("Un parent isolé ne peut pas être marié ou pacsé", exception.getMessage());
    }

    @Test
    @DisplayName("Test célibataire avec revenu déclarant 2")
    public void testCelibataireAvecRevenuDeclarant2() {
        // Arrange
        simulateur.setRevenusNetDeclarant1(30000);
        simulateur.setRevenusNetDeclarant2(20000);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> simulateur.calculImpotSurRevenuNet());
        assertEquals("Un célibataire, un divorcé ou un veuf ne peut pas avoir de revenu déclarant 2", exception.getMessage());
    }

    @Test
    @DisplayName("Test divorcé avec revenu déclarant 2")
    public void testDivorceAvecRevenuDeclarant2() {
        // Arrange
        simulateur.setRevenusNetDeclarant1(30000);
        simulateur.setRevenusNetDeclarant2(20000);
        simulateur.setSituationFamiliale(SituationFamiliale.DIVORCE);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> simulateur.calculImpotSurRevenuNet());
        assertEquals("Un célibataire, un divorcé ou un veuf ne peut pas avoir de revenu déclarant 2", exception.getMessage());
    }

    @Test
    @DisplayName("Test veuf avec revenu déclarant 2")
    public void testVeufAvecRevenuDeclarant2() {
        // Arrange
        simulateur.setRevenusNetDeclarant1(30000);
        simulateur.setRevenusNetDeclarant2(20000);
        simulateur.setSituationFamiliale(SituationFamiliale.VEUF);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> simulateur.calculImpotSurRevenuNet());
        assertEquals("Un célibataire, un divorcé ou un veuf ne peut pas avoir de revenu déclarant 2", exception.getMessage());
    }

    public static Stream<Arguments> donneesRobustesse() {
        return Stream.of(
                Arguments.of(-1, 0, "CELIBATAIRE", 0, 0, false), // Revenu négatif
                Arguments.of(20000, 0, null, 0, 0, false), // Situation familiale null
                Arguments.of(35000, 0, "CELIBATAIRE", -1, 0, false), // Nb enfants négatif
                Arguments.of(95000, 0, "CELIBATAIRE", 0, -1, false), // Nb enfants handicapés négatif
                Arguments.of(200000, 0, "CELIBATAIRE", 3, 4, false), // Plus d'enfants handicapés que d'enfants
                Arguments.of(200000, 0, "MARIE", 3, 2, true), // Parent isolé marié
                Arguments.of(200000, 0, "PACSE", 3, 2, true), // Parent isolé pacsé
                Arguments.of(200000, 0, "MARIE", 8, 0, false), // Trop d'enfants
                Arguments.of(200000, 10000, "CELIBATAIRE", 8, 0, false), // Célibataire avec revenus déclarant 2
                Arguments.of(200000, 10000, "VEUF", 8, 0, false), // Veuf avec revenus déclarant 2
                Arguments.of(200000, 10000, "DIVORCE", 8, 0, false) // Divorcé avec revenus déclarant 2
        );
    }

    // COUVERTURE EXIGENCE : Robustesse
    @DisplayName("Tests de robustesse avec des valeurs interdites")
    @ParameterizedTest(name ="Test avec revenuNetDeclarant1={0}, revenuDeclarant2={1}, situationFamiliale={2}, nbEnfantsACharge={3}, nbEnfantsSituationHandicap={4}, parentIsole={5}")
    @MethodSource("donneesRobustesse")
    public void testRobustesse(int revenuNetDeclarant1, int revenuNetDeclarant2, String situationFamiliale, int nbEnfantsACharge,
                               int nbEnfantsSituationHandicap, boolean parentIsole) {

        // Arrange
        simulateur.setRevenusNetDeclarant1(revenuNetDeclarant1);
        simulateur.setRevenusNetDeclarant2(revenuNetDeclarant2);
        if (situationFamiliale == null)
            simulateur.setSituationFamiliale(null);
        else
            simulateur.setSituationFamiliale(SituationFamiliale.valueOf(situationFamiliale));
        simulateur.setNbEnfantsACharge(nbEnfantsACharge);
        simulateur.setNbEnfantsSituationHandicap(nbEnfantsSituationHandicap);
        simulateur.setParentIsole(parentIsole);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> simulateur.calculImpotSurRevenuNet());
    }

    // AVEC D'AUTRES IDEES DE TESTS
    // AVEC @ParameterizedTest et @CsvFileSource
    @DisplayName("Tests supplémentaires de cas variés de foyers fiscaux - ")
    @ParameterizedTest(name = " avec revenuNetDeclarant1={0}, revenuNetDeclarant2={1}, situationFamiliale={2}, nbEnfantsACharge={3}, nbEnfantsSituationHandicap={4}, parentIsole={5} - IMPOT NET ATTENDU = {6}")
    @CsvFileSource(resources={"/datasImposition.csv"}, numLinesToSkip = 1)
    public void testCasImposition(int revenuNetDeclarant1, int revenuNetDeclarant2, String situationFamiliale, int nbEnfantsACharge,
                                  int nbEnfantsSituationHandicap, boolean parentIsole, int impotAttendu) {

        // Arrange
        simulateur.setRevenusNetDeclarant1(revenuNetDeclarant1);
        simulateur.setRevenusNetDeclarant2(revenuNetDeclarant2);
        simulateur.setSituationFamiliale(SituationFamiliale.valueOf(situationFamiliale));
        simulateur.setNbEnfantsACharge(nbEnfantsACharge);
        simulateur.setNbEnfantsSituationHandicap(nbEnfantsSituationHandicap);
        simulateur.setParentIsole(parentIsole);

        // Act
        simulateur.calculImpotSurRevenuNet();

        // Assert
        assertEquals(impotAttendu, simulateur.getImpotSurRevenuNet());
    }
}