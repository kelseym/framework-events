/*
 * org.nrg.framework.utilities.serializers.RemoteEventJsonSerializer
 * TIP is developed by the Neuroinformatics Research Group
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 6/11/14 3:24 PM
 */

package org.nrg.framework.utilities.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.nrg.framework.logging.RemoteEvent;

import java.io.IOException;

public class RemoteEventJsonSerializer extends JsonSerializer<RemoteEvent> {
    @Override
    public void serialize(final RemoteEvent value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {

    }
}
