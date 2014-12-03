/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.runtime;

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.ComponentPattern;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;

public class ReleaseComponentFeature extends AbstractCustomFeature {

	public ReleaseComponentFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Release";
	}

	@Override
	public String getDescription() {
		return "Sends the release command for the selected component";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		if (context.getPictogramElements()[0] instanceof ComponentShapeImpl) {
			return true;
		}
		return false;
	}

	@Override
	public void execute(ICustomContext context) {
		final ComponentShape componentShape = (ComponentShapeImpl) context.getPictogramElements()[0];

		final SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {

			@Override
			protected void doExecute() {
				// Delete the component model object
				ComponentPattern.deleteComponentInstantiation(ci, sad);

				// Now we just need to remove the graphical representation of the component
				IRemoveContext rc = new RemoveContext(componentShape);
				IFeatureProvider featureProvider = getFeatureProvider();
				IRemoveFeature removeFeature = featureProvider.getRemoveFeature(rc);
				if (removeFeature != null) {
					removeFeature.remove(rc);
				}
			}
		});

	}

}
