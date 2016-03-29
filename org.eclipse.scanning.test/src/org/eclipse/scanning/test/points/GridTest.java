/*-
 * Copyright © 2015 Diamond Light Source Ltd.
 *
 * This file is part of GDA.
 *
 * GDA is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 *
 * GDA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along
 * with GDA. If not, see <http://www.gnu.org/licenses/>.
 */

package org.eclipse.scanning.test.points;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.dawnsci.analysis.dataset.roi.CircularROI;
import org.eclipse.dawnsci.analysis.dataset.roi.PolygonalROI;
import org.eclipse.dawnsci.analysis.dataset.roi.RectangularROI;
import org.eclipse.scanning.api.points.PointsValidationException;
import org.eclipse.scanning.api.points.IPointGenerator;
import org.eclipse.scanning.api.points.IPointGeneratorService;
import org.eclipse.scanning.api.points.IPosition;
import org.eclipse.scanning.api.points.Point;
import org.eclipse.scanning.api.points.models.BoundingBox;
import org.eclipse.scanning.api.points.models.GridModel;
import org.eclipse.scanning.api.scan.ScanEstimator;
import org.eclipse.scanning.points.PointGeneratorFactory;
import org.junit.Before;
import org.junit.Test;

public class GridTest {

	private IPointGeneratorService service;

	@Before
	public void before() throws Exception {
		service = new PointGeneratorFactory();
	}

	@Test
	public void testFillingRectangleNoROI() throws Exception {

		// Create a grid scan model
		BoundingBox box = new BoundingBox();
		box.setxStart(0);
		box.setyStart(0);
		box.setWidth(3);
		box.setHeight(3);

		GridModel model = new GridModel();
		model.setRows(20);
		model.setColumns(20);
		model.setBoundingBox(box);

		// Get the point list
		IPointGenerator<GridModel, Point> gen = service.createGenerator(model);
		List<Point> pointList = gen.createPoints();

		assertEquals(pointList.size(), gen.size());
		checkPoints(pointList);
		GeneratorUtil.testGeneratorPoints(gen, 20, 20);
	}

	@Test(expected = PointsValidationException.class)
	public void testNegativeRowCount() throws Exception {

		BoundingBox box = new BoundingBox();
		box.setxStart(0);
		box.setyStart(0);
		box.setWidth(3);
		box.setHeight(3);

		GridModel model = new GridModel();
		model.setRows(-20);  // An unsigned integer type would solve this problem...
		model.setColumns(20);
		model.setBoundingBox(box);

		IPointGenerator<GridModel, Point> gen = service.createGenerator(model);
		List<Point> pointList = gen.createPoints();

		assertEquals(pointList.size(), gen.size());
		GeneratorUtil.testGeneratorPoints(gen, 20, 20);
	}

	@Test
	public void testSimpleBox() throws Exception {

		BoundingBox box = new BoundingBox();
		box.setxStart(-0.5);
		box.setyStart(-0.5);
		box.setWidth(5);
		box.setHeight(5);

		GridModel model = new GridModel();
		model.setRows(5);
		model.setColumns(5);
		model.setBoundingBox(box);

		IPointGenerator<GridModel, Point> gen = service.createGenerator(model);
		List<Point> pointList = gen.createPoints();

		// Zeroth point is (0, 0).
		assertEquals(0.0, pointList.get(0).getX(), 1e-8);
		assertEquals(0.0, pointList.get(0).getY(), 1e-8);

		// Oneth point is (1, 0).
		assertEquals(1.0, pointList.get(1).getX(), 1e-8);
		assertEquals(0.0, pointList.get(1).getY(), 1e-8);

		// Fifth point is (0, 1).
		assertEquals(0.0, pointList.get(5).getX(), 1e-8);
		assertEquals(1.0, pointList.get(5).getY(), 1e-8);
	}

	@Test
	public void testSimpleBoxSnake() throws Exception {

		BoundingBox box = new BoundingBox();
		box.setxStart(-0.5);
		box.setyStart(-0.5);
		box.setWidth(5);
		box.setHeight(5);

		GridModel model = new GridModel();
		model.setRows(5);
		model.setColumns(5);
		model.setSnake(true);
		model.setBoundingBox(box);

		IPointGenerator<GridModel, Point> gen = service.createGenerator(model);
		List<Point> pointList = gen.createPoints();

		// Zeroth point is (0, 0).
		assertEquals(0.0, pointList.get(0).getX(), 1e-8);
		assertEquals(0.0, pointList.get(0).getY(), 1e-8);

		// Oneth point is (1, 0).
		assertEquals(1.0, pointList.get(1).getX(), 1e-8);
		assertEquals(0.0, pointList.get(1).getY(), 1e-8);

		// Fifth point is (4, 1).
		assertEquals(4.0, pointList.get(5).getX(), 1e-8);
		assertEquals(1.0, pointList.get(5).getY(), 1e-8);
	}

	@Test
	public void testBackwardsBox() throws Exception {

		BoundingBox box = new BoundingBox();
		box.setxStart(4.5);
		box.setyStart(-0.5);
		box.setWidth(-5);
		box.setHeight(5);

		GridModel model = new GridModel();
		model.setRows(5);
		model.setColumns(5);
		model.setBoundingBox(box);

		IPointGenerator<GridModel, Point> gen = service.createGenerator(model);
		List<Point> pointList = gen.createPoints();

		// Zeroth point is (4, 0).
		assertEquals(4.0, pointList.get(0).getX(), 1e-8);
		assertEquals(0.0, pointList.get(0).getY(), 1e-8);

		// Oneth point is (3, 0).
		assertEquals(3.0, pointList.get(1).getX(), 1e-8);
		assertEquals(0.0, pointList.get(1).getY(), 1e-8);

		// Fifth point is (4, 1).
		assertEquals(4.0, pointList.get(5).getX(), 1e-8);
		assertEquals(1.0, pointList.get(5).getY(), 1e-8);
	}

	@Test
	public void testBackwardsBoxSnake() throws Exception {

		BoundingBox box = new BoundingBox();
		box.setxStart(4.5);
		box.setyStart(-0.5);
		box.setWidth(-5);
		box.setHeight(5);

		GridModel model = new GridModel();
		model.setRows(5);
		model.setColumns(5);
		model.setSnake(true);
		model.setBoundingBox(box);

		IPointGenerator<GridModel, Point> gen = service.createGenerator(model);
		List<Point> pointList = gen.createPoints();

		// Zeroth point is (4, 0).
		assertEquals(4.0, pointList.get(0).getX(), 1e-8);
		assertEquals(0.0, pointList.get(0).getY(), 1e-8);

		// Oneth point is (3, 0).
		assertEquals(3.0, pointList.get(1).getX(), 1e-8);
		assertEquals(0.0, pointList.get(1).getY(), 1e-8);

		// Fifth point is (0, 1).
		assertEquals(0.0, pointList.get(5).getX(), 1e-8);
		assertEquals(1.0, pointList.get(5).getY(), 1e-8);
	}

	@Test
	public void testDoublyBackwardsBox() throws Exception {

		BoundingBox box = new BoundingBox();
		box.setxStart(4.5);
		box.setyStart(4.5);
		box.setWidth(-5);
		box.setHeight(-5);

		GridModel model = new GridModel();
		model.setRows(5);
		model.setColumns(5);
		model.setBoundingBox(box);

		IPointGenerator<GridModel, Point> gen = service.createGenerator(model);
		List<Point> pointList = gen.createPoints();

		// Zeroth point is (4, 4).
		assertEquals(4.0, pointList.get(0).getX(), 1e-8);
		assertEquals(4.0, pointList.get(0).getY(), 1e-8);

		// Oneth point is (3, 4).
		assertEquals(3.0, pointList.get(1).getX(), 1e-8);
		assertEquals(4.0, pointList.get(1).getY(), 1e-8);

		// Fifth point is (4, 3).
		assertEquals(4.0, pointList.get(5).getX(), 1e-8);
		assertEquals(3.0, pointList.get(5).getY(), 1e-8);
	}

	@Test
	public void testDoublyBackwardsBoxSnake() throws Exception {

		BoundingBox box = new BoundingBox();
		box.setxStart(4.5);
		box.setyStart(4.5);
		box.setWidth(-5);
		box.setHeight(-5);

		GridModel model = new GridModel();
		model.setRows(5);
		model.setColumns(5);
		model.setSnake(true);
		model.setBoundingBox(box);

		IPointGenerator<GridModel, Point> gen = service.createGenerator(model);
		List<Point> pointList = gen.createPoints();

		// Zeroth point is (4, 4).
		assertEquals(4.0, pointList.get(0).getX(), 1e-8);
		assertEquals(4.0, pointList.get(0).getY(), 1e-8);

		// Oneth point is (3, 4).
		assertEquals(3.0, pointList.get(1).getX(), 1e-8);
		assertEquals(4.0, pointList.get(1).getY(), 1e-8);

		// Fifth point is (0, 3).
		assertEquals(0.0, pointList.get(5).getX(), 1e-8);
		assertEquals(3.0, pointList.get(5).getY(), 1e-8);
	}

	@Test
	public void testFillingRectangle() throws Exception {

		// Create a simple bounding rectangle
		RectangularROI roi = new RectangularROI(0, 0, 3, 3, 0);

		// Create a raster scan path
		GridModel model = new GridModel();
		model.setRows(20);
		model.setColumns(20);

		// Get the point list
		IPointGenerator<GridModel, Point> gen = service.createGenerator(model, roi);
		List<Point> pointList = gen.createPoints();

		assertEquals(pointList.size(), gen.size());

		checkPoints(pointList);
		GeneratorUtil.testGeneratorPoints(gen, 20, 20);
	}

	@Test
	public void testFillingRectangleIterator() throws Exception {

		// Create a simple bounding rectangle
		RectangularROI roi = new RectangularROI(0, 0, 3, 3, 0);

		// Create a raster scan path
		GridModel model = new GridModel();
		model.setRows(20);
		model.setColumns(20);

		// Get the point list
		IPointGenerator<GridModel, Point> gen = service.createGenerator(model, roi);
		Iterator<Point>     it = gen.iterator();
		List<Point>  pointList = new ArrayList<Point>(7);
		while (it.hasNext()) pointList.add(it.next());

		assertArrayEquals(pointList.toArray(), gen.createPoints().toArray());

		checkPoints(pointList);
		GeneratorUtil.testGeneratorPoints(gen, 20, 20);

	}

	@Test
	public void testFillingCircle() throws Exception {

		// Create a simple bounding rectangle
		CircularROI circle = new CircularROI(1.5, 1.5, 1.5);

		// Create a raster scan path
		GridModel gridScanPath = new GridModel();
		gridScanPath.setRows(20);
		gridScanPath.setColumns(20);

		// Get the point list
		IPointGenerator<GridModel, Point> gen = service.createGenerator(gridScanPath, circle);
		List<Point> pointList = gen.createPoints();

		assertEquals(pointList.size(), gen.size());

		assertEquals(pointList.size(), 316);
		GeneratorUtil.testGeneratorPoints(gen);
	}

	@Test
	public void testFillingBoundingCircleSkewed() throws Exception {

		// Create a simple bounding rectangle
		CircularROI circle = new CircularROI(1.5, 1.5, 15);

		// Create a raster scan path
		GridModel gridScanPath = new GridModel();
		gridScanPath.setRows(20);
		gridScanPath.setColumns(200);

		// Get the point list
		IPointGenerator<GridModel, Point> gen = service.createGenerator(
				gridScanPath, circle);
		List<Point> pointList = gen.createPoints();

		assertEquals(pointList.size(), gen.size());

		assertEquals(pointList.size(), 3156);
		GeneratorUtil.testGeneratorPoints(gen);
	}

	@Test
	public void testFillingPolygon() throws Exception {

		PolygonalROI diamond = new PolygonalROI(new double[] { 1.5, 0 });
		diamond.insertPoint(new double[] { 3, 1.5 });
		diamond.insertPoint(new double[] { 1.5, 3 });
		diamond.insertPoint(new double[] { 0, 1.5 });
		diamond.insertPoint(new double[] { 1.5, 0 });

		BoundingBox box = new BoundingBox();
		box.setxStart(0);
		box.setyStart(0);
		box.setWidth(3);
		box.setHeight(3);

		GridModel model = new GridModel();
		model.setRows(20);
		model.setColumns(20);
		model.setBoundingBox(box);

		// Get the point list
		IPointGenerator<GridModel, Point> gen = service.createGenerator(model, diamond);
		List<Point> pointList = gen.createPoints();

		assertEquals(pointList.size(), gen.size());

		assertTrue(pointList.size() < 400); // Some must not be in the polygon!
		GeneratorUtil.testGeneratorPoints(gen);
	}

	private void checkPoints(List<Point> pointList) {
		
		// Check correct number of points
		assertEquals(20 * 20, pointList.size());

		// Check some random points are correct
		assertEquals(0.075, pointList.get(0).getX(), 1e-8);
		for (int i = 0; i < 20; i++) {
			assertEquals(i, pointList.get(i).getIndex("x"));
			assertEquals(0, pointList.get(i).getIndex("y"));
		}
		for (int i = 20; i < 40; i++) {
			assertEquals(i-20, pointList.get(i).getIndex("x"));
			assertEquals(1, pointList.get(i).getIndex("y"));
		}
		
		assertEquals(0.075, pointList.get(0).getY(), 1e-8);

		assertEquals(0.075 + 3 * (3.0 / 20.0), pointList.get(3).getX(), 1e-8);
		assertEquals(0.075 + 0.0, pointList.get(3).getY(), 1e-8);

		assertEquals(0.075 + 2 * (3.0 / 20.0), pointList.get(22).getX(), 1e-8);
		assertEquals(0.075 + 1 * (3.0 / 20.0), pointList.get(22).getY(), 1e-8);

		assertEquals(0.075 + 10 * (3.0 / 20.0), pointList.get(350).getX(), 1e-8);
		assertEquals(0.075 + 17 * (3.0 / 20.0), pointList.get(350).getY(), 1e-8);

	}

	@Test
	public void testFillingRectangleAwayFromOrigin() throws Exception {

		// Create a simple bounding rectangle
		RectangularROI roi = new RectangularROI(-10.0, 5.0, 3.0, 3.0, 0.0);

		// Create a grid scan path
		GridModel model = new GridModel();
		model.setRows(3);
		model.setColumns(3);

		// Get the point list
		IPointGenerator<GridModel, Point> gen = service.createGenerator(model, roi);
		List<Point> pointList = gen.createPoints();

		assertThat(pointList.size(), is(equalTo(9)));

		// Check some points
		assertThat(pointList.get(0), is(equalTo(new Point(0, -9.5, 0, 5.5))));
		assertThat(pointList.get(1), is(equalTo(new Point(1, -8.5, 0, 5.5))));
		assertThat(pointList.get(3), is(equalTo(new Point(0, -9.5, 1, 6.5))));
		assertThat(pointList.get(5), is(equalTo(new Point(2, -7.5, 1, 6.5))));
		assertThat(pointList.get(7), is(equalTo(new Point(1, -8.5, 2, 7.5))));
	}

	@Test
	public void testFillingRectangleWithSnake() throws Exception {

		// Create a simple bounding rectangle
		RectangularROI roi = new RectangularROI(0, 0, 3, 3, 0);

		// Create a grid scan path
		GridModel model = new GridModel();
		model.setRows(3);
		model.setColumns(3);
		model.setSnake(true);

		// Get the point list
		IPointGenerator<GridModel, Point> gen = service.createGenerator(model, roi);
		List<Point> pointList = gen.createPoints();

		assertThat(pointList.size(), is(equalTo(9)));

		// Check some points
		assertThat(pointList.get(0), is(equalTo(new Point(0, 0.5, 0, 0.5))));
		assertThat(pointList.get(1), is(equalTo(new Point(1, 1.5, 0, 0.5))));
		assertThat(pointList.get(3), is(equalTo(new Point(2, 2.5, 1, 1.5))));
		assertThat(pointList.get(7), is(equalTo(new Point(1, 1.5, 2, 2.5))));
	}
}
