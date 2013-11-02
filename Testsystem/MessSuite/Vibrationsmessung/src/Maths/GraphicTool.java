package Maths;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
 
public class GraphicTool extends JPanel {
    private int[] lightData;
    private int[] vibData;
    final int PAD = 20;
 
    
    public GraphicTool() {
    	
    	this.lightData = new int[] {
    	        21, 14, 18, 03, 86, 88, 74, 87, 54, 77,
    	        61, 55, 48, 60, 49, 36, 38, 27, 20, 18
    	    };
    	this.vibData = new int[] {
    	        21, 14, 18, 03, 86, 88, 74, 87, 54, 77,
    	        61, 55, 48, 60, 49, 36, 38, 27, 20, 18
    	    };
    }

    public GraphicTool(int[] lightData, int[] vibData) {
    	
    	this.vibData = vibData;
    	this.lightData = lightData;
    }
    
    
    protected void paintSensorVals(Graphics2D g2, int yOffset, int[] data, String dataSource) {

        int w = getWidth();
        int h = 300;//getHeight();
        
        DataAnalysis dataAnalysis = new DataAnalysis(data);
        
        // Draw ordinate.
        g2.draw(new Line2D.Double(PAD, PAD + yOffset, PAD, h-PAD + yOffset));
        // Draw abcissa.
        g2.draw(new Line2D.Double(PAD, h-PAD + yOffset, w-PAD, h-PAD + yOffset));
        // Draw labels.
        Font font = g2.getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("0", frc);
        float sh = lm.getAscent() + lm.getDescent();
        // Ordinate label.
        String s = dataSource;
        float sy = PAD + yOffset + ((h - 2*PAD) - s.length()*sh)/2 + lm.getAscent();
        for(int i = 0; i < s.length(); i++) {
            String letter = String.valueOf(s.charAt(i));
            float sw = (float)font.getStringBounds(letter, frc).getWidth();
            float sx = (PAD - sw)/2;
            g2.drawString(letter, sx, sy);
            sy += sh;
        }
        
        
        
        // Abcissa label.
        s = "time";
        sy = h + yOffset - PAD + (PAD - sh)/2 + lm.getAscent();
        float sw = (float)font.getStringBounds(s, frc).getWidth();
        float sx = (w - sw)/2;
        g2.drawString(s, sx, sy);
        // Draw lines.
        double xInc = (double)(w - 2*PAD)/(data.length-1);
        
        double scale = (double)(h - 2*PAD)/dataAnalysis.max / 1.1;
        g2.setPaint(Color.green.darker());
        for(int i = 0; i < data.length-1; i++) {
            double x1 = PAD + i*xInc;
            double y1 = h + yOffset - PAD - scale*data[i];
            double x2 = PAD + (i+1)*xInc;
            double y2 = h + yOffset - PAD - scale*data[i+1];
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
        // Mark data points.
        g2.setPaint(Color.red);
        for(int i = 0; i < data.length; i++) {
            double x = PAD + i*xInc;
            double y = h + yOffset - PAD - scale*data[i];
            g2.fill(new Ellipse2D.Double(x-2, y-2, 4, 4));
        }
        
        g2.setPaint(Color.black);
        
        // Draw max and min
        s = String.valueOf(dataAnalysis.max);
        
        g2.setStroke(new BasicStroke(2));
        double y = h + yOffset - PAD - scale*dataAnalysis.max;
        g2.draw(new Line2D.Double(PAD - 3, y, PAD + 3, y));
        g2.setStroke(new BasicStroke(1));
        sx = PAD + 7;
        g2.drawString(s, sx, (float)y + 4);
        
        
        g2.setPaint(Color.red); g2.setStroke(new BasicStroke(2));
        s = String.valueOf(dataAnalysis.mean);
        g2.draw(new Line2D.Double(PAD, h + yOffset - PAD - scale*dataAnalysis.mean, w - PAD, h + yOffset - PAD - scale*dataAnalysis.mean));
        g2.setPaint(Color.black); 
        g2.drawString(s, sx, (float)(h + yOffset - PAD - scale*dataAnalysis.mean) - 4);
        g2.setStroke(new BasicStroke(1));
        
        
        
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
       
        paintSensorVals(g2, 0, vibData, "vibData");
        paintSensorVals(g2, 300, lightData, "lightData");
    }
 
 
    
    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new GraphicTool());
        f.setSize(800,650);
        f.setLocation(50,50);
        f.setVisible(true);
    }
}
