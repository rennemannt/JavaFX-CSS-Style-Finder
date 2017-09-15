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
package org.rennemann.javafx.stylefinder.matchers;

import java.util.Arrays;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for the StyleClassMatcher.
 *
 * @author Travis Rennemann
 */
public class StyleClassMatcherTest {

    /**
     * Test of find method, of class StyleClassMatcher.
     */
    @Test
    public void testFind() {
        System.out.println("test StyleClassMatcher -> find");
        String input = "<Label minHeight=\"-Infinity\" minWidth=\"-Infinity\" styleClass=\"srch_line_item_label\" text=\"Data Type:\" AnchorPane.rightAnchor=\"0.0\" AnchorPane.topAnchor=\"0.0\" HBox.hgrow=\"NEVER\" />";
        List<String> expResult = Arrays.asList("srch_line_item_label");
        List<String> result = StyleClassMatcher.find(input);
        assertEquals(expResult, result);
    }

}
