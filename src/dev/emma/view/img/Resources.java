package emma.view.img;

public class Resources {
	
	public static String getPath(String name){
		return Resources.getPath(name, 24);
	}
	
	public static String getPath(String name, int size){
		return "img/"+size+"/"+name+".png";
	}
}
