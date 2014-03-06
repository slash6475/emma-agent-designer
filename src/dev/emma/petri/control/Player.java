package emma.petri.control;

/**
 * Via une source de données DataStream, controle la vue, et affiche le réseau de Petri associé aux données. Plusieurs types de sources de données : données sniffées à chaud, données évaluées, logs (sniffés ou évalués)
 * @author  pierrotws
 */

public class Player{

	private DataStream inputStream;
	
	
	public DataStream getInputStream(){
		return inputStream;
	}
	public void setInputStream(DataStream stream){
		inputStream=stream;
	}
}
