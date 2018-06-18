package org.ziglang.eclipse;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory implements IPerspectiveFactory
{
    @Override
    public void createInitialLayout(IPageLayout layout)
    {
        IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, layout.getEditorArea());
        topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
        topLeft.addView(IPageLayout.ID_OUTLINE);

        IFolderLayout bottom = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.75f, layout.getEditorArea());
        bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
    }
}
