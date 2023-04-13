
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.lang.Object;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class ImageDisplay {

    JFrame frame;
    JLabel lbIm1;
    JLabel lbIm2;
    BufferedImage img;
    int width = 512;
    int height = 512;

    // Draws a black line on the given buffered image from the pixel defined by (x1, y1) to (x2, y2)
    public void drawLine(BufferedImage image,int x1, int y1, int x2, int y2, float s) {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));
        g.drawLine(x1, y1, x2, y2);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        
    }

    public void showIms(String param0, String param1, String param2){

        //System.out.println("The first parameter was: " + param0);


        float s = Float.valueOf(param1);
        s = 1 - s;
        if(s<=0)
            s=1;

        int als = Integer.parseInt(param2);
        if((als!=0 && als!=1))
            als = 0;

        // Initialize a plain white image
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int ind = 0;
        for(int y = 0; y < height; y++){

            for(int x = 0; x < width; x++){

                int r = (int) 255;
                int g = (int) 255;
                int b = (int) 255;

                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                //int pix = ((a << 24) + (r << 16) + (g << 8) + b);
                img.setRGB(x,y,pix);
                ind++;
            }
        }
        
        int n = Integer.parseInt(param0);
        int n1=n;
        
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 2;
        double angleStep = 2 * Math.PI / n;
        double angle = 0;
        for (int i = 0; i < n; i++) {
          angle = i * angleStep;
          double ang = angle;
          double trigcos= Math.cos(angle);
          double trigsin = Math.sin(angle);
          int extrax = 0;
          int extray = 0;
            if(trigcos!=0 && ((ang<(Math.PI/2) && ang>=0) || (ang>(Math.PI*1.5) && ang<=(2*Math.PI))))
            {   
                extrax = (int)(width/2*(1-trigcos));
                extray = (int)(height/2*(trigsin)*((1/trigcos-1)));
            }
            else if(trigcos!=0 && ((ang>(Math.PI/2) && ang<(3*Math.PI/2))))
            {
                extrax = (int)(1*(width/2*(1-trigcos)));
                extray = (int)(1*(height/2*(trigsin)*((1/trigcos-1))));
                extrax *= -1;
                extray *= -1;
            }
            else if(trigcos==0)
            {
              extrax = 0;
              extray = 0;
            }
            
            int endX = (int) ( centerX + extrax + (radius+Math.abs(extrax))* trigcos);
            int endY = (int) ( centerY + extray + (radius+Math.abs(extray)) * trigsin);

            drawLine(img,centerX, centerY, endX, endY,s);
        }
        
        // Use labels to display the images
        frame = new JFrame();
        GridBagLayout gLayout = new GridBagLayout();
        frame.getContentPane().setLayout(gLayout);

        JLabel lbText1 = new JLabel("Original image (Left)");
        lbText1.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel lbText2 = new JLabel("Image after modification (Right)");
        lbText2.setHorizontalAlignment(SwingConstants.CENTER);
        lbIm1 = new JLabel(new ImageIcon(img));

    
        BufferedImage new_img = new BufferedImage((int)(width*s), (int)(height*s), BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y < (int)(height*s); y++){

            for(int x = 0; x < (int)(width*s); x++)
            {
                int r = (int) 255;
                int g = (int) 255;
                int b = (int) 255;

                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                new_img.setRGB(x,y,pix);
            }
        }



//Anti - Aliasing
        int interval = (int)(1/s),k=0;
        int xpos=0, ypos=0;
        //System.out.println(interval);
        if(als==1)
        {
			float[][] rp = new float[512][512];
			float[][] bp = new float[512][512];
			float[][] gp = new float[512][512];
            for(int i=1;i<height-1;i+=interval)
            {
                float p=0, r=0, g=0, b=0, a=0;
                k=0;
                for(int j=1;j<width-1;j+=interval)
                {
                    k++;   
					p = img.getRGB(i,j);
					Color color = new Color((int)(p), true);
					r = color.getRed();
					g = color.getGreen();
					b = color.getBlue();
					rp[i][j] = r;
					gp[i][j] = g;
					bp[i][j] = b;
                }
            }
			int p=0, r=0, g=0, b=0, a=0;
			for(int i=1;i<height-1;i+=interval)
            {
                
                k=0;
                for(int j=1;j<width-1;j+=interval)
                {
                    k++;
                    r = (int)(rp[i][j] + rp[i-1][j-1] + rp[i-1][j] + rp[i-1][j+1] + rp[i][j-1] + rp[i][j+1] + rp[i+1][j-1] + rp[i+1][j] + rp[i+1][j+1])/9;
					g = (int)(gp[i][j] + gp[i-1][j-1] + gp[i-1][j] + gp[i-1][j+1] + gp[i][j-1] + gp[i][j+1] + gp[i+1][j-1] + gp[i+1][j] + gp[i+1][j+1])/9;
					b = (int)(bp[i][j] + bp[i-1][j-1] + bp[i-1][j] + bp[i-1][j+1] + bp[i][j-1] + bp[i][j+1] + bp[i+1][j-1] + bp[i+1][j] + bp[i+1][j+1])/9;

					long pp = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | ((b&0xff)<<0);
                    xpos = (int)(i*s);
                    ypos = (int)(j*s);
                    new_img.setRGB((int)(xpos), (int)(ypos), (int)(pp));
                }
            }
        }
        if(als==0)
        {
            for(int i=0;i<height;i+=interval)
            {
                int p, r, g, b, a;
                k=0;
                for(int j=0;j<width;j+=interval)
                {
                    k++;
                    p = (img.getRGB(i,j));
                    xpos = (int)(i*s);
                    ypos = (int)(j*s);
                    new_img.setRGB((int)(xpos), (int)(ypos), p);
                }
            }
        }

        lbIm2 = new JLabel(new ImageIcon(new_img));


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        frame.getContentPane().add(lbText1, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        frame.getContentPane().add(lbText2, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        frame.getContentPane().add(lbIm1, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        frame.getContentPane().add(lbIm2, c);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
    }

    public static void main(String[] args) {
        ImageDisplay ren = new ImageDisplay();
        int ctr = 0;
        {
            ren.showIms(args[0],args[1],args[2]);
        }
    }

}
