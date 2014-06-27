package emma.view.img;

public class Resources {
	
	public static String getPath(String name){
		return Resources.getPath(name, 24);
	}
	
	public static String getPath(String name, int size){
		return "src/emma/view/img/"+size+"/"+name+".png";
	}
}
