/*
 * Copyright 2017 Pete Cornish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.apiman.cli.managerapi.management;

import io.apiman.cli.exception.CommandException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

import java.net.HttpURLConnection;
import java.util.function.Supplier;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Tests for {@link ManagementApiUtil}.
 *
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ManagementApiUtilTest {
    private static final String URL = "http://example.com";

    @Mock
    private Supplier<Response> request;

    private Response response;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(request);
    }

    @Test
    public void testInvokeAndCheckResponse_StatusMatch() throws Exception {
        // test data
        response = new Response(URL, HttpURLConnection.HTTP_OK, "OK",
                newArrayList(), new TypedString("test body"));

        // mock behaviour
        when(request.get()).thenReturn(response);

        // test
        ManagementApiUtil.invokeAndCheckResponse(request);

        // assertions
        verify(request).get();
    }

    @Test
    public void testInvokeAndCheckResponse_StatusMismatch() throws Exception {
        // test data
        response = new Response(URL, HttpURLConnection.HTTP_NOT_FOUND, "Not Found",
                newArrayList(), new TypedString(""));

        // mock behaviour
        when(request.get()).thenReturn(response);

        // test
        try {
            ManagementApiUtil.invokeAndCheckResponse(HttpURLConnection.HTTP_OK, request);
            fail(CommandException.class + " expected");

        } catch (CommandException ignored) {
            // verify behaviour
            verify(request).get();
        }
    }

    @Test
    public void testInvokeAndCheckResponse_RetrofitError() throws Exception {
        // test data
        response = new Response(URL, HttpURLConnection.HTTP_NOT_FOUND, "Not Found",
                newArrayList(), new TypedString(""));

        // mock behaviour
        when(request.get()).thenThrow(RetrofitError.httpError(URL, response, null, null));

        // test
        try {
            ManagementApiUtil.invokeAndCheckResponse(HttpURLConnection.HTTP_OK, request);
            fail(CommandException.class + " expected");

        } catch (CommandException ignored) {
            // verify behaviour
            verify(request).get();
        }
    }

    @Test
    public void testInvokeAndCheckResponse_RetrofitErrorNullResponse() throws Exception {
        // test data
        response = null;

        // mock behaviour
        when(request.get()).thenThrow(RetrofitError.unexpectedError(URL, new RuntimeException("Test exception")));

        // test
        try {
            ManagementApiUtil.invokeAndCheckResponse(HttpURLConnection.HTTP_OK, request);
            fail(IllegalArgumentException.class + " expected");

        } catch (IllegalArgumentException ignored) {
            // verify behaviour
            verify(request).get();
        }
    }
}
