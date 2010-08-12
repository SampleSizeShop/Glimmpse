package edu.cudenver.bios.glimmpse.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.i18n.client.Constants;

import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener.SolutionType;

public interface GlimmpseConstants extends Constants
{
    // shared style names for input wizard steps
	public static final String STYLE_DISABLED = "disabled";
	public static final String STYLE_GLIMMPSE_PANEL = "glimmpsePanel";
    public static final String STYLE_WIZARD_STEP_PANEL = "wizardStepPanel";
    public static final String STYLE_WIZARD_STEP_INPUT_CONTAINER = "wizardStepInputContainer";
    public static final String STYLE_WIZARD_STEP_HEADER = "wizardStepHeader";
    public static final String STYLE_WIZARD_STEP_DESCRIPTION = "wizardStepDescription";
    public static final String STYLE_WIZARD_STEP_SUBPANEL = "subpanel";
    public static final String STYLE_WIZARD_STEP_SELECTED = "selected";
    public static final String STYLE_WIZARD_STEP_DESELECTED = "deselected";
    public static final String STYLE_WIZARD_STEP_EVEN = "even";
    public static final String STYLE_WIZARD_STEP_ODD = "odd";
    public static final String STYLE_WIZARD_STEP_BUTTON = "wizardStepButton";
    
    public static final String STYLE_WIZARD_STEP_TABLE_PANEL = "wizardStepTablePanel";
    public static final String STYLE_WIZARD_STEP_TABLE = "wizardStepTable";
    public static final String STYLE_WIZARD_STEP_TABLE_HEADER = "wizardStepTableHeader";
    public static final String STYLE_WIZARD_STEP_TABLE_ROW = "wizardStepTableRow";
    
    public static final String STYLE_WIZARD_STEP_LIST_PANEL = "wizardStepListPanel";
    public static final String STYLE_WIZARD_STEP_LIST = "wizardStepList";
    public static final String STYLE_WIZARD_STEP_LIST_BUTTON = "wizardStepListButton";
    public static final String STYLE_WIZARD_STEP_LIST_TEXTBOX = "wizardStepListTextBox";
    public static final String STYLE_WIZARD_STEP_LIST_HEADER = "wizardStepListHeader";

    public static final String STYLE_MATRIX_PANEL = "matrixPanel";
    public static final String STYLE_MATRIX_DIMENSION = "matrixDimensions";
    public static final String STYLE_MATRIX_DATA = "matrixData";
    public static final String STYLE_MATRIX_CELL= "matrixCell";
    
    public static final String STYLE_MESSAGE = "message";
    public static final String STYLE_MESSAGE_ERROR = "error";
    public static final String STYLE_MESSAGE_OKAY = "okay";

    // REST API tag names
    public static final String TAG_POWER_PARAMETERS = "glmmPowerParameters";
    public static final String TAG_STUDY = "study";
    public static final String TAG_SOLVING_FOR = "solvingFor";
    public static final String TAG_POWER_LIST = "powerList";
    public static final String TAG_ALPHA_LIST = "alphaList";
    public static final String TAG_OPTIONS = "options";
    public static final String TAG_SAMPLE_SIZE_LIST = "sampleSizeList";
    public static final String TAG_MATRIX = "matrix";
    public static final String TAG_ROW_META_DATA = "rowMetaData";
    public static final String TAG_ESSENCE_MATRIX = "essenceMatrix";
    public static final String TAG_FIXED_RANDOM_MATRIX = "fixedRandomMatrix";
    public static final String TAG_BETA_SCALE_LIST = "betaScaleList";
    public static final String ATTR_MODE = "mode";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_ROWS = "rows";
    public static final String ATTR_COLUMNS = "columns";
    
    // mode names
    public static final String MODE_MATRIX = "matrix";
    public static final String MODE_GUIDED = "guided";

    // matrix names
    public static final String MATRIX_FIXED = "fixed";
    public static final String MATRIX_RANDOM = "random";
    public static final String MATRIX_DESIGN_FIXED = "design";
    public static final String MATRIX_DESIGN_RANDOM = "designRandom";
    public static final String MATRIX_BETA = "beta";
    public static final String MATRIX_BETA_RANDOM = "betaRandom";
    public static final String MATRIX_BETWEEN_CONTRAST = "betweenSubjectContrast";
    public static final String MATRIX_BETWEEN_CONTRAST_RANDOM = "betweenSubjectContrastRandom";
    public static final String MATRIX_WITHIN_CONTRAST = "withinSubjectContrast";
    public static final String MATRIX_SIGMA_ERROR = "sigmaError";
    public static final String MATRIX_SIGMA_OUTCOME = "sigmaOutcome";
    public static final String MATRIX_SIGMA_OUTCOME_COVARIATE = "sigmaOutcomeGaussianRandom ";
    public static final String MATRIX_SIGMA_COVARIATE = "sigmaGaussianRandom";
    public static final String MATRIX_THETA = "theta";
    
    // test names
    public static final String TEST_UNIREP = "unirep";
    public static final String TEST_UNIREP_GEISSER_GRENNHOUSE = "unirepGG";
    public static final String TEST_UNIREP_HUYNH_FELDT = "unirepHF";
    public static final String TEST_UNIREP_BOX = "unirepBox";
    public static final String TEST_WILKS_LAMBDA = "wl";
    public static final String TEST_PILLAI_BARTLETT_TRACE= "pbt";
    public static final String TEST_HOTELLING_LAWLEY_TRACE = "hlt";

	// dimension names derived from linear model theory.
	// ensures that default matrix dimensions conform properly
    public static final int DEFAULT_N = 3;
    public static final int DEFAULT_Q = 3;
    public static final int DEFAULT_P = 2;
    public static final int DEFAULT_A = 2;
    public static final int DEFAULT_B = 1;
    
    // power method names for the REST api
    public static final String POWER_METHOD_CONDITIONAL = "conditional";
    public static final String POWER_METHOD_UNCONDITIONAL = "unconditional";
    public static final String POWER_METHOD_QUANTILE = "quantile";

    // default solving for value
    public static final SolutionType DEFAULT_SOLUTION = SolutionType.POWER;
    public static final String SOLUTION_TYPE_POWER = "power";
    public static final String SOLUTION_TYPE_SAMPLE_SIZE = "sampleSize";
    public static final String SOLUTION_TYPE_EFFECT_SIZE = "effectSize";

    // toolbar buttons
    public String buttonSaveStudy();
    public String buttonCancel();
    
    // navigation buttons
    public String buttonNext();
    public String buttonPrevious();
    
    // start panel constants
    public String startTitle();
    public String startDescription();
    public String startGoButton();
    public String startMatrixTitle();
    public String startMatrixDescription();
    public String startGuidedTitle();
    public String startGuidedDescription();
    public String startUploadTitle();
    public String startUploadDescription();
    
    // left navigation / steps left panel
    public String stepsLeftOutcomes();
    public String stepsLeftPredictors();
    public String stepsLeftGroups();
    public String stepsLeftHypotheses();
    public String stepsLeftEffectSize();
    public String stepsLeftVariability();
    public String stepsLeftAlpha();
    public String stepsLeftOptions();
    public String stepsLeftResults();
    public String stepsLeftDesign();
    public String stepsLeftContrast();
    public String stepsLeftBeta();
    public String stepsLeftTheta();
    public String stepsLeftSigma();
    public String stepsLeftSolvingFor();
    
    // tool bar menus / items
    public String toolBarSaveMenu();
    public String toolBarSaveStudyMenuItem();
    public String toolBarSaveResultsMenuItem();
    public String toolBarSaveCurveMenuItem();
    public String toolBarClearMenu();
    public String toolBarClearAllMenuItem();
    public String toolBarClearScreenMenuItem();
    public String toolBarHelpMenu();
    public String toolBarHelpManualMenuItem();
    
    // "solving for" panel constants
    public String solvingForTitle();
    public String solvingForDescription();
    public String solvingForPowerLabel();
    public String solvingForSampleSizeLabel();
    public String solvingForEffectSizeLabel();
    public String solvingForNominalPowerTitle();
    public String solvingForNominalPowerDescription();
    public String solvingForNominalPowerTableColumn();
    
    // alpha panel constants
    public String alphaTitle();
    public String alphaDescription();
    public String alphaTableColumn();
    
    // outcomes panel constants
    public String outcomesTitle();
    public String outcomesDescription();
    public String outcomesTableColumn();
    public String repeatedMeasuresTitle();
    public String repeatedMeasuresDescription();
    public String repeatedOverTableColumn();
    public String repetitionsTableColumn();
    
    // predictors panel constants
    public String predictorsTitle();
    public String predictorsDescription();
    public String predictorsTableColumn();
    public String categoriesTableColumn();
    public String categoricalTitle();
    public String categoricalDescription();
    public String covariateTitle();
    public String predictorsCovariateDescription();
    public String matrixCovariateDescription();
    public String covariateCheckBoxLabel();
    public String covariateMeanLabel();
    public String covariateStandardDeviationLabel();

    // study groups panel constants
    public String studyGroupsTitle();
    public String studyGroupsDescription();
    public String relativeGroupSizeTitle();
    public String relativeGroupSizeDescription();
    public String relativeGroupSizeTableColumn();
    public String perGroupSampleSizeTitle();
    public String perGroupSampleSizeDescription();
    public String perGroupSampleSizeTableColumn();

    // hypotheses panel constants
    public String hypothesisTitle();
    public String hypothesisDescription();

    // effect size panel
    public String effectSizeTitle();
    public String effectSizeDescription();
    public String effectSizeScalingTitle();
    public String effectSizeScalingDescription();
    public String effectSizeTableColumn();

    // variability panel
    public String variabilityTitle();
    public String variabilityDescription();
    
    // options panel constants
    public String optionsTitle();
    public String optionsDescription();
    public String testTitle();
    public String testDescription();
    public String testUnirepLabel();
    public String testUnirepGeisserGreenhouseLabel();
    public String testUnirepHuynhFeldtLabel();
    public String testUnirepBoxLabel();
    public String testWilksLambdaLabel();
    public String testPillaiBartlettTraceLabel();
    public String testHotellingLawleyTraceLabel();
    // power method constants
    public String powerMethodTitle();
    public String powerMethodDescription();
    public String powerMethodConditionalLabel();
    public String powerMethodQuantileLabel();
    public String powerMethodUnconditionalLabel();
    public String quantilesTableColumn();
    // display constants
    public String displayOptionsTitle();
    public String displayOptionsDescription();
    public String displayOptionsTableLabel();
    public String displayOptionsCurveLabel();
    public String displayOptionsXAxisLabel();
    public String displayOptionsXAxisSampleSizeLabel();
    public String displayOptionsXAxisEffectSizeLabel();
    public String displayOptionsXAxisVarianceLabel();

    // matrix constants
    public String matrixDimensionSeparator();
    
    // design matrix screen
    public String matrixDesignTitle();
    public String matrixDesignDescription();
    public String matrixCategoricalEffectsLabel();
    public String matrixCovariateEffectsLabel();
    
    // beta matrix screen
    public String matrixBetaTitle();
    public String matrixBetaDescription();
    public String matrixBetaScalingTitle();
    public String matrixBetaScalingDescription();
    public String matrixBetaScaleTableColumn();
    public String matrixBetaFixedMatrixName();
    public String matrixBetaGaussianMatrixName();

    // sigma matrix screen
    public String sigmaScaleTableColumn();
    
    // error messages
    public String errorUploadFailed();
    public String errorUploadInvalidStudyFile();
    public String errorInvalidAlpha();
    public String errorInvalidMean();
    public String errorInvalidStandardDeviation();
    public String errorInvalidQuantile();
    public String errorInvalidPower();
    public String errorInvalidNumber();
    
    // confirm messages
    public String confirmClearScreen();
    public String confirmClearAll();
}
