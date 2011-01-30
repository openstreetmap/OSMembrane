package de.osmembrane.model.algorithms;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import de.osmembrane.model.pipeline.AbstractConnector;
import de.osmembrane.model.pipeline.AbstractFunction;
import de.osmembrane.model.pipeline.AbstractParameter;

public class GraphPlanarizer {

	private List<AbstractFunction> functions;

	private static final double X_OFFSET = 400.0;
	private static final double Y_OFFSET = 300.0;

	public GraphPlanarizer(List<AbstractFunction> functions) {
		this.functions = functions;
	}

	public void planarize() {
		/* Reset all function to their zero-position */
		resetAllFunctions();

		List<AbstractFunction> pirmaryFunctions = getPrimaryFunctions();

		/* set the xOffsetfactor to -1.0 'cause orderFunction will add 1.0 in the first step */
		double xOffsetFactor = -1.0;
		double yOffsetFactor = 0.0;
		for (AbstractFunction function : pirmaryFunctions) {
			System.out.println("> orderFunction(" + function.hashCode() + "," + xOffsetFactor + "," + yOffsetFactor + ")");
			double newYOffset = orderFunctions(function, xOffsetFactor, yOffsetFactor);
			yOffsetFactor += newYOffset;
			System.out.println("> returned yOffsetFactor=" + newYOffset);
		}
	}

	private void resetAllFunctions() {
		for (AbstractFunction function : functions) {
			function.getCoordinate().setLocation(0.0, 0.0);
		}
	}

	/**
	 * Returns all functions with no connections at their inConnectors.
	 * 
	 * @return all functions with no connections at their inConnectors.
	 */
	private List<AbstractFunction> getPrimaryFunctions() {
		List<AbstractFunction> primaryFunctions = new ArrayList<AbstractFunction>();
		for (AbstractFunction function : functions) {
			for (AbstractConnector connector : function.getInConnectors()) {
				if (connector.getConnections().length > 0) {
					/* found a inConnection, do not add as a primary one */
					break;
				}
			}
			primaryFunctions.add(function);
		}
		return primaryFunctions;
	}

	/**
	 * Orders all functions connected to this function.
	 * 
	 * @return returns the required space in height
	 */
	private double orderFunctions(AbstractFunction function, double xOffsetFactor, double yOffsetFactor) {
		xOffsetFactor++;
		
		System.out.println("| " + function.hashCode() + "\t" + xOffsetFactor + "\t" + yOffsetFactor);
		
		function.getCoordinate().setLocation(X_OFFSET * xOffsetFactor, Y_OFFSET * yOffsetFactor);
		
		boolean moreThanOneConnection = false;
		for(AbstractConnector connector : function.getOutConnectors()) {
			for(AbstractConnector connector2 : connector.getConnections()) {
				if(moreThanOneConnection) {
					yOffsetFactor++;
				} else {
					moreThanOneConnection = true;
				}
				yOffsetFactor += orderFunctions(connector2.getParent(), xOffsetFactor, yOffsetFactor);
			}
		}

		return yOffsetFactor;
	}
}