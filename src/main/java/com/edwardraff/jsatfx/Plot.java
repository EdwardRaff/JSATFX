/*
 * Copyright (C) 2015 Your Organisation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.edwardraff.jsatfx;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleFunction;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import jsat.DataSet;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import jsat.regression.RegressionDataSet;
import jsat.regression.Regressor;

/**
 *
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class Plot
{
    
    private static double getTick(Vec x)
    {
        return getTick(x, 10);
    }
    
    
    private static double getTick(Vec x, int number)
    {
        return getTick(x.min(), x.max(), number);
    }
    
    private static double getTick(double min, double max, int number)
    {
        double range = max-min;
        
        int shift = 1;
        
        while(true)
        {
             double candidate = Math.pow(number, Math.round(Math.log(range)/Math.log(number))-shift);
             if(range/candidate < 1)
                 shift++;
             else
                 return candidate;
        }
    }

    protected static NumberAxis vecToAxis(String name, Vec xVals)
    {
        double min = xVals.min();
        double max = xVals.max();
        double range = max-min;
        min -= range*0.05;
        max += range*0.05;
        NumberAxis xAxis = (name == null) ? 
                new NumberAxis(min, max, getTick(xVals)) :
                new NumberAxis(name.trim(), min, max, getTick(xVals));
        return xAxis;
    }
    
    /**
     * Creates a scatter plot of the given dataset using the first two columns
     * for the x and y axis.
     *
     * @param d the dataset to plot
     * @return a scatter plot of the data
     */
    public static ScatterChart<Number, Number> scatter(DataSet d)
    {
        return scatter(d, 0, 1);
    }

    /**
     * Creates a scatter plot of the given dataset
     * @param d the dataset to plot
     * @param x the index of the feature to use for the x axis
     * @param y the index of the feature to use for the y axis
     * @return a scatter plot of the data
     */
    public static ScatterChart<Number, Number> scatter(DataSet d, int x, int y)
    {
        Vec xVals = d.getNumericColumn(x);
        Vec yVals = d.getNumericColumn(y);
        
        NumberAxis xAxis = vecToAxis(d.getNumericName(x), xVals);
        NumberAxis yAxis = vecToAxis(d.getNumericName(y), yVals);
        
        final ScatterChart<Number,Number> sc =  new ScatterChart<>(xAxis,yAxis);
        
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        
        for(int i = 0; i < xVals.length(); i++)
            series.getData().add(new XYChart.Data(xVals.get(i), yVals.get(i)));
        
        sc.getData().addAll(series);
        
        
        sc.setLegendVisible(false);
        
        return sc;
    }
    
    /**
     * Creates a scatter plot of the given dataset, where points will be
     * categorized based on the class they are in. The first two columns will be
     * used for the x and y values.
     *
     * @param d the dataset to plot
     * @return a scatter plot of the data
     */
    public static ScatterChart<Number, Number> scatterC(ClassificationDataSet d)
    {
        return scatterC(d, 0, 1);
    }
    
    /**
     * Creates a scatter plot of the given dataset, where points will be
     * categorized based on the class they are in.
     *
     * @param d the dataset to plot
     * @param x the index of the feature to use for the x axis
     * @param y the index of the feature to use for the y axis
     * @return a scatter plot of the data
     */
    public static ScatterChart<Number, Number> scatterC(ClassificationDataSet d, int x, int y)
    {
        Vec xVals = d.getNumericColumn(0);
        Vec yVals = d.getNumericColumn(1);
        
        NumberAxis xAxis = vecToAxis(d.getNumericName(0), xVals);
        NumberAxis yAxis = vecToAxis(d.getNumericName(1), yVals);
        
        ScatterChart<Number,Number> sc =  new ScatterChart<>(xAxis,yAxis);
        
        List<XYChart.Series<Number, Number>> allSeries = new ArrayList<>();
        for(int i = 0; i < d.getClassSize(); i++)
        {
            allSeries.add(new XYChart.Series<>());
            allSeries.get(i).setName(d.getPredicting().getOptionName(i));
        }
        
        for(int i = 0; i < xVals.length(); i++)
            allSeries.get(d.getDataPointCategory(i)).getData().add(new XYChart.Data(xVals.get(i), yVals.get(i)));
            
        for (XYChart.Series<Number, Number> series : allSeries)
            sc.getData().addAll(series);

        
        sc.setLegendVisible(true);
        
        return sc;
    }
    
    /**
     * Creates a plot of a 2D scatterC problem, and visualizes the classifier's decision throughout the whole space. 
     * @param d the dataset to plot
     * @param classifier the trained classifier to visualize
     * @return a plot of the specified classifier and data
     */
    public static ClassificationPlot<Number, Number> classification(ClassificationDataSet d, Classifier classifier)
    {
        Vec xVals = d.getNumericColumn(0);
        Vec yVals = d.getNumericColumn(1);
        
        NumberAxis xAxis = vecToAxis(d.getNumericName(0), xVals);
        NumberAxis yAxis = vecToAxis(d.getNumericName(1), yVals);
        
        ClassificationPlot<Number, Number> sc = new ClassificationPlot<>(xAxis, yAxis, classifier);
        
        
        Color[] colors = Utils.getNcolors(d.getClassSize());
        
        List<XYChart.Series<Number, Number>> allSeries = new ArrayList<>();
        for(int i = 0; i < d.getClassSize(); i++)
        {
            allSeries.add(new XYChart.Series<>());
            allSeries.get(i).setName(d.getPredicting().getOptionName(i));
        }
        
        
        for(int i = 0; i < xVals.length(); i++)
        {
            int cat = d.getDataPointCategory(i);
            XYChart.Data datum = new XYChart.Data(xVals.get(i), yVals.get(i));
            datum.setNode(Utils.getShape(cat, colors[cat]));
            allSeries.get(cat).getData().add(datum);
        }
        
        
        for (int i = 0; i < colors.length; i++)
        {
            XYChart.Series<Number, Number> series = allSeries.get(i);
            sc.getData().add(series);
        }
        
        //set the legen to correct colors
        Set<Node> items = sc.lookupAll("Label.chart-legend-item");
        
        int i = 0;
        for (Node item : items)
        {
            Label label = (Label) item;
            Shape shape = Utils.getShape(i, colors[i]);
            label.setGraphic(shape);
            i++;
        }
        
        sc.setLegendVisible(true);
        
        
        return sc;
    }
    
    public static RegressionPlot<Number, Number> regression(RegressionDataSet d, Regressor r)
    {
        return regression(d, (double value) -> r.regress(new DataPoint(DenseVector.toDenseVec(value))));
    }
    
    public static RegressionPlot<Number, Number> regression(RegressionDataSet d, DoubleFunction<Double> r)
    {
        Vec xVals = d.getNumericColumn(0);
        Vec yVals = d.getTargetValues();
        
        NumberAxis xAxis = vecToAxis(d.getNumericName(0), xVals);
        NumberAxis yAxis = vecToAxis("Target", yVals);
        
        RegressionPlot<Number, Number> chart = new RegressionPlot<>(xAxis, yAxis, r);
        
        List<XYChart.Data<Number, Number>> data = new ArrayList<>();
        
        //populating the series with data
        for(int i = 0; i < xVals.length(); i++)
        {
            XYChart.Data datum = new XYChart.Data(xVals.get(i), yVals.get(i));
            
            data.add(datum);
        }
        data.sort((XYChart.Data<Number, Number> o1, XYChart.Data<Number, Number> o2) -> Double.compare(o1.getXValue().doubleValue(), o2.getXValue().doubleValue()));

        XYChart.Series<Number,Number> series = new XYChart.Series<>();
        for(XYChart.Data<Number, Number> i : data)
            series.getData().add(i);
        chart.getData().add(series);
        
        
        chart.setLegendVisible(false);
        
        return chart;
    }
}
