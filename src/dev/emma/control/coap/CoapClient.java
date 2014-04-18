package emma.control.coap;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import org.apache.log4j.Logger;

import ch.ethz.inf.vs.californium.coap.BlockOption;
import ch.ethz.inf.vs.californium.coap.Message.messageType;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.registries.OptionNumberRegistry;

import emma.control.*;
import emma.model.nodes.Network;

public class CoapClient implements Client {

	private static Logger logger = Logger.getLogger(Network.class);
	private static final int BLOCK_SIZE = 64;
	private boolean stop = false;
	
	public void stop(){
		stop = false;
	}
	// coap://[fe80::200:2:2:202]:5683/plop/
	public Response send(int method, String uriString, String payload) throws URISyntaxException{
		Response response = null;
		
		URI uri = new URI(uriString);
		byte[] payloadBlock = payload.getBytes();
		byte[] block;
		BlockOption option = null;
		int blockCounter = 0;
		boolean Last = false;
		stop = false;

		
		/* Send each CoAP block */
		while (( method == CodeRegistry.METHOD_DELETE
				|| method == CodeRegistry.METHOD_GET 
				|| blockCounter * BLOCK_SIZE < payloadBlock.length) 
				&& !stop){
			
			Last = ((blockCounter+1)*BLOCK_SIZE > payloadBlock.length);
			option = new BlockOption(OptionNumberRegistry.BLOCK1, blockCounter, (int)((Math.log(BLOCK_SIZE)/Math.log(2))-4), !Last);
			block = Arrays.copyOfRange(payloadBlock, blockCounter*BLOCK_SIZE, Last ? payloadBlock.length:  ((blockCounter + 1)*BLOCK_SIZE));
			
			Request request = new Request(method);
			request.setType(messageType.CON);
			request.setURI(uri + "/");	
			
			if(!(method == CodeRegistry.METHOD_DELETE	|| method == CodeRegistry.METHOD_GET)){
				request.setPayload(block);
				request.setOption(option);
			}
			
			try {
				if(logger.isDebugEnabled())
					request.prettyPrint();

				request.enableResponseQueue(true);
				request.execute();
				response = request.receiveResponse();

				if(response == null){
					logger.error("RESPONSE null !!");
					return null;					
				}
				
				if(logger.isDebugEnabled())
					response.prettyPrint();					
					
				if(response.getCode() != CodeRegistry.RESP_CONTENT && response.getCode() != CodeRegistry.RESP_CHANGED){
					stop = true;
					
					logger.info("Response for block " + blockCounter + ": " + CodeRegistry.toString(response.getCode()));
					}
				else {
					if(method == CodeRegistry.METHOD_DELETE	|| method == CodeRegistry.METHOD_GET)
						stop = true;
					
					blockCounter++;
				}
				
			} catch (IOException | InterruptedException e) {
				System.err.println("Failed to execute request: " + e.getMessage());
				e.printStackTrace();
				stop = true;
			}
		}
		return response;
	}

}