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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Travis Rennemann
 */
public class StyleClassMatcher {

    private static final Pattern STLYE_CLASS_PATTERN = Pattern.compile("styleClass=\\\"(\\w{1,})\\\"");

    /**
     * Find all of the styleClass values in the given input string.
     *
     * @param input The string to search
     * @return
     */
    public static List<String> find(String input) {
        List<String> styleClasses = new ArrayList<>();
        Matcher matcher = STLYE_CLASS_PATTERN.matcher(input);
        while (matcher.find()) {
            if (matcher.groupCount() > 0) {
                styleClasses.add(matcher.group(1));
            }
        }

        return styleClasses;
    }
}
