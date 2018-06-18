package org.ziglang.eclipse;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
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
                DefaultDamagerRepairer damagerRepairer = new DefaultDamagerRepairer(new ZigTokenizer());
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
}
