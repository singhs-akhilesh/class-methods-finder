package Infra;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DeprecatedAPIWebCrawler {

    private static final String CONTENTS_SELECTOR = "li > a[href]";
    private static final String TABLE_SELECTOR = "table";
    private static final String TABLE_BODY_SELECTOR = "tbody";
    private static final String TABLE_ROW_SELECTOR = "tr";
    private static final String TABLE_HEADER_SELECTOR = "th";
    private static final String TABLE_DATA_SELECTOR = "td";
    private static final String CAPTION_SPAN_SELECTOR = "caption > span";
    private static final String DIV_SPAN_SELECTOR = "div > span";

    private Set<String> getPageDeprecatedContents(String URL) throws IOException {
        Document document = Jsoup.connect(URL).get();
        Elements contentListOnPage = document.select(CONTENTS_SELECTOR);
        return contentListOnPage.stream().map(Element::text).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Map<String, Map<String, String>> getPageDeprecatedItemAndRecommendation(String URL) throws IOException {
        Set<String> deprecatedContents = getPageDeprecatedContents(URL);
        Document document = Jsoup.connect(URL).get();
        Elements contentListOnPage = document.select(TABLE_SELECTOR);
        Map<String, Map<String, String>> deprecatedItemAndRecommendationMap = new HashMap<>();
        for (Element table : contentListOnPage) {
            for (Element captionSpan : table.select(CAPTION_SPAN_SELECTOR)) {
                if (deprecatedContents.contains(captionSpan.text())) {
                    Map<String, String> rowHeaderDataMap = new HashMap<>();
                    for (Element tableBody : table.select(TABLE_BODY_SELECTOR)) {
                        for (Element tableRows : tableBody.select(TABLE_ROW_SELECTOR)) {
                            String header = tableRows.select(TABLE_HEADER_SELECTOR).text();
                            String data = tableRows.select(TABLE_DATA_SELECTOR).text();
                            if (!header.isEmpty()) {
                                rowHeaderDataMap.put(header, data);
                            } else if(!data.isEmpty()){
                                header = data;
                                data = tableRows.select(DIV_SPAN_SELECTOR).text();
                                rowHeaderDataMap.put(header,data);
                            }
                        }
                    }
                    deprecatedItemAndRecommendationMap.put(captionSpan.text(), rowHeaderDataMap);
                }
            }
        }
        return deprecatedItemAndRecommendationMap;
    }
}