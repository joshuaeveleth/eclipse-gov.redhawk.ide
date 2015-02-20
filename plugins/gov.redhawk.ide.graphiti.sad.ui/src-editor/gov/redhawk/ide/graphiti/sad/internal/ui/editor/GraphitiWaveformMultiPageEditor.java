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
package gov.redhawk.ide.graphiti.sad.internal.ui.editor;

import gov.redhawk.ide.graphiti.sad.ui.SADUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.sad.ui.diagram.GraphitiWaveformDiagramEditor;
import gov.redhawk.ide.graphiti.sad.ui.diagram.SadDiagramUtilHelper;
import gov.redhawk.ide.graphiti.sad.ui.diagram.providers.SADDiagramTypeProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.editor.AbstractGraphitiMultiPageEditor;
import gov.redhawk.ide.internal.ui.handlers.CleanUpComponentFilesAction;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.NonDirtyingCommand;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.sca.util.PluginUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mil.jpeojtrs.sca.prf.provider.PrfItemProviderAdapterFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.provider.SadItemProviderAdapterFactory;
import mil.jpeojtrs.sca.scd.provider.ScdItemProviderAdapterFactory;
import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;

import CF.Application;

/**
 * This is an example of a Sad model editor.
 */
public class GraphitiWaveformMultiPageEditor extends AbstractGraphitiMultiPageEditor implements ITabbedPropertySheetPageContributor, IViewerProvider {

	public static final String ID = "gov.redhawk.ide.graphiti.sad.ui.editor.presentation.SadEditorID";

	public static final String EDITING_DOMAIN_ID = "mil.jpeojtrs.sca.sad.diagram.EditingDomain";

	private ResourceListener nameListener;
	private IFormPage propertiesPage;

	private class ResourceListener extends AdapterImpl {
		private SoftwareAssembly sad;
		private final Resource sadResource;

		public ResourceListener(final Resource spdResource) {
			this.sadResource = spdResource;
			if (this.sadResource != null) {
				this.sadResource.eAdapters().add(this);
				this.sad = getSoftwareAssembly();
				if (this.sad != null) {
					this.sad.eAdapters().add(this);
					updateTitle();
				}
			}
		}

		/**
		 * Gets the soft pkg.
		 *
		 * @return the soft pkg
		 */
		private SoftwareAssembly getSoftwareAssembly() {
			return ModelUtil.getSoftwareAssembly(this.sadResource);
		}

		public void dispose() {
			if (this.sad != null) {
				this.sad.eAdapters().remove(this);
			}
			if (this.sadResource != null) {
				this.sadResource.eAdapters().remove(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void notifyChanged(final Notification msg) {
			if (msg.getNotifier() instanceof Resource) {
				switch (msg.getFeatureID(Resource.class)) {
				case Resource.RESOURCE__IS_LOADED:
					if (this.sad != null) {
						this.sad.eAdapters().remove(this);
						this.sad = null;
					}
					if (this.sadResource.isLoaded()) {
						this.sad = getSoftwareAssembly();
						if (this.sad != null) {
							this.sad.eAdapters().add(this);
							updateTitle();
						}
					}
					break;
				default:
					break;
				}
			} else if (msg.getNotifier() instanceof SoftwareAssembly) {
				final int featureID = msg.getFeatureID(SoftwareAssembly.class);

				if (featureID == SadPackage.SOFTWARE_ASSEMBLY__NAME) {
					if (msg.getEventType() == Notification.SET) {
						updateTitle();
					}
				}
			}
		}
	}

	/**
	 * This creates a model editor.
	 */
	public GraphitiWaveformMultiPageEditor() {
		super();
	}

	/**
	 * This sets the selection into whichever viewer is active.
	 */
	@Override
	public void setSelectionToViewer(final Collection< ? > collection) {
		final Collection< ? > theSelection = collection;
		// Make sure it's okay.
		//
		if (theSelection != null && !theSelection.isEmpty()) {
			// I don't know if this should be run this deferred
			// because we might have to give the editor a chance to process the
			// viewer update events
			// and hence to update the views first.
			//
			//
			final Runnable runnable = new Runnable() {
				@Override
				public void run() {
					// Try to select the items in the current content viewer of
					// the editor.
					//
					if (getViewer() != null) {
						getViewer().setSelection(new StructuredSelection(theSelection.toArray()), true);
					}
				}
			};
			runnable.run();
		}
	}

	/**
	 * This is how the framework determines which interfaces we implement.
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") final Class key) {
		if (key.equals(IGotoMarker.class)) {
			return this;
		} else if (key.equals(ScaWaveform.class)) {
			return PluginUtil.adapt(ScaWaveform.class, getSoftwareAssembly());
		} else if (key.isAssignableFrom(Application.class)) {
			return PluginUtil.adapt(ScaWaveform.class, getSoftwareAssembly());
		} else {
			return super.getAdapter(key);
		}
	}

	@Override
	public void dispose() {

		if (this.nameListener != null) {
			this.nameListener.dispose();
			this.nameListener = null;
		}
		super.dispose();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		String name = null;
		final SoftwareAssembly sad = getSoftwareAssembly();
		if (sad != null) {
			name = sad.getName();
			if (name == null) {
				name = getEditorInput().getName();
			}
		}
		if (name != null) {
			return name;
		} else {
			return "";
		}
	}

	/**
	 * @return
	 */
	private SoftwareAssembly getSoftwareAssembly() {
		return ModelUtil.getSoftwareAssembly(getMainResource());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addPages() {
		// Only creates the other pages if there is something that can be edited

		if (!getEditingDomain().getResourceSet().getResources().isEmpty()
			&& !(getEditingDomain().getResourceSet().getResources().get(0)).getContents().isEmpty()) {
			try {
				int pageIndex = 0;

				final Resource sadResource = getMainResource();

				addNameListener(sadResource);

				final IFormPage page = createOverviewPage(sadResource);
				setOverviewPage(page);
				this.addPage(page);

				propertiesPage = createPropertiesPage(sadResource);
				addPage(propertiesPage);

				final DiagramEditor editor = createDiagramEditor();
				setDiagramEditor(editor);
				final IEditorInput diagramInput = createDiagramInput(sadResource);
				pageIndex = addPage(editor, diagramInput);
				setPageText(pageIndex, "Diagram");

				// set layout for target-sdr editors
				DUtil.layout(editor);

				IEditorPart textEditor = createTextEditor();
				setTextEditor(textEditor);
				if (textEditor != null) {
					final int sadSourcePageNum = addPage(-1, textEditor, getEditorInput(), sadResource);
					this.setPageText(sadSourcePageNum, getEditorInput().getName());
				}

			} catch (final PartInitException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to create editor parts.", e),
					StatusManager.LOG | StatusManager.SHOW);
			} catch (final IOException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to create editor parts.", e),
					StatusManager.LOG | StatusManager.SHOW);
			} catch (final CoreException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to create editor parts.", e),
					StatusManager.LOG | StatusManager.SHOW);
			}
		}
	}

	protected IFormPage createPropertiesPage(Resource sadResource) {
		GraphitiWaveformPropertiesPage retVal = new GraphitiWaveformPropertiesPage(this, "propertiesPage", "Properties", true);
		retVal.setInput(sadResource);
		return retVal;
	}


	protected void addNameListener(final Resource sadResource) {
		this.nameListener = new ResourceListener(sadResource);
	}

	/**
	 * Given the sad resource (sad.xml) create a Diagram resource, associate it with the sad resource and return
	 * diagram editor input
	 * @param sadResource
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	protected IEditorInput createDiagramInput(final Resource sadResource) throws IOException, CoreException {

		final URI diagramURI = DUtil.getDiagramResourceURI(SadDiagramUtilHelper.INSTANCE, sadResource);

		DUtil.initializeDiagramResource(SadDiagramUtilHelper.INSTANCE, SADDiagramTypeProvider.DIAGRAM_TYPE_ID,
			SADDiagramTypeProvider.PROVIDER_ID, diagramURI, sadResource);

		Resource diagramResource = getEditingDomain().getResourceSet().getResource(diagramURI, true);

		// load diagram from resource
		final Diagram diagram = (Diagram) diagramResource.getContents().get(0);

		// load sad from resource
		final SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(sadResource);

		// link diagram with SoftwareAssembly
		NonDirtyingCommand.execute(diagram, new NonDirtyingCommand() {
			public void execute() {

				// set property specifying diagram context (design, local, domain)
				Graphiti.getPeService().setPropertyValue(diagram, DUtil.DIAGRAM_CONTEXT, getDiagramContext(sadResource));

				// link diagram and sad
				PictogramLink link = PictogramsFactory.eINSTANCE.createPictogramLink();
				link.getBusinessObjects().add(sad);
				diagram.setLink(link);
			}
		});

		// return editor input from diagram with sad diagram type
		return DiagramEditorInput.createEditorInput(diagram, SADDiagramTypeProvider.PROVIDER_ID);

	}


	protected DiagramEditor createDiagramEditor() {
		GraphitiWaveformDiagramEditor d = new GraphitiWaveformDiagramEditor((TransactionalEditingDomain) getEditingDomain());
		return d;
	}


	protected IFormPage createOverviewPage(final Resource sadResource) {
		final GraphitiWaveformOverviewPage retVal = new GraphitiWaveformOverviewPage(this);
		retVal.setInput(sadResource);
		return retVal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEditingDomainId() {
		return GraphitiWaveformMultiPageEditor.EDITING_DOMAIN_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AdapterFactory getSpecificAdapterFactory() {
		final ComposedAdapterFactory factory = new ComposedAdapterFactory();
		factory.addAdapterFactory(new SadItemProviderAdapterFactory());
		factory.addAdapterFactory(new EcoreItemProviderAdapterFactory());
		factory.addAdapterFactory(new SpdItemProviderAdapterFactory());
		factory.addAdapterFactory(new ScdItemProviderAdapterFactory());
		factory.addAdapterFactory(new PrfItemProviderAdapterFactory());
		return factory;
	}

	@Override
	public List<Object> getOutlineItems() {
		final List<Object> myList = new ArrayList<Object>();
		if (getOverviewPage() != null) {
			myList.add(getOverviewPage());
		}
		if (getSoftwareAssembly().getPartitioning() != null) {
			myList.add(getSoftwareAssembly().getPartitioning());
		}
		return myList;
	}

	@Override
	public boolean isPersisted(final Resource resource) {
		return resource.getURI().equals(getSoftwareAssembly().eResource().getURI()) && super.isPersisted(resource);
	}

	/**
	 * The Text editor always stays in sync with other editor changes therefore always save it.
	 * Calling emfDoSave handles saving the graphiti diagram resource
	 */
	@Override
	public void doSave(final IProgressMonitor monitor) {
		final CleanUpComponentFilesAction cleanAction = new CleanUpComponentFilesAction();
		cleanAction.setRoot(getSoftwareAssembly());
		cleanAction.run();

		super.doSave(monitor);
	}
}
