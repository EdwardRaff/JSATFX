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

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.paint.Color;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.Classifier;
import jsat.classifiers.DataPoint;
import jsat.linear.DenseVector;
import static java.lang.Math.*;

/**
 *
 * @author Edward Raff <Raff.Edward@gmail.com>
 * @param <X>
 * @param <Y>
 */
public class ClassificationPlot<X extends Number, Y extends Number> extends ScatterChart<X, Y>
{
    private boolean hard = false;
    private int resolution = 5;
    private double backgroundOpacity = 0.8;

    private Canvas canvas;
    private Classifier classifier;

    public ClassificationPlot(Axis<X> xAxis, Axis<Y> yAxis, Classifier classifier)
    {
        super(xAxis, yAxis);
        canvas = new Canvas(7, 7);
        
        getPlotChildren().add(canvas);
        this.classifier = classifier;
    }
    
    @Override
    protected void layoutChildren()
    {
        super.layoutChildren(); //To change body of generated methods, choose Tools | Templates.
    }

    public void setResolution(int resolution)
    {
        this.resolution = resolution;
    }

    public int getResolution()
    {
        return resolution;
    }
    
    public void setHardBoundaries(boolean hard)
    {
        this.hard = hard;
    }
    
    public boolean isHardBoundaries()
    {
        return hard;
    }
    
    
    @Override
    protected void layoutPlotChildren()
    {   
        super.layoutPlotChildren();
        
        double x_min = ((NumberAxis)getXAxis()).getLowerBound();
        double y_min = ((NumberAxis)getYAxis()).getLowerBound();
        double x_max = ((NumberAxis)getXAxis()).getUpperBound();
        double y_max = ((NumberAxis)getYAxis()).getUpperBound();
        
        
        double mid_x = getXAxis().getDisplayPosition((X) Double.valueOf((x_max+x_min)/2));
        double mid_y = getYAxis().getDisplayPosition((Y) Double.valueOf((y_max+y_min)/2));
        
        
        if (canvas != null)
        {
            getPlotChildren().remove(canvas);
            getPlotChildren().add(0, canvas = new Canvas(getWidth(), getHeight()));
            
            Color[] colors = Utils.getNcolors(getData().size());
            for(int i = 0; i < colors.length; i++)
            {
                colors[i] = colors[i].brighter();
                colors[i] = colors[i].deriveColor(0, 1, 1, backgroundOpacity);
            }
            
            final double w = canvas.prefWidth(-1);
            final double h = canvas.prefHeight(-1);
            
            
            double leftMost = mid_x-(w/2);
            double topMost = mid_y-(h/2);
            
            GraphicsContext graphics = canvas.getGraphicsContext2D();
            graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            graphics.setFill(Color.BLUE);
            graphics.setStroke(Color.BLUE);
            for(int i = 0; i < canvas.getWidth(); i+=resolution)
                for(int j = 0; j < canvas.getHeight(); j+=resolution)
                {
                    double x_val = getXAxis().getValueForDisplay(leftMost+i).doubleValue();
                    double y_val = getYAxis().getValueForDisplay(topMost+j).doubleValue();

                    DataPoint dp = new DataPoint(DenseVector.toDenseVec(x_val, y_val));
                    CategoricalResults classification = classifier.classify(dp);
                    if(hard)
                    {
                        graphics.setFill(colors[classification.mostLikely()]);
                    }
                    else
                    {
                        double R = 0, G = 0,B = 0;
                        for(int k = 0; k < colors.length; k++)
                        {
                            R += colors[k].getRed()*colors[k].getRed()*classification.getProb(k)*65025;
                            G += colors[k].getGreen()*colors[k].getGreen()*classification.getProb(k)*65025;
                            B += colors[k].getBlue()*colors[k].getBlue()*classification.getProb(k)*65025;
                            
                        }

                        graphics.setFill(Color.rgb((int)sqrt(R), (int)sqrt(G), (int)sqrt(B), backgroundOpacity));
                   }
                    graphics.fillRect(i, j, resolution, resolution);
                }
                        
            canvas.resizeRelocate(mid_x - (w / 2), mid_y - (h / 2), w, h);
        }
        
    }

}
