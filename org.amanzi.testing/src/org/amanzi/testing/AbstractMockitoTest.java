/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.testing;

import java.util.Map;
import java.util.Map.Entry;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.mockito.stubbing.Stubber;
import org.mockito.verification.VerificationMode;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AbstractMockitoTest extends AbstractTest {

    protected Node getNodeMock() {
        return mock(Node.class);
    }

    protected Node getNodeMock(final Map<String, Object> values) {
        Node result = getNodeMock();

        for (Entry<String, Object> singleEntry : values.entrySet()) {
            when(result.getProperty(singleEntry.getKey())).thenReturn(singleEntry.getValue());
        }

        return result;
    }

    protected <T> T mock(final Class<T> mockedClass) {
        return Mockito.mock(mockedClass);
    }

    protected <T> OngoingStubbing<T> when(final T methodCall) {
        return Mockito.when(methodCall);
    }

    protected <T> T eq(final T value) {
        return Mockito.eq(value);
    }

    protected Stubber doThrow(final Throwable toBeThrown) {
        return Mockito.doThrow(toBeThrown);
    }

    protected <T> T verify(final T mock) {
        return Mockito.verify(mock);
    }

    protected <T> T verify(final T mock, final VerificationMode mode) {
        return Mockito.verify(mock, mode);
    }

    protected VerificationMode never() {
        return Mockito.never();
    }

    protected VerificationMode atLeastOnce() {
        return Mockito.atLeastOnce();
    }

    protected <T> T spy(final T object) {
        return Mockito.spy(object);
    }

    protected <T> T any(final Class<T> clazz) {
        return Mockito.any(clazz);
    }

    protected Stubber doReturn(final Object toBeReturned) {
        return Mockito.doReturn(toBeReturned);
    }

    protected void verifyNoMoreInteractions(final Object... mocks) {
        Mockito.verifyNoMoreInteractions(mocks);
    }

    protected VerificationMode atLeast(final int minNumberOfInvocations) {
        return Mockito.atLeast(minNumberOfInvocations);
    }

}
