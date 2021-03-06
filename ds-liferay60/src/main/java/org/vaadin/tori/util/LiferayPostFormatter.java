/*
 * Copyright 2012 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.vaadin.tori.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;
import org.vaadin.tori.data.entity.Post;
import org.vaadin.tori.util.PostFormatter.FontsInfo.FontFace;
import org.vaadin.tori.util.PostFormatter.FontsInfo.FontSize;

import com.liferay.portlet.messageboards.util.BBCodeUtil;

public class LiferayPostFormatter implements PostFormatter {

    private static final Logger LOG = Logger
            .getLogger(LiferayPostFormatter.class);

    private static Collection<FontFace> fontFaces;
    private static Collection<FontSize> fontSizes;

    static {
        fontFaces = new ArrayList<FontFace>(Arrays.asList(LiferayCommonFontFace
                .values()));
        fontSizes = new ArrayList<FontSize>(Arrays.asList(LiferayFontSize
                .values()));
    }

    @Override
    public String format(final Post post,
            final Map<String, String> replacements,
            final boolean replaceMessageBoardsLinks) {
        String msgBody = post.getBodyRaw().trim();
        if (replaceMessageBoardsLinks) {
            msgBody = LiferayUrlConverter.convertAllUrlsToToriForm(msgBody);
        }

        if (post.isFormatBBCode()) {
            try {
                msgBody = BBCodeUtil.getHTML(msgBody);
            } catch (Exception e) {
                msgBody = msgBody.replaceAll("\\<.*?>", "");
                LOG.debug("Couldn't parse the given post body: " + msgBody);
            }
        }

        if (replacements != null) {
            for (final Entry<String, String> entry : replacements.entrySet()) {
                try {
                    msgBody = msgBody.replaceAll(entry.getKey(),
                            entry.getValue());
                } catch (final PatternSyntaxException e) {
                    LOG.warn(
                            "Invalid replacement regex pattern: "
                                    + entry.getKey(), e);
                }
            }
        }
        return msgBody;
    }

    @Override
    public FontsInfo getFontsInfo() {
        return new FontsInfo() {
            @Override
            public Collection<FontFace> getFontFaces() {
                return fontFaces;
            }

            @Override
            public Collection<FontSize> getFontSizes() {
                return fontSizes;
            }
        };
    }

    @Override
    public String getQuote(final Post postToQuote) {
        if (postToQuote == null) {
            return "";
        }
        return String.format("[quote=%s]%s[/quote]\n", postToQuote.getAuthor()
                .getDisplayedName(), postToQuote.getBodyRaw());
    }

}
