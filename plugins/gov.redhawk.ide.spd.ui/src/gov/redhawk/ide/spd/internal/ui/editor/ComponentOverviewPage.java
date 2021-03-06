/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.spd.internal.ui.editor;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.debug.ui.LaunchUtil;
import gov.redhawk.ide.sdr.ui.export.DeployableScaExportWizard;
import gov.redhawk.ide.spd.ui.ComponentUiPlugin;
import gov.redhawk.ide.spd.ui.editor.AuthorsSection;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.prf.ui.editor.page.PropertiesFormPage;
import gov.redhawk.ui.editor.AbstractOverviewPage;
import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * The Class ComponentOverviewPage.
 */
public class ComponentOverviewPage extends AbstractOverviewPage implements IViewerProvider {

	/** The Constant PAGE_ID. */
	public static final String PAGE_ID = "componentOverview"; //$NON-NLS-1$
	/** The Constant Toolbar ID. */
	public static final String TOOLBAR_ID = "gov.redhawk.ide.spd.internal.ui.editor.overview.toolbar";
	private GeneralInfoSection fInfoSection;
	private InterfaceSection fInterfaceSection;
	private ComponentContentSection componentContent;
	private TestingSection testingSection;
	private ExportingSection exportingSection;
	private AuthorsSection fAuthorsSection;
	private Resource spdResource;
	private Resource scdResource;
	private IResource wavDevResource;

	/**
	 * Instantiates a new component overview page.
	 * 
	 * @param editor the editor
	 * @param spdResource the spd resource
	 */
	public ComponentOverviewPage(final ComponentEditor editor) {
		super(editor, ComponentOverviewPage.PAGE_ID, "Overview");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComponentEditor getEditor() {
		return (ComponentEditor) super.getEditor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createFormContent(final IManagedForm managedForm) {

		final ScrolledForm form = managedForm.getForm();
		final FormToolkit toolkit = managedForm.getToolkit();

		// TODO form.setImage();
		form.setText("Overview");

		fillBody(managedForm, toolkit);

		// Refresh Page
		refresh(this.spdResource);
		refresh(this.scdResource);

		super.createFormContent(managedForm);

		final ToolBarManager manager = (ToolBarManager) form.getToolBarManager();
		final IMenuService service = (IMenuService) getSite().getService(IMenuService.class);
		service.populateContributionManager(manager, "toolbar:" + ComponentOverviewPage.TOOLBAR_ID);
		manager.update(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		this.removeResourceListener(this.scdResource);
		super.dispose();
	}

	/**
	 * Fill body.
	 * 
	 * @param managedForm the managed form
	 * @param toolkit the toolkit
	 */
	private void fillBody(final IManagedForm managedForm, final FormToolkit toolkit) {
		final Composite body = managedForm.getForm().getBody();
		body.setLayout(FormLayoutFactory.createFormTableWrapLayout(true, 2));

		final Composite left = toolkit.createComposite(body);
		left.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
		left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		createGeneralInfoSection(managedForm, left, toolkit);
		// XXX Don't add the author section since it was deemed confusing
		// createAuthorsSection(managedForm, left, toolkit);

		final Composite right = toolkit.createComposite(body);
		right.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		// The following sections don't make sense for libraries, since the don't have .prf or .scd
		if (!isSoftpackageLibrary()) {
			createInterfaceSection(managedForm, right, toolkit);

			createComponentContentSection(managedForm, right, toolkit);

			createTestingSection(managedForm, right, toolkit);
		}

		createExportingSection(managedForm, right, toolkit);

	}

	/**
	 * Creates the testing section.
	 * 
	 * @param managedForm the managed form
	 * @param right the right
	 * @param toolkit the toolkit
	 */
	private void createTestingSection(final IManagedForm managedForm, final Composite right, final FormToolkit toolkit) {
		this.testingSection = new TestingSection(this, right);
		managedForm.addPart(this.testingSection);
	}

	/**
	 * Creates the component content section.
	 * 
	 * @param managedForm the managed form
	 * @param right the right
	 * @param toolkit the toolkit
	 */
	private void createComponentContentSection(final IManagedForm managedForm, final Composite right, final FormToolkit toolkit) {
		this.componentContent = new ComponentContentSection(this, right);
		managedForm.addPart(this.componentContent);
	}

	/**
	 * Creates the general info section.
	 * 
	 * @param managedForm the managed form
	 * @param left the left
	 * @param toolkit the toolkit
	 */
	private void createGeneralInfoSection(final IManagedForm managedForm, final Composite left, final FormToolkit toolkit) {
		this.fInfoSection = new GeneralInfoSection(this, left);
		managedForm.addPart(this.fInfoSection);

	}

	/**
	 * Creates the exporting section.
	 * 
	 * @param managedForm the managed form
	 * @param right the right
	 * @param toolkit the toolkit
	 */
	private void createExportingSection(final IManagedForm managedForm, final Composite right, final FormToolkit toolkit) {
		this.exportingSection = new ExportingSection(this, right);
		managedForm.addPart(this.exportingSection);

	}

	/**
	 * Creates the Interface section.
	 * 
	 * @param managedForm the managed form
	 * @param right the right
	 * @param toolkit the toolkit
	 * @since 2.1
	 */
	public void createInterfaceSection(final IManagedForm managedForm, final Composite right, final FormToolkit toolkit) {
		this.fInterfaceSection = new InterfaceSection(this, right);
		managedForm.addPart(this.fInterfaceSection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void linkActivated(final HyperlinkEvent e) {
		final Object href = e.getHref();
		if (ComponentContentSection.PROP_HREF.equals(href)) {
			getEditor().setActivePage(PropertiesFormPage.PAGE_ID);
		} else if (ComponentContentSection.IMPL_HREF.equals(href)) {
			getEditor().setActivePage(ImplementationPage.PAGE_ID);
		} else if (TestingSection.TESTING_HREF_LOCAL_LAUNCH.equals(href)) {
			launch(ILaunchManager.RUN_MODE);
		} else if (TestingSection.TESTING_HREF_LOCAL_DEBUG.equals(href)) {
			launch(ILaunchManager.DEBUG_MODE);
		} else if ("export".equals(e.getHref())) {
			final DeployableScaExportWizard wizard = new DeployableScaExportWizard();
			final IProject project = ModelUtil.getProject(getInput());
			wizard.init(getEditor().getSite().getWorkbenchWindow().getWorkbench(), new StructuredSelection(project));
			final WizardDialog dialog = new WizardDialog(getEditor().getSite().getShell(), wizard);
			dialog.open();
		}
	}

	private void launch(final String mode) {
		try {
			ILaunchConfigurationWorkingCopy newConfig = LaunchUtil.createLaunchConfiguration(SoftPkg.Util.getSoftPkg(spdResource), getEditorSite().getShell());
			if (spdResource.getURI().isPlatform()) {
				ILaunchConfiguration config = LaunchUtil.chooseConfiguration(mode, LaunchUtil.findLaunchConfigurations(newConfig), getEditorSite().getShell());
				if (config == null) {
					config = newConfig.doSave();
				}
				DebugUITools.launch(config, mode);
			} else {
				LaunchUtil.launch(newConfig, mode);
			}
		} catch (CoreException e) {
			final Status status = new Status(e.getStatus().getSeverity(), ComponentUiPlugin.PLUGIN_ID, e.getLocalizedMessage(), e);
			StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInput(final Resource input) {
		super.setInput(input);
		this.spdResource = input;
		final SoftPkg spd = SoftPkg.Util.getSoftPkg(input);
		if (spd != null && spd.getDescriptor() != null && spd.getDescriptor().getComponent() != null) {
			this.scdResource = spd.getDescriptor().getComponent().eResource();
			addResourceListener(this.scdResource);
			refresh(this.scdResource);
		}
		// Adding the wavDev to the list of tracked resources. This will allow it to be updated when external
		// forces (From the Generate Code methods) update the CRC32s contained within the File.
		// This was to fix Bug # 21
		final URI wavDevUri = CodegenUtil.getSettingsURI(spd);
		if (wavDevUri != null && wavDevUri.isPlatform()) {
			IPath wavDevPath = new Path(wavDevUri.toPlatformString(true));
			this.wavDevResource = ResourcesPlugin.getWorkspace().getRoot().getFile(wavDevPath);
			this.getEditor().getResourceTracker().addTrackedResource(wavDevResource);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refresh(final Resource resource) {
		if (resource == null) {
			return;
		}

		if (resource == this.scdResource) {
			if (this.fInterfaceSection != null) {
				this.fInterfaceSection.refresh(this.scdResource);
			}
		} else {
			if (this.componentContent != null) {
				this.componentContent.refresh(resource);
			}
			if (this.exportingSection != null) {
				this.exportingSection.refresh(resource);
			}
			if (this.fAuthorsSection != null) {
				this.fAuthorsSection.refresh(resource);
			}
			if (this.fInfoSection != null) {
				this.fInfoSection.refresh(resource);
			}
			if (this.testingSection != null) {
				this.testingSection.refresh(resource);
			}
		}
	}
	
	/**
	 * Checks to see if the resource is a Softpackage Library
	 * Important because a number of entry fields are omitted in the case of libraries 
	 */
	public boolean isSoftpackageLibrary() {
		for (EObject i : spdResource.getContents()) {
			if (i instanceof SoftPkg) {
				for (Implementation impl : ((SoftPkg) i).getImplementation()) {
					if (CodeFileType.SHARED_LIBRARY.equals(impl.getCode().getType())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @return the scdResource
	 */
	public Resource getScdResource() {
		return this.scdResource;
	}
	
	/**
	 * @return the spdResource
	 */
	public Resource getSpdResource() {
		return this.spdResource;
	}

	@Override
	public Viewer getViewer() {
		return null;
	}
}
