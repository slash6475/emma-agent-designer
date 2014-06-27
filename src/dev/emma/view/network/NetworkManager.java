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
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import emma.control.coap.CoapClient;
import emma.mapper.Mapper;
import emma.mapper.MappingNotFoundException;
import emma.mapper.SimpleMapper;
import emma.model.nodes.Network;
import emma.petri.view.CorruptedFileException;
import emma.petri.view.XMLParser;
import emma.view.img.Resources;
import emma.view.swing.FileNameFilter;
import emma.view.swing.petri.DesktopFrame;

public class NetworkManager extends DesktopFrame{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NetworkManager.class);
	
	protected JTextArea payload;
    protected JComboBox<String> method;
    protected JComboBox<String> ip;
    protected JTextField uri;
    protected JTextField port;
    protected NumberFormat format;
	private JButton launch_btn;
	
    private Network network;
    private Mapper mapper;
    private XMLParser xmlParser;
    
    private JFileChooser fileChooser;
    private FileNameFilter filesFilter;
	private FileFilter agentFilter;
	private Gson gson;	
	private JsonParser jsonParser;
	
	public NetworkManager(Network netwk){
		super("Agent Launcher", true, true, false, true);
		NetworkViewer netViewer = new NetworkViewer(netwk);
		this.setJMenuBar(new NetworkMenuBar(this));
		this.method = new JComboBox<String>();
		this.method.addItem("GET");
		this.method.addItem("PUT");
		this.method.addItem("POST");
		this.method.addItem("DELETE");
		this.method.setUI(new BasicComboBoxUI() {
            @SuppressWarnings("serial")
			protected JButton createArrowButton() {
                return new JButton() {
                    public int getWidth() {
                        return 0;
                    }
                };
            }
        });
		this.payload = new JTextArea(15, 20);
		this.payload.setEditable(true);

		this.ip = new JComboBox<String>();
		this.ip.addItem("127.0.0.1");
		this.ip.setEditable(true);
		this.ip.setUI(new BasicComboBoxUI() {
            @SuppressWarnings("serial")
			protected JButton createArrowButton() {
                return new JButton() {
                    public int getWidth() {
                        return 0;
                    }
                };
            }
        });
		this.port = new JTextField("5683");
		this.uri = new JTextField("/");
        JLabel label_coap = new JLabel("coap://");
        JLabel label_coap_end = new JLabel(":");

        this.launch_btn = new JButton(new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				NetworkManager.this.launch();
			}
		});
        this.launch_btn.setIcon(new ImageIcon(Resources.getPath("send",16)));
        
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
                .addComponent(port, 45, 45, 45)
                .addComponent(uri, 60, 60, Short.MAX_VALUE)
                .addComponent(launch_btn,30,30,30)
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
                    .addComponent(launch_btn)
                )
        );
 
        this.fileChooser=new JFileChooser();
        for(FileFilter f : fileChooser.getChoosableFileFilters()){
			fileChooser.removeChoosableFileFilter(f);
		}
        this.filesFilter = new FileNameFilter("epnf|lst","Files (.epnf,.lst)");
        this.agentFilter = new FileNameFilter("json","Agent Files (.json)");
        this.getContentPane().add(jpanel_uri,BorderLayout.NORTH);
        this.getContentPane().add(netViewer,BorderLayout.WEST);
        this.getContentPane().add(new JScrollPane(payload),BorderLayout.CENTER);
        
        this.network = netwk;
        this.mapper=new SimpleMapper();
        try {
			this.xmlParser=new XMLParser();
		} catch (TransformerConfigurationException | ParserConfigurationException e) {
			logger.warn(e.getMessage());
		}
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		this.jsonParser = new JsonParser();
        this.pack();
        this.setVisible(true);
	}

	public void loadIP() {}
	
	public void loadAgent() {}
	
	public void sendAgent() {}
    
    public void refresh() {}
    
    public void generate(){
    	fileChooser.addChoosableFileFilter(this.filesFilter);
    	if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
            	String dep = gson.toJson(jsonParser.parse(mapper.getMapping(network, xmlParser.getNetFromXMLFile(file)).getDeploymentAgent()));
        		payload.setText(dep);
			} catch (MappingNotFoundException | CorruptedFileException | IOException | SAXException ex) {
				logger.warn("ERROR :\n"+ex.getMessage());
			}
        }
    	fileChooser.removeChoosableFileFilter(this.filesFilter);
    }
    
    public void load(){
    	fileChooser.addChoosableFileFilter(this.agentFilter);
    	if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
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
                payload.setText(gson.toJson(jsonParser.parse(sb.toString())));
                br.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Opening agent error : " + ex.getMessage());
                logger.warn("Opening agent error : " + ex.getMessage());
            }
        }
    	fileChooser.removeChoosableFileFilter(this.agentFilter);
    }
    
    public void save(){
    	String agent = payload.getText();
		fileChooser.addChoosableFileFilter(this.agentFilter);
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if(!file.getName().endsWith(".json")){
            	file = new File(file.getPath()+".json");
            }
            int dialogResult = JOptionPane.YES_OPTION;
            if (file.exists()){
                dialogResult = JOptionPane.showConfirmDialog(null, "This agent already exists, do you want to erase it ?", "Warning", JOptionPane.YES_NO_OPTION);
            }
            if (dialogResult == JOptionPane.YES_OPTION) {
            	FileWriter fw;
              	try {
              		fw = new FileWriter(file.getAbsoluteFile());
              		BufferedWriter bw = new BufferedWriter(fw);
              		bw.write(agent.replaceAll(" |\n|\t", ""));
              		bw.close();
              	}
              	catch (IOException ex) {
              		JOptionPane.showMessageDialog(null, "Saving agent error : " + ex.getMessage());
              		logger.warn("Saving agent error : " + ex.getMessage());
              	} 
            }
        }
		fileChooser.removeChoosableFileFilter(this.agentFilter);
    }
    
    public void launch(){
    	
    	NetworkManager.this.setEnabled(false);
		NetworkManager.this.setVisible(false);
		CoapClient client = new CoapClient();
		String uriString = "coap://" + ip.getSelectedItem() +": " + port.getText() + uri.getText();
		String payloadString = payload.getText().replaceAll(" |\n|\t","");
		
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
		} catch (URISyntaxException e) {
			logger.warn(e.getMessage());
		}
		finally{
			NetworkManager.this.setVisible(true);
			NetworkManager.this.setEnabled(true);
		}
    }
}
