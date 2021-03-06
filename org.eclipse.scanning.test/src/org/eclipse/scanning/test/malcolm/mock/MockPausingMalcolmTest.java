package org.eclipse.scanning.test.malcolm.mock;

import org.eclipse.scanning.api.malcolm.IMalcolmDevice;
import org.eclipse.scanning.connector.epics.EpicsV4ConnectorService;
import org.eclipse.scanning.test.malcolm.AbstractPausingMalcolmTest;
import org.eclipse.scanning.test.malcolm.device.MockedMalcolmService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

@Ignore("TODO Get this running but needs more work.")
public class MockPausingMalcolmTest extends AbstractPausingMalcolmTest {
	

	@Override
	@Before
	public void create() throws Exception {
		this.connectorService = new EpicsV4ConnectorService();
		this.service      = new MockedMalcolmService(true);
		this.device       = service.getDevice("zebra");
	}

	@Override
	@After
	public void dispose() throws Exception {
		if (device!=null)     device.dispose();
		((MockedMalcolmService)service).dispose();
	}

	@Override
	protected IMalcolmDevice createAdditionalConnection() throws Exception {
		return service.getDevice("zebra");
	}

}
