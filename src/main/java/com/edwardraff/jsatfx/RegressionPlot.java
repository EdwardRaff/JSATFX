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
import java.util.function.DoubleFunction;

/**
 *
 * @author Edward Raff <Raff.Edward@gmail.com>
 * @param <X>
 * @param <Y>
 */
public class RegressionPlot<X extends Number, Y extends Number> extends ScatterChart<X, Y>
{
    private Canvas canvas;
    private DoubleFunction<Double> regressor;
    private int stepSize = 3;

    public RegressionPlot(Axis<X> xAxis, Axis<Y> yAxis, DoubleFunction<Double> regressor)
    {
        super(xAxis, yAxis);
        canvas = new Canvas(7, 7);
        
        getPlotChildren().add(canvas);
        this.regressor = regressor;
    }
    
    @Override
    protected void layoutChildren()
    {
        super.layoutChildren(); //To change body of generated methods, choose Tools | Templates.
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
            
            final double w = getWidth();
            final double h = getHeight();
            
            
            double leftMost = mid_x-(w/2);
            double topMost = mid_y-(h/2);
            
            GraphicsContext graphics = canvas.getGraphicsContext2D();
            graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            graphics.setFill(Color.BLUE);
            graphics.setStroke(Color.BLUE);
            for(int i = 0; i < canvas.getWidth(); i+=stepSize)
                {
                    double x_val_prev = getXAxis().getValueForDisplay(leftMost+i-stepSize).doubleValue();
                    Double y_val_prev = regressor.apply(x_val_prev);
                    double j_prev = getYAxis().getDisplayPosition((Y) y_val_prev);
                    
                    double x_val = getXAxis().getValueForDisplay(leftMost+i).doubleValue();
                    Double y_val = regressor.apply(x_val);
                    double j = getYAxis().getDisplayPosition((Y) y_val);
                    
                    graphics.moveTo(i-stepSize, j_prev);
                    graphics.lineTo(i, j);
                    graphics.stroke();
                }
                        
//            canvas.resizeRelocate(mid_x - (w / 2), mid_y - (h / 2), w, h);
            canvas.resizeRelocate(mid_x - (w / 2), 0, w, h);//Why does this work but not 0, 0 ?
        }
        
    }

}
