package cooperzuranski.finalproject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import org.jfree.chart.*;
import org.jfree.data.xy.XYSeries;
import javax.swing.*;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

public abstract class MaskGraphs implements Serializable{//I know couldve done this with one class, "switch syle", but wanted to show abstract use - I think this was actually way nicer in the end
    double m, b;//for line where y= mx + b
    double rSq;
    XYSeriesCollection data;//used as a means to save data between show->polate graphs
    XYDataset dataSet;//sim
    ChartPanel chartPanel;//sim, and needed for immediate update
    
    public MaskGraphs( double m, double b, double rSq,  double[][] points ){//constructor 
        this.m = m;
        this.b = b;
        this.rSq = rSq;
        double x;
        XYSeries scatterPts = new XYSeries("Tests");
        XYSeries regression = new XYSeries("Linear Regression");
        this.data = new XYSeriesCollection();
        
        for (double[] point : points) {//scatter plot points
            scatterPts.add(point[0], point[1]);
        }
        data.addSeries(scatterPts);
        regression.add(x = scatterPts.getDataItem(0).getXValue(), m*x + b);//start of trendline
        regression.add(x = scatterPts.getDataItem(scatterPts.getItemCount() - 1).getXValue(), m*x + b);//end of trendline
        data.addSeries(regression);
        
        this.dataSet = this.data;//casted series collection to dtaset to graph    
    }//constructor
    
    public JPanel showGraph(boolean popout){//can be returned to be inside of main panel or own JFrame!
        //makes physical graph out of pieces
        JFreeChart chart = createChart(this.dataSet);
        this.chartPanel = new ChartPanel(chart);
        
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BorderLayout());
        myPanel.add(this.chartPanel);
        Label regLabel = new Label("R Squared: " + Double.toString(rSq), Label.CENTER);
            regLabel.setBackground(Color.ORANGE);
            regLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 27));//increase fontsize
            myPanel.add(regLabel, BorderLayout.SOUTH);
        JPanel polPanel = new JPanel();//polate button and input
            polPanel.setLayout(new GridLayout(13,1));
            JTextField polateIn = new JTextField("Enter an X value", JTextField.CENTER);//text entry
                polateIn.setPreferredSize( new Dimension(140,20) );
            JButton goButton = new JButton("GO");//button
                goButton.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent a){//does stuff when button pressed
                        try{
                            polateIn.setBackground(Color.WHITE);//reset state
                            polate(Double.valueOf(polateIn.getText()));
                        }catch(NumberFormatException e){
                            polateIn.setBackground(Color.red);//error state of input
                        }//trycatch input err
                    }//new action performed 
                });//inner class action listner
                goButton.setPreferredSize( new Dimension(140,20) );
            JLabel polLabel = new JLabel("Interpolate / Extrapolate:");
                polLabel.setPreferredSize( new Dimension(140,20) );
            for(int i =0; i <5; i++)//fill space for centered UI
                polPanel.add(new JLabel());
            polPanel.add(polLabel);
            polPanel.add(polateIn);
            polPanel.add(goButton);  
            for(int i = 0; i < 5; i++)//fill space for centered UI
                polPanel.add(new JLabel());
            myPanel.add(polPanel, BorderLayout.EAST);
        
            if(popout){//individual window
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.add(myPanel);
                frame.pack();
                frame.setVisible(true);
            }
            return myPanel;
    }//show graph
    
    private void polate(double inputX){//adds inter/extrapolation
        double x; 
        Double result;
        JFreeChart newChart;
        
        if(this.data.getSeriesCount() == 3){//have same name different value, must remove
            this.data.removeSeries(2);
        }
 
        result = this.m*inputX + this.b;//new y value
        if(inputX > this.data.getDomainUpperBound(false) ){//above dataset
            XYSeries pol = new XYSeries("Extrapolation Result: "+ result.toString());
            pol.add(inputX ,result);//new point
            pol.add(x =this.data.getDomainUpperBound(false), this.m*x + this.b);
            this.data.addSeries(pol);//compiler was angry
            this.dataSet = this.data;//add by cast new set
            newChart = this.reCreateChart(this.dataSet, result, true);//yucky duplicated code
        }else if (inputX < this.data.getDomainLowerBound(false)){//below dataset
            XYSeries pol = new XYSeries("Extrapolation Result: "+ result.toString());
            pol.add(inputX ,result);//new point
            pol.add(x =this.data.getDomainLowerBound(false), this.m*x + this.b);
            this.data.addSeries(pol);
            this.dataSet = this.data;//add by cast new set
            newChart = this.reCreateChart(this.dataSet, result, true);
        }else{//interpolate
            XYSeries pol = new XYSeries("Interpolation Result: "+ result.toString());//pol is declared inside due to diffrent labels
            pol.add(inputX, result);
            this.data.addSeries(pol);
            this.dataSet = this.data;//add by cast new set
            newChart = this.reCreateChart(this.dataSet, result, false);
        }
        
        this.chartPanel.setChart(newChart);
        this.chartPanel.repaint();//update
    }//polate
    
    public abstract JFreeChart createChart(XYDataset dataSet);//differes depending on specific data, chart is an argument of final graph
    
    protected abstract JFreeChart reCreateChart(XYDataset dataSet, Double resultY, boolean extra);//refreshes after an update 
}
