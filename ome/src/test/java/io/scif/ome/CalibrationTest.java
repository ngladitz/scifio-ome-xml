/*
 * #%L
 * SCIFIO support for the OME data model (OME-XML and OME-TIFF).
 * %%
 * Copyright (C) 2013 - 2014 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package io.scif.ome;

import static org.junit.Assert.assertEquals;
import io.scif.FormatException;
import io.scif.Metadata;
import io.scif.SCIFIO;
import io.scif.ome.xml.meta.OMEMetadata;
import io.scif.util.FormatTools;

import java.io.IOException;

import net.imglib2.meta.Axes;

import org.junit.Test;

/**
 * Unit tests to ensure calibration values are preserved properly when
 * translating to/from the OME classes.
 * 
 * @author Mark Hiner
 */
public class CalibrationTest {

	private final SCIFIO scifio = new SCIFIO();
	private final String id =
		"testImg&lengths=512,512,2,3,4&axes=X,Y,Z,Time,Channel.fake";

	// Try setting calibration values, translate to OMEMetadata and
	// verify they persisted
	@Test
	public void testTranslateToOME() throws IOException, FormatException {
		// Parse source metadata
		final Metadata meta = scifio.initializer().parseMetadata(id);

		// Adjust calibration
		FormatTools.calibrate(meta.get(0).getAxis(Axes.X), 5.0, 0);
		FormatTools.calibrate(meta.get(0).getAxis(Axes.Y), 6.0, 0);
		FormatTools.calibrate(meta.get(0).getAxis(Axes.Z), 7.0, 0);
		FormatTools.calibrate(meta.get(0).getAxis(Axes.TIME), 8.0, 0);

		final OMEMetadata omeMeta = new OMEMetadata(scifio.getContext());

		// Translate to OMEMetadata
		scifio.translator().translate(meta, omeMeta, false);

		// Verify results
		assertEquals(omeMeta.getRoot().getPixelsPhysicalSizeX(0).getValue(),
			new Double(5.0));
		assertEquals(omeMeta.getRoot().getPixelsPhysicalSizeY(0).getValue(),
			new Double(6.0));
		assertEquals(omeMeta.getRoot().getPixelsPhysicalSizeZ(0).getValue(),
			new Double(7.0));
		assertEquals(omeMeta.getRoot().getPixelsTimeIncrement(0), new Double(8.0));
	}

}
