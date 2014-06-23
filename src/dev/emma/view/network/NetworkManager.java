package emma.view.network;

import java.awt.BorderLayout;
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
import emma.model.nodes.Network;
import emma.view.swing.petri.DesktopFrame;

public class NetworkManager extends DesktopFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(NetworkManager.class);
    protected JTextArea payload;
    protected JComboBox<String> method;
    protected JComboBox<String> ip;
    protected JTextField uri;
    protected JTextField port;
    protected NumberFormat format;

    protected JButton refresh_btn;
    protected JButton gen_btn;
    protected JButton launch_btn;
    protected JButton load_btn;
    protected JButton save_btn;
	
    private Network network;

	public NetworkManager(Network netwk){
		super("Agent Launcher", true, true, false, true);
		NetworkViewer netViewer = new NetworkViewer(netwk);
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
        payload = new JTextArea(15, 20);
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
                .addComponent(method, 60, 60,60)
                .addComponent(label_coap, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(ip, 70, 80, 100)
                .addComponent(label_coap_end, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(port, 40, 40, 40)
                .addComponent(uri, 60, 60, Short.MAX_VALUE)
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
 
        refresh_btn = new JButton(refresh);
        gen_btn = new JButton(gen);
        launch_btn = new JButton(launch);
        load_btn = new JButton(load);
        save_btn = new JButton(save);

        Box control = Box.createHorizontalBox();
        control.add(refresh_btn);
        control.add(gen_btn);
        control.add(load_btn);
        control.add(save_btn);
        control.add(launch_btn);
		
        this.getContentPane().add(jpanel_uri,BorderLayout.NORTH);
        this.getContentPane().add(netViewer,BorderLayout.WEST);
        this.getContentPane().add(new JScrollPane(payload),BorderLayout.CENTER);
        this.getContentPane().add(control,BorderLayout.SOUTH);
        this.network = netwk;
        this.pack();
        this.setVisible(true);
	}

	public void loadIP() {}
	
	public void loadAgent() {}
	
	public void sendAgent() {}
	
    private Action refresh = new AbstractAction("Refresh") {
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent arg0) {
			logger.debug("Refresh");
		}
    };
    
    private Action gen =  new AbstractAction("Generate"){
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                System.out.println(file.getName());
            }
		}
    };
    
    private Action launch = new AbstractAction("Launch") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			NetworkManager.this.setEnabled(false);
			NetworkManager.this.setVisible(false);
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
				if(rsp != null){
					JOptionPane.showMessageDialog(NetworkManager.this, rsp.getPayloadString());
				}
				else{
					JOptionPane.showMessageDialog(NetworkManager.this, "No response");	
				}
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			} 
			finally{
				NetworkManager.this.setVisible(true);
				NetworkManager.this.setEnabled(true);
			}
		}
    	
    };
    
    private Action save = new AbstractAction("Save") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
            String agent = payload.getText();
            JFileChooser fc = new JFileChooser();

            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                int dialogResult = JOptionPane.YES_OPTION;
                
                if (file.exists()){
                    dialogResult = JOptionPane.showConfirmDialog(null, "This agent already exists, do you want to erase it ?", "Warning", JOptionPane.YES_NO_OPTION);
                }
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
    
    private Action load = new AbstractAction("Load") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
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
