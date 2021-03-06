package org.eclipse.scanning.device.ui;

public class DevicePreferenceConstants {

	/**
	 * Preference for showing tooltips in control table.
	 */
	public final static String SHOW_CONTROL_TOOLTIPS = "org.eclipse.scanning.device.ui.control.showTips";
	
	/**
	 * Preference for number format.
	 */
	public static final String NUMBER_FORMAT = "org.eclipse.scanning.device.ui.device.numberFormat";
	
	/**
	 * Topic for publishing the result of an event calculation.
	 */
	public static final String PATH_CALCULATION_TOPIC = "uk/ac/diamond/daq/mapping/client/events/PathCalculationEvent";

	/**
	 * Normally scan regions are saved and remembered when the user restarts.
	 */
	public static final String AUTO_SAVE_REGIONS = "org.eclipse.scanning.device.ui.device.autoSaveRegions";

	/**
	 * Stores if the user should not be able to edit the scan sequence. They can still use the UI to switch this back on.
	 */
	public static final String LOCK_SCAN_SEQUENCE = "org.eclipse.scanning.device.ui.control.lockScanPipeline";

	/**
	 * Property to store if we are showing scan regions or not.
	 */
	public static final String SHOW_SCAN_REGIONS = "org.eclipse.scanning.device.ui.vis.showScanRegions";

	/**
	 * If a start position should be set when the user edits the value.
	 */
	public static final String START_POSITION = "org.eclipse.scanning.device.ui.scan.startPosition";
	
	/**
	 * If an end position should be set when the user edits the value.
	 */
	public static final String END_POSITION = "org.eclipse.scanning.device.ui.scan.endPosition";

	/**
	 * Record if the user would like to see scan information
	 */
	public static final String SHOW_SCAN_INFO = "org.eclipse.scanning.device.ui.scan.showScanSummary";

	/**
	 * Record if the user would like to see scan command
	 */
	public static final String SHOW_SCAN_CMD = "org.eclipse.scanning.device.ui.scan.showScanCommand";

	/**
	 * The preference which the stream choice is saved under.
	 */
	public static final String STREAM_ID = "org.eclipse.scanning.device.ui.streams.selectedStreamConnection";

	/**
	 * True if a before script should be run with the UI scan.
	 */
	public static final String BEFORE_SCRIPT = "org.eclipse.scanning.device.ui.scan.beforeScript";

	/**
	 * True if a before script should be run with the UI scan.
	 */
	public static final String AFTER_SCRIPT = "org.eclipse.scanning.device.ui.scan.afterScript";

}
