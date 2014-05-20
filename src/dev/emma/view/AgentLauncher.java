package emma.view;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxUI;

import org.apache.log4j.Logger;

import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;

import emma.control.coap.CoapClient;

public class AgentLauncher extends JPanel{
	private static Logger logger = Logger.getLogger(AgentLauncher.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    protected JTextArea payload;
    protected JComboBox<String> method;
    protected JComboBox<String> ip;
    protected JTextField uri;
    protected JTextField port;
    protected NumberFormat format;
    
    protected JButton refresh_btn;
    protected JButton launch_btn;
    protected JButton load_btn;
    protected JButton save_btn;
    
    
	public static void main(String[] args) {
		Frame win = new Frame("Agent Launcher");
		win.add(new AgentLauncher());
		win.setVisible(true);
		win.setSize(400, 400);
	}
	
	public void loadIP() {
		
	}
	
	public void loadAgent() {
	
	}
	
	public void sendAgent() {
	
	}
	
	
	public AgentLauncher(){
		method = new JComboBox<String>();
		method.addItem("GET");
		method.addItem("PUT");
		method.addItem("POST");
		method.addItem("DELETE");
		method.setUI(new BasicComboBoxUI() {
            @SuppressWarnings("serial")
			protected JButton createArrowButton() {
                return new JButton() {
                    public int getWidth() {
                        return 0;
                    }
                };
            }
        });
		
        payload = new JTextArea(5, 20);
        payload.setEditable(true);
        
        ip = new JComboBox<String>();
        ip.addItem("127.0.0.1");
        ip.setEditable(true);
        ip.setUI(new BasicComboBoxUI() {
            @SuppressWarnings("serial")
			protected JButton createArrowButton() {
                return new JButton() {
                    public int getWidth() {
                        return 0;
                    }
                };
            }
        });
        port = new JTextField("5683");
        
        uri = new JTextField("/");
        JLabel label_coap = new JLabel("coap://");
        JLabel label_coap_end = new JLabel(":");

        JPanel jpanel_uri = new JPanel();
        GroupLayout layout = new GroupLayout(jpanel_uri);
        jpanel_uri.setLayout(layout);
        
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(method, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(label_coap, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(ip, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(label_coap_end, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(port, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(uri, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap()
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(method)
                    .addComponent(label_coap)
                    .addComponent(ip)
                    .addComponent(label_coap_end)
                    .addComponent(port)
                    .addComponent(uri)
                )
        );
 
        refresh_btn = new JButton(Refresh);
        launch_btn = new JButton(Launch);
        load_btn = new JButton(Load);
        save_btn = new JButton(Save);

        Box control = Box.createHorizontalBox();
        control.add(refresh_btn);
        control.add(load_btn);
        control.add(save_btn);
        control.add(launch_btn);

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        //this.getContentPane().add(BorderLayout.NORTH, uri_box);
        this.add(jpanel_uri);
        this.add(new JScrollPane(payload));
        this.add(control);
	}
	
    private Action Refresh = new AbstractAction("Refresh") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			logger.debug("Refresh");
		}
    };
    
    private Action Launch = new AbstractAction("Launch") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {

			
			AgentLauncher.this.setEnabled(false);
			AgentLauncher.this.setVisible(false);
			
			CoapClient client = new CoapClient();
			String uriString = "coap://" + ip.getSelectedItem() +": " + port.getText() + uri.getText();
			String payloadString = payload.getText().replace(" ","").replace("\n", "");
			
			int methodTosend = -1;
			if(method.getSelectedItem().equals("GET"))			methodTosend = CodeRegistry.METHOD_GET;
			else if(method.getSelectedItem().equals("PUT"))		methodTosend = CodeRegistry.METHOD_PUT;
			else if(method.getSelectedItem().equals("POST"))	methodTosend = CodeRegistry.METHOD_POST;
			else if(method.getSelectedItem().equals("DELETE"))	methodTosend = CodeRegistry.METHOD_DELETE;
			
			try {
				uriString = uriString.replace(" ","");
				Response rsp = client.send(methodTosend, uriString, payloadString);
				if(rsp != null)
					JOptionPane.showMessageDialog(AgentLauncher.this, rsp.getPayloadString());
				else JOptionPane.showMessageDialog(AgentLauncher.this, "No response");
				

				
				
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			finally{
				AgentLauncher.this.setVisible(true);
				AgentLauncher.this.setEnabled(true);
			}
		}
    	
    };
    
    private Action Save = new AbstractAction("Save") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
            String agent = payload.getText();

            JFileChooser fc = new JFileChooser();
          
            int returnVal = fc.showSaveDialog(null);


            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                int dialogResult = JOptionPane.YES_OPTION;
                
                if (file.exists()) 
                    dialogResult = JOptionPane.showConfirmDialog(null, "This agent already exists, do you want to erase it ?", "Warning", JOptionPane.YES_NO_OPTION);

                if (dialogResult == JOptionPane.YES_OPTION) {
                  	FileWriter fw;
                  	try {
                  		fw = new FileWriter(file.getAbsoluteFile());
                  		BufferedWriter bw = new BufferedWriter(fw);
                  		bw.write(agent);
                  		bw.close();
                  	}
                  	catch (IOException ex) {
                  		JOptionPane.showMessageDialog(null, "Saving agent error : " + ex.getMessage());
                  		}  
                  	}
                }
            }
		};
    
    private Action Load = new AbstractAction("Load") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                BufferedReader br;
                try {
                    br = new BufferedReader(new FileReader(file.getAbsolutePath()));

                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();

                    while (line != null) {
                        sb.append(line);
                        sb.append("\n");
                        line = br.readLine();
                    }

                    payload.setText(sb.toString());
                    br.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Opening agent error : " + ex.getMessage());
                }
            }
        }
    };
}
