import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.MarkerPassingConfigurationEvaluator;
import de.dailab.nsm.decomposition.graph.spreadingActivation.MarkerPassing.ParameterLearner.NfoldCrossvalidation;
import de.dailab.nsm.semanticDistanceMeasures.DataExample;

import java.util.List;

/**
 * Created by Johannes Fähndrich on 10.06.18 as part of his dissertation.
 */
public class WSMarkerPassingConfigurationEvaluator extends MarkerPassingConfigurationEvaluator {
    static WSTest wsTest = null;

    public WSMarkerPassingConfigurationEvaluator() {
        wsTest = new WSTest();
        wsTest.init();
    }

    @Override
    public double evaluateIndividual(MarkerPassingConfig candidate) {

        double overallResult = 0.0;
        List<DataExample> trainingWinogradSchema = NfoldCrossvalidation.getInstance(candidate.getFolds(), new WinogradSchemaDataSetReader()).getTrainingPairs();
        synchronized (trainingWinogradSchema) {
            int i =0;
            for (DataExample training : trainingWinogradSchema) {
                training.setResult(wsTest.evaluateWinogradSchema((WinogradSchemaData) training, candidate)); //DoubleNodeWithMultipleThresholds.class));
                overallResult += training.getResult();
                System.out.print("\r CurrentResult: "+ training.getResult() + " on WS " + ++i + " of " +trainingWinogradSchema.size()+" Winograd Scheamas.");
            }
        }
        return overallResult/trainingWinogradSchema.size();
    }
}
