package org.eclipse.scanning.device.ui.device;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.scanning.api.device.IActivatable;
import org.eclipse.scanning.api.device.IRunnableDevice;
import org.eclipse.scanning.api.device.IRunnableDeviceService;
import org.eclipse.scanning.api.event.scan.DeviceInformation;
import org.eclipse.scanning.api.scan.ScanningException;
import org.eclipse.scanning.device.ui.Activator;
import org.eclipse.scanning.device.ui.EventConnectionView;
import org.eclipse.scanning.device.ui.ServiceHolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shows a list of available detectors to the user. 
 * They may click on one and configure it.
 * 
 * @author Matthew Gerring
 *
 */
public class DetectorView extends EventConnectionView {

	private static final Logger logger = LoggerFactory.getLogger(DetectorView.class);
	public  static final String ID     = "org.eclipse.scanning.device.ui.detectorView";
	
	// UI
	private TableViewer       viewer;
	private Image             ticked, unticked, defaultIcon;
	private Map<String,Image> iconMap;

	// Services
	private IRunnableDeviceService dservice;
	
	public DetectorView() {
		try {
		    this.defaultIcon = Activator.getImageDescriptor("icons/camera-lens.png").createImage();
		    this.ticked      = Activator.getImageDescriptor("icons/ticked.png").createImage();
		    this.unticked    = Activator.getImageDescriptor("icons/unticked.gif").createImage();
		} catch (Throwable ne) {
			ne.printStackTrace(); // Should not happen
		}
		this.iconMap     = new HashMap<>(7);
	}
	
            
	@Override
	public void createPartControl(Composite parent) {
		
		viewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
		
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(false);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

		createColumns(viewer, "", "Name");
		

		try {
			this.dservice = ServiceHolder.getEventService().createRemoteService(getUri(), IRunnableDeviceService.class);
			viewer.setContentProvider(new DetectorContentProvider(dservice));
		} catch (Exception e) {
			logger.error("Cannot create content provider", e);
		}
		viewer.setInput(new Object());
		
		getSite().setSelectionProvider(viewer);
		
		createActions();
		initializeToolBar();
		initializeMenu();

	}

;
	
	private void createColumns(TableViewer tableViewer, String icon, String name) {
		
		TableViewerColumn tickedColumn = new TableViewerColumn(tableViewer, SWT.CENTER);
		tickedColumn.getColumn().setWidth(24);
		tickedColumn.getColumn().setMoveable(false);
		tickedColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				
				if (!(element instanceof DeviceInformation)) return null;
				DeviceInformation<?> info = (DeviceInformation<?>)element;
				
				return info.isActivated() ? ticked : unticked;
			}
			@Override
			public String getText(Object element) {
				return null;
			}
		});
		
        MouseAdapter mouseClick = new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
			    Point pt = new Point(e.x, e.y);
				TableItem item = viewer.getTable().getItem(pt);
				if (item == null) return;
				Rectangle rect = item.getBounds(0);
				if (rect.contains(pt)) {
					final DeviceInformation<?> info = (DeviceInformation<?>)item.getData();
					try {
						IActivatable device = (IActivatable)dservice.getRunnableDevice(info.getName());
						device.setActivated(!info.isActivated());
						info.setActivated(!info.isActivated());
						getViewSite().getActionBars().getStatusLineManager().setErrorMessage(null);
					} catch (ScanningException e1) {
						logger.error("Unable to set activated state!", e1);
						getViewSite().getActionBars().getStatusLineManager().setErrorMessage("Server is not available: "+e1.getLocalizedMessage());
					}
					tableViewer.refresh(info);
				}
			}
        };
		tableViewer.getTable().addMouseListener(mouseClick);

		
		TableViewerColumn iconColumn = new TableViewerColumn(tableViewer, SWT.CENTER);
		iconColumn.getColumn().setWidth(24);
		iconColumn.getColumn().setMoveable(false);
		iconColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				if (!(element instanceof DeviceInformation)) return null;
				DeviceInformation<?> info = (DeviceInformation<?>)element;
				if (info.getIcon()==null) return defaultIcon;
				try {
					return getIcon(info.getIcon());
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			@Override
			public String getText(Object element) {
				return null;
			}
		});

		TableViewerColumn nameColumn = new TableViewerColumn(tableViewer, SWT.LEFT);
		nameColumn.getColumn().setWidth(300);
		nameColumn.getColumn().setMoveable(false);
		nameColumn.getColumn().setText(name);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				if (!(element instanceof DeviceInformation)) return null;
				DeviceInformation<?> info = (DeviceInformation<?>)element;
				String label = info.getLabel();
				if (label==null) label = info.getName();
				if (label==null) label = info.getId();
				if (label==null) label = "Unamed Device "+info;
 				return label;
			}
		});
		
	}

	protected Image getIcon(String fullPath) throws IOException {
		
		if (fullPath==null)      return defaultIcon;
		if ("".equals(fullPath)) return defaultIcon;
		
		try {
			if (iconMap.containsKey(fullPath)) return iconMap.get(fullPath);
			final String[] sa = fullPath.split("/");
			final Bundle bundle = Platform.getBundle(sa[0]);
			if (bundle!=null) {
				Image image = new Image(null, bundle.getResource(sa[1]+"/"+sa[2]).openStream());
				iconMap.put(fullPath, image);
			}
			return iconMap.get(fullPath);
		} catch (Exception ne) {
			logger.debug("Cannot get icon for "+fullPath, ne);
			return defaultIcon;
		}
	}

	@Override
	public void setFocus() {
		viewer.getTable().setFocus();
	}

	public static String createId(final String uri, final String requestName, final String responseName) {
		
		final StringBuilder buf = new StringBuilder();
		buf.append(ID);
		buf.append(":");
		buf.append(createSecondaryId(uri, requestName, responseName));
		return buf.toString();
	}
	/**
	 * Create the actions.
	 */
	private void createActions() {
		IAction refresh = new Action("Refresh", Activator.getImageDescriptor("icons/recycle.png")) {
			public void run() {
				viewer.refresh();
			}
		};
		getViewSite().getActionBars().getToolBarManager().add(refresh);
		getViewSite().getActionBars().getMenuManager().add(refresh);
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		@SuppressWarnings("unused")
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		@SuppressWarnings("unused")
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	
	public void dispose() {
		super.dispose();
		if (ticked!=null)   ticked.dispose();
		if (unticked!=null) unticked.dispose();
		if (defaultIcon!=null) defaultIcon.dispose();
		for (String path : iconMap.keySet()) iconMap.get(path).dispose();
		iconMap.clear();
	}
}
