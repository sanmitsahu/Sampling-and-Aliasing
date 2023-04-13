import java.awt.image.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;

//Class Extends JPanel to organize components
public class VideoDisplay extends JPanel{
    
    private final float lfps = 60;
    private float rfps;
    private int n;
    private float s;
    final int LDelay = Math.round(1000.0f/lfps);
    private float LCounter = 0, RCounter = 0, lstep, rstep, spokeStep;
    private int RIGHT_IMAGE_DELAY;
    JLabel LPane, RPane;
    private int xcenter = 256, ycenter = 256;
    private int size = 512;
    private Timer LTimer, RTimer;
    private ImageIcon leftTemp, rightTemp;

    //Function to create a JFrame to display videos
    private void display_videos(){
        //Create an instance of JFrame
        JFrame f = new JFrame("Display videos");
        //Add The current object
        f.getContentPane().add(this);
        f.pack();
        f.setVisible(true);
        //Stop execution on closing the frame
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //Function to Update the contents of the Left Pane
    public void updateLPane(){
        //Calculate left counter
        LCounter = (LCounter + lstep)%360;
        //Create the left image
        leftTemp = makeImage(LCounter);
        leftTemp.getImage().flush();
        //Add left image to the Left pane
        LPane.setIcon(leftTemp);
        LPane.revalidate();
        //repaint the left pane
        LPane.repaint();
    }
    //Function to Update the contents of the Right Pane
    public void updateRPane(){
        //Calculate Right Pane
        RCounter = (RCounter + rstep)%360;
        //Create Right Image
        rightTemp = makeImage(RCounter);
        rightTemp.getImage().flush();
        //Add Right image to Right Pane
        RPane.setIcon(rightTemp);
        RPane.revalidate();
        //Repaint the right pane
        RPane.repaint();
    }


    public VideoDisplay(String[] arguments){
        //n is the number of radial lines in the image, passed from command line as argument 0
        n = Integer.parseInt(arguments[0]);
        //s is the speed of rotation of the wheel in the image, passed from command line as argument 1
        s = Float.parseFloat(arguments[1]);
        //rfps is the frames per second that is expected, passed from command line as argument 2
        rfps = Float.parseFloat(arguments[2]);
        //Calculate the delay for right image based on the rfps variable
        RIGHT_IMAGE_DELAY = Math.round(1000.0f/rfps);

        //The angle at which we start drawing the next line for the left image
        lstep = (360/lfps)*s;
        //The angle at which we start drawing the next line for the right image
        rstep = (360/rfps)*s;

        //Create a panel to add the first video
        LPane = new JLabel(makeImage(LCounter));
        //add left pane to the VideoDisplat object
        this.add(LPane);
        //Create a panel to add the first video
        RPane = new JLabel(makeImage(RCounter));
        //add the right pane to the VideoDisplay object
        this.add(RPane);
        spokeStep = 360.0f/(float)n;

        //Create a timer to redraw the left image after a given time interval
        //Add Action Listener to the left timer
        LTimer = new Timer(LDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Call update left pane image
                updateLPane();
            }
        });
        //Start Left Timer
        LTimer.start();

        //Create a timer to redraw the right image after a given time interval
        //Add Action Listener to the Right Timer
        RTimer = new Timer(RIGHT_IMAGE_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Call Update Right Pane Image Function
                updateRPane();
            }
        });
        //Start the right timer
        RTimer.start();
    }


    //Function to create the image with n radial lines
    public ImageIcon makeImage(float degreeStep){
        float angle = degreeStep , m = 0;
        //Converting the angle to be in 0 to 360 range
        angle = angle%360; 
        //Calculating slope
        m = (float)Math.tan(angle*Math.PI/180);

        //Creating a red line starter to observe rotations
        int RLStarter = 0;
        //Creating a new empty BufferedImage
        BufferedImage Temp = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        //Changing the Buffered iamge to white
        Temp = setImageWhite(Temp, size, size);
        for(int i=0;i<n;i++){
            //If angle Equals 0
            if(angle==0){
                Temp = drawLines(511, ycenter, Temp, size, size, RLStarter);
            }
            //If angle Equals 45
            else if(angle==45){
                Temp = drawLines(511, 511, Temp, size, size, RLStarter);
            }
            //If angle Equals 135
            else if(angle==135){
                Temp = drawLines(0, 511, Temp, size, size, RLStarter);
            }
            //If angle Equals 90
            else if(angle==90){
                Temp = drawLines(xcenter, 511, Temp, size, size, RLStarter);
            }
            //If angle Equals 270
            else if(angle==270){
                Temp = drawLines(xcenter, 0, Temp, size, size, RLStarter);
            }
            //If angle Equals 180
            else if(angle==180){
                Temp = drawLines(0, ycenter, Temp, size, size, RLStarter);
            }
            //If angle Equals 225
            else if(angle==225){
                Temp = drawLines(0, 0, Temp, size, size, RLStarter);
            }
            //If angle Equals 315
            else if(angle==315){
                Temp = drawLines(511, 0, Temp, size, size, RLStarter);
            }

            //If angle is between 45 and 0 or between 315 and 360
            else if((0<angle && angle<45) || (315<angle && angle<360)){
                Temp = drawLines(511, Math.round((255*m) + 256), Temp, size, size, RLStarter);
            }
            //If angle is between 45 and 90 or between 90 and 135
            else if((45<angle && angle<90) || (90<angle && angle<135)){
                Temp = drawLines(Math.round((255/m) + 256), 511, Temp, size, size, RLStarter);
            }
            //If angle is between 135 and 180 or between 180 and 225
            else if((135<angle && angle<180) || (180<angle && angle<225)){
                Temp = drawLines(0, Math.round((-255*m) + 256), Temp, size, size, RLStarter);
            }
            //If angle is between 225 and 270 or between 270 and 315
            else if((225<angle && angle<270) || (270<angle && angle<315)){
                Temp = drawLines(Math.round((-255/m) + 256), 0, Temp, size, size, RLStarter);
            }
            //Increment Angle   
            angle += spokeStep;
            //REduce the angle to 0 to 360 range
            angle = angle%360;
            //Recalculate Slope
            m = (float)Math.tan(angle*Math.PI/180);
            //Update Red Line Starter
            if(RLStarter==0)
                RLStarter = 1;
        }
        //Return the new image created
        return (new ImageIcon(Temp));
    }

    //Function to paint the whole image white
    public BufferedImage setImageWhite(BufferedImage img, int width, int height){
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                //set all bytes to 255
                byte r = (byte)255;
                byte g = (byte)255;
                byte b = (byte)255;
                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                //set image to white using setrgb function
                img.setRGB(x,y,pix);
            }
        }
        return img;
    }

    //Function to draw lines
    public BufferedImage drawLines(int x2, int y2, BufferedImage img, int width, int height, int RLStarter){
        
        //x1 and y1 are the start point from where the lines are  drawn
        int x1 = xcenter, y1 = ycenter;

        int delx = x2 - x1;
        int dely = y2 - y1;
        //Initialize start end end points for lines
        int startX = x1, startY = y1, endX = x2, endY = y2;
       //set slopw to zero
        double m = 0;
        //create pixelcolor ariable
        int PColor;
        if(RLStarter==0){
            PColor = 0xff000000 | ((255 & 0xff) << 16) | ((0 & 0xff) << 8) | (0 & 0xff);
            //set Red Line starter to 1
            RLStarter = 1;
        }
        else
            PColor = 0xff000000 | ((0 & 0xff) << 16) | ((0 & 0xff) << 8) | (0 & 0xff);
        
        boolean AX;
        //Calculate Slope
        if(delx != 0) m = dely/(double)delx;
        
        //If slope is less than 1

        if(Math.abs(m) <= 1 && delx !=0) {
            AX = true;
            //If delta x is negative
            if(delx < 0) {
                //Assign start and end values
                startX = x2;
                startY = y2;
                endX = x1;
                endY = y1;
            }
            m = (endY - startY)/(double)(endX - startX);
        }
        else {
            AX = false;
            //Id dely is negative
            if(dely < 0) {
                //Assign start and end values
                startX = x2;
                startY = y2;
                endX = x1;
                endY = y1;
            }
            //Calculating the slope, it gives the angle of line with horizontal axis
            if(delx == 0) {m = 0;}
            else {m = (endX - startX)/(double)(endY - startY);}
        }
        
        //Setting x and y position for the pixel and RGB pixel value
        img.setRGB(startX, startY, PColor);
        
        if(AX) {
            double y = startY + 0.5;
            for(int x = startX + 1; x <= endX; x++) {
                //Calculating the y coordinate of the radial line
                y = y + m;
                //Setting x and y position for the pixel and RGB pixel value
                img.setRGB(x, (int)Math.floor(y), PColor);
            }
        }
        else {
            double x = startX + 0.5;
            for(int y = startY + 1; y <= endY; y++) {
                //Calculating the x coordinate of the radial line
                x = x + m;
                //Setting x and y position for the pixel and RGB pixel value
                img.setRGB((int)Math.floor(x), y, PColor);
            }
        }
        return img;
    }

    //Main Function
    public static void main(String[] args){
        //run the piece of code on the AWT thread.
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                //Pass command line arguments to the public function VideoDisplay
                new VideoDisplay(args).display_videos();
            }
        });
    }
}