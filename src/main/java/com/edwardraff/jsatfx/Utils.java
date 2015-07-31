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

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 *
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class Utils
{
    public static Shape getShape(int i, Color fillColor)
    {
        return getShape(i, fillColor, fillColor.darker().darker());
    }
    
    public static Shape getShape(int i, Paint fillColor, Paint strokeColor)
    {
        Shape shape;
        switch(i % 3)
        {
            case 1:
                shape = new Rectangle(5*2, 5*2, fillColor);
                break;
            case 2://Rectangle
                Polygon polygon = new Polygon(5.0,   0.0,
                                              10.0, 10.0,
                                              0.0,  10.0);
                polygon.setFill(fillColor);
                shape = polygon;
                break;
            default://case 0
                shape = new Circle(4.0, fillColor);
        }
        
        shape.setStroke(strokeColor);
        return shape;
    }

    public static Color[] getNcolors(int N)
    {
        Color[] colors = new Color[N];
        int pos = 0;
        for (double h = 0; pos < N; h += 360 / N)
        {
            Color c = Color.hsb(h, 0.9, 0.9);
            colors[pos++] = c;
        }
        return colors;
    }
}
