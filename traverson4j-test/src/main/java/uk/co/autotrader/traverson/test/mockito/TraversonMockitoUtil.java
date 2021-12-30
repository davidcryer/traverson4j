package uk.co.autotrader.traverson.test.mockito;

import uk.co.autotrader.traverson.TraversonBuilder;

import static org.mockito.Mockito.mock;

public class TraversonMockitoUtil {

    public static TraversonBuilder mockTraversonBuilder() {
        return mock(TraversonBuilder.class, new TraversonBuilderAnswer());
    }
}
