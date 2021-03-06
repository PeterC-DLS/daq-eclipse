package org.eclipse.scanning.device.ui.vis;

import java.util.Arrays;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dawnsci.plotting.api.IPlottingService;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.PlotType;
import org.eclipse.dawnsci.plotting.api.region.IRegionSystem;
import org.eclipse.dawnsci.plotting.api.tool.IToolPageSystem;
import org.eclipse.dawnsci.plotting.api.trace.IImageTrace;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.Random;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.scanning.api.IModelProvider;
import org.eclipse.scanning.api.annotation.ui.FieldValue;
import org.eclipse.scanning.api.points.models.ScanRegion;
import org.eclipse.scanning.device.ui.ScanningPerspective;
import org.eclipse.scanning.device.ui.ServiceHolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A view which sends IROI events, implements IROI.class in getAdapter(...) and 
 * responds to model selections which contain rois.
 * 
 * @author Matthew Gerring
 *
 */
public class VisualiseView extends ViewPart implements IAdaptable, ISelectionListener {
	
	public static final String ID = "org.eclipse.scanning.device.ui.vis.visualiseView";
	
	private static final Logger logger = LoggerFactory.getLogger(VisualiseView.class);
			
	// Delegated control
	private   PlottingController      controller;

	// The plot
	private   IPlottingSystem<Object> system;

	public VisualiseView() {
		try {
			IPlottingService service = ServiceHolder.getPlottingService();
			system = service.createPlottingSystem();
			
            controller = new PlottingController(system);
 
		} catch (Exception ne) {
			logger.error("Unable to make plotting system", ne);
			system = null; // It creates the view but there will be no plotting system 
		}

	}

	@Override
	public void createPartControl(Composite parent) {

		system.createPlotPart(parent, getPartName(), getViewSite().getActionBars(), PlotType.IMAGE, this);  

		// Plot a random image
		createExampleTrace(); // TODO FIXME
		
		// Connect to existing regions, although they might not be desirable ones
		controller.connect();
        
        getSite().setSelectionProvider(controller);
        
        getSite().getPage().addSelectionListener(this);
        
		ScanningPerspective.createKeyPlayers();

	}

	private void createExampleTrace() {
		// TODO Correct data source, not this random one!
		IDataset x = DatasetFactory.createRange(-100d, 0d, 100d/3012, Dataset.FLOAT);
		x.setName("x");
		IDataset y = DatasetFactory.createRange(100d, 200d, 100d/4096, Dataset.FLOAT);
		y.setName("y");
		IImageTrace it = system.createImageTrace("image");
		it.setData(Random.rand(4096, 3012), Arrays.asList(new IDataset[]{x,y}), false);
		double[] globalRange = new double[4];
		globalRange[0] = x.min().doubleValue();
		globalRange[1] = x.max().doubleValue();
		globalRange[2] = y.min().doubleValue();
		globalRange[3] = y.max().doubleValue();
		it.setGlobalRange(globalRange);
		system.addTrace(it);
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection==null) return;
		if (!(selection instanceof StructuredSelection)) return;
		final Object object = ((StructuredSelection)selection).getFirstElement();
		if (object==null) return;
		if (object instanceof FieldValue) {
			processModel(((FieldValue)object).getModel());
		} else if (object instanceof IModelProvider<?>) {
			processModel(((IModelProvider)object).getModel());
		} else if (object instanceof ScanRegion) {
			controller.refresh(); // Axes might have changed
		}
	}
	
	private void processModel(Object model) {
		try {
			controller.setModel(model);
		} catch (Exception ignored) {
			logger.trace("Unable to deal with model "+model, ignored);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter){
		if (controller.getAdapter(adapter)!=null) return controller.getAdapter(adapter);
		if (IPlottingSystem.class == adapter) return (T)system;
		if (IRegionSystem.class == adapter)   return (T)system;
		if (IToolPageSystem.class == adapter) return system.getAdapter(adapter);
		return super.getAdapter(adapter);
	}

	@Override
	public void setFocus() {
		if (system!=null) system.setFocus();
	}

	@Override
	public void dispose() {
		getSite().getPage().removeSelectionListener(this);
		controller.dispose();
 		super.dispose();
	}

}
