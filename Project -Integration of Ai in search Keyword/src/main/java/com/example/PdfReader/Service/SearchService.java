package com.example.PdfReader.Service;
import com.example.PdfReader.modelDto.DocumentData;
import com.example.PdfReader.modelDto.Match;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class SearchService {


    public List<Match> searchInDocument(DocumentData document, String query,Boolean caseSensitive, Integer contextChars) {
        log.info("[SearchService] :: searchInDocument :: Starting search operation for query: '{}' in document: '{}'", query, document.getFilename());
        List<Match> matches = new ArrayList<>();
        String searchQuery = Boolean.TRUE.equals(caseSensitive) ? query : query.toLowerCase();
        for (int pageNum = 0; pageNum < document.getPages().size(); pageNum++) {
            String pageText = document.getPages().get(pageNum);
            String searchText = Boolean.TRUE.equals(caseSensitive) ? pageText : pageText.toLowerCase();
            int index = searchText.indexOf(searchQuery);
            while (index >= 0) {
                String snippet = createSnippet(pageText, index, query.length(),contextChars);
                double score = calculateScore(searchText, searchQuery);
                matches.add(new Match(pageNum + 1, snippet, score));
                index = searchText.indexOf(searchQuery, index + 1);
            }
        }
        log.debug("[SearchService] :: searchInDocument :: Sorting matches by score in descending order");
        matches.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return matches;
    }

    private String createSnippet(String text, int position, int queryLength, int contextChars) {
        int start = Math.max(0, position - contextChars / 2);
        //for diving the left and right part of the characters to be shown logic
        int end = Math.min(text.length(), position + queryLength + contextChars / 2);

        String snippet = text.substring(start, end).trim();
        if (start > 0) snippet = "..." + snippet;
        if (end < text.length()) snippet = snippet + "...";
        return snippet;
    }

    private double calculateScore(String pageText, String query) {
        int count = 0;
        int index = pageText.indexOf(query);
        while (index >= 0) {
            count++;
            index = pageText.indexOf(query, index + 1);
        }
        return count / Math.max(1, pageText.length() / 1000.0);
    }
}