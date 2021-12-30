package uk.co.autotrader.traverson.test.mockito;

import org.mockito.internal.stubbing.defaultanswers.ReturnsEmptyValues;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.co.autotrader.traverson.TraversonBuilder;

public class TraversonBuilderAnswer implements Answer<Object> {

    @Override
    public Object answer(InvocationOnMock invocation) {
        Class<?> returnType = invocation.getMethod().getReturnType();
        if (returnType == TraversonBuilder.class) {
            return invocation.getMock();
        } else {
            return new ReturnsEmptyValues().answer(invocation);
        }
    }
}
