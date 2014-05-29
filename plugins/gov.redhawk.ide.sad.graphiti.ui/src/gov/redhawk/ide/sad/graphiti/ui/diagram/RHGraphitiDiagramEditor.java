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
package gov.redhawk.ide.sad.graphiti.ui.diagram;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;

public class RHGraphitiDiagramEditor extends DiagramEditor {

	private EditingDomain editingDomain;

	public RHGraphitiDiagramEditor(EditingDomain editingDomain) {
		this.editingDomain = editingDomain;
	}

//juno
//	@Override
//	protected DefaultUpdateBehavior createUpdateBehavior() {
//		return new DefaultUpdateBehavior(this) {
//
//			// we need to provide our own editing domain so that all editors are working on the
//			// same resource. In order to work with a Graphiti diagram our form creates an editing domain
//			// with the Graphiti supplied Command stack.
//			@Override
//			protected void createEditingDomain() {
//				initializeEditingDomain((TransactionalEditingDomain) editingDomain);
//			}
//		};
//	}

//kepler
	@Override
	protected DiagramBehavior createDiagramBehavior() {
		return new DiagramBehavior(this) {

			@Override
			protected DefaultUpdateBehavior createUpdateBehavior() {
				return new DefaultUpdateBehavior(this) {

					// we need to provide our own editing domain so that all editors are working on the
					// same resource. In order to work with a Graphiti diagram our form creates an editing domain
					// with the Graphiti supplied Command stack.
					@Override
					protected void createEditingDomain() {
						initializeEditingDomain((TransactionalEditingDomain) editingDomain);
					}
				};
			}

		};
	}

}