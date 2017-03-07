package org.rtmm.prac1;
 
/* ------------------
Client
usage: java Client [Server hostname] [Server RTSP listening port] [Video file requested]
---------------------- */
 
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
 
public class RTSPClient {
 
//GUI
//----
JFrame f = new JFrame("Client");
JButton optionButton = new JButton("Option");
JButton metafileButton = new JButton("Metafile");
JButton describeButton = new JButton("Describe");
JButton setupButton = new JButton("Setup");
JButton playButton = new JButton("Play");
JButton pauseButton = new JButton("Pause");
JButton tearButton = new JButton("Teardown");
JPanel mainPanel = new JPanel();
JPanel buttonPanel = new JPanel();
JLabel iconLabel = new JLabel();
ImageIcon icon;
 
 
//RTP variables:
//----------------
DatagramPacket rcvdp; //UDP packet received from the server
DatagramSocket RTPsocket; //socket to be used to send and receive UDP packets
static int RTP_RCV_PORT = 25000; //port where the client will receive the RTP packets
 
Timer timer; //timer used to receive data from the UDP socket
byte[] buf; //buffer used to store data received from the server  
 
//RTSP variables
//----------------
//rtsp states  
 
static RTSPState state; //RTSP state == INIT or READY or PLAYING
Socket RTSPsocket; //socket used to send/receive RTSP messages
 
//input and output stream filters
static BufferedReader RTSPBufferedReader;
static BufferedWriter RTSPBufferedWriter;
static String VideoFileName; //video file to request to the server
int RTSPSeqNb = 0; //Sequence number of RTSP messages within the session
int RTSPid = 0; //ID of the RTSP session (given by the RTSP Server)
int type = 0;//indicates which method type is being used, so as to specify what needs to be read through the RTSPBufferedReader
 
final static String CRLF = "\r\n";
 
//Video constants:
//------------------
static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
 
//--------------------------
//Constructor
//--------------------------
public RTSPClient() {
 
 //build GUI
 //--------------------------
 
 //Frame
 f.addWindowListener(new WindowAdapter() {
    public void windowClosing(WindowEvent e) {
     System.exit(0);
    }
 });
 
 //Buttons
 buttonPanel.setLayout(new GridLayout(2,0));
 buttonPanel.add(metafileButton);
 buttonPanel.add(playButton);
 buttonPanel.add(optionButton);
 buttonPanel.add(tearButton);
 buttonPanel.add(setupButton);
 buttonPanel.add(pauseButton);
 buttonPanel.add(describeButton);

 optionButton.addActionListener(new optionButtonListener());
 describeButton.addActionListener(new DescribeButtonListener());
 setupButton.addActionListener(new setupButtonListener());
 playButton.addActionListener(new playButtonListener());
 pauseButton.addActionListener(new pauseButtonListener());
 tearButton.addActionListener(new tearButtonListener());
 metafileButton.addActionListener(new metafileButtonListener());
 
 optionButton.setEnabled(false);
 setupButton.setEnabled(false);
 playButton.setEnabled(false);
 pauseButton.setEnabled(false);
 describeButton.setEnabled(false);
 tearButton.setEnabled(false);
 
 //Image display label
 iconLabel.setIcon(null);
 
 //frame layout
 mainPanel.setLayout(null);
 mainPanel.add(iconLabel);
 mainPanel.add(buttonPanel);
 iconLabel.setBounds(0,0,380,280);
 buttonPanel.setBounds(0,280,580,50);
 
 f.getContentPane().add(mainPanel, BorderLayout.CENTER);
 f.setSize(new Dimension(600,370));
 f.setVisible(true);
 
 //init timer
 //--------------------------
 timer = new Timer(20, new timerListener());
 timer.setInitialDelay(0);
 timer.setCoalesce(true);
 
 //allocate enough memory for the buffer used to receive data from the server
 buf = new byte[15000];     
}
 
//------------------------------------
//main
//------------------------------------
public static void main(String argv[]) throws Exception
{
 //Create a Client object
 RTSPClient theClient = new RTSPClient();
 
 //get server RTSP port and IP address from the command line
 //------------------
 int RTSP_server_port = Integer.parseInt(argv[1]);
 String ServerHost = argv[0];
 InetAddress ServerIPAddr = InetAddress.getByName(ServerHost);
 
 //get video filename to request:
 VideoFileName = argv[2];
 
 //Establish a TCP connection with the server to exchange RTSP messages
 //------------------
 theClient.RTSPsocket = new Socket(ServerIPAddr, RTSP_server_port);
 
 //Set input and output stream filters:
 RTSPBufferedReader = new BufferedReader(new InputStreamReader(theClient.RTSPsocket.getInputStream()) );
 RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(theClient.RTSPsocket.getOutputStream()) );
 
 //init RTSP state:
 state = RTSPState.INIT;
}
 
 
//------------------------------------
//Handler for buttons
//------------------------------------
 
//.............
//TO COMPLETE
//.............
 
//Handler for Metadata button
//-----------------------
class metafileButtonListener implements ActionListener{
     public void actionPerformed(ActionEvent e){
 
    if(state == RTSPState.INIT){
         try{
             URL myURL = new URL("http://146.231.122.137:8080/movie.asf"); // "file:///home/rtmm/Desktop/RTSPApplication/movie.mjpeg"
             HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
             conn.setRequestMethod("GET");
 
                      int responseCode = conn.getResponseCode();
                        System.out.println("\nSending 'GET' request to URL : " + myURL);
                        System.out.println("Response Code : " + responseCode);
 
                        BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
 
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine + "\n");
                        
                        }
                        in.close();
                        setupButton.setEnabled(true);
                        metafileButton.setEnabled(false);
                    
                    System.out.println(response.toString());
                    
                       
                    String resp = response.toString();
                    String [] parts = resp.split("<");
                    resp = parts[5];
                    parts = resp.split("/");
                    resp = parts[3];
                    resp =resp.substring(0,11);
                    System.out.println(resp);
                    VideoFileName = resp;

                     
         	} catch (SocketException se)
         		{
         		System.out.println("Socket exception: "+se);
         		System.exit(0);
         		} catch (MalformedURLException me) {
         			// TODO Auto-generated catch block
         			me.printStackTrace();
         		} catch (IOException io) {
         			System.out.println("Exception caught in metadatahandler" + io);
            
         		}
         }
    }
}
 
//Handler for Setup button
//-----------------------
class setupButtonListener implements ActionListener{
 public void actionPerformed(ActionEvent e){
 
  // System.out.println("Setup Button pressed !");       
   if (state == RTSPState.INIT)  
    {
       //Init non-blocking RTPsocket that will be used to receive data
      try{
        //construct a new DatagramSocket to receive RTP packets from the server, on port RTP_RCV_PORT
          RTPsocket = new DatagramSocket(RTP_RCV_PORT);
           
        //set TimeOut value of the socket to 5msec.
          timer.setDelay(5);
      }
      catch (SocketException se)
        {
          System.out.println("Socket exception: "+se);
          System.exit(0);
        }
 
      //init RTSP sequence number
      RTSPSeqNb = 1;
      
      //Send SETUP message to the server
 
      send_RTSP_request(RTSPRequest.SETUP);
 
      //Wait for the response  
      if (parse_server_response() != 200){
        System.err.println("Invalid Server Response");
    
         }
      else  
        {
          optionButton.setEnabled(true);
          setupButton.setEnabled(true);
          playButton.setEnabled(true);
          pauseButton.setEnabled(true);
          describeButton.setEnabled(true);
          tearButton.setEnabled(true);
          //change RTSP state and print new state  
          state = RTSPState.READY;  
          System.out.println("New RTSP state: READY" + CRLF);
          //transport header
          //somehow read session ID
        }
    }//else if state != INIT then do nothing
    
 }
}
//System.out.println(request_type);
//Handler for Play button
//-----------------------
class playButtonListener implements ActionListener {
 public void actionPerformed(ActionEvent e){
 
  // System.out.println("Play Button pressed !");  
 
   if (state == RTSPState.READY)  
    {
      //increase RTSP sequence number
       RTSPSeqNb++;
 
 
      //Send PLAY message to the server
        
      send_RTSP_request(RTSPRequest.PLAY);
 
      //Wait for the response  
      if (parse_server_response() != 200)
          System.err.println("Invalid Server Response");
      else  
        {
          //change RTSP state and print out new state
          state = RTSPState.PLAYING;
          System.out.println("New RTSP state: PLAYING"  + CRLF);
 
          //start the timer
          timer.start();
        }
    }//else if state != READY then do nothing
 }
}
 
 
//Handler for Pause button
//-----------------------
class pauseButtonListener implements ActionListener {
 public void actionPerformed(ActionEvent e){
 
   //System.out.println("Pause Button pressed !");    
 
   if (state == RTSPState.PLAYING)  
    {
      //increase RTSP sequence number
       RTSPSeqNb++;
 
      //Send PAUSE message to the server
      send_RTSP_request(RTSPRequest.PAUSE);
    
      //Wait for the response  
     if (parse_server_response() != 200)
          System.err.println("Invalid Server Response");
      else  
        {
          //change RTSP state and print out new state
          state = RTSPState.READY;
          System.out.println("New RTSP state: READY"  + CRLF);
           
          //stop the timer
          timer.stop();
        }
    }
   //else if state != PLAYING then do nothing
 }
}
 
//Handler for Teardown button
//-----------------------
class tearButtonListener implements ActionListener {
 public void actionPerformed(ActionEvent e){
 
   System.out.println("Teardown Button pressed !");   
 
   //increase RTSP sequence number
   RTSPSeqNb++;
    
 
   //Send TEARDOWN message to the server
   send_RTSP_request(RTSPRequest.TEARDOWN);
 
   //Wait for the response  
   if (parse_server_response() != 200)
    System.err.println("Invalid Server Response");
   else  
    {      
      //change RTSP state and print out new state
       state = RTSPState.INIT;
      System.out.println("New RTSP state: TEARDOWN"  + CRLF);
 
      //stop the timer
      timer.stop();
 
      //exit
      System.exit(0);
    }
 }
}
 
//Handler for Options button
//-----------------------
class optionButtonListener implements ActionListener {
public void actionPerformed(ActionEvent e){
 
 //System.out.println("Options Button pressed !");  
 
        type = 1;
      send_RTSP_request(RTSPRequest.OPTION);
 
      //Wait for the response  
      if (parse_server_response() != 200)
          System.err.println("Invalid Server Response");
      else  
        {  
          System.out.println(""  + CRLF);
        }
    }//else if state != READY then do nothing
}
 
//Handler for Options Play button
//-----------------------
class DescribeButtonListener implements ActionListener {
public void actionPerformed(ActionEvent e){
 
//System.out.println("Play Button pressed !");  

 
        type = 2;
      //Send PLAY message to the server
      send_RTSP_request(RTSPRequest.DESCRIBE);
 
      //Wait for the response  
      if (parse_server_response() != 200)
          System.err.println("Invalid Server Response");
      else  
        {

          System.out.println(""  + CRLF);
 
        }
    }//else if state != READY then do nothing
}
//------------------------------------
//Handler for timer
//------------------------------------
 
class timerListener implements ActionListener {
 public void actionPerformed(ActionEvent e) {
    
   //Construct a DatagramPacket to receive data from the UDP socket
   rcvdp = new DatagramPacket(buf, buf.length);
 
   try{
    //receive the DP from the socket:
    RTPsocket.receive(rcvdp);
    
    //create an RTPpacket object from the DP
    RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());
    //print important header fields of the RTP packet received:  
    System.out.println("Got RTP packet with SeqNum # "+rtp_packet.getsequencenumber()+" TimeStamp "+rtp_packet.gettimestamp()+" ms, of type "+rtp_packet.getpayloadtype());
    
    //print header bitstream:
    //rtp_packet.printheader();
 
    //get the payload bitstream from the RTPpacket object
    int payload_length = rtp_packet.getpayload_length();
    byte [] payload = new byte[payload_length];
    rtp_packet.getpayload(payload);
 
    //get an Image object from the payload bitstream
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Image image = toolkit.createImage(payload, 0, payload_length);
    
    //display the image as an ImageIcon object
    icon = new ImageIcon(image);
    iconLabel.setIcon(icon);
   }
   catch (InterruptedIOException iioe){
    //System.out.println("Nothing to read");
   }
   catch (IOException ioe) {
    System.out.println("Exception caught in client timer: "+ioe);
   }
 }
}
 
//------------------------------------
//Parse Server Response
//------------------------------------
private int parse_server_response()  
{
 int reply_code = 0;
 
 try
 {
   //parse status line and extract the reply_code:
   String StatusLine = RTSPBufferedReader.readLine();
   //System.out.println("RTSP Client - Received from Server:");
   System.out.println(StatusLine);
 
   StringTokenizer tokens = new StringTokenizer(StatusLine);
   tokens.nextToken(); //skip over the RTSP version
   reply_code = Integer.parseInt(tokens.nextToken());
    
   //if reply code is OK get and print the 2 other lines
   if (reply_code == 200)
    {
	   String SeqNumLine = RTSPBufferedReader.readLine();
	   System.out.println(SeqNumLine);
       
	   if(type == 0)//default
	   {
		   String SessionLine = RTSPBufferedReader.readLine();
		   System.out.println(SessionLine);
		   tokens = new StringTokenizer(SessionLine);
		   tokens.nextToken(); //skip over the Session:
		   RTSPid = Integer.parseInt(tokens.nextToken());
	   }
      
	   if(type == 1)//option type
	   {
		   type = 0;
		   String Option = RTSPBufferedReader.readLine();
		   System.out.println(Option);
        
	   }
    
	   if(type == 2)//description type
	   {
		   type = 0;
		   for(int i = 0; i < 14; i ++)//14 seperate parameters
		   	{
			   String info = RTSPBufferedReader.readLine();
			   System.out.println(info);
		   	}
        }
    }
 }
 catch(Exception ex)
   {
    System.out.println("Exception caught in parse server: "+ex);
    System.exit(0);
   }
 
 return(reply_code);
}
 
//------------------------------------
//Send RTSP Request
//------------------------------------
 
//.............
//TO COMPLETE
//.............
 
private void send_RTSP_request(RTSPRequest request_type)
{
 try{
   //Use the RTSPBufferedWriter to write to the RTSP socket
 
   //write the request line:
     String requestLine = request_type.toString()+" " + this.VideoFileName + " RTSP/1.0"+CRLF;
     RTSPBufferedWriter.write(requestLine);
 
   //write the CSeq line:  
     String cseqLine = "CSeq: "+this.RTSPSeqNb + CRLF;
     RTSPBufferedWriter.write(cseqLine);
 
   //check if request_type is equal to "SETUP" and in this case write the Transport: line advertising to the server the port used to receive the RTP packets RTP_RCV_PORT
     if (request_type == RTSPRequest.SETUP) {
         String transportLine = "Transport: RTP/UDP; client_port= "+this.RTP_RCV_PORT + CRLF;
         RTSPBufferedWriter.write(transportLine);
     }
   //check if request_type is equal to "OPTION", and write required parameters of OPTIONS request (specified in RTSP RFC)
     else if(request_type == RTSPRequest.OPTION)
     {
         String require = "Require: implicit-play"+ CRLF;
         String pro = "Proxy-Require: gzipped-messages"+ CRLF;
         RTSPBufferedWriter.write(require);
         RTSPBufferedWriter.write(pro);
     }
   //check if request_type is equal to "DESCRIBE", and write required parameters of DESCRIBE request (specified in RTSP RFC)
     else if (request_type == RTSPRequest.DESCRIBE)
     {
         String Accept = "Accept: application/sdp, application/rtsl, application/mheg "+ CRLF;
         RTSPBufferedWriter.write(Accept);
     }
   //if the request_type is neither "OPTION" nor "DESCRIBE"
     else{
         String sessionLine = "Session:"+RTSPid+ CRLF;
         RTSPBufferedWriter.write(sessionLine);
     }
 
     RTSPBufferedWriter.flush();
     } catch (Exception ex) {
         System.out.println("Exception caught in send RTSP request: " + ex);
         System.exit(0);
     }
}
 
}//end of Class Client
 
 
 




