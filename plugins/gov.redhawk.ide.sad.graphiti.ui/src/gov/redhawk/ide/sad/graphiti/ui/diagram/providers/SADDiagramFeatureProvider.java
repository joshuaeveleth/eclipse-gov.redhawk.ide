package gov.redhawk.ide.sad.graphiti.ui.diagram.providers;

import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.DecrementStartOrderFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.IncrementStartOrderFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.MarkExternalPortFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.MarkNonExternalPortFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.SetAsAssemblyControllerFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.delete.DeleteSADConnectInterface;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.layout.ZestLayoutDiagramFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.ComponentPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByCORBANamePattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByDomainManagerPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByEventChannelPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByFileManagerPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByServicePattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.HostCollocationPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.SADConnectInterfacePattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.features.impl.UpdateNoBoFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.pattern.DefaultFeatureProviderWithPatterns;
import org.eclipse.graphiti.pattern.DirectEditingFeatureForPattern;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;



public class SADDiagramFeatureProvider extends DefaultFeatureProviderWithPatterns {

	
	public SADDiagramFeatureProvider(IDiagramTypeProvider diagramTypeProvider){
		super(diagramTypeProvider);
		
		//Add Patterns for Domain Objects
		addPattern(new ComponentPattern());
		addConnectionPattern(new SADConnectInterfacePattern());
		addPattern(new HostCollocationPattern());
		addPattern(new FindByDomainManagerPattern());
		addPattern(new FindByFileManagerPattern());
		addPattern(new FindByEventChannelPattern());
		addPattern(new FindByServicePattern());
		addPattern(new FindByCORBANamePattern());
		
		
		//would be cool to add a diagram listener and fire off events on hover, not sure how to achieve this
		//add a diagram listener
//		final Diagram diagram = diagramTypeProvider.getDiagram();
//		((IDiagramContainerUI)diagramTypeProvider.getDiagramEditor()).getGraphicalViewer().getControl().addMouseMoveListener(new MouseMoveListener() {
//			@Override
//			public void mouseMove(MouseEvent e) {
//				ILocationInfo info = Graphiti.getPeService().getLocationInfo(diagram, e.x, e.y);
//				Shape shape = info.getShape();
//				Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(shape);
//				if(bo instanceof SadComponentInstantiation){
//					return;
//				}
//			}
//		});
			
//		final Diagram diagram = diagramTypeProvider.getDiagram();
//		diagramTypeProvider.getNotificationService().
//		DiagramBehavior vov = (DiagramBehavior)diagramTypeProvider.getDiagramBehavior();
//		vov.getContentEditPart().addEditPartListener(new EditPartListener(){
//
//			@Override
//            public void childAdded(EditPart child, int index) {
//	            // TODO Auto-generated method stub
//	            
//            }
//
//			@Override
//            public void partActivated(EditPart editpart) {
//	            // TODO Auto-generated method stub
//	            
//            }
//
//			@Override
//            public void partDeactivated(EditPart editpart) {
//	            // TODO Auto-generated method stub
//	            
//            }
//
//			@Override
//            public void removingChild(EditPart child, int index) {
//	            // TODO Auto-generated method stub
//	            
//            }
//
//			@Override
//            public void selectedStateChanged(EditPart editpart) {
//	            // TODO Auto-generated method stub
//	            
//            }
//		
//		});
//		vov.
//		diagramTypeProvide
//		diagramTypeProvider.getDiagramEditor().getDiagramTypeProvider()..getGraphicalViewer().getControl().addMouseMoveListener(new MouseMoveListener() {
//			@Override
//			public void mouseMove(MouseEvent e) {
//				ILocationInfo info = Graphiti.getPeService().getLocationInfo(diagram, e.x, e.y);
//				Shape shape = info.getShape();
//				Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(shape);
//			}
		

	}
	
	//the text we double click is nested inside of the pictogram element that links to our business object
	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
		if (context == null) {
			throw new IllegalArgumentException("Argument context must not be null."); //$NON-NLS-1$
		}
		IDirectEditingFeature ret = null;
		for (IPattern pattern : getPatterns()) {
			if (checkPattern(pattern, getBusinessObjectForPictogramElement(DUtil.findContainerShapeParentWithProperty(
					context.getPictogramElement(), RHContainerShapeImpl.SHAPE_outerContainerShape)))) {
				IPattern choosenPattern = null;
				IDirectEditingFeature f = new DirectEditingFeatureForPattern(this, pattern);
				if (checkFeatureAndContext(f, context)) {
					if (ret == null) {
						ret = f;
						choosenPattern = pattern;
					} else {
						traceWarning("getDirectEditingFeature", pattern, choosenPattern); //$NON-NLS-1$
					}
				}
			}
		}

		if (ret == null) {
			ret = getDirectEditingFeatureAdditional(context);
		}

		return ret;
	}
	
//	@Override
//	protected IDirectEditingFeature getDirectEditingFeatureAdditional(IDirectEditingContext context) {
//		
//		PictogramElement pe = context.getPictogramElement();
//		ComponentShape componentShape = (ComponentShape)DUtil.findContainerShapeParentWithProperty(
//				pe, RHContainerShapeImpl.SHAPE_outerContainerShape);
//		if(componentShape != null && getBusinessObjectForPictogramElement(componentShape) instanceof SadComponentInstantiation){
//			return new ComponentDirectEditUsageNameFeature(getDiagramTypeProvider().getFeatureProvider());
//		}
//		
//		return null;
//		
//	}

	
	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		ICustomFeature[] ret = super.getCustomFeatures(context);
		List<ICustomFeature> retList = new ArrayList<ICustomFeature>();
		for (int i = 0; i < ret.length; i++) {
			retList.add(ret[i]);
		}
		
		//add zest layout feature if diagram selected
		if(context.getPictogramElements() != null && context.getPictogramElements().length > 0 && context.getPictogramElements()[0] instanceof Diagram){
			retList.add(new ZestLayoutDiagramFeature(this.getDiagramTypeProvider().getFeatureProvider()));
		}
		
		//add external port menu item if we clicked on a port
		if(context.getPictogramElements() != null && context.getPictogramElements().length > 0){
		    Object obj = DUtil.getBusinessObject(context.getPictogramElements()[0]);
	    	
		    //make sure business object is port stub
		    if(obj instanceof ProvidesPortStub || obj instanceof UsesPortStub){
		    	
		    	boolean mark = true;
		    	
		    	//get sad from diagram
			    final SoftwareAssembly sad = DUtil.getDiagramSAD(this.getDiagramTypeProvider().getFeatureProvider(), this.getDiagramTypeProvider().getDiagram());
			    EList<Port> externalPortList = sad.getExternalPorts().getPort();
		    	
		    	//if it's already there disable this feature
			    if(obj instanceof ProvidesPortStub){
			    	for(Port p: externalPortList){
			    		if(p.getProvidesIndentifier().equals(((ProvidesPortStub)obj).getName())){
			    			mark = false;
			    		}
			    	}
			    	
			    }
			    //if it's already there disable this feature
			    if(obj instanceof UsesPortStub){
			    	for(Port p: externalPortList){
			    		if(p.getUsesIdentifier().equals(((UsesPortStub)obj).getName())){
			    			mark = false;
			    		}
			    	}
			    }
		    	//add the mark feature
			    if(mark){
			    	retList.add(new MarkExternalPortFeature(this.getDiagramTypeProvider().getFeatureProvider()));
			    }else{
			    	retList.add(new MarkNonExternalPortFeature(this.getDiagramTypeProvider().getFeatureProvider()));
			    }
		    }
		}
	    
		
		//add Set As Assembly Controller menu item
		if(context.getPictogramElements() != null && context.getPictogramElements().length > 0){
			Object obj = DUtil.getBusinessObject(context.getPictogramElements()[0]);
			if(obj instanceof SadComponentInstantiation){
				retList.add(new SetAsAssemblyControllerFeature(this.getDiagramTypeProvider().getFeatureProvider()));
			}
		}
		
		//add Increment Start Order menu item
		if(context.getPictogramElements() != null && context.getPictogramElements().length > 0){
			Object obj = DUtil.getBusinessObject(context.getPictogramElements()[0]);
			if(obj instanceof SadComponentInstantiation){
				retList.add(new IncrementStartOrderFeature(this.getDiagramTypeProvider().getFeatureProvider()));
			}
		}
				
		//add Decrement Start Order menu item
		if(context.getPictogramElements() != null && context.getPictogramElements().length > 0){
			Object obj = DUtil.getBusinessObject(context.getPictogramElements()[0]);
			if(obj instanceof SadComponentInstantiation){
				retList.add(new DecrementStartOrderFeature(this.getDiagramTypeProvider().getFeatureProvider()));
			}
		}

		ret = retList.toArray(ret);
		return ret;
	}
	
	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
		
		//Search for shapes that we don't want the user to have move capability
		if(DUtil.doesPictogramContainProperty(context, 
			  new String[] {RHContainerShapeImpl.SHAPE_usesPortRectangleShape,
				RHContainerShapeImpl.SHAPE_providesPortRectangleShape,
				RHContainerShapeImpl.SHAPE_interfaceContainerShape}))
		{
			return new DefaultMoveShapeFeature(this) {
				public boolean canMove(IContext context) {
					return false;
				}
			};
		}
		
		return super.getMoveShapeFeature(context);
	}
	
	
	
	@Override
	public ICreateFeature[] getCreateFeatures(){
		
		ICreateFeature[] defaultCreateFeatures = super.getCreateFeatures();
		
		//features added via pattern
		return defaultCreateFeatures;
	}
	
	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context){

		//hide update icon for some pictogram elements
		if(DUtil.doesPictogramContainProperty(context, 
				new String[] {RHContainerShapeImpl.SHAPE_providesPortsContainerShape,
				RHContainerShapeImpl.SHAPE_usesPortsContainerShape,
				RHContainerShapeImpl.SHAPE_providesPortContainerShape,
				RHContainerShapeImpl.SHAPE_usesPortContainerShape,
				RHContainerShapeImpl.SHAPE_providesPortRectangleShape,
				RHContainerShapeImpl.SHAPE_usesPortRectangleShape,
				RHContainerShapeImpl.SHAPE_interfaceContainerShape,
				RHContainerShapeImpl.SHAPE_interfaceEllipseShape}))
		{
			return new UpdateNoBoFeature(this) {
				public boolean isAvailable(IContext context) {
					return false;
				}
			};
		}
		
		return super.getUpdateFeature(context);
	}
	

	
	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context){
		
		//Search for shapes that we don't want the user to have delete capability
		if(DUtil.doesPictogramContainProperty(context, 
			  new String[] {RHContainerShapeImpl.SHAPE_providesPortsContainerShape,
				RHContainerShapeImpl.SHAPE_usesPortsContainerShape,
				RHContainerShapeImpl.SHAPE_providesPortContainerShape,
				RHContainerShapeImpl.SHAPE_usesPortContainerShape,
				RHContainerShapeImpl.SHAPE_providesPortRectangleShape,
				RHContainerShapeImpl.SHAPE_usesPortRectangleShape,
				RHContainerShapeImpl.SHAPE_interfaceContainerShape,
				RHContainerShapeImpl.SHAPE_interfaceEllipseShape}))
		{
			return new DefaultDeleteFeature(this) {
				public boolean isAvailable(IContext context) {
					return false;
				}
			};
		}
		//is user deleting a connection
		if(context != null && context.getPictogramElement() != null && context.getPictogramElement().getLink() != null){
			for(EObject eObj: context.getPictogramElement().getLink().getBusinessObjects()){
				if (eObj instanceof SadConnectInterface){
					return new DeleteSADConnectInterface(this);
				}
			}
		}
		
		return super.getDeleteFeature(context);
	}
	
	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context){
		
		//Search for shapes that we don't want the user to have remove capability
		if(DUtil.doesPictogramContainProperty(context, 
				  new String[] {RHContainerShapeImpl.SHAPE_providesPortsContainerShape,
				RHContainerShapeImpl.SHAPE_usesPortsContainerShape,
				RHContainerShapeImpl.SHAPE_providesPortContainerShape,
				RHContainerShapeImpl.SHAPE_usesPortContainerShape,
				RHContainerShapeImpl.SHAPE_providesPortRectangleShape,
				RHContainerShapeImpl.SHAPE_usesPortRectangleShape,
				RHContainerShapeImpl.SHAPE_interfaceContainerShape,
				RHContainerShapeImpl.SHAPE_interfaceEllipseShape,
				RHContainerShapeImpl.SHAPE_outerContainerShape}))
			{
			return new DefaultRemoveFeature(this) {
				public boolean isAvailable(IContext context) {
					return false;
				}
			};
		}
		
		return super.getRemoveFeature(context);
		
	}
	
	@Override
	public IFeature[] getDragAndDropFeatures(IPictogramElementContext context){
		
		ICreateConnectionFeature[] connectionFeatures = getCreateConnectionFeatures();
	
		return connectionFeatures;
	}
	
	
	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context){
		
		//Search for shapes that we don't want the user to have resize capability
		if(DUtil.doesPictogramContainProperty(context, 
				new String[] {RHContainerShapeImpl.SHAPE_providesPortRectangleShape,
				RHContainerShapeImpl.SHAPE_usesPortRectangleShape,
				RHContainerShapeImpl.SHAPE_interfaceContainerShape}))
		{
			return new DefaultResizeShapeFeature(this) {
				public boolean canResizeShape(IResizeShapeContext context) {
					return false;
				}
			};
		}
				
		return super.getResizeShapeFeature(context);
	}
	
	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
		
		
		if (context == null) {
			throw new IllegalArgumentException("Argument context must not be null."); //$NON-NLS-1$
		}
		
		return super.getLayoutFeature(context);
	}
}
