package cooperzuranski.finalproject;

import java.awt.Color;
import java.io.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

public class HumdGraph extends MaskGraphs implements Serializable{

    public HumdGraph(double m, double b, double rSq,  double[][] points){
        super( m,  b,  rSq,  points);
    }
    
    @Override
    public JFreeChart createChart(XYDataset dataSet){//implementation from abstract method
        JFreeChart chart = ChartFactory.createScatterPlot("Humidity VS Particles <= 5um", "Humidity: %", "# Particles", 
            dataSet, PlotOrientation.VERTICAL, true, false, false);
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer render = (XYLineAndShapeRenderer)plot.getRenderer();
        render.setSeriesPaint(0,Color.BLACK);
        render.setSeriesLinesVisible(1, Boolean.TRUE);//0th series is scatter - default correct
        render.setSeriesShapesVisible(1, Boolean.FALSE);//just want line visible, not default on scatter
        return chart;
    }//create chart
    
    @Override
    protected JFreeChart reCreateChart(XYDataset dataSet, Double resultY, boolean extra){
        JFreeChart newChart = ChartFactory.createScatterPlot("Humidity VS Particles <= 5um", "Humidity: %", "# Particles",
            dataSet, PlotOrientation.VERTICAL, true, false, false);
        XYPlot plot = newChart.getXYPlot();
        XYLineAndShapeRenderer render = (XYLineAndShapeRenderer)plot.getRenderer();
        render.setSeriesPaint(0,Color.BLACK);
        render.setSeriesLinesVisible(1, Boolean.TRUE);//0th series is scatter - default correct
        render.setSeriesShapesVisible(1, Boolean.FALSE);//just want line visible, not default on scatter
        if(extra){
            render.setSeriesLinesVisible(2, Boolean.TRUE);
            render.setSeriesShapesVisible(2, Boolean.FALSE);
        }else{//interpolation
            render.setSeriesLinesVisible(2, Boolean.FALSE);
            render.setSeriesShapesVisible(2, Boolean.TRUE);
        }//inter or extrapolate
        render.setSeriesPaint(2,Color.BLUE);
        
        return newChart;
    }//recreate chart
}//endclass
