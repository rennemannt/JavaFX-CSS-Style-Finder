/*
 * Copyright 2017 Rennemann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rennemann.javafx.stylefinder.readers;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the DOM Reader.
 *
 * @author Travis Rennemann
 */
public class StyleClassDomReaderTest {

    private final File testFxml;

    public StyleClassDomReaderTest() throws URISyntaxException {
        testFxml = new File(StyleClassDomReader.class.getResource("/MainView.fxml").toURI());
    }

    /**
     * Test of find method, of class StyleClassDomReader.
     */
    @Test
    public void testFind() throws Exception {
        System.out.println("find");
        StyleClassDomReader instance = new StyleClassDomReader(testFxml);
        Set<String> expResult = new HashSet<>(Arrays.asList("run-ico", "browse-ico", "menu-bg", "export-ico", "earth-2d"));
        Set<String> result = instance.find();
        assertEquals(expResult, result);
    }
}
