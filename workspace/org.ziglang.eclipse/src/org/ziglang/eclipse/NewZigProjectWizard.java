package org.ziglang.eclipse;

import java.util.Arrays;

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
            private boolean buildCommandHasBeenEdited;
            private Button buildFileOption;
            private Text buildCommandTextbox;

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

                Group buildCommandGroup = new Group(rootComposite, SWT.NONE);
                buildCommandGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
                buildCommandGroup.setText("Build command");
                buildCommandGroup.setLayout(new GridLayout(2, false));

                buildFileOption = new Button(buildCommandGroup, SWT.RADIO);
                buildFileOption.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
                buildFileOption.setText("Use build.zig");
                buildFileOption.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        buildCommandTextbox.setText("zig build --build-file=../build.zig");
                    }
                });

                Button customBuildOption = new Button(buildCommandGroup, SWT.RADIO);
                customBuildOption.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
                customBuildOption.setText("Use custom command");

                buildCommandTextbox = new Text(buildCommandGroup, SWT.BORDER);
                buildCommandTextbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                buildCommandTextbox.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent e)
                    {
                        buildCommandHasBeenEdited = true;
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
                if (projectLocation.exists()) {
                    if (!projectLocation.isDirectory())
                        return false;

                    if (!buildCommandHasBeenEdited) {
                        String[] topLevelFiles = projectLocation.list();
                        if (topLevelFiles != null && Arrays.asList(topLevelFiles).contains("build.zig")) {
                            // assume the user wants to use build.zig
                            buildFileOption.setSelection(true);
                        }
                    }
                }

                return true;
            }
        });
    }

    @Override
    public boolean performFinish()
    {
        return false;
    }
}
