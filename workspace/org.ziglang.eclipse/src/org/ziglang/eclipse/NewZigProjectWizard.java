package org.ziglang.eclipse;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewZigProjectWizard extends Wizard implements INewWizard
{
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
    }

    private java.io.File projectLocation;
    @Override
    public void addPages()
    {
        addPage(new WizardPage("New Zig Project", "New Zig Project", null) {
            {
                setPageComplete(false);
            }
            private Text fileTextbox;

            @Override
            public void createControl(Composite parent)
            {
                Composite rootComposite = new Composite(parent, SWT.NONE);
                rootComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                rootComposite.setLayout(new GridLayout());

                Group locationGroup = new Group(rootComposite, SWT.NONE);
                locationGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
                locationGroup.setText("Location");
                locationGroup.setLayout(new GridLayout(2, false));

                fileTextbox = new Text(locationGroup, SWT.BORDER);
                fileTextbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                fileTextbox.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e)
                    {
                        validate();
                    }
                });

                Button browseButton = new Button(locationGroup, SWT.PUSH);
                browseButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
                browseButton.setText("&Browse...");
                browseButton.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SHEET);
                        dialog.setFilterPath(System.getProperty("user.home"));
                        String directoryString = dialog.open();
                        if (directoryString == null)
                            return;

                        fileTextbox.setText(directoryString);
                        validate();
                    }
                });

                setControl(rootComposite);
            }

            private void validate()
            {
                setPageComplete(isValid());
            }
            private boolean isValid()
            {
                projectLocation = new java.io.File(fileTextbox.getText());
                if (!projectLocation.isAbsolute())
                    return false;

                if (!projectLocation.isDirectory())
                    return false;

                String projectName = projectLocation.getName();
                if (projectName.isEmpty())
                    return false;

                if (ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).exists())
                    return false;

                return true;
            }
        });
    }

    @Override
    public boolean performFinish()
    {
        String projectName = projectLocation.getName();

        IProjectDescription projectDescription = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
        projectDescription.setLocation(new Path(projectLocation.getPath()));
        ICommand buildCommand = projectDescription.newCommand();
        buildCommand.setBuilderName("org.ziglang.eclipse.zigbuilder");
        projectDescription.setBuildSpec(new ICommand[] { buildCommand });

        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(getShell());
        try {
            progressMonitorDialog.run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
                {
                    try {
                        project.create(projectDescription, monitor);
                        project.open(monitor);
                    } catch (CoreException e) {
                        throw new InvocationTargetException(e);
                    }
                }
            });
        } catch (InvocationTargetException e) {
            // idk what to do here
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
