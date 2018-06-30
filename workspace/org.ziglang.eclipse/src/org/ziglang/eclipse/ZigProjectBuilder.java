package org.ziglang.eclipse;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class ZigProjectBuilder extends IncrementalProjectBuilder
{
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
