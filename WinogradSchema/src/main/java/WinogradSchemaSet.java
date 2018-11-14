import java.io.IOException;
import java.util.Collection;

/**
 * Created by faehndrich on 30.09.16.
 */
public interface WinogradSchemaSet {
    Collection<WinogradSchemaData> readExampleDataSet(String path2DataSet);
    Collection<WinogradSchemaData> getNYUDataSet() throws IOException;
    Collection<WinogradSchemaData> getRahmanDataSet() throws IOException;
    Collection<WinogradSchemaData> readPDPChallangeDataset() throws IOException;
    Collection<WinogradSchemaData> readWSChallangeDataset() throws IOException;

    Collection<WinogradSchemaData> getLevesqueDataSet() throws IOException;

    Collection<WinogradSchemaData> getWSCDataSet() throws IOException;




}
