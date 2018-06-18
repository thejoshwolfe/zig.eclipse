package org.ziglang.eclipse;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

public class ZigEditor extends AbstractDecoratedTextEditor
{
    public ZigEditor()
    {
        setSourceViewerConfiguration(new SourceViewerConfiguration() {
            @Override
            public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
            {
                PresentationReconciler presentationReconciler = new PresentationReconciler();
                DefaultDamagerRepairer damagerRepairer = new DefaultDamagerRepairer(new ZigScanner());
                presentationReconciler.setRepairer(damagerRepairer, IDocument.DEFAULT_CONTENT_TYPE);
                presentationReconciler.setDamager(damagerRepairer, IDocument.DEFAULT_CONTENT_TYPE);
                return presentationReconciler;
            }
            //            @Override
            //            public IContentAssistant getContentAssistant(ISourceViewer sv)
            //            {
            //                ContentAssistant ca = new ContentAssistant();
            //                IContentAssistProcessor cap = new ZigCompletionProcessor();
            //                ca.setContentAssistProcessor(cap, IDocument.DEFAULT_CONTENT_TYPE);
            //                ca.setInformationControlCreator(getInformationControlCreator(sv));
            //                return ca;
            //            }
            //            @Override
            //            public ITextHover getTextHover(ISourceViewer sv, String contentType)
            //            {
            //                return new ZigTextHover();
            //            }
        });
    }

    private static class ZigScanner extends RuleBasedScanner
    {
        public ZigScanner()
        {
            Display display = Display.getCurrent();
            Token keywordToken = new Token(new TextAttribute(new Color(display, 0x93, 0x05, 0x55, 0xff), null, SWT.BOLD));
            Token commentToken = new Token(new TextAttribute(new Color(display, 0x3f, 0x7f, 0x5f, 0xff)));
            Token stringToken = new Token(new TextAttribute(new Color(display, 0x2c, 0x04, 0xff, 0xff)));

            WordRule keywordRule = new WordRule(new IWordDetector() {
                @Override
                public boolean isWordStart(char c)
                {
                    return Character.isJavaIdentifierStart(c);
                }
                @Override
                public boolean isWordPart(char c)
                {
                    return Character.isJavaIdentifierPart(c);
                }
            });
            keywordRule.addWord("if", keywordToken);

            WhitespaceRule whitespaceRule = new WhitespaceRule(new IWhitespaceDetector() {
                @Override
                public boolean isWhitespace(char c)
                {
                    return c == ' ' || c == '\n';
                }
            });
            setRules(new IRule[] { //
                    keywordRule, //
                    new SingleLineRule("//", null, commentToken, (char)0, true, true), //
                    new SingleLineRule("\"", "\"", stringToken, '\\', true, true), //
                    new SingleLineRule("'", "'", stringToken, '\\', true, true), //
                    whitespaceRule, //
            });
        }
    }

}
