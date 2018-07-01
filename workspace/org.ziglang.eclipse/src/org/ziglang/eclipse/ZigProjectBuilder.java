package org.ziglang.eclipse;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.console.AbstractConsole;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.part.IPageBookViewPage;

public class ZigProjectBuilder extends IncrementalProjectBuilder
{
    private static void initConsole()
    {
        // i think i have to do this to put the build output in the console tab
        IConsole console = new AbstractConsole("Zig Build", null) {
            @Override
            public IPageBookViewPage createPage(IConsoleView view)
            {
                // TODO Auto-generated method stub
                return null;
            }
        };
        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
    }

    @Override
    protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException
    {
        ProcessBuilder processBuilder = new ProcessBuilder("zig", "build", "--build-file", "build.zig");
        processBuilder.directory(getProject().getLocation().toFile());
        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);

        return null;
    }
}
