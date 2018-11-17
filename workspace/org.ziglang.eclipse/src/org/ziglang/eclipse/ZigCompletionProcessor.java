package org.ziglang.eclipse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Point;

public class ZigCompletionProcessor implements IContentAssistProcessor
{
    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
    {
        Point selectedRange = viewer.getSelectedRange();
        if (selectedRange.y > 0) {
            // no suggestions for a non-empty selection.
            return null;
        }

        IDocument document = viewer.getDocument();
        // just give me everything
        String entireDocument;
        try {
            entireDocument = document.get(0, document.getLength());
        } catch (BadLocationException e) {
            // please
            throw new RuntimeException(e);
        }

        int wordStart = offset;
        while (wordStart > 0) {
            char c = entireDocument.charAt(wordStart - 1);
            if (!ZigTokenizer.WORD_DETECTOR.isWordPart(c))
                break;
            wordStart -= 1;
        }
        String prefix = entireDocument.substring(wordStart, offset);

        HashMap<String, Integer> popularityContest = new HashMap<>();

        // this should probably be cached...
        Matcher wordMatcher = ZigTokenizer.WORD_PATTERN.matcher(entireDocument);
        while (wordMatcher.find()) {
            if (wordMatcher.start() == wordStart) {
                // don't count the word we're typing as canon.
                continue;
            }
            String word = wordMatcher.group();
            if (!word.startsWith(prefix))
                continue;
            popularityContest.put(word, 1 + popularityContest.getOrDefault(word, 0));
        }

        // if you've typed an actual word already, you probably want to continue it.
        if (popularityContest.containsKey(prefix)) {
            popularityContest.put(prefix, -1);
        }

        // throw in the rest of the language words at low priority
        for (String[] wordList : new String[][] { ZigKeywordDefs.KEYWORDS, ZigKeywordDefs.BUILTINS })
            for (String word : wordList)
                if (word.startsWith(prefix))
                    popularityContest.putIfAbsent(word, 0);

        String[] words = popularityContest.keySet().toArray(new String[0]);
        Arrays.sort(words, new Comparator<String>() {
            @Override
            public int compare(String a, String b)
            {
                int cmp = popularityContest.get(b) - popularityContest.get(a);
                if (cmp != 0)
                    return cmp;
                return a.compareTo(b);
            }
        });

        ArrayList<ICompletionProposal> result = new ArrayList<>();
        int limit = Math.min(words.length, 100);
        for (int i = 0; i < limit; i++) {
            String word = words[i];
            result.add(new CompletionProposal(word, wordStart, prefix.length(), word.length()));
        }
        return result.toArray(new ICompletionProposal[0]);
    }

    @Override
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset)
    {
        return new IContextInformation[0];
    }

    @Override
    public char[] getCompletionProposalAutoActivationCharacters()
    {
        return new char[0];
    }
    @Override
    public char[] getContextInformationAutoActivationCharacters()
    {
        return new char[0];
    }
    @Override
    public String getErrorMessage()
    {
        return null;
    }
    @Override
    public IContextInformationValidator getContextInformationValidator()
    {
        return null;
    }
}
