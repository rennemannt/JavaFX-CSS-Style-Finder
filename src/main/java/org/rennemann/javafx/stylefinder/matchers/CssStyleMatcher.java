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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JavaFX CSS style finder.
 *
 * @author Travis Rennemann
 */
public class CssStyleMatcher {

    private static final Pattern CSS_STYLE_PATTERN = Pattern.compile("([^.\\}\\{]+)(\\s{0,}\\{[^\\}]+\\})", Pattern.DOTALL | Pattern.MULTILINE);

    /**
     * Find all of the CSS styles in the given input string.
     *
     * @param input The string to search
     * @return
     */
    public static Set<String> find(String input) {
        Set<String> styleClasses = new HashSet<>();
        input = input.replaceAll("(?s)/\\*.*?\\*/", ""); //This is to remove the comments from the CSS
        Matcher matcher = CSS_STYLE_PATTERN.matcher(input);
        while (matcher.find()) {
            if (matcher.groupCount() > 0) {
                styleClasses.add(matcher.group(1).trim());
            }
        }

        return styleClasses;
    }
}
